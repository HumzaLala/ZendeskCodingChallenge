import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.util.Scanner;
import static org.junit.Assert.*;

public class ZendeskTicketViewerTest {

    // Allows for testing of console output
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    // Tests a menu option that is not '1' or '2' or 'quit' ignoring case
    @Test
    public void testIncorrectMenuInput() {
        InputStream stdin = System.in;
        System.setIn(new ByteArrayInputStream("abcdef\nquit\n".getBytes()));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        PrintStream stdout = System.out;
        System.setOut(ps);

        ZendeskTicketViewer.main(new String[0]);

        System.setIn(stdin);
        System.setOut(stdout);

        String actualOutput = byteArrayOutputStream.toString();
        boolean is_out_invalid = actualOutput.contains("Invalid input");
        assertTrue(is_out_invalid);
    }

    @Test
    public void testIncorrectOption2IndexInputOutOfBounds() {
        String actualOutput = getActualOutput(2, 40, "55\nquit\n");
        assertTrue(actualOutput.contains("Invalid ticket number"));
    }

    @Test
    public void testIncorrectOption2AlphabeticalInput() {
        String actualOutput = getActualOutput(2, 40, "abcdefghi\nquit\n");
        assertTrue(actualOutput.contains("Invalid ticket number"));
    }

    @Test
    public void testIncorrectNumericMenuInput() {
        String actualOutput = getActualOutput(17, 2, "quit\n");
        assertEquals(actualOutput, "Invalid input\r\n" +
                "\n   Select view options:\r\n" +
                "   * Press 1 to view all tickets\r\n" +
                "   * Press 2 to view a ticket\r\n" +
                "   * Type 'quit' to exit\r\n");
    }

    @Test
    public void testPrintAllExitAfter1Page() {
        String actualOutput = getActualOutput(1, 100, "y\nn\nquit\n");
        String expectedOutput = getExpectedOutput(50, 1);
        assertEquals(actualOutput, expectedOutput);
    }

    /***
     *    Tests correct output using the file 2tickets.txt as the JSON against the
     *    expected output String expectedResult. This test is included to better visualize what the
     *    tests "testPrintAllWith40Items" and "testPrintAllWith100Items" are doing
     ***/
    @Test
    public void testPrintAllWith2Items() {
        String actualOutput = getActualOutput(1, 2, "quit\n");
        String expectedResult = "---------START OF TICKET 0-------------------------\r\n" +
                "    Subject: Sample ticket: Meet the ticket\r\n" +
                "    Submitted By: 1902278299544\r\n" +
                "    Date: 2021-11-21T00:19:58Z\r\n" +
                "---------END OF TICKET 0---------------------------\r\n" +
                "---------START OF TICKET 1-------------------------\r\n" +
                "    Subject: velit eiusmod reprehenderit officia cupidatat\r\n" +
                "    Submitted By: 1902278299544\r\n" +
                "    Date: 2021-11-21T03:01:16Z\r\n" +
                "---------END OF TICKET 1---------------------------\r\n" +
                "END OF LISTING\r\n" +
                "\n   Select view options:\r\n" +
                "   * Press 1 to view all tickets\r\n" +
                "   * Press 2 to view a ticket\r\n" +
                "   * Type 'quit' to exit\r\n";
        assertEquals(expectedResult, actualOutput);
    }

    /***
     *    Testing correct output using the file '40tickets.txt' as the JSON input against a truncated
     *    version of the file 'expected_output_100.txt' which will only show output until 40 tickets
     ***/
    @Test
    public void testPrintAllWith40Items() {
        String actualOutput = getActualOutput(1, 40, "y\nquit\n");
        String expectedOutput = getExpectedOutput(40, 0);
        assertEquals(expectedOutput, actualOutput);
    }

