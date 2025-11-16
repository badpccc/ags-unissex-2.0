package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ServicosController {

    @FXML private TableView<?> tabelaServicos;

    @FXML
    private void initialize() {
        System.out.println("Tela SERVIÇOS carregada");
    }

    @FXML
    private void novoServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            // pega controller do modal
            ModalNovoServicoController controller = loader.getController();

            // callback quando salvar
            controller.setCallback(() -> {
                System.out.println("SERVIÇO SALVO → atualizar tabela");
                carregarServicos();
            });

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Novo Serviço");
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void carregarServicos() {
        // Aqui você vai buscar no banco
        System.out.println("Carregando serviços...");
    }
}
