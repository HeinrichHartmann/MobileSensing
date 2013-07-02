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
package de.unikassel.android.sdcframework.app.facade;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import de.unikassel.android.sdcframework.R;

/**
 * Dialog utility class.
 * 
 * @author Katy Hilgenberg
 *
 */
public class DialogUtils
{   
  /**
   * Does display an alert message box.
   * 
   * @param context
   *          the application context
   * @param msg
   *          the message to display
   */
  public static final void showAlertMessage( Context context, String msg )
  {
    final AlertDialog alertDialog = new AlertDialog.Builder( context ).create();
    alertDialog.setTitle( context.getResources().getText( R.string.sdc_app_name ) );
    alertDialog.setMessage( msg );
    alertDialog.setButton( context.getResources().getText(
        R.string.str_ok ), new DialogInterface.OnClickListener()
    {
      public void onClick( DialogInterface dialog, int whichButton )
      {
        alertDialog.cancel();
      }
    } );
    alertDialog.setCancelable( true );
    alertDialog.setCanceledOnTouchOutside( true );
    alertDialog.show();
  }
}
