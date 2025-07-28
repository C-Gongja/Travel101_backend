# Travel 101 Backend Server (Restful API)
가제 travel101 backend
(Frontend)[<https://github.com/C-Gongja/Travel101]

### 🛠️ Built With 
Java: Spring Boot (maven -> gradle)

OAuth2.0

AWS S3

PostgreSQL

## 🔥 Why I Built This
There are lots of trip planners out there in the world, but most of them focus on flight and hotel reservation systems rather than actual advantures. Furthermore, many AI-powered trip planners have been built by big companies. I get why many companies have gone in this direction — mainly for profitability and other practical reasons.

However, I personally believe that traveling is more about learning new things and discovering yourself through new experiences.

So I started thinking — where do I get most of my new experiences, directly or indirectly? For me, platforms like Instagram and YouTube are where I find the most inspiration and new ideas.
That’s why I want to build a social network focused on 🌍 travel, 🚀 exploration, and 🌱 new experiences.

This is just the beginning of my idea. I hope to share the full project with you as soon as possible!!

## How to run
1. Clone the repository
2. Change your current directory to the sharavel-be from the cloned project:
```
 $ cd <project_directory_name>/sharavel-be
```
3. Configure the database connection is application.properties (check the Database section for more information).
4. run gradle project
```
 $ ./gradlew bootRun
```
If you are on Windows, use:
```
$ .\gradlew.bat bootRun
```

## Database
I used PostgreSQL for this project. The database connection can be configured in the application.properties file.

```
spring.datasource.url=${DATASOURCE_URL}
spring.datasource.username=${DATASOURCE_USERNAME}
spring.datasource.password=${DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=${DRIVER_CLASS_NAME}
spring.jpa.database-platform=${DB_PLATFORM}
```
>[!CAUTION] <br>
>The above application.properties is configured for a Docker container using PostgreSQL. <br>
>Your own application.properties may be different.
