package software.amazonaws.example.dynamodb.streams;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazonaws.example.product.entity.Product;


public class UpdatedProductFunction implements RequestHandler<DynamodbEvent, Void>  {

	@Override
	public Void handleRequest(DynamodbEvent input, Context context) {
		sendSQSMessage(null);
		
		 System.out.println("num of records: "+input.getRecords().size());
		for (DynamodbStreamRecord record: input.getRecords()) {
			 Map<String, AttributeValue>  keyValue=record.getDynamodb().getNewImage();
			 System.out.println("map "+keyValue);
			 
			 //throw new NumberFormatException("For input string: \"id: 3\"");
			 /* 
			 double id = Double.valueOf(keyValue.get("id").getS());
			 System.out.println("id "+id);
			 */
		}
		return null;
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
	
	}
