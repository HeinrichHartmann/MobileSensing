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

import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.preferences.EnabledPreference;
import de.unikassel.android.sdcframework.preferences.FrequencyPreference;
import de.unikassel.android.sdcframework.preferences.PriorityLevelPreference;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import android.test.AndroidTestCase;

/**
 * Tests for the sensor device configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSensorDeviceConfigurationImpl extends AndroidTestCase
{
  
  /**
   * Test method for Construction
   */
  public final void testConstruction()
  {
    // test default construction
    SensorDeviceConfigurationImpl config = new SensorDeviceConfigurationImpl();
    assertEquals( "Unexpected frequency after default construction",
        FrequencyPreference.DEFAULT.intValue(), config.getFrequency() );
    assertEquals( "Unexpected enabled state after default construction",
        EnabledPreference.DEFAULT.booleanValue(), config.isEnabled() );
    assertEquals( "Unexpected priority after default construction",
        PriorityLevelPreference.DEFAULT, config.getSamplePriority() );
    
    // test construction by values
    int frequency = 10;
    boolean enabled = true;
    SensorDevicePriorities prio = SensorDevicePriorities.Level4;
    
    config = new SensorDeviceConfigurationImpl( frequency, prio, enabled );
    assertEquals( "Unexpected frequency after construction",
        frequency, config.getFrequency() );
    assertEquals( "Unexpected enabled state after construction",
        enabled, config.isEnabled() );
    assertEquals( "Unexpected priority after construction",
        prio, config.getSamplePriority() );
    
    // test copy construction
    SensorDeviceConfigurationImpl copiedConfig =
        new SensorDeviceConfigurationImpl( config );
    assertEquals( "Unexpected frequency after copy construction",
        config.getFrequency(), copiedConfig.getFrequency() );
    assertEquals( "Unexpected enabled state after construction",
        config.isEnabled(), copiedConfig.isEnabled() );
    assertEquals( "Unexpected priority after construction",
        config.getSamplePriority(), copiedConfig.getSamplePriority() );
    
    // assert values have been copied and are referenced
    copiedConfig.setFrequency( 1000 );
    copiedConfig.setEnabled( false );
    copiedConfig.setSamplePriority( SensorDevicePriorities.Level0 );
    assertFalse( "Expected different frequency",
        config.getFrequency() == copiedConfig.getFrequency() );
    assertFalse( "Expected different enabled state",
        config.isEnabled() == copiedConfig.isEnabled() );
    assertFalse( "Expected different priority",
        config.getSamplePriority().equals( copiedConfig.getSamplePriority() ) );
  }
  
  /**
   * Test method for setter and getter
   */
  public final void testSetterWithGetter()
  {
    SensorDeviceConfigurationImpl config = new SensorDeviceConfigurationImpl();
    
    // create different values to current configuration
    int frequency = config.getFrequency() * 2;
    boolean enabled = !config.isEnabled();
    SensorDevicePriorities prio = SensorDevicePriorities.Level4;
    if ( config.getSamplePriority() == prio )
    {
      prio = SensorDevicePriorities.Level0;
    }
    
    // do test setter & getter
    config.setFrequency( frequency );
    assertEquals( "Unexpected frequency value", frequency,
        config.getFrequency() );
    
    config.setEnabled( enabled );
    assertEquals( "Unexpected enabled state", enabled, config.isEnabled() );
    
    config.setSamplePriority( prio );
    assertEquals( "Unexpected priority value", prio, config.getSamplePriority() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl#equals(java.lang.Object)}
   * .
   */
  public final void testComparison()
  {
    int frequency = 10;
    boolean enabled = true;
    SensorDevicePriorities prio = SensorDevicePriorities.Level4;
    
    SensorDeviceConfigurationImpl config =
        new SensorDeviceConfigurationImpl( frequency, prio, enabled );
    SensorDeviceConfigurationImpl sameConfig =
        new SensorDeviceConfigurationImpl( config );
    
    assertTrue( "Expected that configurations are equal",
        config.equals( sameConfig ) );
    
    SensorDeviceConfigurationImpl diffConfig =
        new SensorDeviceConfigurationImpl( frequency * 2, prio, enabled );
    assertFalse( "Expected that configurations are not equal",
        config.equals( diffConfig ) );
    diffConfig =
        new SensorDeviceConfigurationImpl( frequency,
            SensorDevicePriorities.Level2, enabled );
    assertFalse( "Expected that configurations are not equal",
        config.equals( diffConfig ) );
    diffConfig = new SensorDeviceConfigurationImpl( frequency, prio, !enabled );
    assertFalse( "Expected that configurations are not equal",
        config.equals( diffConfig ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.facade.UpdatableConfiguration#update(de.unikassel.android.sdcframework.preferences.facade.Configuration)}
   * .
   */
  public final void testUpdate()
  {
    int frequency = 10;
    SensorDevicePriorities prio = SensorDevicePriorities.Level4;
    SensorDeviceConfigurationImpl config =
        new SensorDeviceConfigurationImpl( frequency, prio, true );
    
    SensorDeviceConfigurationImpl updateConfig =
        new SensorDeviceConfigurationImpl( frequency * 10,
            SensorDevicePriorities.Level2, !config.isEnabled() );
    assertFalse( "Expected configurations not equal",
        config.equals( updateConfig ) );
    config.update( updateConfig );
    assertTrue( "Expected same configuration after update",
        config.equals( updateConfig ) );
  }
  
}
