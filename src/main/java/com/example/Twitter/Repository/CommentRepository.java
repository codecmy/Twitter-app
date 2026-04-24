package com.example.Twitter.Repository;

import com.example.Twitter.Entity.Comment;
import com.example.Twitter.Enums;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByPostIdFkAndWrittenBy(String postIdFk, Enums.WrittenByEnum writtenBy);
    long countByPostIdFk(String postIdFk);
}
