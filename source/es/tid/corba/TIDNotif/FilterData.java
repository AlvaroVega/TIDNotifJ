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

import java.io.*;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;
import es.tid.util.parser.SimpleNode;

import org.omg.CosNotifyFilter.Filter;

public class FilterData extends CommonData implements java.io.Serializable
{
  public java.util.Hashtable constraintTable;

  transient public java.util.Hashtable filterTable;
  transient public int filter_counter;

  transient public Filter reference;

  //
  // Constructors
  //
  public FilterData(String id, org.omg.PortableServer.POA poa)
  {
    this.id = id;
    this.poa = poa;
    constraintTable = new java.util.Hashtable();
    filterTable = new java.util.Hashtable();
    filter_counter = 0;
    reference = NotifReference.discriminatorReference(poa, id);
  } 

  private void readObject(ObjectInputStream s)
                                     throws IOException, ClassNotFoundException
  {
    s.defaultReadObject();

    filter_counter = 0;
    filterTable = new java.util.Hashtable();
    for ( java.util.Iterator e =
                          constraintTable.entrySet().iterator(); e.hasNext(); )
    {
      java.util.Map.Entry entry = (java.util.Map.Entry)e.next();

      try
      {
        SimpleNode new_filter = TIDParser.parse((String)entry.getValue());
        filterTable.put((Integer)entry.getKey(), new_filter);
        filter_counter++;
      }
      catch (es.tid.util.parser.ParseException ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
      catch (java.lang.Exception ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }
}
