package es.grupo18.jobmatcher.model;

// This class doesn't have any uses right now, but it will in the future

public class Admin extends Account {
    
    private Role role;
    
    public Admin(Long accountId, String name, String email, String password, Role role) {
        super(accountId, name, email, password);
        this.role = role;
    }

    public enum Role {
        SUPER_ADMIN, MODERATOR, SUPPORT
    }
    
    // Getters

    public Role getRole() { return role; }
    
    // Setters

    public void setRole(Role role) { this.role = role; }

    // Update methods

    public void updateRole(Role newRole){
        this.role = newRole;
    }

}
