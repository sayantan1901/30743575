## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.





1. **Database Configuration:**

   - Locate the database connection details in your DAO classes.
   - Update the configuration to match your MySQL setup:
     ```java
     String url = "jdbc:mysql://localhost:3306/supportticketingsystem";
     String user = "root";
     String password = "Sayantan@1901";
     ```
2. **Add Required Libraries:**
   - Ensure the following `.jar` files are in the `lib/` directory:
     - `jcalendar_1.0.0.jar`
     - `mysql-connector-j-9.0.0.jar`
   - If these files are not present, download them and place them in the `lib/` directory.


3. **Application Menu:**

   - The application will display a menu with various operations you can perform:
     ```
     Customer Support Ticketing System
     1. Create Ticket
     2. View Ticket
     3. Update Ticket Status
     4. Delete Ticket
     5. Assign Ticket to Agent
     6. View Ticket History
     7. Update Ticket History Description
     8. View Agent Details
     9. Update Agent Information
     10. Exit
     Enter your choice:
     ```

4. **Interacting with the Application:**
   - To perform an operation, enter the corresponding number for the menu option.
   - Follow the prompts for each operation to input the required details.

---

## Dependency Management

The `JAVA PROJECTS` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-dependency#manage-dependencies).
