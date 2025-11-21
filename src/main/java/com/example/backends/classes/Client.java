package com.example.backends.classes;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Client {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate registrationDate;
    private boolean isActive;
    private String notes;
    
    // Atributos específicos para cabeleireiros
    private String hairType; // Tipo de cabelo (liso, ondulado, cacheado, crespo)
    private String hairTexture; // Textura (fino, médio, grosso)
    private String scalp; // Couro cabeludo (oleoso, seco, misto, sensível)
    private String allergies; // Alergias ou sensibilidades
    private LocalDateTime lastVisit; // Última visita
    private String observations; // Observações sobre o cliente
    
    public Client() {
        this.registrationDate = LocalDate.now();
        this.isActive = true;
    }
    
    public Client(String name, String phoneNumber) {
        this();
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getHairType() {
        return hairType;
    }
    
    public void setHairType(String hairType) {
        this.hairType = hairType;
    }
    
    public String getHairTexture() {
        return hairTexture;
    }
    
    public void setHairTexture(String hairTexture) {
        this.hairTexture = hairTexture;
    }
    
    public String getScalp() {
        return scalp;
    }
    
    public void setScalp(String scalp) {
        this.scalp = scalp;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    public LocalDateTime getLastVisit() {
        return lastVisit;
    }
    
    public void setLastVisit(LocalDateTime lastVisit) {
        this.lastVisit = lastVisit;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Client client = (Client) obj;
        return id != null && id.equals(client.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return name; // Retornar apenas o nome para exibição em ComboBox
    }
}