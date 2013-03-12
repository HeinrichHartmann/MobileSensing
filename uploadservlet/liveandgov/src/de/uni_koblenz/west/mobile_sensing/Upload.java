package de.uni_koblenz.west.mobile_sensing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LivegovServlet
 */
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Upload() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = request.getHeader("filename");
		String uuid = request.getHeader("uuid");
		Date date = new Date();
		DateFormat dateformat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat timeformat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		String datetimestring = dateformat.format(date)+"_"+timeformat.format(date);
		File outfile = new File("/srv/liveandgov/uploads/"+uuid+"_"+datetimestring+"_"+filename);
		OutputStream outstream = new FileOutputStream(outfile);
		InputStream instream = request.getInputStream();
		copyStream(instream, outstream);
		outstream.flush();
		outstream.close();
	}

	private void copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

}
