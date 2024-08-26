# Toll Calculator API 
### Done by [Yazan Ghafir](https://yazanghafir.com)

## Objective

The objective of this project is to develop a simple, robust and flexible toll calculation system for vehicles passing through toll stations. The system determines the applicable toll fees based on vehicle types, passage times, and specific toll-free rules. This system is built using the principles of Domain-Driven Design (DDD) and follows a clean architecture to ensure maintainability, scalability, and ease of testing.

### API Testing

The API is available online for testing with an integrated Swagger UI. You can access the live API and its documentation at the following URL:

[Swagger UI - Toll Calculator API](https://toll-calculator-yazanghafir.azurewebsites.net/)

## Logic Requirements:

- Each passage through a toll station in Gothenburg costs 8, 13, or 18 SEK depending on the time of day.
- The maximum amount per day and vehicle is 60 SEK.

### Toll Schedule

| Time             | Amount  |
|------------------|---------|
| 06:00 - 06:29    | 8 SEK   |
| 06:30 - 06:59    | 13 SEK  |
| 07:00 - 07:59    | 18 SEK  |
| 08:00 - 08:29    | 13 SEK  |
| 08:30 - 14:59    | 8 SEK   |
| 15:00 - 15:29    | 13 SEK  |
| 15:30 - 16:59    | 18 SEK  |
| 17:00 - 17:59    | 13 SEK  |
| 18:00 - 18:29    | 8 SEK   |
| 18:30 - 05:59    | 0 SEK   |

- Congestion tax is levied on vehicles passing a toll station Monday through Friday between 06:00 and 18:29.
- No tax is levied on Saturdays, public holidays, the day before a public holiday, or during the month of July.
- Certain vehicles are exempt from the congestion tax.
- A car that passes multiple toll stations within 60 minutes is only taxed once. The amount to be paid is the highest toll fee of those passages.

## Project Structure

The project is structured according to the clean architecture principles, which separate the concerns into distinct layers:

![image](https://github.com/user-attachments/assets/91a51137-ce87-4782-9acb-5253e2547217)

- **API (Representation Layer)**: Handles HTTP requests and responses, including the main entry point for the application via Spring Boot controllers.
  
- **Application Layer**: Contains the core application logic, divided into handlers, queries, and validators:
  - **Handlers**: Implement the business rules and coordinate the application's response to user input.
  - **Queries**: Retrieve and return data from the system in a read-only fashion.
  - **Validators**: Ensure the correctness of input data before it's processed by the application logic.

- **Domain Layer**: Encapsulates the core business logic, including domain entities and value objects. This layer is independent of any external frameworks or technologies, ensuring the business logic is at the center of the design.

- **Infrastructure Layer**: Handles the implementation details such as data storage, external services, and configuration loading. This layer is responsible for integrating with the outer world while keeping the core logic clean and isolated.

### Key Features

- **Configuration Management**: All configurations, such as vehicle types and toll fees by time, are abstracted into JSON files. This design allows you to edit configuration files without making any changes to the source code, offering flexibility and ease of maintenance.

- **Object-Oriented Design**: The system leverages various object-oriented programming techniques such as inheritance, abstract classes, and polymorphism to create a modular and extendable codebase.

- **Spring Dependency Injection**: Utilized for managing dependencies, making the code more modular, easier to test, and adhering to the Inversion of Control principle.

- **Lombok**: Used to reduce boilerplate code, especially for getters, setters, constructors, and builders, improving code readability.

- **Jackson**: Used for JSON serialization and deserialization, simplifying the process of converting objects to JSON and vice versa.

- **Swagger**: Integrated for live testing and API documentation, making it easier to interact with and test the API endpoints.

## Deployment and Testing

### Continuous Deployment

The project is set up with a GitHub Action that automates the deployment process to an Azure App Service. Every push or pull request to the main branch triggers this action, ensuring that the latest version of the code is always available online.

### Testing and Coverage

The project has a strong emphasis on testing, with a test coverage of 76%. The tests ensure that the core business logic is thoroughly validated, including edge cases and error handling. Proper exception handling is implemented throughout the application to ensure robustness in production.

## Design and Implementation Details

- **Domain-Driven Design (DDD)**: The system follows DDD principles, focusing on the core domain and ensuring that the business logic is encapsulated within the domain layer.

- **Clean Architecture**: The architecture separates the application into distinct layers, promoting a clear separation of concerns and making the system more maintainable and testable.

- **Configuration Abstraction**: Configuration files such as vehicle types and toll fees are stored in JSON format, allowing for easy updates without modifying the source code.

- **Object-Oriented Techniques**: The codebase uses object-oriented principles, including inheritance, polymorphism, and encapsulation, to create a modular and flexible system.

- **Spring Boot Controllers with Redirect**: The API uses Spring Boot controllers to handle incoming HTTP requests, with redirect capabilities integrated into the controllers.

- **Pom.xml Management**: The project’s `pom.xml` file is well-organized, ensuring that dependencies are managed effectively and the build process is streamlined.

### Future Enhancements

Given more time, the following enhancements could be made:

- **Logging Mechanism**: Implementing a proper logging mechanism to track the application's behavior and issues in production.

- **Secret Management**: Integrating with Azure Key Vault or Azure App Configuration for secure and centralized management of secrets and configuration settings.
