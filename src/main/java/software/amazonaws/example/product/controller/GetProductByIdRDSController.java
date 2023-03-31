// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import software.amazonaws.example.product.entity.Product;

@Controller
public class GetProductByIdRDSController {
 
  @Get("/productsFromRDS/{id}")
  public Optional<Product> getProductById(@PathVariable String id) throws Exception {
	  try
	  {
		  		    		    
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
		throw ex;
	  }
	  
	 return Optional.of(new Product("1", "Dummy Product", BigDecimal.valueOf(1.99)));
    
  }
}