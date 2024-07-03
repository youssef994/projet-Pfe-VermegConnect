package com.example.questionanwser.Model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;


    private int upvotes;

    private int downvotes;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "upvoters", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "username")
    private Set<String> upvoters = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "downvoters", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "username")
    private Set<String> downvoters = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "post-answers")
    private List<Answer> answers;


    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinTable(name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tags> tags = new HashSet<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonBackReference(value = "user-posts")
    private UserCredentials user;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
