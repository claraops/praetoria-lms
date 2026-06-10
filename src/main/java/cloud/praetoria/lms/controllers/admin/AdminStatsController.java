package cloud.praetoria.lms.controllers.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cloud.praetoria.lms.dtos.ApiResponse;
import cloud.praetoria.lms.repositories.*;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final UserBadgeRepository userBadgeRepository;

    @GetMapping("/global")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("activeOrganizations", organizationRepository.count());
        stats.put("totalCoursesCompleted", userCourseProgressRepository.count());
        stats.put("totalBadgesAwarded", userBadgeRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/organizations/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOrganizationStats(@PathVariable Long id) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("organizationId", id);
        stats.put("activeStudents", userRepository.count());
        stats.put("totalCoursesCompleted", userCourseProgressRepository.count());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
