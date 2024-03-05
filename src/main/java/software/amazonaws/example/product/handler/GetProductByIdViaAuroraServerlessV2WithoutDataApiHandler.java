// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.aurora.entity.Product;

public class GetProductByIdViaAuroraServerlessV2WithoutDataApiHandler
		implements RequestHandler<APIGatewayProxyRequestEvent,APIGatewayProxyResponseEvent> {

	private static final Logger logger = LoggerFactory.getLogger(GetProductByIdViaAuroraServerlessV2WithoutDataApiHandler.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		final String id = event.getPathParameters().get("id");
		final String dbEndpoint;
		String rds_proxy_endpoint=System.getenv("RDS_PROXY_ENDPOINT");
		
		if (rds_proxy_endpoint != null) {
			dbEndpoint=rds_proxy_endpoint;
		} else {
		    dbEndpoint = System.getenv("DB_ENDPOINT");
		}
		logger.info("db endpoint env: " + dbEndpoint);

		String userName = System.getenv("DB_USER_NAME");
		String userPassword = System.getenv("DB_USER_PASSWORD");
		// logger.info("name: "+userName+ " password: "+userPassword);

		String JDBC_PREFIX = "jdbc:postgresql://";
		String portNumber = "5432";
		String databasename = "postgres";
		String url = JDBC_PREFIX + dbEndpoint + ":" + portNumber + "/" + databasename;
		logger.info("url: " + url);
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.info("error message" + e.getMessage());
		}
		String sql = "select id, name, price from tbl_product where id=?";
		try (Connection connection = DriverManager.getConnection(url, userName, userPassword);
				PreparedStatement preparedStatement = this.createPreparedStatement(connection, sql, id);
				ResultSet rs = preparedStatement.executeQuery()) {
			if (rs.next()) {
				Long productId = rs.getLong("id");
				String name = rs.getString("name");
				BigDecimal price = rs.getBigDecimal("price");
				Product product = new Product(productId, name, price);
				logger.info("product found:  " + product);
				return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.OK)
						.withBody(objectMapper.writeValueAsString(product));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.info("error message " + ex.getMessage());
			throw new RuntimeException("rethrow exception ",ex);
		}
		return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.NOT_FOUND)
				.withBody("Product with id = " + id + " not found");
	}
	
	private PreparedStatement createPreparedStatement(Connection connection, String sql, String id) throws NumberFormatException, SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setLong(1, Long.valueOf(id));
		return preparedStatement;
	}
}