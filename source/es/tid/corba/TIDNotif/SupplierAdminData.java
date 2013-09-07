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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.omg.CORBA.ORB;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannel;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdminHelper;
import org.omg.ServiceManagement.AdministrativeState;
import org.omg.ServiceManagement.OperationMode;
import org.omg.ServiceManagement.OperationalState;

import es.tid.corba.ExceptionHandlerAdmin.ExceptionHandler;
import es.tid.corba.ExceptionHandlerAdmin.ExceptionHandlerHelper;
import es.tid.corba.TIDNotif.qos.ProxyQoSAdmin;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;

public class SupplierAdminData extends CommonData 
	implements java.io.Serializable
{
  private static final String NULL = "";

  public String name;        // Nombre del Objeto
  public String channel_id;
  public boolean destroying;
  public int level;

  transient public OperationMode operation_mode;
  transient public OperationalState operational_state;
  transient public AdministrativeState administrative_state;

  transient public SortedMap proxyPushConsumerTable;
  transient public SortedMap proxyPullConsumerTable;
    
  transient public ExceptionHandler exception_handler;

  // Notification Channel al que pertenece el SupplierAdmin (su padre)
  transient public InternalDistributionChannel notificationChannel;
  transient public InternalSupplierAdmin supplierAdminParent;
  transient public InternalSupplierAdmin supplierAdminChild;
  transient public InternalSupplierAdmin reference;

  transient public TransformingOperatorData transforming_operator;
  transient public String operator_ir;

  public AdminCriteria associatedCriteria;
  
  public FilterAdminImpl filterAdminDelegate;
  ProxyQoSAdmin qosAdminDelegate;



  public SupplierAdminData( String name,
                            String id,
                            String channel_id,
                            org.omg.PortableServer.POA poa,
                            InternalDistributionChannel channel,
                            AdminCriteria criteria,
                            OperationMode operation_mode,
							ORB orb)
  {
    this.name = name;
    this.id = id;
    this.channel_id = channel_id;
    this.poa = poa;
    this.operation_mode = operation_mode;

    operational_state = OperationalState.enabled;
    administrative_state = AdministrativeState.unlocked;

    exception_handler = null;
    transforming_operator = null;

    level = 0;
    proxyPushConsumerTable = Collections.synchronizedSortedMap( new TreeMap() );
    proxyPullConsumerTable = Collections.synchronizedSortedMap( new TreeMap() );

    notificationChannel = channel;

    // default criteria 
    associatedCriteria = new AdminCriteria(criteria);

    this.qosAdminDelegate = new ProxyQoSAdmin( orb );
    reference = 
        NotifReference.internalSupplierAdminReference(poa, 
                                                      id,
                                                      qosAdminDelegate.getPolicies());

    destroying = false;
    
	this.filterAdminDelegate = new FilterAdminImpl();
	

  }

  private void writeObject(ObjectOutputStream s) throws IOException
  {
    //
    s.defaultWriteObject();

    // 
    s.writeInt(operation_mode.value());
    s.writeInt(operational_state.value());
    s.writeInt(administrative_state.value());

    if (transforming_operator == null)
    {
      s.writeBoolean(false);
    }
    else
    {
      s.writeBoolean(true);
      s.writeObject(transforming_operator.id);
    }

    try
    {
      writeCorbaObject(
             notificationChannel, InternalDistributionChannelHelper.type(), s);

      if (exception_handler == null)
      {
        s.writeBoolean(false);
      }
      else
      {
        s.writeBoolean(true);
        writeCorbaObject(exception_handler, ExceptionHandlerHelper.type(), s);
      }
      if (supplierAdminParent == null)
      {
        s.writeBoolean(false);
      }
      else
      {
        s.writeBoolean(true);
        writeCorbaObject(
                   supplierAdminParent, InternalSupplierAdminHelper.type(), s);
      }
      if (supplierAdminChild == null)
      {
        s.writeBoolean(false);
      }
      else
      {
        s.writeBoolean(true);
        writeCorbaObject(
                    supplierAdminChild, InternalSupplierAdminHelper.type(), s);
      }
    }
    catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.DEBUG, ex);
    }

    //s.writeObject(proxyPushConsumerTable.ids());
    java.util.Vector v = new java.util.Vector();
    synchronized(proxyPushConsumerTable)
    {
      for ( java.util.Iterator e = proxyPushConsumerTable.values().iterator(); 
                                                                 e.hasNext(); )
      {
        v.add(((CommonData)e.next()).id);
      }
    }
    s.writeObject(v);
    v.clear();

    //s.writeObject(proxyPullConsumerTable.ids());
    v = new java.util.Vector();
    synchronized(proxyPullConsumerTable)
    {
      for ( java.util.Iterator e = proxyPullConsumerTable.values().iterator(); 
                                                                 e.hasNext(); )
      {
        v.add(((CommonData)e.next()).id);
      }
    }
    s.writeObject(v);
    v.clear();
  }

  private void readObject(ObjectInputStream s)
                                     throws IOException, ClassNotFoundException
  {
    //
    s.defaultReadObject();
    
    
    this.qosAdminDelegate.setORB(PersistenceManager._orb);

    // 
    operation_mode = OperationMode.from_int(s.readInt());
    operational_state = OperationalState.from_int(s.readInt());
    administrative_state = AdministrativeState.from_int(s.readInt());

    boolean b = s.readBoolean();
   
    if (b)
    {
      operator_ir = (String) s.readObject();
    }
    else
    {
      operator_ir = null;
      transforming_operator = null;
    }

    try
    {
      notificationChannel = 
                  InternalDistributionChannelHelper.narrow(readCorbaObject(s));

      b = s.readBoolean();
      if (b)
      {
        exception_handler = ExceptionHandlerHelper.narrow(readCorbaObject(s));
      }
      else
      {
        exception_handler = null;
      }
      b = s.readBoolean();
      if (b)
      {
        supplierAdminParent = 
                        InternalSupplierAdminHelper.narrow(readCorbaObject(s));
      }
      else
      {
        supplierAdminParent = null;
      }
      b = s.readBoolean();
      if (b)
      {
        supplierAdminChild = 
                        InternalSupplierAdminHelper.narrow(readCorbaObject(s));
      }
      else
      {
        supplierAdminChild = null;
      }
    }
    catch (org.omg.IOP.CodecPackage.FormatMismatch ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.DEBUG, ex);
    }

    proxyPushConsumerTable = Collections.synchronizedSortedMap( new TreeMap() );
    java.util.Vector v = (java.util.Vector)s.readObject();
    for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
    {
      proxyPushConsumerTable.put((String)e.nextElement(), NULL);
    }

    proxyPullConsumerTable = Collections.synchronizedSortedMap( new TreeMap() );
    v = (java.util.Vector)s.readObject();
    for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
    {
      proxyPullConsumerTable.put((String)e.nextElement(), NULL);
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
