package com.chestionare.chestionare360.Controller;




import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class StripeController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private static final String SUCCESS_URL = "https://chestionareauto360.onrender.com/donation/success?session_id={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL = "http://chestionareauto360.onrender.com/donation/cancel";

    @PostMapping("/create-checkout-session")
    @ResponseBody
    public Map<String, Object> createCheckoutSession(@RequestBody Map<String, Object> data,
                                                     @AuthenticationPrincipal UserDetails userDetails) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        final Set<Integer> validAmounts = Set.of(1, 3, 10, 50);
        int amountInEuro = 1;

        try {
            Object amountObj = data.get("amount");
            if (amountObj instanceof Number) {
                amountInEuro = ((Number) amountObj).intValue();
            } else if (amountObj != null) {
                amountInEuro = Integer.parseInt(amountObj.toString());
            }
        } catch (Exception ignored) {}

        if (!validAmounts.contains(amountInEuro)) {
            amountInEuro = 1;
        }

        long amountInCents = amountInEuro * 100L;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Donație ChestionareAuto360")
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);

        return Map.of("id", session.getId());
    }
}
