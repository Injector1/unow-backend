package com.umbrellanow.unow_backend.integrations.paypal;

import java.io.IOException;

public interface PayPalService {
    String createDepositOrder(double depositAmount) throws IOException;
    boolean capturePayment(String orderId) throws IOException;
    boolean refundPayment(String captureId, double refundAmount) throws IOException;
}
