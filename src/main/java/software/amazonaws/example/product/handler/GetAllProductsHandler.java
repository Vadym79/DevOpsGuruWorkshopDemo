// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import software.amazonaws.example.product.dao.DynamoProductDao;
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Products;

public class GetAllProductsHandler implements RequestHandler<APIGatewayProxyRequestEvent, Products> {

	private static final ProductDao productDao = new DynamoProductDao();

	@Override
	public Products handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
		return productDao.getAllProduct();
	}
}
