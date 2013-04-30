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

import de.unikassel.android.sdcframework.preferences.TimeProviderConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.TimeProviderConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfigurationChangeEvent;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;
import android.test.AndroidTestCase;

/**
 * Test for the time provider configuration change event
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTimeProviderConfigurationChangeEventImpl extends
    AndroidTestCase
{
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TimeProviderConfigurationChangeEventImpl#getConfiguration()}
   * .
   */
  public final void testGetConfiguration()
  {
    List< String > listProviders = new ArrayList< String >();
    listProviders.add( "ptbtime1.ptb.de" );
    listProviders.add( "ptbtime2.ptb.de" );
    listProviders.add( "atom.uhr.de" );
    TimeProviderErrorStrategyDescription strategy =
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates;
    
    TimeProviderConfigurationChangeEvent event =
        new TimeProviderConfigurationChangeEventImpl(
            new TimeProviderConfigurationImpl( listProviders, strategy ) );
    
    List< String > eventProviders = event.getConfiguration().getProviders();
    TimeProviderErrorStrategyDescription eventStrategy =
        event.getConfiguration().getErrorStrategyDescription();
    
    assertEquals( "Unexpected provider count", listProviders.size(),
        eventProviders.size() );
    for ( String provider : listProviders )
    {
      assertTrue( "Expected provider in configuration list " + provider,
          eventProviders.contains( provider ) );
    }
    assertEquals( "Unexpected error strategy", strategy, eventStrategy );
  }
  
}
