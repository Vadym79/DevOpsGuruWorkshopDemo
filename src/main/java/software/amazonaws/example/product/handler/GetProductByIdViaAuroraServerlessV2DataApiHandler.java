// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.aurora.entity.Product;
import software.amazonaws.example.product.dao.AuroraServerlessV2DataApiDao;

public class GetProductByIdViaAuroraServerlessV2DataApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final AuroraServerlessV2DataApiDao auroraServerlessV2DataApiDao = new AuroraServerlessV2DataApiDao();
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override 
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		final String id = event.getPathParameters().get("id");
		Optional<Product> optionalProduct =  auroraServerlessV2DataApiDao.getProductById(id);
		try {
			if (optionalProduct.isEmpty()) {
				context.getLogger().log(" product with id " + id + "not found ");
				return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.NOT_FOUND)
						.withBody("Product with id = " + id + " not found");
			}
			context.getLogger().log(" product " + optionalProduct.get() + "  found ");
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.OK)
					.withBody(objectMapper.writeValueAsString(optionalProduct.get()));
		} catch (Exception je) {
			je.printStackTrace();
			return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR)
					.withBody("Internal Server Error :: " + je.getMessage());
		}
	}
}