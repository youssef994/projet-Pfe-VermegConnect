package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Service.AuthenticationService;
import com.example.questionanwser.Service.JwtService;
import com.example.questionanwser.Service.PostService;
import dto.PostDTO;
import dto.PostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<Page<PostDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Post> pagedResult = postService.getAllPosts(paging);

        // Convert Page<Post> to Page<PostDTO>
        List<PostDTO> postDTOs = pagedResult.getContent().stream().map(post -> {
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(post.getPostId());
            postDTO.setTitle(post.getTitle());
            postDTO.setContent(post.getContent());
            postDTO.setCreatedAt(post.getCreatedAt());
            postDTO.setUpvotes(post.getUpvotes());
            postDTO.setDownvotes(post.getDownvotes());
            postDTO.setTags(post.getTags());
            postDTO.setUsername(post.getUser() != null ? post.getUser().getUsername() : null);
            postDTO.setTags(post.getTags());
            return postDTO;
        }).collect(Collectors.toList());

        Page<PostDTO> postDTOPage = new PageImpl<>(postDTOs, paging, pagedResult.getTotalElements());
        return new ResponseEntity<>(postDTOPage, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable(value = "id") Long postId) {
        Post post = postService.getPostById(postId);

        // Map Post entity to PostDTO
        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setUpvotes(post.getUpvotes());
        postDTO.setDownvotes(post.getDownvotes());
        postDTO.setTags(post.getTags());
        postDTO.setUsername(post.getUser() != null ? post.getUser().getUsername() : null); // Get the username

        return ResponseEntity.ok().body(postDTO);
    }



    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostRequest postRequest, @RequestHeader("Authorization") String token) {
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
            Post post = new Post();
            post.setTitle(postRequest.getTitle());
            post.setContent(postRequest.getContent());
            post.setUser(user);

            // Create the post with associated tags
            Post createdPost = postService.createPost(post, postRequest.getTags());

            // Map Post entity to PostDTO
            PostDTO postDTO = new PostDTO();
            postDTO.setPostId(createdPost.getPostId());
            postDTO.setTitle(createdPost.getTitle());
            postDTO.setContent(createdPost.getContent());
            postDTO.setCreatedAt(createdPost.getCreatedAt());
            postDTO.setUpvotes(createdPost.getUpvotes());
            postDTO.setDownvotes(createdPost.getDownvotes());
            postDTO.setTags(createdPost.getTags());
            postDTO.setUsername(username);

            return ResponseEntity.status(HttpStatus.CREATED).body(postDTO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return error response if something goes wrong
        }
    }



    @PutMapping("/update/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable(value = "id") Long postId, @RequestBody PostRequest postRequest) {
        Post updatedPost = postService.updatePost(postId, postRequest);
        PostDTO postDTO = convertToDTO(updatedPost);
        return ResponseEntity.ok().body(postDTO);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable(value = "id") Long postId, @RequestHeader("Authorization") String token) {
        try {
            // Retrieve username from the token
            String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token

            // Retrieve the post
            Post post = postService.getPostById(postId);

            // Check if the current user is the owner of the post
            if (!post.getUser().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // Return forbidden if user is not the owner
            }

            // Delete the post
            postService.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return error response if something goes wrong
        }
    }


    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String query) {
        List<Post> posts = postService.searchPosts(query);
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Post>> getRecentPosts() {
        List<Post> recentPosts = postService.findRecentPosts();
        return ResponseEntity.ok(recentPosts);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<Post>> getPostsByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable paging = PageRequest.of(page, size);
        Page<Post> pagedResult = postService.getPostsByTag(tag, paging);
        return new ResponseEntity<>(pagedResult, HttpStatus.OK);
    }
    @PostMapping("/{postId}/upvote")
    public ResponseEntity<PostDTO> upvotePost(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        Post updatedPost = postService.upvotePost(postId, username);
        PostDTO postDTO = convertToDTO(updatedPost);
        return ResponseEntity.ok(postDTO);
    }

    @PostMapping("/{postId}/downvote")
    public ResponseEntity<PostDTO> downvotePost(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        Post updatedPost = postService.downvotePost(postId, username);
        PostDTO postDTO = convertToDTO(updatedPost);
        return ResponseEntity.ok(postDTO);
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostDTO>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> pagedResult = postService.getPostsByUserId(userId, pageable);

        // Convert Page<Post> to Page<PostDTO>
        List<PostDTO> postDTOs = pagedResult.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        Page<PostDTO> postDTOPage = new PageImpl<>(postDTOs, pageable, pagedResult.getTotalElements());

        return new ResponseEntity<>(postDTOPage, HttpStatus.OK);
    }

    private PostDTO convertToDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setPostId(post.getPostId());
        postDTO.setTitle(post.getTitle());
        postDTO.setContent(post.getContent());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setUpvotes(post.getUpvoters().size()); // Use .size() to get count of upvoters
        postDTO.setDownvotes(post.getDownvoters().size()); // Use .size() to get count of downvoters
        postDTO.setTags(post.getTags());
        postDTO.setUsername(post.getUser() != null ? post.getUser().getUsername() : null);
        return postDTO;
    }
    @PostMapping("/{postId}/follow")
    public ResponseEntity<Void> followPost(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7));
        postService.followPost(postId, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unfollow")
    public ResponseEntity<Void> unfollowPost(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7));
        postService.unfollowPost(postId, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/isFollowing")
    public ResponseEntity<Boolean> isFollowingPost(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        boolean isFollowing = postService.isFollowingPost(postId, username);
        return ResponseEntity.ok(isFollowing);
    }

    @GetMapping("/followed-posts")
    public ResponseEntity<Page<PostDTO>> getFollowedPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token) {

        String username = jwtService.getUsernameFromToken(token.substring(7)); // Remove "Bearer " from the token
        Pageable paging = PageRequest.of(page, size);
        Page<Post> pagedResult = postService.getFollowedPosts(username, paging);

        List<PostDTO> postDTOs = pagedResult.getContent().stream().map(this::convertToDTO).collect(Collectors.toList());
        Page<PostDTO> postDTOPage = new PageImpl<>(postDTOs, paging, pagedResult.getTotalElements());

        return new ResponseEntity<>(postDTOPage, HttpStatus.OK);
    }

}
