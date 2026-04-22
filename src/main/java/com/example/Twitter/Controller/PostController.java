package com.example.Twitter.Controller;

import com.example.Twitter.DTO.BotPostDTO;
import com.example.Twitter.DTO.UserPostDTO;
import com.example.Twitter.Entity.Post;
import com.example.Twitter.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class PostController{
    @Autowired
    private PostService postService;
    @GetMapping("/api/post/health-check")
    public String healthCheck(){
        return "post api healthy";
    }
    @PostMapping("/api/post/human/write")
    public ResponseEntity<?> post(@RequestBody UserPostDTO postDTO) {
        try {
            boolean b = postService.writePostUser(postDTO);
            if (b) {
                return ResponseEntity.ok().build();
            }else{
                throw new RuntimeException("Something went wrong, Please try again");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/api/post/bot/write")
    public ResponseEntity<?> botPost(@RequestBody BotPostDTO postDTO) {
        try {
            boolean b = postService.writePostBot(postDTO);
            if (b) {
                return ResponseEntity.ok().build();
            }else return ResponseEntity.badRequest().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/api/post/get/{postId}")
    public ResponseEntity<?> getPost(@PathVariable("postId") String postId){
        try{
            Optional<Post> postById = postService.getPostById(postId);
            if(postById.isPresent()){
                return ResponseEntity.ok().body(postById.get());
            }else return ResponseEntity.notFound().build();
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
