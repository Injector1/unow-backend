package com.umbrellanow.unow_backend.integrations.paypal;

import com.paypal.core.PayPalHttpClient;
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
import java.util.List;

@Service
public class PayPalServiceImpl implements PayPalService {
    @Autowired
    private PayPalHttpClient payPalHttpClient;


    @Override
    public String createDepositOrder(double depositAmount) throws IOException {
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
                        .returnUrl("http://localhost:8081/paypal/success")
                        .cancelUrl("http://localhost:8081/paypal/cancel")
        );

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        Order order = payPalHttpClient.execute(request).result();

        return order.links().stream()
                .filter(link -> "approve".equals(link.rel()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No approval link found"))
                .href();
    }

    @Override
    public boolean capturePayment(String orderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(new OrderRequest());
        Order order = payPalHttpClient.execute(request).result();

        return "COMPLETED".equalsIgnoreCase(order.status());

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
