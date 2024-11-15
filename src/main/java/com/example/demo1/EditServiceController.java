package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditServiceController {
    @FXML
    private TextField categoryField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;

    private SettingsController.ServiceItem selectedItem;
    private SettingsController parentController;
    private Stage stage;

    public void initData(SettingsController.ServiceItem item, SettingsController parentController, Stage stage) {
        this.selectedItem = item;
        this.parentController = parentController;
        this.stage = stage;

        // Initialize fields with current item data
        //categoryField.setText(selectedItem.getCategory());
        nameField.setText(selectedItem.getName());
        priceField.setText(String.valueOf(selectedItem.getPrice()));
    }

    @FXML
    private void saveChanges() {
        //String newCategory = categoryField.getText();
        String newName = nameField.getText();
        double newPrice = Double.parseDouble(priceField.getText());

        // Update the selected item
        selectedItem.setCategory(selectedItem.getCategory());
        selectedItem.setName(newName);
        selectedItem.setPrice(newPrice);

        // Update the TableView and data source in the parent controller
        parentController.updateService(selectedItem);

        // Close the dialog
        stage.close();
    }
}
