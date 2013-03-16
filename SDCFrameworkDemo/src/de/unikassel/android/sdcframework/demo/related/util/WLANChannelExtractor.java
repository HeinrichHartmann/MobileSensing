/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework project.
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
package de.unikassel.android.sdcframework.demo.related.util;

/**
 * Class with the WLAN channel related information according to IEEE 802.11.
 * To be used to extract the channel number by the frequency.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class WLANChannelExtractor
{
  /**
   * The base band frequency
   */
  public static final int BAND_FREQUENCY = 2400;
  
  /**
   * The reference channel frequency offsets in Mhz to start frequency of the
   * 2.4 GHz band
   */
  public static final int[] REF_CHANNEL_FREQ_OFFSET = new int[] { 12, 17, 22, 27, 32,
      37, 42, 47, 52, 57, 62, 67, 72, 84 };
  
  /**
   * The highest channel number
   */
  public static final int MAX_CHANNELS = 14;
  
  /**
   * The channel width in Mhz (802.11b uses 22MHz, resp. +-11 MHz from the
   * reference channel frequency)
   */
  public static final int CHANNEL_WIDTH = 22;
  
  /**
   * Does determine the channel number for a WLAN frequency.
   * 
   * @param frequency
   *          the frequency in MHz
   * @return the related WLAN channel number
   */
  public static int getChannel( int frequency )
  {
    int freqOffset = frequency - BAND_FREQUENCY;
    for ( int i = 0; i < MAX_CHANNELS; ++i )
    {
      if ( freqOffset == REF_CHANNEL_FREQ_OFFSET[ i ] ) { return i +1; }
    }
    return MAX_CHANNELS;
  }
  
}
