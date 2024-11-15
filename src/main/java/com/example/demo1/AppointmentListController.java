package com.example.demo1;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AppointmentListController implements Initializable {

    @FXML
    private TableView<Appointment> tableView;

    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> timeColumn;
    @FXML
    private TableColumn<Appointment, Double> priceColumn;
    @FXML
    private TableColumn<Appointment, String> selectedServicesColumn;
    @FXML
    private TableColumn<Appointment, String> numberColumn;
    @FXML
    private TableColumn<Appointment, String> nameColumn;
    @FXML
    private TableColumn<Appointment, String> commentColumn;
    @FXML
    private TableColumn<Appointment, String> paymentTypeColumn;
    @FXML
    private TableColumn<Appointment, String> customerNameColumn;
    @FXML
    private TableColumn<Appointment, String> addressColumn;
    @FXML
    private TableColumn<Appointment, String> tableNumberColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;

    @FXML
    private DatePicker editDate;
    @FXML
    private TextField editTimeH;
    @FXML
    private TextField editTimeM;
    @FXML
    private ChoiceBox<String> editAmPm;
    @FXML
    private TextField editPrice;
    @FXML
    private TextField editTips;
    @FXML
    private ListView<String> currServices;
    @FXML
    private ListView<String> newServices;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshTable();
    }

    public void refreshTable() {
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> data = rw.readBookings("data.json");
        ObservableList<Appointment> appointments = FXCollections.observableArrayList(data);

        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));

        DecimalFormat df = new DecimalFormat("#.00");

        priceColumn.setCellValueFactory(cellData -> {
            Appointment appointment = cellData.getValue();
            double multiplier = appointment.getType().equals("صالة") ? 1.2768 : 1.14;
            double adjustedPrice = appointment.getPrice() * multiplier;
            return new SimpleDoubleProperty(adjustedPrice).asObject();
        });

        priceColumn.setCellFactory(column -> new TableCell<Appointment, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(df.format(price));
                }
            }
        });

        selectedServicesColumn.setCellValueFactory(cellData -> {
            Map<String, Number> selectedServices = cellData.getValue().getSelectedServices();
            List<String> servicesWithValues = new ArrayList<>();
            for (Map.Entry<String, Number> entry : selectedServices.entrySet()) {
                servicesWithValues.add(entry.getKey() + ": " + entry.getValue());
            }
            return new SimpleStringProperty(String.join(", ", servicesWithValues));
        });

        numberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumber()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        commentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));
        paymentTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPayment()));
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNameCustomer()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        tableNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTableNumber()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        tableView.setItems(appointments);

        // Set up nameColumn click event to open edit dialog
        nameColumn.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setOnMouseClicked(null);
                } else {
                    setText(item);
                    setOnMouseClicked(event -> {
                        if (!isEmpty()) {
                            showEditPopup(getTableView().getItems().get(getIndex()));
                        }
                    });
                }
            }
        });
    }


    private void showEditPopup(Appointment appointment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditPopUp.fxml"));
            Parent root = loader.load();
            EditController editController = loader.getController();
            editController.initData(appointment, this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Edit Appointment");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open the edit dialog.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
