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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import de.unikassel.android.sdcframework.devices.SensorDeviceAvailabilityTester;
import de.unikassel.android.sdcframework.devices.SensorDeviceConfigurationUpdateVisitor;
import de.unikassel.android.sdcframework.devices.SensorDeviceManagerImpl;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.SensorDevicePreferencesImpl;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;

/**
 * Tests for the sensor device manager.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSensorDeviceManagerImpl extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for creation .
   */
  public final void testSensorDeviceManagerImpl()
  {
    try
    {
      new SensorDeviceManagerImpl( null );
      fail( "Expected exception for null parameter" );
    }
    catch ( Exception e )
    {}
    
    ApplicationPreferenceManagerImpl preferenceManager =
        new ApplicationPreferenceManagerImpl();
    SensorDeviceManagerImpl manager =
        new SensorDeviceManagerImpl( preferenceManager );
    
    assertSame( "Expected preference manager linked", preferenceManager,
        manager.getPreferenceManager() );
    assertNotNull( "Expected device factory created",
        manager.getDeviceFactory() );
    assertNotNull( "Expected device collection not null", manager.getDevices() );
    assertTrue( "Expected device collection empty after creation",
        manager.getDevices().isEmpty() );
  }
  
  /**
   * Test method for life cycles
   */
  public final void testLifeCycle()
  {
    ApplicationPreferenceManagerImpl preferenceManager =
        new ApplicationPreferenceManagerImpl();
    SensorDeviceManagerImpl manager =
        new SensorDeviceManagerImpl( preferenceManager );
    assertNotNull( "Expected device collection not null", manager.getDevices() );
    assertTrue( "Expected device collection empty after creation",
        manager.getDevices().isEmpty() );
    
    // configure known devices for availability tester
    Set< SensorDeviceIdentifier > configuredDevices =
        new HashSet< SensorDeviceIdentifier >();
    configuredDevices.add( SensorDeviceIdentifier.Accelerometer );
    configuredDevices.add( SensorDeviceIdentifier.Bluetooth );
    configuredDevices.add( SensorDeviceIdentifier.Wifi );
    configuredDevices.add( SensorDeviceIdentifier.GPS );
    configuredDevices.add( SensorDeviceIdentifier.GSM );
    configuredDevices.add( SensorDeviceIdentifier.Twitter );
    SensorDeviceAvailabilityTester availabilityTester =
        SensorDeviceAvailabilityTester.getInstance();
    availabilityTester.configure( configuredDevices, getContext() );
    
    // get available devices on current hardware
    List< SensorDeviceIdentifier > availableDevices =
        availabilityTester.getAvailableSensorDevices();
    
    // test creation
    manager.onCreate( getContext() );
    
    assertEquals( "Expected devices created for all availbale sensors",
        availableDevices.size(), manager.getDevices().size() );
    
    Collection< SensorDevice > managedDevices = manager.getDevices();
    Collection< SensorDevice > enabledDevices = new Vector< SensorDevice >();
    for ( SensorDevice device : managedDevices )
    {
      SensorDeviceIdentifier deviceIdentifier = device.getDeviceIdentifier();
      assertTrue( "Expected device id in available devices list",
          availableDevices.contains( deviceIdentifier ) );
      assertNotNull( "Expected device scanner attached",
          device.getScanner() );
      assertFalse( "Expected scanner not running",
          device.getScanner().isEnabled() );
      assertFalse( "Expected device scanning disabled",
          device.isDeviceScanningEnabled() );
      
      preferenceManager.getPreferencesForDevice( deviceIdentifier ).getEnabledPreference().setDefault(
          true );
      if ( preferenceManager.getDeviceConfiguration( deviceIdentifier,
          getContext() ).isEnabled() )
      {
        enabledDevices.add( device );
      }
    }
    
    // test resume
    manager.onResume( getContext() );
    
    for ( SensorDevice device : enabledDevices )
    {
      assertTrue( "Expected device scanning enabled",
          device.isDeviceScanningEnabled() );
      if ( device.isDeviceInSystemEnabled( getContext() ) )
      {
        assertTrue( "Expected scanner running now",
            device.getScanner().isEnabled() );
      }
    }
    
    Collection< SensorDevice > copyOfManagedDevices =
        new Vector< SensorDevice >( manager.getDevices() );
    Collection< SensorDeviceScanner > copyOfManagedScanners =
        new Vector< SensorDeviceScanner >();
    for ( SensorDevice device : copyOfManagedDevices )
    {
      copyOfManagedScanners.add( device.getScanner() );
    }
    
    // test pause
    manager.onPause( getContext() );
    
    managedDevices = manager.getDevices();
    for ( SensorDevice device : managedDevices )
    {
      assertFalse( "Expected scanner not running now",
          device.getScanner().isEnabled() );
    }
    
    // test destroy
    manager.onDestroy( getContext() );
    assertNotNull( "Expected device collection not null", manager.getDevices() );
    assertTrue( "Expected device collection empty after destroy",
        manager.getDevices().isEmpty() );
    
    for ( SensorDevice device : copyOfManagedDevices )
    {
      assertNull( "Expected scanner link cleared", device.getScanner() );
    }
    
    for ( SensorDeviceScanner scanner : copyOfManagedScanners )
    {
      assertFalse( "Expected scanner not running anymore", scanner.isEnabled() );
      assertNull( "Expected device link cleared", scanner.getDevice() );
    }
  }
  
  /**
   * Test method for visitation.
   */
  public final void testAccept()
  {
    ApplicationPreferenceManagerImpl preferenceManager =
        new ApplicationPreferenceManagerImpl();
    SensorDeviceManagerImpl manager =
        new SensorDeviceManagerImpl( preferenceManager );
    
    // configure known devices for availability tester
    Set< SensorDeviceIdentifier > configuredDevices =
        new HashSet< SensorDeviceIdentifier >();
    configuredDevices.add( SensorDeviceIdentifier.Accelerometer );
    configuredDevices.add( SensorDeviceIdentifier.Bluetooth );
    configuredDevices.add( SensorDeviceIdentifier.Wifi );
    configuredDevices.add( SensorDeviceIdentifier.GPS );
    configuredDevices.add( SensorDeviceIdentifier.GSM );
    configuredDevices.add( SensorDeviceIdentifier.Twitter );
    SensorDeviceAvailabilityTester availabilityTester =
        SensorDeviceAvailabilityTester.getInstance();
    availabilityTester.configure( configuredDevices, getContext() );
    
    manager.onCreate( getContext() );
    manager.onResume( getContext() );
    
    // test visitation
    Collection< SensorDevice > managedDevices = manager.getDevices();
    for ( SensorDevice device : managedDevices )
    {
      boolean enabled = !device.getConfiguration().isEnabled();
      int frequency = device.getConfiguration().getFrequency() + 10;
      SensorDevicePriorities priority =
          device.getConfiguration().getSamplePriority();
      
      SensorDeviceConfigurationChangeEvent event =
          new SensorDeviceConfigurationChangeEventImpl(
              new SensorDeviceConfigurationImpl( frequency, priority, enabled ),
              device.getDeviceIdentifier() );
      SensorDeviceVisitor visitor =
          new SensorDeviceConfigurationUpdateVisitor( event, getContext() );
      
      boolean result = manager.accept( visitor );
      assertTrue( "Expected visitation done", result );
      assertEquals( "Expected frequency changed", frequency,
          device.getConfiguration().getFrequency() );
      assertEquals( "Expected enabled state changed", enabled,
          device.getConfiguration().isEnabled() );
      
      if ( enabled && device.isDeviceInSystemEnabled( getContext() ) )
      {
        assertTrue( "Expected scanner running now",
            device.getScanner().isEnabled() );
      }
    }
    manager.onPause( getContext() );
    manager.onDestroy( getContext() );
  }
  
  /**
   * Test method for event observation.
   */
  public final void testEventObservation()
  {    
    ApplicationPreferenceManagerImpl preferenceManager =
        new ApplicationPreferenceManagerImpl();
    final SensorDeviceManagerImpl manager =
        new SensorDeviceManagerImpl( preferenceManager );
    
    // register an event Observer
    final SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    // configure known devices for availability tester
    Set< SensorDeviceIdentifier > configuredDevices =
        new HashSet< SensorDeviceIdentifier >();
    configuredDevices.add( SensorDeviceIdentifier.Accelerometer );
    configuredDevices.add( SensorDeviceIdentifier.Bluetooth );
    configuredDevices.add( SensorDeviceIdentifier.Wifi );
    configuredDevices.add( SensorDeviceIdentifier.GSM );
    configuredDevices.add( SensorDeviceIdentifier.Twitter );
    SensorDeviceAvailabilityTester availabilityTester =
        SensorDeviceAvailabilityTester.getInstance();
    availabilityTester.configure( configuredDevices, getContext() );
    
    SharedPreferences sharedPreferences =
      preferenceManager.getSharedPreferences( getContext() );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    for( SensorDeviceIdentifier id : availabilityTester.getAvailableSensorDevices() )
    {
      SensorDevicePreferencesImpl preference = new SensorDevicePreferencesImpl( id );
      editor.putBoolean( preference.getEnabledPreference().getKey(), true );
      editor.putString( preference.getFrequencyPreference().getKey(), "1000" );
    }
    editor.commit();
    
    // create manager in looper thread to allow asynchronous event handling
    /**
     * Internal looper test thread
     * 
     * @author Katy Hilgenberg
     * 
     */
    class LooperThread extends LooperThreadForTest
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.devices.tests.LooperThreadForTest#
       * doPrepareTest()
       */
      @Override
      public void doPrepareTest()
      {
        // create devices in manager and add event observer
        manager.onCreate( getContext() );
        
        assertTrue( "Test can not performed without a supported device",
            manager.getDevices().size() > 0 );
        
        manager.onResume( getContext() );
      }
    }
    ;
    
    // create a looper thread instance
    LooperThread looperThread = new LooperThread();
    
    // start the looper thread and wait for PREPARATION finished
    looperThread.start();
    while ( !looperThread.hasPreparationDone.get() )
    {
      TestUtils.sleep( 100 );
    }
    observer.observedEvents.clear();
    
    manager.registerEventObserver( observer );
    
    TestUtils.sleep( 2000 );
    
    // test for observed events
    assertTrue( "Expected observed events", observer.observedEvents.size() > 0 );
    
    // unregister the event Observer
    manager.unregisterEventObserver( observer );
    manager.onDestroy( getContext() );
    looperThread.interrupt();
  }
}
