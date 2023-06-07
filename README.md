
# Service Ackgm_hdm

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

    curl -i -X GET -H http://{server-domain|ip}/olympus/ackgmhdm/health

#### Response
    HTTP/1.1 200 OK
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Mon, 22 May 2023 05:00:55 GMT
    
   Honda Olympus [name: ms.monitor] [version: 1.0.2] [profile: dev] 2023-05-22T05:00:55 America/Mexico_City

### 2. Manually run service once
#### Request
`POST /olympus/logevent/v1/event`

    curl -i -X POST -H 'Content-Type: application/json'  http://{server-domain|ip}/olympus/ackgmhdm/v1/event

#### Response
    HTTP/1.1 200 OK
    Content-Type: application/json
    Transfer-Encoding: chunked
    Date: Mon, 15 May 2023 05:00:55 GMT
    
    {
    "message": "\"SUCCESSS\"",
    "details": null
    }
    
    
    
#### Server Logs output:
    
    Ackgm_hdm Scheduller running - 1685053336
Maxtransit request with Status Code: 200 OK
----rqstIdentifier----:: 1234
Hibernate: select afefixedor0_.id as id1_2_, afefixedor0_.ack_id as ack_id2_2_, afefixedor0_.action_id as action_i3_2_, afefixedor0_.chrg_asct as chrg_asc4_2_, afefixedor0_.chrg_fcm as chrg_fcm5_2_, afefixedor0_.create_timestamp as create_t6_2_, afefixedor0_.due_date as due_date7_2_, afefixedor0_.envio_flag as envio_fl8_2_, afefixedor0_.extern_config_id as extern_c9_2_, afefixedor0_.model_color_id as model_c10_2_, afefixedor0_.order_number as order_n11_2_, afefixedor0_.order_type as order_t12_2_, afefixedor0_.origin_type as origin_13_2_, afefixedor0_.request_id as request14_2_, afefixedor0_.selling_code as selling15_2_, afefixedor0_.ship_fcm as ship_fc16_2_, afefixedor0_.ship_sct as ship_sc17_2_, afefixedor0_.start_day as start_d18_2_, afefixedor0_.status_ev_id as status_19_2_, afefixedor0_.update_timestamp as update_20_2_ from afedb.afe_fixed_orders_ev afefixedor0_ where afefixedor0_.request_id=?
Start:: Accepted flow
Hibernate: insert into afedb.afe_ack_ev (ack_msg, ack_request_timestamp, ack_status, fixed_order_id, last_change_timestamp) values (?, ?, ?, ?, ?)
Start:: finalFlow
Hibernate: select afefixedor0_.id as id1_2_0_, afefixedor0_.ack_id as ack_id2_2_0_, afefixedor0_.action_id as action_i3_2_0_, afefixedor0_.chrg_asct as chrg_asc4_2_0_, afefixedor0_.chrg_fcm as chrg_fcm5_2_0_, afefixedor0_.create_timestamp as create_t6_2_0_, afefixedor0_.due_date as due_date7_2_0_, afefixedor0_.envio_flag as envio_fl8_2_0_, afefixedor0_.extern_config_id as extern_c9_2_0_, afefixedor0_.model_color_id as model_c10_2_0_, afefixedor0_.order_number as order_n11_2_0_, afefixedor0_.order_type as order_t12_2_0_, afefixedor0_.origin_type as origin_13_2_0_, afefixedor0_.request_id as request14_2_0_, afefixedor0_.selling_code as selling15_2_0_, afefixedor0_.ship_fcm as ship_fc16_2_0_, afefixedor0_.ship_sct as ship_sc17_2_0_, afefixedor0_.start_day as start_d18_2_0_, afefixedor0_.status_ev_id as status_19_2_0_, afefixedor0_.update_timestamp as update_20_2_0_ from afedb.afe_fixed_orders_ev afefixedor0_ where afefixedor0_.id=?
Hibernate: update afedb.afe_fixed_orders_ev set ack_id=?, action_id=?, chrg_asct=?, chrg_fcm=?, create_timestamp=?, due_date=?, envio_flag=?, extern_config_id=?, model_color_id=?, order_number=?, order_type=?, origin_type=?, request_id=?, selling_code=?, ship_fcm=?, ship_sct=?, start_day=?, status_ev_id=?, update_timestamp=? where id=?
Hibernate: select afeactione0_.id as id1_1_, afeactione0_.action as action2_1_, afeactione0_.create_timestamp as create_t3_1_, afeactione0_.update_timestamp as update_t4_1_ from afedb.afe_action afeactione0_ where afeactione0_.action=?
Hibernate: insert into afedb.afe_orders_history (action_id, create_timestamp, fixed_order_id) values (?, ?, ?)
El proceso fue exitoso para la orden: 6
Calling logEvent service
EventVO [source=ms.ackgm_hdm, status=0, msg=El proceso fue exitoso para la orden: 6, file=]
LogEvent created with Status Code: 302 FOUND
Message: null
End:: finalFlow
