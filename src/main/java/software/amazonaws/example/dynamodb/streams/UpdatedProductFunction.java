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
			Map<String, AttributeValue> oldImageMap = record.getDynamodb().getOldImage();
			System.out.println("old image map " + oldImageMap);

			Map<String, AttributeValue> newImageMap = record.getDynamodb().getNewImage();
			System.out.println("new image map " + newImageMap);
			
			int productId=Integer.valueOf(newImageMap.get("PK").getS());
			System.out.println("product id "+productId);
			if(productId >=500 && productId<550) {
				throw new NumberFormatException("For input string: id: "+productId);
			}
		}
		 return null;
	}

}
