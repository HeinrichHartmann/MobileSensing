package de.unikassel.sdcframework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LogServlet
 */
public class LogServlet extends HttpServlet
{
	
	/**
	 * The serial version id
	 */
	private static final long serialVersionUID = -6826891573424475499L;
	
	/**
	 * The log directory
	 */
	private String log_dir;

	/**
	 * Default constructor.
	 */
	public LogServlet()
	{}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet( HttpServletRequest request,
			HttpServletResponse response ) throws ServletException, IOException
	{
		response.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED,
				"not supported" );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost( HttpServletRequest request,
			HttpServletResponse response ) throws ServletException, IOException
	{
		response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		
		log_dir = getServletConfig().getInitParameter( "localpath" );

		try
		{
			String uuid = request.getHeader( "uuid" );
			String filePath = new StringBuffer( log_dir ).append( uuid )
					.toString();
			String fileName = filePath + File.separatorChar
					+ request.getHeader( "filename" );

			File dir = new File( filePath );
			if ( ( dir.exists() && dir.isDirectory() ) || dir.mkdir() )
			{
				FileOutputStream out = new FileOutputStream(
						new File( fileName ) );
				ServletInputStream in = request.getInputStream();
				try
				{
					byte buffer[] = new byte[1024];
					int cnt = in.read( buffer );
					while ( cnt != -1 )
					{
						out.write( buffer, 0, cnt );
						cnt = in.read( buffer );
					}
				}
				finally
				{
					out.close();
					in.close();
				}
				response.setStatus( HttpServletResponse.SC_OK );
			}
		}
		catch ( Exception e )
		{
			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage() );
		}
	}
}
