# DevOps Guru Workshop Java Based Serverless Application

## Prerequisites

You need Java 11, Maven, Go, Hey tool, AWS CLI (configured) and AWS SAM installed to build and deploy this application.
Or use AWS Cloud 9 server where you only need to install Maven to build and run the application and Hey tool to do a load test.


## Deployment

[Github example](https://github.com/Vadym79/DevOpsGuruWorkshopDemo) inspired by [Lambda demo with common Java application frameworks](https://github.com/aws-samples/serverless-java-frameworks-samples)
 
Deploy the demo to your AWS account using [AWS SAM](https://aws.amazon.com/serverless/sam/).


```bash

Clone git repositoy localy
git clone https://github.com/Vadym79/DevOpsGuruWorkshopDemo.git

Compile and package the Java application with Maven from the root (where pom.xml is located) of the project
mvn clean package

Or use already packaged artifact from target/micronaut-lambda-function-1.0.0-SNAPSHOT.jar
In order for SAM to deploy this application, the micronaut-lambda-function-1.0.0-SNAPSHOT.jar should be placed in the subfolder called target.
Please see template.yaml configuration and CodeUri: of the Lambda function 

Deploy your application with AWS SAM
sam deploy -g
```
SAM will create an output of the API Gateway endpoint URL for future use in our load tests.
Please also check you API Key. I'll need both: API Gateway Endpoint URL and API Key to use the application properly.

## Enable DevOps Guru

Go to the AWS DevOps Guru Service -> Settings -> Analyzed Resources and then choose "CloudFormation Stacks"
and then select DevOpsGuruDemoProductsAPi  

## Create some Demo Products

1) Login into your AWS account and to to the API Gateway Service

2) Select DevOpsGuruDemoProductsAPI and in the category Resources under /products click PUT Method execution

3) Click on Test and then add product with id =1 like this

 3.1)  In the Path {id} field enter value "1"  

  3.2) In the "Request Body" below enter

[
    {
      "id": 1,
      "name": "Print 10x13",
      "price": 0.15
    }
]

 3.3) Click on the "Test" button and check that the result was HTTP 200. Also go into the DynamoDB ProductTable and check the created items there

4) Create some other products. Make sure that the value in the Path {id} exactly matches the "id" value in the "Request Body"

## Install Hey Tool for the Load Test

Go to AWS Cloud Cloud9 Service and click "create environnment" with the name like serverlessdays-hamburg-devops-guru-workshop
and agreeing upon other default configutation (e.g. t2.micro instance).

Cloud9 environment has AWS CLI, AWS SAM and Go (required for running the Hey tool)

Install [Hey Tool](https://github.com/rakyll/hey) by running in the bash console

```bash
curl -sf https://gobinaries.com/rakyll/hey | sh
```

##  Load Testing examples

### Warm up the getProductById function Lambda function for 2 hours

```bash
hey -q 1 -z 120m -c 1 -H "X-API-Key: a6ZbcDefQW12BN56WEM7" https://ax4q0xu5ka.execute-api.eu-central-1.amazonaws.com/prod/products/1
```

### Load test the getProductById to provoke DynamoDB or Lambda throttling  errors 

```bash
hey -q 10 -z 10m -c 10 -H "X-API-Key: a6ZbcDefQW12BN56WEM7" https://ax4q0xu5ka.execute-api.eu-central-1.amazonaws.com/prod/products/1
```

### Load test the getProductById to provoke API Gateway 4XX errors (401 not found)

```bash
hey -q 10 -z 10m -c 5 -H "X-API-Key: a6ZbcDefQW12BN56WEM7" https://ax4q0xu5ka.execute-api.eu-central-1.amazonaws.com/prod/products/200
```


### Load test the deleteProduct to provoke Lambda function has concurrency spillover error

```bash
hey -q 1 -z 20m -c 9  -m DELETE -H "X-API-Key: a6ZbcDefQW12BN56WEM7" -H  "Content-Type: application/json;charset=utf-8" https://ax4q0xu5ka.execute-api.eu-central-1.amazonaws.com/prod/products/11
```



