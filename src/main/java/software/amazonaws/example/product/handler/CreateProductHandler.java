// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.KinesisException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionResponse;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazonaws.example.product.dao.DynamoProductDao;
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Product;

public class CreateProductHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final ProductDao productDao = new DynamoProductDao();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		//try {
			String requestBody = requestEvent.getBody();
			Product product;
			try {
				product = objectMapper.readValue(requestBody, Product.class);
			} catch (Exception e) {
				e.printStackTrace();
				return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
						.withBody("Internal Server Error :: " + e.getMessage());
			}
			productDao.putProduct(product);
			Integer productId=Integer.valueOf(product.getId());

			if(productId <= 20) { 
				sendSQSMessage(product);
				startSfnWorkflow(product);
			}
			else if (productId > 20 && productId < 100) {
				sendSQSMessage(product);
			}
			else if (productId > 100 && productId < 200) {
				startSfnWorkflow(product);
			}
			else if (productId >= 200 && productId < 300) {
				putKinesisDataStreamRecord(product);
			}
			else if (productId >= 300 && productId < 400) {
				publishSNSTopic(product);
			}
			else {
				//publishEventBridge(product);
			}
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.CREATED)
					.withBody("Product with id = " + product.getId() + " created");
		/*
	    } catch (Exception e) {
			e.printStackTrace();
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
					.withBody("Internal Server Error :: " + e.getMessage());
		}
		*/
	}
	private void startSfnWorkflow(Product product) {
		final String json = 
		"{\n"+
		 "   \"productId\": \"" +product.getId()+  "\"\n"+
		"}\n";
		System.out.println(json);
		SfnClient sfnClient = SfnClient.builder().region(Region.EU_CENTRAL_1).build();
		String stateMachineArn= System.getenv("STATE_MACHINE_ARN");
		System.out.println("stateMachineARN "+stateMachineArn);
		StartExecutionRequest executionRequest = StartExecutionRequest.builder()
	                .input(json)
	                .stateMachineArn(stateMachineArn)

	                .build();

	    StartExecutionResponse response = sfnClient.startExecution(executionRequest);
	    System.out.println("response "+response.toString());   
	}

	private void sendSQSMessage(Product product) {
		SqsClient sqsClient = SqsClient.builder().region(Region.EU_CENTRAL_1).build();
		String queueArn= System.getenv("INVOCATION_QUEUE_URL");
		System.out.println("queueARN "+queueArn);

		sqsClient.sendMessage(SendMessageRequest.builder()
				//.queueUrl("https://sqs.eu-central-1.amazonaws.com/265634257610/new-product-created")
				.queueUrl(queueArn)
				.messageBody("created product with id " + product.getId()).delaySeconds(3).build());
	}

	private void putKinesisDataStreamRecord(Product product) {
		KinesisClient kinesisClient = KinesisClient.builder().region(Region.EU_CENTRAL_1).build();

		String productRecordAsJson=null;
		try {
			productRecordAsJson = objectMapper.writeValueAsString(product);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println("Putting product to kinesis data stream: "+productRecordAsJson);
		PutRecordRequest request = PutRecordRequest.builder().partitionKey(product.getId()) 
				.streamName("orderedProductDataStream").data(SdkBytes.fromUtf8String(productRecordAsJson)).build();

		try {
			kinesisClient.putRecord(request);
		} catch (KinesisException e) {
			System.err.println(e.getMessage());
		}
	}


	private void publishSNSTopic(Product product) {
		SnsClient snsClient = SnsClient.builder().region(Region.EU_CENTRAL_1).build();
		String topicArn= System.getenv("INVOCATION_TOPIC_URL");
		System.out.println("topicARN "+topicArn);
		String productRecordAsJson=null;
		try {
			productRecordAsJson = objectMapper.writeValueAsString(product);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		try {
            PublishRequest request = PublishRequest.builder()
                .message(productRecordAsJson)
                .topicArn(topicArn)
                .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

         } catch (SnsException e) {
            System.out.println(e.awsErrorDetails().errorMessage());

         }
	}

}