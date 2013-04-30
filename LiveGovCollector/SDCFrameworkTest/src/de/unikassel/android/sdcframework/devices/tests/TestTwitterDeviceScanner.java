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
package de.unikassel.android.sdcframework.devices.tests;

import java.util.Iterator;
import java.util.Queue;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.TwitterSampleData;
import de.unikassel.android.sdcframework.devices.TwitterDevice;
import de.unikassel.android.sdcframework.devices.TwitterDeviceScanner;
import de.unikassel.android.sdcframework.provider.TwitterProvider;
import de.unikassel.android.sdcframework.provider.TwitterProviderData;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;

/**
 * Test for the twitter device scanner
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTwitterDeviceScanner extends
    ProviderTestCase2< TwitterProvider >
{
  /**
   * the mock content resolver
   */
  private ContentResolver resolver;
  
  /**
   * Constructor
   */
  public TestTwitterDeviceScanner()
  {
    super( TwitterProvider.class,
        TwitterProviderData.getInstance().getAuthority() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.ProviderTestCase2#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    resolver = getMockContext().getContentResolver();
    emptyDatabase();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  public void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Method to empty the database
   */
  private void emptyDatabase()
  {
    TwitterProvider provider = getProvider();
    provider.getDbHelper().getWritableDatabase().delete(
        provider.getProviderData().getDBTableName(), null, null );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.TwitterDeviceScanner#TwitterDeviceScanner(android.content.ContentResolver)}
   * and
   * {@link de.unikassel.android.sdcframework.devices.TwitterDeviceScanner#doNotifyForSamples(android.content.ContentResolver)}
   * .
   */
  public final void testTwitterDeviceScanner()
  {
    TwitterProvider provider = getProvider();
    ContentProviderData providerData = provider.getProviderData();
    
    // create a device with samples with time stamp
    TwitterDevice device = new TwitterDevice( getMockContext() );
    device.getConfiguration().setEnabled( true );
    
    // create a sample observer
    SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    // create an enabled scanner
    TwitterDeviceScanner scanner =
        new TwitterDeviceScanner( resolver );
    scanner.registerEventObserver( observer );
    scanner.setDevice( device, getContext() );
    
    // test if scanner was started as expected
    assertTrue( "Expected scanner running", scanner.isEnabled() );
    
    // send sample to data sink
    ContentValues values = new ContentValues();
    Long ts1 = System.currentTimeMillis();
    String msg1 = "message 1";
    values.put( ContentProviderData.TIMESTAMP, ts1.toString() );
    values.put( ContentProviderData.SYNCED, 1 );
    values.put( TwitterProviderData.MESSAGE, msg1 );
    resolver.insert( providerData.getContentUri(), values );
    values = new ContentValues();
    Long ts2 = ts1 + 1;
    String msg2 = "message 1";
    values.put( ContentProviderData.TIMESTAMP, ts2.toString() );
    values.put( ContentProviderData.SYNCED, 0 );
    values.put( TwitterProviderData.MESSAGE, msg2 );
    resolver.insert( providerData.getContentUri(), values );
    
    // trigger notification manually as the mock content resolver does stub out
    // the change notification
    // (compare
    // http://developer.android.com/guide/topics/testing/testing_android.html#MockObjectClasses)
    Queue< Long > rowIds = scanner.doNotifyForSamples( resolver );
    
    // test for samples taken
    int sampleCount = observer.observedEvents.size();
    assertEquals( "Expected more samples taken", 2, sampleCount );
    
    Iterator< Sample > it = observer.observedEvents.iterator();
    Sample sample = it.next();
    assertTrue( "Expected twitter sensor data contained",
        sample.getData() instanceof TwitterSampleData );
    Sample lastSample = it.next();
    assertTrue( "Expected twitter sensor data contained",
        lastSample.getData() instanceof TwitterSampleData );
    long timediff = lastSample.getTimeStamp() - sample.getTimeStamp();
    assertTrue( "Expected different timestamps", timediff > 0 );
    assertEquals( "Expected right priority set",
        device.getConfiguration().getSamplePriority().ordinal(),
        sample.getPriority() );
    assertEquals( "Expected right priority set",
        device.getConfiguration().getSamplePriority().ordinal(),
        lastSample.getPriority() );
    assertEquals( "Expected twitter message set", msg1,
        ( (TwitterSampleData) sample.getData() ).getMessage() );
    assertEquals( "Expected twitter message set", msg2,
        ( (TwitterSampleData) lastSample.getData() ).getMessage() );
    
    // test for deletion of gathered data
    scanner.doDeleteGatheredData( resolver, rowIds );
    Cursor cursor =
        resolver.query( providerData.getContentUri(), null, null, null,
            null );
    assertEquals( "Expected 0 samples in database", 0, cursor.getCount() );
    cursor.close();
    
    // stop scanner
    scanner.setDevice( null, getMockContext() );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
  }
}
