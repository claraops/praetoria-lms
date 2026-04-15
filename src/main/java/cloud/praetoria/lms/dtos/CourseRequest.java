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
public class CourseRequest {

    @NotBlank(message = "Le nom du cours est obligatoire")
    private String name;

    private String description;

    private String videoUrl;

    @NotBlank(message = "Le contenu du cours est obligatoire")
    private String content;

    @NotNull(message = "L'identifiant du module est obligatoire")
    private Long moduleId;
}
