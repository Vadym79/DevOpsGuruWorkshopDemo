package software.amazonaws.example.kinesis;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;

public class OrderedProductFunction implements RequestHandler<KinesisEvent, Void> {

	@Override
	public Void handleRequest(KinesisEvent input, Context context) {
		System.out.println("num of records: " + input.getRecords().size());
		for (KinesisEventRecord record : input.getRecords()) {
			ByteBuffer byteBuffer = record.getKinesis().getData();
			String recordString = Charset.forName("UTF-8").decode(byteBuffer).toString();
			System.out.println("Record " + recordString);

			/*
			 * Integer productId=Integer.valueOf(recordString.substring(0,5));
			 * System.out.println("product id "+productId);
			 */

		}
		return null;
	}

}
