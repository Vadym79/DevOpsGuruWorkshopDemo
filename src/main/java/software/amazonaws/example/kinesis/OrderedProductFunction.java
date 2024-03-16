package software.amazonaws.example.kinesis;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazonaws.example.product.entity.Product;

public class OrderedProductFunction implements RequestHandler<KinesisEvent, Void> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public Void handleRequest(KinesisEvent input, Context context) {
		System.out.println("num of records: " + input.getRecords().size());
		for (KinesisEventRecord record : input.getRecords()) {
			ByteBuffer byteBuffer = record.getKinesis().getData();
			String productRecordAsJson= Charset.forName("UTF-8").decode(byteBuffer).toString();
			System.out.println("Record " + productRecordAsJson);
			
			Product product=null;
			try {
				product = objectMapper.readValue(productRecordAsJson, Product.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("product "+product);
			int productId=Integer.valueOf(product.getId());
			if(productId >=200 && productId<250) {
				throw new NumberFormatException("For input string: id: "+productId);
			}
			
		}
		return null;
	}

}
