package com.example.questionanwser.Service;

import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Long postId, Post postDetails) {
        return postRepository.findById(postId)
                .map(post -> {
                    post.setTitle(postDetails.getTitle());
                    post.setContent(postDetails.getContent());
                    return postRepository.save(post);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    public void deletePost(Long postId) {
        postRepository.findById(postId)
                .map(post -> {
                    postRepository.delete(post);
                    return post;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }



}
