package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class ServicosController {

    @FXML private TableView<?> tabelaServicos;

    @FXML
    private void initialize() {
        // Aqui você futuramente carrega os serviços
        System.out.println("Tela SERVIÇOS carregada");
    }
}
