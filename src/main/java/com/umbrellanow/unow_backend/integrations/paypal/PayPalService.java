package com.umbrellanow.unow_backend.integrations.paypal;

import java.io.IOException;
import java.util.Map;

public interface PayPalService {
    Map<String, String> createPaymentOrder(double depositAmount) throws IOException;
    String capturePayment(String orderId) throws IOException;
    boolean refundPayment(String captureId, double refundAmount) throws IOException;
}
