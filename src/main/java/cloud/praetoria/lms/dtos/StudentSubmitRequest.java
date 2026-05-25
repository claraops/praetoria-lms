package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubmitRequest {
    @Min(value = 0, message = "Le score doit être entre 0 et 100")
    @Max(value = 100, message = "Le score doit être entre 0 et 100")
    private Integer score;
}
