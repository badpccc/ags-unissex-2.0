package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

import com.example.backends.classes.Appointment;
import com.example.backends.database.data.ClientDAO;
import com.example.backends.database.data.ServicesDAO;

public class EditarAgendamentoController {

    @FXML private TextField txtCliente;
    @FXML private TextField txtServico;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;
    @FXML private TextField txtPreco;

    private Appointment agendamentoOriginal;
    private Consumer<Appointment> callback;

    /**
     * Carrega o agendamento no formulário para edição
     */
    public void carregarAgendamento(Appointment ag, Consumer<Appointment> callback) {
        this.agendamentoOriginal = ag;
        this.callback = callback;

        try {
            // Buscar dados reais pelo ID
            var client = ClientDAO.getClientByID(ag.getClientId());
            txtCliente.setText(client != null ? client.getName() : "");

            List<Long> serviceIds = ag.getServiceIds();
            if (!serviceIds.isEmpty()) {
                var service = ServicesDAO.getServiceByID(serviceIds.get(0));
                txtServico.setText(service != null ? service.getName() : "");
            }

            dpData.setValue(ag.getAppointmentDateTime().toLocalDate());
            txtHora.setText(ag.getAppointmentDateTime().toLocalTime().toString());
            txtPreco.setText(String.valueOf(ag.getTotalPrice() != null ? ag.getTotalPrice() : 0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Salva as alterações no agendamento
     */
    @FXML
    private void salvar() {
        try {
            if (dpData.getValue() == null || txtHora.getText().isEmpty() || txtPreco.getText().isEmpty()) {
                mostrarErro("Erro", "Preencha todos os campos obrigatórios!");
                return;
            }

            // Atualizar data/hora
            agendamentoOriginal.setAppointmentDateTime(
                    dpData.getValue().atTime(LocalTime.parse(txtHora.getText()))
            );

            // Atualizar preço como BigDecimal
            agendamentoOriginal.setTotalPrice(
                    BigDecimal.valueOf(Double.parseDouble(txtPreco.getText()))
            );

            // Para o cliente e serviço, você poderia atualizar os IDs se necessário
            // Exemplo: buscar cliente/serviço pelo nome novamente

            if (callback != null) {
                callback.accept(agendamentoOriginal);
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

    /**
     * Fecha o modal
     */
    private void fechar() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
        stage.close();
    }

    /**
     * Mostra mensagem de erro
     */
    private void mostrarErro(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
