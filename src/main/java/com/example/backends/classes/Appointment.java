package com.example.backends.classes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.example.backends.enums.AppointmentStatus; // ← Import corrigido!

public class Appointment {
    private Long id;
    private Long clientId; // Referência ao cliente
    private Long stylistId; // Referência ao cabeleireiro/funcionário
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status; // Usando enum em vez de String
    private List<Long> serviceIds; // Lista de IDs dos serviços
    private BigDecimal totalPrice;
    private String notes; // Observações do atendimento
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Appointment() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = AppointmentStatus.AGENDADO;
    }
    
    public Appointment(Long clientId, LocalDateTime appointmentDateTime) {
        this();
        this.clientId = clientId;
        this.appointmentDateTime = appointmentDateTime;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public Long getStylistId() {
        return stylistId;
    }
    
    public void setStylistId(Long stylistId) {
        this.stylistId = stylistId;
    }
    
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
    
    public AppointmentStatus getStatus() {
        return status;
    }
    
    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
    
    public List<Long> getServiceIds() {
        return serviceIds;
    }
    
    public void setServiceIds(List<Long> serviceIds) {
        this.serviceIds = serviceIds;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Métodos utilitários
    public boolean isCompleted() {
        return status == AppointmentStatus.CONCLUIDO;
    }
    
    public boolean isCancelled() {
        return status == AppointmentStatus.CANCELADO;
    }
    
    public boolean isActive() {
        return status != null && status.isActive();
    }
    
    public boolean isFinished() {
        return status != null && status.isFinished();
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", stylistId=" + stylistId +
                ", appointmentDateTime=" + appointmentDateTime +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                '}';
    }
}