package cloud.praetoria.lms.dtos;

import cloud.praetoria.lms.entities.Promotion;
import cloud.praetoria.lms.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoResponse {

    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private Long userCount;
    private List<UserSummaryResponse> users;

    public static PromoResponse fromEntity(Promotion promotion) {
        if (promotion == null) return null;
        
        return PromoResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .active(promotion.isActive())
                .userCount((long) promotion.getUsers().size())
                .build();
    }

    public static PromoResponse fromEntityWithUsers(Promotion promotion) {
        if (promotion == null) return null;
        
        List<UserSummaryResponse> userSummaries = promotion.getUsers().stream()
                .map(UserSummaryResponse::fromEntity)
                .collect(Collectors.toList());
        
        return PromoResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .active(promotion.isActive())
                .userCount((long) promotion.getUsers().size())
                .users(userSummaries)
                .build();
    }
}