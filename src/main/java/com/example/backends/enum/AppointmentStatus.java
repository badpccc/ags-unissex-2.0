package com.example.backends.enum;

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
    
    @Override
    public String toString() {
        return displayName;
    }
}