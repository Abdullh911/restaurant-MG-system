package com.example.demo1;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

public class ClientsController implements Initializable {
    @FXML
    private TableView<ClientCard> tableView;
    @FXML
    private TableColumn<ClientCard, String> nameColumn;
    @FXML
    private TableColumn<ClientCard, String> numberColumn;
    @FXML
    private TableColumn<ClientCard, Integer> numOfVisitsColumn;
    @FXML
    private TableColumn<ClientCard, String> lastVisit;
    @FXML
    TextField search;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadWrite rw = new ReadWrite();
        HashMap<String, ClientCard> data = rw.readClients("data.json");

        ObservableList<ClientCard> clientDataList = FXCollections.observableArrayList(data.values());

        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        numberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumber()));
        numOfVisitsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getVisits().size()).asObject());
        lastVisit.setCellValueFactory(cellData -> {
            if (!cellData.getValue().getVisits().isEmpty()) {
                return new SimpleStringProperty(cellData.getValue().getVisits().get(cellData.getValue().getVisits().size() - 1).getDate());
            } else {
                return new SimpleStringProperty("");
            }
        });
        search.textProperty().addListener((observable, oldValue, newValue) -> filterData(newValue, clientDataList));

        nameColumn.setCellFactory(column -> {
            return new TableCell<ClientCard, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    if (!empty) {
                        setTooltip(new Tooltip("Click for details"));
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 1) {
                                ClientCard client = getTableView().getItems().get(getIndex());
                                ArrayList<Appointment> vis = new ArrayList<>();
                                for (Map.Entry<String, ClientCard> entry : data.entrySet()) {
                                    if (entry.getValue().getName().equals(client.getName())) {
                                        vis = entry.getValue().getVisits();
                                    }
                                }
                                if (!vis.isEmpty()) {
                                    showPopup(client.getName(), vis);
                                }
                            }
                        });
                    }
                }
            };
        });

        tableView.setItems(clientDataList);
    }

    private void filterData(String searchText,ObservableList<ClientCard> clientDataList) {
        ObservableList<ClientCard> filteredData = FXCollections.observableArrayList();
        for (ClientCard client : clientDataList) {
            if (client.getNumber().toLowerCase().contains(searchText.toLowerCase())) {
                filteredData.add(client);
            }
        }
        tableView.setItems(filteredData);
    }
    private void showPopup(String name, ArrayList<Appointment> appointments) {
        Collections.sort(appointments, (a1, a2) -> a2.getDate().compareTo(a1.getDate()));
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Appointments for " + name);

        ListView<String> listView = new ListView<>();
        ObservableList<String> items = listView.getItems();
        for (Appointment appointment : appointments) {
            String item = "Date: " + appointment.getDate() + ", Time: " + appointment.getTime() +
                    ", Services: " + appointment.getSelectedServices() + ", Price: " + appointment.getPrice() +
                    ", Tips: " + appointment.getTip();
            items.add(item);
        }

        VBox vbox = new VBox(listView);
        Scene scene = new Scene(vbox, 600, 600);
        popupStage.setScene(scene);
        popupStage.show();
    }
}