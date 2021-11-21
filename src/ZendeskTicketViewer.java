
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ZendeskTicketViewer {
    private static Scanner console;
    public static void main(String[] args) {
        System.out.println("Welcome to the Zendesk Ticket Viewer!\n");
        greet_user();
        JSONObject json = null;
        console = new Scanner(System.in);

        String user_choice = console.next();
        while(!user_choice.equals("quit")) {
            json = getJSONObject();
            JSONArray tickets = json.getJSONArray("tickets");
            if(Integer.parseInt(user_choice) == 1) {
                // view all tickets
            } else if(Integer.parseInt(user_choice) == 2) {
                System.out.println("Enter Ticket Number: ");
                int num = console.nextInt();
                if(num < json.getInt("count")) { // check for valid ticket number
                    System.out.println("Subject: " + tickets.getJSONObject(num).get("subject"));
                } else {
                    System.out.println("Invalid ticket number");
                }
            }
            greet_user();
            user_choice = console.next();
        }
    }

    private static JSONObject getJSONObject() {
        String sUrl = "https://zcc9547.zendesk.com/api/v2/tickets.json";
        try {
            URL url = new URL(sUrl);
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.setRequestProperty("Authorization", "Basic aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH");
            int resCode = req.getResponseCode();
            if (resCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
                JSONObject obj = new JSONObject(sb.toString());
                req.disconnect();
                return obj;
            } else {
                System.out.println("Something went wrong :(");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void greet_user() {
        System.out.println("\n   Select view options:");
        System.out.println("   * Press 1 to view all tickets");
        System.out.println("   * Press 2 to view a ticket");
        System.out.println("   * Type 'quit' to exit");
    }
}
