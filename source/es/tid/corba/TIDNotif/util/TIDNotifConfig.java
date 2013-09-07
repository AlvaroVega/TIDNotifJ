/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 80 $
* Date: $Date: 2008-04-14 12:29:50 +0200 (Mon, 14 Apr 2008) $
* Last modified by: $Author: avega $
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

public class TIDNotifConfig
{
  public static final int GLOBAL_POA    = 0;
  public static final int LOCAL_POA     = 1;
  public static final int EXCLUSIVE_POA = 2;

  // Root
  private static final String NOTIF_KEY_PREFIX  = "TIDNotif";

  // Parametros de configuracion para la base de datos
  public static final String DB_URL_KEY          = "TIDNotif.database.url";
  public static final String DB_USER_KEY         = "TIDNotif.database.user";
  public static final String DB_PASSWORD_KEY     = "TIDNotif.database.password";
  public static final String DB_POOLMAXCONNS_KEY = "TIDNotif.database.poolmaxconns";
  public static final String DB_CACHESIZE_KEY    = "TIDNotif.database.CacheSize";

  public static final String PERS_DB_KEY      = "TIDNotif.persistence.db";
  public static final String RECOVERY_BOOT_KEY= "TIDNotif.persistence.load";

  public static final String MAX_DISCONNECTED_TIME = 
                                     "TIDNotif.consumer.max_disconnected_time";
  public static final String MAX_COMM_FAILURES = 
                                         "TIDNotif.consumer.max_comm_failures";
  public static final String ON_COMM_FAILURE = 
                                           "TIDNotif.consumer.on_comm_failure";
  public static final String MAX_NO_RESPONSE = 
                                         "TIDNotif.consumer.max_no_response";
  public static final String ON_NO_RESPONSE = 
                                           "TIDNotif.consumer.on_no_response";

  //public static final String ORB_BUG_KEY        = "TIDNotif.orb.bug";
  public static final String SPEEDUP_POA_KEY    = "TIDNotif.poa.speedup";
  public static final String CORBA_CALLS_KEY    = "TIDNotif.corba.calls";

  public static final String TRACE_LEVEL_KEY    = "TIDNotif.trace.level";
  public static final String TRACE_DATE_KEY     = "TIDNotif.trace.date";
  public static final String TRACE_APPNAME_KEY  = "TIDNotif.trace.appname";
  public static final String TRACE_TO_FILE_KEY  = "TIDNotif.trace.tofile";
  public static final String TRACE_FILENAME_KEY = "TIDNotif.trace.filename";

  public static final String TRACE_FILESIZE_KEY = "TIDNotif.trace.file_size";
  public static final String TRACE_NUMFILES_KEY = "TIDNotif.trace.num_files";

  public static final String ADMIN_PORT_KEY     = "TIDNotif.orb.port";

  public static final String URL_MODE_KEY       = "TIDNotif.ior.urlmode";
  public static final String IOR_TO_FILE_KEY    = "TIDNotif.ior.tofile";
  public static final String IOR_PATH_KEY       = "TIDNotif.ior.path";
  public static final String IOR_FILENAME_KEY   = "TIDNotif.ior.filename";

  public static final String DATA_PATH_KEY      = "TIDNotif.data.path";
  public static final String DATA_ROOT_KEY      = "TIDNotif.data.root";
  public static final String OBJECTS_PATH_KEY   = "TIDNotif.objects.path";
  public static final String PROPS_FILENAME_KEY = "TIDNotif.property.filename";

  public static final String CHANNEL_POA_KEY    = "TIDNotif.channel.poa";
  public static final String SUPPLIER_POA_KEY   = "TIDNotif.supplier.poa";
  public static final String CONSUMER_POA_KEY   = "TIDNotif.consumer.poa";

  public static final String CONTENTION_KEY     = "TIDNotif.contention.active";
  public static final String CONTENT_TIME_KEY   = "TIDNotif.contention.time";
  public static final String FLOOD_EVENTS_KEY   = "TIDNotif.flood.numevents";
  public static final String FLOOD_TIME_KEY     = "TIDNotif.flood.time";

