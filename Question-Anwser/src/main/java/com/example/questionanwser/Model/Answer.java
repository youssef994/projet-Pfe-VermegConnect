package com.example.questionanwser.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
    private Date createdAt;

    @Column(name = "validated")
    private boolean validated;

    @Column(name = "upvotes")
    private int upvotes;

    @Column(name = "downvotes")
    private int downvotes;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private UserCredentials user;
}
