// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import software.amazonaws.example.product.aurora.entity.Product;

public class GetProductByIdViaAuroraServerlessV2WithoutDataApiHandler
		implements RequestHandler<APIGatewayProxyRequestEvent, Optional<Product>> {

	private static final Logger logger = LoggerFactory.getLogger(GetProductByIdViaAuroraServerlessV2WithoutDataApiHandler.class);

	@Override
	public Optional<Product> handleRequest(APIGatewayProxyRequestEvent event, Context context) {
		final String id = event.getPathParameters().get("id");
		String dbEndpoint = System.getenv("DB_ENDPOINT");
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
				PreparedStatement preparedStatement = this.createprePreparedStatement(connection, sql, id);
				ResultSet rs = preparedStatement.executeQuery()) {
			if (rs.next()) {
				Long productId = rs.getLong("id");
				String name = rs.getString("name");
				BigDecimal price = rs.getBigDecimal("price");
				Product product = new Product(productId, name, price);
				logger.info("product found:  " + product);
				return Optional.of(product);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			logger.info("error message " + ex.getMessage());
			throw new RuntimeException("rethrow exception ",ex);
		}
		return Optional.empty();
	}
	
	private PreparedStatement createprePreparedStatement(Connection connection, String sql, String id) throws NumberFormatException, SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setLong(1, Long.valueOf(id));
		return preparedStatement;
	}
}