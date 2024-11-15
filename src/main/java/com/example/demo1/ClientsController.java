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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ClientsController implements Initializable {
    @FXML
    private TableView<Appointment> tableView;
    @FXML
    DatePicker date;
    @FXML
    private Label totalLabel;
    @FXML
    private Label minLabel;
    @FXML
    private Label maxLabel;
    @FXML
    private Label avgLabel;

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
    private TableColumn<Appointment, String> comment;
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
    private TextField lastDays;

    private ObservableList<Appointment> appointments;
    private ArrayList<Appointment>filteredL;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> data = rw.readBookings("data.json");
        appointments = FXCollections.observableArrayList(data);

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
            // Ensure selectedServices is Map<String, Double>
            Map<String, Double> selectedServices = new HashMap<>();
            cellData.getValue().getSelectedServices().forEach((key, value) -> selectedServices.put(key, value.doubleValue()));

            List<String> servicesWithValues = new ArrayList<>();
            for (Map.Entry<String, Double> entry : selectedServices.entrySet()) {
                servicesWithValues.add(entry.getKey() + ": " + entry.getValue());
            }
            return new SimpleStringProperty(String.join(", ", servicesWithValues));
        });
        numberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumber()));
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        comment.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getComment()));
        paymentTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPayment()));
        customerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNameCustomer()));
        addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        tableNumberColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTableNumber()));
        typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        tableView.setItems(appointments);

        lastDays.textProperty().addListener((observable, oldValue, newValue) -> {
            filterAppointments(newValue, data);
        });

        // Calculate statistics for all data initially
        calculateStatistics(data);
    }

    private void filterAppointments(String lastDaysValue, ArrayList<Appointment> data) {
        if (lastDaysValue == null || lastDaysValue.isEmpty()) {
            tableView.setItems(appointments);
            calculateStatistics(data);
            return;
        }

        try {
            int days = Integer.parseInt(lastDaysValue);
            LocalDate currentDate = LocalDate.now();
            List<Appointment> filteredList = new ArrayList<>();

            for (Appointment appointment : appointments) {
                LocalDate appointmentDate = LocalDate.parse(appointment.getDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (appointmentDate.isAfter(currentDate.minusDays(days))) {
                    filteredList.add(appointment);
                }
            }
            filteredL=new ArrayList<>(filteredList);
            calculateStatistics(filteredList);
            tableView.setItems(FXCollections.observableArrayList(filteredList));
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer)
            tableView.setItems(appointments);
            calculateStatistics(data);
        }
    }

    private void calculateStatistics(List<Appointment> appointmentList) {
        DecimalFormat dff = new DecimalFormat("#.00");
        String lowest = "", highest = "";
        double max = Double.MIN_VALUE, min = Double.MAX_VALUE, sum = 0;

        for (Appointment app : appointmentList) {
            double multiplier = app.getType().equals("صالة") ? 1.2768 : 1.14;
            double price = app.getPrice() * multiplier;
            if (price > max) {
                max = price;
                highest = app.getDate();
            }
            if (price < min) {
                min = price;
                lowest = app.getDate();
            }
            sum += price;
        }

        if (!appointmentList.isEmpty()) {
            double avg = sum / appointmentList.size();

            maxLabel.setText(dff.format(max) + " (" + highest + ")");
            minLabel.setText(dff.format(min) + " (" + lowest + ")");
            avgLabel.setText(dff.format(avg));
            totalLabel.setText(dff.format(sum));
        }
    }
    @FXML
    private void viewAppointments() {
        LocalDate selectedDate = date.getValue();
        if (selectedDate == null) {
            showAlert("Please select a date.");
            return;
        }

        List<Appointment> appointmentsForSelectedDate = appointments.stream()
                .filter(appointment -> LocalDate.parse(appointment.getDate()).equals(selectedDate))
                .collect(Collectors.toList());

        if (appointmentsForSelectedDate.isEmpty()) {
            showAlert("No appointments found for the selected date.");
            return;
        }

        showAppointmentsPopup(appointmentsForSelectedDate);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAppointmentsPopup(List<Appointment> appointments) {
        try {
            // Load the FXML for the popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("appointments_popup.fxml"));
            Parent root = loader.load();

            // Get the controller for the popup
            AppointmentsPopupController popupController = loader.getController();
            popupController.setAppointments(appointments);

            // Create and show the popup stage
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setWidth(400);
            stage.setTitle("Orders for " + date.getValue());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    ////////

    public void generatePdf() {
        int days = 30;

        if (lastDays.getText().length() > 0) {
            days = Integer.parseInt(lastDays.getText());
        }
        ReadWrite rw = new ReadWrite();
        ArrayList<Appointment> appointments = rw.readBookings("data.json");
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days);
        LocalDate endDate = today;
        List<Appointment> appointmentsWithinRange = appointments.stream()
                .filter(appointment -> {
                    LocalDate appointmentDate = LocalDate.parse(appointment.getDate());
                    return !appointmentDate.isBefore(startDate) && !appointmentDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
        Collections.sort(appointmentsWithinRange, Comparator.comparing(appointment -> LocalDate.parse(appointment.getDate())));
        Map<String, Double> revenuePerDay = new LinkedHashMap<>();
        for (Appointment appointment : appointmentsWithinRange) {
            String date = appointment.getDate();
            double price = appointment.getPrice();
            revenuePerDay.put(date, revenuePerDay.getOrDefault(date, (double) 0) + price);
        }

        generateRevenuePDF(revenuePerDay, appointmentsWithinRange);
        generateRevenueExcel(revenuePerDay,appointmentsWithinRange);
        lastDays.setText("");
    }
    public String getFileName(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String formattedDateTime = currentDateTime.format(formatter);
        String fileTitle = lastDays.getText();
        if (fileTitle.length() == 0) {
            fileTitle = "30";
        }
        return "5Samakat-revenue_report-Last " + fileTitle + " Days " + formattedDateTime;
    }
    private void generateRevenuePDF(Map<String, Double> revenuePerDay, List<Appointment> appointmentsWithinRange) {
        try (PDDocument document = new PDDocument()) {
            String fileTitle = lastDays.getText();
            if (fileTitle.length() == 0) {
                fileTitle = "30";
            }
//            LocalDate currentDate = LocalDate.now();
//            Month currentMonth = currentDate.getMonth();
//            String monthString = currentMonth.toString();
            PDPage page = new PDPage();
            document.addPage(page);
            float contentHeight = 700;
            contentHeight -= 20;

            PDImageXObject pdImage = PDImageXObject.createFromFile("D:\\sho8l Mall\\5smakat\\src\\main\\resources\\com\\example\\demo1\\logoz.png", document);

            float imageWidth = 200;
            float imageHeight = 200;

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(pdImage, 20, 650, imageWidth, imageHeight);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Revenue for Last " + fileTitle + " Days");
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(PDType1Font.HELVETICA, 10);

            float yOffset = 0;
            double total = 0;
            Double maxDay= (double) 0;
            Double minDay=Double.MAX_VALUE;
            //int totalTips=0;
            String min="",max="";
            for (Appointment app:filteredL) {
                double multiplier=app.getType().equals("صالة") ?1.2768:1.14;
//                int tip = getAppointmentsForDate(appointmentsWithinRange, entry.getKey())
//                        .stream()
//                        .mapToInt(Appointment::getTip)
//                        .sum();

                if (yOffset < contentHeight - 50) {
                    contentStream.showText(app.getDate() + " - " + Math.round((app.getPrice()*multiplier) * 100.0) / 100.0 + " EGP");
                    contentStream.newLineAtOffset(0, -15);
                    yOffset += 15;

                }
                else {
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage();
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(100, 700);
                    contentStream.showText("Revenue for Last " + fileTitle + " Days (continued)");
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    yOffset = 0;
                }
                if(minDay> app.getPrice()*multiplier){
                    minDay=app.getPrice()*multiplier;
                    min= app.getDate();
                }
                if(maxDay<app.getPrice()*multiplier){
                    maxDay=app.getPrice()*multiplier;
                    max=app.getDate();
                }
                total += app.getPrice()*multiplier;
                //totalTips+=tip;
            }
            double avg=0;
            if(revenuePerDay.size()>0){
                avg=total/filteredL.size();
            }
            else{
                minDay= (double) 0;
            }
            contentStream.endText();
            avg=Math.round(avg * 100.0) / 100.0;
            total=Math.round(total * 100.0) / 100.0;
            minDay=Math.round(minDay * 100.0) / 100.0;
            maxDay=Math.round(maxDay * 100.0) / 100.0;
            contentStream.moveTo(20, contentHeight - yOffset);
            contentStream.lineTo(580, contentHeight - yOffset);
            contentStream.setLineWidth(1);
            contentStream.stroke();
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(20, contentHeight - yOffset - 15);
            contentStream.showText("Total Revenue " + total + " EGP");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Lowest Revenue " + minDay + " EGP ("+min+")");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Highest Revenue " + maxDay + " EGP ("+max+")");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Average Revenue " + avg + " EGP");
            contentStream.newLineAtOffset(0, -15);
            //contentStream.showText("Total Tips " + totalTips + " EGP");
            contentStream.newLineAtOffset(0, -15);
            contentStream.endText();

            contentStream.close();
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String formattedDateTime = currentDateTime.format(formatter);
            document.save(getFileName() + ".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void generateRevenueExcel(Map<String, Double> revenuePerDay, List<Appointment> appointmentsWithinRange) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Revenue Data");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Date");
            headerRow.createCell(1).setCellValue("Revenue (EGP)");
            //headerRow.createCell(2).setCellValue("Tips (EGP)");
            int rowNum = 1;
            for (Appointment app:filteredL) {
                double multiplier=app.getType().equals("صالة") ?1.2768:1.14;
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(app.getDate());
                row.createCell(1).setCellValue(app.getPrice()*multiplier);
//                int tip = getAppointmentsForDate(appointmentsWithinRange, entry.getKey())
//                        .stream()
//                        .mapToInt(Appointment::getTip)
//                        .sum();

                //row.createCell(2).setCellValue(tip);
            }
            String excelFileName = getFileName()+".xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(excelFileName)) {
                workbook.write(fileOut);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
