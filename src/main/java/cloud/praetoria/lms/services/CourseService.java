package cloud.praetoria.lms.services;

import cloud.praetoria.lms.dtos.CourseRequest;
import cloud.praetoria.lms.dtos.CourseResponse;
import java.util.List;

public interface CourseService {

    List<CourseResponse> getAllCourses(Long userId);

    CourseResponse getCourseById(Long id, Long userId);

    List<CourseResponse> getCoursesByModuleId(Long moduleId, Long userId);

    CourseResponse createCourse(CourseRequest courseRequest);

    CourseResponse updateCourse(Long id, CourseRequest courseRequest);

    void deleteCourse(Long id);

    CourseResponse toggleCompleted(Long courseId, Long userId);
}