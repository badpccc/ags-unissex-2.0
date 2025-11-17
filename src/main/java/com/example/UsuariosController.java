package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class UsuariosController {

    @FXML private VBox listaUsuarios;

    @FXML
    public void initialize() {
        addUsuario("João Silva", "Administrador");
        addUsuario("Carlos Mendes", "Barbeiro");
        addUsuario("Marcos Lima", "Atendente");
    }

    private void addUsuario(String nome, String funcao) {
        listaUsuarios.getChildren().add(criarCardUsuario(nome, funcao));
    }

    private HBox criarCardUsuario(String nome, String funcao) {

        HBox card = new HBox(20);
        card.getStyleClass().add("usuario-item");
        card.setAlignment(Pos.CENTER_LEFT);

        // **NOME + FUNÇÃO**
        VBox vboxInfo = new VBox(4);
        vboxInfo.setFillWidth(true);
        vboxInfo.getStyleClass().add("info-box");

        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("usuario-nome");

        Label lblFuncao = new Label(funcao);
        lblFuncao.getStyleClass().add("usuario-funcao");

        vboxInfo.getChildren().addAll(lblNome, lblFuncao);
        HBox.setHgrow(vboxInfo, Priority.ALWAYS);

        // **BOTÕES**
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");

        HBox boxBotoes = new HBox(10, editar, excluir);
        boxBotoes.setAlignment(Pos.CENTER_RIGHT);

        // **Agora só adicionamos info + botões (sem foto)**
        card.getChildren().addAll(vboxInfo, boxBotoes);

        return card;
    }

    @FXML
    public void novoUsuario() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ModalNovoUsuario.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Novo Usuário");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
