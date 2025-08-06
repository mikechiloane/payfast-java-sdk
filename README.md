# PayFast Java SDK

A comprehensive Java SDK for PayFast payment gateway integration with proper signature validation and PayFast-compliant URL encoding.

## Features

- ✅ **One-off payments** - Single payment transactions
- ✅ **Subscription payments** - Recurring billing support  
- ✅ **Automatic signature generation** - Python-compatible URL encoding
- ✅ **ITN validation** - Instant Transaction Notification handling
- ✅ **Sandbox/Production modes** - Easy environment switching
- ✅ **Comprehensive error handling** - Specific exceptions for different scenarios
- ✅ **HTML form generation** - Ready-to-use payment forms
- ✅ **Full PayFast compliance** - Matches official Python implementation

## Installation

Add to your `pom.xml`:
```xml
<dependency>
    <groupId>com.recceda</groupId>
    <artifactId>payfast-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### 1. Configuration
```java
import com.recceda.payfast.PayFastClient;
import com.recceda.payfast.config.PayFastConfig;

// Sandbox configuration
PayFastConfig config = new PayFastConfig(
    "10000100",           // merchant ID
    "46f0cd694581a",      // merchant key  
    null,                 // passphrase (optional)
    true                  // sandbox mode
);

PayFastClient client = new PayFastClient(config);
```

### 2. One-off Payment
```java
import com.recceda.payfast.model.PaymentRequest;
import com.recceda.payfast.model.PayFastResponse;
import java.math.BigDecimal;

PaymentRequest payment = new PaymentRequest();
payment.setAmount(new BigDecimal("100.00"));
payment.setItemName("Demo Product");
payment.setItemDescription("Product description");
payment.setMPaymentId("ORDER-" + System.currentTimeMillis());

// Optional: Set return URLs
payment.setReturnUrl("https://yoursite.com/return");
payment.setCancelUrl("https://yoursite.com/cancel");
payment.setNotifyUrl("https://yoursite.com/notify");

// Optional: Set buyer information
payment.setNameFirst("John");
payment.setNameLast("Doe");
payment.setEmailAddress("john.doe@example.com");

PayFastResponse response = client.createPayment(payment);
if (response.isSuccess()) {
    // Redirect user to response.getPaymentUrl()
    String paymentUrl = response.getPaymentUrl();
    // Also saves HTML form as payfast_payment_form.html
}
```

### 3. Subscription Payment
```java
import com.recceda.payfast.model.SubscriptionRequest;

SubscriptionRequest subscription = new SubscriptionRequest();
subscription.setAmount(new BigDecimal("50.00"));           // Initial amount
subscription.setItemName("Monthly Subscription");
subscription.setItemDescription("Premium service subscription");
subscription.setMPaymentId("SUB-" + System.currentTimeMillis());

// Subscription specific settings
subscription.setSubscriptionType("1");                     // Subscription
subscription.setRecurringAmount(5000);                     // 50.00 in cents
subscription.setFrequency(3);                             // Monthly (1=Weekly, 2=BiWeekly, 3=Monthly, 4=Quarterly, 5=BiAnnual, 6=Annual)
subscription.setCycles(12);                               // 12 months (0 = infinite)

PayFastResponse response = client.createSubscription(subscription);
if (response.isSuccess()) {
    String subscriptionUrl = response.getPaymentUrl();
    // Also saves HTML form as payfast_subscription_form.html
}
```

### 4. ITN (Instant Transaction Notification) Handling
```java
import com.recceda.payfast.model.NotificationData;
import java.util.Map;

