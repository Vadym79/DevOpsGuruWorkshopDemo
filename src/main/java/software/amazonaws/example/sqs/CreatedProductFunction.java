package software.amazonaws.example.sqs;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;


public class CreatedProductFunction implements RequestHandler<SQSEvent, Void>  {

	@Override
	public Void handleRequest(SQSEvent input, Context context) {
		System.out.println("event body "+input.getRecords().get(0).getBody());
		try {
			Thread.sleep(18000);
		} catch (InterruptedException e) {
			
		}
		return null;
	}

}
