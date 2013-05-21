package de.uni_koblenz.west.mobile_sensing;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ShowData
 */
public class ShowData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<title>Data Monitor</title>");
		writer.println("<script src=\"http://code.jquery.com/jquery-1.9.1.min.js\"></script>");
		writer.println("<link rel=\"stylesheet\" href=\"http://cdn.leafletjs.com/leaflet-0.4.5/leaflet.css\" />");
		writer.println("<script src=\"http://cdn.leafletjs.com/leaflet-0.4.5/leaflet.js\"></script>");
	    writer.println("<script src=\"ShowData.js\"></script>");

		writer.println("</head>");
		writer.println("<body>");
		writer.println("<a href=\"index.html\"><img src=\"static/livGovLogo.png\"/></a>");
		
		 Pattern p = Pattern.compile("^[a-zA-Z0-9-]+$");
		 Matcher m = p.matcher(request.getParameter("uuid"));
		 if(m.matches()) {
				writer.println("<div id=\"map\" style=\"width: 700px; height: 400px; position: relative;\"></div>");
				writer.println("<script>initilize('" + request.getParameter("uuid") + "');</script>");
		 }
		 else {
			 writer.println("Please enter a valid device ID!");
		 }		
		writer.println("<div><br/><table id=\"overview\"></table></div>");
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
