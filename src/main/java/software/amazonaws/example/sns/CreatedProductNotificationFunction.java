package software.amazonaws.example.sns;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class CreatedProductNotificationFunction implements RequestHandler<Map<String, String>, Void> {

	@Override
	public Void handleRequest(Map<String, String> event, Context context) {
		String productId = event.get("productId");
		System.out.println("received product Id: " + productId);
		Integer productIdAsInt=Integer.valueOf(productId);
		System.out.println("product Id as int: " + productIdAsInt);
		//send email
		return null;
	}
}