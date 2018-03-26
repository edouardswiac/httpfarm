# HttpFarm - A time-based HTTP job scheduler [![Build Status](https://travis-ci.org/edouardswiac/httpfarm.svg?branch=master)](https://travis-ci.org/edouardswiac/httpfarm)

HTTP requests engine meets crontab. 

## Features
- Configurable HTTP method, headers, timeouts, retries.
- Basic task flow: trigger Job B after Job A succceeds. (TODO)
- Simple REST management API centered around two nouns: Jobs and JobExecutions
- Asynchronous I/O enables XXX of concurrent job executions on modest hardware  (TODO)
- ~16mo fat JAR with 0 dependencies. Requires Java 8+.

## Why not just use cron?
- Errors are swallowed unless execution output is piped to a notification system
- No first class support for HTTP. 
- No clean execution log history. You have to grep syslog.

## Installation
Download the latest release. Run it with
`java -jar httpfarm.jar`

The REST API will be available on port `8080`. HttpFarm will start polling for due jobs every minute.
```
23:30:41,267 [main] INFO  io.httpfarm.Main - Welcome to HTTPFarm!
23:30:41,271 [main] INFO  io.httpfarm.Main - Press ctrl+c to exit.
23:30:41,615 [vert.x-eventloop-thread-0] INFO  io.httpfarm.JobPoller - polling for due jobs every 60 seconds
23:30:41,624 [vert.x-eventloop-thread-0] INFO  io.httpfarm.JobPoller - polling for due jobs...
23:30:41,668 [vert.x-eventloop-thread-0] INFO  io.httpfarm.JobPoller - found 1 jobs ready
23:30:42,200 [vert.x-eventloop-thread-2] INFO  io.httpfarm.HttpExecutor - [2a170465-5154-40d9-b62c-ca8b4941c03c] starting (GET https://httpbin.org/anything)
23:30:42,735 [vert.x-eventloop-thread-2] INFO  io.httpfarm.HttpExecutor - [2a170465-5154-40d9-b62c-ca8b4941c03c] complete (GET https://httpbin.org/anything)
``` 

## Configuration

### Add a new job
    curl --request POST \
      --url http://localhost:8080/jobs/ \
      --header 'content-type: application/json' \
      --data '{
            "url": "https://httpbin.org/html",
            "headers": {},
            "cronExpr": "* * * * *",
            "timeoutMillis": 5000,
            "maxTries": 2,
            "method": "GET"}'


### List jobs
    curl http://localhost:8080/jobs
      
### View a job and its executions
    curl  http://localhost:8080/jobs/$job_uuid
    {
    "executions": [
        {
          "uuid": "e6785d8b-61ae-48dd-8d2c-d5b34a7f331d",
          "url": "https://httpbin.org/html",
          "jobUuid": "97de3373-7ffa-47f2-91d0-f11161848154",
          "start": 1522028484547,
          "end": 1522028484706,
          "requestHeaders": {},
          "responseBody": "[SNIP]",
          "responseHeaders": {
            "X-Processed-Time": "0",
            "Server": "meinheld/0.6.1",
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Credentials": "true",
            "Connection": "close",
            "Content-Length": "3741",
            "Date": "Mon, 26 Mar 2018 01:41:24 GMT",
            "Via": "1.1 vegur",
            "X-Powered-By": "Flask",
            "Content-Type": "text/html; charset=utf-8"
          },
          "responseStatusCode": 200,
          "retryFrom": null,
          "error": null
        },
        {
          "uuid": "c79911f1-470d-4c68-96a1-08bff8c170c2",
          "url": "https://httpbin.org/html",
          "jobUuid": "97de3373-7ffa-47f2-91d0-f11161848154",
          "start": 1522028424593,
          "end": 1522028424885,
          "requestHeaders": {},
          "responseBody": "[SNIP]",
          "responseHeaders": {
            "X-Processed-Time": "0",
            "Server": "meinheld/0.6.1",
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Credentials": "true",
            "Connection": "close",
            "Content-Length": "3741",
            "Date": "Mon, 26 Mar 2018 01:40:24 GMT",
            "Via": "1.1 vegur",
            "X-Powered-By": "Flask",
            "Content-Type": "text/html; charset=utf-8"
          },
          "responseStatusCode": 200,
          "retryFrom": null,
          "error": null
        }]
    }
### Delete a job
    curl --request DELETE http://localhost:8080/jobs/$job_uuid

