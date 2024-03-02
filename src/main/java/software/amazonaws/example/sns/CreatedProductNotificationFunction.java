package software.amazonaws.example.sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SNSEvent.SNSRecord;

public class CreatedProductNotificationFunction implements RequestHandler<SNSEvent, Void> {

	@Override
	public Void handleRequest(SNSEvent event, Context context) {
		for (SNSRecord record : event.getRecords()) {
			System.out.println("sns message: " + record.getSNS().getMessage());
			//throw new NumberFormatException("For input string: \"id: 3\"");
		}
		return null;

	}
}