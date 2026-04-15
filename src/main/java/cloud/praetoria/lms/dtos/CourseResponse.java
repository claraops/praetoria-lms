package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponse {
    private Long id;
    private String name;
    private String description;
    private String videoUrl;
    private String content;
    private Boolean completed;
    private Long moduleId;
    private String moduleName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CourseResponse fromEntity(Course course) {
        return builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .videoUrl(course.getVideoUrl())
                .content(course.getContent())
                .moduleId(course.getModule() != null ? course.getModule().getId() : null)
                .moduleName(course.getModule() != null ? course.getModule().getName() : null)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    public static CourseResponse fromEntity(Course course, boolean completed) {
        return builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .videoUrl(course.getVideoUrl())
                .content(course.getContent())
                .completed(completed)
                .moduleId(course.getModule() != null ? course.getModule().getId() : null)
                .moduleName(course.getModule() != null ? course.getModule().getName() : null)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}