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
    private Boolean completed;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Construit un {@link ExerciseResponse} sans information de complétion.
     * Utilisé pour les vues administrateur qui n'ont pas besoin de cette information.
     *
     * @param exercise l'entité exercice source
     * @return le DTO correspondant, {@code completed} à {@code null}
     */
    public static ExerciseResponse fromEntity(Exercise exercise) {
        return buildBase(exercise).build();
    }

    /**
     * Construit un {@link ExerciseResponse} avec l'état de complétion pour l'utilisateur courant.
     *
     * @param exercise l'entité exercice source
     * @param completedByUser {@code true} si l'utilisateur a complété cet exercice
     * @return le DTO correspondant avec le champ {@code completed} renseigné
     */
    public static ExerciseResponse fromEntity(Exercise exercise, boolean completedByUser) {
        return buildBase(exercise)
                .completed(completedByUser)
                .build();
    }

    /**
     * Initialise les champs communs pour les utilisateurs et administrateurs.
     *
     * @param exercise l'entité exercice source
     * @return un builder pré-rempli avec les champs communs
     */
    private static ExerciseResponseBuilder buildBase(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .content(exercise.getContent())
                .moduleId(exercise.getModule() != null ? exercise.getModule().getId() : null)
                .moduleName(exercise.getModule() != null ? exercise.getModule().getName() : null)
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt());
    }
}
