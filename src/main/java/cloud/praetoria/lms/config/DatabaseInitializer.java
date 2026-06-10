package cloud.praetoria.lms.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import cloud.praetoria.lms.entities.Organization;
import cloud.praetoria.lms.entities.Role;
import cloud.praetoria.lms.entities.User;
import cloud.praetoria.lms.enums.RoleName;
import cloud.praetoria.lms.repositories.OrganizationRepository;
import cloud.praetoria.lms.repositories.RoleRepository;
import cloud.praetoria.lms.repositories.UserRepository;
import cloud.praetoria.lms.services.GamificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GamificationService gamificationService;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Des utilisateurs existent déjà, initialisation ignorée");
            return;
        }

        Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
            .orElseThrow(() -> new RuntimeException("ROLE_ADMIN non trouvé"));
        Role teacherRole = roleRepository.findByRoleName(RoleName.ROLE_TEACHER)
            .orElseThrow(() -> new RuntimeException("ROLE_TEACHER non trouvé"));
        Role studentRole = roleRepository.findByRoleName(RoleName.ROLE_STUDENT)
            .orElseThrow(() -> new RuntimeException("ROLE_STUDENT non trouvé"));

        Organization org = organizationRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Aucune organisation trouvée dans data.sql"));

        String encodedPassword = passwordEncoder.encode("password123");

        User admin = userRepository.save(User.builder()
            .email("admin@praetoria.cloud")
            .firstName("Admin")
            .lastName("Principal")
            .password(encodedPassword)
            .role(adminRole)
            .organization(org)
            .isActive(true)
            .build());
        gamificationService.initializeGamification(admin);
        log.info("Utilisateur admin créé: admin@praetoria.cloud");

        User teacher = userRepository.save(User.builder()
            .email("teacher@praetoria.cloud")
            .firstName("Teacher")
            .lastName("Test")
            .password(encodedPassword)
            .role(teacherRole)
            .organization(org)
            .isActive(true)
            .build());
        gamificationService.initializeGamification(teacher);
        log.info("Utilisateur teacher créé: teacher@praetoria.cloud");

        User student = userRepository.save(User.builder()
            .email("student@praetoria.cloud")
            .firstName("Student")
            .lastName("Test")
            .password(encodedPassword)
            .role(studentRole)
            .organization(org)
            .isActive(true)
            .build());
        gamificationService.initializeGamification(student);
        log.info("Utilisateur student créé: student@praetoria.cloud");
    }
}
