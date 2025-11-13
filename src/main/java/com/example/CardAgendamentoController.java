package com.example;

import com.example.dao.AgendamentoDAO;
import com.example.models.Agendamento;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;

public class CardAgendamentoController {

    @FXML private TextField txtClienteNome;
    @FXML private TextField txtServico;
    @FXML private DatePicker dateAgendamento;
    @FXML private TextField txtHora;
    @FXML private TextArea txtObservacoes;
    @FXML private ComboBox<String> cbStatus;

    @FXML
    private AnchorPane cardAgendamento;

    @FXML
    private void salvarAgendamento() {
        try {
            String nome = txtClienteNome.getText().trim();
            String servico = txtServico.getText().trim();
            LocalDate data = dateAgendamento.getValue();
            String horaTexto = txtHora.getText().trim();
            String status = cbStatus.getValue() != null ? cbStatus.getValue() : "Pendente";
            String observacoes = txtObservacoes.getText().trim();

            if (nome.isEmpty() || servico.isEmpty() || data == null || horaTexto.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Preencha todos os campos obrigatórios!");
                return;
            }

            // Converte hora com segurança
            LocalTime hora;
            try {
                hora = LocalTime.parse(horaTexto);
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.WARNING, "Formato de hora inválido! Use HH:mm (ex: 14:30)");
                return;
            }

            // ✅ Cria o agendamento
            Agendamento ag = new Agendamento(
                    nome,
                    servico,
                    data,
                    hora,
                    status,
                    observacoes
            );

            // ✅ Salva no banco
            AgendamentoDAO.salvar(ag);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Agendamento salvo com sucesso!");
            limparCampos();

            // ✅ Fecha o modal (card)
            fecharCard();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao salvar o agendamento: " + e.getMessage());
        }
    }

    private void limparCampos() {
        txtClienteNome.clear();
        txtServico.clear();
        dateAgendamento.setValue(null);
        txtHora.clear();
        txtObservacoes.clear();
        cbStatus.setValue(null);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Agendamentos");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // 🔹 Fecha o card/modal
    @FXML
    private void fecharCard() {
        // Remove o overlay que contém o card
        Node node = cardAgendamento.getParent();
        if (node != null && node.getParent() instanceof AnchorPane root) {
            root.getChildren().remove(node);
        } else {
            // fallback se estiver em uma janela separada
            Stage stage = (Stage) cardAgendamento.getScene().getWindow();
            stage.close();
        }
    }
}
