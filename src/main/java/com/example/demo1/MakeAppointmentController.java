package com.example.demo1;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.print.Printable.PAGE_EXISTS;

public class MakeAppointmentController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(MakeAppointmentController.class.getName());

    @FXML
    private TextField name;
    @FXML
    private TextField comment;
    @FXML
    private TextField address;
    @FXML
    private TextField mobileNum;
    @FXML
    Label priceLabel;
    @FXML
    private ScrollPane categoriesScrollPane;
    @FXML
    private ScrollPane itemsScrollPane;
    @FXML
    private VBox categoriesPane;
    @FXML
    private FlowPane itemsPane;
    @FXML
    private TableView<Map.Entry<String, Number>> selectedItemsTable;
    @FXML
    private TableColumn<Map.Entry<String, Number>, String> nameColumn;
    @FXML
    private TableColumn<Map.Entry<String, Number>, Number> quantityColumn;
    @FXML
    private ChoiceBox<String> ch;
    @FXML
    private ChoiceBox<String> chTableNum;
    @FXML
    private ChoiceBox<String> chPayment;

    private Appointment newApp = new Appointment();
    private HashMap<String, ArrayList<item>> services;
    private double multiple = 1;

    private ObservableList<Map.Entry<String, Number>> selectedItems = FXCollections.observableArrayList();

    @FXML
    public void setMultiple(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();
        if (buttonText.equals("1/2")) {
            multiple = 0.5;
        } else if (buttonText.equals("1/4")) {
            multiple = 0.25;
        } else {
            multiple = Integer.parseInt(buttonText);
        }
        System.out.println(multiple);
    }

    @FXML
    public void resetMultiple() {
        multiple = 1;
        System.out.println(multiple);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadWrite dataSet = new ReadWrite();
        services = dataSet.readServices("data.json");
        ch.getItems().addAll("صالة", "ديلفري", "تيك اواي");
        chTableNum.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13");
        chPayment.getItems().addAll("نقدي", "نقدي/فيزا", "فيزا");

        nameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getKey()));
        quantityColumn.setCellValueFactory(param -> new SimpleDoubleProperty(param.getValue().getValue().doubleValue()));

        for (String category : services.keySet()) {
            Button categoryButton = new Button(category);
            categoryButton.getStyleClass().add("category-button");
            categoryButton.setMaxWidth(Double.MAX_VALUE);
            categoryButton.setOnAction(e -> displayItems(category, services.get(category)));
            categoriesPane.getChildren().add(categoryButton);
        }

        selectedItemsTable.setItems(selectedItems); // Bind TableView to ObservableList

        updateSelectedItemsTableView();

        // Setting wrapping properties
        itemsPane.setPrefWrapLength(itemsScrollPane.getWidth());
        itemsPane.setHgap(10); // Horizontal gap between buttons
        itemsPane.setVgap(10); // Vertical gap between buttons

        // Add a listener to update the wrap length when the ScrollPane's width changes
        itemsScrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            itemsPane.setPrefWrapLength(newValue.doubleValue());
        });
    }

    private void displayItems(String category, ArrayList<item> itemsList) {
        itemsPane.getChildren().clear();
        for (item itm : itemsList) {
            Button itemButton = new Button(itm.getName() + " - EGP" + itm.getPrice());
            itemButton.getStyleClass().add("item-button");
            itemButton.setOnAction(e -> addItem(itm.getName(), category));
            itemsPane.getChildren().add(itemButton);
        }
    }

    public void addItem(String itemName, String category) {
        Number currentQuantityNumber = newApp.getSelectedServices().getOrDefault(itemName, 0.0);
        double currentQuantity = currentQuantityNumber.doubleValue(); // Convert to double explicitly

        newApp.getSelectedServices().put(itemName, currentQuantity + multiple);

        double itemPrice = 0.0;
        for (item i : services.get(category)) {
            if (itemName.equals(i.getName())) {
                itemPrice = i.getPrice();
                break;
            }
        }

        double totalPrice = newApp.getPrice() + itemPrice * multiple; // Assuming getPrice() returns double
        newApp.setPrice(totalPrice);

        // Update TableView with selected items
        updateSelectedItemsTableView();
        priceLabel.setText(String.valueOf(totalPrice));
        resetMultiple();
    }

    public double getItemPriceByName(String itemName) {
        for (Map.Entry<String, ArrayList<item>> entry : services.entrySet()) {
            for (item itm : entry.getValue()) {
                if (itemName.equals(itm.getName())) {
                    return itm.getPrice();
                }
            }
        }
        return 0;
    }

    @FXML
    public void deleteSelectedItem() {
        Map.Entry<String, Number> selectedItem = selectedItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String itemName = selectedItem.getKey();
            Number itemQuantity = selectedItem.getValue();

            // Remove item from HashMap
            newApp.getSelectedServices().remove(itemName);

            // Calculate the item price and update the total price
            double itemPrice = getItemPriceByName(itemName);
            double totalPrice = newApp.getPrice() - (itemPrice * itemQuantity.doubleValue());
            newApp.setPrice(totalPrice);
            priceLabel.setText(String.valueOf(totalPrice));
            // Update TableView
            updateSelectedItemsTableView();
        }
    }

    @FXML
    public void editSelectedItem() {
        Map.Entry<String, Number> selectedItem = selectedItemsTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            try {
                // Load the edit dialog FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("editDialog.fxml"));
                Stage dialogStage = new Stage();
                AnchorPane pane = loader.load();
                Scene scene = new Scene(pane);
                dialogStage.setScene(scene);
                dialogStage.setTitle("Edit Quantity");

                // Set controller and initial values
                EditDialogController controller = loader.getController();
                controller.setDialogStage(dialogStage);
                // Cast Number to Double when passing to setInitialValues
                controller.setInitialValues(new AbstractMap.SimpleEntry<>(selectedItem.getKey(), selectedItem.getValue().doubleValue()), newApp);

                dialogStage.showAndWait(); // Show the dialog and wait for user interaction
                System.out.println(selectedItems);
                System.out.println(newApp.getSelectedServices());
                // Update TableView after dialog closes (if needed)
                updateSelectedItemsTableView();
                priceLabel.setText(String.valueOf(newApp.getPrice()));

            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error loading edit dialog", e);
            }
        }
    }

    private int getOrderNum() {
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> orders = rw.readBookings("data.json");
        int res = 0;
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formatDate = currentDate.format(formatter2);
        System.out.println(orders);
        for (Appointment app : orders) {
            if (app.getDate().equals(formatDate)) {
                res++;
            }
        }
        return res+1;
    }

    private void updateSelectedItemsTableView() {
        selectedItems.clear(); // Clear the list first
        for (Map.Entry<String, Number> entry : newApp.getSelectedServices().entrySet()) {
            selectedItems.add(entry);
        }
    }

    @FXML
    public void endOrder() {
        if (chPayment.getValue() == null || chPayment.getValue().isEmpty() || ch.getValue() == null || ch.getValue().isEmpty() || newApp.getSelectedServices().isEmpty()) {
            return;
        }
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        String formatTime = currentTime.format(formatter);
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formatDate = currentDate.format(formatter2);
        newApp.setDate(formatDate);
        newApp.setTime(formatTime);
        newApp.setType(ch.getValue());
        newApp.setPayment(chPayment.getValue());
        newApp.setTableNumber(chTableNum.getValue());
        newApp.setNameCustomer(name.getText());
        newApp.setAddress(address.getText());
        newApp.setNumber(mobileNum.getText());
        newApp.setComment(comment.getText());
        String timestamp = String.valueOf(getOrderNum());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String id = sdf.format(new Date());
        newApp.setId(id);
        newApp.setName(timestamp);
        if (!newApp.getSelectedServices().isEmpty()) {
            ReadWrite rw = new ReadWrite();
            ArrayList<Appointment> apps = rw.readBookings("data.json");
            apps.add(newApp);
            System.out.println(newApp);
            printReceipt(newApp);
            rw.writeBookings("data.json", apps);
            selectedItems.clear();
            comment.clear();
            name.clear();
            mobileNum.clear();
            address.clear();
            ch.setValue("");
            chPayment.setValue("");
            chTableNum.setValue("");
            newApp = new Appointment();
        }
    }

    private void printReceipt(Appointment appointment) {
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        PageFormat pageFormat = printerJob.defaultPage();

        Paper paper = new Paper();
        double paperWidth = 80 * 72 / 25.4;
        double paperHeight = 200 * 72 / 25.4;
        paper.setSize(paperWidth, paperHeight);
        paper.setImageableArea(0, 0, paperWidth, paperHeight);

        pageFormat.setPaper(paper);
        pageFormat.setOrientation(PageFormat.PORTRAIT);

        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int y = 20;
                int lineHeight = 15;
                int centerX = (int) (paperWidth / 2);

                ImageIcon icon = new ImageIcon("D:\\sho8l Mall\\5smakat\\src\\main\\resources\\com\\example\\demo1\\pic.png");
                Image image = icon.getImage();

                int imageWidth = 120;
                int imageHeight = 120;
                int imageX = centerX - (imageWidth / 2);
                g2d.drawImage(image, imageX, y, imageWidth, imageHeight, null);
                y += imageHeight + lineHeight;

                // Draw "5 Samakat" centered
                String title = "5 Samakat";
                g2d.setFont(new Font("Helvetica", Font.BOLD, 12));
                int titleWidth = g2d.getFontMetrics().stringWidth(title);
                g2d.drawString(title, centerX - (titleWidth / 2), y);
                y += lineHeight;

                // Draw date and time on the right
                g2d.setFont(new Font("Helvetica", Font.PLAIN, 10));
                String dateTime = appointment.getDate() + " " + appointment.getTime();
                int dateTimeWidth = g2d.getFontMetrics().stringWidth(dateTime);
                g2d.drawString(dateTime, (int) paperWidth - dateTimeWidth - 10, y);
                y += lineHeight;

                g2d.drawLine(0, y, (int) paperWidth, y);
                y += lineHeight;

                for (Map.Entry<String, Number> entry : appointment.getSelectedServices().entrySet()) {
                    String service = entry.getKey();
                    String quantity = "x" + entry.getValue();
                    g2d.drawString(service, 10, y);
                    int quantityWidth = g2d.getFontMetrics().stringWidth(quantity);
                    g2d.drawString(quantity, (int) paperWidth - quantityWidth - 10, y);
                    y += lineHeight;
                }

                g2d.drawLine(0, y, (int) paperWidth, y);
                y += lineHeight;

                double subtotal = appointment.getPrice();
                double taxRate = 0.14;
                double taxes = subtotal * taxRate;
                double service=subtotal*0.12;
                double total = subtotal + taxes;
                if (appointment.getType().equals("صالة")){
                    total=appointment.getPrice()*1.12*1.14;
                    taxes=(appointment.getPrice()*1.12)*0.14;
                    service=appointment.getPrice()*0.12;

                }
                String fomattedService = String.format("%.2f", service);
                String formattedSubtotal = String.format("%.2f", subtotal);
                String formattedTaxes = String.format("%.2f", taxes);
                String formattedTotal = String.format("%.2f", total);

                g2d.drawString("Subtotal: " + formattedSubtotal, 10, y);
                y += lineHeight;
                if (appointment.getType().equals("صالة")){
                    g2d.drawString("12% Service: " + fomattedService, 10, y);
                    y += lineHeight;
                }
                g2d.drawString("14% Taxes: " + formattedTaxes, 10, y);
                y += lineHeight;
                g2d.drawString("Total: " + formattedTotal, 10, y);
                y += lineHeight;

                return PAGE_EXISTS;
            }
        }, pageFormat);

        boolean doPrint = printerJob.printDialog();
        if (doPrint) {
            try {
                printerJob.print();
            } catch (PrinterException e) {
                LOGGER.log(Level.SEVERE, "Printing failed: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(null, "Printing failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
