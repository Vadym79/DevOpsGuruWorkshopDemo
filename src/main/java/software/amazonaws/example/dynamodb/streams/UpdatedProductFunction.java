package software.amazonaws.example.dynamodb.streams;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;

public class UpdatedProductFunction implements RequestHandler<DynamodbEvent, Void> {

	@Override
	public Void handleRequest(DynamodbEvent input, Context context) {
		System.out.println("num of records: " + input.getRecords().size());
		for (DynamodbStreamRecord record : input.getRecords()) {
			Map<String, AttributeValue> keyValue = record.getDynamodb().getNewImage();
			System.out.println("map " + keyValue);

			//throw new NumberFormatException("For input string: \"id: 3\"");
			/*
			 * double id = Double.valueOf(keyValue.get("id").getS());
			 * System.out.println("id "+id);
			 */
		}
		return null;
	}

}
