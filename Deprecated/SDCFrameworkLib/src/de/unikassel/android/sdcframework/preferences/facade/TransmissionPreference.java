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

import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * Interface for the preferences of the transmission module. <br/>
 * <br/>
 * 
 * the following preferences can be configured
 * <ul>
 * <li>the minimum count of samples to transfer,</li>
 * <li>the maximum count of samples to transfer,</li>
 * <li>the minimum frequency for archive transmission,</li>
 * <li>the {@link TransmissionProtocolPreference transmission protocol preference defaults},</li>
 * <li>the archive type,</li>
 * <li>a flag for encrypted data transfer.</li>
 * </ul>
 * <br/>
 * <br/>
 * Internal defaults are:
 * <ul>
 * <li>a minimum of 100 samples per transfer,</li>
 * <li>a maximum of 1000 samples per transfer,</li>
 * <li>a minimum frequency of 1800000 milliseconds ( 30 minutes ),</li>
 * <li>zip as archive type,</li>
 * <li>encryption disabled.</li>
 * </ul>
 * <br/>
 * Internal defaults are used if there is no default configuration available in
 * the XML configuration file of the framework.
 * 
 * @author Katy Hilgenberg
 *
 */
public interface TransmissionPreference extends
    SinglePreference< TransmissionConfiguration >
{ 
  /**
   * Getter for the preference for the minimum count of samples to transfer
   * 
   * @return the preference for the minimum count of samples to transfer
   */
  public abstract SinglePreference< Integer >
      getMinSampleTransferCountPreference();
  
  /**
   * Getter for the preference for the maximum count of samples to transfer
   * 
   * @return the preference for the maximum count of samples to transfer
   */
  public abstract SinglePreference< Integer >
      getMaxSampleTransferCountPreference();
  
  /**
   * Getter for the preference for the minimum transmission frequency
   * 
   * @return the preference for the minimum transmission frequency
   */
  public abstract SinglePreference< Long >
      getMinTransferFrequencyPreference();
  
  /**
   * Getter for the preference for the transmission protocol parameters
   * 
   * @return the preference for the transmission protocol parameters
   */
  public abstract TransmissionProtocolPreference
      getProtocolPreference();
  
  /**
   * Getter for the preference for the archive type
   * 
   * @return the preference for the archive type
   */
  public abstract SinglePreference< ArchiveTypes >
      getArchiveTypePreference();
  
  /**
   * Getter for the archive encryption enabled preference
   * 
   * @return the archive encryption enabled preference
   */
  public abstract SinglePreference< Boolean >
      getEncryptionEnabledPreference();
}
