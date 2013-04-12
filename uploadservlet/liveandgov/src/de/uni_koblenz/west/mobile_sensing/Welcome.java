package de.uni_koblenz.west.mobile_sensing;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Welcome
 */
@WebServlet("/Welcome")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Welcome() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<head><title>Welcome</title></head>");
		writer.println("<body>");
		writer.println("<img src=\"static/livGovLogo.png\"/>");
		writer.println("Please input your device ID:<br/>");
		writer.println("<form action=\"ShowData\" method=\"get\">");
		writer.println("<input type=\"text\" name=\"uuid\">");
		writer.println("<input type=\"submit\" value=\"Submit\">");
		writer.println("</form>");
		writer.println("<body>");
		writer.println("</html>");
			
		writer.close();	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
