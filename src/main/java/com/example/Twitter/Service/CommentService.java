package com.example.Twitter.Service;

import com.example.Twitter.DTO.CommentDTO;
import com.example.Twitter.Entity.Comment;
import com.example.Twitter.Entity.Post;
import com.example.Twitter.Entity.User;
import com.example.Twitter.Repository.CommentRepository;
import com.example.Twitter.Repository.PostRepository;
import com.example.Twitter.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

public boolean createComment(CommentDTO commentDTO){
        try {
            Optional<Post> postOpt = postRepository.findById(commentDTO.getPostId());
            if(postOpt.isEmpty()){
                throw new RuntimeException("Post does not exist");
            }
            Post post = postOpt.get();

            Optional<User> userOpt = userRepository.findById(commentDTO.getAuthorId());
            if(userOpt.isEmpty()){
                throw new RuntimeException("User does not exist");
            }
            User user = userOpt.get();

            List<Comment> comments = commentRepository.findAll();
            int depth = (int) comments.stream()
                    .filter(c -> c.getPostIdFk().equals(commentDTO.getPostId()))
                    .count();
            Comment comment = new Comment();
            comment.setAuthorId(commentDTO.getAuthorId());
            comment.setContent(commentDTO.getContent());
            comment.setPostIdFk(commentDTO.getPostId());
            comment.setPost(post);
            comment.setUser(user);
            comment.setDepth_level(depth + 1);
            commentRepository.save(comment);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
    }
}
}
