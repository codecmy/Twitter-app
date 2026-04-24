package com.example.Twitter.Controller;

import com.example.Twitter.DTO.BotDetailsDTO;
import com.example.Twitter.DTO.BotPostDTO;
import com.example.Twitter.DTO.CommentDTO;
import com.example.Twitter.Service.BotService;
import com.example.Twitter.Service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        }catch (ResponseStatusException e){
            log.error(e.getReason());
            return ResponseEntity.status(e.getStatusCode()).build();
        }catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("bot/{botId}/likes/{postId}")
    public ResponseEntity<?> like(@PathVariable("botId") String botId,@PathVariable("postId") String postId){
        try {
            botService.botLikedPost(postId,botId);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return ResponseEntity.ok().build();
    }
}
