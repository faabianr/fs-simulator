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

## Architecture Specs
* Blocks = 1K
* Disk file = 1 MB
* inode list = 4 blocks = 16 inodes per block = 64 inodes in total
* 1 directory = 1 block = 1K
* 1 directory entry = 16 bytes (2 bytes: inode number, 14 bytes: name)
* Given above's point: 1 directory will contain 64 entries

### Main Objects

| Object        | Description  | 
| -----------   | -----------  | 
| Boot Block    | It mocks the boot block with a size of 1K. It is located at block #1 |  
| Super Block   | Contains the LIL and LBL. The size of super block is 2 blocks = 2K |
| Inodes List   | The inodes list contains all the inodes of the system. It uses 4 blocks of 1K. Each inode uses 64 bytes, given that, each block contains 16 inodes, so in total we have 64 inodes in our system |
| Data Blocks   | 8th node is not used and 9th is used for root directory, so free blocks start from 10th block.|


## Available Commands
| Command      | Description                     | Usage
| -----------  | -----------                     | -----------                 |
| createdir    | Creates a new directory         | createdir <directory name>  |
| listdir      | Displays directory's content    | listdir [directory to list] |

## Contributors
* Mariam Castañeda
* Fabián Rivera
