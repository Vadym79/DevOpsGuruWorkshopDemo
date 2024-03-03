// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Product;

@Controller
public class GetProductByIdController {

  private final ProductDao productDao;
  private static final Logger logger = LoggerFactory.getLogger(GetProductByIdController.class);	

  public GetProductByIdController(ProductDao productDao) {
    this.productDao = productDao;
  }
 
  @Get("/products/{id}")
  public Optional<Product> getProductById(@PathVariable String id) {
	
	/*
	try {
		Thread.sleep(20000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	*/
	 // return Optional.of(new Product()); 
	Optional<Product> product= productDao.getProduct(id);
    if(product.isPresent()) {
    	logger.info ("found product "+product.get());
    } else {
    	logger.info ("prouct with id "+id+ " not found");
    }
    	
	return product;
  }

}
