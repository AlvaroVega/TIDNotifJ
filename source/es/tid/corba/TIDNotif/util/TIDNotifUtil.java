/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 3 $
* Date: $Date: 2006-01-17 17:42:13 +0100 (Tue, 17 Jan 2006) $
* Last modified by: $Author: aarranz $
*
* (C) Copyright 2005 Telefónica Investigación y Desarrollo
*     S.A.Unipersonal (Telefónica I+D)
*
* Info about members and contributors of the MORFEO project
* is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/CREDITS
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
* If you want to use this software an plan to distribute a
* proprietary application in any way, and you are not licensing and
* distributing your source code under GPL, you probably need to
* purchase a commercial license of the product.  More info about
* licensing options is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/Licensing
*/ 

package es.tid.corba.TIDNotif.util;


public class TIDNotifUtil
{
  private static final String IOR_EXT = ".ior";

  // PATHS
  private static String properties_file = null;

  public static boolean check_path(String pathname, boolean mk_dir)
  {
    java.io.File f = new java.io.File(pathname);
    if (f.exists()) return true;
    if (mk_dir)
    {
      return f.mkdir();
    }
    return false;
  }

  public static boolean check_filepath(String pathname, boolean mk_dir)
  {
    java.io.File f = new java.io.File((new java.io.File(pathname)).getParent());
    if (f.exists()) return true;
    if (mk_dir)
    {
      return f.mkdir();
    }
    return false;
  }

  public static String tidConfigFilename(boolean mkdir)
                                                     throws java.io.IOException
  {
    if (properties_file == null)
    {
      StringBuffer the_file = new StringBuffer();
      the_file.append( TIDNotifConfig.get(TIDNotifConfig.DATA_PATH_KEY) );
      if (!check_path(the_file.toString(), mkdir)) 
        throw new java.io.IOException();
      the_file.append(java.io.File.separatorChar);
      the_file.append( TIDNotifConfig.get(TIDNotifConfig.DATA_ROOT_KEY) );
      if (!check_path(the_file.toString(),mkdir)) 
        throw new java.io.IOException();
      the_file.append(java.io.File.separatorChar);
      the_file.append( TIDNotifConfig.get(TIDNotifConfig.ADMIN_PORT_KEY));
      if (!check_path(the_file.toString(),mkdir)) 
        throw new java.io.IOException();
      the_file.append(java.io.File.separatorChar);
      the_file.append( TIDNotifConfig.get(TIDNotifConfig.PROPS_FILENAME_KEY) );
      properties_file = the_file.toString();
    }
    return properties_file;
  }


  public static void exportReference( String reference ) 
                                                     throws java.io.IOException
  {
    if ( TIDNotifConfig.getBool(TIDNotifConfig.IOR_TO_FILE_KEY) == true )
    {
      StringBuffer the_file = new StringBuffer();
      the_file.append(TIDNotifConfig.get(TIDNotifConfig.DATA_PATH_KEY));
      if (!check_path(the_file.toString(), true))
      {
        System.err.println("Warning: Cannot access TIDNotif.data.path");
        //throw new java.io.IOException();
        return;
      }
      the_file.append(java.io.File.separatorChar);
      the_file.append(TIDNotifConfig.get(TIDNotifConfig.IOR_FILENAME_KEY));
      the_file.append('_');
      the_file.append(TIDNotifConfig.get(TIDNotifConfig.ADMIN_PORT_KEY));
      the_file.append(IOR_EXT);

      if (!check_filepath(the_file.toString(), true))
      {
        System.err.println("\nWarning: Cannot write agent reference to file.");
      }
      else
      {
        try
        {
          java.io.FileOutputStream file =
                             new java.io.FileOutputStream(the_file.toString());
          java.io.PrintStream pfile = new java.io.PrintStream(file);
          pfile.println(reference);
        }
        catch ( java.io.IOException ex )
        {
         System.err.println("\nWarning: Cannot write agent reference to file.");
          //ex.printStackTrace();
          //System.exit(-1);
        }
      }
    }
    else
    {
      System.out.println(reference);
    }
  }
}
