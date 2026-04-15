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
    private Boolean completed;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un {@link QuizResponse} sans information de complétion.
     * Utilisé pour les vues administrateur qui n'ont pas besoin de cette information.
     *
     * @param quiz l'entité quiz source
     * @return le DTO correspondant, {@code completed} à {@code null}
     */
    public static QuizResponse fromEntity(Quiz quiz) {
        return buildBase(quiz).build();
    }

    /**
     * Construit un {@link QuizResponse} avec l'état de complétion pour l'utilisateur courant.
     *
     * @param quiz l'entité quiz source
     * @param completedByUser {@code true} si l'utilisateur a complété ce quiz
     * @return le DTO correspondant avec le champ {@code completed} renseigné
     */
    public static QuizResponse fromEntity(Quiz quiz, boolean completedByUser) {
        return buildBase(quiz)
                .completed(completedByUser)
                .build();
    }

    /**
     * Initialise les champs communs pour les utilisateurs et administrateurs.
     *
     * @param quiz l'entité quiz source
     * @return un builder pré-rempli avec les champs communs
     */
    private static QuizResponseBuilder buildBase(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .name(quiz.getName())
                .content(quiz.getContent())
                .moduleId(quiz.getModule() != null ? quiz.getModule().getId() : null)
                .moduleName(quiz.getModule() != null ? quiz.getModule().getName() : null)
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt());
    }
}
