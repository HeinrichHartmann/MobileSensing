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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventCollection;

/**
 * Implementation of a collection of {@linkplain Sample}s.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root
public class SampleCollection
    implements ObservableEventCollection< Sample >, BroadcastableEvent, Parcelable
{  
  /**
   * Our custom sample collection intent action
   */
  public static final String ACTION =
      "de.unikassel.android.sdcframework.intent.action.SAMPLECOLLECTION";
  
  /**
   * The parcelable extra name for intent transport.
   */
  public static final String PARCELABLE_EXTRA_NAME = SampleCollection.class.getSimpleName();
  
  /**
   * The Parcelable creator.
   */
  public static final Parcelable.Creator< SampleCollection > CREATOR =
      new Parcelable.Creator< SampleCollection >()
  {
    
    @Override
    public SampleCollection createFromParcel( Parcel source )
    {
      return new SampleCollection( source );
    }
    
    @Override
    public SampleCollection[] newArray( int size )
    {
      return new SampleCollection[ size ];
    }
  };
  /**
   * The collection of sensor device Samples
   */
  @ElementList( name = "samples" )
  private List< Sample > samples;
  
  /**
   * Constructor
   */
  public SampleCollection()
  {
    setSamples( new Vector< Sample >() );
  }
  
  /**
   * Constructor
   * 
   * @param source
   *          the parcel source
   */
  public SampleCollection( Parcel source )
  {
    this.samples = new Vector< Sample >();
    source.readList( this.samples, Sample.class.getClassLoader() );
  }
  
  /* (non-Javadoc)
   * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
   */
  @Override
  public void writeToParcel( Parcel dest, int flags )
  {
    dest.writeList( samples );
  }
  
  /**
   * Setter for the samples
   * 
   * @param samples
   *          the samples to set
   */
  public final void setSamples( List< Sample > samples )
  {
    this.samples = samples;
  }
  
  /**
   * Access to the collection
   * 
   * @return the sample collection
   */
  public List< Sample > getSamples()
  {
    return samples;
  }
  
  /**
   * Method to clear the collection
   */
  public void clear()
  {
    samples.clear();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    String s = super.toString();
    try
    {
      s = GlobalSerializer.toXml( this );
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this, "Serialization failed" );
      e.printStackTrace();
    }
    return s;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#add(java.lang.Object)
   */
  @Override
  public boolean add( Sample sample )
  {
    return samples.add( sample );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#addAll(java.util.Collection)
   */
  @Override
  public boolean addAll( Collection< ? extends Sample > samples )
  {
    return this.samples.addAll( samples );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#contains(java.lang.Object)
   */
  @Override
  public boolean contains( Object object )
  {
    return samples.contains( object );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#containsAll(java.util.Collection)
   */
  @Override
  public boolean containsAll( Collection< ? > samples )
  {
    return this.samples.containsAll( samples );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return samples.isEmpty();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#iterator()
   */
  @Override
  public Iterator< Sample > iterator()
  {
    return samples.iterator();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#remove(java.lang.Object)
   */
  @Override
  public boolean remove( Object object )
  {
    return samples.remove( object );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#removeAll(java.util.Collection)
   */
  @Override
  public boolean removeAll( Collection< ? > samples )
  {
    return this.samples.removeAll( samples );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#retainAll(java.util.Collection)
   */
  @Override
  public boolean retainAll( Collection< ? > samples )
  {
    return this.samples.retainAll( samples );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#size()
   */
  @Override
  public int size()
  {
    return samples.size();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#toArray()
   */
  @Override
  public Object[] toArray()
  {
    return samples.toArray();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.util.Collection#toArray(T[])
   */
  @Override
  public < T > T[] toArray( T[] array )
  {
    return samples.toArray( array );
  }
  
  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public Intent getIntent()
  {
    Intent intent = new Intent();
    intent.setAction( ACTION );
    intent.putExtra( PARCELABLE_EXTRA_NAME, this );
    return intent;
  }
}
