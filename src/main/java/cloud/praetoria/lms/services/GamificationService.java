package cloud.praetoria.lms.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.BadgeDTO;
import cloud.praetoria.lms.dtos.GamificationProfileDTO;
import cloud.praetoria.lms.dtos.LeaderboardDTO;
import cloud.praetoria.lms.dtos.LeaderboardEntryDTO;
import cloud.praetoria.lms.entities.Badge;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.entities.UserBadge;
import cloud.praetoria.lms.entities.UserGamification;
import cloud.praetoria.lms.repositories.BadgeRepository;
import cloud.praetoria.lms.repositories.CourseRepository;
import cloud.praetoria.lms.repositories.ModuleRepository;
import cloud.praetoria.lms.repositories.UserBadgeRepository;
import cloud.praetoria.lms.repositories.UserCourseProgressRepository;
import cloud.praetoria.lms.repositories.UserGamificationRepository;
import cloud.praetoria.lms.repositories.UserQuizProgressRepository;
import cloud.praetoria.lms.repositories.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GamificationService {

    private final UserGamificationRepository userGamificationRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final UserQuizProgressRepository userQuizProgressRepository;
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    
    private static final int XP_PER_COURSE_COMPLETION = 10;
    private static final int XP_PER_MODULE_COMPLETION = 50;
    private static final int XP_PER_QUIZ_COMPLETION = 20;
    private static final int XP_BONUS_PERFECT_SCORE = 10;

    /**
     * Initialise la gamification pour un nouvel utilisateur
     */
    @Transactional
    public void initializeGamification(User user) {
        if (userGamificationRepository.findByUser(user).isEmpty()) {
            UserGamification gamification = UserGamification.builder()
                    .user(user)
                    .totalXp(0)
                    .level(1)
                    .streakDays(0)
                    .longestStreak(0)
                    .build();
            userGamificationRepository.save(gamification);
            log.info(" Gamification initialisée pour l'utilisateur: {}", user.getEmail());
        }
    }

    /**
     * Ajoute des XP et met à jour le streak
     */
    @Transactional
    public void addXp(User user, int xpAmount) {
        UserGamification gamification = userGamificationRepository.findByUser(user)
                .orElseGet(() -> {
                    initializeGamification(user);
                    return userGamificationRepository.findByUser(user).get();
                });
        
        gamification.updateStreak();

        gamification.addXp(xpAmount);
        userGamificationRepository.save(gamification);
        
        log.info("les +{} XP pour {} (Total: {} XP, Niveau: {})", 
                xpAmount, user.getEmail(), gamification.getTotalXp(), gamification.getLevel());
 
        checkAndAwardBadges(user);
    }

    /**
     * Vérifie et attribue les badges selon les critères
     */
    @Transactional
    public void checkAndAwardBadges(User user) {
        UserGamification gamification = userGamificationRepository.findByUser(user).orElse(null);
        if (gamification == null) return;

        List<Badge> allBadges = badgeRepository.findAll();
        List<Badge> awardedBadges = new ArrayList<>();

        for (Badge badge : allBadges) {
            if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
                continue;
            }
            
           boolean shouldAward = checkBadgeCriteria(user, badge, gamification);
            
            if (shouldAward) {
                awardBadge(user, badge);
                awardedBadges.add(badge);
            }
        }

        if (!awardedBadges.isEmpty()) {
            log.info(" Nouveaux badges pour {} : {}", 
                    user.getEmail(), 
                    awardedBadges.stream().map(Badge::getDisplayName).collect(Collectors.joining(", ")));
        }
    }

    /**
     * Vérifie les critères spécifiques d'un badge
     *****/
    private boolean checkBadgeCriteria(User user, Badge badge, UserGamification gamification) {
        return switch (badge.getName()) {
            case "FIRST_COURSE" -> hasCompletedFirstCourse(user);
            case "MODULE_COMPLETED" -> hasCompletedModule(user);
            case "STREAK_7" -> gamification.getStreakDays() >= 7;
            case "STREAK_30" -> gamification.getStreakDays() >= 30;
            case "XP_100" -> gamification.getTotalXp() >= 100;
            case "XP_500" -> gamification.getTotalXp() >= 500;
            case "XP_1000" -> gamification.getTotalXp() >= 1000;
            case "QUIZ_MASTER" -> hasCompletedTenQuizzes(user);
            case "PERFECT_SCORE" -> hasPerfectScore(user);
            default -> false;
        };
    }
    
    
    private boolean hasCompletedFirstCourse(User user) {
			    return userCourseProgressRepository.countByUserAndCompletedTrue(user) > 0;
			}
			
			private boolean hasCompletedModule(User user) {
				
			    // a faire plus tard ( vérifier s'il existe un module dont tous les cours sont complétés)
			    // Utilisez UserCourseProgressRepository et CourseRepository
				return false;
			}
			
			private boolean hasCompletedTenQuizzes(User user) {
			    return userQuizProgressRepository.countByUserAndCompletedTrue(user) >= 10;
			}
			
			private boolean hasPerfectScore(User user) {
			    return userQuizProgressRepository.findByUser(user).stream()
			           .anyMatch(q -> q.getScore() != null && q.getScore() == 100);
			}

	/**
     * Attribue un badge à l'utilisateur
     */
    private void awardBadge(User user, Badge badge) {
        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .earnedAt(LocalDateTime.now())
                .build();
        userBadgeRepository.save(userBadge);

        log.info("Badge débloqué: {} pour {}", badge.getDisplayName(), user.getEmail());
    }

    /**
     * Récupère le profil de gamification d'un utilisateur
     */
    @Transactional(readOnly = true)
    public GamificationProfileDTO getGamificationProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        UserGamification gamification = userGamificationRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profil de gamification non trouvé"));

        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);
        long totalBadges = userBadgeRepository.countByUser(user);

        int xpToNextLevel = 100 - (gamification.getTotalXp() % 100);
        int progressToNextLevel = gamification.getTotalXp() % 100;

        List<BadgeDTO> badgeDTOs = new ArrayList<>();
        for (Badge badge : badgeRepository.findAll()) {
            boolean earned = userBadges.stream().anyMatch(ub -> ub.getBadge().getId().equals(badge.getId()));
            String earnedAt = userBadges.stream()
                    .filter(ub -> ub.getBadge().getId().equals(badge.getId()))
                    .findFirst()
                    .map(ub -> ub.getEarnedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .orElse(null);
            
            badgeDTOs.add(BadgeDTO.builder()
                    .badgeId(badge.getId())
                    .name(badge.getName())
                    .displayName(badge.getDisplayName())
                    .description(badge.getDescription())
                    .iconUrl(badge.getIconUrl())
                    .criteria(badge.getCriteria())
                    .earned(earned)
                    .earnedAt(earnedAt)
                    .build());
        }

        return GamificationProfileDTO.builder()
                .userId(user.getId())
                .userName(user.getFirstName() + " " + user.getLastName())
                .userEmail(user.getEmail())
                .totalXp(gamification.getTotalXp())
                .level(gamification.getLevel())
                .streakDays(gamification.getStreakDays())
                .longestStreak(gamification.getLongestStreak())
                .lastActivityDate(gamification.getLastActivityDate() != null ? 
                        gamification.getLastActivityDate().toString() : null)
                .totalBadges((int) totalBadges)
                .xpToNextLevel(xpToNextLevel)
                .progressToNextLevel(progressToNextLevel)
                .badges(badgeDTOs)
                .build();
    }
    
 


    /**
     * Récupère le classement global (top 10)
     */
    @Transactional(readOnly = true)
    public LeaderboardDTO getLeaderboard() {
        List<UserGamification> topUsers = userGamificationRepository.findTop10ByOrderByTotalXpDesc();
        
        List<LeaderboardEntryDTO> entries = new ArrayList<>();
        int rank = 1;
        for (UserGamification ug : topUsers) {
            entries.add(buildLeaderboardEntry(ug, rank++));
        }
        
        return LeaderboardDTO.builder()
                .entries(entries)
                .totalUsers(entries.size())
                .build();
    }

    /**
     * Récupère le classement par organisation
     */
    @Transactional(readOnly = true)
    public LeaderboardDTO getOrganizationLeaderboard(Long organizationId) {
        List<UserGamification> topUsers = userGamificationRepository
                .findByUserOrganizationOrderByTotalXpDesc(organizationId);
        
        List<LeaderboardEntryDTO> entries = new ArrayList<>();
        int rank = 1;
        for (UserGamification ug : topUsers.stream().limit(10).collect(Collectors.toList())) {
            entries.add(buildLeaderboardEntry(ug, rank++));
        }
        
        return LeaderboardDTO.builder()
                .entries(entries)
                .totalUsers(topUsers.size())
                .build();
    }

    /**
     * Construit une entrée de classement
     */
    private LeaderboardEntryDTO buildLeaderboardEntry(UserGamification ug, int rank) {
        long badgeCount = userBadgeRepository.countBadgesByUser(ug.getUser());
        
        return LeaderboardEntryDTO.builder()
                .rank(rank)
                .userId(ug.getUser().getId())
                .userName(ug.getUser().getFullName())
                .userEmail(ug.getUser().getEmail())
                .organizationName(ug.getUser().getOrganization().getName())
                .totalXp(ug.getTotalXp())
                .level(ug.getLevel())
                .badgeCount(badgeCount)
                .build();
    }

}
