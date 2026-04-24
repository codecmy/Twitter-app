package com.example.Twitter.Controller;

import com.example.Twitter.DTO.CommentDTO;
import com.example.Twitter.DTO.UserDetailsSignInDTO;
import com.example.Twitter.Entity.User;
import com.example.Twitter.Service.CommentService;
import com.example.Twitter.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;
    @GetMapping("/health-check")
    public String health_test(){
        return "healthy";
    }
    @PostMapping("/signin")
    private ResponseEntity<?> login(@RequestBody UserDetailsSignInDTO userDetailsSignInDTO){
        if (userService.createUser(userDetailsSignInDTO)) {
            return ResponseEntity.ok().build();
        }else
            return ResponseEntity.badRequest().build();
    }
    @GetMapping("/user-details/{userId}")
    private ResponseEntity<?> getUserDetails(@PathVariable String userId){
        User byUserId = userService.findByUserId(userId);
        if (byUserId == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(byUserId);
    }
    @PostMapping("/user/comment")
    public ResponseEntity<?> comment(@RequestBody CommentDTO commentDTO){
        try {
            boolean comment = commentService.createComment(commentDTO);
            if (comment) {
                return ResponseEntity.ok().build();
            }else {
                return ResponseEntity.badRequest().build();
            }
        }catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
