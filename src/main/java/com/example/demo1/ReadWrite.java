package com.example.demo1;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReadWrite {
    public static void writeClients(String filename, Map<String, ClientCard> hashMap) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = new HashMap<>();
            data.put("clients", hashMap);
            data.put("services",readServices("data.json"));
            data.put("bookings",readBookings("data.json"));
            mapper.writeValue(new File(filename), data);
            System.out.println("Data has been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeBookings(String filename, ArrayList<Appointment> linkedList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = new HashMap<>();
            data.put("bookings", linkedList);
            data.put("services",readServices("data.json"));
            data.put("clients",readClients("data.json"));
            mapper.writeValue(new File(filename), data);
            System.out.println("Data has been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeServices(String filename, ArrayList<String> linkedList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = new HashMap<>();
            data.put("services", linkedList);
            data.put("clients",readClients("data.json"));
            data.put("bookings",readBookings("data.json"));
            mapper.writeValue(new File(filename), data);
            System.out.println("Data has been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static HashMap<String, ClientCard> readClients(String filename) {
        HashMap<String, ClientCard> mp = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(new File(filename), Map.class);
            if (data.containsKey("clients") && data.get("clients") != null) {
                Map<String, LinkedHashMap<String, Object>> clientsMap = (Map<String, LinkedHashMap<String, Object>>) data.get("clients");
                for (Map.Entry<String, LinkedHashMap<String, Object>> entry : clientsMap.entrySet()) {
                    LinkedHashMap<String, Object> clientData = entry.getValue();
                    ClientCard client = new ClientCard();
                    client.setName((String) clientData.get("name"));
                    client.setNumber((String) clientData.get("number"));

                    ArrayList<LinkedHashMap<String, Object>> visitsData = (ArrayList<LinkedHashMap<String, Object>>) clientData.get("visits");
                    ArrayList<Appointment> visits = new ArrayList<>();
                    for (LinkedHashMap<String, Object> visitData : visitsData) {
                        Appointment visit = new Appointment();
                        visit.setDate((String) visitData.get("date"));
                        Object priceObject = visitData.get("price");
                        int price = (priceObject != null) ? (int) priceObject : 0;
                        visit.setPrice(price);

                        visit.setSelectedServices((ArrayList<String>) visitData.get("selectedServices"));
                        visit.setNumber((String) visitData.get("number"));
                        visit.setName((String) visitData.get("name"));
                        visit.setTime((String) visitData.get("time"));
                        visit.setId((String) visitData.get("id"));
                        Object tipObject = visitData.get("tip");
                        int tip = (tipObject != null) ? (int) tipObject : 0;
                        visit.setTip(tip);

                        visits.add(visit);
                    }

                    client.setVisits(visits);
                    mp.put(entry.getKey(), client);
                }
                System.out.println("Data has been read from " + filename);
            } else {
                System.err.println("Error: The 'clients' key is missing or the value is null.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mp;
    }


    public static ArrayList<Appointment> readBookings(String filename) {
        ArrayList<Appointment> bookings = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(new File(filename), Map.class);
            ArrayList<Map<String, Object>> bookingDataList = (ArrayList<Map<String, Object>>) data.get("bookings");
            for (Map<String, Object> bookingData : bookingDataList) {
                Appointment appointment = new Appointment();
                appointment.setDate((String) bookingData.get("date"));
                appointment.setPrice((Integer) bookingData.get("price"));
                appointment.setSelectedServices((ArrayList<String>) bookingData.get("selectedServices"));
                appointment.setNumber((String) bookingData.get("number"));
                appointment.setName((String) bookingData.get("name"));
                appointment.setId((String) bookingData.get("id"));
                Object tipObject = bookingData.get("tip");
                int tip = 0;
                if (tipObject != null) {
                    tip = (Integer) tipObject;
                }
                appointment.setTip(tip);

                appointment.setTime((String) bookingData.get("time"));
                bookings.add(appointment);
            }
            System.out.println("Data has been read from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }



    public static ArrayList<String> readServices(String filename) {
        ArrayList<String> arrayList=new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(new File(filename), Map.class);
            arrayList = (ArrayList<String>) data.get("services");
            System.out.println("Data has been read from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
