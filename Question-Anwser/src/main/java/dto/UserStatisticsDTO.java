package dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserStatisticsDTO {
    private int totalPosts;
    private int totalLikes;
    private int totalDislikes;
    private int totalAnswers;
    private int totalFollowedPosts;


}
