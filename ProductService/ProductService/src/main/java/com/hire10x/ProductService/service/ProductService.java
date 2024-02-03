package com.hire10x.ProductService.service;

import com.hire10x.ProductService.model.ProductRequest;
import com.hire10x.ProductService.model.ProductResponse;

public interface ProductService {

	long addProduct(ProductRequest productRequest);

	ProductResponse getProductById(long productId);

	void reduceQuantity(long productId, long quantity);

}
