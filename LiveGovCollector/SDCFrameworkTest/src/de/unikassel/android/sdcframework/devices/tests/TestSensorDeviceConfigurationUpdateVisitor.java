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

import android.test.AndroidTestCase;
import de.unikassel.android.sdcframework.devices.SensorDeviceConfigurationUpdateVisitor;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;

/**
 * Tests for the sensor device configuration update visitor.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSensorDeviceConfigurationUpdateVisitor extends AndroidTestCase
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
  
  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for Construction
   */
  public final void testSensorDeviceConfigurationUpdateVisitor()
  {
    SensorDeviceConfigurationImpl configuration =
        new SensorDeviceConfigurationImpl();
    configuration.setEnabled( true );
    configuration.setFrequency( 10000 );
    configuration.setSamplePriority( SensorDevicePriorities.Level4 );
    
    SensorDeviceConfigurationChangeEvent update =
        new SensorDeviceConfigurationChangeEventImpl( configuration,
            SensorDeviceIdentifier.GSM );
    
    SensorDeviceConfigurationUpdateVisitor visitor =
        new SensorDeviceConfigurationUpdateVisitor( update, getContext() );
    
    assertSame( "Expected update initialized", update, visitor.getUpdate() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.SensorDeviceConfigurationUpdateVisitor#visit(de.unikassel.android.sdcframework.devices.facade.SensorDevice)}
   * .
   */
  public final void testVisit()
  {
    // create update and visitor
    SensorDeviceIdentifier updateDeviceID = SensorDeviceIdentifier.GSM;
    
    SensorDeviceConfigurationImpl configuration =
        new SensorDeviceConfigurationImpl();
    configuration.setEnabled( true );
    configuration.setFrequency( 10000 );
    configuration.setSamplePriority( SensorDevicePriorities.Level4 );
    
    SensorDeviceConfigurationChangeEvent update =
        new SensorDeviceConfigurationChangeEventImpl( configuration,
            updateDeviceID );
    
    SensorDeviceConfigurationUpdateVisitor visitor =
        new SensorDeviceConfigurationUpdateVisitor( update, getContext() );
    
    // create different test devices and test visitation
    SensorDevice device = new AbstractSensorDeviceForTest( updateDeviceID );
    device.getConfiguration().setEnabled( false );
    device.getConfiguration().setFrequency( 0 );
    device.getConfiguration().setSamplePriority( SensorDevicePriorities.Level0 );
    
    boolean result = visitor.visit( device );
    
    assertFalse( "Expected visitation done", result );
    assertEquals( "Unexpected enabled value after accepting visitor ",
        configuration.isEnabled(), device.getConfiguration().isEnabled() );
    assertEquals( "Unexpected frequency value after accepting visitor ",
        configuration.getFrequency(), device.getConfiguration().getFrequency() );
    assertEquals( "Unexpected priority value after accepting visitor ",
        configuration.getSamplePriority(),
        device.getConfiguration().getSamplePriority() );
    
    device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.Unknown );
    device.getConfiguration().setEnabled( false );
    device.getConfiguration().setFrequency( 0 );
    device.getConfiguration().setSamplePriority( SensorDevicePriorities.Level0 );
    
    result = visitor.visit( device );
    
    assertTrue( "Expected visitation not done", result );
    assertFalse( "Unexpected enabled value after accepting visitor ",
        configuration.isEnabled() == device.getConfiguration().isEnabled() );
    assertFalse(
        "Unexpected frequency value after accepting visitor ",
        configuration.getFrequency() == device.getConfiguration().getFrequency() );
    assertFalse(
        "Unexpected priority value after accepting visitor ",
        configuration.getSamplePriority() == device.getConfiguration().getSamplePriority() );
    
  }
  
}
