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
package de.unikassel.android.sdcframework.data;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

/**
 * A time provider configuration entry hold the address of a single NTP time
 * provider. <br/>
 * <br/>
 * Example: <br/>
 * <blockquote> &lt;provider&gt; ptbtime1.ptb.de &lt;provider&gt; </blockquote> <br/>
 * 
 * @see SDCConfiguration
 * @author Katy Hilgenberg
 * 
 */
public class TimeProviderConfigurationEntries
{
  /**
   * The provider for the file transfer
   */
  @ElementList( entry = "provider", inline = true )
  private List< String > providers;
  
  /**
   * The description of the strategy in case of time provider syncronization
   * errors. <br/>
   * Has to be the string representation of a valid
   * de.unikassel.android.sdcframework
   * .persistence.facade.DBFullStrategyDescriptiontrategyDescription strategy
   * description}.
   */
  @Element( name = "errorStrategy", required = false )
  private String errorStrategy;
  
  /**
   * Constructor
   */
  public TimeProviderConfigurationEntries()
  {
    super();
    setProviders( new ArrayList< String >() );
  }
  
  /**
   * Getter for the provider list
   * 
   * @return the provider list
   */
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
   *          the errorStrategy to set
   */
  public void setErrorStrategy( String errorStrategy )
  {
    this.errorStrategy = errorStrategy;
  }
  
  /**
   * Getter for the error strategy
   * 
   * @return the errorStrategy
   */
  public String getErrorStrategy()
  {
    return errorStrategy;
  }
  
}
