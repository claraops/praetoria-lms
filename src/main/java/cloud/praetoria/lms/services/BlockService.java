package cloud.praetoria.lms.services;


import java.util.List;

import cloud.praetoria.lms.dtos.BlockRequest;
import cloud.praetoria.lms.dtos.BlockResponse;

public interface BlockService {

    /**
     * Récupère tous les blocs
     *
     * @return la liste des blocs
     */
    List<BlockResponse> getAllBlocks();

    /**
     * Récupère un bloc par son identifiant, avec la liste de ses modules.
     *
     * @param id l'identifiant du bloc
     * @return le bloc avec ses modules
     */
    BlockResponse getBlockById(Long id);

    /**
     * Crée un nouveau bloc.
     *
     * @param blockRequest les données du bloc à créer
     * @return le bloc créé
     */
    BlockResponse createBlock(BlockRequest blockRequest);

    /**
     * Met à jour un bloc existant.
     *
     * @param id l'identifiant du bloc à modifier
     * @param blockRequest les nouvelles données
     * @return le bloc mise à jour
     */
    BlockResponse updateBlock(Long id, BlockRequest blockRequest);

    /**
     * Supprime définitivement un bloc.
     *
     * @param id l'identifiant du bloc à supprimer
     */
    void deleteBlock(Long id);
}