package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {

    @Email(message = "L'email doit être valide")
    private String email;

    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String firstName;

    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String lastName;
}