  public static final String RETURN_ON_ERROR_KEY="TIDNotif.constraint.return";

  public static final String TIME_DEBUG_KEY     = "TIDNotif.debug.time";

  public static final String ROOTPOA_QUEUE_KEY = "TIDNotif.rootpoa.queuesize";
  public static final String ROOTPOA_MAXTHR_KEY= "TIDNotif.rootpoa.maxthreads";
  public static final String ROOTPOA_MINTHR_KEY= "TIDNotif.rootpoa.minthreads";

  public static final String SUPPLIER_QUEUE_KEY = "TIDNotif.supplier.queuesize";
  public static final String SUPPLIER_MAXTHR_KEY="TIDNotif.supplier.maxthreads";
  public static final String SUPPLIER_MINTHR_KEY="TIDNotif.supplier.minthreads";

  public static final String CONSUMER_QUEUE_KEY = "TIDNotif.consumer.queuesize";
  public static final String CONSUMER_MAXTHR_KEY="TIDNotif.consumer.maxthreads";
  public static final String CONSUMER_MINTHR_KEY="TIDNotif.consumer.minthreads";

  public static final String CHANNEL_QUEUE_KEY = "TIDNotif.internal.queuesize";
  public static final String CHANNEL_MAXTHR_KEY="TIDNotif.internal.maxthreads";
  public static final String CHANNEL_MINTHR_KEY="TIDNotif.internal.minthreads";

  public static final String DATE_FORMAT_KEY = "TIDNotif.format.date";
  public static final String TIME_FORMAT_KEY = "TIDNotif.format.time";


  private static final String[][] configTable = {

  // none, file, oracle
  { PERS_DB_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "file" },

  { DB_URL_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "jdbc:oracle:user:@myhost:myport:mybbdd" },
  { DB_USER_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "owner1" },
  { DB_PASSWORD_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "owner1" },
  { DB_POOLMAXCONNS_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"5","0","99" },
  { DB_CACHESIZE_KEY,TIDConfig.NUMTYPE, TIDConfig.READ_WRITE,"1000","0","99999"},

  { MAX_DISCONNECTED_TIME,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"0","0","999999"},

  { MAX_COMM_FAILURES,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"-1","-1","99999"},
  // 0=none /  1=lock / 2=destroy
  { ON_COMM_FAILURE, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE, "0", "0", "2" },
  { MAX_NO_RESPONSE,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"-1","-1","99999"},
  // 0=none /  1=lock / 2=destroy
  { ON_NO_RESPONSE, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE, "0", "0", "2" },

  //{ ORB_BUG_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_ONLY, "false" },
  { SPEEDUP_POA_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_ONLY, "true" },
  { CORBA_CALLS_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_ONLY, "true" },

  { TRACE_LEVEL_KEY, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE, "0" , "0", "4"},
  { TRACE_DATE_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_WRITE, "true" },
  { TRACE_APPNAME_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "null" },
  { TRACE_TO_FILE_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_WRITE, "true" },
  { TRACE_FILENAME_KEY,TIDConfig.STRTYPE,TIDConfig.READ_WRITE,"tidnotif"},
  { TRACE_NUMFILES_KEY, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE,"1" ,"1","99"},
  { TRACE_FILESIZE_KEY, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE,
                                               "4096000" , "1024", "99999999"},

  // Directorio temporal donde almacenar schemas
  { DATA_PATH_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "." },
  { DATA_ROOT_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, ".notif" },
  { OBJECTS_PATH_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "db" },
  { PROPS_FILENAME_KEY, TIDConfig.STRTYPE, TIDConfig.READ_ONLY,"properties"},

  // No tiene mucha utilidad, solo para comprobar que es iidentico al del ORB
  { ADMIN_PORT_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"2002","1025","99999"},

  { URL_MODE_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_WRITE, "false" },
  { IOR_TO_FILE_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_WRITE, "true" },
  { IOR_PATH_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "./" },
  { IOR_FILENAME_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "agent" },

  // 0=none /  1=common / 2=exclusive
  // 0=global /  1=local
  { CHANNEL_POA_KEY, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE, "1" , "0", "1"},

  // 0=global /  1=local / 2=exclusive
  { SUPPLIER_POA_KEY, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE, "2" , "0", "2"},
  { CONSUMER_POA_KEY, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE, "2" , "0", "2"},

  // No se permitira que lleguen eventos al canal con un ritmo superior a
  // FLOOD_EVENTS_KEY por FLOOD_TIME_KEY, de ser asi se esperara 
  // CONTENT_TIME_KEY milliseconds
  { CONTENTION_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_WRITE, "false" },
  { CONTENT_TIME_KEY, TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"25","10","1000"},
  { FLOOD_EVENTS_KEY, TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"10","1","10000"},
  { FLOOD_TIME_KEY, TIDConfig.NUMTYPE, TIDConfig.READ_WRITE,"200","10","1000"},

  { RETURN_ON_ERROR_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_WRITE, "true" },

  { TIME_DEBUG_KEY, TIDConfig.BOOLTYPE, TIDConfig.READ_WRITE, "false" },

  { ROOTPOA_QUEUE_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"25000","0","99999"},
  { ROOTPOA_MAXTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"45","-1","99999"},
  { ROOTPOA_MINTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"5","0","99999" },

  { SUPPLIER_QUEUE_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"25000","0","99999"},
  { SUPPLIER_MAXTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"20","-1","99999"},
  { SUPPLIER_MINTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"-1","-1","99999"},

  { CONSUMER_QUEUE_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"75000","0","99999"},
  { CONSUMER_MAXTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"40","-1","99999"},
  { CONSUMER_MINTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"-1","-1","99999"},

  { CHANNEL_QUEUE_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"25000","0","99999"},
  { CHANNEL_MAXTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"25","-1","99999"},
  { CHANNEL_MINTHR_KEY,TIDConfig.NUMTYPE,TIDConfig.READ_WRITE,"-1","-1","99999"},

  { DATE_FORMAT_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "dd-MM-yyyy" },
  { TIME_FORMAT_KEY, TIDConfig.STRTYPE, TIDConfig.READ_WRITE, "HH:mm:ss" },

  };
  
