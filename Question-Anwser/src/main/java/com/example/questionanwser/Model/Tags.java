package com.example.questionanwser.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    private String name;
    private String description;


    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();
    public Tags() {

    }

    public Tags(String name) {
        this.name = name;
    }
}