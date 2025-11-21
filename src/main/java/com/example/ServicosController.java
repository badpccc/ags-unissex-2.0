package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.util.List;
import java.math.BigDecimal;
import java.time.Duration;

import com.example.backends.classes.Service;
import com.example.backends.database.data.ServicesDAO;

public class ServicosController {

    @FXML private TableView<Service> tabelaServicos;
    @FXML private TableColumn<Service, String> colNome;
    @FXML private TableColumn<Service, String> colCategoria;
    @FXML private TableColumn<Service, BigDecimal> colPreco;
    @FXML private TableColumn<Service, String> colDuracao;
    @FXML private TableColumn<Service, String> colDescricao;
    
    private ObservableList<Service> listaServicos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        System.out.println("Tela SERVIÇOS carregada");
        configurarTabela();
        carregarServicos();
    }
    
    private void configurarTabela() {
        // Configurar as colunas da tabela
        colNome.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPreco.setCellValueFactory(new PropertyValueFactory<>("price"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Configurar coluna de duração com formatação customizada
        colDuracao.setCellValueFactory(cellData -> {
            Duration duration = cellData.getValue().getDuration();
            if (duration != null) {
                long minutos = duration.toMinutes();
                return new javafx.beans.property.SimpleStringProperty(minutos + " min");
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        // Configurar formatação do preço
        colPreco.setCellFactory(column -> {
            return new TableCell<Service, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(String.format("R$ %.2f", price));
                    }
                }
            };
        });
        
        // Associar a lista à tabela
        tabelaServicos.setItems(listaServicos);
    }

    @FXML
    private void novoServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modal_novoservico.fxml"));
            Parent root = loader.load();

            ModalNovoServicoController controller = loader.getController();

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

    @FXML
    public void editarServico() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("editarservico.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Editar Serviço");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {  // <-- trocado para Exception (igual novoServico)
            e.printStackTrace();
        }
    }

    private void carregarServicos() {
        try {
            System.out.println("Carregando serviços do banco...");
            List<Service> services = ServicesDAO.getAllServices();
            
            listaServicos.clear();
            listaServicos.addAll(services);
            
            System.out.println("✅ " + services.size() + " serviços carregados");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar serviços: " + e.getMessage());
            e.printStackTrace();
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Erro ao carregar serviços");
            alert.setContentText("Não foi possível carregar a lista de serviços do banco de dados.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void excluirServico() {
        Service servicoSelecionado = tabelaServicos.getSelectionModel().getSelectedItem();
        
        if (servicoSelecionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Selecione um serviço para excluir.");
            alert.showAndWait();
            return;
        }
        
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir serviço");
        confirmacao.setContentText("Tem certeza que deseja excluir o serviço '" + servicoSelecionado.getName() + "'?");
        
        if (confirmacao.showAndWait().get() == ButtonType.OK) {
            try {
                boolean sucesso = ServicesDAO.delete(servicoSelecionado.getId());
                
                if (sucesso) {
                    carregarServicos(); // Recarrega a lista
                    
                    Alert sucesso_alert = new Alert(Alert.AlertType.INFORMATION);
                    sucesso_alert.setTitle("Sucesso");
                    sucesso_alert.setHeaderText(null);
                    sucesso_alert.setContentText("Serviço excluído com sucesso!");
                    sucesso_alert.showAndWait();
                } else {
                    Alert erro = new Alert(Alert.AlertType.ERROR);
                    erro.setTitle("Erro");
                    erro.setHeaderText(null);
                    erro.setContentText("Erro ao excluir serviço.");
                    erro.showAndWait();
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao excluir serviço: " + e.getMessage());
                e.printStackTrace();
                
                Alert erro = new Alert(Alert.AlertType.ERROR);
                erro.setTitle("Erro");
                erro.setHeaderText("Erro ao excluir serviço");
                erro.setContentText("Erro inesperado: " + e.getMessage());
                erro.showAndWait();
            }
        }
    }
}
