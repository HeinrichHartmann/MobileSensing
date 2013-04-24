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

import java.security.InvalidParameterException;

import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;

/**
 * Generic abstract base class for a single preference type. <br/>
 * <br/>
 * The key for the Android shared preferences will be constructed from the
 * keyPrefix and keySuffix values provided in the
 * {@linkplain #SinglePreferenceImpl(String, String, Object) constructor call}.
 * 
 * @author Katy Hilgenberg
 * @param <T>
 *          the type of the preference value
 * 
 */
public abstract class SinglePreferenceImpl< T > implements SinglePreference< T >
{
  /**
   * Default preference value
   */
  private T defaultValue;
  
  /**
   * The key value for the Android preferences
   */
  private final String key;
  
  /**
   * Constructor
   * 
   * @param key
   *          the key value
   * @param defaultValue
   *          the default value
   */
  protected SinglePreferenceImpl( String key, T defaultValue )
  {
    super();
    if( defaultValue == null )
      throw new InvalidParameterException( "default value is null" );
    if( key == null )
      throw new InvalidParameterException( "key is null" );
    this.key = key;
    setDefault( defaultValue );
  }
  
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
  public SinglePreferenceImpl( String keyPrefix, String keySuffix,
      T defaultValue )
  {
    this( keyPrefix + "_" + keySuffix, defaultValue );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SinglePreference#getKey
   * ()
   */
  @Override
  public final String getKey()
  {
    return key;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getDefault()
   */
  @Override
  public final T getDefault()
  {
    return defaultValue;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * setDefault(java.lang.Object)
   */
  @Override
  public final void setDefault( T defaultValue )
  {
    // we do never override defaults with null values
    if( defaultValue != null )
      this.defaultValue = defaultValue;
  }
  
  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#testForKey(java.lang.String)
   */
  @Override
  public boolean testForKey( String key )
  {
    return this.key.equals( key );
  }

}
