package com.example;

import javafx.fxml.FXML;

public class PaginaNeutraController {

    private SecondaryController mainController;

    public void setMainController(SecondaryController controller) {
        this.mainController = controller;
    }

    @FXML
    private void voltar() {
        if (mainController != null) {
            mainController.carregarTela("dashboard");
        }
    }
}
