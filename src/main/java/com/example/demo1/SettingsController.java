package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class SettingsController implements Initializable {
    @FXML
    private TextField addService;
    @FXML
    private TextField price;
    @FXML
    private TextField category;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton; // New Edit button
    @FXML
    private TableView<ServiceItem> servicesTable;
    @FXML
    private TableColumn<ServiceItem, String> categoryColumn;
    @FXML
    private TableColumn<ServiceItem, String> nameColumn;
    @FXML
    private TableColumn<ServiceItem, Double> priceColumn;

    private ObservableList<ServiceItem> serviceItems;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the columns
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Initialize the ObservableList
        serviceItems = FXCollections.observableArrayList();

        // Set the items for the TableView
        servicesTable.setItems(serviceItems);

        // Load the data
        loadServices();
    }

    private void loadServices() {
        ReadWrite rw = new ReadWrite();
        HashMap<String, ArrayList<item>> services = rw.readServices("data.json");

        for (Map.Entry<String, ArrayList<item>> entry : services.entrySet()) {
            String category = entry.getKey();
            ArrayList<item> items = entry.getValue();
            for (item itm : items) {
                serviceItems.add(new ServiceItem(category, itm.getName(), itm.getPrice(),itm.getId()));
            }
        }
    }

    public void addService() {
        if (!addService.getText().isEmpty() && !price.getText().isEmpty() && !category.getText().isEmpty()) {
            ReadWrite rw = new ReadWrite();
            HashMap<String, ArrayList<item>> services = rw.readServices("data.json");

            String newService = addService.getText();
            String categoryText = category.getText();
            int priceValue = Integer.parseInt(price.getText().replaceAll("[^\\d.]", ""));

            item newItem = new item();
            newItem.setName(newService);
            newItem.setPrice(priceValue);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String id = sdf.format(new Date());
            id+=categoryText;
            newItem.setId(id);
            services.computeIfAbsent(categoryText, k -> new ArrayList<>());
            if (!services.get(categoryText).contains(newItem)) {
                services.get(categoryText).add(newItem);
                rw.writeServices("data.json", services);
                serviceItems.add(new ServiceItem(categoryText, newService, priceValue,id));
            }

            addService.clear();
            price.clear();
            category.clear();
        } else {
            if (addService.getText().isEmpty()) {
                addService.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            if (price.getText().isEmpty()) {
                price.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
            if (category.getText().isEmpty()) {
                category.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
            }
        }
    }

    @FXML
    private void deleteSelectedService() {
        ServiceItem selectedItem = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            ReadWrite rw = new ReadWrite();
            HashMap<String, ArrayList<item>> services = rw.readServices("data.json");

            String category = selectedItem.getCategory();
            String name = selectedItem.getName();
            double price = selectedItem.getPrice();

            services.get(category).removeIf(item -> item.getId().equals(selectedItem.getId()));
            rw.writeServices("data.json", services);
            serviceItems.remove(selectedItem);
        }
    }

    @FXML
    private void editSelectedService() {
        ServiceItem selectedItem = servicesTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                // Load the modal dialog
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EditService.fxml"));
                Parent root = loader.load();

                // Access the controller and initialize data
                EditServiceController controller = loader.getController();
                Stage stage = new Stage();
                controller.initData(selectedItem, this, stage);

                // Set up the stage and show the dialog
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait(); // Wait for the dialog to close

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to update the service item after editing in the modal dialog
    public void updateService(ServiceItem updatedItem) {
        // Update the TableView display
        serviceItems.removeIf(item -> item.getId().equals(updatedItem.getId()));
        serviceItems.add(updatedItem);

        // Update the data source (if needed)
        ReadWrite rw = new ReadWrite();
        HashMap<String, ArrayList<item>> services = rw.readServices("data.json");
        String category = updatedItem.getCategory();
        services.get(category).removeIf(item -> item.getId().equals(updatedItem.getId()));
        item newItem = new item();
        newItem.setName(updatedItem.getName());
        newItem.setPrice(updatedItem.getPrice());
        newItem.setId(updatedItem.getId());
        services.computeIfAbsent(category, k -> new ArrayList<>());
        services.get(category).add(newItem);
        rw.writeServices("data.json", services);
    }

    // Inner class to represent each row in the TableView
    public static class ServiceItem {
        private String category;
        private String name;
        private double price;
        private String id;

        public ServiceItem(String category, String name, double price,String id) {
            this.category = category;
            this.name = name;
            this.price = price;
            this.id=id;
        }

        public String getCategory() {
            return category;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
        public String getId() {
            return id;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
