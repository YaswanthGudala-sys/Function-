# Function Hall Booking — JDBC + Java + Node.js

A beginner-friendly full-stack training project.

## Stack
- Frontend: HTML, CSS, JavaScript
- Node.js: serves the frontend and proxies `/api/*` to Java
- Java: HTTP API + JDBC
- MySQL: database

## Files
- frontend/index.html
- frontend/styles.css
- frontend/app.js
- java/pom.xml
- java/src/main/java/com/hallbooking/Main.java
- db/schema.sql
- server.js
- package.json

## Run
1. Install JDK 17+, Maven, Node.js 18+, and MySQL 8+.
2. Run `db/schema.sql` in MySQL. Default DB is `function_hall`.
3. Open `java/src/main/java/com/hallbooking/Main.java` and change DB_USER/DB_PASSWORD if needed.
4. Terminal 1:
   `cd java && mvn package && java -jar target/function-hall-api.jar`
5. Terminal 2:
   `npm install && npm start`
6. Open `http://localhost:3000`.

The payment flow is a training/demo payment form; it does not charge real money. The Confirm Booking action saves the booking and displays a success animation.
