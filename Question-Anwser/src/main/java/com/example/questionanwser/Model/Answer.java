package com.example.questionanwser.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "answers")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;


    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date createdAt;

    @Column(name = "validated")
    private boolean validated;

    @Column(name = "upvotes")
    private int upvotes;

    @Column(name = "downvotes")
    private int downvotes;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "answer_upvoters", joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "username")
    private Set<String> upvoters = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "answer_downvoters", joinColumns = @JoinColumn(name = "answer_id"))
    @Column(name = "username")
    private Set<String> downvoters = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference(value = "post-answers")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonBackReference(value = "user-answers")
    private UserCredentials user;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }
}
