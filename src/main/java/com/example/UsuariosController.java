package com.example;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class UsuariosController {

    @FXML private VBox listaUsuarios;

    @FXML
    public void initialize() {
        addUsuario("João Silva", "Administrador", "https://i.pravatar.cc/150?img=1");
        addUsuario("Carlos Mendes", "Barbeiro", "https://i.pravatar.cc/150?img=2");
        addUsuario("Marcos Lima", "Atendente", "https://i.pravatar.cc/150?img=3");
    }

    private void addUsuario(String nome, String funcao, String fotoUrl) {
        listaUsuarios.getChildren().add(criarCardUsuario(nome, funcao, fotoUrl));
    }

    private HBox criarCardUsuario(String nome, String funcao, String fotoUrl) {

        HBox card = new HBox(20);
        card.getStyleClass().add("usuario-item");

        // FOTO
        ImageView foto = new ImageView(new Image(fotoUrl));
        foto.setFitWidth(55);
        foto.setFitHeight(55);
        foto.getStyleClass().add("usuario-foto");

        // NOME + FUNÇÃO
        VBox vboxInfo = new VBox(4);
        vboxInfo.setFillWidth(true);
        vboxInfo.getStyleClass().add("info-box");

        Label lblNome = new Label(nome);
        lblNome.getStyleClass().add("usuario-nome");

        Label lblFuncao = new Label(funcao);
        lblFuncao.getStyleClass().add("usuario-funcao");

        vboxInfo.getChildren().addAll(lblNome, lblFuncao);
        HBox.setHgrow(vboxInfo, Priority.ALWAYS);

        // BOTÕES
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");

        HBox boxBotoes = new HBox(10, editar, excluir);
        boxBotoes.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(foto, vboxInfo, boxBotoes);

        return card;
    }

    @FXML
    private void novoUsuario() {
        System.out.println("Novo usuário clicado!");
    }
}
