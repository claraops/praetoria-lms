package cloud.praetoria.lms.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.CourseRequest;
import cloud.praetoria.lms.dtos.CourseResponse;
import cloud.praetoria.lms.security.UserDetailsImpl;
import cloud.praetoria.lms.services.CourseService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Cours", description = "Endpoints pour la gestion des cours")
public class CourseController {


    private final CourseService courseService;

    
    @GetMapping
    @Operation(summary = "Lister tous les cours")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if (userDetails == null) {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
                userDetails = (UserDetailsImpl) auth.getPrincipal();
            }
        }

        if (userDetails == null) {
            log.error(" userDetails est toujours null après vérification manuelle");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Utilisateur non authentifié"));
        }
        
        log.debug("✅ Récupération des cours pour: {}", userDetails.getEmail());
        List<CourseResponse> courses = courseService.getAllCourses(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(courses));
    }
 
    	
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un cours par son identifiant")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CourseResponse course = courseService.getCourseById(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(course));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Lister les cours d'un module")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getCoursesByModuleId(
            @PathVariable Long moduleId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<CourseResponse> courses = courseService.getCoursesByModuleId(moduleId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(courses));
    }

    @PostMapping
    @Operation(summary = "Créer un cours")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse courseResponse = courseService.createCourse(courseRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseResponse, "Cours créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un cours")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseRequest courseRequest) {
        CourseResponse courseResponse = courseService.updateCourse(id, courseRequest);
        return ResponseEntity.ok(ApiResponse.success(courseResponse, "Cours mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un cours")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.successVoid("Cours supprimé avec succès"));
    }

   
}