// In your notification endpoint (e.g., Spring Controller)
@PostMapping("/payfast/notify")
public ResponseEntity<String> handleITN(HttpServletRequest request) {
    try {
        // Extract parameters from the ITN request
        Map<String, String> itnParams = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 0) {
                itnParams.put(key, values[0]);
            }
        });
        
        // Validate the ITN
        if (client.getITNHandler().validateITN(itnParams)) {
            // Parse the notification data
            NotificationData data = client.getITNHandler().parseNotification(itnParams);
            
            // Process the payment confirmation
            String paymentStatus = data.getPaymentStatus();
            String paymentId = data.getMPaymentId();
            BigDecimal amount = data.getAmountGross();
            
            // Your business logic here
            processPaymentConfirmation(paymentId, paymentStatus, amount);
            
            return ResponseEntity.ok("OK");
        } else {
            log.warn("Invalid ITN received");
            return ResponseEntity.badRequest().body("Invalid ITN");
        }
    } catch (Exception e) {
        log.error("ITN processing failed", e);
        return ResponseEntity.status(500).body("Error processing ITN");
    }
}
```

## Demo Application

Run the included demo to see the SDK in action:

```bash
mvn exec:java -Dexec.mainClass="com.recceda.App"
```

This will:
- Create a sample payment form
- Create a sample subscription form  
- Generate HTML files you can open in your browser
- Display PayFast sandbox URLs for testing

## Error Handling

The SDK provides comprehensive error handling with specific exceptions:

```java
import com.recceda.payfast.exception.*;

try {
    PayFastResponse response = client.createPayment(payment);
} catch (ConfigurationException e) {
    // Invalid configuration (missing merchant ID/key)
    log.error("Configuration error: {}", e.getMessage());
} catch (ValidationException e) {
    // Invalid request data (missing required fields, invalid amounts, etc.)
    log.error("Validation error: {}", e.getMessage());
} catch (SignatureException e) {
    // Signature generation or validation errors
    log.error("Signature error: {}", e.getMessage());
} catch (HttpException e) {
    // HTTP communication errors
    log.error("HTTP error: {}", e.getMessage());
} catch (PayFastException e) {
    // Base exception for all other PayFast operations
    log.error("PayFast error: {}", e.getMessage());
}
```

## Configuration Options

### PayFast Frequency Values
- `1` - Weekly
- `2` - Bi-weekly  
- `3` - Monthly
- `4` - Quarterly
- `5` - Bi-annually
- `6` - Annually

### PayFast Test Credentials
For sandbox testing, use these credentials:
- **Merchant ID**: `10000100`
- **Merchant Key**: `46f0cd694581a`
- **Sandbox URL**: `https://sandbox.payfast.co.za/eng/process`

## Technical Implementation

### Signature Generation
This SDK implements PayFast's signature generation algorithm with:
- **Python-compatible URL encoding** using `urllib.parse.quote_plus` equivalent
- **Proper parameter ordering** matching PayFast's official Python implementation
- **Spaces encoded as '+'** (not '%20') per PayFast requirements
- **Uppercase hex encoding** for special characters

### HTML Form Generation
The SDK automatically generates HTML payment forms that:
- Include all required PayFast parameters
- Have proper signatures for validation
- Auto-submit via JavaScript (optional)
- Save to descriptive filenames for debugging

## Testing

Run the comprehensive test suite:

```bash
mvn test
```

The SDK includes:
- **146 test cases** covering all functionality
- Unit tests for all components
- Integration tests with real PayFast examples
- Signature validation tests
- Error handling tests

## Requirements

- **Java 8+**
- **Maven 3.6+**
- **SLF4J** for logging

## Production Setup

For production use:

1. **Get real credentials** from PayFast merchant portal
2. **Set sandbox to false**:
   ```java
   PayFastConfig config = new PayFastConfig(
       "your-merchant-id",
       "your-merchant-key", 
       "your-passphrase",
       false  // production mode
   );
   ```
3. **Configure proper return URLs** for your domain
4. **Set up ITN endpoint** for payment confirmations
5. **Use HTTPS** for all URLs

## Support

- **PayFast Documentation**: https://developers.payfast.co.za/
- **PayFast Support**: https://www.payfast.co.za/support/

## License

MIT License - see LICENSE file for details

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality  
4. Ensure all tests pass
5. Submit a pull request

---

**Note**: This SDK has been thoroughly tested and validated against PayFast's official requirements. The signature generation matches PayFast's Python implementation exactly, ensuring full compatibility with their payment gateway.