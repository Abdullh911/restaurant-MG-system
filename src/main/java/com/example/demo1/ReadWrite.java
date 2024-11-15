package com.example.demo1;

import com.fasterxml.jackson.core.type.TypeReference;
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
    public static void writeBookings(String filename, ArrayList<Appointment> bookings) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            // Convert ArrayList<Appointment> to JSON
            ArrayList<Map<String, Object>> data = new ArrayList<>();
            for (Appointment appointment : bookings) {
                Map<String, Object> appointmentData = new HashMap<>();
                appointmentData.put("date", appointment.getDate());
                appointmentData.put("price", appointment.getPrice());
                appointmentData.put("selectedServices", appointment.getSelectedServices());
                appointmentData.put("number", appointment.getNumber());
                appointmentData.put("name", appointment.getName());
                appointmentData.put("id", appointment.getId());
                appointmentData.put("address", appointment.getAddress());
                appointmentData.put("payment", appointment.getPayment());
                appointmentData.put("tableNumber", appointment.getTableNumber());
                appointmentData.put("nameCustomer", appointment.getNameCustomer());
                appointmentData.put("type", appointment.getType());
                appointmentData.put("time", appointment.getTime());
                appointmentData.put("comment", appointment.getComment());

                data.add(appointmentData);
            }

            Map<String, Object> finalData = new HashMap<>();
            finalData.put("services",readServices("data.json"));
            finalData.put("bookings", data);
            mapper.writeValue(file, finalData);

            System.out.println("Data has been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeServices(String filename, HashMap<String, ArrayList<item>>linkedList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = new HashMap<>();
            data.put("services", linkedList);
            //data.put("clients",readClients("data.json"));
            data.put("bookings",readBookings("data.json"));
            mapper.writeValue(new File(filename), data);
            System.out.println("Data has been written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public static HashMap<String, ClientCard> readClients(String filename) {
//        HashMap<String, ClientCard> mp = new HashMap<>();
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            Map<String, Object> data = mapper.readValue(new File(filename), Map.class);
//            if (data.containsKey("clients") && data.get("clients") != null) {
//                Map<String, LinkedHashMap<String, Object>> clientsMap = (Map<String, LinkedHashMap<String, Object>>) data.get("clients");
//                for (Map.Entry<String, LinkedHashMap<String, Object>> entry : clientsMap.entrySet()) {
//                    LinkedHashMap<String, Object> clientData = entry.getValue();
//                    ClientCard client = new ClientCard();
//                    client.setName((String) clientData.get("name"));
//                    client.setNumber((String) clientData.get("number"));
//
//                    ArrayList<LinkedHashMap<String, Object>> visitsData = (ArrayList<LinkedHashMap<String, Object>>) clientData.get("visits");
//                    ArrayList<Appointment> visits = new ArrayList<>();
//                    for (LinkedHashMap<String, Object> visitData : visitsData) {
//                        Appointment visit = new Appointment();
//                        visit.setDate((String) visitData.get("date"));
//                        Object priceObject = visitData.get("price");
//                        double price = (priceObject != null) ? (int) priceObject : 0;
//                        visit.setPrice(price);
//
//                        visit.setSelectedServices((HashMap<String, Double>) visitData.get("selectedServices"));
//                        visit.setNumber((String) visitData.get("number"));
//                        visit.setName((String) visitData.get("name"));
//                        visit.setTime((String) visitData.get("time"));
//                        visit.setId((String) visitData.get("id"));
//                        //Object tipObject = visitData.get("tip");
//                        //int tip = (tipObject != null) ? (int) tipObject : 0;
//                        //visit.setTip(tip);
//
//                        // Set the comment field, if available, otherwise use default value
//                        visit.setComment((String) visitData.getOrDefault("comment", ""));
//
//                        visits.add(visit);
//                    }
//
//                    client.setVisits(visits);
//                    mp.put(entry.getKey(), client);
//                }
//                System.out.println("Data has been read from " + filename);
//            } else {
//                System.err.println("Error: The 'clients' key is missing or the value is null.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return mp;
//    }

    public static ArrayList<Appointment> readBookings(String filename) {
        ArrayList<Appointment> bookings = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(filename);
            if (!file.exists()) {
                System.err.println("File not found: " + filename);
                return bookings;
            }

            // Use TypeReference for more specific type capture
            Map<String, Object> data = mapper.readValue(file, new TypeReference<Map<String, Object>>() {});

            if (data.containsKey("bookings")) {
                // Ensure "bookings" key is present and is an array
                ArrayList<Map<String, Object>> bookingDataList = (ArrayList<Map<String, Object>>) data.get("bookings");

                for (Map<String, Object> bookingData : bookingDataList) {
                    Appointment appointment = new Appointment();
                    appointment.setDate((String) bookingData.get("date"));

                    // Handle price conversion safely
                    Object priceObj = bookingData.get("price");
                    if (priceObj instanceof Number) {
                        appointment.setPrice(((Number) priceObj).doubleValue());
                    } else {
                        // Handle other cases as needed
                        appointment.setPrice(0.0); // Default value or handle differently
                    }

                    appointment.setSelectedServices((HashMap<String, Number>) bookingData.get("selectedServices"));
                    appointment.setNumber((String) bookingData.get("number"));
                    appointment.setName((String) bookingData.get("name"));
                    appointment.setId((String) bookingData.get("id"));
                    appointment.setAddress((String) bookingData.get("address"));
                    appointment.setPayment((String) bookingData.get("payment"));
                    appointment.setTableNumber((String) bookingData.get("tableNumber"));
                    appointment.setNameCustomer((String) bookingData.get("nameCustomer"));
                    appointment.setType((String) bookingData.get("type"));
                    appointment.setTime((String) bookingData.get("time"));
                    appointment.setComment((String) bookingData.getOrDefault("comment", ""));

                    bookings.add(appointment);
                }
            } else {
                System.err.println("Error: 'bookings' key not found or is empty in JSON file.");
            }

            System.out.println("Data has been read from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookings;
    }









    public static HashMap<String, ArrayList<item>> readServices(String filename) {
        HashMap<String, ArrayList<item>> services = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(filename);
            if (!file.exists()) {
                System.err.println("File not found: " + filename);
                return services;
            }

            // Use TypeReference to specify the expected type for generics
            Map<String, Object> data = mapper.readValue(file, new TypeReference<Map<String, Object>>() {});

            if (data.containsKey("services")) {
                Map<String, ArrayList<Map<String, Object>>> servicesMap = (Map<String, ArrayList<Map<String, Object>>>) data.get("services");

                for (String category : servicesMap.keySet()) {
                    ArrayList<Map<String, Object>> itemsList = servicesMap.get(category);
                    ArrayList<item> itemList = new ArrayList<>();
                    for (Map<String, Object> itemMap : itemsList) {
                        item newItem = new item();
                        newItem.setName((String) itemMap.get("name"));

                        // Handle price conversion safely
                        Object priceObj = itemMap.get("price");
                        newItem.setId((String) itemMap.get("id"));
                        if (priceObj instanceof Integer) {
                            newItem.setPrice(((Integer) priceObj).doubleValue());
                        } else if (priceObj instanceof Double) {
                            newItem.setPrice((Double) priceObj);
                        } else {
                            // Handle other cases as needed
                            newItem.setPrice(0.0); // Default value or handle differently
                        }

                        itemList.add(newItem);
                    }
                    services.put(category, itemList);
                }
            }

            System.out.println("Data has been read from " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return services;
    }


}
