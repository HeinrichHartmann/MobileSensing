/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework (Sensor Data Collection Framework) project.
 *
 * The SDCFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The SDCFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the SDCFramework.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unikassel.android.sdcframework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.os.Environment;

import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.BasicAuthHttpProtocol;
import de.unikassel.android.sdcframework.transmission.ConnectionStrategyBuilder;
import de.unikassel.android.sdcframework.transmission.UnknownProtocol;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;
import de.unikassel.android.sdcframework.transmission.facade.UpdatableTransmissionComponent;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * A worker thread to realize a the global log file handling.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class LogfileManager
    extends AbstractWorkerThread
    implements
    UpdatableTransmissionComponent< TransmissionProtocolConfiguration >,
    EventObserver< AlarmEvent >
{
  /**
   * The delay for the automatic instance release ( to allow final log
   * information be still logged)
   */
  private static final long TERMINATION_DELAY = 30000L;
  
  /**
   * The log file name
   */
  private static final String FILE_NAME = File.separatorChar + "sdcf.log.";
  
  /**
   * Relative file path on the external storage media
   */
  public static final String RELATIVE_PATH =
      File.separatorChar + "sdcframework";
  
  /**
   * The environmental storage directory
   */
  private static final String STORAGE_DIR =
      Environment.getExternalStorageDirectory().getAbsolutePath()
              + RELATIVE_PATH;
  
  /**
   * The queue for log messages
   */
  private final ConcurrentLinkedQueue< LogEvent > logEvents;
  
  /**
   * The queue for files to transfer
   */
  private final ConcurrentLinkedQueue< String > transferFiles;
  
  /**
   * The strategy implementing the protocol
   */
  protected ProtocolStrategy protocolStrategy;
  
  /**
   * The connection strategy
   */
  private ConnectionStrategy connectionStrategy;
  
  /**
   * The wake lock holder instance
   */
  private final WakeLockHolder wakeLockHolder;
  
  /**
   * The uuid of the device
   */
  private final UUID uuid;
  
  /**
   * The observable wake up alarm
   */
  private final ObservableAlarm alarm;
  
  /**
   * The context
   */
  private final Context context;
  
  /**
   * The singleton instance
   */
  private static LogfileManager instance = null;
  
  /**
   * Getter for the instance
   * 
   * @return the log file transfer task instance
   */
  public final static synchronized LogfileManager getInstance()
  {
    return instance;
  }
  
  /**
   * Method to create the global instance
   * 
   * @param context
   *          the context
   * @param uuid
   *          the unique SDC installation identifier for this device
   */
  public final static synchronized void createInstance( Context context,
      UUID uuid )
  {
    releaseInstance();
    instance = new LogfileManager( context, uuid );
    instance.startWork();
  }
  
  /**
   * Method to destroy the global instance
   */
  private final static synchronized void releaseInstance()
  {
    if ( instance != null )
    {
      instance.doTerminate();
      instance = null;
    }
  }
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param uuid
   *          the unique SDC installation identifier for this device
   */
  private LogfileManager( Context context, UUID uuid )
  {
    super();
    setLogging( false );
    this.context = context;
    this.wakeLockHolder = new WakeLockHolder( context );
    Logger.getInstance().debug( this,
        wakeLockHolder.hashCode() + ": wake lock holder created" );
    this.uuid = uuid;
    this.logEvents = new ConcurrentLinkedQueue< LogEvent >();
    transferFiles = new ConcurrentLinkedQueue< String >();
    this.protocolStrategy = null;
    this.connectionStrategy = null;
    this.alarm = AlarmBuilder.createAlarm( this, context );
    this.alarm.onCreate( context );
    this.alarm.onResume( context );
    alarm.registerEventObserver( this );
  }
  
  /**
   * Method to prepare delayed instance release
   */
  public static synchronized void prepareReleaseInstance()
  {
    if ( instance != null )
    {
      instance.alarm.setAlarm( TERMINATION_DELAY );
    }
  }
  
  /**
   * Method to test for a valid configuration
   * 
   * @return true if upload configuration is available
   */
  protected final boolean isConfigured()
  {
    return protocolStrategy != null && protocolStrategy.getHost() != null;
  }
  
  /**
   * Method to add a log event
   * 
   * @param event
   *          the log event to add to the queue
   */
  public final void addLogEvent( LogEvent event )
  {
    logEvents.offer( event );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractWorkerThread#doCleanUp()
   */
  @Override
  protected final void doCleanUp()
  {
    alarm.cancelAlarm();
    alarm.unregisterEventObserver( this );
    alarm.onPause( context );
    alarm.onDestroy( context );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
   */
  @Override
  protected final void doWork()
  {
    // process log event if available
    LogEvent event = logEvents.poll();
    
    if ( event != null )
    {
      // just empty queue as long as external storage is not available
      while ( !isExternalStorageAvailable() && !logEvents.isEmpty() )
      {
        event = logEvents.remove();
      }
      
      if ( !saveLogEvent( event ) )
      {
        Logger.getInstance().error( this,
            "Failed to save log event: " + event.getLongMessage() );
      }
      
      if ( !transferFiles.isEmpty() && isConfigured() )
      {
        String file = transferFiles.peek();
        if ( uploadFile( file ) )
        {
          transferFiles.poll();
          Logger.getInstance().debug( this,
              "Successful log file transfer: \"" + file + "\"" );
        }
      }
    }
    else
    {
      try
      {
        Thread.sleep( 2000 );
      }
      catch ( InterruptedException e )
      {}
    }
  }
  
  /**
   * Me6thod to save a log event
   * 
   * @param event
   *          the log event to save to file
   * @return true if successful, false otherwise
   */
  private boolean saveLogEvent( LogEvent event )
  {
    String msg = event.getLongMessage() + '\n';
    
    FileOutputStream fos = null;
    try
    {
      File logFile =
          getLogFile( TimeProvider.getUTCDayTimeMillis( event.getTimeStamp() ) );
      fos = new FileOutputStream( logFile, true );
      fos.write( msg.getBytes() );
      fos.flush();
      return true;
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this,
          "Exception in saveLogEvent: " + e.getMessage() );
    }
    finally
    {
      if ( fos != null )
      {
        try
        {
          fos.close();
        }
        catch ( IOException e )
        {}
      }
    }
    return false;
  }
  
  /**
   * Getter for the actual log file
   * 
   * @param ts
   *          the time stamp
   * @return the current log file if accessible
   * @throws IOException
   */
  protected final File getLogFile( long ts ) throws IOException
  {
    if ( !isExternalStorageAvailable() )
    {
      throw new IOException( "External storage not availale!" );
    }
    
    File dir = FileUtils.fileFromPath( STORAGE_DIR );
    if ( ( dir.exists() || dir.mkdirs() ) && dir.isDirectory() )
    {
      // try to access the expected log file
      File file =
          FileUtils.fileFromPath( new StringBuffer( STORAGE_DIR ).append(
              FILE_NAME ).append(
              TimeProvider.toUTCDate( ts ) ).toString() );
      if ( !file.exists() )
      {
        // new log file will be created -> mark older files for transfer
        for ( File oldFile : dir.listFiles() )
        {
          transferFiles.offer( oldFile.getAbsolutePath() );
        }
      }
      return file;
    }
    throw new IOException( "Failed to create directory "
        + dir.getAbsolutePath() );
  }
  
  /**
   * Test method for availability of the external storage device
   * 
   * @return true if available, false otherwise
   */
  private boolean isExternalStorageAvailable()
  {
    return Environment.MEDIA_MOUNTED.equals( Environment.getExternalStorageState() );
  }
  
  /**
   * Does upload a file to the configured remote server using the given
   * authentication data
   * 
   * @param fileName
   *          the file to upload
   * @return true if successful, false otherwise
   */
  private synchronized final boolean uploadFile( String fileName )
  {
    if ( !isConfigured() )
      return false;
    
    boolean result = true;
    
    File file = FileUtils.fileFromPath( fileName );
    
    // simply ignore not existing files
    if ( file.exists() )
    {
      result = false;
      
      String currentFile =
          new StringBuffer( STORAGE_DIR ).append( FILE_NAME ).append(
              TimeProvider.toUTCDate( TimeProvider.getUTCDayTimeMillis() ) ).toString();
      
      // wake up device for time sync
      wakeLockHolder.acquireWakeLock();
      
      try
      {
        // wait a bit for connectivity
        try
        {
          Thread.sleep( 5000 );
        }
        catch ( InterruptedException e )
        {}
        
        protocolStrategy.setFileName( fileName );
        result = connectionStrategy.doWork( protocolStrategy );
        if ( result )
        {
          if ( !currentFile.equals( file.getAbsolutePath() ) )
          {
            file.delete();
          }
        }
      }
      finally
      {
        wakeLockHolder.releaseWakeLock();
      }
    }
    
    return result;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.facade.
   * UpdatableTransmissionComponent#updateConfiguration(android.content.Context,
   * de
   * .unikassel.android.sdcframework.preferences.facade.UpdatableConfiguration)
   */
  @Override
  public final synchronized void updateConfiguration( Context context,
      TransmissionProtocolConfiguration config )
  {
    this.connectionStrategy = ConnectionStrategyBuilder.buildStrategy( config );
    
    protocolStrategy = null;
    try
    {
      // determine protocol type
      URL url = new URL( config.getURL() );
      String protocol = url.getProtocol();
      
      if ( "http".equals( protocol ) )
      {
        // HTTP the only protocol we do support right now
        protocolStrategy = new BasicAuthHttpProtocol( context, uuid, config );
      }
    }
    catch ( MalformedURLException e )
    {}
    
    if ( protocolStrategy == null )
    {
      // unknown protocol
      protocolStrategy = new UnknownProtocol( context, uuid, config );
    }
  }
  
  /**
   * Static method to clear any available log file on the external storage media
   */
  public final static void clearAllLogs()
  {
    LogfileManager instance = LogfileManager.getInstance();
    File dir = FileUtils.fileFromPath( STORAGE_DIR );
    
    if ( dir.exists() && dir.isDirectory() )
    {
      for ( File file : dir.listFiles() )
      {
        if ( instance != null && instance.isConfigured() )
        {
          instance.transferFiles.offer( file.getAbsolutePath() );
        }
        else
        {
          file.delete();
        }
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de.
   * unikassel.android.sdcframework.util.facade.ObservableEventSource,
   * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void onEvent(
      ObservableEventSource< ? extends AlarmEvent > eventSource,
      AlarmEvent observedEvent )
  {
    releaseInstance();
  }
  
}
