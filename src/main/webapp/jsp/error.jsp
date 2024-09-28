<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" href="images/logo.jpg" type="image/x-icon">
    <title>
        <% 
        String status = (String) request.getAttribute("status");
        String bookingFailed = (String) request.getAttribute("booking_failed");
        
        if ("failed".equals(status)) { 
            out.print("Error: No Seats Available"); 
        } else if ("booking_failed".equals(bookingFailed)) { 
            out.print("Error: Booking Failed"); 
        } else { 
            out.print("Airline Reservation System"); 
        }
        %>
    </title>
    <!-- JS Libraries -->
    <script src="https://cdn.jsdelivr.net/npm/jquery@3.6.0/dist/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert@2.1.2/dist/sweetalert.min.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert@2.1.2/dist/sweetalert.css">
</head>
<body>
    <!-- Hidden inputs to pass status messages -->
    <input type="hidden" id="status" value="<%= status != null ? status : "" %>">
    <input type="hidden" id="booking_failed" value="<%= bookingFailed != null ? bookingFailed : "" %>">

    <!-- Cancel Button -->
    <button id="cancelButton">Cancel</button>

    <!-- Alerts Based on Status -->
    <script type="text/javascript">
        var status = document.getElementById("status").value;
        var booking_failed = document.getElementById("booking_failed").value;

        console.log("Status:", status);
        console.log("Booking Failed:", booking_failed);

        // Event listener for cancel button
        document.getElementById("cancelButton").addEventListener("click", function() {
            console.log("Cancel button clicked!"); // Debugging output
            swal({
                title: "Are you sure?",
                text: "Do you want to cancel the booking?",
                icon: "warning",
                buttons: true,
                dangerMode: true,
            }).then((willCancel) => {
                if (willCancel) {
                    console.log("User confirmed cancellation."); // Debugging output
                    window.location.href = "index.jsp";
                } else {
                    swal("Booking process will continue.");
                }
            });
        });

        if (status === "failed") {
            swal("Sorry", "No Seats Available", "error")
            .then(() => {
                window.location.href = "index.jsp";
            });
        } else if (booking_failed === "booking_failed") {
            swal("Sorry", "Booking Failed", "error")
            .then(() => {
                window.location.href = "index.jsp";
            });
        }
    </script>
</body>
</html>
