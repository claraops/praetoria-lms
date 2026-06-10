package cloud.praetoria.lms.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import cloud.praetoria.lms.entities.Quiz;
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
public class QuizResponse {

    private Long id;
    private String name;
    private String content;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QuizResponse fromEntity(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .name(quiz.getName())
                .content(quiz.getContent())
                .moduleId(quiz.getModule() != null ? quiz.getModule().getId() : null)
                .moduleName(quiz.getModule() != null ? quiz.getModule().getName() : null)
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }
}
