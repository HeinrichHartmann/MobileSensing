package de.unikassel.sdcframework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging
 * 
 * @author Katy Hilgenberg
 * 
 */
public class LogUtil
{

	/**
	 * The slf4j logger
	 */
	private final Logger logger;

	/**
	 * Constructor
	 * 
	 * @param c
	 *            the class owning this logger
	 */
	public LogUtil( @SuppressWarnings( "rawtypes" ) Class c )
	{
		this.logger = LoggerFactory.getLogger( c );
	}

	/**
	 * Method to log a debug message
	 * 
	 * @param message
	 *            the message
	 */
	public void debug( String message )
	{
		if ( logger.isDebugEnabled() )
			this.logger.debug( message );
	}

	/**
	 * Method to log a info message
	 * 
	 * @param message
	 *            the message
	 */
	public void info( String message )
	{
		if ( logger.isInfoEnabled() )
			this.logger.info( message );
	}

	/**
	 * Method to log an warning message
	 * 
	 * @param message
	 *            the message
	 */
	public void warning( String message )
	{
		if ( logger.isWarnEnabled() )
			this.logger.warn( message );
	}

	/**
	 * Method to log an error message
	 * 
	 * @param message
	 *            the message
	 */
	public void error( String message )
	{
		this.logger.error( message );
	}
}
