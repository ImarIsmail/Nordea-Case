#  Nordea Case - Bookflow

This repository contains a small Spring Boot 3.5 reactive backend.

## What is implemented

- Loan a book
- Return a book
- Rate a book
- Seeded H2 database with books, members, loans, and ratings
- Unit and integration tests

## How the project is structured

- `controller` contains the HTTP API layer.
- `service` contains the "logic" and data flow.
- `repository` contains the database access interfaces.
- `domain` contains the persistent entities that map to the tables.
- `dto` contains request and response objects used by the API.
- `exception` contains custom errors and the global exception handler.
- `src/main/resources` contains the H2 configuration plus `schema.sql` and `data.sql`.
- `src/test` contains the tests that verify the service logic and the API wiring.

## Key files

- [CaseApplication.java](src/main/java/Nordea/Case/CaseApplication.java) starts the application.
- [LibraryController.java](src/main/java/Nordea/Case/bookflow/controller/LibraryController.java) exposes the endpoints.
- [LibraryService.java](src/main/java/Nordea/Case/bookflow/service/LibraryService.java) performs the logic operations.
- [schema.sql](src/main/resources/schema.sql) defines the database tables.
- [data.sql](src/main/resources/data.sql) loads sample data on startup.
- [LibraryServiceTest.java](src/test/java/Nordea/Case/bookflow/service/LibraryServiceTest.java) checks service behavior. (Unit test)
- [LibraryControllerIntegrationTest.java](src/test/java/Nordea/Case/bookflow/controller/LibraryControllerIntegrationTest.java) checks the API end to end. (Integration test)
- [frontend/](frontend) contains the optional Angular dashboard for step 5.

## Verification

- Automated verification: `.\\mvnw.cmd test`

Manual verification means starting the app and making HTTP requests to the API endpoints,with something like a browser, Postman, or `curl`.

Example calls:

- `GET /api/members/1/loans` to see the active loans for member 1.
- `GET /api/books/1/ratings-summary?memberId=1` to see the rating summary for book 1.
- `POST /api/loans` with a body like `{"memberId":2,"bookId":3}` to create a loan.
- `POST /api/ratings` with a body like `{"memberId":2,"bookId":3,"score":5,"feedback":"Good read"}` to save a rating.
- `POST /api/loans/3/return` with a body like `{"memberId":2}` to return a loan.

These examples are what "call the endpoints under `/api`" means in practice.

## Angular frontend

The Angular dashboard lives in the [frontend/](frontend) subfolder.

Run it like this:

1. Start the backend by running `.\mvnw.cmd spring-boot:run`
2. Open a second terminal in `frontend/`.
3. Run `npm install`.
4. Run `npm start`.
5. Open the local the Frontend on a browser should be available on `http://localhost:4200/`

The page shows the current loans for the given member, and for each loaned book it shows the aggregate rating plus the member's personal rating (if any).
