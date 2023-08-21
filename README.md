# BreakDecider 🗳️

Welcome to BreakDecider, a voting application developed by whosFritz. BreakDecider is designed to facilitate voting on yes-or-no questions, primarily targeted towards students. Users can log in, create voting topics, and cast their votes. Additionally, the teacher role comes with admin privileges, enabling them to manage posts and deactivate users. The application is built using Spring Boot, Maven, Vaadin, and MariaDB, and it's deployed to an Apache2 web server.

## Features ✨

- User Authentication: Students and teachers can log in securely to access the application.
- Voting Topics: Users can create and participate in voting topics centered around yes-or-no questions.
- Teacher Privileges: Teachers have admin rights, allowing them to delete posts and deactivate users.
- Responsive Interface: The application is built with Vaadin, ensuring a user-friendly experience on various devices.
- Data Storage: MariaDB is used to store user information, voting topics, and votes securely.
- Deployment: The application is deployed to an Apache2 web server for easy access.

## Technologies Used 🛠️

- Spring Boot
- Maven
- Vaadin
- MariaDB
- Apache2

## Getting Started 🚀

Follow these steps to get BreakDecider up and running locally:

1. **Clone the Repository:** Clone this repository to your local machine using the following command:

```git clone https://github.com/whosFritz/BreakDecider.git```

3. **Database Setup:** Set up a MariaDB database for the application and update the database configuration in the `application.properties` file.
4. **Build and Run:** Navigate to the project directory and build/run the application using Maven:
   
```cd break-decider```

```mvn spring-boot:run```

6. **Access the Application:** Open your web browser and go to `http://localhost:8084` to access the BreakDecider application.

## Usage Example 📸

1. **Login:** Log in as a student or teacher.
2. **Create Voting Topic:** Students and teachers can create voting topics with yes-or-no questions.
3. **Vote:** Users can cast their votes on the available topics.
4. **Teacher Privileges:** Teachers can manage posts and deactivate users.

## Deployment 🌐

The BreakDecider application can be deployed to an Apache2 web server or any other web server of your choice. Ensure that you configure the server to serve the application correctly.

## Contributing 👥

Contributions are welcome! If you'd like to contribute to the project, feel free to submit pull requests or open issues.

## License 📝

This project is licensed under the [MIT License](LICENSE).

---

Feel free to contact whosFritz for any questions or feedback. Enjoy using BreakDecider for your voting needs! 👍🗳️
