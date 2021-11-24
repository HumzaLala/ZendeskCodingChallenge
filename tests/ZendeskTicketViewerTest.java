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
    public void testIncorrectWebsiteButCorrectToken() {
        String token = "aHVtemFsMUBvdXRsb29rLmNvbS90b2tlbjo3TkNHMXlwVk9VMU43SkFxNm9md2FQM0U3WWdBQVJZZnhQV1VaSTNH";
        String site = "incorrect website";
        ZendeskTicketViewer.getJSONFromService(site, token);
        boolean is_site_incorrect = outContent.toString().contains("There was a problem with getting the tickets");
        assertTrue(is_site_incorrect);
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
    public void testPrintAllWith2Items() {
        String tickets_json_with_2_elements = "{\"tickets\":[{\"url\":\"https://zcc9547.zendesk.com/api/v2/tickets/1.json\",\"id\":1,\"external_id\":null,\"via\":{\"channel\":\"sample_ticket\",\"source\":{\"from\":{},\"to\":{},\"rel\":null}},\"created_at\":\"2021-11-21T00:19:58Z\",\"updated_at\":\"2021-11-21T00:19:58Z\",\"type\":\"incident\",\"subject\":\"Sample ticket: Meet the ticket\",\"raw_subject\":\"Sample ticket: Meet the ticket\",\"description\":\"Hi there,\\n\\nI’m sending an email because I’m having a problem setting up your new product. Can you help me troubleshoot?\\n\\nThanks,\\n The Customer\\n\\n\",\"priority\":\"normal\",\"status\":\"open\",\"recipient\":null,\"requester_id\":1267066761189,\"submitter_id\":1902278299544,\"assignee_id\":1902278299544,\"organization_id\":null,\"group_id\":1260815648729,\"collaborator_ids\":[],\"follower_ids\":[],\"email_cc_ids\":[],\"forum_topic_id\":null,\"problem_id\":null,\"has_incidents\":false,\"is_public\":true,\"due_at\":null,\"tags\":[\"sample\",\"support\",\"zendesk\"],\"custom_fields\":[],\"satisfaction_rating\":null,\"sharing_agreement_ids\":[],\"fields\":[],\"followup_ids\":[],\"ticket_form_id\":1260814919589,\"brand_id\":1260803214149,\"allow_channelback\":false,\"allow_attachments\":true},{\"url\":\"https://zcc9547.zendesk.com/api/v2/tickets/2.json\",\"id\":2,\"external_id\":null,\"via\":{\"channel\":\"api\",\"source\":{\"from\":{},\"to\":{},\"rel\":null}},\"created_at\":\"2021-11-21T03:01:16Z\",\"updated_at\":\"2021-11-21T03:01:16Z\",\"type\":null,\"subject\":\"velit eiusmod reprehenderit officia cupidatat\",\"raw_subject\":\"velit eiusmod reprehenderit officia cupidatat\",\"description\":\"Aute ex sunt culpa ex ea esse sint cupidatat aliqua ex consequat sit reprehenderit. Velit labore proident quis culpa ad duis adipisicing laboris voluptate velit incididunt minim consequat nulla. Laboris adipisicing reprehenderit minim tempor officia ullamco occaecat ut laborum.\\n\\nAliquip velit adipisicing exercitation irure aliqua qui. Commodo eu laborum cillum nostrud eu. Mollit duis qui non ea deserunt est est et officia ut excepteur Lorem pariatur deserunt.\",\"priority\":null,\"status\":\"open\",\"recipient\":null,\"requester_id\":1902278299544,\"submitter_id\":1902278299544,\"assignee_id\":1902278299544,\"organization_id\":1260918308409,\"group_id\":1260815648729,\"collaborator_ids\":[],\"follower_ids\":[],\"email_cc_ids\":[],\"forum_topic_id\":null,\"problem_id\":null,\"has_incidents\":false,\"is_public\":true,\"due_at\":null,\"tags\":[\"est\",\"incididunt\",\"nisi\"],\"custom_fields\":[],\"satisfaction_rating\":null,\"sharing_agreement_ids\":[],\"fields\":[],\"followup_ids\":[],\"ticket_form_id\":1260814919589,\"brand_id\":1260803214149,\"allow_channelback\":false,\"allow_attachments\":true}],\"next_page\":\"https://zcc9547.zendesk.com/api/v2/tickets.json?page=2\",\"previous_page\":null,\"count\":3}";
        JSONObject obj = new JSONObject(tickets_json_with_2_elements);
        ZendeskTicketViewer.handleTicketViewer(1, obj, new Scanner(System.in));
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
                "END OF LISTING\r\n";
        assertEquals(expectedResult, outContent.toString());
    }

    @Test
    public void testPrintAllWith40Items() {
        String actualOutput = getActualOutput(1, 40, "y\n");
        String expectedOutput = getExpectedOutput(40, 0);
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testPrintAllWith100Items() {
        String actualOutput = getActualOutput(1, 100, "y\ny\ny\n");
        String expectedOutput = getExpectedOutput(100, 2);
        assertEquals(expectedOutput, actualOutput);
    }

    @Test
    public void testPrintSingle() {
        //takes from json file with all 100 tickets
        String actualOutput = getActualOutput(2, 100, "2\n5\n");
        String expectedOutput = "Enter Ticket Number: (Starting from 0)\r\n" +
                "---------START OF TICKET 2-------------------------\r\n" +
                "    Subject: excepteur laborum ex occaecat Lorem\r\n" +
                "    Submitted By: 1902278299544\r\n" +
                "    Date: 2021-11-21T03:01:17Z\r\n" +
                "---------END OF TICKET 2---------------------------\r\n";
        assertEquals(actualOutput, expectedOutput);
    }

    private String getActualOutput(int viewing_option, int num_tickets, String inputStrings) {
        File tickets_count_100 = new File(num_tickets + "tickets.txt");
        String tickets_json_with_x_elements = "";
        try {
            Scanner scan = new Scanner(tickets_count_100);
            tickets_json_with_x_elements = scan.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.setIn(new ByteArrayInputStream(inputStrings.getBytes()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(byteArrayOutputStream);
        PrintStream stdout = System.out;
        System.setOut(ps);
        JSONObject obj = new JSONObject(tickets_json_with_x_elements);
        ZendeskTicketViewer.handleTicketViewer(viewing_option, obj, new Scanner(System.in));
        System.setIn(System.in);
        System.setOut(stdout);
        return byteArrayOutputStream.toString();
    }

    private String getExpectedOutput(int num_tickets, int num_user_inputs) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader("expected_output_100.txt"));
            String line;
            int counter = 0;
            while ((line = br.readLine()) != null && counter <= num_tickets * 5 + num_user_inputs) {
                sb.append(line).append("\r\n");
                counter++;
            }
            sb.append("END OF LISTING").append("\r\n");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}