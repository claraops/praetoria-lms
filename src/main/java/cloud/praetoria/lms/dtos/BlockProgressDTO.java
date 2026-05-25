package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockProgressDTO {
    private Long blockId;
    private String blockName;
    private String description;
    private String cover;
    private Integer totalModules;
    private Integer completedModules;
    private Double percentComplete;
    private Boolean isCompleted;
}