package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignBlocksRequest {

    @NotNull(message = "La liste des identifiants de blocs est obligatoire")
    private List<Long> blockIds;
}
