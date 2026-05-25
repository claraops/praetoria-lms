package cloud.praetoria.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSummaryDTO {
    private Long quizId;
    private String name;
    private String content;
    private Boolean isCompleted;
    private Integer score;
    private Integer attempts;
}