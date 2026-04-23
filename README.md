# Smart Campus API

A RESTful API built with JAX-RS (Jersey) for managing Rooms and Sensors 
across a university Smart Campus. Built for 5COSC022W Client-Server 
Architectures Coursework 2025/26.

---

## Technology Stack

- Java 17
- JAX-RS (Jersey 3.1.3)
- Grizzly HTTP Server
- Maven
- Jackson (JSON)

---


## API Base URL
http://localhost:8080/api/v1

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1/info | Discovery endpoint |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a room |
| GET | /api/v1/rooms/{roomId} | Get one room |
| DELETE | /api/v1/rooms/{roomId} | Delete a room |
| GET | /api/v1/sensors | Get all sensors |
| POST | /api/v1/sensors | Create a sensor |
| GET | /api/v1/sensors/{sensorId} | Get one sensor |
| DELETE | /api/v1/sensors/{sensorId} | Delete a sensor |
| GET | /api/v1/sensors/{sensorId}/readings | Get all readings |
| POST | /api/v1/sensors/{sensorId}/readings | Add a reading |

---

## Sample curl Commands

### 1. Discovery endpoint
curl -X GET http://localhost:8080/api/v1/info

### 2. Create a room
curl -X POST http://localhost:8080/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "LIB-301", "name": "Library Quiet Study", "capacity": 50}'

### 3. Create a senso
curl -X POST http://localhost:8080/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-001", "type": "Temperature", "status": "ACTIVE", "roomId": "LIB-301"}'


### 4. Filter sensors by type
curl -X GET "http://localhost:8080/api/v1/sensors?type=Temperature"

### 5. Add a sensor reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 22.5}'

### 6. Delete a room (fails if sensors exist)
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301

### 7. Get all readings for a sensor
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/reading

---

## Error Responses

| Status Code | Scenario |
|-------------|----------|
| 409 Conflict | Deleting a room that still has sensors |
| 422 Unprocessable Entity | Creating a sensor with a roomId that does not exist |
| 403 Forbidden | Posting a reading to a sensor in MAINTENANCE status |
| 404 Not Found | Resource does not exist |
| 500 Internal Server Error | Unexpected server error |

---

## Report — Answers to Coursework Questions

---

### Part 1 — Question 1
**Explain the default lifecycle of a JAX-RS Resource class and how it impacts in-memory data management.**

By default, JAX-RS creates a new instance of every resource class for each incoming HTTP request. This is called per-request lifecycle. This means every time a client sends a request to /api/v1/rooms, Jersey creates a brand new RoomResource object, handles the request, and then discards it.

This has a very important consequence for in-memory data storage. Since each resource instance is thrown away after every request, you cannot store data inside the resource class itself — the data would be lost immediately. To solve this, I used the Singleton pattern in my DataStore class. DataStore.getInstance() returns the same single instance every time, no matter how many resource objects are created. This means all requests share the same HashMap data.

Additionally, because multiple requests can arrive concurrently, I used ConcurrentHashMap instead of a regular HashMap. A regular HashMap is not thread-safe and can corrupt data if two requests try to write at the same time. ConcurrentHashMap handles this safely by allowing only one thread to modify a given key at a time, preventing race conditions and data loss.

---

### Part 1 — Question 2
**Why is HATEOAS considered a hallmark of advanced RESTful design?**

HATEOAS stands for Hypermedia As The Engine Of Application State. It means that API responses include links to related resources and available actions, rather than just returning raw data. For example, my discovery endpoint returns not just version information but also links like "rooms": "/api/v1/rooms" and "sensors": "/api/v1/sensors".

This benefits client developers in several important ways. First, clients do not need to hardcode URLs — they can discover available endpoints dynamically by following the links in responses. Second, if the API changes its URL structure, clients that follow links will automatically adapt without breaking. Third, it reduces dependency on external documentation because the API itself tells the client what it can do next. Compared to static documentation which can become outdated, HATEOAS keeps the API self-describing and always accurate.

---

### Part 2 — Question 1
**What are the implications of returning only IDs versus full room objects in a list response?**

Returning only IDs means very small response sizes, which saves network bandwidth. However the client then needs to make a separate GET request for each room to get its details, which means many additional HTTP calls. This is known as the N+1 problem and can make the client very slow.

Returning full room objects means larger response sizes but the client gets everything it needs in one single request. This reduces the number of HTTP round trips significantly. For most use cases, returning full objects is better because the cost of extra data in one response is much lower than the cost of making dozens of extra HTTP requests. My implementation returns full room objects in the list to prioritise client efficiency over minimising response size.

---

### Part 2 — Question 2
**Is the DELETE operation idempotent in your implementation?**

Yes, DELETE is idempotent in my implementation. Idempotent means that sending the same request multiple times produces the same server state as sending it once.

The first DELETE request on a room that exists and has no sensors will successfully delete it and return 204 No Content. If the same DELETE request is sent a second time, the room no longer exists, so my code returns 404 Not Found. The state of the server is the same after both calls — the room is gone. This satisfies the idempotency requirement because the server state does not change after the first successful deletion, even if the client keeps sending the same request. The HTTP status code may differ between calls but the resource state remains consistent.

---

### Part 3 — Question 1
**What happens if a client sends data in a format other than JSON to a @Consumes(APPLICATION_JSON) endpoint?**

