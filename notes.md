
-   **Open a Terminal or Command Prompt:**

    -   On Windows: Use Command Prompt or PowerShell.
    -   On Unix-like systems (Linux, macOS): Use Terminal.
-   **Navigate to Project Directory:**

    -   Use the `cd` command to navigate to the root directory of the extracted project. This directory should contain the `pom.xml` file.

    bash

-   `cd path/to/your/project`

-   **Build the Project:**

    -   Run the following command to build the project using Maven.

    bash

-   `mvn clean install`

    This command will clean the project, compile the code, run tests, and create an executable JAR or WAR file.

-   **Run the Spring Boot Application:**

    -   Once the build is successful, you can use the following command to run the Spring Boot application:

    bash


`mvn spring-boot:run`

Alternatively, you can run the generated JAR file directly:

bash

-   `java -jar target/your-project-name.jar`

    Replace `your-project-name` with the actual name of your generated JAR file.

-   **Access the API:**

    -   After the application has started, you should see log messages indicating that the Spring Boot application has started. By default, the API will be accessible at `http://localhost:8080`.
-   **Test the API:**

    -   Open a web browser or use a tool like [Postman](https://www.postman.com/) to test the API endpoints. The specific endpoints and functionality depend on the implementation of the Spring Boot application.
    - Check \credit-account-management\files\Card Account Management.postman_collection.json for the curl requests for testing
-   **Stop the Application:**

    -   To stop the running Spring Boot application, press `Ctrl + C` in the terminal or command prompt where the application is running.

-   **To run on docker use the following :**

    	FROM openjdk:8-jdk-alpine  
    	COPY target/your-application.jar /app.jar  
    	CMD ["java", "-jar", "/app.jar"]  


-   **Database  :**

    I have used postgresql and the database configs are located in \credit-account-management\src\main\resources\application-prod.yml