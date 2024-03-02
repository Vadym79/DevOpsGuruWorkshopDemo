// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.util.List;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import software.amazonaws.example.product.dao.AuroraServerlessV2DataApiDao;
import software.amazonaws.example.product.aurora.entity.Product;

public class CreateProductsViaAuroraServerlessV2DataApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, List<Product>> {

	private static final AuroraServerlessV2DataApiDao auroraServerlessV2DataApiDao = new AuroraServerlessV2DataApiDao();
	

	@Override 
	public List<Product> handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		String body = event.getBody();
		
		if (body != null && !body.isEmpty()) {
			List<Product> products = new Gson().fromJson(body, new TypeToken<List<Product>>(){}.getType());
			System.out.println("deserialized products "+ products);
			if (products != null) {
				return auroraServerlessV2DataApiDao.createProducts(products);
			}
		}
		
		return null;
		
	}
	
	
}