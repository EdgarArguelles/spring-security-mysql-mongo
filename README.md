Spring API RESTful
===============

This is an example how to create a RESTful API using Spring Boot and the follow features:
- Implement Spring Dependency Injection.
- Implementing Spring Security and Java JSON Web Token.
- Basic structure to handle User/Roles/Permissions.
- Handle app Exceptions.
- Ready for using JpaSpecification, QueryDslPredicate and Query methods. 
- Model ready for use MySQL or MongoDB (In this example the app uses MySQL as Data Base, but its ready to use MongoDB instead).
- API Documentation with Swagger.
- CORS ready to be used by different clients (UIs or another APIs). 
- Example of Unit Test and Integration test with Code Coverage.

# Stack:
- Gradle
- Spring Boot
- Spring Data
- Spring Security
- JJWT (Java JSON Web Token)
- Project Lombok (Getter and Setter Annotations)
- Swagger (REST Documentation)
- Junit
- Jacoco (Junit Code Coverage)

# Disable security
In order to run application without security, in app.security.WebSecurityConfig.

- Comment @EnableGlobalMethodSecurity annotation.
- Change .anyRequest().authenticated() for .anyRequest().permitAll().

# Switch MySQL by Mongo
Change all Repositories: extends MongoRepository instead of MySQLRepository, for example.

- Change "UserRepository extends MySQLUserRepository" for "UserRepository extends MongoUserRepository"

# Build (create war)
    ./gradlew build

# Run
    ./gradlew bootRun

# Run Tests
    ./gradlew test jacocoTestReport
- Generated report (build/reports/tests/test/index.html)
- Coverage (build/reports/jacoco/test/html/index.html)

# Java Documentation
    ./gradlew javadoc
- Generated report (build/docs/javadoc/index.html)

# REST Documentation
    http://localhost:8000/app/swagger-ui.html