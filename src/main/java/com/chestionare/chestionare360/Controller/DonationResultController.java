package com.chestionare.chestionare360.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DonationResultController {

    @GetMapping("/donation/success")
    public String donationSuccess() {
        return "donationSuccess";
    }

    @GetMapping("/donation/cancel")
    public String donationCancel() {
        return "donationCancel";
    }
}
