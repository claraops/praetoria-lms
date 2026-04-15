package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.CourseRequest;
import cloud.praetoria.lms.dtos.CourseResponse;
import cloud.praetoria.lms.entities.Course;
import cloud.praetoria.lms.entities.Module;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.CourseRepository;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.repositories.UserRepository;
import cloud.praetoria.lms.services.CourseService;
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
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    @Override
    public List<CourseResponse> getAllCourses(Long userId) {
        return courseRepository.findAll().stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponse getCourseById(Long id, Long userId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
        return CourseResponse.fromEntity(course);
    }

    @Override
    public List<CourseResponse> getCoursesByModuleId(Long moduleId, Long userId) {
        return courseRepository.findByModuleId(moduleId).stream()
                .map(CourseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest courseRequest) {
        Module module = moduleRepository.findById(courseRequest.getModuleId())
                .orElseThrow(() -> new ResourceNotFoundException("Module non trouvé"));
        
        Course course = Course.builder()
                .name(courseRequest.getName())
                .description(courseRequest.getDescription())
                .videoUrl(courseRequest.getVideoUrl())
                .content(courseRequest.getContent())
                .module(module)
                .build();
        
        return CourseResponse.fromEntity(courseRepository.save(course));
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest courseRequest) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
        
        course.setName(courseRequest.getName());
        course.setDescription(courseRequest.getDescription());
        course.setVideoUrl(courseRequest.getVideoUrl());
        course.setContent(courseRequest.getContent());
        
        return CourseResponse.fromEntity(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CourseResponse toggleCompleted(Long courseId, Long userId) {
        // Version simplifiée - retourne le cours sans changer l'état
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));
        return CourseResponse.fromEntity(course);
    }
}