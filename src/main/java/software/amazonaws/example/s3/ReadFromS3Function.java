package software.amazonaws.example.s3;

import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class ReadFromS3Function implements RequestHandler<APIGatewayProxyRequestEvent, Void> {
	
	private static final S3Client s3 = S3Client.builder().region(Region.EU_CENTRAL_1).build();

	@Override
	public Void handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		//getObjectBytes("265634257610-devops-guru-workshop", "test");
		processParallelyWithStream();
		return null;
	}
	
	public static void getObjectBytes(String bucketName, String keyName) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
           
            byte[] data = objectBytes.asByteArray();
            String content = new String(data, StandardCharsets.UTF_8);
            System.out.println("Successfully obtained bytes from an S3 object "+content);
        }  
         catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
	
	private static void processParallelyWithStream() {
	    IntStream.range(0, 1)
	      .parallel()
	      .forEach(i -> {
	         getObjectBytes("265634257610-devops-guru-workshop", "test"); 
	      });
	}
}