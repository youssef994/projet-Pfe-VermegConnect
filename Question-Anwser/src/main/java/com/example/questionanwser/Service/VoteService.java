package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Vote;
import com.example.questionanwser.Repository.VoteRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VoteService {

    private final VoteRepository voteRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public List<Vote> getAllVotes() {
        return voteRepository.findAll();
    }

    public Vote saveVote(Vote vote) {
        return voteRepository.save(vote);
    }

    public Vote getVoteById(Long id) {
        Optional<Vote> optionalVote = voteRepository.findById(id);
        return optionalVote.orElseThrow(() -> new NotFoundException("Vote not found with id: " + id));
    }

    public void deleteVoteById(Long id) {
        if (!voteRepository.existsById(id)) {
            throw new NotFoundException("Vote not found with id: " + id);
        }
        voteRepository.deleteById(id);
    }
}
