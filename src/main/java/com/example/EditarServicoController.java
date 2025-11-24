package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.example.backends.classes.Service;
import com.example.backends.database.data.ServicesDAO;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.function.Consumer;

public class EditarServicoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextField txtDuracao;
    @FXML private TextArea txtDescricao;
    @FXML private TextField txtCategoria;
    @FXML private CheckBox chkAtivo;

    private Service servicoOriginal;
    private Consumer<Service> callback;

    public void setCallback(Consumer<Service> callback) {
        this.callback = callback;
    }

    /**
     * Carrega os dados do serviço para edição
     */
    public void carregarServico(Service servico) {
        this.servicoOriginal = servico;
        
        txtNome.setText(servico.getName());
        txtPreco.setText(servico.getPrice() != null ? servico.getPrice().toString() : "");
        
        if (servico.getDuration() != null) {
            long minutos = servico.getDuration().toMinutes();
            txtDuracao.setText(String.valueOf(minutos));
        }
        
        txtDescricao.setText(servico.getDescription());
        txtCategoria.setText(servico.getCategory());
        chkAtivo.setSelected(servico.isActive());
    }

    @FXML
    private void salvar() {
        // Validações
        if (txtNome.getText().isEmpty()) {
            mostrarAlerta("Digite o nome do serviço!", Alert.AlertType.WARNING);
            return;
        }
        
        if (txtPreco.getText().isEmpty()) {
            mostrarAlerta("Digite o preço do serviço!", Alert.AlertType.WARNING);
            return;
        }
        
        if (txtDuracao.getText().isEmpty()) {
            mostrarAlerta("Digite a duração do serviço!", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            // Atualizar dados do serviço original
            servicoOriginal.setName(txtNome.getText());
            servicoOriginal.setPrice(new BigDecimal(txtPreco.getText()));
            servicoOriginal.setDuration(Duration.ofMinutes(Long.parseLong(txtDuracao.getText())));
            servicoOriginal.setDescription(txtDescricao.getText());
            servicoOriginal.setCategory(txtCategoria.getText());
            servicoOriginal.setActive(chkAtivo.isSelected());
            
            // Atualizar no banco
            boolean sucesso = ServicesDAO.update(servicoOriginal);
            
            if (sucesso) {
                mostrarAlerta("✅ Serviço atualizado com sucesso!", Alert.AlertType.INFORMATION);
                
                if (callback != null) {
                    callback.accept(servicoOriginal);
                }
                
                fechar();
            } else {
                mostrarAlerta("❌ Erro ao atualizar serviço!", Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("❌ Preço ou duração inválidos!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("❌ Erro: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo, mensagem, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
