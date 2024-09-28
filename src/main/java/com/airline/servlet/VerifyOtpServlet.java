package com.airline.servlet;

// VerifyOtpServlet.java
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/VerifyOtpServlet")
public class VerifyOtpServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String enteredOtp = request.getParameter("otp");
		HttpSession session = request.getSession();
		String email = (String) session.getAttribute("email"); // You can carry this from the previous page/session

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration", "root", "Snehaprasad");

			String hashOtp = hashOtp(enteredOtp);

			// Validate OTP and check expiration
			String query = "SELECT * FROM users WHERE uemail=? AND otp=? AND otp_expiration > NOW()";
			ps = con.prepareStatement(query);
			ps.setString(1, email);
			ps.setString(2, hashOtp);
			rs = ps.executeQuery();

			if (rs.next()) {
				// OTP is valid, redirect to password reset page
				response.sendRedirect("jsp/passwordreset.jsp");
			} else {
				response.getWriter().println("Invalid or expired OTP!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				ps.close();
				con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String hashOtp(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashedBytes = md.digest(password.getBytes());
		return Base64.getEncoder().encodeToString(hashedBytes); // Convert to Base64 encoded string
	}

}