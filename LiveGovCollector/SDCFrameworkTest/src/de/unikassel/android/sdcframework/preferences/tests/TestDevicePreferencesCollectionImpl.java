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
package de.unikassel.android.sdcframework.preferences.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.preferences.DevicePreferencesCollectionImpl;
import de.unikassel.android.sdcframework.preferences.SensorDevicePreferencesImpl;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;
import android.test.AndroidTestCase;

/**
 * Tests for the device preferences collection .
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestDevicePreferencesCollectionImpl extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }
  
  /**
   * Test method for construction
   * {@link de.unikassel.android.sdcframework.preferences.DevicePreferencesCollectionImpl#DevicePreferencesCollectionImpl()}
   * .
   */
  public final void testPreconditions()
  {
    DevicePreferencesCollectionImpl devicePreferenceCollection =
        new DevicePreferencesCollectionImpl();
    assertNotNull( "Expected an collection pointer to an empty collection",
        devicePreferenceCollection.getPreferences() );
    assertTrue( "Expected an empty collection",
        devicePreferenceCollection.getPreferences().isEmpty() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.DevicePreferencesCollectionImpl#addPreferences(de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences)}
   * .
   */
  public final void testAddPreferences()
  {
    DevicePreferencesCollectionImpl devicePreferenceCollection =
      new DevicePreferencesCollectionImpl();
  
    // create and add preferences for all available device types
    Vector< SensorDevicePreferences > vecPrefs =
        new Vector< SensorDevicePreferences >();
    int i = 0;
    
    for ( SensorDeviceIdentifier id : SensorDeviceIdentifier.values() )
    {
      SensorDevicePreferencesImpl preference = new SensorDevicePreferencesImpl( id );
      vecPrefs.add( preference );
      
      ++i;
      devicePreferenceCollection.addPreferences( preference );
      assertEquals( "Unexpected collection size", i,
          devicePreferenceCollection.getPreferences().size() );
    }
    
    // test for special objects in collection
    for ( SensorDevicePreferences pref : vecPrefs )
    {
      assertTrue( "Expected preference in preference collection", 
          devicePreferenceCollection.getPreferences().contains( pref ) );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.DevicePreferencesCollectionImpl#getPreferencesForDevice(de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier)}
   * .
   */
  public final void testGetPreferencesForDevice()
  {
    DevicePreferencesCollectionImpl devicePreferenceCollection =
        new DevicePreferencesCollectionImpl();
    
    // create and add preferences for all available device types
    Map< SensorDeviceIdentifier, SensorDevicePreferences > mapPrefs =
        new HashMap< SensorDeviceIdentifier, SensorDevicePreferences >();
    
    for ( SensorDeviceIdentifier id : SensorDeviceIdentifier.values() )
    {
      SensorDevicePreferencesImpl preference = new SensorDevicePreferencesImpl( id );
      mapPrefs.put( id, preference );
      devicePreferenceCollection.addPreferences( preference );
    }   
    
    for ( SensorDeviceIdentifier id : SensorDeviceIdentifier.values() )
    {
      assertSame( "Unexpected preference for device " + id, 
          mapPrefs.get( id ), devicePreferenceCollection.getPreferencesForDevice( id ) );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.DevicePreferencesCollectionImpl#removeAll()}
   * .
   */
  public final void testRemoveAll()
  {
    DevicePreferencesCollectionImpl devicePreferenceCollection =
      new DevicePreferencesCollectionImpl();
  
    for ( SensorDeviceIdentifier id : SensorDeviceIdentifier.values() )
    {
      SensorDevicePreferencesImpl preference = new SensorDevicePreferencesImpl( id );
      devicePreferenceCollection.addPreferences( preference );
    }
    
    assertEquals( "Unexpected preference count", SensorDeviceIdentifier.values().length,
        devicePreferenceCollection.getPreferences().size() );
    devicePreferenceCollection.removeAll();
    
    assertEquals( "Unexpected preference count", 0,
        devicePreferenceCollection.getPreferences().size() );
  }
  
}
