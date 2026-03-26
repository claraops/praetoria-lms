package cloud.praetoria.lms.enums;

public enum RoleName {
    ROLE_STUDENT("Étudiant"),
    ROLE_TEACHER("Enseignant"),
    ROLE_ADMIN("Administrateur");
    
    private final String description;
    
    RoleName(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}