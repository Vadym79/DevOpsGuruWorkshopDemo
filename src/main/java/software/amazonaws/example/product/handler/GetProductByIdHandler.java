// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazonaws.example.product.dao.DynamoProductDao;
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Product;

public class GetProductByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final ProductDao productDao = new DynamoProductDao();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(GetProductByIdHandler.class);

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		String id = requestEvent.getPathParameters().get("id");
		try {
			Optional<Product> optionalProduct = productDao.getProduct(id);
			
			if (optionalProduct.isEmpty()) {
				context.getLogger().log(" product with id " + id + "not found ");
				return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.NOT_FOUND)
						.withBody("Product with id = " + id + " not found");
			}
			logger.info(" product " + optionalProduct.get() + " found ");
			if(Integer.valueOf(id) > 100) {
			    this.publishSNSTopic(optionalProduct.get());
			}
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.OK)
					.withBody(objectMapper.writeValueAsString(optionalProduct.get()));
		} catch (Exception je) {
			logger.info("got error during get product by id "+je.getMessage());
			throw new RuntimeException("rethrow exception ",je);
			//return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
				//	.withBody("Internal Server Error :: " + je.getMessage());
		}
	}
	
	private void publishSNSTopic(Product product) {
		SnsClient snsClient = SnsClient.builder().region(Region.EU_CENTRAL_1).build();
		String topicArn= System.getenv("INVOCATION_TOPIC_URL");
		System.out.println("topicARN "+topicArn);
		String message=null;
		try {
			message = objectMapper.writeValueAsString(product);
		} catch (JsonProcessingException e) {

		}
		try {
            PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(topicArn)
                .build();

            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());

         } catch (SnsException e) {
            System.out.println(e.awsErrorDetails().errorMessage());

         }
	}
}