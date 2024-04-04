package com.example.questionanwser.Repository;

import com.example.questionanwser.Model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote,Long> {


}
