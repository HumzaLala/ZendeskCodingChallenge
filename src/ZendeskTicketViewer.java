import org.json.JSONObject;
import org.json.JSONArray;
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
        String user_choice = get_user_menu_option(console);
        JSONObject tickets_json = null;
        if(!user_choice.equalsIgnoreCase("quit")) {
            // Fetches the tickets.json file using a hardcoded API token to simply access the api
            // under my Zendesk account. For further development, the user can be prompted for credentials
            tickets_json = getJSONFromService("https://zcc9547.zendesk.com/api/v2/tickets.json", "aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH");
            if(tickets_json == null) {return;}
        }
        while(!user_choice.equalsIgnoreCase("quit")) {
            int viewing_option;
            try { // deal with string inputs
                viewing_option = Integer.parseInt(user_choice);
            } catch(NumberFormatException e) {
                System.out.println("Invalid input");
                user_choice = get_user_menu_option(console);
                continue;
            }
            handleTicketViewer(viewing_option, tickets_json, console);
            user_choice = get_user_menu_option(console);
        }
    }

    /**
     * Handles ticket output based on user menu option
     * @param viewing_option - Either a 1 or 0 denoting a menu option
     * @param tickets_json - The JSONObject containing ticket data
     * @param console - used for user input
     */
    public static void handleTicketViewer(int viewing_option, JSONObject tickets_json, Scanner console) {
        JSONArray tickets = tickets_json.getJSONArray("tickets");
        int num_tickets = tickets_json.getInt("count");
        if(viewing_option == 1) { // view all tickets
            printAllTicketsPaged(num_tickets, tickets, console);
        } else if(viewing_option == 2) { // view single ticket
            System.out.println("Enter Ticket Number: (Starting from 0)");
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

    /**
     * Prints every user ticket in a paged format, with a maximum of 25 tickets per page, prompting the user
     * to move to the next page
     * @param num_tickets - The total number of tickets
     * @param tickets - the JSONArray of all the user's tickets
     * @param console - used for user input
     */
    public static void printAllTicketsPaged(int num_tickets, JSONArray tickets, Scanner console) {
        int count = 0;
        for(int i = 0; i <= num_tickets - 1; i++) {
            try {
                print_ticket(i, tickets, num_tickets);
                count++;
            } catch(IndexOutOfBoundsException e) {
                break;
            }
            // At the end of each page (of 25), prompt the user to go to the next page
            if(count % 25 == 0 && count != num_tickets - 1) {
                System.out.println("Next page? (y/n): ");
                String res = console.nextLine();
                if(res.equalsIgnoreCase("n")) {
                    break;
                }
            }
        }
        System.out.println("END OF LISTING");
    }

    /**
     * Prints formatted view of ticket to the console
     * @param index - The ticket number
     * @param tickets - The JSONArray of all the user's tickets
     * @param num_tickets - The total number of tickets the user has
     * @throws IndexOutOfBoundsException if the index is beyond the number of tickets
     */
    public static void print_ticket(int index, JSONArray tickets, int num_tickets) {
        if(index >= num_tickets - 1 || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        // Tickets are printed starting at index 0 to allow for easy integration with the API
        System.out.println("---------START OF TICKET " + index + "-------------------------");
        System.out.println("    Subject: " + tickets.getJSONObject(index).get("subject"));
        System.out.println("    Submitted By: " + tickets.getJSONObject(index).get("submitter_id"));
        System.out.println("    Date: " + tickets.getJSONObject(index).get("created_at"));
        // More fields may be added from the API
        System.out.println("---------END OF TICKET " + index + "---------------------------");
    }


    /**
     * Fetches the json file at sUrl using the given apiToken
     * The following parameters are used primarily for testing
     * @param sUrl - URL of json file
     * @param apiToken - API token used for authorization
     * @return JSONObject of json file fetched from the service, or null if there was an issue, notifying the user
     */
    public static JSONObject getJSONFromService(String sUrl, String apiToken) {
        try {
            URL url = new URL(sUrl);
            HttpURLConnection req = (HttpURLConnection) url.openConnection();
            req.setRequestProperty("Authorization", "Basic " + apiToken);
            int resCode = req.getResponseCode();
            switch (resCode) {
                case 200 -> {
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
                }
                case 401 -> System.out.println("You don't have permission :(");
                default -> System.out.println("Something went wrong :(");
            }
        } catch (IOException e) {
            System.out.println("There was a problem with getting the tickets");
        }
        return null;
    }

    /**
     * Displays the menu of options and returns the user's choice as a String
     * @param console - used for console input
     * @return the user's menu choice as a String
     */
    public static String get_user_menu_option(Scanner console) {
        System.out.println("\n   Select view options:");
        System.out.println("   * Press 1 to view all tickets");
        System.out.println("   * Press 2 to view a ticket");
        System.out.println("   * Type 'quit' to exit");
        return console.nextLine();
    }
}
