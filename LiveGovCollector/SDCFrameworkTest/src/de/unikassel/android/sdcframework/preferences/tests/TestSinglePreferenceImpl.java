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

import android.content.SharedPreferences;

import de.unikassel.android.sdcframework.preferences.SinglePreferenceImpl;

import junit.framework.TestCase;

/**
 * Tests for the generic class for single preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSinglePreferenceImpl extends TestCase
{
  /**
   * Inner test class for an extension of the abstract generic class
   * SinglePreferencesImpl<T>. The getConfiguration Method is implemented by
   * returning the default value.
   * 
   * @author Katy Hilgenberg
   * @param <T> the preference value type
   * 
   */
  private class AlwaysDefaultPreference< T > extends SinglePreferenceImpl< T >
  {
    
    /**
     * Constructor
     * 
     * @param keyPrefix
     *          the key prefix
     * @param keySuffix
     *          the key suffix
     * @param defaultValue
     *          the default value
     */
    public AlwaysDefaultPreference( String keyPrefix, String keySuffix,
        T defaultValue )
    {
      super( keyPrefix, keySuffix, defaultValue );
    }
    
    /**
     * Constructor
     * 
     * @param key
     *          the key
     * @param defaultValue
     *          the default value
     */
    public AlwaysDefaultPreference( String key, T defaultValue )
    {
      super( key, defaultValue );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.preferences.facade.SinglePreference
     * #getConfiguration(android.content.SharedPreferences)
     */
    @Override
    public T getConfiguration( SharedPreferences sharedPreferences )
    {
      return getDefault();
    }
    
  }
  
  /**
   * Test method for instance construction 
   * {@link de.unikassel.android.sdcframework.preferences.SinglePreferenceImpl#SinglePreferenceImpl(java.lang.String, java.lang.String, java.lang.Object)}
   * .
   */
  public final void testConstruction()
  {
    String key = "key";
    String keySuffix = "suffix";
    String keyPrefix = "Prefix";
    String expectedKey = keyPrefix + "_" + keySuffix;
    Integer defaultValue = 4711;
    
    SinglePreferenceImpl<Integer> preference = new AlwaysDefaultPreference< Integer >( key, defaultValue );
    assertEquals( "Unexpected key value after construction", key, preference.getKey() );
    assertEquals( "Unexpected default value after construction", defaultValue, preference.getDefault() );
    assertTrue( "Unexpected key test result", preference.testForKey( key ) );
    
    preference = new AlwaysDefaultPreference< Integer >( keyPrefix, keySuffix, defaultValue );
    assertEquals( "Unexpected key value after construction", expectedKey, preference.getKey() );
    assertEquals( "Unexpected default value after construction", defaultValue, preference.getDefault() );
    assertTrue( "Unexpected key test result", preference.testForKey( expectedKey ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.SinglePreferenceImpl#setDefault(java.lang.Object)}
   * .
   */
  public final void testSetDefault()
  {
    String key = "key";
    Integer defaultValue = 0;    
    
    SinglePreferenceImpl<Integer> preference = new AlwaysDefaultPreference< Integer >( key, 0 );
    assertEquals( "Unexpected default value after construction", defaultValue, preference.getDefault() );

    defaultValue = 1337;
    preference.setDefault( defaultValue );
    assertEquals( "Unexpected default value ", defaultValue, preference.getDefault() );

    defaultValue = -22;
    preference.setDefault( defaultValue );
    assertEquals( "Unexpected default value ", defaultValue, preference.getDefault() );
  }  
}
