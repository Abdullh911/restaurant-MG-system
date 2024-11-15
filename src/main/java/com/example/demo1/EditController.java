package com.example.demo1;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EditController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(MakeAppointmentController.class.getName());
    @FXML
    private TextField comment;
    @FXML
    private TextField newTableNum;
    @FXML
    private Label editPrice;
    @FXML
    private Label total;
    @FXML
    private ListView<String> available;
    @FXML
    private TableView<Map.Entry<String, Number>> added;
    @FXML
    private TableColumn<Map.Entry<String, Number>, String> nameColumn;
    @FXML
    private TableColumn<Map.Entry<String, Number>, Number> quantityColumn;
    @FXML
    private ListView<String> cats;

    private HashMap<String, Double> selected; // Stores selected services with quantity
    private HashMap<String, ArrayList<item>> services; // Stores all available services
    private Appointment oldApp; // Original appointment being edited
    private AppointmentListController appointmentListController; // Controller for the appointment list view
    public double multiplier = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selected = new HashMap<>(); // Initialize selected services map
        available.setOnMouseClicked(event -> handleAvailableListViewClick());
        ReadWrite rw = new ReadWrite();
        services = rw.readServices("data.json");
        for (Map.Entry<String, ArrayList<item>> entry : services.entrySet()) {
            cats.getItems().add(entry.getKey());
        }
        cats.setOnMouseClicked(event -> loadCategoryItems());

        // Set up the TableView columns
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue()));
    }

    public void loadCategoryItems() {
        String selectedItem = cats.getSelectionModel().getSelectedItem();
        available.getItems().clear(); // Clear previous items before loading new ones
        if (selectedItem != null) {
            for (item itm : services.get(selectedItem)) {
                available.getItems().add(itm.getName());
            }
        }
    }

    @FXML
    public void setMultiple(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String buttonText = clickedButton.getText();
        if (buttonText.equals("1/2")) {
            multiplier = 0.5;
        } else if (buttonText.equals("1/4")) {
            multiplier = 0.25;
        } else {
            multiplier = 1;
        }
    }

    public void initData(Appointment appointment, AppointmentListController appointmentListController) {
        this.appointmentListController = appointmentListController;
        this.oldApp = appointment;

        editPrice.setText(String.valueOf(appointment.getPrice()));
        double totalWithTax = appointment.getPrice() * 1.14;
        if (appointment.getType().equals("صالة")) {
            totalWithTax *= 1.12;
        }
        DecimalFormat df = new DecimalFormat("#.00");
        total.setText(df.format(totalWithTax));
        newTableNum.setText(appointment.getTableNumber());
        comment.setText(appointment.getComment());

        selected.clear();
        added.getItems().clear(); // Clear the TableView before adding new items
        for (Map.Entry<String, Number> entry : appointment.getSelectedServices().entrySet()) {
            selected.put(entry.getKey(), entry.getValue().doubleValue());
            added.getItems().add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().doubleValue()));
        }

//        available.getItems().clear();
//        for (Map.Entry<String, ArrayList<item>> entry : services.entrySet()) {
//            for (item service : entry.getValue()) {
//                available.getItems().add(service.getName());
//            }
//        }
    }

    private void handleAvailableListViewClick() {
        String selectedItem = available.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            selected.put(selectedItem, selected.getOrDefault(selectedItem, 0.0) + multiplier);
            updateSelectedItemsTableView();
            updatePriceAndTotal();
        }
        multiplier = 1;
    }

    private void updateSelectedItemsTableView() {
        added.getItems().clear();
        for (Map.Entry<String, Double> entry : selected.entrySet()) {
            added.getItems().add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
        }
    }

    private void updatePriceAndTotal() {
        double price = 0;
        for (Map.Entry<String, Double> entry : selected.entrySet()) {
            price += getItemPriceByName(entry.getKey()) * entry.getValue();
        }
        editPrice.setText(String.valueOf(price));
        double totalWithTax = price * 1.14;
        DecimalFormat df = new DecimalFormat("#.00");
        total.setText(df.format(totalWithTax));
    }

    private double getItemPriceByName(String itemName) {
        for (Map.Entry<String, ArrayList<item>> entry : services.entrySet()) {
            for (item itm : entry.getValue()) {
                if (itemName.equals(itm.getName())) {
                    return itm.getPrice();
                }
            }
        }
        return 0;
    }

    public void submit() {
        if (editPrice.getText().isEmpty() || selected.isEmpty()) {
            highlightEmptyFields();
        } else {
            Appointment editedApp = createEditedAppointment();
            updateDataFiles(editedApp);
            closeStage();
            appointmentListController.refreshTable();
        }
    }

    private void highlightEmptyFields() {
        if (selected.isEmpty()) {
            added.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        } else if (editPrice.getText().isEmpty()) {
            editPrice.setStyle("-fx-border-color: red; -fx-border-width: 1px;");
        }
    }

    private Appointment createEditedAppointment() {
        Appointment editedApp = new Appointment();
        editedApp.setId(oldApp.getId());
        editedApp.setTableNumber(newTableNum.getText());
        editedApp.setName(oldApp.getName());
        editedApp.setNumber(oldApp.getNumber());

        HashMap<String, Number> selectedAsNumber = new HashMap<>();
        for (Map.Entry<String, Double> entry : selected.entrySet()) {
            selectedAsNumber.put(entry.getKey(), entry.getValue());
        }

        editedApp.setSelectedServices(selectedAsNumber); // Use the converted map

        editedApp.setComment(comment.getText());
        editedApp.setPayment(oldApp.getPayment());
        editedApp.setAddress(oldApp.getAddress());
        editedApp.setDate(oldApp.getDate());
        editedApp.setTime(oldApp.getTime());
        editedApp.setType(oldApp.getType());
        editedApp.setPrice(Double.parseDouble(editPrice.getText()));
        editedApp.setNameCustomer(oldApp.getNameCustomer());

        return editedApp;
    }

    private void updateDataFiles(Appointment editedApp) {
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> apps = rw.readBookings("data.json");
        updateAppointmentList(apps, editedApp);
        rw.writeBookings("data.json", apps);
    }

    private void updateAppointmentList(ArrayList<Appointment> apps, Appointment editedApp) {
        for (int i = 0; i < apps.size(); i++) {
            if (apps.get(i).getId().equals(oldApp.getId())) {
                apps.set(i, editedApp);
                break;
            }
        }
    }

    private void closeStage() {
        Stage stage = (Stage) comment.getScene().getWindow();
        stage.close();
    }

    public void delete() {
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> apps = rw.readBookings("data.json");
        removeAppointment(apps);
        rw.writeBookings("data.json", apps);
        closeStage();
        appointmentListController.refreshTable();
    }

    private void removeAppointment(ArrayList<Appointment> apps) {
        apps.removeIf(app -> app.getId().equals(oldApp.getId()));
    }
    @FXML
    private void print() {
        Appointment appointment=oldApp;
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
