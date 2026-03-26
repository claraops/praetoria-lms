package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    
    @Size(min = 2, max = 100, message = "Prénom doit être entre 2 et 100 caractères")
    private String firstName;
    
    @Size(min = 2, max = 100, message = "Nom doit être entre 2 et 100 caractères")
    private String lastName;
    
}
