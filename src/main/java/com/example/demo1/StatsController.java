package com.example.demo1;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.*;

public class StatsController implements Initializable {
    @FXML
    private PieChart frequentServices;
    @FXML
    private BarChart<?,?> bar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ReadWrite rw = new ReadWrite();
        HashMap<String, ClientCard> data = rw.readClients("data.json");
        HashMap<String, Integer> serviceCountMap = new HashMap<>();
        ArrayList<String>s=rw.readServices("data.json");
        data.values().forEach(client -> {
            client.getVisits().forEach(appointment -> {
                appointment.getSelectedServices().forEach(service -> {
                    serviceCountMap.put(service, serviceCountMap.getOrDefault(service, 0) + 1);
                });
            });
        });
        for(int i=0;i<s.size();i++){
            if(!serviceCountMap.containsKey(s.get(i))){
                serviceCountMap.put(s.get(i),0);
            }
        }
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        serviceCountMap.forEach((service, count) -> {
            pieData.add(new PieChart.Data(service, count));
        });
        pieData.forEach(d ->
                d.nameProperty().bind(Bindings.concat("Service: ", d.getName(), " , Amount: ", d.pieValueProperty()))
        );
        frequentServices.setData(pieData);
        ArrayList<Appointment>apps=rw.readBookings("data.json");
        Collections.sort(apps, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment app1, Appointment app2) {
                return app1.getDate().compareTo(app2.getDate());
            }
        });
        if(apps.size()>0){
            XYChart.Series series1=new XYChart.Series<>();
            series1.setName("Days Revenue");
            LinkedHashMap<String,Integer> revenue=new LinkedHashMap<>();
            for (int i = Math.max(0, apps.size() - 31); i < apps.size(); i++) {
                if (revenue.containsKey(apps.get(i).getDate())) {
                    revenue.put(apps.get(i).getDate(), revenue.get(apps.get(i).getDate()) + apps.get(i).getPrice());
                } else {
                    revenue.put(apps.get(i).getDate(), apps.get(i).getPrice());
                }
            }
            revenue.forEach((date, price) -> {
                series1.getData().add(new XYChart.Data(date,price));
            });
            bar.getData().addAll(series1);
        }

    }
}
