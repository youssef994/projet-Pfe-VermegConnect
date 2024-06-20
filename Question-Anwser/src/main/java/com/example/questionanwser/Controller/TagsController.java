package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Post;
import com.example.questionanwser.Model.Tags;
import com.example.questionanwser.Service.TagsService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagsController {

    private final TagsService tagsService;

    @Autowired
    public TagsController(TagsService tagsService) {
        this.tagsService = tagsService;
    }

    @GetMapping
    public ResponseEntity<List<Tags>> getAllTags() {
        List<Tags> tags = tagsService.getAllTags();
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }

    @GetMapping("/tags/{id}")
    public ResponseEntity<Tags> getTagById(@PathVariable Long id) {
        Optional<Tags> optionalTag = tagsService.getTagById(id);
        Tags tag = optionalTag.orElseThrow(() -> new NotFoundException("Tag not found with id: " + id));
        return ResponseEntity.ok().body(tag);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") Long id) {
        tagsService.deleteTag(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/search")
    public List<Tags> searchTags(@RequestParam String query) {
        return tagsService.getTagsByName(query);
    }

    @PostMapping("/create")
    public Tags createTag(@RequestParam String name) {
        return tagsService.getOrCreateTag(name);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Tags>> getPopularTags(@RequestParam(defaultValue = "4") int limit) {
        List<Tags> popularTags = tagsService.getPopularTags(limit);
        return ResponseEntity.ok(popularTags);
    }


}
