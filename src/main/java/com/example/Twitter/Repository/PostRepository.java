package com.example.Twitter.Repository;

import com.example.Twitter.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, String>{
    @Query("SELECT p.authorId FROM Post p WHERE p.id = :id")
    String getAuthorIdById(String id);
}
