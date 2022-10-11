# Book Service

### Requirements
#### Build & Development
For building and running the application you need:

* [Java 11](https://www.oracle.com/uk/java/technologies/javase/jdk11-archive-downloads.html)
* [Maven 3](https://maven.apache.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
    * [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#web)
    * [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#data.sql.jpa-and-spring-data)
    * [Spring Security](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#web.security)
* [JUnit - Test Framework](https://www.tutorialspoint.com/junit/junit_test_framework.htm)
* [Mockito](https://site.mockito.org/)
* [H2 Database Engine](http://www.h2database.com/html/quickstart.html)

#### Supporting Tools
* [IntelliJ IDEA](https://www.jetbrains.com/idea/)
* [Postman](https://www.postman.com/downloads/)

### Running the application

In order to run the application using [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) from the command line

```shell
BUILD:
    mvn clean install
RUN:
    mvn spring-boot:run
```

### Functionality

The following list defines the functionality of the application, 

#### Type of Application
It is a Spring-based REST Application exposing the following two entities,
* Books
    
        A Book has an id, title, description, genre, price, unitsSold and a single Author
* Authors

        An Author has an id, first name, last name and list of books associated with the Author

The Data is stored in the in-memory H2 Database. 

#### Exposing the API
The Rest application facilitates the following REST API Calls,
* GET
* POST
* DELETE

##### GET
The *GET* request enables the user to retrieve Data from the database. The following GET commands can be used via 
**Postman** tool.

* Get Book By Id
```shell 
http://localhost:8080/api/books/{id}
```
* Get Book using Pagination
```shell 
http://localhost:8080/api/books?page={pageNumber}&pageSize={pageSize}
```
* Get Author By Id
```shell 
http://localhost:8080/api/authors/{id}
```
* Get Author using Pagination
```shell 
http://localhost:8080/api/authors?page={pageNumber}&pageSize={pageSize}
```

##### POST
The *POST* request enables the user to save Data to the database. The following POST commands can be used via 
**Postman** tool.

* Save Book
```shell 
http://localhost:8080/api/books/
```
   The POST request requires the user to send Data in JSON-Format as follows,
```
    {
      "title": "Dance of Ice and Fire",
      "description": "Game of Thrones Series",
      "price": 105.25,
      "unitsSold": 35,
      "genre": "Fantasy",
      "authorId": 3
    }
```
* Save Author
```shell 
http://localhost:8080/api/authors/
```
   The POST request requires the user to send Data in JSON-Format as follows,
```
    {
      "firstName": "George",
      "lastName": "R.R. Martin"
    }
```

##### DELETE
The *DELETE* request enables the user to Delete Data from the database. User is unable to Delete data without 
Basic Authentication. The authorization credentials are as follows, 

        Username: admin
        Password: password  

The following DELETE commands can be used via **Postman** tool.

* Delete Book by Id
```shell 
http://localhost:8080/api/books/{id}
```

* Delete Author by Id
```shell 
http://localhost:8080/api/authors/{id}
```











