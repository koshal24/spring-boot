Postman collection for LMS backend (local)

How to use

1. Start your Spring Boot app locally (default port 8080):

   mvn spring-boot:run

2. Import the collection into Postman:
   - File > Import > Choose `LMS-Backend-Collection.postman_collection.json`

3. Set collection variables (optional):
   - baseUrl: http://localhost:8080 (default)
   - jwt: (will be auto-saved after login)

4. Run the requests:
   - Run `Auth - Register` to create a test user (adjust email if needed).
   - Run `Auth - Login (save token)` — the test script will save the JWT into the collection variable `jwt`.
   - Use the following requests to exercise the API (collection includes them):
     - Courses: GET /api/courses, GET /api/courses/{{courseId}}, POST /api/courses, DELETE /api/courses/{{courseId}}
     - Purchases: GET /api/purchases, POST /api/purchases, DELETE /api/purchases/{{purchaseId}}
     - Quizzes: GET /api/quizzes/course/{{courseId}}, GET /api/quizzes/{{quizId}}, POST /api/quizzes, PUT /api/quizzes/{{quizId}}, DELETE /api/quizzes/{{quizId}}

5. Helpful variables to set in Postman (Collection variables):
   - baseUrl: http://localhost:8080
   - jwt: (populated after login)
   - courseId, quizId, purchaseId: IDs returned from create endpoints; paste them into variables to use GET/PUT/DELETE requests.

Notes
- The collection includes a `Health` endpoint you can run anytime to confirm the server is up.
- Replace `{{baseUrl}}` if your server uses a different host/port.
- If the project uses different protected endpoint paths, update the collection or add new requests accordingly.
