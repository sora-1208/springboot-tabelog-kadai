const stripe = Stripe('pk_test_51OCKZcK73e8cOAU5d6eUHJUOpoHiTGOfk8kFbCrnIAfyiELIXCkYTPol1idIfkaZ4eIISiHcX0us549Yo3CMyKr100gmYHaRyO');
const paymentButton = document.querySelector('#paymentButton');
 
paymentButton.addEventListener('click', () => {
	stripe.redirectToCheckout({
		sessionId: sessionId
	})
});