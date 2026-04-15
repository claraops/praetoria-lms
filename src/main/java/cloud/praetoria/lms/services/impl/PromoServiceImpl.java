package cloud.praetoria.lms.services.impl;

import cloud.praetoria.lms.dtos.PromoRequest;
import cloud.praetoria.lms.dtos.PromoResponse;
import cloud.praetoria.lms.entities.Promotion;
import cloud.praetoria.lms.exceptions.DuplicateResourceException;
import cloud.praetoria.lms.exceptions.ResourceNotFoundException;
import cloud.praetoria.lms.repositories.PromotionRepository;
import cloud.praetoria.lms.services.PromoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PromoServiceImpl implements PromoService {

    private final PromotionRepository promotionRepository;

    @Override
    public List<PromoResponse> getAllPromos() {
        log.debug("Récupération de toutes les promotions");
        return promotionRepository.findAll().stream()
                .map(PromoResponse::fromEntity)
                .toList();
    }

    @Override
    public PromoResponse getPromoById(Long id) {
        log.debug("Récupération de la promotion avec l'id: {}", id);

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id: " + id));

        return PromoResponse.fromEntityWithUsers(promotion);
    }

    @Override
    @Transactional
    public PromoResponse createPromo(PromoRequest promoRequest) {
        log.info("Création d'une nouvelle promotion: {}", promoRequest.getName());

        if (promotionRepository.existsByNameIgnoreCase(promoRequest.getName())) {
            throw new DuplicateResourceException("Une promotion avec ce nom existe déjà: " + promoRequest.getName());
        }

        Promotion promotion = Promotion.builder()
                .name(promoRequest.getName())
                .startDate(promoRequest.getStartDate())
                .endDate(promoRequest.getEndDate())
                .active(true)
                .build();

        Promotion savedPromotion = promotionRepository.save(promotion);
        log.info("Promotion créée avec succès: {}", savedPromotion.getName());

        return PromoResponse.fromEntity(savedPromotion);
    }

    @Override
    @Transactional
    public PromoResponse updatePromo(Long id, PromoRequest promoRequest) {
        log.info("Mise à jour de la promotion avec l'id: {}", id);

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id: " + id));

        if (promotionRepository.existsByNameIgnoreCaseAndIdNot(promoRequest.getName(), id)) {
            throw new DuplicateResourceException("Une promotion avec ce nom existe déjà: " + promoRequest.getName());
        }

        promotion.setName(promoRequest.getName());
        promotion.setStartDate(promoRequest.getStartDate());
        promotion.setEndDate(promoRequest.getEndDate());

        Promotion updatedPromotion = promotionRepository.save(promotion);
        log.info("Promotion mise à jour avec succès: {}", updatedPromotion.getName());

        return PromoResponse.fromEntity(updatedPromotion);
    }

    @Override
    @Transactional
    public void deletePromo(Long id) {
        log.info("Suppression de la promotion avec l'id: {}", id);

        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion non trouvée avec l'id: " + id);
        }

        promotionRepository.deleteById(id);
        log.info("Promotion supprimée avec succès: {}", id);
    }

    @Override
    @Transactional
    public PromoResponse toggleActive(Long id) {
        log.info("Bascule de l'état actif de la promotion avec l'id: {}", id);

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id: " + id));

        promotion.setActive(!promotion.isActive());
        Promotion updatedPromotion = promotionRepository.save(promotion);

        log.info("Promotion {} {}", updatedPromotion.getName(), 
                updatedPromotion.isActive() ? "activée" : "désactivée");

        return PromoResponse.fromEntity(updatedPromotion);
    }
}