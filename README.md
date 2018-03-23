# HttpFarm - A time-based HTTP job scheduler

It's like cron, because it uses the same crontab expression, but it's dedicated to HTTP jobs. A convenient REST management API lets you create/view/delete jobs and their detailed execution log.

## Features
- In memory storage (Persistent storage planned in future versions)
- REST/JSON management API (Create/View/Delete jobs and executions)
- ~16mo JAR with 0 dependencies.
- Asynchronous I/O enables hundred of concurrent job executions on modest hardware

## Why not just use cron?
- Errors are swallowed unless execution output is piped to a notification system
- No first class support for HTTP. 
- No clean execution log history. You have to grep syslog.

## Installation
Download the latest release. Run it with
`java -jar httpfarm.jar`

HttpFarm will start polling for due jobs every minute.
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
`POST /jobs`

### View a job
`GET /jobs/$UUID`

### Delete a job
`DELETE /jobs/$UUID`
