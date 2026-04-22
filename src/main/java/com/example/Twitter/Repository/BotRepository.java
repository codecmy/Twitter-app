package com.example.Twitter.Repository;

import com.example.Twitter.Entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepository extends JpaRepository<Bot, String> {
}
