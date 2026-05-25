package cloud.praetoria.lms.controllers.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.CourseSummaryDTO;
import cloud.praetoria.lms.services.student.StudentCourseService;

@Slf4j
@RestController
@RequestMapping("/api/student/courses")
@RequiredArgsConstructor
@Tag(name = "Student - Cours", description = "Endpoints pour l'étudiant - Consultation et progression des cours")
@PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
public class StudentCourseController {

    private final StudentCourseService studentCourseService;

    @GetMapping("/{courseId}")
    @Operation(summary = "Récupérer le contenu complet d'un cours (démarrage auto si premier accès)")
    public ResponseEntity<ApiResponse<CourseSummaryDTO>> getCourseContent(@PathVariable Long courseId) {
        CourseSummaryDTO course = studentCourseService.getCourseContent(courseId);
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @PostMapping("/{courseId}/complete")
    @Operation(summary = "Marquer un cours comme terminé (+10 XP)")
    public ResponseEntity<ApiResponse<Void>> completeCourse(@PathVariable Long courseId) {
        studentCourseService.completeCourse(courseId);
        return ResponseEntity.ok(ApiResponse.successVoid("Cours complété ! +10 XP"));
    }
}