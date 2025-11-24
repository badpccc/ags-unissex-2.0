package com.example.backends.classes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

    // LEMBRAR DE ADICIONAR CADA UM DESSES ATRIBUTOS NA TELA DE REGISTRAR NOVO FUNCIONÁRIO! 
public class Employee {
    private Long id;
    private String name;
    private String username;
    private String passwordHash;
    private String email;
    private String phoneNumber;
    private String cpf;
    private LocalDate hireDate;
    private boolean isActive;
    private String notes;
    
    // Atributos específicos para cabeleireiros
    private String specialties; // Especialidades (corte, coloração, tratamentos, etc.)
    private String experienceLevel; // Iniciante, Intermediário, Avançado, Especialista
    private BigDecimal baseSalary; // Salário base
    private BigDecimal commissionRate; // Percentual de comissão (ex: 0.30 = 30%)
    private String workingHours; // Horário de trabalho (ex: "08:00-18:00")
    private List<String> workingDays; // Dias da semana que trabalha
    private String position; // Cargo (Cabeleireiro, Cabeleireiro Sênior, Supervisor, etc.)
    private LocalDateTime lastTrainingDate; // Última capacitação/curso
    private String certificates; // Certificações e cursos
    private boolean canPerformChemicalTreatments; // Pode fazer química (coloração, alisamento)
    private String preferredClientType; // Masculino, Feminino, Infantil, Todos
    
    public Employee() {
        this.hireDate = LocalDate.now();
        this.isActive = true;
        this.canPerformChemicalTreatments = false;
        this.experienceLevel = "Iniciante";
        this.commissionRate = new BigDecimal("0.30"); 
    }
    
    public Employee(String name, String phoneNumber, String cpf) {
        this();
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.cpf = cpf;
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
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
    
    public String getSpecialties() {
        return specialties;
    }
    
    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }
    
    public String getExperienceLevel() {
        return experienceLevel;
    }
    
    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }
    
    public BigDecimal getBaseSalary() {
        return baseSalary;
    }
    
    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }
    
    public BigDecimal getCommissionRate() {
        return commissionRate;
    }
    
    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }
    
    public String getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
    
    public List<String> getWorkingDays() {
        return workingDays;
    }
    
    public void setWorkingDays(List<String> workingDays) {
        this.workingDays = workingDays;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public LocalDateTime getLastTrainingDate() {
        return lastTrainingDate;
    }
    
    public void setLastTrainingDate(LocalDateTime lastTrainingDate) {
        this.lastTrainingDate = lastTrainingDate;
    }
    
    public String getCertificates() {
        return certificates;
    }
    
    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }
    
    public boolean isCanPerformChemicalTreatments() {
        return canPerformChemicalTreatments;
    }
    
    public void setCanPerformChemicalTreatments(boolean canPerformChemicalTreatments) {
        this.canPerformChemicalTreatments = canPerformChemicalTreatments;
    }
    
    public String getPreferredClientType() {
        return preferredClientType;
    }
    
    public void setPreferredClientType(String preferredClientType) {
        this.preferredClientType = preferredClientType;
    }
    
    public boolean isExperienced() {
        return "Avançado".equals(experienceLevel) || "Especialista".equals(experienceLevel);
    }
    
    public boolean canWork(String dayOfWeek) {
        return workingDays != null && workingDays.contains(dayOfWeek);
    }
    
    public BigDecimal calculateCommission(BigDecimal serviceValue) {
        if (serviceValue == null || commissionRate == null) {
            return BigDecimal.ZERO;
        }
        return serviceValue.multiply(commissionRate);
    }
    
    public boolean needsTraining() {
        if (lastTrainingDate == null) return true;
        return lastTrainingDate.isBefore(LocalDateTime.now().minusMonths(6));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee employee = (Employee) obj;
        return id != null && id.equals(employee.id);
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