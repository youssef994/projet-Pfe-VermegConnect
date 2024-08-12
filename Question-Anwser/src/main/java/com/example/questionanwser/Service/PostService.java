package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Notification;
import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.Tags;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.TagsRepository;
import com.example.questionanwser.Repository.UserCredentialRepository;
import dto.PostRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagsRepository tagsRepository;

    @Autowired
    private RestTemplate questionAnswerRestTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    @Transactional
    public Post createPost(Post post, Set<String> tagNames) {
        Set<Tags> tags = tagNames.stream()
                .map(name -> tagsRepository.findByName(name)
                        .orElseGet(() -> tagsRepository.save(new Tags(name))))
                .collect(Collectors.toSet());
        post.setTags(tags);
        Post savedPost = postRepository.save(post);
        entityManager.createNativeQuery("UPDATE posts SET search_vector = to_tsvector('english', title || ' ' || content)").executeUpdate();
        return savedPost;
    }

    public Post updatePost(Long postId, PostRequest postRequest) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());

            // Update tags
            Set<Tags> tags = postRequest.getTags().stream()
                    .map(name -> tagsRepository.findByName(name)
                            .orElseGet(() -> tagsRepository.save(new Tags(name))))
                    .collect(Collectors.toSet());
            post.setTags(tags);

            // Update other fields as necessary
            return postRepository.save(post);
        } else {
            throw new ResourceNotFoundException("Post not found with id " + postId);
        }
    }

    public void deletePost(Long postId) {
        postRepository.findById(postId)
                .map(post -> {
                    postRepository.delete(post);
                    return post;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }
    public List<Post> searchPosts(String query) {
        return postRepository.findBySearch(query);
    }


    public List<Post> findRecentPosts() {
        Pageable pageable = PageRequest.of(0, 4, Sort.by("createdAt").descending());
        Page<Post> recentPostsPage = postRepository.findAll(pageable);
        return recentPostsPage.getContent();
    }

    public Page<Post> getPostsByTag(String tag, Pageable pageable) {
        return postRepository.findByTagsName(tag, pageable);
    }

    @Transactional
    public Post upvotePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Check if the user has already upvoted
        if (post.getUpvoters().contains(username)) {
            throw new IllegalArgumentException("You have already upvoted this post");
        }

        // Check if the user has previously downvoted and adjust counts accordingly
        if (post.getDownvoters().contains(username)) {
            post.getDownvoters().remove(username);
            post.setDownvotes(post.getDownvotes() - 1);
        }

        // Add the user to the upvoters set
        post.getUpvoters().add(username);
        post.setUpvotes(post.getUpvotes() + 1);

        // Notify the user about the upvote
        notifyUserAboutUpvote(post.getUserId(), post);

        return postRepository.save(post);
    }


    @Transactional
    public Post downvotePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Check if the user has already downvoted
        if (post.getDownvoters().contains(username)) {
            throw new IllegalArgumentException("You have already downvoted this post");
        }

        // Check if the user has previously upvoted and adjust counts accordingly
        if (post.getUpvoters().contains(username)) {
            post.getUpvoters().remove(username);
            post.setUpvotes(post.getUpvotes() - 1);
        }

        // Add the user to the downvoters set
        post.getDownvoters().add(username);
        post.setDownvotes(post.getDownvotes() + 1);
        // Notify the user about the downvote
        notifyUserAboutDownvote(post.getUserId(), post);
        return postRepository.save(post);
    }

    public Page<Post> getPostsByUserId(Long userId,Pageable pageable) {
        return postRepository.findByUser_Id(userId,pageable); // or use custom query if needed
    }


    @Transactional
    public void followPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        post.getFollowers().add(username);
        postRepository.save(post);
        // Notify the user about the new follower
        notifyUserAboutFollow(post.getUserId(), post);
    }

    @Transactional
    public void unfollowPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        post.getFollowers().remove(username);
        postRepository.save(post);
    }
    @Transactional
    public boolean isFollowingPost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return post.getFollowers().contains(username);
    }

    public Page<Post> getFollowedPosts(String username, Pageable pageable) {
        return postRepository.findFollowedPostsByUsername(username, pageable);
    }
    public Long countPostsByUserId(int userId) {
        return postRepository.countByUser_Id(userId);
    }


    public int getTotalUpvotesByUsername(String username) {
        return postRepository.countTotalUpvotesByUsername(username);
    }

    public int getTotalDownvotesByUsername(String username) {
        return postRepository.countTotalDownvotesByUsername(username);
    }


    public void notifyUserAboutUpvote(Integer userId, Post post) {
        Notification notification = new Notification();
        notification.setUserId(Long.valueOf(userId));
        notification.setType("UPVOTE");
        notification.setContent("Your post was upvoted!");
        questionAnswerRestTemplate.postForObject("http://localhost:8085/notifications", notification, Notification.class);
    }

    public void notifyUserAboutDownvote(Integer userId, Post post) {
        Notification notification = new Notification();
        notification.setUserId(Long.valueOf(userId));
        notification.setType("DOWNVOTE");
        notification.setContent("Your post was downvoted!");
        questionAnswerRestTemplate.postForObject("http://localhost:8085/notifications", notification, Notification.class);
    }
    public void notifyUserAboutFollow(Integer userId, Post post) {
        Notification notification = new Notification();
        notification.setUserId(Long.valueOf(userId));
        notification.setType("FOLLOW");
        notification.setContent("Your post was followed!");
        questionAnswerRestTemplate.postForObject("http://localhost:8085/notifications", notification, Notification.class);
    }

}
