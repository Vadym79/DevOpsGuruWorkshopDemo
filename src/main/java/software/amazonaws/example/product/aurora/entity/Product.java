// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

package software.amazonaws.example.product.aurora.entity;


import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.gson.annotations.SerializedName;


public class Product {
  private Long id;
  
  @SerializedName("name")
  private String name;
  
  @SerializedName("price")
  private BigDecimal price;

  public Product() {
  }

  public Product(Long id, String name, BigDecimal price) {
    this.id = id;
    this.name = name;
    setPrice(this.price = price);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price.setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  public String toString() {
    return "Product{" +
      "id='" + id + '\'' +
      ", name='" + name + '\'' +
      ", price=" + price +
      '}';
  }
}
