package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Vote;
import com.example.questionanwser.Service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/votes")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getAllVotes() {
        List<Vote> votes = voteService.getAllVotes();
        return new ResponseEntity<>(votes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vote> getVoteById(@PathVariable Long id) {
        Vote vote = voteService.getVoteById(id);
        return new ResponseEntity<>(vote, HttpStatus.OK);
    }

    @PostMapping("create")
    public ResponseEntity<Vote> saveVote(@RequestBody Vote vote) {
        Vote savedVote = voteService.saveVote(vote);
        return new ResponseEntity<>(savedVote, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoteById(@PathVariable Long id) {
        voteService.deleteVoteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
