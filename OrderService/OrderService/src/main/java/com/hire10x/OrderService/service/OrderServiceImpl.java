package com.hire10x.OrderService.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hire10x.OrderService.entity.Order;
import com.hire10x.OrderService.exception.CustomException;
import com.hire10x.OrderService.external.client.PaymentService;
import com.hire10x.OrderService.external.client.ProductService;
import com.hire10x.OrderService.external.request.PaymentRequest;
import com.hire10x.OrderService.external.response.PaymentResponse;
import com.hire10x.OrderService.external.response.ProductResponse;
import com.hire10x.OrderService.model.OrderRequest;
import com.hire10x.OrderService.model.OrderResponse;
import com.hire10x.OrderService.repository.OrderRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public long placeOrder(OrderRequest orderRequest) {
		// Order Entity -> Save the data with Status Order Created
		// Product Service - Block Products (Reduce the Quantity)
		// Payment Service -> Payments -> Success-> COMPLETE, Else
		// CANCELLED

		log.info("Placing Order Request: {}", orderRequest);

		productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

		log.info("Creating Order with Status CREATED");
		Order order = Order.builder().amount(orderRequest.getTotalAmount()).orderStatus("CREATED")
				.productId(orderRequest.getProductId()).orderDate(Instant.now()).quantity(orderRequest.getQuantity())
				.build();

		order = orderRepository.save(order);

		log.info("Calling Payment Service to complete the payment");
		PaymentRequest paymentRequest = PaymentRequest.builder().orderId(order.getId())
				.paymentMode(orderRequest.getPaymentMode()).amount(orderRequest.getTotalAmount()).build();

		String orderStatus = null;
		try {
			paymentService.doPayment(paymentRequest);
			log.info("Payment done Successfully. Changing the Oder status to PLACED");
			orderStatus = "PLACED";
		} catch (Exception e) {
			log.error("Error occurred in payment. Changing order status to PAYMENT_FAILED");
			orderStatus = "PAYMENT_FAILED";
		}

		order.setOrderStatus(orderStatus);
		orderRepository.save(order);

		log.info("Order Places successfully with Order Id: {}", order.getId());
		return order.getId();
	}

	@Override
	public OrderResponse getOrderDetails(long orderId) {
		log.info("Get order details for Order Id : {}", orderId);

		Order order = orderRepository.findById(orderId).orElseThrow(
				() -> new CustomException("Order not found for the order Id:" + orderId, "NOT_FOUND", 404));

		log.info("Invoking Product service to fetch the product for id: {}", order.getProductId());
		ProductResponse productResponse = restTemplate
				.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class);

		
        log.info("Getting payment information form the payment Service");
        PaymentResponse paymentResponse
                = restTemplate
                .getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
                );
		
        
        //for product details 
		OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails
				.builder()
				.productName(productResponse.getProductName())
				.productId(productResponse.getProductId())
				.build();
		
		// for payment
	      OrderResponse.PaymentDetails paymentDetails
          = OrderResponse.PaymentDetails
          .builder()
          .paymentId(paymentResponse.getPaymentId())
          .paymentStatus(paymentResponse.getStatus())
          .paymentDate(paymentResponse.getPaymentDate())
          .paymentMode(paymentResponse.getPaymentMode())
          .build();

	      // for order
		OrderResponse orderResponse = OrderResponse.builder().
				orderId(order.getId()).orderStatus(order.getOrderStatus())
				.amount(order.getAmount())
				.orderDate(order.getOrderDate())
				.productDetails(productDetails)
				.paymentDetails(paymentDetails)
				.build();

		return orderResponse;
	}

}
