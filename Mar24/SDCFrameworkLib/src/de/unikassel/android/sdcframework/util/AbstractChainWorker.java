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
package de.unikassel.android.sdcframework.util;

import de.unikassel.android.sdcframework.util.facade.ChainWorker;

/**
 * Generic abstract Implementation of the "Chain of Responsibility" pattern.. <br/>
 * <br/>
 * Extending classes have to implement the {@link #process(Object)} method and
 * can override the {@link #doWork(Object)} method if necessary.
 * 
 * @author Katy Hilgenberg
 * 
 * 
 * @param <T>
 *          the workers client type
 */
public abstract class AbstractChainWorker< T > implements ChainWorker< T >
{
  
  /**
   * The successor
   */
  private ChainWorker< T > successor;
  
  /**
   * Constructor
   */
  public AbstractChainWorker()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.ChainWorker#doWork
   * (java.lang.Object)
   */
  @Override
  public boolean doWork( T client )
  {
    // call the workers execution method
    if ( !process( client ) )
    {
      // if execution fails call the successor if there is one
      if ( successor != null )
      {
        return successor.doWork( client );
      }
      return false;
    }
    return true;
  }
  
  /**
   * Processing method of the worker
   * 
   * @param client
   *          the client
   * @return true if successful
   */
  protected abstract boolean process( T client );
  
  @Override
  public final ChainWorker< T > getSuccessor()
  {
    return successor;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.ChainWorker#setSuccessor
   * (de.unikassel.android.sdcframework.persistence.facade.ChainWorker)
   */
  @Override
  public final void setSuccessor( ChainWorker< T > successor )
  {
    this.successor = successor;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.ChainWorker#withSuccessor
   * (de.unikassel.android.sdcframework.persistence.facade.ChainWorker)
   */
  @Override
  public final ChainWorker< T > withSuccessor( ChainWorker< T > successor )
  {
    setSuccessor( successor );
    return this.successor;
  }
  
}