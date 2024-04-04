package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Answer;
import com.example.questionanwser.Service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @GetMapping
    public List<Answer> getAllAnswers() {
        return answerService.getAllAnswers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable(value = "id") Long answerId) {
        Answer answer = answerService.getAnswerById(answerId);
        return ResponseEntity.ok().body(answer);
    }

    @PostMapping("create")
    public ResponseEntity<Answer> createAnswer(@RequestBody Answer answer) {
        Answer createdAnswer = answerService.createAnswer(answer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAnswer);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Answer> updateAnswer(@PathVariable(value = "id") Long answerId, @RequestBody Answer answerDetails) {
        Answer updatedAnswer = answerService.updateAnswer(answerId, answerDetails);
        return ResponseEntity.ok().body(updatedAnswer);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable(value = "id") Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
}
