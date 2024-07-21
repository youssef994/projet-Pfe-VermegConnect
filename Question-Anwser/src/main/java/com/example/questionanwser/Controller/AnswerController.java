package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Answer;
import com.example.questionanwser.Service.AnswerService;
import com.example.questionanwser.Service.JwtService;
import dto.AnswerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private JwtService jwtService;


    @GetMapping("/post/{postId}")
    public ResponseEntity<List<AnswerDTO>> getAnswersByPostId(@PathVariable Long postId) {
        List<Answer> answers = answerService.getAnswersByPostId(postId);
        List<AnswerDTO> answerDTOs = answers.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(answerDTOs);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable(value = "id") Long answerId) {
        Answer answer = answerService.getAnswerById(answerId);
        return ResponseEntity.ok().body(answer);
    }

    @PostMapping("/create")
    public ResponseEntity<AnswerDTO> createAnswer(@RequestBody AnswerDTO answerDTO, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        Answer answer = new Answer();
        answer.setContent(answerDTO.getContent());

        Answer createdAnswer = answerService.createAnswer(answer, answerDTO.getPostId(), username);

        AnswerDTO createdAnswerDTO = convertToDTO(createdAnswer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnswerDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AnswerDTO> updateAnswer(@PathVariable(value = "id") Long answerId, @RequestBody AnswerDTO answerDTO) {
        Answer answerDetails = new Answer();
        answerDetails.setContent(answerDTO.getContent());

        Answer updatedAnswer = answerService.updateAnswer(answerId, answerDetails);
        AnswerDTO updatedAnswerDTO = convertToDTO(updatedAnswer);
        return ResponseEntity.ok().body(updatedAnswerDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable(value = "id") Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/upvote")
    public ResponseEntity<AnswerDTO> upvoteAnswer(@PathVariable(value = "id") Long answerId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        Answer upvotedAnswer = answerService.upvoteAnswer(answerId, username);
        return ResponseEntity.ok(convertToDTO(upvotedAnswer));
    }

    @PostMapping("/{id}/downvote")
    public ResponseEntity<AnswerDTO> downvoteAnswer(@PathVariable(value = "id") Long answerId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        Answer downvotedAnswer = answerService.downvoteAnswer(answerId, username);
        return ResponseEntity.ok(convertToDTO(downvotedAnswer));
    }
    @PostMapping("/{id}/validate")
    public ResponseEntity<AnswerDTO> validateAnswer(@PathVariable(value = "id") Long answerId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        Answer validatedAnswer = answerService.validateAnswer(answerId, username);
        return ResponseEntity.ok(convertToDTO(validatedAnswer));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AnswerDTO>> searchAnswersByContent(@RequestParam String content) {
        List<Answer> answers = answerService.searchAnswersByContent(content);
        List<AnswerDTO> answerDTOs = answers.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(answerDTOs);
    }

    private AnswerDTO convertToDTO(Answer answer) {
        AnswerDTO answerDTO = new AnswerDTO();
        answerDTO.setAnswerId(answer.getAnswerId());
        answerDTO.setContent(answer.getContent());
        answerDTO.setCreatedAt(answer.getCreatedAt());
        answerDTO.setValidated(answer.isValidated());
        answerDTO.setUpvotes(answer.getUpvotes());
        answerDTO.setDownvotes(answer.getDownvotes());
        answerDTO.setPostId(answer.getPost().getPostId());
        answerDTO.setUsername(answer.getUser() != null ? answer.getUser().getUsername() : null);
        return answerDTO;
    }
}