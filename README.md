# FS Simulator

This project creates an unix-like filesystem and implements its main algorithms to create, delete, copy and move files.
The main functionality is exposed using a Rest API which is consumed by a web page.

## How to run it
After cloning the project:
1. Go to project's location
2. Run `./gradlew bootRun`
3. Open the web application http://localhost:8080/v1/fs-simulator/

### Rest API
Swagger UI is included with this project so the Rest API can be easily explored. The Swagger UI can be found at: http://localhost:8080/v1/fs-simulator/swagger-ui.html

## Available Commands
| Command      | Description                     | Usage
| -----------  | -----------                     | -----------                 |
| createdir    | Creates a new directory         | createdir <directory name>  |
| listdir      | Displays directory's content    | listdir [directory to list] |

## Contributors
* Mariam Castañeda
* Fabián Rivera
