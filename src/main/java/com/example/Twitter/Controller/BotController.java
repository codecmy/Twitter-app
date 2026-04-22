package com.example.Twitter.Controller;

import com.example.Twitter.DTO.BotDetailsDTO;
import com.example.Twitter.DTO.BotPostDTO;
import com.example.Twitter.DTO.CommentDTO;
import com.example.Twitter.Service.BotService;
import com.example.Twitter.Service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BotController{
    @Autowired
    private BotService botService;
    @Autowired
    private CommentService commentService;
    @PostMapping("/bot/create")
    public ResponseEntity<?> createBot(@RequestBody BotDetailsDTO botDetails) {
        try {
            boolean bot = botService.createBot(botDetails);
            if(bot){
                return ResponseEntity.ok().build();
            }else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/bot/post")
    public ResponseEntity<?> botPost(@RequestBody BotPostDTO botPostDTO){
        try {
            boolean b = botService.postBot(botPostDTO);
            if (b) {
                return ResponseEntity.ok().build();
            }else  {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/bot/comment")
    public ResponseEntity<?> comment(@RequestBody CommentDTO commentDTO){
        try {
            boolean comment = commentService.createComment(commentDTO);
            if (comment) {
                return ResponseEntity.ok().build();
            }else {
                return ResponseEntity.badRequest().build();
            }
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}