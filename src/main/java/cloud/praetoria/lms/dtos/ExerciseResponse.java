package cloud.praetoria.lms.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import cloud.praetoria.lms.entities.Exercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExerciseResponse {

    private Long id;
    private String name;
    private String content;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ExerciseResponse fromEntity(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .content(exercise.getContent())
                .moduleId(exercise.getModule() != null ? exercise.getModule().getId() : null)
                .moduleName(exercise.getModule() != null ? exercise.getModule().getName() : null)
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .build();
    }
}
