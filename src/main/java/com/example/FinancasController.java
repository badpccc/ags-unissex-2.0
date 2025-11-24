package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

import com.example.backends.classes.Appointment;
import com.example.backends.classes.Client;
import com.example.backends.database.data.AppointmentDAO;
import com.example.backends.database.data.ClientDAO;
import com.example.backends.enums.AppointmentStatus;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FinancasController {

    @FXML private Label lblSaldo;
    @FXML private Label lblReceitas;
    @FXML private Label lblDespesas;
    @FXML private VBox listaTimeline;
    
    private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        carregarFinancas();
    }

    private void carregarFinancas() {
        new Thread(() -> {
            List<Appointment> agendamentos = AppointmentDAO.getAllAppointments();
            
            BigDecimal totalReceitas = BigDecimal.ZERO;
            BigDecimal totalDespesas = BigDecimal.ZERO;
            
            // Calcular receitas (agendamentos concluídos)
            if (agendamentos != null) {
                for (Appointment ag : agendamentos) {
                    if (ag.getStatus() == AppointmentStatus.CONCLUIDO) {
                        totalReceitas = totalReceitas.add(ag.getTotalPrice());
                    }
                }
            }
            
            // Por enquanto, despesas = 0 (pode ser implementado depois)
            // Você pode adicionar uma tabela de despesas no futuro
            
            BigDecimal saldo = totalReceitas.subtract(totalDespesas);
            
            final BigDecimal fReceitas = totalReceitas;
            final BigDecimal fDespesas = totalDespesas;
            final BigDecimal fSaldo = saldo;
            
            Platform.runLater(() -> {
                lblSaldo.setText(String.format("R$ %.2f", fSaldo));
                lblReceitas.setText(String.format("R$ %.2f", fReceitas));
                lblDespesas.setText(String.format("R$ %.2f", fDespesas));
                
                carregarMovimentacoes(agendamentos);
            });
        }).start();
    }
    
    private void carregarMovimentacoes(List<Appointment> agendamentos) {
        listaTimeline.getChildren().clear();
        
        if (agendamentos == null || agendamentos.isEmpty()) {
            Label vazio = new Label("❌ Nenhuma movimentação encontrada.");
            vazio.setStyle("-fx-font-size: 1.3em; -fx-text-fill: #999;");
            listaTimeline.getChildren().add(vazio);
            return;
        }
        
        // Filtrar apenas agendamentos concluídos
        for (Appointment ag : agendamentos) {
            if (ag.getStatus() == AppointmentStatus.CONCLUIDO) {
                listaTimeline.getChildren().add(criarCardMovimentacao(ag));
            }
        }
        
        if (listaTimeline.getChildren().isEmpty()) {
            Label vazio = new Label("📋 Nenhum agendamento concluído ainda.");
            vazio.setStyle("-fx-font-size: 1.3em; -fx-text-fill: #999;");
            listaTimeline.getChildren().add(vazio);
        }
    }
    
    private HBox criarCardMovimentacao(Appointment ag) {
        HBox card = new HBox(20);
        card.getStyleClass().add("mov-item");
        card.setPadding(new Insets(20));
        
        // Ícone
        Label icone = new Label("💵");
        icone.setStyle("-fx-font-size: 2.5em;");
        
        // Informações
        VBox info = new VBox(6);
        info.getStyleClass().add("mov-info");
        
        // Buscar nome do cliente
        Client cliente = ClientDAO.getClientByID(ag.getClientId());
        String nomeCliente = cliente != null ? cliente.getName() : "Cliente desconhecido";
        
        Label descricao = new Label("👤 " + nomeCliente);
        descricao.getStyleClass().add("mov-desc");
        
        Label data = new Label("🗓️ " + ag.getAppointmentDateTime().format(formatoData));
        data.getStyleClass().add("mov-legenda");
        
        Label status = new Label("✅ " + ag.getStatus().getDisplayName());
        status.getStyleClass().add("mov-legenda");
        
        info.getChildren().addAll(descricao, data, status);
        
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        
        // Valor
        Label valor = new Label(String.format("+ R$ %.2f", ag.getTotalPrice()));
        valor.getStyleClass().add("mov-valor-green");
        
        card.getChildren().addAll(icone, info, espaco, valor);
        
        return card;
    }

    @FXML
    private void novaMovimentacao() {
        System.out.println("Nova movimentação clicada!");
        // Aqui você pode abrir um modal ou outra tela depois
    }
}
