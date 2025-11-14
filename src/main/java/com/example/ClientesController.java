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

        // Carrega todos os clientes do banco
        ClienteDAO.listar().forEach(this::adicionarCliente);

        btnAdicionarCliente.setOnAction(e -> abrirModalNovoCliente());
    }

    // ------------ MODAL NOVO CLIENTE ----------------
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

    // ------------ ADICIONAR ITEM NA LISTA --------------
    public void adicionarCliente(Cliente c) {
        listaClientes.getChildren().add(criarItemCliente(c));
    }

    // ------------ CRIA O CARD COMPLETO DO CLIENTE --------
    public HBox criarItemCliente(Cliente c) {

        HBox linha = new HBox();
        linha.getStyleClass().add("cliente-card");
        linha.setSpacing(40);

        // ----------- COLUNA 1 (DADOS PESSOAIS) -----------------
        VBox col1 = new VBox(
                criarLabel("Nome: ", c.getNome()),
                criarLabel("Telefone: ", c.getTelefone()),
                criarLabel("Email: ", c.getEmail()),
                criarLabel("Endereço: ", c.getAddress()),
                criarLabel("Ativo: ", c.isActive() ? "Sim" : "Não"),
                criarLabel("Cadastro: ", c.getRegistrationDate() != null ? c.getRegistrationDate().toString() : "—")
        );
        col1.setSpacing(6);

        // ----------- COLUNA 2 (DADOS CAPILARES + VISITA) ---------
        VBox col2 = new VBox(
                criarLabel("Tipo de cabelo: ", c.getHairType()),
                criarLabel("Textura: ", c.getHairTexture()),
                criarLabel("Couro cabeludo: ", c.getScalp()),
                criarLabel("Alergias: ", getOr(c.getAllergies(), "Nenhuma")),
                criarLabel("Profissional preferido: ", c.getPreferredStylist()),
                criarLabel("Última visita: ",
                        c.getLastVisit() != null ? c.getLastVisit().toLocalDate().toString() : "—"),
                criarLabel("Observações: ", getOr(c.getNotes(), "—"))
        );
        col2.setSpacing(6);

        // --------------------- BOTÕES ----------------------------
        Button editar = new Button("Editar");
        editar.getStyleClass().add("btn-edit");
        editar.setOnAction(e -> abrirEditarCliente(c));

        Button excluir = new Button("Excluir");
        excluir.getStyleClass().add("btn-delete");
        excluir.setOnAction(e -> {
            ClienteDAO.excluirPorId(c.getId());
            listaClientes.getChildren().remove(linha);
        });

        VBox boxAcoes = new VBox(10, editar, excluir);
        boxAcoes.setStyle("-fx-alignment: center-right;");

        // Adiciona tudo ao card
        linha.getChildren().addAll(col1, col2, boxAcoes);

        return linha;
    }

    // ------------ CRIA LABEL FORMATADA ---------------
    private Label criarLabel(String titulo, String conteudo) {
        Label lbl = new Label(titulo + (conteudo != null ? conteudo : "—"));
        lbl.getStyleClass().add("cliente-info");
        return lbl;
    }

    private String getOr(String valor, String padrao) {
        return (valor != null && !valor.isEmpty()) ? valor : padrao;
    }

    // ------------ MODAL EDITAR CLIENTE -----------------
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

            atualizarLista();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------ ATUALIZAR LISTA COMPLETA -------------
    private void atualizarLista() {
        listaClientes.getChildren().clear();
        ClienteDAO.listar().forEach(this::adicionarCliente);
    }
}
