package com.example.demo1;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    @FXML
    TextField addService;
    @FXML
    ListView<String> services;
    @FXML
    Button deleteButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadWrite rw = new ReadWrite();
        ArrayList<String> sr = rw.readServices("data.json");
        services.getItems().addAll(sr);

        // Add listener to ListView to handle selection changes
        services.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                // Enable delete button when an item is selected
                deleteButton.setDisable(false);
            }
        });

        // Add event handler to delete button
        deleteButton.setOnAction(event -> {
            String selectedService = services.getSelectionModel().getSelectedItem();
            if (selectedService != null) {
                ArrayList<String> servicesList = rw.readServices("data.json");
                servicesList.remove(selectedService);
                rw.writeServices("data.json", servicesList);
                services.getItems().clear();
                services.getItems().addAll(servicesList);
                deleteButton.setDisable(true); // Disable delete button after deletion
            }
        });
    }

    public void addService() {
        if (!addService.getText().isEmpty()) {
            ReadWrite rw = new ReadWrite();
            ArrayList<String> sr = rw.readServices("data.json");
            String newService = addService.getText();
            if (!sr.contains(newService)) {
                sr.add(newService);
                rw.writeServices("data.json", sr);
                services.getItems().clear();
                services.getItems().addAll(sr);
            }
            addService.clear();
        }
    }
}
