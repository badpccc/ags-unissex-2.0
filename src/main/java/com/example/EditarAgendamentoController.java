package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

public class EditarAgendamentoController {

    @FXML private TextField txtCliente;
    @FXML private TextField txtServico;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;
    @FXML private TextField txtPreco;

    private AgendamentosController.AgendamentoTemp agendamentoOriginal;
    private Consumer<AgendamentosController.AgendamentoTemp> callback;

    // Carregar os dados do agendamento no formulário
    public void carregarAgendamento(
            AgendamentosController.AgendamentoTemp ag,
            Consumer<AgendamentosController.AgendamentoTemp> callback
    ) {
        this.agendamentoOriginal = ag;
        this.callback = callback;

        txtCliente.setText(ag.cliente);
        txtServico.setText(ag.servico);
        dpData.setValue(ag.data);
        txtHora.setText(ag.hora.toString());
        txtPreco.setText(String.valueOf(ag.preco));
    }

    @FXML
    private void salvar() {
        try {
            AgendamentosController.AgendamentoTemp atualizado =
                    new AgendamentosController.AgendamentoTemp(
                            txtCliente.getText(),
                            txtServico.getText(),
                            dpData.getValue(),
                            LocalTime.parse(txtHora.getText()),
                            Double.parseDouble(txtPreco.getText())
                    );

            // Retorna o novo agendamento para o controller principal
            if (callback != null) {
                callback.accept(atualizado);
            }

            fechar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar", "Verifique os campos e tente novamente.");
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
        stage.close();
    }

    private void mostrarErro(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
