// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import software.amazonaws.example.product.aurora.entity.Product;

@Controller
public class GetProductByIdAuroraClusterController {
	
  private static final Logger logger = LoggerFactory.getLogger(GetProductByIdAuroraClusterController.class);	
 
  @Get("/productsFromAuroraCluster/{id}")
  public Optional<Product> getProductById(@PathVariable String id) throws Exception {
	  try
	  {
		  		    		    
		  String dbEndpoint= System.getenv("DB_ENDPOINT");
		  logger.info("db endpoint env: "+dbEndpoint);
		  
		  String userName= System.getenv("DB_USER_NAME");
		  String userPassword= System.getenv("DB_USER_PASSWORD");
		  //logger.info("name: "+userName+  " password: "+userPassword);
			  
		
		  String JDBC_PREFIX = "jdbc:postgresql://"; 
		  
		  //String dbEndpoint = "devopsgurudemoproductsapi-mydb-zeflgq9zsxao.cz4c3ydzsnta.eu-central-1.rds.amazonaws.com";
		  String portNumber = "5432";
		  String databasename = "postgres";
		  String url = JDBC_PREFIX + dbEndpoint+":"+portNumber+"/"+databasename;
		  logger.info("url: "+url);	
			 
		  Connection connection = null;
		  try 
		  {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, userName, userPassword); 
		  } 
		  catch (Exception e) 
		  {
				e.printStackTrace();
				logger.info("error message, rethrow "+e.getMessage());	
				throw e;
		  }
		    
		  if (connection!=null)
		  {
		    //Try to read from the rds database
		   
		    
		    String sql = "select id, name, price from tbl_product where id=?";

		    PreparedStatement preparedStatement =
		            connection.prepareStatement(sql);

		    preparedStatement.setLong(1, Long.valueOf(id));
		    ResultSet rs = preparedStatement.executeQuery();
		    while ( rs.next() ) 
		    {
		        Long productId= rs.getLong("id");
		        String name= rs.getString("name");
		        BigDecimal price= rs.getBigDecimal("price");
		        Product product=new Product(productId,name,price);
		        logger.info("product found:  "+product);
		        return Optional.of(product);
		        
		    }
		    /*
		     ResultSet rs; 
		    Statement statement = connection.createStatement(); 
		    rs = statement.executeQuery("select setting from pg_settings where name='max_connections'");
		    while ( rs.next() ) 
		    {
		        String setting = rs.getString("setting");
		        logger.info("max connection "+setting);
		        Thread.sleep(3000);
		    }
		    */	     
		      
		 }    
	  }
	  catch (Exception ex)
	  {
		ex.printStackTrace();
		logger.info("error message, then rethrow"+ex.getMessage());
		throw ex;
	  }
	  
	 return Optional.empty();
    
  }
}