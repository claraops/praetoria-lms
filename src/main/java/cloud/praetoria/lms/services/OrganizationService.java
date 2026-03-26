package cloud.praetoria.lms.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cloud.praetoria.lms.dtos.CreateOrganizationRequest;
import cloud.praetoria.lms.dtos.OrganizationDTO;
import cloud.praetoria.lms.entities.Organization;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {
    
    private final OrganizationRepository organizationRepository;
    
    /**
     * Créer une nouvelle organisation (admin Praetoria uniquement)
     */
    @Transactional
    public OrganizationDTO createOrganization(CreateOrganizationRequest request) {
        log.info("Création d'une organisation: {}", request.getName());
        
        // Générer une clé unique : PRE- + 8 caractères
        String registrationKey = generateRegistrationKey();
        
        Organization organization = Organization.builder()
            .name(request.getName())
            .registrationKey(registrationKey)
            .contactEmail(request.getContactEmail())
            .maxStudents(request.getMaxStudents())
            .licenseExpiresAt(request.getLicenseExpiresAt())
            .isActive(true)
            .build();
        
        organizationRepository.save(organization);
        log.info("Organisation créée: {} avec clé: {}", request.getName(), registrationKey);
        
        return toDTO(organization);
    }
    
    /**
     * Récupérer une organisation par ID
     */
    public OrganizationDTO getOrganizationById(Long id) {
        Organization org = organizationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée"));
        return toDTO(org);
    }
    
    /**
     * Lister toutes les organisations
     */
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Régénérer la clé d'inscription d'une organisation
     */
    @Transactional
    public OrganizationDTO regenerateKey(Long id) {
        Organization org = organizationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée"));
        
        String newKey = generateRegistrationKey();
        org.setRegistrationKey(newKey);
        organizationRepository.save(org);
        
        log.info("Clé régénérée pour l'organisation {}: {}", org.getName(), newKey);
        return toDTO(org);
    }
    
    /**
     * Activer/Désactiver une organisation
     */
    @Transactional
    public void toggleOrganizationActive(Long id, boolean isActive) {
        Organization org = organizationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Organisation non trouvée"));
        
        org.setIsActive(isActive);
        organizationRepository.save(org);
        log.info("Organisation {} {} ", org.getName(), isActive ? "activée" : "désactivée");
    }
    
    /**
     * Générer une clé d'inscription unique
     */
    private String generateRegistrationKey() {
        String key;
        do {
            key = "PRE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (organizationRepository.existsByRegistrationKey(key));
        return key;
    }
    
    private OrganizationDTO toDTO(Organization org) {
        long studentCount = organizationRepository.countActiveStudents(org);
        
        return OrganizationDTO.builder()
            .id(org.getId())
            .name(org.getName())
            .registrationKey(org.getRegistrationKey())
            .contactEmail(org.getContactEmail())
            .maxStudents(org.getMaxStudents())
            .currentStudentCount(studentCount)
            .isActive(org.getIsActive())
            .licenseExpiresAt(org.getLicenseExpiresAt())
            .createdAt(org.getCreatedAt())
            .build();
    }
    
}
