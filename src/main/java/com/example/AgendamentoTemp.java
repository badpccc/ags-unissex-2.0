package com.example;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendamentoTemp {
    public String cliente;
    public String servico;
    public LocalDate data;
    public LocalTime hora;
    public double preco;

    public AgendamentoTemp(String cliente, String servico, LocalDate data, LocalTime hora, double preco) {
        this.cliente = cliente;
        this.servico = servico;
        this.data = data;
        this.hora = hora;
        this.preco = preco;
    }
}
