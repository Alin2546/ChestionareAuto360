const stripe = Stripe('pk_live_51RE6YoFaV5Kx4nqr3WpvqqaZNXmaTZIGydvogZDFlrzDtyYEJOkqGVFNTePGDNaM8nfgWAdtKbvRJlOTOdwL0CLn00TuFjKT3h');

function donate(amount) {
    fetch("/create-checkout-session", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ amount: amount })
    })
        .then(res => res.json())
        .then(data => {
            console.log("Checkout session response:", data);
            if (!data.id) {
                throw new Error("No session id returned from server");
            }
            return stripe.redirectToCheckout({ sessionId: data.id });
        })
        .catch(err => console.error(err));
}