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
        String userChoice = getUserMenuOption(console);
        JSONObject ticketsJson = null;
        if(!userChoice.equalsIgnoreCase("quit")) {
            // Fetches the tickets.json file using a hardcoded API token to simply access the api
            // under my Zendesk account. For further development, the user can be prompted for credentials
            String ticketsURL = "https://zcc9547.zendesk.com/api/v2/tickets.json";
            String apiToken = "aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH";
            ticketsJson = getJSONFromService(ticketsURL, apiToken);
            // user is notified about service issues in getJSONFromService()
            if(ticketsJson == null) {return;}
        }
        while(!userChoice.equalsIgnoreCase("quit")) {
            userChoice = processUserInput(ticketsJson, userChoice, console);
        }
    }

    public static String processUserInput(JSONObject ticketsJson, String currUserChoice, Scanner console) {
        int viewingOption;
        try { // deal with string inputs
            viewingOption = Integer.parseInt(currUserChoice);
        } catch(NumberFormatException e) {
            System.out.println("Invalid input");
            return getUserMenuOption(console);
        }
        handleTicketViewingOption(viewingOption, ticketsJson, console);
        return getUserMenuOption(console);
    }


    /**
     * Handles ticket output based on user menu option
     * @param viewingOption - Either a 1 or 0 denoting a menu option
     * @param ticketsJson - The JSONObject containing ticket data
     * @param console - used for user input
     */
    public static void handleTicketViewingOption(int viewingOption, JSONObject ticketsJson, Scanner console) {
        JSONArray tickets = ticketsJson.getJSONArray("tickets");
        int numTickets = ticketsJson.getInt("count");
        if(viewingOption == 1) { // view all tickets
            printAllTicketsPaged(numTickets, tickets, console);
        } else if(viewingOption == 2) { // view single ticket
            System.out.println("Enter Ticket Number: (Starting from 0)");
            try {
                int num = Integer.parseInt(console.nextLine());
                printTicket(num, tickets, numTickets);
            } catch(Exception e) { // covers NumberFormatException and IndexOutOfBoundsException
                System.out.println("Invalid ticket number");
            }
        } else {
            System.out.println("Invalid input");
        }
    }

    /**
     * Prints every user ticket in a paged format, with a maximum of 25 tickets per page, prompting the user
     * to move to the next page
     * @param num_tickets - The total number of tickets
     * @param tickets - the JSONArray of all the user's tickets
     * @param console - used for user input
     */
    private static void printAllTicketsPaged(int num_tickets, JSONArray tickets, Scanner console) {
        int count = 0;
        for(int i = 0; i <= num_tickets - 1; i++) {
            try {
                printTicket(i, tickets, num_tickets);
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
    private static void printTicket(int index, JSONArray tickets, int num_tickets) {
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
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + apiToken);
            int resCode = connection.getResponseCode();
            if(!sUrl.endsWith("json")) {
                System.out.println("Something went wrong :(");
            } else if(resCode == 200) {
                return createJSONObject(connection);
            } else if(resCode == 401) {
                System.out.println("You don't have permission :(");
            }
        } catch (IOException e) {
            System.out.println("There was a problem with getting the tickets");
        }
        return null;
    }

    private static JSONObject createJSONObject(HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        JSONObject obj = new JSONObject(sb.toString());
        connection.disconnect();
        return obj;
    }

    /**
     * Displays the menu of options and returns the user's choice as a String
     * @param console - used for console input
     * @return the user's menu choice as a String
     */
    private static String getUserMenuOption(Scanner console) {
        System.out.println("\n   Select view options:");
        System.out.println("   * Press 1 to view all tickets");
        System.out.println("   * Press 2 to view a ticket");
        System.out.println("   * Type 'quit' to exit");
        return console.nextLine();
    }
}
