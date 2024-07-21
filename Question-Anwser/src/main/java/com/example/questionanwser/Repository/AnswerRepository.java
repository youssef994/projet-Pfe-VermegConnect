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

    @Query("SELECT COUNT(u) FROM Answer a JOIN a.upvoters u WHERE u = :username")
    Long sumUpvotesForAnswersByUser(@Param("username") String username);

    @Query("SELECT COUNT(u) FROM Answer a JOIN a.downvoters u WHERE u = :username")
    Long sumDownvotesForAnswersByUser(@Param("username") String username);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.user.id = :userId")
    Long countAnswersByUserId(@Param("userId") int userId);
    @Query("SELECT a FROM Answer a WHERE LOWER(a.content) LIKE LOWER(CONCAT('%', :content, '%'))")
    List<Answer> findByContentContaining(@Param("content") String content);

}
