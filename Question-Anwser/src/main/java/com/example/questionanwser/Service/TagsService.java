package com.example.questionanwser.Service;


import com.example.questionanwser.Model.Tags;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.TagsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagsService {

    @Autowired
    TagsRepository tagsRepository;

    public List<Tags> getAllTags() {
        return tagsRepository.findAll();
    }

    public Optional<Tags> getTagById(Long id) {
        return tagsRepository.findById(id);
    }

    public Tags createTag(Tags tag) {
        return tagsRepository.save(tag);
    }

    public void deleteTag(Long id) {
        tagsRepository.deleteById(id);
    }
    public List<Tags> getTagsByName(String name) {
        return tagsRepository.findByNameContainingIgnoreCase(name);
    }

    public Tags getOrCreateTag(String name) {
        Tags tag = tagsRepository.findByNameIgnoreCase(name);
        if (tag == null) {
            tag = new Tags();
            tag.setName(name);
            tag = tagsRepository.save(tag);
        }
        return tag;
    }
    public List<Tags> getPopularTags(int limit) {
        return tagsRepository.findPopularTags(PageRequest.of(0, limit)).getContent();
    }



}
