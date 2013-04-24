package de.unikassel.sdcframework;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class SDCFSimpleServer
{
	private static final int PORT = 8081;

	/**
	 * The archive processor
	 */
	private final ArchiveProcessor archiveProcessor;
	
	/**
	 * The task executor
	 */
	private final ScheduledExecutorService executor;
	
	/**
	 * The processor handle of the archive processing runnable
	 */
	private final ScheduledFuture< ? > processorHandle;
	
	/**
	 * Constructor
	 */
	public SDCFSimpleServer( String path, String keyFileName )
	{
		super();
		if ( keyFileName != null )
		{
			archiveProcessor = new ArchiveProcessor( new File(keyFileName), path );
			
		}
		else
		{
			archiveProcessor = new ArchiveProcessor( path );
		}
		executor = Executors.newSingleThreadScheduledExecutor();
		processorHandle = executor.scheduleWithFixedDelay( archiveProcessor, 1, 15, TimeUnit.SECONDS );
	}
	
	/**
	 * Constructor
	 */
	public SDCFSimpleServer( String path )
	{
		this( path, null );
	}

	/**
	 * @param args
	 */
	public static void main( String[] args )
	{
		int result = 1;

		boolean displayHelp = true;
		Options options = new Options();

		// Add i option
		Option opt = new Option( "?", "help", false, "print this help message" );
		options.addOption( opt );
		opt = new Option( "key", "keyfile", false, "the rsa private key file" );
		opt.setArgs( 1 );
		opt.setRequired( false );
		options.addOption( opt );
		opt = new Option( "p", "path", false, "the working directory path" );
		opt.setArgs( 1 );
		opt.setRequired( true );
		options.addOption( opt );

		// Handle the command line parameters.
		// start the post processing thread
		SDCFSimpleServer processor = null;
		try
		{

			// Create a parser, and add to it the appropriate Options.
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args );

			String keyFileName = cmd.getOptionValue( "key" );
			String path = cmd.getOptionValue( "p" );
			
			if( keyFileName != null && !new File( keyFileName ).exists() )
			{
				System.err.println( "Key file does not exist" );
			}
			else
			{
				// try to start post processing
				displayHelp = cmd.hasOption( "?" );
				try
				{
					// create the asynchrounous task
					processor = new SDCFSimpleServer( path, keyFileName );
				}
				catch ( Exception e )
				{
					System.err.println( "Unexpected Error: "
							+ e.getMessage() );
					e.printStackTrace();
					System.exit( 2 );
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			result = 2;
			displayHelp = true;
		}
		
		if ( displayHelp )
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( SDCFSimpleServer.class.getSimpleName(),
					options );
			System.exit( result );
		}

		// start the server
		Server server = new Server( PORT );
		WebAppContext root = new WebAppContext();
		ClassLoader classLoader = Server.class.getClassLoader();
		URL resource = classLoader.getResource( "webapp" );
		String webappDir = resource.toExternalForm();
		System.out.println( webappDir );
		root.setWar( webappDir );
		root.setContextPath( "/" );

		server.setHandler( root );

		try
		{
			server.start();
			server.join();
			processor.processorHandle.cancel( true );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.exit( 2 );
		}
	}

}
