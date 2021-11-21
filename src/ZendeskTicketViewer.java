
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
        JSONObject tickets_json = null;
        console = new Scanner(System.in);

        String user_choice = console.next();
        if(!user_choice.equalsIgnoreCase("quit")) {
            tickets_json = getJSONObject("tickets.json");
        }
        while(!user_choice.equalsIgnoreCase("quit")) {
            JSONArray tickets = tickets_json.getJSONArray("tickets");
            int num_tickets = tickets_json.getInt("count");
            if(Integer.parseInt(user_choice) == 1) {
                // view all tickets
                for(int i = 0; i < num_tickets - 1; i++) {
                    print_ticket(i, tickets);
                }
            } else if(Integer.parseInt(user_choice) == 2) {
                System.out.println("Enter Ticket Number: ");
                int num = console.nextInt();
                if(num < num_tickets) { // check for valid ticket number
                    print_ticket(num, tickets);
                } else {
                    System.out.println("Invalid ticket number");
                }
            }
            greet_user();
            user_choice = console.next();
        }
    }

    private static void print_ticket(int index, JSONArray tickets) {
        System.out.println("---------START OF TICKET " + index + "-------------------------");
        System.out.println("    Subject: " + tickets.getJSONObject(index).get("subject"));
        System.out.println("    Submitted By: " + tickets.getJSONObject(index).get("submitter_id"));
        System.out.println("    Date: " + tickets.getJSONObject(index).get("created_at"));
        System.out.println("---------END OF TICKET " + index + "---------------------------\n");
    }

    private static JSONObject getJSONObject(String file) {
        String sUrl = "https://zcc9547.zendesk.com/api/v2/" + file;
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
