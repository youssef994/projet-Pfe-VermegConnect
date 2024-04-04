package dto;

import com.example.authentication.Model.UserCredentials;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCredentialResponse {
    private String token;
    private UserCredentials user;
}
