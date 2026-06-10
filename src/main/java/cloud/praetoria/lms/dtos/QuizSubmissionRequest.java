package cloud.praetoria.lms.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionRequest {
    @NotNull(message = "Les réponses sont requises")
    private Map<Integer, String> answers;  
}