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
* (C) Copyright 2005 Telef�nica Investigaci�n y Desarrollo
*     S.A.Unipersonal (Telef�nica I+D)
*
* Info about members and contributors of the MORFEO project
* is available at:
*
* (C) Copyright 2005 Telefónica Investigación y Desarrollo
*     S.A.Unipersonal (Telefónica I+D)
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

import java.io.PrintWriter;

public class TIDNotifTrace 
{
  public final static int NONE = 0;
  public final static int ERROR = 1;
  public final static int USER = 2;
  public final static int DEBUG = 3;
  public final static int DEEP_DEBUG = 4;

  private static final String TRACE_EXT = ".out";

  private static final String APPNAME = "";

  private static es.tid.util.trace.Trace notiftrace = null;

  //private static boolean check_path(String pathname)
  //{
    //java.io.File f = new java.io.File(pathname);
    //if (f.exists()) return true;
    //return f.mkdir();
  //}

  private static void _init(int level)
  {
    if (level > 0)
    {
      if (notiftrace != null)
      {
        notiftrace.close();
      }
      String appname = TIDNotifConfig.get(TIDNotifConfig.TRACE_APPNAME_KEY);
      if (appname.compareTo("null") == 0)
      {
        appname = APPNAME;
      }
      if (TIDNotifConfig.getBool(TIDNotifConfig.TRACE_TO_FILE_KEY))
      {
          StringBuffer the_file = new StringBuffer();
          the_file.append(TIDNotifConfig.get(TIDNotifConfig.DATA_PATH_KEY));
          if (!TIDNotifUtil.check_path(the_file.toString(), true))
          {
            System.err.println("\nServer: Cannot log trace.");
            notiftrace = null;
            level = 0;
            return;
          }
          the_file.append(java.io.File.separatorChar);
          the_file.append(TIDNotifConfig.get(
                                        TIDNotifConfig.TRACE_FILENAME_KEY) );
          the_file.append('_');
          the_file.append(TIDNotifConfig.get(
                                            TIDNotifConfig.ADMIN_PORT_KEY) );
          the_file.append(TRACE_EXT);

          if (!TIDNotifUtil.check_filepath(the_file.toString(), true))
          {
            System.err.println("\nServer: Cannot log trace. (2)");
            notiftrace = null;
            level = 0;
            return;
          }
        try 
        {
          int num_files =
                      TIDNotifConfig.getInt(TIDNotifConfig.TRACE_NUMFILES_KEY);
          if (num_files > 1)
          {
            es.tid.util.trace.CircularTraceFile ctf =
                new es.tid.util.trace.CircularTraceFile (num_files,
                     TIDNotifConfig.getInt(TIDNotifConfig.TRACE_FILESIZE_KEY),
                                              the_file.append(".").toString());

            notiftrace= es.tid.util.trace.Trace.create_trace(ctf,appname,level);
          }
          else
          {
            notiftrace = es.tid.util.trace.Trace.create_trace(
                                          the_file.toString(), appname, level);
          }
        }
        catch (Exception e)
        {
          System.err.println("\nServer: Cannot log trace. (3)");
          notiftrace = null;
          level = 0;
          return;
        }
      }
      else
      {
        try 
        {
          notiftrace = es.tid.util.trace.Trace.create_trace(
                          new java.io.OutputStreamWriter(System.err),
                                                              appname, level );
        }
        catch (Exception e)
        {
          System.err.println("\nServer: Cannot log trace. (4)");
          notiftrace = null;
          level = 0;
          return;
        }
      }
      notiftrace.setIncludeDate(
                       TIDNotifConfig.getBool(TIDNotifConfig.TRACE_DATE_KEY) );
      notiftrace.setDoFlushAt(1);
    }
  }

  public static void init()
  {
    int level = TIDNotifConfig.getInt(TIDNotifConfig.TRACE_LEVEL_KEY);
    _init(level);
  }

  public static void setLevel( int level )
  {
    if (notiftrace == null)
    {
      _init(level);
    }
    else
    {
      if ( (level >= es.tid.util.trace.Trace.NONE) && 
           (level <= es.tid.util.trace.Trace.DEEP_DEBUG) )
      {
        notiftrace.setLevel(level);
      }
    }
  }

  public static PrintWriter getLog()
  {
    if (notiftrace != null)
    {
      return notiftrace.getLog();
    }
    return null;
  }

  public static void print( int level, String message )
  {
    if (notiftrace != null)
    {
      notiftrace.print(level, message);
    }
  }

  public static void print( int level, String[] message )
  {
    if (notiftrace != null)
    {
      notiftrace.print(level, message);
    }
  }

  public static void printStackTrace( int level, Exception  e )
  {
    if (notiftrace != null)
    {
      notiftrace.printStackTrace(level, e);
    }
  }

  public static es.tid.util.trace.Trace getTrace()
  {
    return notiftrace;
  }

  //
  // METODOS PARA SACAR TRAZAS SOBRE FUNCIONES ESPECIFICAS
  // =====================================================
  //

}
