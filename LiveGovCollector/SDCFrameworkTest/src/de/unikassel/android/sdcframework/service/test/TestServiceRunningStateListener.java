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
package de.unikassel.android.sdcframework.service.test;

import java.util.ArrayList;
import java.util.List;

import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.service.ServiceRunningStateListener;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;

/**
 * @author Katy Hilgenberg
 *
 */
public class TestServiceRunningStateListener extends AndroidTestCase
{

  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }

  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Tests for the running state listener 
   */
  public void testRunningStateListener()
  {
    final List<Boolean> listStateUpdates = new ArrayList< Boolean >();
    
    ServiceRunningStateListener listener = new ServiceRunningStateListener( SDCService.ACTION )
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.service.ServiceRunningStateListener
       * #serviceStateChanged(boolean)
       */
      @Override
      protected void serviceStateChanged( boolean isRunning )
      {
        listStateUpdates.add( isRunning );
      }
    };

    IntentFilter filter = new IntentFilter();
    filter.addAction( SDCService.ACTION );
    getContext().registerReceiver( listener, filter );
    
    // test broadcasts
    Intent intent = new Intent( SDCService.ACTION );
    intent.putExtra( SDCService.INTENT_NAME_RUNNING_FLAG, true );
    getContext().sendBroadcast( intent );
    
    intent = new Intent( SDCService.ACTION );
    intent.putExtra( SDCService.INTENT_NAME_RUNNING_FLAG.toString(), false );
    getContext().sendBroadcast( intent );
    
    TestUtils.sleep( 500 );
    
    assertEquals( "Unexpected intent count received", 2, listStateUpdates.size() );
    assertEquals( "Unexpected state update", Boolean.TRUE, listStateUpdates.get( 0 ) );
    assertEquals( "Unexpected state update", Boolean.FALSE, listStateUpdates.get( 1 ) );
    
    getContext().unregisterReceiver( listener );
  }
  
}
