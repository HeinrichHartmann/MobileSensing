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
package de.unikassel.android.sdcframework.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.SharedPreferences;
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * Implementation of the time provider preference.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TimeProviderPreferenceImpl implements TimeProviderPreference
{
  /**
   * The key prefix.
   */
  private static final String PREFIX = "sdc_tpc";
  
  /**
   * The separator between two provider entries
   */
  public static final String SEPARATOR = ";";
  
  /**
   * Android preference key for providers.
   */
  public static final String KEY_PROVIDERS = "time_providers";
  
  /**
   * The log level default value
   */
  public static final String DEFAULT_PROVIDERS = "ptbtime1.ptb.de" + SEPARATOR
      + "ptbtime2.ptb.de";
  
  /**
   * Android preference key for the error strategy.
   */
  public static final String KEY_ERR_STRATEGY = "err_strategy";
  
  /**
   * The default for the error strategy.
   */
  public static final TimeProviderErrorStrategyDescription DEFAULT_ERR_STRATEGY =
      TimeProviderErrorStrategyDescription.ShutdownService;
  
  /**
   * The preference for the time providers
   */
  private final StringPreference timeProviderPreference;
  
  /**
   * The preference for the error strategy
   */
  private final SinglePreference< TimeProviderErrorStrategyDescription > errorStrategyPreference;
  
  /**
   * Constructor
   */
  public TimeProviderPreferenceImpl()
  {
    super();
    this.timeProviderPreference =
        new StringPreference( getKey(), KEY_PROVIDERS, DEFAULT_PROVIDERS );
    
    this.errorStrategyPreference =
        new SinglePreferenceImpl< TimeProviderErrorStrategyDescription >(
            getKey(), KEY_ERR_STRATEGY, DEFAULT_ERR_STRATEGY )
    {
      
      @Override
      public TimeProviderErrorStrategyDescription getConfiguration(
          SharedPreferences sharedPreferences )
      {
        TimeProviderErrorStrategyDescription strategy = getDefault();
        String strategyName =
            sharedPreferences.getString( getKey(), strategy.toString() );
        try
        {
          strategy =
              TimeProviderErrorStrategyDescription.valueOf( strategyName );
        }
        catch ( Exception e )
        {}
        return strategy;
      }
    };
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SinglePreference#getKey
   * ()
   */
  @Override
  public String getKey()
  {
    return PREFIX;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public TimeProviderConfiguration getConfiguration(
      SharedPreferences sharedPreferences )
  {
    String providerString =
        timeProviderPreference.getConfiguration( sharedPreferences );
    return new TimeProviderConfigurationImpl(
        getProviderListFromEntry( providerString ),
        errorStrategyPreference.getConfiguration( sharedPreferences ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getDefault()
   */
  @Override
  public TimeProviderConfiguration getDefault()
  {
    return new TimeProviderConfigurationImpl(
        getProviderListFromEntry( timeProviderPreference.getDefault() ),
        errorStrategyPreference.getDefault() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * setDefault(java.lang.Object)
   */
  @Override
  public void setDefault( TimeProviderConfiguration defaultValue )
  {
    timeProviderPreference.setDefault( getProvidersEntryFromList( defaultValue.getProviders() ) );
    errorStrategyPreference.setDefault( defaultValue.getErrorStrategyDescription() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * testForKey(java.lang.String)
   */
  @Override
  public boolean testForKey( String key )
  {
    return timeProviderPreference.testForKey( key )
        || errorStrategyPreference.testForKey( key );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference
   * #getProvidersEntry(java.util.List)
   */
  @Override
  public final String getProvidersEntryFromList( List< String > providers )
  {
    StringBuffer providerString = new StringBuffer();
    if ( providers != null )
    {
      for ( String provider : providers )
      {
        if ( provider == null )
          continue;
        
        if ( providerString.length() > 0 )
        {
          providerString.append( SEPARATOR );
        }
        providerString.append( provider );
      }
    }
    return providerString.toString();
  }
  
  /**
   * Method to convert a provider preference entry into a list of providers
   * 
   * @param providerString
   *          the string with the concatenated provider entries
   * @return a list of the contained providers
   */
  public final List< String > getProviderListFromEntry( String providerString )
  {
    if ( providerString == null || providerString.length() < 1 )
    {
      return Collections.emptyList();
    }
    
    List< String > providers = new ArrayList< String >();
    for ( String provider : providerString.split( SEPARATOR ) )
    {
      if ( provider.length() > 0 )
        providers.add( provider );
    }
    
    return providers;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference
   * #getErrorStrategyPreference()
   */
  @Override
  public SinglePreference< TimeProviderErrorStrategyDescription >
      getErrorStrategyPreference()
  {
    return errorStrategyPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference
   * #getProvidersPreference()
   */
  @Override
  public StringPreference getProvidersPreference()
  {
    return timeProviderPreference;
  }
  
}
