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

import java.util.List;

import de.unikassel.android.sdcframework.data.TimeProviderConfigurationEntries;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.util.ObjectUtils;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * The implementation of the NTP time provider configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TimeProviderConfigurationImpl
    implements TimeProviderConfiguration
{
  /**
   * The NTP time provider list
   */
  private List< String > providers;
  
  /**
   * The time provider sync error strategy description.
   */
  private TimeProviderErrorStrategyDescription errorStrategy;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private TimeProviderConfigurationImpl()
  {}
  
  /**
   * Constructor
   * 
   * @param providers
   *          the NTP time provider list
   * @param errorStrategy
   *          the error strategy
   */
  public TimeProviderConfigurationImpl( List< String > providers,
      TimeProviderErrorStrategyDescription errorStrategy )
  {
    super();
    this.providers = providers;
    this.errorStrategy = errorStrategy;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration
   * #getProviders()
   */
  @Override
  public List< String > getProviders()
  {
    return providers;
  }
  
  /**
   * Setter for the provider list
   * 
   * @param providers
   *          the provider list to set
   */
  public void setProviders( List< String > providers )
  {
    this.providers = providers;
  }
  
  /**
   * Setter for the error strategy
   * 
   * @param errorStrategy
   *          the error strategy to set
   */
  public void
      setErrorStrategyDescription(
          TimeProviderErrorStrategyDescription errorStrategy )
  {
    this.errorStrategy = errorStrategy;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.UpdatableConfiguration
   * #update(de.unikassel.android.sdcframework.preferences.facade.Configuration)
   */
  @Override
  public void update( TimeProviderConfiguration configuration )
  {
    setProviders( configuration.getProviders() );
    setErrorStrategyDescription( configuration.getErrorStrategyDescription() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration
   * #
   * update(de.unikassel.android.sdcframework.data.TimeProviderConfigurationEntries
   * )
   */
  @Override
  public void update( TimeProviderConfigurationEntries config )
  {
    if ( config != null )
    {
      setProviders( config.getProviders() );
      
      String tmp = config.getErrorStrategy();
      if ( tmp != null )
      {
        setErrorStrategyDescription( TimeProviderErrorStrategyDescription.valueOf( tmp ) );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    boolean result = false;
    if ( o instanceof TimeProviderConfiguration )
    {
      TimeProviderConfiguration conf = (TimeProviderConfiguration) o;
      List< String > providers = this.getProviders();
      
      List< String > providers2 = conf.getProviders();
      result = providers == providers2;
      if ( !result && providers != null && providers2 != null
          && providers.size() == providers2.size() )
      {
        result = true;
        for ( String otherProvider : providers2 )
        {
          if ( !providers.contains( otherProvider ) )
          {
            result = false;
            break;
          }
        }
      }
      result =
          result
              && ObjectUtils.equals( conf.getErrorStrategyDescription(),
                  getErrorStrategyDescription() );
    }
    return result;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration
   * #getErrorStrategyDescription()
   */
  @Override
  public TimeProviderErrorStrategyDescription getErrorStrategyDescription()
  {
    return errorStrategy;
  }
  
}
