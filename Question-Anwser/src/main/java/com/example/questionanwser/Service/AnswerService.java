package com.example.questionanwser.Service;


import com.example.questionanwser.Model.Answer;
import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.AnswerRepository;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.UserCredentialRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserCredentialRepository userRepository;

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Answer getAnswerById(Long answerId) {
        return answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answerId));
    }

    @Transactional
    public Answer createAnswer(Answer answer, Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id " + postId));
        UserCredentials user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username " + username));

        answer.setPost(post);
        answer.setUser(user);
        return answerRepository.save(answer);
    }

    public Answer updateAnswer(Long answerId, Answer answerDetails) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answerId));

        answer.setContent(answerDetails.getContent());
        return answerRepository.save(answer);
    }

    public void deleteAnswer(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id " + answerId));

        answerRepository.delete(answer);
    }
    public List<Answer> getAnswersByPostId(Long postId) {
        return answerRepository.findByPost_PostId(postId);
    }

    @Transactional
    public Answer upvoteAnswer(Long answerId, String username) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        if (answer.getUpvoters().contains(username)) {
            throw new IllegalArgumentException("You have already upvoted this answer");
        }

        if (answer.getDownvoters().contains(username)) {
            answer.getDownvoters().remove(username);
            answer.setDownvotes(answer.getDownvotes() - 1);
        }

        answer.getUpvoters().add(username);
        answer.setUpvotes(answer.getUpvotes() + 1);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer downvoteAnswer(Long answerId, String username) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        if (answer.getDownvoters().contains(username)) {
            throw new IllegalArgumentException("You have already downvoted this answer");
        }

        if (answer.getUpvoters().contains(username)) {
            answer.getUpvoters().remove(username);
            answer.setUpvotes(answer.getUpvotes() - 1);
        }

        answer.getDownvoters().add(username);
        answer.setDownvotes(answer.getDownvotes() + 1);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer validateAnswer(Long answerId, String username) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        if (answer.isValidated()) {
            throw new IllegalArgumentException("This answer is already validated");
        }

        // only the user who created the question can validate an answer
        Post post = answer.getPost();
        UserCredentials postUser = post.getUser();
        if (!postUser.getUsername().equals(username)) {
            throw new SecurityException("You are not authorized to validate this answer");
        }

        // Un-validate any previously validated answers
        List<Answer> answers = post.getAnswers();
        for (Answer a : answers) {
            if (a.isValidated()) {
                a.setValidated(false);
                answerRepository.save(a);
            }
        }

        answer.setValidated(true);
        return answerRepository.save(answer);
    }

}
