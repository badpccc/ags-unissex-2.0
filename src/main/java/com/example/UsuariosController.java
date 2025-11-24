package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.example.backends.classes.Adm;
import com.example.backends.classes.Employee;
import com.example.backends.database.data.AdmDAO;
import com.example.backends.database.data.EmployeeDAO;

import java.util.List;

public class UsuariosController {

    @FXML private VBox listaUsuarios;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblAdmins;
    @FXML private Label lblFuncionarios;
    @FXML private ToggleButton btnMostrarAdmins;
    @FXML private ToggleButton btnMostrarFuncionarios;
    
    private ToggleGroup toggleGroup;
    private List<Adm> administradores;
    private List<Employee> funcionarios;
    private boolean mostrandoAdministradores = false;

    @FXML
    public void initialize() {
        // Configurar Toggle Group
        toggleGroup = new ToggleGroup();
        btnMostrarAdmins.setToggleGroup(toggleGroup);
        btnMostrarFuncionarios.setToggleGroup(toggleGroup);
        
        // Selecionar "Todos" por padrão
        btnMostrarFuncionarios.setSelected(true);
        
        // Listeners para os botões
        btnMostrarAdmins.setOnAction(e -> {
            mostrandoAdministradores = true;
            carregarAdministradores();
        });
        btnMostrarFuncionarios.setOnAction(e -> {
            mostrandoAdministradores = false;
            carregarFuncionarios();
        });
        
        // Carregar dados
        carregarDados();
    }
    
    private void carregarDados() {
        // Buscar do banco
        administradores = AdmDAO.getAll();
        funcionarios = EmployeeDAO.getAll();
        
        // Atualizar cards
        int totalAdmins = administradores != null ? administradores.size() : 0;
        int totalFuncionarios = funcionarios != null ? funcionarios.size() : 0;
        
        lblTotalUsuarios.setText(String.valueOf(totalAdmins + totalFuncionarios));
        lblAdmins.setText(String.valueOf(totalAdmins));
        lblFuncionarios.setText(String.valueOf(totalFuncionarios));
        
        // Manter o filtro atual
        if (mostrandoAdministradores) {
            carregarAdministradores();
        } else {
            carregarFuncionarios();
        }
    }
    
    private void carregarAdministradores() {
        listaUsuarios.getChildren().clear();
        
        if (administradores == null || administradores.isEmpty()) {
            Label lblVazio = new Label("Nenhum administrador cadastrado");
            lblVazio.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
            listaUsuarios.getChildren().add(lblVazio);
            return;
        }
        
        for (Adm adm : administradores) {
            listaUsuarios.getChildren().add(criarCardAdministrador(adm));
        }
    }
    
    private void carregarFuncionarios() {
        listaUsuarios.getChildren().clear();
        
        if (funcionarios == null || funcionarios.isEmpty()) {
            Label lblVazio = new Label("Nenhum funcionário cadastrado");
            lblVazio.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
            listaUsuarios.getChildren().add(lblVazio);
            return;
        }
        
        for (Employee emp : funcionarios) {
            listaUsuarios.getChildren().add(criarCardFuncionario(emp));
        }
    }
    
