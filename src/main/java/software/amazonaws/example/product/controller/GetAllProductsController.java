// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.controller;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import software.amazonaws.example.product.dao.ProductDao;
import software.amazonaws.example.product.entity.Products;

@Controller
public class GetAllProductsController {

  private static final Logger logger = LoggerFactory.getLogger(GetAllProductsController.class);
  private final ProductDao productDao;

  public GetAllProductsController(ProductDao productDao) {
    this.productDao = productDao;
  }

  @Get("/products")
  public Products getAllProducts() {
    logger.info("Entering GetProducts Method !!");
    
    /*
    try {
		Thread.sleep(10000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	*/
   
     // return new Products(new ArrayList<>());
     return productDao.getAllProduct();
    
  }

}
