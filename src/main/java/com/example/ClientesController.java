package com.example;

import com.example.dao.ClienteDAO;
import com.example.models.Cliente;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ClientesController {

    @FXML
    private VBox listaClientes;

    @FXML
    private Button btnAdicionarCliente;

    @FXML
    public void initialize() {

        // Carrega do banco
        ClienteDAO.listar().forEach(this::adicionarCliente);

        btnAdicionarCliente.setOnAction(e -> abrirModalNovoCliente());
    }

    // Modal para adicionar
    private void abrirModalNovoCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("novo_cliente.fxml"));
            Parent root = loader.load();

            NovoClienteController controller = loader.getController();

            Stage modal = new Stage();
            modal.setScene(new Scene(root));
            modal.setTitle("Novo Cliente");
            modal.initOwner(btnAdicionarCliente.getScene().getWindow());
            modal.initModality(Modality.APPLICATION_MODAL);

            controller.setOnClienteSalvo(cliente -> {
                ClienteDAO.salvar(cliente);
                adicionarCliente(cliente);
            });

            modal.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Adiciona visualmente usando o objeto Cliente inteiro
    public void adicionarCliente(Cliente c) {
        listaClientes.getChildren().add(criarItemCliente(c));
    }

    // Cria card do cliente
    // Cria card do cliente
    public HBox criarItemCliente(Cliente c) {

        HBox linha = new HBox();
        linha.getStyleClass().add("linha-cliente");
        linha.setSpacing(20);

        Label lblNome = new Label(c.getNome());
        lblNome.getStyleClass().add("cliente-nome");

        Label lblTelefone = new Label(c.getTelefone());
        lblTelefone.getStyleClass().add("cliente-info");

        Label lblEmail = new Label(c.getEmail());
        lblEmail.getStyleClass().add("cliente-info");

        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-edit");
        editar.setOnAction(e -> abrirEditarCliente(c));   // ✅ Agora funciona

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-delete");

        excluir.setOnAction(e -> {
            ClienteDAO.excluirPorId(c.getId());
            listaClientes.getChildren().remove(linha);
        });

        HBox acoes = new HBox(10, editar, excluir);

        linha.getChildren().addAll(lblNome, lblTelefone, lblEmail, acoes);

        return linha;
    }
    private void atualizarLista() {
        listaClientes.getChildren().clear();
        ClienteDAO.listar().forEach(this::adicionarCliente);
    }

    @FXML
    private void abrirEditarCliente(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditarCliente.fxml"));
            Parent root = loader.load();

            EditarClienteController controller = loader.getController();
            controller.setCliente(cliente);

            Stage stage = new Stage();
            stage.setTitle("Editar Cliente");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            atualizarLista(); // recarrega os clientes na tela

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
