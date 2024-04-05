package com.example.questionanwser.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;


    private String voteType;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "postId", insertable = false, updatable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;
}
