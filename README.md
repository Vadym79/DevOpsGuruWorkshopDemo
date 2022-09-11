# DevOps Guru Workshop Java Based Serverless Application

## Deployment

Github example:https://github.com/Vadym79/DevOpsGuruWorkshopDemo 

Deploy the demo to your AWS account using [AWS SAM](https://aws.amazon.com/serverless/sam/).


```bash
mvn clean package
sam deploy -g
```
SAM will create an output of the API Gateway endpoint URL for future use in our load tests. 

## Install Hey Tool for the Load Test

Go to AWS Cloud Cloud9 Service and click "create envirnonment" with the name like serverlessdays-hamburg-devops-guru-workshop
and agreeing upon other default configutation (e.g. t2.micro instance).

Cloud9 environment has AWS CLI, AWS SAM and Go (required for running the Hey tool)

Install Hey tool (https://github.com/rakyll/hey)  by running in the bash console

```bash
curl -sf https://gobinaries.com/rakyll/hey | sh
```

##  Load Testing examples

### Warm up the getProductById function Lambda function for 2 hours

```bash
hey -q 1 -z 120m -c 1 -H "X-API-Key: a6ZbcDefQW12BN56WEM6" https://gx4q0xu5kd.execute-api.eu-central-1.amazonaws.com/prod/products/1
```

### Load test the getProductById to provoke DynamoDb or Lambda throttling  errors 

```bash
hey -q 10 -z 10m -c 5 -H "X-API-Key: a6ZbcDefQW12BN56WEM6" https://gx4q0xu5kd.execute-api.eu-central-1.amazonaws.com/prod/products/1
```

### Load test the getProductById to provoke API Gateway 4XX errors (401 not found)

```bash
hey -q 10 -z 10m -c 5 -H "X-API-Key: a6ZbcDefQW12BN56WEM6" https://gx4q0xu5kd.execute-api.eu-central-1.amazonaws.com/prod/products/200
```


### Load test the deleteProduct to provoke Lambda function has concurrency spillover error

```bash
hey -q 1 -z 20m -c 9  -m DELETE -H "X-API-Key: a6ZbcDefQW12BN56WEM6" -H  "Content-Type: application/json;charset=utf-8" https://gx4q0xu5kd.execute-api.eu-central-1.amazonaws.com/prod/products/11
```



