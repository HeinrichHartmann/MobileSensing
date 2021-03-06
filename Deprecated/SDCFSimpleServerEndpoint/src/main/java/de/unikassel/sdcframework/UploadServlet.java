package de.unikassel.sdcframework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UploadServlet
 */
public class UploadServlet extends HttpServlet
{
	/**
	 * The serial version id
	 */
	private static final long serialVersionUID = -34222475977712947L;
	
	/**
	 * The log directory
	 */
	private String archive_dir;

	/**
	 * Default constructor.
	 */
	public UploadServlet()
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
		
		archive_dir = getServletConfig().getInitParameter( "localpath" );
		Long time = System.currentTimeMillis();
		
		String format = "unknown";
		MimeType type;
		try
		{
			type = new MimeType( request.getContentType() );
			format = type.getSubType();
		}
		catch ( MimeTypeParseException e1 )
		{
			e1.printStackTrace();
		}

		try
		{
			String uuid = request.getHeader( "uuid" );
			String filePath = new StringBuffer( archive_dir ).append( uuid )
					.toString();

			String fileName = new StringBuffer(archive_dir).append(uuid)
					.append("-").append(time).append(".").append(format).toString();

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
					response.setStatus( HttpServletResponse.SC_OK );
				}
				finally
				{
					out.close();
					in.close();
				}
			}
		}
		catch ( Exception e )
		{
			response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage() );
		}
	}
}
