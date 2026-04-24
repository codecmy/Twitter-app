package com.example.Twitter.Service;

import com.example.Twitter.DTO.BotPostDTO;
import com.example.Twitter.DTO.UserPostDTO;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PostService{
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    public boolean writePostBot(BotPostDTO postDTO){
        try{
           Post post = new Post();
           post.setAuthorId(postDTO.getAuthorId());
           post.setContent(postDTO.getContent());
           Optional<Bot> byId = botRepository.findById(postDTO.getAuthorId());
           if(byId.isEmpty()){
                throw new RuntimeException("User does not exist");
           }
           post.setBot(byId.get());
           postRepository.save(post);
           return true;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    public boolean writePostUser(UserPostDTO userPostDTO){
        try{
            Post post = new Post();
            post.setAuthorId(userPostDTO.getAuthorId());
            post.setContent(userPostDTO.getContent());
            Optional<User> byId = userRepository.findById(userPostDTO.getAuthorId());
            if(byId.isEmpty()){
                 throw new RuntimeException("User does not exist");
            }
            post.setUser(byId.get());
            postRepository.save(post);
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    public Optional<Post> getPostById(String postId){
        return postRepository.findById(postId);
    }
}
