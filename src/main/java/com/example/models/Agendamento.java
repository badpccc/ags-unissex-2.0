package com.example.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Agendamento {
    private int id;
    private String clienteNome;
    private String servico;
    private LocalDate dataAgendamento;
    private LocalTime hora;
    private String status;
    private String observacoes;

    public Agendamento(String nome, String serviçoPadrão, LocalDateTime localDateTime, String pendente, String nenhumaObservação) {}

    public Agendamento(int id, String clienteNome, String servico, LocalDate dataAgendamento,
                       LocalTime hora, String status, String observacoes) {
        this.id = id;
        this.clienteNome = clienteNome;
        this.servico = servico;
        this.dataAgendamento = dataAgendamento;
        this.hora = hora;
        this.status = status;
        this.observacoes = observacoes;
    }

    // Construtor para criação (sem ID)
    public Agendamento(String clienteNome, String servico, LocalDate dataAgendamento,
                       LocalTime hora, String status, String observacoes) {
        this.clienteNome = clienteNome;
        this.servico = servico;
        this.dataAgendamento = dataAgendamento;
        this.hora = hora;
        this.status = status;
        this.observacoes = observacoes;
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public LocalDate getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(LocalDate dataAgendamento) { this.dataAgendamento = dataAgendamento; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
