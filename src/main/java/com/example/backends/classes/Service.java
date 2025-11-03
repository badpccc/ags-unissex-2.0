package com.example.backends.classes;

import java.math.BigDecimal;
import java.time.Duration;

public class Service {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Duration duration; 
    private String category;
    private boolean isActive;
    
    public Service() {
        this.isActive = true;
    }
    
    public Service(String name, BigDecimal price, Duration duration) {
        this();
        this.name = name;
        this.price = price;
        this.duration = duration;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Duration getDuration() {
        return duration;
    }
    
    public void setDuration(Duration duration) {
        this.duration = duration;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    // Método utilitário para formatar duração
    public String getFormattedDuration() {
        if (duration == null) return "N/A";
        
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        
        if (hours > 0) {
            return String.format("%dh %02dmin", hours, minutes);
        } else {
            return String.format("%dmin", minutes);
        }
    }
    
    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", duration=" + getFormattedDuration() +
                ", category='" + category + '\'' +
                '}';
    }
}