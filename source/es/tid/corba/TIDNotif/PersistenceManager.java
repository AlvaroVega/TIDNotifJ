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

package es.tid.corba.TIDNotif;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;

import org.omg.IOP.Codec;
import org.omg.IOP.Encoding;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryHelper;

public class PersistenceManager
{
  public static final String NONE_TYPE = "none";
  public static final String ORACLE_TYPE = "oracle";
  public static final String FILE_TYPE = "file";

  private static boolean initialized = false;
  private static PersistenceDB _persistence_db = null;

  public static org.omg.CORBA.ORB _orb = null;
  public static Codec _codec = null;

  public static void init( String db, String id, org.omg.CORBA.ORB orb ) 
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, 
                               "PersistenceManager.init(): <"+db+"> ["+id+"]");
    _orb = orb;
    try
    {
      _codec = ((CodecFactory)CodecFactoryHelper.narrow(
        _orb.resolve_initial_references("CodecFactory"))).create_codec(
          new Encoding(org.omg.IOP.ENCODING_CDR_ENCAPS.value,(byte)1,(byte)2));
    }
    catch (Exception ex)
    {
      //TIDAdapterTrace.printStackTrace(TIDAdapterTrace.ERROR, ex);
      throw new org.omg.CORBA.INITIALIZE("Error create_codec()");
    }

    if (initialized)
    {
      throw new org.omg.CORBA.INITIALIZE("Already Initialized");
    }

    if (db.compareTo(NONE_TYPE) == 0)
    {
        //initialized = false;
        initialized = true;
        _persistence_db =
            new es.tid.corba.TIDNotif.none.NONEPersistenceDB(id);
    }
    else if (db.compareTo(FILE_TYPE) == 0)
    {
      initialized = true;
      _persistence_db = new es.tid.corba.TIDNotif.file.FILEPersistenceDB(id);
    }
    else if (db.compareTo(ORACLE_TYPE) == 0)
    {
      initialized = false;
      //initialized = true;
      //_persistence_db =
                  //new es.tid.corba.TIDNotif.oracle.SQLPersistenceDB(id, orb);
    }
    else
      throw new org.omg.CORBA.NO_IMPLEMENT();
  }

  public static PersistenceDB getDB()
  {
    return _persistence_db;
  }

  public static boolean isActive()
  {
    return (_persistence_db != null);
  }
}
