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

import android.graphics.Color;

/**
 * The color generator used to produce distinct colors
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ColorGenerator
{
  /**
   * The indices for the HSV array
   * 
   * @author Katy Hilgenberg
   * 
   */
  enum IDX
  {
    /** hue index */
    HUE,
    /** saturation index */
    SAT,
    /** value index */
    VAL
  }
  
  /**
   * The hue increment value
   */
  private final static int HUE_STEP = 45;
  
  /**
   * The saturation default
   */
  private final static float SATURATION = 62.F;
  
  /**
   * The value default
   */
  private final static float VALUE = 85.F;
  
  /**
   * The available color count
   */
  public static int CNT_COLORS = 360 / ( HUE_STEP - 1 );
  
  /**
   * the color array
   */
  private final int[] colors = new int[ CNT_COLORS ];
  
  /**
   * Constructor 
   */
  public ColorGenerator()
  {
    super();    
    generateColors();
  }

  /**
   * the color generation method
   */
  private void generateColors()
  {
    float[] hsv = new float[]{ 0.F, SATURATION, VALUE };
    
    for ( int i = 0; i < colors.length; i +=2 )
    {
      colors[ i ] = Color.HSVToColor( hsv );
      hsv[ IDX.HUE.ordinal() ] += HUE_STEP;
    }
    
    for ( int i = 1; i < colors.length; i +=2 )
    {
      colors[ i ] = Color.HSVToColor( hsv );
      hsv[ IDX.HUE.ordinal() ] += HUE_STEP;
    }
  }

  /**
   * Method to generate a set of distinct colors
   * 
   * @return the color array
   */
  public final int[] getColors()
  {
    return colors;
  }
}
