package band.gosrock.infrastructure.outer.api.tossPayments.client;


import band.gosrock.infrastructure.outer.api.tossPayments.config.TossHeaderConfig;
import band.gosrock.infrastructure.outer.api.tossPayments.dto.request.CreatePaymentsRequest;
import band.gosrock.infrastructure.outer.api.tossPayments.dto.response.PaymentsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tossCreatePayments",
        url = "https://api.tosspayments.com",
        configuration = TossHeaderConfig.class)
public interface TossPaymentsClient {

    @PostMapping("/v1/payments")
    PaymentsResponse createPayments(@RequestBody CreatePaymentsRequest createPaymentsRequest);

    @PostMapping("/v1/payments/confirm")
    PaymentsResponse confirmPayments(@RequestBody CreatePaymentsRequest createPaymentsRequest);

    // TODO : 멱등키 구현
    @PostMapping("/v1/payments/{paymentKey}/cancel")
    PaymentsResponse cancelPayments(@PathVariable("paymentKey") String paymentKey);

    @GetMapping("/v1/payments/orders/{orderId}")
    PaymentsResponse getTransactionByOrderId(@PathVariable("orderId") String orderId);

    @GetMapping("/v1/payments/{paymentKey}")
    PaymentsResponse getTransactionByPaymentKey(@PathVariable("paymentKey") String paymentKey);
}
