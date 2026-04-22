package com.example.Twitter.Service;

import com.example.Twitter.DTO.BotDetailsDTO;
import com.example.Twitter.DTO.BotPostDTO;
import com.example.Twitter.Entity.Bot;
import com.example.Twitter.Entity.Post;
import com.example.Twitter.Repository.BotRepository;
import com.example.Twitter.Repository.PostRepository;
import org.hibernate.boot.internal.Abstract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BotService{
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private BotRepository botRepository;

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
            postRepository.save(post);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
