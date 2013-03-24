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
package de.unikassel.android.sdcframework.preferences.facade;

import java.util.List;

import de.unikassel.android.sdcframework.preferences.StringPreference;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * Interface for a NTP time provider preference
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface TimeProviderPreference extends
    SinglePreference< TimeProviderConfiguration >
{
  /**
   * Method to convert a list of providers to a preference entry of concatenated
   * provider addresses.
   * 
   * @param providers
   *          the list of the providers
   * @return the provider preference entry
   */
  public abstract String getProvidersEntryFromList( List< String > providers );
  
  /**
   * Getter for the providers preference
   * 
   * @return the providers preference
   */
  public abstract StringPreference getProvidersPreference();
  
  /**
   * Getter for the error strategy preference.
   * 
   * @return the error strategy description preference
   */
  public abstract SinglePreference< TimeProviderErrorStrategyDescription >
      getErrorStrategyPreference();
}
