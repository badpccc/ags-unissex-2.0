package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ModalNovoServicoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextField txtDuracao;
    @FXML private TextArea txtDescricao;

    private Runnable callback;

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @FXML
    public void salvarServico() {

        // 📌 Aqui futuramente vai salvar no banco
        System.out.println("Serviço criado: " + txtNome.getText());

        if (callback != null) callback.run();

        fecharModal();
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
}
