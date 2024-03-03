// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import software.amazonaws.example.product.dao.DynamoProductDao;
import software.amazonaws.example.product.dao.ProductDao;

public class DeleteProductByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, Void> {

	private static final ProductDao productDao = new DynamoProductDao();

	@Override
	public Void handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		String id = requestEvent.getPathParameters().get("id");
		productDao.deleteProduct(id);
		return null;
	}

}
