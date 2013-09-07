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

import es.tid.corba.TIDNotif.qos.ProxyQoSAdmin;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;

import org.omg.ServiceManagement.OperationMode;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;

import org.omg.CORBA.ORB;
import org.omg.CosEventComm.PushSupplier;
import org.omg.CosEventComm.PushSupplierHelper;
import org.omg.CosNotifyComm.StructuredPushSupplier;

import es.tid.corba.ExceptionHandlerAdmin.ExceptionHandler;
import es.tid.corba.ExceptionHandlerAdmin.ExceptionHandlerHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannel;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdminHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalProxyPushConsumer;

public class ProxyPushConsumerData extends CommonData implements java.io.Serializable
{
  public String name;        // Nombre del Objeto
  public int type;           // CosNotifyChannelAdmin::ProxyType
  public String admin_id;
  public String channel_id;
  public boolean connected;
  public boolean destroying;

  transient public OperationMode operation_mode;
  transient public OperationalState operational_state;
  transient public AdministrativeState administrative_state;
  transient public boolean narrowed;
  transient public org.omg.CORBA.Object push_supplier_object;
  transient public PushSupplier pushSupplier;
  transient public StructuredPushSupplier structuredPushSupplier;
  
  
  transient public String operator_ir;
  transient public TransformingOperatorData transforming_operator;
  transient public ExceptionHandler exception_handler;
  transient public InternalSupplierAdmin supplierAdmin;
  transient public InternalDistributionChannel notificationChannel;
  
  transient public InternalProxyPushConsumer reference;
  
  public ProxyQoSAdmin qosAdminDelegate;
  public FilterAdminImpl 		 filterAdminDelegate;
  

  // Tiempos
  public long num_received_events;
  public long last_event_arrive_time;
  public long max_proccessing_time;
  public long total_proccessing_time;
  public long total_proccessed_events;

  public long eventCount;

  final static String NARROW_ERROR =
                              " *** WARNING *** narrow() push supplier client";

  public ProxyPushConsumerData( String name,
                                int type,
                                String id,
                                org.omg.PortableServer.POA poa, 
                                OperationMode operation_mode,
                                String admin_id, String channel_id,
                                InternalSupplierAdmin supplierAdmin,
                                ORB orb)
  {
    this.name = name;
    this.id = id;
    this.poa = poa;
    this.operation_mode = operation_mode;
    this.admin_id = admin_id;
    this.channel_id = channel_id;
    this.supplierAdmin = supplierAdmin;

    connected = false;

    operational_state = OperationalState.enabled;
    administrative_state = AdministrativeState.unlocked;

    narrowed = false;
    push_supplier_object = null;
    pushSupplier = null;    
    operator_ir = null;
    transforming_operator = null;
    exception_handler = null;

    qosAdminDelegate = new ProxyQoSAdmin(orb);
    filterAdminDelegate = new FilterAdminImpl();
    
    reference = NotifReference.iProxyPushConsumerReference(
    	poa, 
        id, 
        qosAdminDelegate.getPolicies()
	);
    
    destroying = false;

    eventCount = 0;

    // Tiempos
    num_received_events = 0;
    last_event_arrive_time = 0;
    //last_event_arrive_time = System.currentTimeMillis();
    max_proccessing_time = 0;
    total_proccessing_time = 0;
    total_proccessed_events = 0;
  }

  private void writeObject(ObjectOutputStream s) throws IOException
  {
    //
    s.defaultWriteObject();

    // 
    s.writeInt(operation_mode.value());
    s.writeInt(operational_state.value());
    s.writeInt(administrative_state.value());


    // Operator id
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
      // Parent reference
      writeCorbaObject( supplierAdmin, InternalSupplierAdminHelper.type(), s);
      writeCorbaObject(notificationChannel, 
                       InternalDistributionChannelHelper.type(),s);

      // Client reference
      if (connected)
      {
        writeCorbaObject(pushSupplier, PushSupplierHelper.type(), s);
      }

      // Exception handler reference
      if (exception_handler == null)
      {
        s.writeBoolean(false);
      }
      else
      {
        s.writeBoolean(true);
        writeCorbaObject(exception_handler, ExceptionHandlerHelper.type(), s);
      }
    }
    catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.DEBUG, ex);
    }
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
      supplierAdmin = InternalSupplierAdminHelper.narrow(readCorbaObject(s));
      notificationChannel = 
          InternalDistributionChannelHelper.narrow(readCorbaObject(s));

      if (connected)
      {
        push_supplier_object = readCorbaObject(s);
/*
        try
        {
          pushSupplier = PushSupplierHelper.narrow(push_supplier_object);
          narrowed = true;
        }
        catch (java.lang.Exception ex)
        {
          TIDNotifTrace.print(TIDNotifTrace.DEBUG, NARROW_ERROR);
          pushSupplier = null;
          narrowed = false;
        }
*/
      }

      b = s.readBoolean();
      if (b)
      {
        exception_handler = ExceptionHandlerHelper.narrow(readCorbaObject(s));
      }
      else
      {
        exception_handler = null;
      }
    }
    catch (org.omg.IOP.CodecPackage.FormatMismatch ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.DEBUG, ex);
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
