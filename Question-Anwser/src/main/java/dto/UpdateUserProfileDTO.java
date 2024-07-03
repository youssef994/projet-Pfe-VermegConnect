package dto;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateUserProfileDTO {
    private String fullName;
    private String jobTitle;
    private String bio;
    private LocalDate birthDate;
    private String phoneNumber;
    private String profilePictureUrl;

    // set birthDate from String
    public void setBirthDate(String birthDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.birthDate = LocalDate.parse(birthDate, formatter);
    }
}
