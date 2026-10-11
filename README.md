#Defense Logistics

#Team16

A web application to manage defense logistics: personnel, equipment, vehicles, supplies,
logistics requests, maintenance and deployments.

Technologies: Java 17, Spring Boot, REST APIs, Microservices, PostgreSQL (JDBC), Thymeleaf.

##Modules

7 small REST services and 1 web UI. Each service has its own database.



1 - logistics-ui 8080 - web pages, login, dashboard
2 - personnel-service 8081 - personnel_db - personnel details, role, unit, posting, availability
3 - equipment-service 8082 - equipment_db - equipment records, condition, status, location
4 - vehicle-service 8083 vehicle_db - vehicles, drivers, availability
5 -inventory-service 8084 - inventory_db - supplies, stock, issue, low stock
6 - request-service 8085 - request_db - logistics requests (approve, assign, allocate, track)
7 - maintenance-service 8086 - maintenance_db - preventive / corrective maintenance, service history
8 - deployment-service 8087 - deployment_db - deployment records

How they talk to each other:
- request-service calls personnel, vehicle, equipment, inventory and deployment services when a request is allocated.
- maintenance-service calls vehicle / equipment service to change the status (MAINTENANCE / AVAILABLE).
- logistics-ui calls all the services.

## Requirements

- JDK 17 or higher
- PostgreSQL running on port 5432 with user `postgres` and password `admin`
- Internet for the first run (Maven downloads libraries)
- Ports 8080 to 8087 should be free

If your postgres password is different, run this in pgAdmin Query Tool:
`ALTER USER postgres WITH PASSWORD 'admin';`

## Run on Mac (one command)

git clone <repo-url>
cd <repo-folder>
chmod +x *.sh
./run-all.sh

The script creates the databases, builds and starts all 8 applications and opens
http://localhost:8080 in the browser. First time it takes a few minutes.

- Logs are saved in the `logs` folder (one file for each service).
- To stop everything: `./stop-all.sh`

## Run with pgAdmin and Spring Tool Suite

1. Database: in pgAdmin create 7 empty databases named `personnel_db`, `equipment_db`, `vehicle_db`,
   `inventory_db`, `request_db`, `maintenance_db`, `deployment_db`.
   For each one: right click -> Query Tool -> open the matching file from the `database` folder
   (example: `personnel_db.sql`) -> click the Play button.
2. STS: File -> Import -> Maven -> Existing Maven Projects -> select this folder -> Finish.
   Wait for Maven to finish downloading.
3. Window -> Show View -> Boot Dashboard. Start the 7 services first and `logistics-ui` last.
4. Open http://localhost:8080


## Demo steps (example from the problem statement)

1. Login as `unit`, open Requests and add a request.
   Example: ID `REQ002`, unit `UNIT-A`, Vehicle/Equipment `BOTH`, location `Chennai`,
   people `2`, supply item `ITM001`, quantity `50`.
2. Logout and login as `officer`. In the Requests table choose **Approve** and click Go.
3. Choose **Allocate resources** and click Go.
   The system checks that a vehicle, equipment, people and stock are available (same location is
   preferred), reserves them, issues the supplies and creates a deployment record.
4. Choose **Start**, then **Complete** (type some notes in the box).
   The vehicle, equipment and people become AVAILABLE again.
   If you choose **Cancel** instead, the supplies are added back to the stock.
5. Open the Dashboard to see availability, readiness %, pending requests, low stock and maintenance status.

Request status order:
SUBMITTED -> APPROVED -> ASSIGNED (optional) -> ALLOCATED -> IN_PROGRESS -> COMPLETED

A request can also be REJECTED (before allocation) or CANCELLED.

## REST APIs

Every service has the normal CRUD APIs (`GET /x`, `GET /x/{id}`, `POST /x`, `PUT /x`, `DELETE /x/{id}`).
Extra APIs:

- Personnel: `GET /personnel/available`, `PUT /personnel/{id}/availability?availability=`
- Equipment: `GET /equipment/available`, `PUT /equipment/{id}/status?status=`
- Vehicles: `GET /vehicles/available`, `PUT /vehicles/{id}/status?status=`
- Inventory: `GET /inventory/low-stock`, `PUT /inventory/{id}/issue?quantity=`
- Requests: `GET /requests/status/{status}`, `PUT /requests/{id}/approve`, `reject`, `assign`, `allocate`, `start`, `complete`, `cancel`
- Maintenance: `GET /maintenance/upcoming`, `/overdue`, `/history/{assetId}`, `PUT /maintenance/{id}/start`, `complete`, `cancel`

## Limitations

- Login and roles are checked only in the UI. The REST services have no login.
- Usernames and passwords are demo values kept in application.properties.
- Inventory does not keep a separate history of every issue.

## Team 16 - Consists of 4 members

# 1. Priyanshu Singh - 26MCA0084 - Team Leader
# 2. Sristhi Mishra - 26MCA0079
# 3. Priya singh Subodh - 26MCA0076
# 4. Deepti - 26MCA0120
