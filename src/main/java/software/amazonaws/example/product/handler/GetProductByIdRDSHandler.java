// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.handler;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micronaut.http.annotation.Controller;
import software.amazon.awssdk.http.HttpStatusCode;
import software.amazonaws.example.product.entity.Product;

@Controller
public class GetProductByIdRDSHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
	  try
	  {
		  //String id = requestEvent.getPathParameters().get("id");		    		    
		  String dbEndpoint= System.getenv("DB_ENDPOINT");
		  System.out.println("db endpoint env: "+dbEndpoint);
		
		  String JDBC_PREFIX = "jdbc:postgresql://"; 
		  
		  //String dbEndpoint = "devopsgurudemoproductsapi-mydb-zeflgq9zsxao.cz4c3ydzsnta.eu-central-1.rds.amazonaws.com";
		  String portNumber = "5432";
		  String databasename = "DevOpsGuruWorkshop";
		  String username = "postgres";
		  String password = "postgres";
		  String url = JDBC_PREFIX + dbEndpoint+":"+portNumber+"/"+databasename;
		 
		  Connection connection = null;
		  try 
		  {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, username, password); 
		  } 
		  catch (Exception e) 
		  {
				e.printStackTrace();
				System.out.println("error message, rethrow "+e.getMessage());	
				throw e;
		  }
		    
		  if (connection!=null)
		  {
		    //Try to read from the rds database
		    ResultSet rs;
		    Statement statement = connection.createStatement();
		    rs = statement.executeQuery("select setting from pg_settings where name='max_connections'");
		    while ( rs.next() ) 
		    {
		        String setting = rs.getString("setting");
		        System.out.println("max connection "+setting);
		        Thread.sleep(3000);
		    }	     
		      
		 }    
	  }
	  catch (Exception ex)
	  {
		ex.printStackTrace();
		System.out.println("error message, then rethrow"+ex.getMessage());
		throw new RuntimeException(ex);
	  }
	  
	  try {
		return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.OK)
					.withBody(objectMapper.writeValueAsString(new Product("1", "Dummy Product", BigDecimal.valueOf(1.99))));
   	   } catch (JsonProcessingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
	  return new APIGatewayProxyResponseEvent().withStatusCode(HttpStatusCode.NOT_FOUND)
				.withBody("Product with id =  not found");
	     
  }
}