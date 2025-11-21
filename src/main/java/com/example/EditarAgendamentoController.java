package com.example;

import com.example.backends.classes.Appointment;
import com.example.backends.classes.Client;
import com.example.backends.classes.Employee;
import com.example.backends.classes.Service;
import com.example.backends.database.data.AppointmentDAO;
import com.example.backends.database.data.ClientDAO;
import com.example.backends.database.data.EmployeeDAO;
import com.example.backends.database.data.ServicesDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

public class EditarAgendamentoController {

    @FXML private ComboBox<Client> cmbCliente;
    @FXML private ComboBox<Employee> cmbFuncionario;
    @FXML private ComboBox<Service> cmbServico;
    @FXML private DatePicker dpData;
    @FXML private TextField txtHora;
    @FXML private TextArea txtObservacoes;

    private Appointment agendamentoOriginal;
    private Consumer<Appointment> callback;

    @FXML
    private void initialize() {
        carregarComboBoxes();
        configurarComboBoxes();
    }

    private void carregarComboBoxes() {
        try {
            List<Client> clients = ClientDAO.getAllClients();
            List<Employee> employees = EmployeeDAO.getAllEmployees();
            List<Service> services = ServicesDAO.getAllServices();

            cmbCliente.getItems().setAll(clients);
            cmbFuncionario.getItems().setAll(employees);
            cmbServico.getItems().setAll(services);
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void configurarComboBoxes() {
        cmbCliente.setCellFactory(listView -> new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                setText(empty || client == null ? null : client.getName());
            }
        });
        cmbCliente.setButtonCell(new ListCell<Client>() {
            @Override
            protected void updateItem(Client client, boolean empty) {
                super.updateItem(client, empty);
                setText(empty || client == null ? null : client.getName());
            }
        });

        cmbFuncionario.setCellFactory(listView -> new ListCell<Employee>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                setText(empty || employee == null ? null : employee.getName());
            }
        });
        cmbFuncionario.setButtonCell(new ListCell<Employee>() {
            @Override
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                setText(empty || employee == null ? null : employee.getName());
            }
        });

        cmbServico.setCellFactory(listView -> new ListCell<Service>() {
            @Override
            protected void updateItem(Service service, boolean empty) {
                super.updateItem(service, empty);
                setText(empty || service == null ? null : service.getName() + " - R$ " + service.getPrice());
            }
        });
        cmbServico.setButtonCell(new ListCell<Service>() {
            @Override
            protected void updateItem(Service service, boolean empty) {
                super.updateItem(service, empty);
                setText(empty || service == null ? null : service.getName() + " - R$ " + service.getPrice());
            }
        });
    }

    // Carregar os dados do agendamento no formulário
    public void carregarAgendamento(
            Appointment appointment,
            Consumer<Appointment> callback
    ) {
        this.agendamentoOriginal = appointment;
        this.callback = callback;

        try {
            // Buscar objetos relacionados pelos IDs
            Client client = ClientDAO.getClientByID(appointment.getClientId());
            Employee employee = EmployeeDAO.getEmployeeByID(appointment.getStylistId());
            
            // Para os serviços, assumindo que há apenas um serviço principal
            if (appointment.getServiceIds() != null && !appointment.getServiceIds().isEmpty()) {
                Service service = ServicesDAO.getServiceByID(appointment.getServiceIds().get(0));
                cmbServico.setValue(service);
            }

            // Carregar dados nos controles
            cmbCliente.setValue(client);
            cmbFuncionario.setValue(employee);
            dpData.setValue(appointment.getAppointmentDateTime().toLocalDate());
            txtHora.setText(appointment.getAppointmentDateTime().toLocalTime().toString());
            txtObservacoes.setText(appointment.getNotes());
        } catch (Exception e) {
            mostrarErro("Erro", "Erro ao carregar dados do agendamento: " + e.getMessage());
        }
    }

    @FXML
    private void salvar() {
        try {
            // Validações
            if (cmbCliente.getValue() == null) {
                mostrarErro("Erro", "Selecione um cliente");
                return;
            }
            if (cmbFuncionario.getValue() == null) {
                mostrarErro("Erro", "Selecione um funcionário");
                return;
            }
            if (cmbServico.getValue() == null) {
                mostrarErro("Erro", "Selecione um serviço");
                return;
            }
            if (dpData.getValue() == null) {
                mostrarErro("Erro", "Selecione uma data");
                return;
            }
            if (txtHora.getText().trim().isEmpty()) {
                mostrarErro("Erro", "Digite o horário");
                return;
            }

            // Criar datetime
            LocalDate data = dpData.getValue();
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            LocalDateTime dateTime = LocalDateTime.of(data, hora);

            // Atualizar o agendamento
            agendamentoOriginal.setClientId(cmbCliente.getValue().getId());
            agendamentoOriginal.setStylistId(cmbFuncionario.getValue().getId());
            agendamentoOriginal.setServiceIds(List.of(cmbServico.getValue().getId()));
            agendamentoOriginal.setAppointmentDateTime(dateTime);
            agendamentoOriginal.setNotes(txtObservacoes.getText().trim());
            agendamentoOriginal.setTotalPrice(cmbServico.getValue().getPrice());
            agendamentoOriginal.setUpdatedAt(LocalDateTime.now());

            // Salvar no banco
            AppointmentDAO.update(agendamentoOriginal);

            // Retorna o agendamento atualizado para o controller principal
            if (callback != null) {
                callback.accept(agendamentoOriginal);
            }

            fechar();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarErro("Erro ao salvar", "Erro: " + e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        fechar();
    }

    private void fechar() {
        Stage stage = (Stage) cmbCliente.getScene().getWindow();
        stage.close();
    }

    private void mostrarErro(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
