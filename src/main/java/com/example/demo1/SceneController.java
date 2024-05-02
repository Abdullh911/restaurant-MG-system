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
        Map<String, Integer> revenuePerDay = new LinkedHashMap<>();
        for (Appointment appointment : appointmentsWithinRange) {
            String date = appointment.getDate();
            int price = appointment.getPrice();
            revenuePerDay.put(date, revenuePerDay.getOrDefault(date, 0) + price);
        }

        generateRevenuePDF(revenuePerDay);
    }
    private void generateRevenuePDF(Map<String, Integer> revenuePerDay) {
        try (PDDocument document = new PDDocument()) {
            String fileTitle = lastDays.getText();
            if (fileTitle.length() == 0) {
                fileTitle = "30";
            }
            LocalDate currentDate = LocalDate.now();
            Month currentMonth = currentDate.getMonth();
            String monthString = currentMonth.toString();
            PDPage page = new PDPage();
            document.addPage(page);
            float contentHeight = 700;
            contentHeight -= 20;

            PDImageXObject pdImage = PDImageXObject.createFromFile("D:\\demo1\\src\\main\\resources\\com\\example\\demo1\\log2.jpg", document);

            float imageWidth = 50;
            float imageHeight = 50;

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(pdImage, 20, 710, imageWidth, imageHeight);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Revenue for Last " + fileTitle + " Days");
            contentStream.newLineAtOffset(0, -20);
            contentStream.setFont(PDType1Font.HELVETICA, 10);

            float yOffset = 0;
            int total = 0,maxDay=-1,minDay=Integer.MAX_VALUE;
            for (Map.Entry<String, Integer> entry : revenuePerDay.entrySet()) {

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
                minDay=Math.min(minDay,entry.getValue());
                maxDay=Math.max(maxDay,entry.getValue());
                total += entry.getValue();
            }
            int avg=total/revenuePerDay.size();
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
            contentStream.showText("Lowest Revenue " + minDay + " EGP");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Highest Revenue " + maxDay + " EGP");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Average Revenue " + avg + " EGP");
            contentStream.newLineAtOffset(0, -15);
            contentStream.endText();

            contentStream.close();
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String formattedDateTime = currentDateTime.format(formatter);
            document.save("MG-revenue_report-Last " + fileTitle + " Days " + formattedDateTime + ".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