When @Consumes(MediaType.APPLICATION_JSON) is declared on a method, JAX-RS checks the Content-Type header of every incoming request before it reaches the method. If a client sends a request with Content-Type: text/plain or Content-Type: application/xml, JAX-RS will immediately reject the request with an HTTP 415 Unsupported Media Type response. The resource method is never even called. This is handled entirely by the JAX-RS framework automatically, protecting the application from receiving data in formats it cannot process. The client is responsible for setting the correct Content-Type: application/json header on every POST request.

---

### Part 3 — Question 2
**Why is the query parameter approach superior to path-based filtering?**

Query parameters like ?type=CO2 are considered superior for filtering collections for several reasons.

First, REST principles state that URL paths should identify resources, not filtering criteria. /api/v1/sensors identifies the sensors collection as a resource. Adding /type/CO2 to the path implies that type/CO2 is a separate resource, which is semantically incorrect.

Second, query parameters are much more flexible and can be combined easily. For example ?type=CO2&status=ACTIVE is natural and simple. Achieving the same with path segments would require a complex URL like /sensors/type/CO2/status/ACTIVE which is confusing.

Third, when no filter is provided, GET /api/v1/sensors returns everything naturally. With path-based filtering you would need a completely separate endpoint for the unfiltered case.

Fourth, query parameters are the universally understood convention for filtering, searching and sorting collections in REST APIs, so any developer reading the API will immediately understand their purpose.

---

### Part 4 — Question 1
**Discuss the architectural benefits of the Sub-Resource Locator pattern.**

The Sub-Resource Locator pattern means that instead of defining all nested endpoints in one giant class, a parent resource delegates to a separate child resource class. In my implementation, SensorResource has a locator method that returns a SensorReadingResource instance when the path /{sensorId}/readings is accessed.

The benefits are significant. First, it enforces the Single Responsibility Principle — SensorResource only manages sensors, and SensorReadingResource only manages readings. Each class has one clear job. Second, it makes the code much easier to read and maintain. If all endpoints were in one class, it would become hundreds of lines long and very hard to navigate. Third, it makes individual classes easier to test in isolation. Fourth, as the API grows with more nested resources, each can be added as its own class without modifying existing code. This follows the Open/Closed Principle — open for extension, closed for modification. Finally, it mirrors the real-world hierarchy: a reading belongs to a sensor, and this ownership is reflected clearly in the code structure.

---

### Part 5 — Question 1
**Why is HTTP 422 more semantically accurate than 404 for a missing referenced resource?**

HTTP 404 Not Found means the URL endpoint itself does not exist — the server cannot find the resource being addressed by the URL path. In this case, the URL /api/v1/sensors is perfectly valid and exists.

HTTP 422 Unprocessable Entity means the server understood the request and the JSON is syntactically valid, but the content contains a logical error that prevents processing. When a client sends a valid JSON body containing a roomId that does not exist in the system, the problem is not that the endpoint is missing — the problem is that the data inside the request references something that does not exist. The request was received correctly, parsed correctly, but cannot be fulfilled because of a semantic validation failure. Therefore 422 is semantically far more accurate because it tells the client precisely what is wrong — the data they provided contains an invalid reference.

---

### Part 5 — Question 2
**What are the cybersecurity risks of exposing Java stack traces?**

Exposing raw Java stack traces to external API consumers is a serious security risk for several reasons.

First, stack traces reveal the internal package and class structure of the application, such as com.smartcampus.resource.RoomResource. An attacker can use this to understand the application architecture and target specific components.

Second, stack traces expose the library names and version numbers being used, for example Jersey 3.1.3 or Grizzly. Attackers can look up known vulnerabilities for those exact versions in public databases like CVE.

Third, stack traces can reveal file system paths on the server which gives attackers information about the server operating system and directory structure.

Fourth, error messages in stack traces sometimes contain actual data values being processed at the time of the crash, which could include sensitive information.

My GlobalExceptionMapper prevents all of this by catching every unhandled exception and returning only a generic 500 Internal Server Error message, keeping all internal details hidden from the client while logging them securely on the server side.


### Part 5 — Question 3
**Why use JAX-RS filters for logging instead of manual Logger statements?**

Using JAX-RS filters for logging is far superior to manual logging for several important reasons.

First, it follows the DRY principle — Do Not Repeat Yourself. With a filter, you write the logging code exactly once in LoggingFilter.java and it automatically applies to every single request and response. With manual logging you would need to copy and paste Logger.info() calls into every resource method across the entire application.

Second, filters implement cross-cutting concerns correctly. Logging is not part of the business logic of creating a room or reading a sensor. Mixing logging code into business logic makes methods harder to read and understand. Filters keep concerns cleanly separated.

Third, if you ever need to change the logging format, with a filter you change one file. With manual logging you would need to find and update every single method across all resource classes.

Fourth, filters guarantee consistent logging — no developer can accidentally forget to add a log statement to a new method. The filter covers everything automatically.

Fifth, filters can be easily enabled or disabled without touching any business logic code, making the application more maintainable and configurable.

---

## Author

- Name: Amitha Bandara
- Module: 5COSC022W Client-Server Architectures
- UOW ID:w2120133
- IIT ID:20240825
