
# Monitor Ackgm_hdm

Its purpose is to review via a scheduller job if **Maxtransit API** provide fixed orders to be validated and set envio_flag as false.  

Once the application has started a shceduller starts running with a customizable parameter of timelapse.  


## How it works

1. The project includes a properties file  (**application.properties**), with the entries:  
   `service.timelapse: To indicate the time service must wait until the next call to **Maxtransit**`
   `maxtransit.timewait: To indicate the to wait for a Maxtransit response`

2. On a daily basis, the module runs a scheduller customizable job that perform the next:  
     
3. Perform a conecction to a database server with the provided host & credentials.

4. Perform a call to **Maxtransit API** to obtain fixed orders to be processed 
   
5. If the shceduller finds one or more fixed orders, this will iterate them & validate, if a data error occurs the loop is closed and wait for the next call to **Maxtransit API** call iteration.

6. If at least one entrie is processed, service will return:

{
    "status": 1,
    "msg":"SUCCESS"
}


## Tools  

+ Java v1.8.0_202
+ Maven v3.8.6
+ Spring Boot v2.6.14
+ JUnit v5.8.2 with AssertJ v3.21.0
+ Lombok v1.18.24
+ Logback v1.2.11


## Run the app

Obtaining the application's Jar file  
`$ mvn clean install`  
  
Running the project as an executable JAR  
`$ mvn spring-boot:run`  

Running the tests  
`$ mvn test`  


## Usage

### 1. Service Health check endpoint
#### Request
`GET /olympus/monitor/v1/health`

    curl -i -X GET -H http://{server-domain|ip}/olympus/monitor/health

#### Response
    HTTP/1.1 200 OK
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Mon, 22 May 2023 05:00:55 GMT
    
   Honda Olympus [name: ms.monitor] [version: 1.0.2] [profile: dev] 2023-05-22T05:00:55 America/Mexico_City

### 2. Manually run service once
#### Request
`POST /olympus/logevent/v1/event`

    curl -i -X POST -H 'Content-Type: application/json'  http://{server-domain|ip}/olympus/monitor/v1/event

#### Response
    HTTP/1.1 200 OK
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Mon, 15 May 2023 05:00:55 GMT
    
    {
    "message": "Monitor check successfully",
    "details": 
    }
    
    
    
#### Server Logs output:
    
227 Entering Passive Mode (172,31,17,4,4,12).
LIST /ms.transferfile/inbound
150 Here comes the directory listing.
226 Directory send OK.
First file: -rw-------    1 1006     1006            0 May 24 17:58 empty-file1.txt
Calling logEvent service
EventVO [source=ms.monitor, status=2, msg=SUCCESS, file=]
LogEvent created with Status Code: 200 OK
Message: OK
Calling transferFile service
TransferFileVO [status=1, msg=SUCCESS, file=empty-file1.txt]
