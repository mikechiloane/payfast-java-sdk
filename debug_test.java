import com.recceda.payfast.lambda.PayFastPaymentFormHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.recceda.payfast.model.PaymentRequest;
import java.math.BigDecimal;

public class DebugTest {
    public static void main(String[] args) {
        System.setProperty("PAYFAST_MERCHANT_ID", "10000100");
        System.setProperty("PAYFAST_PASSPHRASE", "46f0cd694581a");
        System.setProperty("PAYFAST_SANDBOX", "true");
        
        PayFastPaymentFormHandler handler = new PayFastPaymentFormHandler() {
            @Override
            protected PaymentRequest buildPaymentRequest(JsonNode requestData) {
                PaymentRequest payment = new PaymentRequest();
                payment.setMerchantId(getMerchantId());
                payment.setMerchantKey("test-key");
                payment.setAmount(new BigDecimal(requestData.get("amount").asText()));
                payment.setItemName(requestData.get("itemName").asText());
                payment.setMPaymentId("TEST-" + System.currentTimeMillis());
                payment.setReturnUrl("https://example.com/return");
                payment.setCancelUrl("https://example.com/cancel");
                payment.setNotifyUrl("https://example.com/notify");
                return payment;
            }
        };
        
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent();
        request.setBody("{\"amount\":\"100.00\",\"itemName\":\"Test Product\",\"format\":\"html\"}");
        
        APIGatewayProxyResponseEvent response = handler.handleRequest(request, null);
        
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Content-Type: " + response.getHeaders().get("Content-Type"));
        System.out.println("Body:");
        System.out.println(response.getBody());
    }
}
