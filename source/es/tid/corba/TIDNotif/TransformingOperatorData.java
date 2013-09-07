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

//import org.omg.CORBA.Object;

import org.omg.CosNotifyFilter.ConstraintExp;
import org.omg.TransformationAdmin.TransformingRule;

import org.omg.TransformationInternalsAdmin.InternalTransformingOperator;

public class TransformingOperatorData extends CommonData implements Serializable
{
  private static final String ERROR_1 =
             "TransformingOperatorData.readObject(ObjectInputStream s): ERROR";

  public java.util.TreeMap orderTable;
  public int order_gap;
  public int last_rule_order;
  transient public java.util.Hashtable assignedTransformingRuleTable;
  transient public java.util.Hashtable filterTable;
  transient public InternalTransformingOperator reference;

  //
  // Constructors
  //
  public TransformingOperatorData( String id, 
                                   org.omg.PortableServer.POA poa )
  {
    this.id = id;
    this.poa = poa;
    assignedTransformingRuleTable = new java.util.Hashtable();
    filterTable = new java.util.Hashtable();
    orderTable = new java.util.TreeMap();
    order_gap = 100;
    last_rule_order = 0;
    reference = NotifReference.iTransformingOperatorReference(poa, id);
  }

  private void readObject(ObjectInputStream s)
                                     throws IOException, ClassNotFoundException
  {
    s.defaultReadObject();
    assignedTransformingRuleTable = new java.util.Hashtable();
    int i = s.readInt();
    try
    {
      for (int j=0; j<i; j++)
      {
        assignedTransformingRuleTable.put( (String)s.readObject(), 
         new TransformingRule( (ConstraintExp) s.readObject(), 
                               readCorbaObject(s),
                               (String) s.readObject() ) );
      }

      filterTable = new java.util.Hashtable();

      for ( java.util.Iterator e =
           assignedTransformingRuleTable.entrySet().iterator(); e.hasNext(); )
      {
        java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
        org.omg.TransformationAdmin.TransformingRule rule =
                (org.omg.TransformationAdmin.TransformingRule)entry.getValue();
        try
        {
          SimpleNode new_filter = TIDParser.parse(rule.expression.constraint_expr);
          filterTable.put( (String)entry.getKey(), new_filter);
        }
        catch (es.tid.util.parser.ParseException ex)
        {
          TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
        }
      }
    }
    catch (Exception ex) // org.omg.IOP.CodecPackage.FormatMismatch;
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      throw new IOException();
    }
  }

  private void writeObject(ObjectOutputStream s) throws IOException
  {
    s.defaultWriteObject();
    s.writeInt(assignedTransformingRuleTable.size());
    try
    {
      for ( java.util.Iterator e =
            assignedTransformingRuleTable.entrySet().iterator(); e.hasNext(); )
      {
        java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
        s.writeObject((String)entry.getKey());
        TransformingRule rule = (TransformingRule)entry.getValue();
        s.writeObject(rule.expression);
        writeCorbaObject(rule.object_ref, null, s);
        s.writeObject(rule.operation);
      }
    }
    catch (Exception ex) // org.omg.IOP.CodecPackage.InvalidTypeForEncoding; 
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      throw new IOException();
    }
  }

  public void writeCorbaObject( org.omg.CORBA.Object object,
                                org.omg.CORBA.TypeCode typecode,
                                ObjectOutputStream s )
                         throws java.io.IOException,
                                org.omg.IOP.CodecPackage.InvalidTypeForEncoding
  {
    org.omg.CORBA.Any any = PersistenceManager._orb.create_any();
    if (typecode == null)
    {
      any.insert_Object(object);
    }
    else
    {
      any.insert_Object(object, typecode);
    }
    //
    //TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, WRITE_OBJECT);
    //
    writeData(any, s);
  }

  public org.omg.CORBA.Object readCorbaObject( ObjectInputStream s )
                                 throws java.io.IOException,
                                        org.omg.IOP.CodecPackage.FormatMismatch
  {
    //
    //TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, READ_OBJECT);
    //
    return readData(s).extract_Object();
  }

  public void writeData( org.omg.CORBA.Any any,
                         ObjectOutputStream s )
                         throws java.io.IOException,
                                org.omg.IOP.CodecPackage.InvalidTypeForEncoding
  {
    byte[] cdr_data = PersistenceManager._codec.encode(any);
    //
    //WRITE_DATA[1] = Integer.toString(cdr_data.length);
    //TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, WRITE_DATA);
    //
    s.writeInt(cdr_data.length);
    s.write(cdr_data, 0, cdr_data.length);
  }

  public org.omg.CORBA.Any readData( ObjectInputStream s )
                                 throws java.io.IOException,
                                        org.omg.IOP.CodecPackage.FormatMismatch
  {
    int pos = 0;
    int sz = s.readInt();
    byte[] cdr_data = new byte[sz];
    do
    {
      //
      //READ_DATA[1] = Integer.toString(pos);
      //READ_DATA[3] = Integer.toString(sz);
      //TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, READ_DATA);
      //
      int rd = s.read(cdr_data, pos, sz);
      if (rd == -1) throw new java.io.IOException();
      sz = sz - rd;
      pos = pos + rd;
    } while (sz > 0);
    //
    //DATA_READED[1] = Integer.toString(cdr_data.length);
    //TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DATA_READED);
    //
    org.omg.CORBA.Any any = PersistenceManager._codec.decode(cdr_data);
    return any;
  }
}
