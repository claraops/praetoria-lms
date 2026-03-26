package cloud.praetoria.lms.dtos;

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
public class ChangePasswordRequest {
    
    @NotBlank(message = "Ancien mot de passe est requis")
    private String oldPassword;
    
    @NotBlank(message = "Nouveau mot de passe est requis")
    @Size(min = 8, max = 100, message = "Mot de passe doit être entre 8 et 100 caractères")
    private String newPassword;
    
    @NotBlank(message = "Confirmation du mot de passe est requise")
    private String confirmPassword;
    
}