    private HBox criarCardAdministrador(Adm adm) {
        HBox card = new HBox(20);
        card.getStyleClass().add("usuario-item");
        card.setAlignment(Pos.CENTER_LEFT);

        // NOME + FUNÇÃO
        VBox vboxInfo = new VBox(4);
        vboxInfo.setFillWidth(true);
        vboxInfo.getStyleClass().add("info-box");

        Label lblNome = new Label(adm.getFullName());
        lblNome.getStyleClass().add("usuario-nome");

        Label lblFuncao = new Label("👤 Administrador - @" + adm.getUsername());
        lblFuncao.getStyleClass().add("usuario-funcao");
        
        Label lblEmail = new Label("📧 " + (adm.getEmail() != null ? adm.getEmail() : "Sem email"));
        lblEmail.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        vboxInfo.getChildren().addAll(lblNome, lblFuncao, lblEmail);
        HBox.setHgrow(vboxInfo, Priority.ALWAYS);
        
        // STATUS
        Label lblStatus = new Label(adm.isActive() ? "✅ ATIVO" : "❌ INATIVO");
        lblStatus.setStyle(adm.isActive() 
            ? "-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 14px;"
            : "-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 14px;");

        // BOTÕES
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");
        editar.setOnAction(e -> editarAdministrador(adm));

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");
        excluir.setOnAction(e -> {
            if (confirmarExclusao("administrador " + adm.getFullName())) {
                AdmDAO.delete(adm.getId());
                carregarDados();
            }
        });

        HBox boxBotoes = new HBox(10, lblStatus, editar, excluir);
        boxBotoes.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(vboxInfo, boxBotoes);
        return card;
    }
    
    private HBox criarCardFuncionario(Employee emp) {
        HBox card = new HBox(20);
        card.getStyleClass().add("usuario-item");
        card.setAlignment(Pos.CENTER_LEFT);

        // NOME + FUNÇÃO
        VBox vboxInfo = new VBox(4);
        vboxInfo.setFillWidth(true);
        vboxInfo.getStyleClass().add("info-box");

        Label lblNome = new Label(emp.getName());
        lblNome.getStyleClass().add("usuario-nome");

        String cargo = emp.getPosition() != null ? emp.getPosition() : "Funcionário";
        Label lblFuncao = new Label("💼 " + cargo);
        lblFuncao.getStyleClass().add("usuario-funcao");
        
        String especialidades = emp.getSpecialties() != null && !emp.getSpecialties().isEmpty() 
            ? emp.getSpecialties() 
            : "Nenhuma especialidade";
        Label lblEspecialidades = new Label("⭐ " + especialidades);
        lblEspecialidades.setStyle("-fx-font-size: 12px; -fx-text-fill: #888;");

        vboxInfo.getChildren().addAll(lblNome, lblFuncao, lblEspecialidades);
        HBox.setHgrow(vboxInfo, Priority.ALWAYS);
        
        // STATUS
        Label lblStatus = new Label(emp.isActive() ? "✅ ATIVO" : "❌ INATIVO");
        lblStatus.setStyle(emp.isActive() 
            ? "-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 14px;"
            : "-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 14px;");

        // BOTÕES
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-editar");
        editar.setOnAction(e -> editarFuncionario(emp));

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-excluir");
        excluir.setOnAction(e -> {
            if (confirmarExclusao("funcionário " + emp.getName())) {
                EmployeeDAO.delete(emp.getId());
                carregarDados();
            }
        });

        HBox boxBotoes = new HBox(10, lblStatus, editar, excluir);
        boxBotoes.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(vboxInfo, boxBotoes);
        return card;
    }
    
    private boolean confirmarExclusao(String nome) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
            javafx.scene.control.Alert.AlertType.CONFIRMATION
        );
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Deseja realmente excluir " + nome + "?");
        alert.setContentText("Esta ação não pode ser desfeita.");
        
        return alert.showAndWait()
            .filter(response -> response == javafx.scene.control.ButtonType.OK)
            .isPresent();
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
            
            // Recarregar dados após fechar o modal
            carregarDados();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void editarAdministrador(Adm adm) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarUsuario.fxml"));
            Parent root = loader.load();
            
            EditarUsuarioController controller = loader.getController();
            controller.carregarAdministrador(adm);
            controller.setCallback(this::carregarDados);

            Stage stage = new Stage();
            stage.setTitle("Editar Administrador");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void editarFuncionario(Employee emp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarUsuario.fxml"));
            Parent root = loader.load();
            
            EditarUsuarioController controller = loader.getController();
            controller.carregarFuncionario(emp);
            controller.setCallback(this::carregarDados);

            Stage stage = new Stage();
            stage.setTitle("Editar Funcionário");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
