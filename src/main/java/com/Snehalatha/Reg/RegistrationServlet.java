package com.Snehalatha.Reg;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegistrationServlet
 */
@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String uname = request.getParameter("name");
		request.setAttribute("uname", uname);
		String uemail = request.getParameter("email");
		String upwd = request.getParameter("pass");
		String ucontact = request.getParameter("contact");
		RequestDispatcher dispatcher = null;
		Connection con = null;
		try {
			String hashPassword = hashPassword(upwd);
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Registration?useSSL=false", "root",
					"Snehaprasad");
			PreparedStatement pr = con.prepareStatement("insert into users(uname,upwd,uemail,umobile) values(?,?,?,?)");
			pr.setString(1, uname);
			pr.setString(2, hashPassword);
			pr.setString(3, uemail);
			pr.setString(4, ucontact);

			int rowcount = pr.executeUpdate();

			dispatcher = request.getRequestDispatcher("registration.jsp");

			if (rowcount > 0) {

				request.setAttribute("status", "success");
			} else {
				request.setAttribute("status", "failed");
			}

			dispatcher.forward(request, response);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String hashPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashedBytes = md.digest(password.getBytes());
		return Base64.getEncoder().encodeToString(hashedBytes); // Convert to Base64 encoded string
	}

}
