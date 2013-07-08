package de.unikassel.android.sdcframework.data;

import java.util.Calendar;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Weekday enumeration.
 * 
 * @author Katy Hilgenberg
 * 
 */
public enum Weekday implements Parcelable
{
  /**
   * Monday.
   */
  Monday,
  
  /**
   * Tuesday.
   */
  Tuesday,
  
  /**
   * Wednesday.
   */
  Wednesday,
  
  /**
   * Thursday.
   */
  Thursday,
  
  /**
   * Friday.
   */
  Friday,
  
  /**
   * Saturday.
   */
  Saturday,
  
  /**
   * Sunday.
   */
  Sunday;
  
  /**
   * Method to determine the Weekday for a given ordinal.
   * 
   * @param ordinal
   *          the ordinal value
   * @return the Weekday for a given ordinal
   */
  public static Weekday valueOfOrdinal( int ordinal )
  {
    if ( ordinal < 0 || ordinal >= Weekday.values().length ) { throw new IndexOutOfBoundsException(); }
    return Weekday.values()[ ordinal ];
  }
  
  /**
   * Method to determine the next Weekday after a given one.
   * 
   * @param current
   *          the current Weekday
   * @return the Weekday after the given one
   */
  public static Weekday next( Weekday current )
  {
    int nextOrdinal = ( current.ordinal() + 1 ) % 7;
    return Weekday.values()[ nextOrdinal ];
  }
  
  /**
   * Method to determine the Weekday of a given calendar date.
   * 
   * @param date
   *          the calendar date value
   * @return the Weekday for the given date
   */
  public static Weekday valueOf( Calendar date )
  {
    switch ( date.get( Calendar.DAY_OF_WEEK ) )
    {
      case Calendar.MONDAY: return Monday;
      case Calendar.TUESDAY: return Tuesday;
      case Calendar.WEDNESDAY: return Wednesday;
      case Calendar.THURSDAY: return Thursday;
      case Calendar.FRIDAY: return Friday;
      case Calendar.SATURDAY: return Saturday;
      case Calendar.SUNDAY: return Sunday;
    }
    return null;
  }
  
  /**
   * The Parcelable creator.
   */
  public static final Parcelable.Creator< Weekday > CREATOR =
      new Parcelable.Creator< Weekday >()
  {
    
    @Override
    public Weekday createFromParcel( Parcel source )
    {
      return valueOfOrdinal( source.readInt() );
    }
    
    @Override
    public Weekday[] newArray( int size )
    {
      return new Weekday[ size ];
    }
  };

  /* (non-Javadoc)
   * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
   */
  @Override
  public void writeToParcel( Parcel dest, int flags )
  {
    dest.writeInt( ordinal() );
  }

  /* (non-Javadoc)
   * @see android.os.Parcelable#describeContents()
   */
  @Override
  public int describeContents()
  {
    return 0;
  }
}