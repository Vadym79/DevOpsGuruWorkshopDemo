package software.amazonaws.example.sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazonaws.example.product.entity.Product;

public class CreatedProductNotificationFunction implements RequestHandler<SNSEvent, Void> {
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Void handleRequest(SNSEvent event, Context context) {
		for (SNSRecord record : event.getRecords()) {
			context.getLogger().log("sns message: " + record.getSNS().getMessage());
			
			Product product=null;
			try {
				product = objectMapper.readValue(record.getSNS().getMessage(), Product.class);
			} catch (Exception e) {
				
			}
			if (Integer.valueOf (product.getId()) >= 200) {
				throw new NumberFormatException("For input string: \"id: 3\"");
			}
		}
		return null;

	}
}