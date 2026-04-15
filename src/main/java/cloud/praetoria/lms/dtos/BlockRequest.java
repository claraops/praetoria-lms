package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockRequest {

    @NotBlank(message = "Le nom du bloc est obligatoire")
    private String name;

    private String description;

    private String cover;
}