# Humza's Zendesk Coding Challenge
This is a CLI-based Java program tha accesses the Zendesk API to display
tickets associated with my account to the user. The menu options
available are

* Press 1 to view all tickets
* Press 2 to view a ticket
* Type 'quit' to exit

Where viewing all tickets will print them in a paged format, with 25 tickets
per page.

Included are unit tests using the JUnit testing framework,
covering incorrect user inputs in menu and submenu options, correct parsing of JSON data,
and correct terminal output based on different JSON data, such as
various numbers of total tickets. Also covered an incorrect api token or website used to fetch the tickets
JSON. 

## Execution
The machine needs to have Java installed to run this program.

To execute in a CLI, run the following
>`cd out/artifacts/zendesk_challenge_jar`
> 
> `java -jar zendesk_challenge.jar`

In a terminal window. 
