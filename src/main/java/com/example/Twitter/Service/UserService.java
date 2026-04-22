package com.example.Twitter.Service;

import com.example.Twitter.DTO.UserDetailsSignInDTO;
import com.example.Twitter.Entity.User;
import com.example.Twitter.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService{
    @Autowired
    private UserRepository userRepository;

    public boolean createUser(UserDetailsSignInDTO userDetails){
        try {
            User user=new User();
            user.setUserName(userDetails.getUserName());
            user.setPremium(userDetails.isPremium());
            userRepository.save(user);
            log.info("User created {}",user.getUserName());
            return true;
        }catch (Exception e){
            log.error("UserDetailsSignInDTO Exception{}", e.getMessage());
            return false;
        }
    }
    public User findByUserId(String userId){
        return userRepository.findById(userId).orElse(null);
    }
}
