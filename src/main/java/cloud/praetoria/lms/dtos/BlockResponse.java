package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.Block;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockResponse {

    private Long id;
    private String name;
    private String description;
    private String cover;
    private List<ModuleResponse> modules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BlockResponse fromEntity(Block block) {
        return BlockResponse.builder()
                .id(block.getId())
                .name(block.getName())
                .description(block.getDescription())
                .cover(block.getCover())
                .createdAt(block.getCreatedAt())
                .updatedAt(block.getUpdatedAt())
                .build();
    }

    public static BlockResponse fromEntityWithModules(Block block) {
        BlockResponse response = fromEntity(block);
        if (block.getModules() != null) {
            response.setModules(block.getModules().stream()
                    .map(ModuleResponse::fromEntity)
                    .toList());
        }
        return response;
    }
}