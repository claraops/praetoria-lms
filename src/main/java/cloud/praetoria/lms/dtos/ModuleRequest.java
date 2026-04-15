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
public class ModuleRequest {

    @NotBlank(message = "Le nom du module est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "L'identifiant du bloc est obligatoire")
    private Long blockId;
}

