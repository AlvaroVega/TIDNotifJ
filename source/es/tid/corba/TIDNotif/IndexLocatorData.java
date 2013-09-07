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

import org.omg.IndexAdmin.IndexLocator;
import org.omg.IndexAdmin.IndexType;

public class IndexLocatorData extends CommonData implements java.io.Serializable
{
  transient public String event_field_reference;
  transient public SimpleNode event_field_filter;
  public int filter_counter;
  transient public IndexType returned_index_type;
  transient public String default_string_index;;
  public int default_int_index;;
  transient public IndexLocator reference;

  //
  // Constructors
  //
  public IndexLocatorData(String id, org.omg.PortableServer.POA poa)
  {
    this.id = id;
    this.poa = poa;

    event_field_reference = null;
    event_field_filter = null;
    filter_counter = 0;
    returned_index_type = IndexType.string_index_type;
    default_string_index = null;
    default_int_index = -1;
    reference = NotifReference.indexLocatorReference(poa, id);
  } 

  private void writeObject(ObjectOutputStream s) throws IOException
  {
    //
    s.defaultWriteObject();

    if (filter_counter == 1)
    {
      s.writeObject(event_field_reference);
    }
    //
    s.writeInt(returned_index_type.value());
    //
    if (default_string_index == null)
    {
      s.writeBoolean(false);
    }
    else
    {
      s.writeBoolean(true);
      s.writeObject(default_string_index);
    }
  }

  private void readObject(ObjectInputStream s)
                                     throws IOException, ClassNotFoundException
  {
    s.defaultReadObject();

    if (filter_counter == 1)
    {
      String event_field_reference = (String)s.readObject();
      try
      {
        event_field_filter = TIDParser.parse(event_field_reference);
      }
      catch (es.tid.util.parser.ParseException ex)
      {
        filter_counter = 0;
        event_field_filter = null;
        event_field_reference = null;
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
      catch (java.lang.Exception ex)
      {
        filter_counter = 0;
        event_field_filter = null;
        event_field_reference = null;
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
    else
    {
      filter_counter = 0;
      event_field_filter = null;
      event_field_reference = null;
    }

    returned_index_type = IndexType.from_int(s.readInt());
    boolean b = s.readBoolean();
    if (b)
    {
      default_string_index = (String) s.readObject();
    }
    else
    {
      default_string_index = null;
    }
  }
}

