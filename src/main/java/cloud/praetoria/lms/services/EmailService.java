package cloud.praetoria.lms.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import cloud.praetoria.lms.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private static final String FROM_EMAIL = "on verra ici pour l'adresse mail plus tard";
    private static final String RESET_PASSWORD_URL = "pareil pour l'url";
    
    /**
     * Envoyer email de réinitialisation du mot de passe
     */
    public void sendPasswordResetEmail(User user, String token) {
        log.info("Envoi email de réinitialisation: {}", user.getEmail());
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String resetLink = RESET_PASSWORD_URL + "?token=" + token;
            
            helper.setTo(user.getEmail());
            helper.setFrom(FROM_EMAIL);
            helper.setSubject("Définir votre mot de passe Praetoria");
            
            String htmlContent = buildPasswordResetEmailHtml(user, resetLink);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email envoyé avec succès: {}", user.getEmail());
            
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email", e);
            throw new RuntimeException("Impossible d'envoyer l'email");
        }
    }
    
    /**
     * Envoyer email de confirmation de reset
     */
    public void sendPasswordResetConfirmationEmail(User user) {
        log.info("Envoi email de confirmation: {}", user.getEmail());
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setFrom(FROM_EMAIL);
            message.setSubject("Mot de passe réinitialisé");
            message.setText(
                "Bonjour " + user.getFirstName() + ",\n\n" +
                "Votre mot de passe a été réinitialisé avec succès.\n" +
                "Vous pouvez maintenant vous connecter à votre compte.\n\n" +
                "Si vous n'avez pas effectué cette action, contactez-nous immédiatement.\n\n" +
                "Cordialement,\n" +
                "L'équipe Praetoria"
            );
            
            mailSender.send(message);
            log.info("Email de confirmation envoyé: {}", user.getEmail());
            
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de confirmation", e);
        }
    }
    
    /**
     * Construire le HTML de l'email de reset
     */
    private String buildPasswordResetEmailHtml(User user, String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #6366F1; color: white; padding: 20px; text-align: center; border-radius: 5px; }
                    .content { padding: 20px; background-color: #f9fafb; margin: 20px 0; border-radius: 5px; }
                    .button { background-color: #6366F1; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; }
                    .footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Praetoria LMS</h1>
                    </div>
                    <div class="content">
                        <p>Bonjour %s,</p>
                        <p>Cliquez sur le bouton ci-dessous pour définir ou réinitialiser votre mot de passe:</p>
                        <center>
                            <a href="%s" class="button">Définir mon mot de passe</a>
                        </center>
                        <p style="color: #999; font-size: 12px;">Ce lien expire dans 1 heure.</p>
                        <p style="color: #999; font-size: 12px;">Si vous n'avez pas demandé cette réinitialisation, ignorez cet email.</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 Praetoria. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(user.getFirstName(), resetLink);
    }
    
}
