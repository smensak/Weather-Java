package com.company; 

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        // assign arguments
        String api_key = args[0];
        String city = args[1];
        // this program is intended for personal use only, arguments are thus not checked

        try {
            String host = "api.openweathermap.org";
            String api_path = "/data/2.5/";
            String weather_type = "weather";
            String units = "&units=" + "metric";
            api_key = "&APPID=" + api_key;
            String request = "?q=" + city + units + api_key;
            String message = ( "GET " + api_path + weather_type + request + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Connection: close\r\n\r\n");

            Socket s = new Socket(host, 80); // new connection to host on port 80
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            out.println(message); // send HTTP request
            StringBuilder response = new StringBuilder();

            String resp = in.readLine(); // get first line of response
            response.append(resp);
            response.append("\n");
            String[] tmp = resp.split(" ", 3);
            if (!tmp[1].equals("200")) { // if request failed
                System.err.println("Unable to get weather data.");
                System.exit(1);
            }
            while ((resp = in.readLine()) != null) { // get rest of the response
                response.append(resp);
                response.append("\n");
            }

            tmp = response.toString().split("\n\n", 2); // separate data from HTTP header

            // parsing JSON and output
            Object obj = new JSONParser().parse(tmp[1]);
            JSONObject jo = (JSONObject) obj;

            city = (String) jo.get("name");
            JSONObject main = (JSONObject) jo.get("main");
            System.out.println(city);

            System.out.print("Temperature: ");
            try {
                double temp = (Double) main.get("temp");
                System.out.println(temp);
            } catch (RuntimeException e) {
                System.out.println("not available");
            }

            System.out.print("Air pressure: ");
            try {
                long pressure = (Long) main.get("pressure");
                System.out.println(pressure);
            } catch (RuntimeException e) {
                System.out.println("not available");
            }

            JSONObject wind = (JSONObject) jo.get("wind");

            System.out.print("Wind speed: ");
            try {
                double speed = (Double) wind.get("speed");
                System.out.println(speed);
            } catch (RuntimeException e) {
                System.out.println("not available");
            }

            System.out.print("Wind direction: ");
            try {
                long direction = (Long) wind.get("deg");
                System.out.println(direction);
            } catch (RuntimeException e) {
                System.out.println("not available");
            }
        } catch (Exception e) {
            System.err.println("Something went terribly wrong. Sorry :/");
        }
    }
}
