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
package de.unikassel.android.sdcframework.demo.related.view;

import java.text.DecimalFormat;

import de.unikassel.android.sdcframework.data.independent.AccelerometerSampleData;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.FileReferenceSampleData;
import de.unikassel.android.sdcframework.data.independent.GSMSampleData;
import de.unikassel.android.sdcframework.data.independent.GyroscopeSampleData;
import de.unikassel.android.sdcframework.data.independent.LightSampleData;
import de.unikassel.android.sdcframework.data.independent.LocationSampleData;
import de.unikassel.android.sdcframework.data.independent.MagneticFieldSampleData;
import de.unikassel.android.sdcframework.data.independent.OrientationSampleData;
import de.unikassel.android.sdcframework.data.independent.PressureSampleData;
import de.unikassel.android.sdcframework.data.independent.ProximitySampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.TemperatureSampleData;
import de.unikassel.android.sdcframework.data.independent.TimeProviderSampleData;
import de.unikassel.android.sdcframework.data.independent.TwitterSampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.demo.R;
import de.unikassel.android.sdcframework.demo.related.util.WLANChannelExtractor;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

/**
 * The XML list view factory.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ExtendedListViewFactory implements ListViewFactory
{
  /**
   * 
   */
  private static final String DEC_FORMAT = "####.##";
  
  /**
   * 
   */
  private static final String DEC_FORMAT_SEC = "###.####\"";
  
  /**
   * The orange color value
   */
  private final int orange;
  
  /**
   * The separation string
   */
  private final static String SEPARATION = "   ";
  
  /**
   * The line separator
   */
  private final static char LINE_BREAK = '\n';
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   */
  public ExtendedListViewFactory( Context context )
  {
    orange = context.getResources().getColor( R.color.orange );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.view.ListViewFactory#createDataView
   * (android.content.Context,
   * de.unikassel.android.sdcframework.related.view.TableLayoutView)
   */
  @Override
  public View createDataView( Context context, TableLayoutView layoutView )
  {
    layoutView.setIgnoreSelection( true );
    return new TextView( context );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.view.ListViewFactory#createDataView
   * (android.view.View,
   * de.unikassel.android.sdcframework.data.independent.SampleData)
   */
  @Override
  public void updateDataView( View dataView, SampleData sampleData )
    throws Exception
  {
    TextView viewData = (TextView) dataView;
    viewData.setText( getTextRepresentationForData( sampleData ) );
  }
  
  /**
   * Returns the string representation of a double value rounded
   * 
   * @param value
   *          the double value
   * @return the string representation
   */
  private String stringValue( double value )
  {
    DecimalFormat df = new DecimalFormat( DEC_FORMAT );
    return df.format( value );
  }
  
  /**
   * Method to create the text representation for the specific sensor data
   * 
   * @param sampleData
   *          the sensor data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForData(
      SampleData sampleData )
  {
    if ( sampleData instanceof AccelerometerSampleData )
    {
      AccelerometerSampleData data = (AccelerometerSampleData) sampleData;
      return getTextRepresentationForAccelerometerData( data );
    }
    else if ( sampleData instanceof BluetoothSampleData )
    {
      BluetoothSampleData data = (BluetoothSampleData) sampleData;
      return getTextRepresentationForBluetoothData( data );
    }
    else if ( sampleData instanceof FileReferenceSampleData )
    {
      FileReferenceSampleData data = (FileReferenceSampleData) sampleData;
      return getTextRepresentationForFileReferenceData( data );
    }
    else if ( sampleData instanceof LocationSampleData )
    {
      LocationSampleData data = (LocationSampleData) sampleData;
      return getTextRepresentationForLocationData( data );
    }
    else if ( sampleData instanceof GSMSampleData )
    {
      GSMSampleData data = (GSMSampleData) sampleData;
      return getTextRepresentationForGSMData( data );
    }
    else if ( sampleData instanceof GyroscopeSampleData )
    {
      GyroscopeSampleData data = (GyroscopeSampleData) sampleData;
      return getTextRepresentationForGyroscopeData( data );
    }
    else if ( sampleData instanceof LightSampleData )
    {
      LightSampleData data = (LightSampleData) sampleData;
      return getTextRepresentationForLightData( data );
    }
    else if ( sampleData instanceof MagneticFieldSampleData )
    {
      MagneticFieldSampleData data = (MagneticFieldSampleData) sampleData;
      return getTextRepresentationForMagneticFieldData( data );
    }
    else if ( sampleData instanceof OrientationSampleData )
    {
      OrientationSampleData data = (OrientationSampleData) sampleData;
      return getTextRepresentationForOrientationData( data );
    }
    else if ( sampleData instanceof PressureSampleData )
    {
      PressureSampleData data = (PressureSampleData) sampleData;
      return getTextRepresentationForPressureData( data );
    }
    else if ( sampleData instanceof ProximitySampleData )
    {
      ProximitySampleData data = (ProximitySampleData) sampleData;
      return getTextRepresentationForProximityData( data );
    }
    else if ( sampleData instanceof TemperatureSampleData )
    {
      TemperatureSampleData data = (TemperatureSampleData) sampleData;
      return getTextRepresentationForTemperatureData( data );
    }
    else if ( sampleData instanceof TwitterSampleData )
    {
      
      TwitterSampleData data = (TwitterSampleData) sampleData;
      return getTextRepresentationForTwitterData( data );
    }
    else if ( sampleData instanceof WifiSampleData )
    {
      WifiSampleData data = (WifiSampleData) sampleData;
      return getTextRepresentationForWifiData( data );
    }
    else if ( sampleData instanceof TimeProviderSampleData )
    {
      TimeProviderSampleData data = (TimeProviderSampleData) sampleData;
      return getTextRepresentationForTimeProviderData( data );
    }
    
    return new SpannableStringBuilder();
  }
  
  /**
   * @param data
   * @return
   */
  private SpannableStringBuilder getTextRepresentationForTimeProviderData(
      TimeProviderSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( data.isSynced() ? "Synced" : "Out of Sync" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }

  /**
   * Method to create a textual representation of accelerometer sample data
   * 
   * @param data
   *          the accelerometer data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForAccelerometerData(
      AccelerometerSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( "X " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getAccelerationX() ) );
    buffer.append( " m/s\u00B2" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    pos = buffer.length();
    buffer.append( "Y " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getAccelerationY() ) );
    buffer.append( " m/s\u00B2" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    pos = buffer.length();
    buffer.append( "Z " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getAccelerationZ() ) );
    buffer.append( " m/s\u00B2" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of GPS sample data
   * 
   * @param data
   *          the GPS data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForLocationData(
      LocationSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    // Latitude
    buffer.append( convertCoordinate( data.getLatitude() ) );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( " N" );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // Longitude
    pos = buffer.length();
    buffer.append( convertCoordinate( data.getLongitude() ) );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( " O" );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    // GPS Height = height above an ellipsoidal model of the Earth!
    Double altitude = data.getAltitude();
    if ( altitude != null )
    {
      
      buffer.append( LINE_BREAK );
      pos = buffer.length();
      buffer.append( "Alt. " );
      buffer.setSpan( new ForegroundColorSpan( orange ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
      pos = buffer.length();
      buffer.append( stringValue( altitude ) );
      buffer.append( " m" );
      buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
      buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    }
    
    Float speed = data.getSpeed();
    if ( speed != null )
    {
      
      buffer.append( SEPARATION );
      pos = buffer.length();
      buffer.append( "Speed " );
      buffer.setSpan( new ForegroundColorSpan( orange ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
      pos = buffer.length();
      
      buffer.append( stringValue( speed ) );
      buffer.append( " m/s" );
      buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
      buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    }
    
    Float accuracy = data.getAccuracy();
    if ( accuracy != null && accuracy > 0.F )
    {
      buffer.append( SEPARATION );
      
      pos = buffer.length();
      buffer.append( "Acc. " );
      buffer.setSpan( new ForegroundColorSpan( orange ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
      pos = buffer.length();
      buffer.append( stringValue( accuracy ) );
      buffer.append( " m" );
      buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
      buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    }
    return buffer;
  }
  
  /**
   * Method to convert the GPS coordinates to degree, minutes and seconds
   * 
   * @param coordinate
   *          the coordinate
   * @return the String representation
   */
  public static String convertCoordinate( double coordinate )
  {
    if ( coordinate < -180.0 || coordinate > 180.0
            || Double.isNaN( coordinate ) ) { throw new IllegalArgumentException(
        Double.toString( coordinate ) ); }
    
    StringBuilder sb = new StringBuilder();
    if ( coordinate < 0 )
    {
      sb.append( '-' );
      coordinate = -coordinate;
    }
    
    DecimalFormat df = new DecimalFormat( DEC_FORMAT_SEC );
    
    int degrees = (int) Math.floor( coordinate );
    sb.append( degrees );
    sb.append( "\u00B0 " );
    coordinate -= degrees;
    coordinate *= 60.0;
    int minutes = (int) Math.floor( coordinate );
    sb.append( minutes );
    sb.append( "\' " );
    coordinate -= minutes;
    coordinate *= 60.0;
    sb.append( df.format( coordinate ) );
    return sb.toString();
  }
  
  /**
   * Method to create a textual representation of GSM sample data
   * 
   * @param data
   *          the GSM data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForGSMData(
      GSMSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    ;
    
    // Operator
    buffer.append( data.getOperator() );
    buffer.setSpan( new ForegroundColorSpan( Color.CYAN ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // Cell-ID
    pos = buffer.length();
    buffer.append( "Cell " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( Integer.toString( data.getCellId() ) );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // LAC
    pos = buffer.length();
    buffer.append( "LAC " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( Integer.toString( data.getLocationAreaCode() ) );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    int rssi = data.getSignalStrength();
    if ( rssi < 99 )
    {
      // calculation based on
      // http://en.wikipedia.org/wiki/Mobile_phone_signal#ASU
      double signalStrength = 2 * rssi - 113;
      
      buffer.append( LINE_BREAK );
      
      pos = buffer.length();
      buffer.append( stringValue( signalStrength ) );
      buffer.append( " dBm" );
      buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
      buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
          buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    }
    return buffer;
  }
  
  /**
   * Method to create a textual representation of gyroscope sample data
   * 
   * @param data
   *          the gyroscope data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForGyroscopeData(
      GyroscopeSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( "X " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getAngularSpeedX() ) );
    buffer.append( " rad/s" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    pos = buffer.length();
    buffer.append( "Y " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getAngularSpeedY() ) );
    buffer.append( " rad/s" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    pos = buffer.length();
    buffer.append( "Z " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getAngularSpeedZ() ) );
    buffer.append( " rad/s" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of light sample data
   * 
   * @param data
   *          the light data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForLightData(
      LightSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( stringValue( data.getLightLevel() ) );
    buffer.append( " lux" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    return buffer;
  }
  
  /**
   * Method to create a textual representation of magnetic field sample data
   * 
   * @param data
   *          the magnetic field data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForMagneticFieldData(
      MagneticFieldSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( "X " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getMagneticFieldX() ) );
    buffer.append( " \u03BCT" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    pos = buffer.length();
    buffer.append( "Y " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getMagneticFieldY() ) );
    buffer.append( " \u03BCT" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    pos = buffer.length();
    buffer.append( "Z " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getMagneticFieldZ() ) );
    buffer.append( " \u03BCT" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of orientation sample data
   * 
   * @param data
   *          the orientation data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForOrientationData(
      OrientationSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    // Heading
    buffer.append( "Heading " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getHeading() ) );
    buffer.append( "\u00B0" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // Pitch
    pos = buffer.length();
    buffer.append( "Pitch " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getPitch() ) );
    buffer.append( "\u00B0" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // Roll
    pos = buffer.length();
    buffer.append( "Roll " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( stringValue( data.getRoll() ) );
    buffer.append( "\u00B0" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of pressure sample data
   * 
   * @param data
   *          the pressure data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForPressureData(
      PressureSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( stringValue( data.getPressure() ) );
    buffer.append( " hPa (millibar)" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of proximity sample data
   * 
   * @param data
   *          the proximity data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForProximityData(
      ProximitySampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( stringValue( data.getProximityDistance() ) );
    buffer.append( " cm" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of temperature sample data
   * 
   * @param data
   *          the temperature data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForTemperatureData(
      TemperatureSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( stringValue( data.getTemperature() ) );
    buffer.append( " \u2103" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of Twitter sample data
   * 
   * @param data
   *          the Twitter data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForTwitterData(
      TwitterSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( "Message " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( data.getMessage() );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of file reference sample data
   * 
   * @param data
   *          the file reference data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForFileReferenceData(
      FileReferenceSampleData data )
  {
    
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    buffer.append( "Attached File " );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    pos = buffer.length();
    buffer.append( data.getFile() );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of Bluetooth sample data
   * 
   * @param data
   *          the Bluetooth data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForBluetoothData(
      BluetoothSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    // Name
    buffer.append( data.getName() );
    buffer.setSpan( new ForegroundColorSpan( Color.CYAN ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // Address
    pos = buffer.length();
    buffer.append( "[ " );
    buffer.append( data.getAddress() );
    buffer.append( " ]" );
    buffer.setSpan( new ForegroundColorSpan( Color.LTGRAY ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( LINE_BREAK );
    
    // Class
    pos = buffer.length();
    buffer.append( data.getBluetoothClass() );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // RSSI
    pos = buffer.length();
    buffer.append( Integer.toString( data.getRSSI() ) );
    buffer.append( " dBm" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
  
  /**
   * Method to create a textual representation of Wifi sample data
   * 
   * @param data
   *          the Wifi data
   * @return the spannable string builder with the text representation
   */
  private SpannableStringBuilder getTextRepresentationForWifiData(
      WifiSampleData data )
  {
    SpannableStringBuilder buffer = new SpannableStringBuilder();
    int pos = 0;
    
    // SSID
    buffer.append( data.getSSID() );
    buffer.setSpan( new ForegroundColorSpan( Color.CYAN ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // BSSID
    pos = buffer.length();
    buffer.append( "[ " );
    buffer.append( data.getBSSID() );
    buffer.append( " ]" );
    buffer.setSpan( new ForegroundColorSpan( Color.LTGRAY ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( LINE_BREAK );
    
    // frequency
    pos = buffer.length();
    buffer.append( Integer.toString( data.getFrequency() ) );
    buffer.append( " MHz" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // channel
    pos = buffer.length();
    buffer.append( "[ Channel " );
    buffer.append( Integer.toString( WLANChannelExtractor.getChannel( data.getFrequency() ) ) );
    buffer.append( " ]" );
    buffer.setSpan( new ForegroundColorSpan( Color.GREEN ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( SEPARATION );
    
    // signal level
    pos = buffer.length();
    buffer.append( Integer.toString( data.getLevel() ) );
    buffer.append( " dBm" );
    buffer.setSpan( new ForegroundColorSpan( Color.WHITE ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    buffer.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    buffer.append( LINE_BREAK );
    
    // Capabilities
    pos = buffer.length();
    buffer.append( data.getCapabilities() );
    buffer.setSpan( new ForegroundColorSpan( orange ), pos,
        buffer.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    return buffer;
  }
}
