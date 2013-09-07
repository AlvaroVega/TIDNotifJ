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

import org.omg.CosNotifyFilter.MappingConstraintInfo;
import org.omg.CosNotifyFilter.MappingFilter;

public class MappingFilterData extends CommonData implements Serializable
{
  public final static String EXTRACT_LONG = "   ERROR: _default_value.type()";

  transient public MappingFilter reference;
  public org.omg.CORBA.Any default_value;
  public org.omg.CORBA.TypeCode value_type;
  public java.util.Hashtable assignedMappingConstraintInfoTable;
  public SimpleNode matches;

  transient public java.util.Hashtable filterTable;

  //
  // Constructor
  //
  public MappingFilterData( String id,
                            org.omg.CORBA.Any default_value )
  {
    this.id = id;
    this.default_value = default_value;
    try
    {
      value_type = default_value.type();
    }
    catch (Exception ex)
    {
      TIDNotifTrace.print(TIDNotifTrace.ERROR, EXTRACT_LONG);
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
    }
    assignedMappingConstraintInfoTable = new java.util.Hashtable();
    filterTable = new java.util.Hashtable();
    matches = null;
    reference = NotifReference.mappingDiscriminatorReference(poa, id);
  }

  private void readObject(ObjectInputStream s)
                                     throws IOException, ClassNotFoundException
  {
    s.defaultReadObject();
    filterTable = new java.util.Hashtable();
    for ( java.util.Iterator e =
                 assignedMappingConstraintInfoTable.entrySet().iterator(); e.hasNext(); )
    {
      java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
      MappingConstraintInfo info = (MappingConstraintInfo)entry.getValue();
      try
      {
        SimpleNode new_filter = TIDParser.parse( info.constraint_expression.constraint_expr );
        filterTable.put((String)entry.getKey(), new_filter);
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
