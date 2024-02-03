package com.hire10x.OrderService.service;

import com.hire10x.OrderService.model.OrderRequest;
import com.hire10x.OrderService.model.OrderResponse;

public interface OrderService {

	long placeOrder(OrderRequest orderRequest);

	OrderResponse getOrderDetails(long orderId);

}
