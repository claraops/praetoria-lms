package cloud.praetoria.lms.services;

import java.util.List;

import cloud.praetoria.lms.dtos.PromoRequest;
import cloud.praetoria.lms.dtos.PromoResponse;

public interface PromoService {

    /**
     * Récupère toutes les promotions, triées par date de début.
     *
     * @return la liste des promotions
     */
    List<PromoResponse> getAllPromos();

    /**
     * Récupère une promotion par son identifiant, avec la liste de ses utilisateurs.
     *
     * @param id l'identifiant de la promotion
     * @return la promotion avec ses utilisateurs
     */
    PromoResponse getPromoById(Long id);

    /**
     * Crée une nouvelle promotion.
     *
     * @param promoRequest les données de la promotion à créer
     * @return la promotion créée
     */
    PromoResponse createPromo(PromoRequest promoRequest);

    /**
     * Met à jour une promotion existante.
     *
     * @param id l'identifiant de la promotion à modifier
     * @param promoRequest les nouvelles données
     * @return la promotion mise à jour
     */
    PromoResponse updatePromo(Long id, PromoRequest promoRequest);

    /**
     * Supprime définitivement une promotion.
     *
     * @param id l'identifiant de la promotion à supprimer
     */
    void deletePromo(Long id);

    /**
     * Bascule l'état actif/inactif d'une promotion.
     *
     * @param id l'identifiant de la promotion
     * @return la promotion avec son nouvel état
     */
    PromoResponse toggleActive(Long id);
}
