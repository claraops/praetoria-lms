package cloud.praetoria.lms.services;


import java.util.List;

import cloud.praetoria.lms.dtos.QuizRequest;
import cloud.praetoria.lms.dtos.QuizResponse;

public interface QuizService {

    /**
     * Récupère tous les quiz avec l'état de complétion pour l'utilisateur donné.
     *
     * @param userId l'identifiant de l'utilisateur courant
     * @return la liste des quiz
     */
    List<QuizResponse> getAllQuizzes(Long userId);

    /**
     * Récupère un quiz par son identifiant.
     *
     * @param id l'identifiant du quiz
     * @param userId l'identifiant de l'utilisateur courant
     * @return le quiz correspondant
     */
    QuizResponse getQuizById(Long id, Long userId);

    /**
     * Crée un nouveau quiz rattaché à un module (un seul quiz par module).
     *
     * @param quizRequest les données du quiz à créer
     * @return le quiz créé
     */
    QuizResponse createQuiz(QuizRequest quizRequest);

    /**
     * Met à jour un quiz existant.
     *
     * @param id l'identifiant du quiz à modifier
     * @param quizRequest les nouvelles données
     * @return le quiz mis à jour
     */
    QuizResponse updateQuiz(Long id, QuizRequest quizRequest);

    /**
     * Supprime définitivement un quiz.
     *
     * @param id l'identifiant du quiz à supprimer
     */
    void deleteQuiz(Long id);

    /**
     * Bascule l'état complété/non complété d'un quiz pour un utilisateur donné.
     *
     * @param quizId l'identifiant du quiz
     * @param userId l'identifiant de l'utilisateur courant
     * @return le quiz avec le nouvel état de complétion
     */
    QuizResponse toggleCompleted(Long quizId, Long userId);
}
