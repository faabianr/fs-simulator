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
| Data Blocks   | 8th node is used for root directory, so free blocks start from 9th block.|


## Available Commands
| Command      | Description                       | Usage
| -----------  | -----------                       | -----------                     |
| createdir    | Creates a new directory           | createdir [directory name]      |
| createf      | Creates a new file                | createf [file name]             |
| removef      | Removes a file                    | removef [file name]             |
| removedir    | Removes a directory               | removedir [directoy name]       |
| copyf        | Copies a file in directory        | copyf [file name] [directory]   |
| copydir      | Copies a directory in directory   | copydir [dir name] [directory]  |
| listdir      | Displays directory's content      | listdir                         |
| showf        | Displays file's content           | showf [file_name]               |



## Algorithms
### Createdir command
![alt text](https://www.websequencediagrams.com/files/render?link=pDiOofWbzbbVEXAlewnpTMHY8cUHixMvp9pgUeg0n0RrqG1rssxGC0xWiVLRJWJq)
### Createf command
![alt_text](https://www.websequencediagrams.com/files/render?link=XXmG3ZP3OdbNFgCOOEmZSwgC7KTnm3vjg5oIGEw3AeTwzo1VDEJSY21DW2CejLNo)
### Removef command
![alt_text](https://www.websequencediagrams.com/files/render?link=V3HbFHo23mbmKyt1Es75sgGXkcKCBoinyYnPUrqZHyCRuyaS71PfERu79BcSimiR)
### Removedir command
![alt_text](https://www.websequencediagrams.com/files/render?link=zjygWzUMB7s84nlcmZiW1oZwdmVyAQHCtUAoMyKzYixE5STAIl0f1XWqjia4cKJP)
### ListDir command
![alt_text](https://www.websequencediagrams.com/files/render?link=Disaw4xD5j9IpEddtja9N0pnvXW5SVQZPzCIvwWOC4swrKcULMH7AfWo7TNE4MLH)
## Copyf or copydir command
![alt_text](https://www.websequencediagrams.com/files/render?link=MpoNFVOFdc3lQx9VA1EDy5EmyegoarR7N5oji2PpgujA4Y3cHDPlAQoajH6VhqE8)
## Showf command
![alt_text](https://www.websequencediagrams.com/files/render?link=lM7IuFOQKPPie3LsWoqS9Dqi9rU8q8DgzqJnh0OM03X3l3wGVgHyl50zLXAMSZ8g)
## Gotodir command
![alt_text](https://www.websequencediagrams.com/files/render?link=EHBvCrkhG3cDNrgbFPlljiOdZLfR52sm8OvzjB3KYbvomEL1JOFnkv4JZhm5K4Of)
## Contributors
* Mariam Castañeda
* Fabián Rivera
