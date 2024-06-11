package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Service.AuthenticationService;
import com.example.questionanwser.Service.JwtService;
import com.example.questionanwser.Service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;
        @Autowired
    private AuthenticationService authService;
        @Autowired
    private JwtService jwtService;



    @GetMapping
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable(value = "id") Long postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok().body(post);
    }


    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post, @RequestHeader("Authorization") String token) {
        try {
            // Retrieve username from the token
            String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token

            // Retrieve user details from the database based on the username
            Optional<UserCredentials> userOptional = authService.getUserByUsername(username);

            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Return unauthorized if user not found
            }

            // Associate the user with the post
            UserCredentials user = userOptional.get();
            post.setUser(user);

            // Create the post
            Post createdPost = postService.createPost(post);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return error response if something goes wrong
        }
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

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String query) {
        List<Post> posts = postService.searchPosts(query);
        return ResponseEntity.ok().body(posts);
    }
}
