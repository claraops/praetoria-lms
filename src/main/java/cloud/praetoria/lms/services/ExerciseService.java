package cloud.praetoria.lms.services;

import cloud.praetoria.lms.dtos.ExerciseRequest;
import cloud.praetoria.lms.dtos.ExerciseResponse;
import java.util.List;

public interface ExerciseService {

    List<ExerciseResponse> getAllExercises(Long userId);

    ExerciseResponse getExerciseById(Long id, Long userId);

    List<ExerciseResponse> getExercisesByModuleId(Long moduleId, Long userId);

    ExerciseResponse createExercise(ExerciseRequest exerciseRequest);

    ExerciseResponse updateExercise(Long id, ExerciseRequest exerciseRequest);

    void deleteExercise(Long id);

    ExerciseResponse toggleCompleted(Long exerciseId, Long userId);
}