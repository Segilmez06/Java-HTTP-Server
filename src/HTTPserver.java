// Created by Segilmez06
// This project reads a simple HTML file and exposes as a local HTTP server.

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class HTTPserver {

    // Assigning variables
    static Path configPath = Paths.get("config.json");

    static Path filepath = Paths.get("index.html");
    static String dataString = "";

    static int statusCode = 200;
    static String webPath = "/";
    static int port = 80;

    public static void main(String[] args) throws IOException, ParseException {

        // Reads JSON file and gets it's data
        String content = Files.readString(configPath, StandardCharsets.US_ASCII); // Read file as string
        JSONParser parser = new JSONParser(); // Set the JSON parser
        JSONObject json = (JSONObject) parser.parse(content); // Convert string to JSON

        // Reassigns variables with values from JSON
        filepath = Paths.get(json.get("mainFile").toString()); // Get value of key "mainFile" and assign it to filepath
        statusCode = Integer.parseInt(json.get("statusCode").toString()); // Get value of key "statusCode" and assign it to statusCode
        webPath = json.get("webPath").toString(); // Get value of key "webPath" and assign it to webPath
        port = Integer.parseInt(json.get("port").toString()); // Get value of key "port" and assign it to port

        // Starts the server with the specified port and path
        HttpServer webServer = HttpServer.create(new InetSocketAddress(port), 0); // Set server with custom port and path
        webServer.createContext(webPath, new Handle()); // Handling requests
        webServer.start(); // Start the server
    }

    static class Handle implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            // If the specified file is available, then get its data as string
            File file = new File(filepath.toString()); // Get file from path string
            if (file.exists()) {
                dataString = Files.readString(filepath, StandardCharsets.US_ASCII); // If exists then set output to data from the file
            } else {
                dataString = "Err_FileNotFound"; // Else set output to error message
            }

            // Answers the request with data from file
            t.sendResponseHeaders(statusCode, dataString.length()); // Answer with HTTP status code
            OutputStream ostream = t.getResponseBody(); // Set element for streaming data
            ostream.write(dataString.getBytes()); // Write the data from the file
            ostream.close(); // Close the stream
        }
    }
}
