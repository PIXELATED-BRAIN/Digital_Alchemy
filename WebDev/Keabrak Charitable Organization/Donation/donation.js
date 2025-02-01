// donation.js
document.getElementById("submitDonation").addEventListener("click", async () => {
    // Get form inputs
    const donorName = document.getElementById("donorName").value;
    const amount = document.getElementById("donationAmount").value;
    const email = document.getElementById("email").value;

    // Validate inputs
    if (!donorName || !amount || amount <= 0) {
        alert("Please provide a valid name and donation amount.");
        return;
    }

    try {
        // Replace this URL with the actual SantimPay API endpoint
        const santimPayApiUrl = "https://santimpay.example.com/api/v1/donate";

        // Send the donation request
        const response = await fetch(santimPayApiUrl, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer YOUR_API_KEY", // Replace with your actual API key
            },
            body: JSON.stringify({
                name: donorName,
                amount: amount,
                currency: "ETB", // Ethiopian Birr
                email: email,
                description: "Donation to Keabrak Charitable Organization",
                callbackUrl: "https://yourwebsite.com/donation-success", // Replace with your success URL
            }),
        });

        const result = await response.json();

        // Handle the response
        if (response.ok) {
            // Redirect the user to the payment page or display a success message
            alert("Redirecting to payment...");
            window.location.href = result.paymentUrl; // Update based on API response
        } else {
            alert("Donation failed: " + result.message);
        }
    } catch (error) {
        console.error("Error during donation:", error);
        alert("An error occurred while processing your donation.");
    }
});
