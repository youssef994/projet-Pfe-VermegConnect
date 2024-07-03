package com.example.questionanwser.Repository;

import com.example.questionanwser.Model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("SELECT a FROM Answer a JOIN FETCH a.user WHERE a.post.postId = :postId")
    List<Answer> findByPost_PostId(@Param("postId") Long postId);

}
