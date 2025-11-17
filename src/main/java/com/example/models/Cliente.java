package com.example.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cliente {

    private int id;
    private String nome;
    private String telefone;
    private String email;

    private String address;
    private LocalDate registrationDate;
    private boolean isActive;
    private String notes;

    private String hairType;
    private String hairTexture;
    private String scalp;
    private String allergies;
    private LocalDateTime lastVisit;

    // ---------------------- CONSTRUTOR VAZIO (OBRIGATÓRIO!) ----------------------
    public Cliente() {
    }
    public Cliente(int id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;

        this.address = null;
        this.registrationDate = null;
        this.isActive = true;
        this.notes = null;
        this.hairType = null;
        this.hairTexture = null;
        this.scalp = null;
        this.allergies = null;
        this.lastVisit = null;
    }

    // ---------------------- CONSTRUTORES ----------------------
    public Cliente(String nome, String telefone, String email,
                   String address, LocalDate registrationDate, boolean isActive, String notes,
                   String hairType, String hairTexture, String scalp, String allergies,
                   LocalDateTime lastVisit) {

        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.address = address;
        this.registrationDate = registrationDate;
        this.isActive = isActive;
        this.notes = notes;
        this.hairType = hairType;
        this.hairTexture = hairTexture;
        this.scalp = scalp;
        this.allergies = allergies;
        this.lastVisit = lastVisit;
    }

    public Cliente(int id, String nome, String telefone, String email,
                   String address, LocalDate registrationDate, boolean isActive, String notes,
                   String hairType, String hairTexture, String scalp, String allergies,
                   LocalDateTime lastVisit) {

        this(nome, telefone, email, address, registrationDate, isActive, notes,
                hairType, hairTexture, scalp, allergies, lastVisit);

        this.id = id;
    }


    // ---------------------- GETTERS E SETTERS ----------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getHairType() { return hairType; }
    public void setHairType(String hairType) { this.hairType = hairType; }

    public String getHairTexture() { return hairTexture; }
    public void setHairTexture(String hairTexture) { this.hairTexture = hairTexture; }

    public String getScalp() { return scalp; }
    public void setScalp(String scalp) { this.scalp = scalp; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public LocalDateTime getLastVisit() { return lastVisit; }
    public void setLastVisit(LocalDateTime lastVisit) { this.lastVisit = lastVisit; }
}
