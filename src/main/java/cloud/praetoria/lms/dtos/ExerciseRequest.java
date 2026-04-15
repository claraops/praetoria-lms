package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseRequest {

    @NotBlank(message = "Le nom de l'exercice est obligatoire")
    private String name;

    @NotBlank(message = "Le contenu de l'exercice est obligatoire")
    private String content;

    @NotNull(message = "L'identifiant du module est obligatoire")
    private Long moduleId;
}