    /***
     *    Testing correct output using the file '100tickets.txt' as the JSON input against
     *    the file 'expected_output_100.txt'
     ***/
    @Test
    public void testPrintAllWith100Items() {
        String actualOutput = getActualOutput(1, 100, "y\ny\ny\nquit\n");
        String expectedOutput = getExpectedOutput(100, 2);
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testPrintSingle() {
        //takes from json file with all 100 tickets
        String actualOutput = getActualOutput(2, 100, "2\n5\nquit\n");
        String expectedOutput = "Enter Ticket Number: (Starting from 0)\r\n" +
                "---------START OF TICKET 2-------------------------\r\n" +
                "    Subject: excepteur laborum ex occaecat Lorem\r\n" +
                "    Submitted By: 1902278299544\r\n" +
                "    Date: 2021-11-21T03:01:17Z\r\n" +
                "---------END OF TICKET 2---------------------------\r\n" +
                "\n   Select view options:\r\n" +
                "   * Press 1 to view all tickets\r\n" +
                "   * Press 2 to view a ticket\r\n" +
                "   * Type 'quit' to exit\r\n";
        assertEquals(actualOutput, expectedOutput);
    }

    /**
     * Helper function that gets the actual terminal output
     * @param viewing_option - 1 or 2 - The menu viewing option
     * @param num_tickets - The total number of tickets - used to access the correct json file (either for
     *                    40tickets.txt or 100.tickets.txt)
     * @param inputStrings - A String representing what the user would input during execution - used for
     *                     user inputs such as 'y' or 'n' for viewing the next page of tickets, or general
     *                     user inputs such as 'quit', testing incorrect inputs, etc.
     */
    private String getActualOutput(int viewing_option, int num_tickets, String inputStrings) {
        File tickets_count_100 = new File(num_tickets + "tickets.txt");
        String tickets_json_with_x_elements = "";
        try {
            Scanner scan = new Scanner(tickets_count_100);
            tickets_json_with_x_elements = scan.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Sets the user input to the String 'inputStrings'
        System.setIn(new ByteArrayInputStream(inputStrings.getBytes()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        PrintStream stdout = System.out;
        System.setOut(ps);
        JSONObject obj = new JSONObject(tickets_json_with_x_elements);
        ZendeskTicketViewer.processUserInput(obj, viewing_option + "", new Scanner(System.in));
        System.setIn(System.in);
        System.setOut(stdout);
        return byteArrayOutputStream.toString();
    }

    /**
     * Helper function that uses the file 'expected_output_100.txt' and formats in based on the *amount*
     * of tickets being outputted (truncates the rest)
     */
    private String getExpectedOutput(int num_tickets, int num_user_inputs) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader("expected_output_100.txt"));
            String line;
            int counter = 0;
            while ((line = br.readLine()) != null && counter <= num_tickets * 5 + num_user_inputs) {
                // Since actual terminal output is in CRLF format, the expected output needs to be formatted
                // in the same way
                sb.append(line).append("\r\n");
                counter++;
            }
            sb.append("END OF LISTING").append("\r\n").append("\n   Select view options:\r\n" +
                    "   * Press 1 to view all tickets\r\n" +
                    "   * Press 2 to view a ticket\r\n" +
                    "   * Type 'quit' to exit\r\n");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //Integration tests:

    // Checks if an error is outputted to the terminal given incorrect parameters.
    @Test
    public void testIncorrectApiTokenButCorrectWebsite() {
        String token = "incorrect token";
        String site = "https://zcc9547.zendesk.com/api/v2/tickets.json";
        ZendeskTicketViewer.getJSONFromService(site, token);
        boolean is_token_incorrect = outContent.toString().contains("You don't have permission :(");
        assertTrue(is_token_incorrect);
    }

    @Test
    public void testCorrectApiTokenAndCorrectWebsite() {
        String token = "aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH";
        String site = "https://zcc9547.zendesk.com/api/v2/tickets.json";
        boolean bool = ZendeskTicketViewer.getJSONFromService(site, token) != null;
        assertTrue(bool);
    }

    @Test
    public void testIncorrectAndInvalidWebsiteButCorrectToken() {
        String token = "aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH";
        String site = "incorrect website";
        ZendeskTicketViewer.getJSONFromService(site, token);
        boolean is_site_incorrect = outContent.toString().contains("There was a problem with getting the tickets");
        assertTrue(is_site_incorrect);
    }

    @Test
    public void testIncorrectButValidWebsiteBAndCorrectToken() {
        String token = "aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH";
        String site = "https://www.google.com";
        ZendeskTicketViewer.getJSONFromService(site, token);
        boolean is_incorrect = outContent.toString().contains("Something went wrong :(");
        assertTrue(is_incorrect);
    }

}