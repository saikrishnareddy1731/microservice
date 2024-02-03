package com.hire10x.PaymentService.service;

import com.hire10x.PaymentService.model.PaymentRequest;
import com.hire10x.PaymentService.model.PaymentResponse;

public interface PaymentService {

	Long doPayment(PaymentRequest paymentRequest);

	PaymentResponse getPaymentDetailsByOrderId(String orderId);

}
