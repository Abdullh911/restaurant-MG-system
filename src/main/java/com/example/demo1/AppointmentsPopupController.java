package com.example.demo1;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.text.DecimalFormat;
import java.util.List;

public class AppointmentsPopupController {
    @FXML
    private ListView<String> appointmentsListView;
    @FXML
    private Label totalLabel;
    @FXML
    private Label minLabel;
    @FXML
    private Label maxLabel;
    @FXML
    private Label avgLabel;

    public void setAppointments(List<Appointment> appointments) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Appointment appointment : appointments) {
            double multiplier = appointment.getType().equals("صالة") ? 1.2768 : 1.14;
            double adjustedPrice = appointment.getPrice() * multiplier;
            String item = "Time: " + appointment.getTime() + ", Price: " + String.format("%.2f", adjustedPrice) + " EGP";
            items.add(item);
        }
        appointmentsListView.setItems(items);
        calculateStatistics(appointments);
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

            maxLabel.setText(dff.format(max));
            minLabel.setText(dff.format(min));
            avgLabel.setText(dff.format(avg));
            totalLabel.setText(dff.format(sum));
        }
    }
}
