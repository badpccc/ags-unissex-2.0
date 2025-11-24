package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class SecondaryController {

    @FXML private HBox menuClientes;
    @FXML private HBox menuAgendamentos;
    @FXML private HBox menuServicos;
    @FXML private HBox menuFinancas;
    @FXML private HBox menuLogout;
    @FXML private HBox menuUsuarios;
    @FXML private HBox menuPaginaNeutra;

    @FXML private StackPane conteudoArea;

    @FXML
    private void initialize() {
        configurarMenuPorPermissoes();
        carregarTela("dashboard"); // Tela inicial
    }
    
    /**
     * Oculta menus baseado no tipo de usuário
     * Funcionários não podem acessar Finanças e Usuários
     */
    private void configurarMenuPorPermissoes() {
        UserSession session = UserSession.getInstance();
        
        if (session.isEmployee()) {
            // Ocultar menu de Finanças
            if (menuFinancas != null) {
                menuFinancas.setVisible(false);
                menuFinancas.setManaged(false);
            }
            
            // Ocultar menu de Usuários
            if (menuUsuarios != null) {
                menuUsuarios.setVisible(false);
                menuUsuarios.setManaged(false);
            }
        }
    }

    @FXML
    private void showDashboard(MouseEvent e) {
        carregarTela("dashboard");
    }

    @FXML
    private void showClientes(MouseEvent e) {
        carregarTela("clientes");
    }

    @FXML
    private void showAgendamentos(MouseEvent e) {
        carregarTela("agendamentos");
    }

    @FXML
    private void showServicos(MouseEvent e) {
        carregarTela("servicos");
    }

    @FXML
    private void showFinancas(MouseEvent e) {
        carregarTela("financas");
    }

    @FXML
    private void showUsuarios(MouseEvent e) {
        carregarTela("usuarios");
    }

    @FXML
    private void showPaginaNeutra(MouseEvent e) {
        carregarTela("pagina_neutra");
    }

    @FXML
    private void handleLogout(MouseEvent e) throws IOException {
        // Encerrar sessão do usuário
        UserSession.getInstance().logout();
        App.setRoot("primary");
    }


    // -------------------------------------------------------
    // MÉTODO PRINCIPAL PARA CARREGAR QUALQUER FXML
    // -------------------------------------------------------
    public void carregarTela(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/" + fxml + ".fxml")
            );

            Parent root = loader.load();

            // Se a página for a neutra, ela recebe referência do controller principal
            Object controller = loader.getController();
            if (controller instanceof PaginaNeutraController pn) {
                pn.setMainController(this);
            }

            conteudoArea.getChildren().setAll(root);

        } catch (IOException e) {
            System.out.println("Erro ao carregar: " + fxml + ".fxml");
            e.printStackTrace();
        }
    }
}
