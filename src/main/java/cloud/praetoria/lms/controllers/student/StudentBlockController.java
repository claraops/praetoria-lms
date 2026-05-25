package cloud.praetoria.lms.controllers.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.dtos.BlockProgressDTO;
import cloud.praetoria.lms.services.student.StudentBlockService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/student/blocks")
@RequiredArgsConstructor
@Tag(name = "Student - Blocs", description = "Endpoints pour l'étudiant - Consultation des blocs")
@PreAuthorize("hasAnyRole('STUDENT', 'TEACHER', 'ADMIN')")
public class StudentBlockController {

    private final StudentBlockService studentBlockService;

    @GetMapping
    @Operation(summary = "Lister les blocs accessibles à l'étudiant avec progression")
    public ResponseEntity<ApiResponse<List<BlockProgressDTO>>> getUserBlocks() {
        List<BlockProgressDTO> blocks = studentBlockService.getUserBlocksWithProgress();
        return ResponseEntity.ok(ApiResponse.success(blocks));
    }
}