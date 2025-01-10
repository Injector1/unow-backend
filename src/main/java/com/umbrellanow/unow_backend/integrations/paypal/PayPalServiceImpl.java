package com.umbrellanow.unow_backend.integrations.paypal;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.exceptions.HttpException;
import com.paypal.orders.AmountWithBreakdown;
import com.paypal.orders.ApplicationContext;
import com.paypal.orders.Order;
import com.paypal.orders.OrderRequest;
import com.paypal.orders.OrdersCaptureRequest;
import com.paypal.orders.OrdersCreateRequest;
import com.paypal.orders.PurchaseUnitRequest;
import com.paypal.payments.CapturesRefundRequest;
import com.paypal.payments.Money;
import com.paypal.payments.RefundRequest;
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


    @Override
    public Map<String, String> createDepositOrder(double depositAmount) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
        PurchaseUnitRequest purchaseUnit = new PurchaseUnitRequest()
                .amountWithBreakdown(
                        new AmountWithBreakdown()
                                .currencyCode("EUR")
                                .value(String.valueOf(depositAmount))
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

    @Override
    public boolean capturePayment(String orderId) throws IOException {
        try {
            OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
            request.requestBody(new OrderRequest());
            Order order = payPalHttpClient.execute(request).result();

            return "COMPLETED".equalsIgnoreCase(order.status());
        } catch (HttpException ex) {
            if (ex.getMessage().contains("ORDER_ALREADY_CAPTURED")) {
                System.out.println("Order already captured: " + orderId);  // TODO: replace with more robust logging
                return true;
            } else {
                throw ex;
            }
        }
    }

    @Override
    public boolean refundPayment(String captureId, double refundAmount) throws IOException {
        RefundRequest refundRequest = new RefundRequest()
                .amount(new Money()
                        .currencyCode("EUR")
                        .value(String.valueOf(refundAmount)));
        CapturesRefundRequest request = new CapturesRefundRequest(captureId);
        request.requestBody(refundRequest);

        return payPalHttpClient.execute(request).statusCode() == 201;
    }
}
