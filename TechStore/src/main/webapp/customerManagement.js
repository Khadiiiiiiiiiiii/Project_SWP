document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".delete_button").forEach(button => {
        button.addEventListener("click", function () {
            const userId = this.getAttribute("data-user-id");

            if (confirm("Are you sure you want to delete this customer?")) {
                fetch(`DeleteCustomerServlet?userId=${userId}`, {
                    method: "POST"
                })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                alert("Customer deleted successfully!");
                                document.getElementById(`row-${userId}`).remove();
                            } else {
                                alert("Failed to delete customer: " + (data.error || "Unknown error"));
                            }
                        })
                        .catch(error => {
                            console.error("Error:", error);
                            alert("An error occurred while deleting.");
                        });
            }
        });
    });
});
