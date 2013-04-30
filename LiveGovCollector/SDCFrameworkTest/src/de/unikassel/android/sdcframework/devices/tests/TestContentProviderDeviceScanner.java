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
import de.unikassel.android.sdcframework.devices.VirtualSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.provider.test.AbstractProviderForTest;
import de.unikassel.android.sdcframework.provider.test.ContentProviderDataForTest;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.test.ProviderTestCase2;

/**
 * Test for the content provider device scanner base type.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestContentProviderDeviceScanner
    extends ProviderTestCase2< AbstractProviderForTest >
{
  /**
   * the mock content resolver
   */
  private ContentResolver resolver;
  
  /**
   * Constructor
   */
  public TestContentProviderDeviceScanner()
  {
    super( AbstractProviderForTest.class,
        new ContentProviderDataForTest().getAuthority() );
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
  
  /**
   * Method to empty the database
   */
  private void emptyDatabase()
  {
    AbstractProviderForTest provider = getProvider();
    provider.getDbHelper().getWritableDatabase().delete(
        provider.getProviderData().getDBTableName(), null, null );
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
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.ContentProviderDeviceScanner#start(android.content.Context)}
   * and *
   * {@link de.unikassel.android.sdcframework.devices.ContentProviderDeviceScanner#stop(android.content.Context)}
   * and
   * {@link de.unikassel.android.sdcframework.devices.ContentProviderDeviceScanner#doDeleteGatheredData(android.content.ContentResolver, java.util.Queue)}
   * .
   */
  public final void testContentProviderDeviceScanner()
  {
    AbstractProviderForTest provider = getProvider();
    ContentProviderData providerData = provider.getProviderData();
    
    // create a device with samples with time stamp
    VirtualSensorDevice device =
        new VirtualSensorDevice( SensorDeviceIdentifier.Unknown );
    device.getConfiguration().setEnabled( true );
    
    // create a sample observer
    SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    // create an enabled scanner
    ContentProviderDeviceScannerForTest scanner =
        new ContentProviderDeviceScannerForTest( resolver,
            providerData.getContentUri() );
    scanner.registerEventObserver( observer );
    scanner.setDevice( device, getContext() );
    
    // test if scanner was started as expected
    assertTrue( "Expected scanner running", scanner.isEnabled() );
    
    // send sample to data sink
    ContentValues values = new ContentValues();
    Long ts1 = System.currentTimeMillis();
    values.put( ContentProviderData.TIMESTAMP, ts1.toString() );
    resolver.insert( providerData.getContentUri(), values );
    values = new ContentValues();
    Long ts2 = ts1 + 1;
    values.put( ContentProviderData.TIMESTAMP, ts2.toString() );
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
    Sample lastSample = it.next();
    long timediff = lastSample.getTimeStamp() - sample.getTimeStamp();
    assertTrue( "Expected different timestamps", timediff > 0 );
    assertEquals( "Expected right priority set",
        device.getConfiguration().getSamplePriority().ordinal(),
        sample.getPriority() );
    assertEquals( "Expected right priority set",
        device.getConfiguration().getSamplePriority().ordinal(),
        lastSample.getPriority() );
    
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
