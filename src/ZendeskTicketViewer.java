import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ZendeskTicketViewer {
    public static void main(String[] args) {
        System.out.println("Welcome to the Zendesk Ticket Viewer!\n");
        Scanner console = new Scanner(System.in);
        String user_choice = greet_user(console);
        JSONObject tickets_json = null;
        if(!user_choice.equalsIgnoreCase("quit")) {
            tickets_json = getJSONObject("https://zcc9547.zendesk.com/api/v2/tickets.json", "aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH");
        }
        while(!user_choice.equalsIgnoreCase("quit")) {
            int viewing_option;
            try { // deal with string inputs
                viewing_option = Integer.parseInt(user_choice);
            } catch(NumberFormatException e) {
                System.out.println("Invalid input");
                user_choice = greet_user(console);
                continue;
            }
            handleTicketViewer(viewing_option, tickets_json, console);
            user_choice = greet_user(console);
        }
    }

    public static void handleTicketViewer(int viewing_option, JSONObject tickets_json, Scanner console) {
        JSONArray tickets = tickets_json.getJSONArray("tickets");
        int num_tickets = tickets_json.getInt("count");
        if(viewing_option == 1) { // view all tickets
            printAllTicketsPaged(num_tickets, tickets, console);
        } else if(viewing_option == 2) { // view single ticket
            System.out.println("Enter Ticket Number: ");
            try {
                int num = Integer.parseInt(console.nextLine());
                print_ticket(num, tickets, num_tickets);
            } catch(Exception e) {
                System.out.println("Invalid ticket number");
            }
        } else {
            System.out.println("Invalid Input");
        }
    }

    public static void printAllTicketsPaged(int num_tickets, JSONArray tickets, Scanner console) {
        int count = 0;
        for(int i = 0; i < num_tickets - 1; i++) {
            try {
                print_ticket(i, tickets, num_tickets);
                count++;
            } catch(IndexOutOfBoundsException e) {
                break;
            }
            if(count == num_tickets - 1) {
                System.out.println("END OF LISTING");
                break;
            }
            if(count % 25 == 0) {
                System.out.println("Next page? (y/n): ");
                String res = console.nextLine();
                if(res.equalsIgnoreCase("n")) {
                    break;
                }
            }
        }
    }

    public static void print_ticket(int index, JSONArray tickets, int num_tickets) {
        if(index >= num_tickets - 1) {
            throw new IndexOutOfBoundsException();
        }
        System.out.println("---------START OF TICKET " + index + "-------------------------");
        System.out.println("    Subject: " + tickets.getJSONObject(index).get("subject"));
        System.out.println("    Submitted By: " + tickets.getJSONObject(index).get("submitter_id"));
        System.out.println("    Date: " + tickets.getJSONObject(index).get("created_at"));
        System.out.println("---------END OF TICKET " + index + "---------------------------");
    }

    public static JSONObject getJSONObject(String sUrl, String apiToken) {
        try {
            URL url = new URL(sUrl);
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.setRequestProperty("Authorization", "Basic " + apiToken);
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
            } else if(resCode == 401) {
                System.out.println("You don't have permission :(");
            } else {
                System.out.println("Something went wrong :(");
                return null;
            }
        } catch (IOException e) {
            System.out.println("There was a problem with getting the tickets");
        }
        return null;
    }

    public static String greet_user(Scanner console) {
        System.out.println("\n   Select view options:");
        System.out.println("   * Press 1 to view all tickets");
        System.out.println("   * Press 2 to view a ticket");
        System.out.println("   * Type 'quit' to exit");
        return console.nextLine();
    }
}