  public static TIDConfig config = null;

  /**
   * Private constructor
   *
   */
  private TIDNotifConfig( java.util.Properties new_values )
  {
    config = new TIDConfig( NOTIF_KEY_PREFIX, configTable, new_values ); 
  }

  /**
   * 
   *
   */
  public static TIDNotifConfig init(java.util.Properties props)
  {
    if ( config == null)
    {
      return new TIDNotifConfig(props);
    }
    return null;
  }

  /**
   * 
   *
   */
  public static void updateConfig(java.lang.String[] args)
  {
    if ( config != null)
    {
      config.updateConfig(args);
    }
  }

  /**
   * 
   *
   */
  public static void updateConfig(java.util.Properties props)
  {
    if ( config != null)
    {
      config.updateConfig(props);
    }
  }


  public static String get( String key ) throws java.lang.IllegalStateException
  {
    if (config == null)
    {
      throw new java.lang.IllegalStateException();
    }
    return config.get(key);
  }

  public static int getInt( String key ) throws java.lang.IllegalStateException
  {
    if (config == null)
    {
      throw new java.lang.IllegalStateException();
    }

    try
    {
      return Integer.parseInt(config.get(key));
    }
    catch (Exception e) {}

    return 0;
  }

  public static boolean getBool( String key ) throws java.lang.IllegalStateException
  {
    if (config == null)
    {
      throw new java.lang.IllegalStateException();
    }
    try
    {
      if (TIDConfig.TRUE_VALUE.compareTo(config.get(key)) == 0)      
      {
        return true;
      }
      else
      {
        return false;
      }
    }
    catch (Exception e) {}

    return false;
  }

  public static void set( String key, String value )
                                         throws java.lang.IllegalStateException
  {
    if (config == null)
    {
      throw new java.lang.IllegalStateException();
    }
    config.set(key, value);
  }

  public static void dump() throws java.io.IOException
  {
    if ( config != null)
    {
      config.store();
    }
  }

  public static void restore() throws java.io.IOException
  {
    if ( config != null)
    {
      config.load();
    }
  }
}
