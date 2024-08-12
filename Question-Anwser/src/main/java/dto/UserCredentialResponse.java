package dto;

import com.example.questionanwser.Model.UserCredentials;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCredentialResponse {
    private String token;
    private UserCredentials user;

    public void setLoginCount(int loginCount) {
    }
}
