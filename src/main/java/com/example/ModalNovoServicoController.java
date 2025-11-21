package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.time.Duration;

import com.example.backends.classes.Service;
import com.example.backends.database.data.ServicesDAO;

public class ModalNovoServicoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextField txtDuracao;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<String> cbCategoria;

    private Runnable callback;

    @FXML
    public void initialize() {
        // Configura as categorias disponíveis
        cbCategoria.getItems().addAll(
            "Corte",
            "Coloração",
            "Tratamento",
            "Penteado",
            "Manicure",
            "Pedicure",
            "Depilação",
            "Sobrancelha",
            "Outros"
        );
        
        // Adiciona máscara de moeda para preço
        txtPreco.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([\\.,]\\d{0,2})?")) {
                txtPreco.setText(oldValue);
            }
        });
        
        // Adiciona máscara para duração (apenas números)
        txtDuracao.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtDuracao.setText(oldValue);
            }
        });
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @FXML
    public void salvarServico() {
        // Validações
        if (!validarCampos()) {
            return;
        }

        try {
            // Criar objeto Service
            Service service = new Service();
            service.setName(txtNome.getText().trim());
            service.setDescription(txtDescricao.getText().trim());
            service.setCategory(cbCategoria.getValue());
            
            // Converter preço
            String precoStr = txtPreco.getText().replace(",", ".");
            service.setPrice(new BigDecimal(precoStr));
            
            // Converter duração
            int duracaoMinutos = Integer.parseInt(txtDuracao.getText());
            service.setDuration(Duration.ofMinutes(duracaoMinutos));
            
            // Salvar no banco
            boolean sucesso = ServicesDAO.insert(service);
            
            if (sucesso) {
                System.out.println("✅ Serviço salvo com sucesso! ID: " + service.getId());
                mostrarSucesso("Serviço cadastrado com sucesso!");
                
                // Executar callback para atualizar a lista
                if (callback != null) {
                    callback.run();
                }
                
                fecharModal();
            } else {
                mostrarErro("Erro ao salvar serviço no banco de dados.");
            }
            
        } catch (NumberFormatException e) {
            mostrarErro("Preço ou duração inválidos. Verifique os valores inseridos.");
        } catch (Exception e) {
            System.err.println("Erro ao salvar serviço: " + e.getMessage());
            e.printStackTrace();
            mostrarErro("Erro inesperado ao salvar serviço: " + e.getMessage());
        }
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
    
    private boolean validarCampos() {
        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            mostrarErro("Nome do serviço é obrigatório.");
            txtNome.requestFocus();
            return false;
        }
        
        if (txtPreco.getText() == null || txtPreco.getText().trim().isEmpty()) {
            mostrarErro("Preço é obrigatório.");
            txtPreco.requestFocus();
            return false;
        }
        
        if (txtDuracao.getText() == null || txtDuracao.getText().trim().isEmpty()) {
            mostrarErro("Duração é obrigatória.");
            txtDuracao.requestFocus();
            return false;
        }
        
        if (cbCategoria.getValue() == null) {
            mostrarErro("Categoria é obrigatória.");
            cbCategoria.requestFocus();
            return false;
        }
        
        try {
            String precoStr = txtPreco.getText().replace(",", ".");
            BigDecimal preco = new BigDecimal(precoStr);
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarErro("Preço deve ser maior que zero.");
                txtPreco.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Preço inválido. Use formato: 25.50");
            txtPreco.requestFocus();
            return false;
        }
        
        try {
            int duracao = Integer.parseInt(txtDuracao.getText());
            if (duracao <= 0) {
                mostrarErro("Duração deve ser maior que zero.");
                txtDuracao.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarErro("Duração deve ser um número inteiro (em minutos).");
            txtDuracao.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
