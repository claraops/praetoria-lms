package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    
    @NotBlank(message = "Clé d'inscription requise")
    private String registrationKey;
    
    @NotBlank(message = "Email est requis")
    @Email(message = "Email doit être valide")
    private String email;
    
    @NotBlank(message = "Prénom est requis")
    @Size(min = 2, max = 100, message = "Prénom doit être entre 2 et 100 caractères")
    private String firstName;
    
    @NotBlank(message = "Nom est requis")
    @Size(min = 2, max = 100, message = "Nom doit être entre 2 et 100 caractères")
    private String lastName;
    
    @NotBlank(message = "Mot de passe est requis")
    @Size(min = 8, max = 100, message = "Mot de passe doit être entre 8 et 100 caractères")
    private String password;
    
    @NotBlank(message = "Confirmation du mot de passe est requise")
    private String confirmPassword;
    
}
