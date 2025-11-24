package com.example;

/**
 * Classe para gerenciar a sessão do usuário logado
 * Armazena informações sobre o tipo de usuário e permissões
 */
public class UserSession {
    
    private static UserSession instance;
    
    private String username;
    private String userType; // "ADMIN" ou "EMPLOYEE"
    private Long userId;
    private String fullName;
    
    private UserSession() {}
    
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    
    /**
     * Inicia a sessão do usuário
     */
    public void login(String username, String userType, Long userId, String fullName) {
        this.username = username;
        this.userType = userType;
        this.userId = userId;
        this.fullName = fullName;
    }
    
    /**
     * Encerra a sessão do usuário
     */
    public void logout() {
        this.username = null;
        this.userType = null;
        this.userId = null;
        this.fullName = null;
    }
    
    /**
     * Verifica se o usuário é administrador
     */
    public boolean isAdmin() {
        return "ADMIN".equals(userType);
    }
    
    /**
     * Verifica se o usuário é funcionário
     */
    public boolean isEmployee() {
        return "EMPLOYEE".equals(userType);
    }
    
    /**
     * Verifica se o usuário tem permissão para editar
     * Apenas administradores podem editar
     */
    public boolean canEdit() {
        return isAdmin();
    }
    
    /**
     * Verifica se o usuário tem permissão para excluir
     * Apenas administradores podem excluir
     */
    public boolean canDelete() {
        return isAdmin();
    }
    
    /**
     * Verifica se o usuário tem permissão para acessar finanças
     * Apenas administradores podem acessar finanças
     */
    public boolean canAccessFinances() {
        return isAdmin();
    }
    
    /**
     * Verifica se o usuário tem permissão para gerenciar usuários
     * Apenas administradores podem gerenciar usuários
     */
    public boolean canManageUsers() {
        return isAdmin();
    }
    
    // Getters
    public String getUsername() {
        return username;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public boolean isLoggedIn() {
        return username != null && userType != null;
    }
}
