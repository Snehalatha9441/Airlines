package com.airline.servlet;

// SendOtpServlet.java
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.airline.util.OTP;

@WebServlet("/SendOtpServlet")
public class SendOtpServelt extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String email = request.getParameter("email");
		HttpSession session = request.getSession();
		session.setAttribute("email", email);

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration", "root",
					"Snehaprasad");
			String query = "SELECT * FROM users where uemail=?";
			try (PreparedStatement ps = con.prepareStatement(query)) {
				ps.setString(1, email);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						// Generate OTP and set expiration time
						String otp = OTP.generateOTP();
						Timestamp expirationTime = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000); // 5 min
																												// expiry
						String hashOtp = hashOtp(otp);
						// Store OTP in the database
						String updateQuery = "UPDATE users SET otp=?, otp_expiration=? WHERE uemail=?";
						try (PreparedStatement updatePs = con.prepareStatement(updateQuery)) {
							updatePs.setString(1, hashOtp);
							updatePs.setTimestamp(2, expirationTime);
							updatePs.setString(3, email);
							updatePs.executeUpdate();
						}

						// Send OTP via email
						try {
							sendOtpEmail(email, otp);
							response.sendRedirect("jsp/otpverification.jsp");
						} catch (MessagingException e) {
							e.printStackTrace();
							response.getWriter().println("Failed to send OTP. Please try again.");
						}
					} else {
						response.getWriter().println("Email not found!");
					}
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			response.getWriter().println("Database error occurred.");
		}
	}

	private void sendOtpEmail(String email, String otp) throws MessagingException {
		final String username = "snehaswadha174@gmail.com"; // your Gmail address
		final String password = "pnlzaxtdaosqjrfa"; // the 16-character App password generated

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		message.setSubject("Your OTP for Password Reset");
		message.setText("Your OTP is: " + otp);

		Transport.send(message);
	}

	private String hashOtp(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashedBytes = md.digest(password.getBytes());
		return Base64.getEncoder().encodeToString(hashedBytes); // Convert to Base64 encoded string
	}
}