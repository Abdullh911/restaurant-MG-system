package com.example.demo1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.itextpdf.text.pdf.TextField;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.w3c.dom.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo1.ReadWrite.readBookings;

public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    TextField lastDays;

    public void SwitchAppointmentList(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("AppointmentList.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

        stage.setScene(newScene);
        stage.show();
    }

    public void SwitchMakeAppointment(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());
        HelloApplication x=new HelloApplication();
        stage.setScene(newScene);
        stage.show();
    }
    public void SwitchSettings(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Settings.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

        stage.setScene(newScene);
        stage.show();
    }
    public void SwitchClients(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Clients.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

        stage.setScene(newScene);
        stage.show();
    }
    public void SwitchStats(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Stats.fxml"));
        stage = (Stage)((Node) e.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        Scene newScene = new Scene(root, currentScene.getWidth(), currentScene.getHeight());

        stage.setScene(newScene);
        stage.show();
    }
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
        return "MG-revenue_report-Last " + fileTitle + " Days " + formattedDateTime;
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
            int total = 0;
            Double maxDay= (double) 0;
            Double minDay=Double.MAX_VALUE;
            int totalTips=0;
            String min="",max="";
            for (Map.Entry<String, Double> entry : revenuePerDay.entrySet()) {
//                int tip = getAppointmentsForDate(appointmentsWithinRange, entry.getKey())
//                        .stream()
//                        .mapToInt(Appointment::getTip)
//                        .sum();

                if (yOffset < contentHeight - 50) {
                    contentStream.showText(entry.getKey() + " - " + entry.getValue() + " EGP");
                    contentStream.newLineAtOffset(0, -15);
                    yOffset += 15;

                } else {
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
                if(minDay>entry.getValue()){
                    minDay=entry.getValue();
                    min=entry.getKey();
                }
                if(maxDay<entry.getValue()){
                    maxDay=entry.getValue();
                    max=entry.getKey();
                }
                total += entry.getValue();
                //totalTips+=tip;
            }
            int avg=0;
            if(revenuePerDay.size()>0){
                avg=total/revenuePerDay.size();
            }
            else{
                minDay= (double) 0;
            }
            contentStream.endText();

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
            contentStream.showText("Total Tips " + totalTips + " EGP");
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
            for (Map.Entry<String, Double> entry : revenuePerDay.entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
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
    private List<Appointment> getAppointmentsForDate(List<Appointment> appointments, String date) {
        return appointments.stream()
                .filter(appointment -> appointment.getDate().equals(date))
                .collect(Collectors.toList());
    }
}
