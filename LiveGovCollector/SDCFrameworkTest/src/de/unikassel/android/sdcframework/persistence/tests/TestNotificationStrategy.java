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
package de.unikassel.android.sdcframework.persistence.tests;

import de.unikassel.android.sdcframework.persistence.NotificationStrategy;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.app.NotificationManager;
import android.content.Context;
import android.test.AndroidTestCase;

/**
 * @author Katy Hilgenberg
 *
 */
public class TestNotificationStrategy extends AndroidTestCase
{
  
  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }
  
  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test for the notification strategy.
   */
  public final void testNotificationStrategy()
  {
    NotificationManager notificationManager =
      (NotificationManager) getContext().getSystemService( Context.NOTIFICATION_SERVICE );
    
    NotificationStrategy strategy = new NotificationStrategy( getContext(), null );
    PersistentStorageManagerForTest manager = new PersistentStorageManagerForTest();
    
    manager.counter = 0;
    manager.isProcessingCurrentSamples = false;
    
    assertFalse( "Expected strategy not successful", strategy.process( manager ) );
    assertTrue( "Expected base class execute method not called", manager.counter == 0 );
    // Well, seems there is no possibility to test if notification was displayed
    // TODO: FIX ME, seems service icon is not available in test package!
    notificationManager.cancel( NotificationStrategy.NOTIFICATION );

    manager.counter = 0;
    manager.isProcessingCurrentSamples = true;
    
    assertFalse( "Expected strategy successful", strategy.process( manager ) );
    assertTrue( "Expected base class execute method not called", manager.counter == 0 );
    
    // Well, seems there is no possibility to test if notification was displayed
    notificationManager.cancel( NotificationStrategy.NOTIFICATION );
  }
  
}
