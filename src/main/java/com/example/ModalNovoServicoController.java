package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.Duration;

import com.example.backends.classes.Service;
import com.example.backends.database.data.ServicesDAO;

public class ModalNovoServicoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtPreco;
    @FXML private TextField txtDuracao;
    @FXML private ComboBox<String> cbCategoria;
    @FXML private TextArea txtDescricao;
    @FXML private CheckBox chkAtivo;

    private Runnable callback;

    @FXML
    public void initialize() {
        // Inicializa o ComboBox com categorias
        cbCategoria.getItems().addAll(
            "Corte",
            "Barba",
            "Corte e Barba",
            "Coloração",
            "Tratamento",
            "Outros"
        );
        cbCategoria.setValue("Corte"); // Categoria padrão
        
        // Checkbox de ativo marcado por padrão
        chkAtivo.setSelected(true);
    }

    public void setCallback(Runnable callback) {
        this.callback = callback;
    }

    @FXML
    public void salvarServico() {
        // Validação dos campos obrigatórios
        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            mostrarAlerta("Erro", "Nome do serviço é obrigatório!");
            return;
        }

        if (txtPreco.getText() == null || txtPreco.getText().trim().isEmpty()) {
            mostrarAlerta("Erro", "Preço é obrigatório!");
            return;
        }

        if (txtDuracao.getText() == null || txtDuracao.getText().trim().isEmpty()) {
            mostrarAlerta("Erro", "Duração é obrigatória!");
            return;
        }

        if (cbCategoria.getValue() == null || cbCategoria.getValue().trim().isEmpty()) {
            mostrarAlerta("Erro", "Categoria é obrigatória!");
            return;
        }

        try {
            // Cria objeto Service com os dados do formulário
            Service service = new Service();
            service.setName(txtNome.getText().trim());
            service.setDescription(txtDescricao.getText() != null ? txtDescricao.getText().trim() : "");
            
            // Converte preço para BigDecimal
            BigDecimal preco = new BigDecimal(txtPreco.getText().trim().replace(",", "."));
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta("Erro", "Preço deve ser maior que zero!");
                return;
            }
            service.setPrice(preco);
            
            // Converte duração para Duration (em minutos)
            int duracao = Integer.parseInt(txtDuracao.getText().trim());
            if (duracao <= 0) {
                mostrarAlerta("Erro", "Duração deve ser maior que zero!");
                return;
            }
            service.setDuration(Duration.ofMinutes(duracao));
            
            // Define categoria selecionada
            service.setCategory(cbCategoria.getValue());
            
            // Define se o serviço está ativo
            service.setActive(chkAtivo.isSelected());

            // Salva no banco de dados
            boolean sucesso = ServicesDAO.insert(service);
            
            if (sucesso) {
                System.out.println("Serviço salvo com sucesso no banco de dados! ID: " + service.getId());
                
                // Executa callback para atualizar a lista
                if (callback != null) callback.run();
                
                // Fecha o modal
                fecharModal();
            } else {
                mostrarAlerta("Erro", "Não foi possível salvar o serviço no banco de dados.");
            }
            
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Preço ou duração inválidos! Verifique os valores digitados.");
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao salvar serviço: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void fecharModal() {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
