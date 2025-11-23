package com.example;

import com.example.backends.classes.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.Duration;

public class ModalNovoServicoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextField txtDuracao;   // minutos
    @FXML private TextArea txtDescricao;

    private Service serviceCriado;

    public Service getServiceCriado() {
        return serviceCriado;
    }

    @FXML
    public void salvarServico() {
        String nome = txtNome.getText().trim();
        String precoStr = txtPreco.getText().trim();
        String duracaoStr = txtDuracao.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty() || precoStr.isEmpty() || duracaoStr.isEmpty()) {
            mostrarAlerta("Campos obrigatórios", "Preencha todos os campos!");
            return;
        }

        BigDecimal preco;
        try {
            preco = new BigDecimal(precoStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Preço inválido", "Digite um número válido para o preço.");
            return;
        }

        long duracaoMin;
        try {
            duracaoMin = Long.parseLong(duracaoStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Duração inválida", "Digite apenas números inteiros para a duração.");
            return;
        }

        Duration duracao = Duration.ofMinutes(duracaoMin);

        serviceCriado = new Service();
        serviceCriado.setName(nome);
        serviceCriado.setPrice(preco);
        serviceCriado.setDuration(duracao);
        serviceCriado.setDescription(descricao);
        serviceCriado.setActive(true);

        fecharModal();
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    public void preencherFormulario(Service service) {
        txtNome.setText(service.getName());
        txtPreco.setText(service.getPrice().toString());
        txtDuracao.setText(String.valueOf(service.getDuration().toMinutes()));
        txtDescricao.setText(service.getDescription());
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
