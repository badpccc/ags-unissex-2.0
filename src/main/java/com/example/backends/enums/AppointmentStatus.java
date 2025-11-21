package com.example.backends.enums;

public enum AppointmentStatus {
    AGENDADO("Agendado", "Agendamento confirmado"),
    EM_ANDAMENTO("Em Andamento", "Atendimento em execução"),
    CONCLUIDO("Concluído", "Atendimento finalizado"),
    CANCELADO("Cancelado", "Agendamento cancelado"),
    NAO_COMPARECEU("Não Compareceu", "Cliente não compareceu");
    
    private final String displayName;
    private final String description;
    
    AppointmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isActive() {
        return this == AGENDADO || this == EM_ANDAMENTO;
    }
    
    public boolean isFinished() {
        return this == CONCLUIDO || this == CANCELADO || this == NAO_COMPARECEU;
    }
    
    public String getColor() {
        return switch (this) {
            case AGENDADO -> "#3b82f6";        // Azul
            case EM_ANDAMENTO -> "#f59e0b";   // Amarelo
            case CONCLUIDO -> "#10b981";      // Verde
            case CANCELADO -> "#ef4444";      // Vermelho
            case NAO_COMPARECEU -> "#9ca3af"; // Cinza
        };
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}