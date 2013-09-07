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

import java.util.Hashtable;

import es.tid.TIDorbj.core.poa.OID;

public class DataTable2
{
  private static final String NULL = "";

  public Hashtable table;

  public DataTable2()
  {
    table = new Hashtable();
  }

  public boolean containsKey(OID key)
  {
    return table.containsKey(key);
  }

  public void put(OID key, CommonData data)
  {
    if (data != null)
      table.put( key, data );
    else
      table.put( key, NULL );
  }

  public CommonData get(OID key)
  {
    return (CommonData)table.get(key);
  }

  public java.util.Vector values()
  {
    java.util.Vector v = new java.util.Vector();
    synchronized(table)
    {
      for ( java.util.Iterator e=table.values().iterator(); e.hasNext(); )
      {
        v.add((CommonData)e.next());
      }
    }
    return v;
  }

  public java.util.Vector keys()
  {
    java.util.Vector v = new java.util.Vector();
    synchronized(table)
    {
      for ( java.util.Enumeration e=table.keys(); e.hasMoreElements(); )
      {
        v.add((OID)e.nextElement());
      }
    }
    return v;
  }

  public java.util.Vector ids()
  {
    java.util.Vector v = new java.util.Vector();
    synchronized(table)
    {
      for ( java.util.Iterator e=table.values().iterator(); e.hasNext(); )
      {
        v.add(((CommonData)e.next()).id);
      }
    }
    return v;
  }

  public java.util.Set entrySet()
  {
    return table.entrySet();
  }

  public int size()
  {
    return table.size();
  }

  public void clear()
  {
    synchronized(table)
    {
      table.clear();
    }
  }

  public CommonData remove(OID key)
  {
    synchronized(table)
    {
      return (CommonData)table.remove(key);
    }
  }
}
