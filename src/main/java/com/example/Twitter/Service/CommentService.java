package com.example.Twitter.Service;

import com.example.Twitter.DTO.CommentDTO;
import com.example.Twitter.Entity.Bot;
import com.example.Twitter.Entity.Comment;
import com.example.Twitter.Entity.Post;
import com.example.Twitter.Entity.User;
import com.example.Twitter.Enums;
import com.example.Twitter.Repository.BotRepository;
import com.example.Twitter.Repository.CommentRepository;
import com.example.Twitter.Repository.PostRepository;
import com.example.Twitter.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Slf4j
@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private RedisService redisService;

    public boolean createComment(CommentDTO commentDTO){
        String postId = commentDTO.getPostId();
        String authorId = commentDTO.getAuthorId();
        Enums.WrittenByEnum writtenBy = commentDTO.getWrittenBy();

        if (writtenBy == null) {
            throw new ResponseStatusException(BAD_REQUEST, "writtenBy is required");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post does not exist"));

        Optional<Comment> parentComment = Optional.empty();
        int depthLevel = 1;
        if (commentDTO.getParentCommentId() != null && !commentDTO.getParentCommentId().isBlank()) {
            parentComment = commentRepository.findById(commentDTO.getParentCommentId());
            if (parentComment.isEmpty()) {
                throw new ResponseStatusException(NOT_FOUND, "Parent comment does not exist");
            }
            if (!postId.equals(parentComment.get().getPostIdFk())) {
                throw new ResponseStatusException(BAD_REQUEST, "Parent comment does not belong to post");
            }
            depthLevel = parentComment.get().getDepth_level() + 1;
        }
        if (depthLevel > 20) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Vertical cap exceeded");
        }

        long viralityDelta;
        boolean botCountIncremented = false;
        boolean cooldownSet = false;
        String cooldownHumanId = null;

        User authorUser = null;

        if(writtenBy == Enums.WrittenByEnum.BOT){
            Bot bot = botRepository.findById(authorId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Bot does not exist"));

            cooldownHumanId = resolveCooldownHumanId(post, parentComment);
            if (cooldownHumanId != null) {
                cooldownSet = redisService.acquireCooldown(bot.getId(), cooldownHumanId, Duration.ofMinutes(10));
                if (!cooldownSet) {
                    throw new ResponseStatusException(TOO_MANY_REQUESTS, "Cooldown cap exceeded");
                }
            }

            long botCount = redisService.incrementBotCount(postId);
            botCountIncremented = true;
            if(botCount > 100){
                redisService.decrementBotCount(postId);
                botCountIncremented = false;
                if (cooldownSet && cooldownHumanId != null) {
                    redisService.releaseCooldown(authorId, cooldownHumanId);
                }
                throw new ResponseStatusException(TOO_MANY_REQUESTS, "Horizontal cap exceeded");
            }
            viralityDelta = 1;
        } else {
            authorUser = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User does not exist"));
            viralityDelta = 50;
        }

        try {
            Comment comment = new Comment();
            comment.setAuthorId(authorId);
            comment.setContent(commentDTO.getContent());
            comment.setPostIdFk(postId);
            comment.setPost(post);
            if (authorUser != null) {
                comment.setUser(authorUser);
            }
            comment.setWrittenBy(writtenBy);
            comment.setDepth_level(depthLevel);
            commentRepository.save(comment);

            redisService.incrementViralityScore(postId, viralityDelta);
            return true;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            if (botCountIncremented) {
                redisService.decrementBotCount(postId);
            }
            if (cooldownSet && cooldownHumanId != null) {
                redisService.releaseCooldown(authorId, cooldownHumanId);
            }
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    private String resolveCooldownHumanId(Post post, Optional<Comment> parentComment) {
        if (parentComment.isPresent() && parentComment.get().getWrittenBy() == Enums.WrittenByEnum.HUMAN) {
            return parentComment.get().getAuthorId();
        }
        if (post.getUser() != null) {
            return post.getUser().getId();
        }
        return null;
    }
}
