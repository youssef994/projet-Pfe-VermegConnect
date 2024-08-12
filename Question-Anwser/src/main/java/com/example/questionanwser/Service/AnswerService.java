package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Answer;
import com.example.questionanwser.Model.Notification;
import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.AnswerRepository;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.UserCredentialRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
public class AnswerService {
    private static final Logger logger = LoggerFactory.getLogger(AnswerService.class);

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserCredentialRepository userRepository;

    @Autowired
    private RestTemplate questionAnswerRestTemplate;

    @Autowired
    private JwtService jwtService;

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

        Answer savedAnswer = answerRepository.save(answer);

        // Notify user about the new answer
        notifyUserAboutNewAnswer(post.getUserId(), savedAnswer);

        return savedAnswer;
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

        // Notify user about the upvote
        notifyUserAboutUpvote(answer.getUserId(), answer);

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

        // Notify user about the downvote
        notifyUserAboutDownvote(answer.getUserId(), answer);

        return answerRepository.save(answer);
    }

    @Transactional
    public Answer validateAnswer(Long answerId, String username) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found with id: " + answerId));

        if (answer.isValidated()) {
            throw new IllegalArgumentException("This answer is already validated");
        }

        // Get the token from the request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
        } else {
            throw new SecurityException("Token is missing or malformed");
        }

        logger.info("Token: {}", token);

        // Extract roles from the token
        List<String> roles = jwtService.getRolesFromToken(token);
        logger.info("Roles: {}", roles);

        // Only the user who created the question or an ADMIN can validate an answer
        Post post = answer.getPost();
        UserCredentials postUser = post.getUser();
        boolean isAdmin = roles.contains("ROLE_ADMIN");

        if (!postUser.getUsername().equals(username) && !isAdmin) {
            throw new SecurityException("You are not authorized to validate this answer");
        }

        // Un-validate any previously validated answers if the user is not an admin
        if (!isAdmin) {
            List<Answer> answers = post.getAnswers();
            for (Answer a : answers) {
                if (a.isValidated()) {
                    a.setValidated(false);
                    answerRepository.save(a);
                }
            }
        }

        answer.setValidated(true);
        return answerRepository.save(answer);
    }

    public List<Answer> searchAnswersByContent(String content) {
        return answerRepository.findByContentContaining(content);
    }

    public int getTotalUpvotesByUsername(String username) {
        return answerRepository.countTotalUpvotesByUsername(username);
    }

    public int getTotalDownvotesByUsername(String username) {
        return answerRepository.countTotalDownvotesByUsername(username);
    }

    public int getUserAnswersCount(Long userId) {
        return answerRepository.countAnswersByUserId(userId);
    }

    public void notifyUserAboutNewAnswer(Integer userId, Answer answer) {
        Notification notification = new Notification();
        notification.setUserId(Long.valueOf(userId));
        notification.setType("NEW_ANSWER");
        notification.setContent("Your post received a new answer!");
        questionAnswerRestTemplate.postForObject("http://localhost:8085/notifications", notification, Notification.class);
    }

    public void notifyUserAboutUpvote(Integer userId, Answer answer) {
        Notification notification = new Notification();
        notification.setUserId(Long.valueOf(userId));
        notification.setType("UPVOTE");
        notification.setContent("Your answer was upvoted!");
        questionAnswerRestTemplate.postForObject("http://localhost:8085/notifications", notification, Notification.class);
    }

    public void notifyUserAboutDownvote(Integer userId, Answer answer) {
        Notification notification = new Notification();
        notification.setUserId(Long.valueOf(userId));
        notification.setType("DOWNVOTE");
        notification.setContent("Your answer was downvoted!");
        questionAnswerRestTemplate.postForObject("http://localhost:8085/notifications", notification, Notification.class);
    }
}
