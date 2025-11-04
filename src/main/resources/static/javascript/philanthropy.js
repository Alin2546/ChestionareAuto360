const stripe = Stripe('pk_test_51RE6Yy2Xp6cnh4OlQolvY0GKAqmd09HIU8daGndb3ADEdnKfbW2p4GdKLGjzJ3eBZpJOYJnpcdSiVu8yvJnEOKMg00JtXqXOk0');

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