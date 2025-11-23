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

    // Callback temporário para retornar dados ao controller principal
    private Consumer<AgendamentosController> callback;

    /**
     * Define o callback que será chamado quando salvar
     */
    public void setCallback(Consumer<AgendamentosController> callback) {
        this.callback = callback;
    }

    /**
     * Fecha o modal
     */
    @FXML
    private void fecharModal() {
        Stage stage = (Stage) txtCliente.getScene().getWindow();
        stage.close();
    }

    /**
     * Método de placeholder para salvar agendamento
     */
    @FXML
    private void salvarAgendamento() {
        // Aqui você pode pegar os dados dos campos
        String cliente = txtCliente.getText();
        String servico = cmbServico.getValue();
        LocalDate data = dpData.getValue();
        String horaStr = txtHora.getText();
        String precoStr = txtPreco.getText();

        if (cliente.isEmpty() || servico == null || data == null || horaStr.isEmpty() || precoStr.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING, "Preencha todos os campos!", ButtonType.OK);
            a.show();
            return;
        }

        LocalTime hora = LocalTime.parse(horaStr);
        String preco = precoStr;

        System.out.println("Agendamento salvo (temporário):");
        System.out.println("Cliente: " + cliente);
        System.out.println("Serviço: " + servico);
        System.out.println("Data: " + data);
        System.out.println("Hora: " + hora);
        System.out.println("Preço: " + preco);

        // Apenas fecha o modal por enquanto
        fecharModal();
    }

    /**
     * Preenche os campos do modal (opcional)
     */
    public void preencherCampos(String cliente, String servico, LocalDate data, LocalTime hora, String preco) {
        txtCliente.setText(cliente);
        cmbServico.setValue(servico);
        dpData.setValue(data);
        txtHora.setText(hora != null ? hora.toString() : "");
        txtPreco.setText(preco);
    }

    /**
     * Configurar para edição (placeholder)
     */
    public void configurarParaEdicao(Object appointment) {
        // Aqui você pode carregar dados no modal se quiser
        System.out.println("Editar agendamento (temporário)");
    }
}
