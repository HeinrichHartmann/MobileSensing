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

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import de.unikassel.android.sdcframework.preferences.TimeProviderPreferenceImpl;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * Tests for the time provider preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTimeProviderPreferenceImpl extends AndroidTestCase
{
  
  /**
   * @throws java.lang.Exception
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }
  
  /**
   * Test method for construction
   */
  public final void testPreconditions()
  {
    TimeProviderPreferenceImpl preferences = new TimeProviderPreferenceImpl();
    
    assertNotNull( "Expected key not null", preferences.getKey() );
    
    assertNotNull( "Expected providers preference not null",
        preferences.getProvidersPreference() );
    assertNotNull( "Expected error strategy preference not null",
        preferences.getErrorStrategyPreference() );
    
    String expected = preferences.getKey() + '_' + TimeProviderPreferenceImpl.KEY_PROVIDERS;
    assertEquals(
        "Unexpected key for providers",
        expected, preferences.getProvidersPreference().getKey() );
    
    assertEquals(
        "Unexpected key for error strategy",
        preferences.getKey() + '_' + TimeProviderPreferenceImpl.KEY_ERR_STRATEGY,
        preferences.getErrorStrategyPreference().getKey() );
    
    assertTrue( "Unexpected default value type",
        preferences.getDefault() instanceof TimeProviderConfiguration );
    
    assertEquals(
        "Unexpected default value for providers",
        TimeProviderPreferenceImpl.DEFAULT_PROVIDERS,
        preferences.getProvidersEntryFromList( preferences.getDefault().getProviders() ) );
    
    assertEquals(
        "Unexpected default value for error strategy",
        TimeProviderPreferenceImpl.DEFAULT_ERR_STRATEGY,
        preferences.getDefault().getErrorStrategyDescription() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TimeProviderPreferenceImpl#getConfiguration(android.content.SharedPreferences)}
   * .
   */
  public final void testGetConfiguration()
  {
    // TODO: extend
    TimeProviderPreferenceImpl pref = new TimeProviderPreferenceImpl();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    String providers = "atom.uhr.de;dummy.com";
    TimeProviderErrorStrategyDescription strategy =
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates;
    
    editor.putString( pref.getProvidersPreference().getKey(),
        providers.toString() );
    editor.putString( pref.getErrorStrategyPreference().getKey(),
        strategy.name() );
    editor.commit();
    
    TimeProviderConfiguration configuration = pref.getConfiguration(
        sharedPreferences );
    assertEquals(
        "Unexpected configuration for providers",
        providers,
        pref.getProvidersEntryFromList( configuration.getProviders() ) );
    
    assertEquals(
        "Unexpected configuration for error startegy",
        strategy, configuration.getErrorStrategyDescription() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TimeProviderPreferenceImpl#getProviderListFromEntry(String)}
   * .
   */
  public final void testGetProviderListFromEntry()
  {
    TimeProviderPreferenceImpl pref = new TimeProviderPreferenceImpl();
    String provider1 = "atom.uhr.de";
    String provider2 = "dummy.com";
    
    List< String > result = pref.getProviderListFromEntry( provider1 );
    assertEquals( "Unexpected provider list size", 1, result.size() );
    assertEquals( "Wrong provider", provider1, result.get( 0 ) );
    
    List< String > providerList = new ArrayList< String >();
    providerList.add( provider1 );
    providerList.add( provider2 );
    String providers =
        TimeProviderPreferenceImpl.SEPARATOR
            + TimeProviderPreferenceImpl.SEPARATOR + provider1
            + TimeProviderPreferenceImpl.SEPARATOR
            + TimeProviderPreferenceImpl.SEPARATOR
            + TimeProviderPreferenceImpl.SEPARATOR + provider2
            + TimeProviderPreferenceImpl.SEPARATOR;
    
    result = pref.getProviderListFromEntry( providers );
    assertEquals( "Unexpected provider list size", providerList.size(),
        result.size() );
    for ( String provider : result )
    {
      assertTrue( "Wrong provider in list + \"" + provider + "\"",
          providerList.contains( provider ) );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TimeProviderPreferenceImpl#getProvidersEntryFromList(java.util.List)}
   * .
   */
  public final void testGetProvidersEntryFromList()
  {
    TimeProviderPreference pref = new TimeProviderPreferenceImpl();
    String provider1 = "atom.uhr.de";
    String provider2 = "dummy.com";
    String providers =
        provider1 + TimeProviderPreferenceImpl.SEPARATOR + provider2;
    
    List< String > providerList = new ArrayList< String >();
    providerList.add( null );
    providerList.add( provider1 );
    providerList.add( null );
    providerList.add( null );
    providerList.add( provider2 );
    providerList.add( null );
    
    assertEquals(
        "Unexpected provider entry generated from List",
        providers,
        pref.getProvidersEntryFromList( providerList ) );
    
    providerList.clear();
    providerList.add( provider1 );
    providerList.add( provider2 );
    providerList.add( provider1 );
    providers =
        provider1 + TimeProviderPreferenceImpl.SEPARATOR + provider2
            + TimeProviderPreferenceImpl.SEPARATOR + provider1;
    
    assertEquals(
        "Unexpected provider entry generated from List",
        providers,
        pref.getProvidersEntryFromList( providerList ) );
  }
  
}
