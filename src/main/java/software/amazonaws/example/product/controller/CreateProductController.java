// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.controller;



import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import software.amazon.awssdk.core.SdkBytes;
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
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Product;

@Controller
public class CreateProductController {
	private final ProductDao productDao;

	public CreateProductController(ProductDao productDao) {
		this.productDao = productDao;
	}

	@Put("/products/{id}")
	public void createUpdateProduct(@PathVariable String id, @Body Product product) {
		product.setId(id);
		productDao.putProduct(product);
		Integer productId=Integer.valueOf(id);

		if(productId <= 100) { 
			sendSQSMessage(product);
			startSfnWorkflow(product);
		}
		else if (productId > 100 && productId < 200) {
			putKinesisDataStreamRecord(product);
		}
		else {
			publishSNSTopic(product);
		}
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
		
		String productRecord= "id: "+product.getId()+ "  name: "+product.getName()+ " price: "+product.getPrice();

		System.out.println("Putting product to kinesis data stream: "+productRecord);
		PutRecordRequest request = PutRecordRequest.builder().partitionKey(product.getId()) 
				.streamName("orderedProductDataStream").data(SdkBytes.fromUtf8String(productRecord)).build();

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
		String message= "id: "+product.getId()+ "  name: "+product.getName()+ " price: "+product.getPrice();
		try {
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(topicArn)
                .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

         } catch (SnsException e) {
            System.out.println(e.awsErrorDetails().errorMessage());
           
         }
	}
}