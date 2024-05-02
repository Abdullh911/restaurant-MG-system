package com.example.demo1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AppointmentListController implements Initializable {

    @FXML
    private TableView<Appointment> tableView;

    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> timeColumn;

    @FXML
    private TableColumn<Appointment, Integer> priceColumn;
    @FXML
    private TableColumn<Appointment, Integer> tipsColumn;

    @FXML
    private TableColumn<Appointment, String> selectedServicesColumn;

    @FXML
    private TableColumn<Appointment, String> numberColumn;

    @FXML
    private TableColumn<Appointment, String> nameColumn;
    @FXML
    DatePicker editDate;
    @FXML
    TextField editTimeH;
    @FXML
    TextField editTimeM;
    @FXML
    ChoiceBox<String>editAmPm;
    @FXML
    TextField editPrice;
    @FXML
    TextField editTips;
    ArrayList<String> editSelectedServices = new ArrayList<>();
    @FXML
    ListView<String> currServices;
    @FXML
    ListView<String> newServices;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> data = rw.readBookings("data.json");
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        for (Appointment appointment : data) {
            appointments.add(appointment);
        }
        System.out.println(data);
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        priceColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPrice()).asObject());
        selectedServicesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.join(", ", cellData.getValue().getSelectedServices())));
        numberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumber()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
        tipsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTip()).asObject());
        tableView.setItems(appointments);
        nameColumn.setCellFactory(column -> {
            return new TableCell<Appointment, String>() {
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
                                showPopup(getTableView().getItems().get(getIndex()));
                            }
                        });
                    }
                }
            };
        });
    }
    public void refreshTable(){
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> data = rw.readBookings("data.json");
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        for (Appointment appointment : data) {
            appointments.add(appointment);
        }
        System.out.println(data);
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        priceColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getPrice()).asObject());
        selectedServicesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.join(", ", cellData.getValue().getSelectedServices())));
        numberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumber()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
        tipsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTip()).asObject());
        tableView.setItems(appointments);
    }
    private void showPopup(Appointment appointment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditPopUp.fxml"));
            Parent root = loader.load();
            EditController editPopUpController = loader.getController();
            editPopUpController.initData(appointment,this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Edit Appointment");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
