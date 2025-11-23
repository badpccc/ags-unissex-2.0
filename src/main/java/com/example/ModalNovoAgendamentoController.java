package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

public class ModalNovoAgendamentoController {

    @FXML private TextField txtCliente;
    @FXML private ComboBox<String> cmbServico;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;
    @FXML private TextField txtPreco;

    // 🔹 Callback para enviar dados ao AgendamentosController
    private Consumer<AgendamentosController.AgendamentoTemp> callback;

    // 🔹 Permitir o AgendamentosController registrar a callback
    public void setCallback(Consumer<AgendamentosController.AgendamentoTemp> callback) {
        this.callback = callback;
    }

    @FXML
    private void fecharModal() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
        stage.close();
    }
    public void preencherCampos(String cliente, String servico, LocalDate data, LocalTime hora, double preco) {
        txtCliente.setText(cliente);
        cmbServico.setValue(servico);     // ComboBox recebe o serviço
        dpData.setValue(data);            // DatePicker recebe a data
        txtHora.setText(hora.toString()); // TextField recebe a hora
        txtPreco.setText(String.valueOf(preco));
    }


    @FXML
    private void salvarAgendamento() {

        String cliente = txtCliente.getText();
        String servico = cmbServico.getValue();
        LocalDate data = dpData.getValue();
        String horaStr = txtHora.getText();
        String precoStr = txtPreco.getText();

        if (cliente.isEmpty() || servico == null || data == null || horaStr.isEmpty() || precoStr.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Preencha todos os campos!", ButtonType.OK);
            alert.show();
            return;
        }

        LocalTime hora = LocalTime.parse(horaStr);
        double preco = Double.parseDouble(precoStr);

        // 🔹 Criar o agendamento temporário
        AgendamentosController.AgendamentoTemp novo = new AgendamentosController.AgendamentoTemp(
                cliente, servico, data, hora, preco
        );

        // 🔹 Enviar para o controller principal
        if (callback != null) {
            callback.accept(novo);
        }

        fecharModal();
    }
}
