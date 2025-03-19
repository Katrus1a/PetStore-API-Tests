#  PetStore API Testing

**PetStore API Testing** is an automated test framework for testing the [Swagger PetStore API](https://petstore.swagger.io/).  
This project performs validation of CRUD operations (Create, Read, Update, Delete) for the API used in a virtual pet store.

##  Features

🔹 **Automated testing of REST API methods**  
🔹 **Validation of status codes, JSON schemas, and response correctness**  
🔹 **Parameterized test execution**  
🔹 **Detailed test reports with Allure Reports**  
🔹 **CI/CD integration via GitHub Actions**

###  What is tested?
 **POST /pet** – Create a new pet
 **GET /pet/{id}** – Retrieve pet information  
 **PUT /pet** – Update pet information  
 **DELETE /pet/{id}** – Delete a pet

##  Technology Stack

| Technology       | Purpose |
|-----------------|---------|
| **Java**        | Main programming language |
| **REST Assured** | API testing framework |
| **TestNG**      | Test framework |
| **Maven**       | Dependency and build manager |
| **SLF4J + Logback** | Logging |
| **Allure Reports** | Test reporting |
| **GitHub Actions** | CI/CD automation |

