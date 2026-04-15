package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;

    public static UserSummaryResponse fromEntity(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .build();
    }
}