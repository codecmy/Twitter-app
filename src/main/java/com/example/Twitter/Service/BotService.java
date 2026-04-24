package com.example.Twitter.Service;

import com.example.Twitter.DTO.BotDetailsDTO;
import com.example.Twitter.DTO.BotPostDTO;
import com.example.Twitter.Entity.Bot;
import com.example.Twitter.Entity.Post;
import com.example.Twitter.Repository.BotRepository;
import com.example.Twitter.Repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@Slf4j
@Service
public class BotService{
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BotRepository botRepository;
    @Autowired
    private RedisService redisService;

    public boolean createBot(BotDetailsDTO botDetails){
        try {
            Bot bot = new Bot();
            bot.setName(botDetails.getName());
            bot.setPersonaDetails(botDetails.getDescription());
            botRepository.save(bot);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public boolean postBot(BotPostDTO botPostDTO){
        try {
            Post post = new Post();
            post.setAuthorId(botPostDTO.getAuthorId());
            post.setContent(botPostDTO.getContent());
            Bot bot = botRepository.findById(botPostDTO.getAuthorId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Bot does not exist"));
            post.setBot(bot);
            postRepository.save(post);
            return true;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

//Horizontal Cap
    public void botLikedPost(String postId, String botId){
        if (!postRepository.existsById(postId)) {
            throw new ResponseStatusException(NOT_FOUND, "Post does not exist");
        }
        if (!botRepository.existsById(botId)) {
            throw new ResponseStatusException(NOT_FOUND, "Bot does not exist");
        }
        long newCount = redisService.incrementBotCount(postId);
        if(newCount > 100){
            redisService.decrementBotCount(postId);
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Horizontal cap exceeded");
        }
    }
}
