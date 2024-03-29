# aws-lambda-spring-boot-container-poc serverless API with Java 11
The aws-lambda-spring-boot-container-poc project, created with [`aws-serverless-java-container`](https://github.com/awslabs/aws-serverless-java-container).

The starter project defines a simple `/ping` resource that can accept `GET` requests with its tests.
This project uses Java 11, you need to have jdk 11 installed and configured.

The project folder also includes a `sam.yaml` file. You can use this [SAM](https://github.com/awslabs/serverless-application-model) file to deploy the project to AWS Lambda and Amazon API Gateway or test in local with [SAM Local](https://github.com/awslabs/aws-sam-local).

Using [Maven](https://maven.apache.org/), you can create an AWS Lambda-compatible zip file simply by running the maven package command from the project folder.
```bash
$ mvn archetype:generate -DartifactId=aws-lambda-spring-boot-container-poc -DarchetypeGroupId=com.amazonaws.serverless.archetypes -DarchetypeArtifactId=aws-serverless-springboot2-archetype -DarchetypeVersion=1.4 -DgroupId=com.mooveit -Dversion=1.0-SNAPSHOT -Dinteractive=false
$ cd aws-lambda-spring-boot-container-poc
$ mvn clean package

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 6.546 s
[INFO] Finished at: 2018-02-15T08:39:33-08:00
[INFO] Final Memory: XXM/XXXM
[INFO] ------------------------------------------------------------------------
```

You can use [AWS SAM Local](https://github.com/awslabs/aws-sam-local) to start your project.

First, install SAM local:

```bash
$ brew tap aws/tap
$ brew install aws-sam-cli
```

Next, from the project root folder - where the `sam.yaml` file is located - start the API with the SAM Local CLI.

```bash
$ sam local start-api --template sam.yaml

...
Mounting com.amazonaws.serverless.archetypes.StreamLambdaHandler::handleRequest (java8) at http://127.0.0.1:3000/{proxy+} [OPTIONS GET HEAD POST PUT DELETE PATCH]
...
```

Using a new shell, you can send a test ping request to your API:

```bash
$ curl -s http://127.0.0.1:3000/ping | python -m json.tool

{
    "pong": "Hello, World!"
}
``` 

You can use the [AWS CLI](https://aws.amazon.com/cli/) to quickly deploy your application to AWS Lambda and Amazon API Gateway with your SAM template.

You will need an S3 bucket to store the artifacts for deployment. Once you have created the S3 bucket, run the following command from the project's root folder - where the `sam.yaml` file is located:

```
$ aws cloudformation package --template-file sam.yaml --output-template-file output-sam.yaml --s3-bucket <YOUR S3 BUCKET NAME>
Uploading to xxxxxxxxxxxxxxxxxxxxxxxxxx  6464692 / 6464692.0  (100.00%)
Successfully packaged artifacts and wrote output template to file output-sam.yaml.
Execute the following command to deploy the packaged template
aws cloudformation deploy --template-file /your/path/output-sam.yaml --stack-name <YOUR STACK NAME>
```

As the command output suggests, you can now use the cli to deploy the application. Choose a stack name and run the `aws cloudformation deploy` command from the output of the package command.
 
```
$ aws cloudformation deploy --template-file output-sam.yaml --stack-name ServerlessSpringApi --capabilities CAPABILITY_IAM
```

Once the application is deployed, you can describe the stack to show the API endpoint that was created. The endpoint should be the `ServerlessSpringApi` key of the `Outputs` property:

```
$ aws cloudformation describe-stacks --stack-name ServerlessSpringApi
{
    "Stacks": [
        {
            "StackId": "arn:aws:cloudformation:us-west-2:xxxxxxxx:stack/ServerlessSpringApi/xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx", 
            "Description": "AWS Serverless Spring API - com.amazonaws.serverless.archetypes::aws-serverless-springboot2-archetype", 
            "Tags": [], 
            "Outputs": [
                {
                    "Description": "URL for application",
                    "ExportName": "AwsLambdaSpringBootContainerPocApi",
                    "OutputKey": "AwsLambdaSpringBootContainerPocApi",
                    "OutputValue": "https://xxxxxxx.execute-api.us-west-2.amazonaws.com/Prod/ping"
                }
            ], 
            "CreationTime": "2016-12-13T22:59:31.552Z", 
            "Capabilities": [
                "CAPABILITY_IAM"
            ], 
            "StackName": "ServerlessSpringApi", 
            "NotificationARNs": [], 
            "StackStatus": "UPDATE_COMPLETE"
        }
    ]
}

```

Copy the `OutputValue` into a browser or use curl to test your first request:

```bash
$ curl -s https://xxxxxxx.execute-api.us-west-2.amazonaws.com/Prod/ping | python -m json.tool

{
    "pong": "Hello, World!"
}
```

Follow the steps below if you want to run the Spring Boot app in a Docker container.

First build the docker image.
```build docker image
mvn clean install -P standalone
mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
docker build -t matteo-gallo-bb/aws-lambda-spring-boot-container-poc .
```

Run docker image in a Docker container
```run Docker
docker run -p 8080:8080 -t matteo-gallo-bb/aws-lambda-spring-boot-container-poc
```

Run docker image setting a Spring profile
```using Spring Profiles
$ docker run -e "SPRING_PROFILES_ACTIVE=dev" -p 8080:8080 -t matteo-gallo-bb/aws-lambda-spring-boot-container-poc
```

Debug the Spring Boot app in a Docker container
```debugging the application in a Docker container
$ docker run -e "JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n" -p 8080:8080 -p 5005:5005 -t matteo-gallo-bb/aws-lambda-spring-boot-container-poc
```

It's interesting to compare the performances (running in local) between this POC (java 8 lambda with Spring) and a normal lambda that does the same REST call.

```
java11      - cold -> Init Duration: 1086.50 ms   Duration: 1126.33 ms    Billed Duration: 1200 ms    Memory Size: 128 MB     Max Memory Used: 78 MB 
java11      - warm -> Init Duration: 886.54 ms    Duration: 994.98 ms     Billed Duration: 1000 ms    Memory Size: 128 MB     Max Memory Used: 78 MB

Spring Boot - cold -> Init Duration: 6029.70 ms   Duration: 1232.53 ms    Billed Duration: 1300 ms    Memory Size: 512 MB     Max Memory Used: 110 MB 
Spring Boot - warm -> Init Duration: 5667.28 ms   Duration: 1024.92 ms    Billed Duration: 1100 ms    Memory Size: 512 MB     Max Memory Used: 109 MB 
```

To better understand those numbers we should consider that we are doing a REST call to a mock endpoint and it takes between 600ms and 1s.
Knowing this we can come to the conclusion that most of the duration time is due to the REST call and there is no much difference between the two.
The real difference is in the initialization time and memory usage where Spring has a negative impact, making this solution not viable for most of the use cases.