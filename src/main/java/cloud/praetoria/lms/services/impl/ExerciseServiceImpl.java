package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.ExerciseRequest;
import cloud.praetoria.lms.dtos.ExerciseResponse;
import cloud.praetoria.lms.entities.Exercise;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.ExerciseRepository;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ModuleRepository moduleRepository;

    @Override
    public List<ExerciseResponse> getAllExercises(Long userId) {
        return exerciseRepository.findAll().stream()
                .map(ExerciseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ExerciseResponse getExerciseById(Long id, Long userId) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice non trouvé"));
        return ExerciseResponse.fromEntity(exercise);
    }

    @Override
    public List<ExerciseResponse> getExercisesByModuleId(Long moduleId, Long userId) {
        return exerciseRepository.findByModuleId(moduleId).stream()
                .map(ExerciseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExerciseResponse createExercise(ExerciseRequest exerciseRequest) {
        Module module = moduleRepository.findById(exerciseRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        
        Exercise exercise = Exercise.builder()
                .name(exerciseRequest.getName())
                .content(exerciseRequest.getContent())
                .module(module)
                .build();
        
        return ExerciseResponse.fromEntity(exerciseRepository.save(exercise));
    }

    @Override
    @Transactional
    public ExerciseResponse updateExercise(Long id, ExerciseRequest exerciseRequest) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice non trouvé"));
        
        exercise.setName(exerciseRequest.getName());
        exercise.setContent(exerciseRequest.getContent());
        
        return ExerciseResponse.fromEntity(exerciseRepository.save(exercise));
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        exerciseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ExerciseResponse toggleCompleted(Long exerciseId, Long userId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercice non trouvé"));
        return ExerciseResponse.fromEntity(exercise);
    }
}