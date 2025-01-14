package com.umbrellanow.unow_backend.integrations.paypal;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Capture;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnit;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.Refund;
import com.paypal.payments.RefundRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayPalServiceImpl implements PayPalService {
    @Autowired
    private PayPalHttpClient payPalHttpClient;


    @Transactional
    @Override
    public Map<String, String> createPaymentOrder(double amount) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .amountWithBreakdown(
                        new AmountWithBreakdown()
                                .currencyCode("EUR")
                                .value(String.valueOf(amount))
                );
        purchaseUnits.add(purchaseUnit);
        orderRequest.purchaseUnits(purchaseUnits);

        orderRequest.applicationContext(
                new ApplicationContext()
                        .brandName("UmbrellaNow")
                        .returnUrl("http://localhost:8100/paypal/success")
                        .cancelUrl("http://localhost:8100/paypal/cancel")
        );

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        Order order = payPalHttpClient.execute(request).result();

        String approvalLink = order.links().stream()
                .filter(link -> "approve".equals(link.rel()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No approval link found"))
                .href();

        Map<String, String> result = new HashMap<>();
        result.put("approvalLink", approvalLink);
        result.put("orderId", order.id());

        return result;
    }

    @Transactional
    @Override
    public String capturePayment(String orderId) throws IOException {
        try {
            OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
            HttpResponse<Order> response = payPalHttpClient.execute(request);
            Order order = response.result();

            if ("COMPLETED".equalsIgnoreCase(order.status())) {
                for (PurchaseUnit purchaseUnit : order.purchaseUnits()) {
                    for (Capture capture : purchaseUnit.payments().captures()) {
                        return capture.id();
                    }
                }
            }
            throw new RuntimeException("Payment capture failed: order not completed.");
        } catch (HttpException ex) {
            if (ex.getMessage().contains("ORDER_ALREADY_CAPTURED")) {
                System.out.println("Order already captured: " + orderId);
                throw new RuntimeException("Order already captured. Unable to process duplicate captures.");
            } else {
                throw ex;
            }
        }
    }

    @Transactional
    @Override
    public boolean refundPayment(String captureId, double refundAmount) throws IOException {
        // TODO: extract order id and save to our transactions
        RefundRequest refundRequest = new RefundRequest()
                .amount(new Money()
                        .currencyCode("EUR")
                        .value(String.valueOf(refundAmount)));
        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.requestBody(refundRequest);

        HttpResponse<Refund> response = payPalHttpClient.execute(request);

        if (response.statusCode() == 201) {
            System.out.println("Refund successful: " + captureId);
            return true;
        } else {
            System.out.println("Refund failed: " + response.statusCode());
            return false;
        }
    }
}
