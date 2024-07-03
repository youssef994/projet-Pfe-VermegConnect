package dto;

import com.example.questionanwser.Model.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AnswerDTO {
    private Long answerId;
    private String content;
    private Date createdAt;
    private boolean validated;
    private int upvotes;
    private int downvotes;
    private Long postId;
    private String username; // Username of the user who created the answer
}
