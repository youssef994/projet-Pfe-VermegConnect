package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable(value = "id") Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok().body(post);
    }

    @PostMapping("create")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postService.createPost(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable(value = "id") Long postId, @RequestBody Post postDetails) {
        Post updatedPost = postService.updatePost(postId, postDetails);
        return ResponseEntity.ok().body(updatedPost);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable(value = "id") Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }


}
