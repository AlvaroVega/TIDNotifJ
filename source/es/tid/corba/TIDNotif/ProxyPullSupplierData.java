/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 98 $
* Date: $Date: 2008-10-27 11:17:20 +0100 (Mon, 27 Oct 2008) $
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

package es.tid.corba.TIDNotif;

import java.io.*;

import es.tid.corba.TIDNotif.qos.ProxyQoSAdmin;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;

import org.omg.CORBA.ORB;
import org.omg.CosEventComm.PullConsumer;
import org.omg.CosEventComm.PullConsumerHelper;
import org.omg.CosNotification.FifoOrder;

import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdminHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannel;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullSupplier;

public class ProxyPullSupplierData extends CommonData implements Serializable
{
  public String name;        // Nombre del Objeto
  public int type;           //ProxyType
  public String admin_id;
  public String channel_id;
  public boolean connected;
  public boolean destroying;
  // EventQueue
  public EventQueue eventQueue;

  transient public OperationalState operational_state;
  transient public AdministrativeState administrative_state;
  transient public boolean narrowed;
  transient public org.omg.CORBA.Object pull_consumer_object;
  transient public PullConsumer pullConsumer;
  transient public InternalDistributionChannel notificationChannel;
  
  transient public InternalConsumerAdmin consumerAdmin;
  transient InternalProxyPullSupplier reference;
  
  public ProxyQoSAdmin qosAdminDelegate;
  public FilterAdminImpl 		 filterAdminDelegate;


  // private EventQueue _eventQueue;
  final static String NARROW_ERROR =
                              " *** WARNING *** narrow() pull consumer client";

  public ProxyPullSupplierData( String name,
                                int type,
                                String id, 
                                org.omg.PortableServer.POA poa, 
                                String admin_id,
                                String channel_id,
                                InternalConsumerAdmin admin,
                                InternalDistributionChannel channel,
                                ORB orb)
  {
    this.name = name;
    this.type = type;
    this.id = id;
    this.poa = poa;
    this.admin_id = admin_id;
    this.channel_id = channel_id;
    consumerAdmin = admin;
    notificationChannel = channel;

    connected = false;
    narrowed = false;
    pull_consumer_object = null;
    pullConsumer = null;    
    operational_state = OperationalState.enabled;
    administrative_state = AdministrativeState.unlocked;
    qosAdminDelegate = new ProxyQoSAdmin(orb);
    filterAdminDelegate = new FilterAdminImpl();
    reference = 
        NotifReference.iProxyPullSupplierReference(poa, 
                                                   id, 
                                                   qosAdminDelegate.getPolicies());
    eventQueue = new EventQueue(TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_QUEUE_KEY), 
                                new FifoComparator(),
                                FifoOrder.value);
    destroying = false;
  }

  private void writeObject(ObjectOutputStream s) throws IOException
  {
    //
    s.defaultWriteObject();

    //
    s.writeInt(operational_state.value());
    s.writeInt(administrative_state.value());

    

    try
    {
      writeCorbaObject(consumerAdmin, InternalConsumerAdminHelper.type(), s);
      writeCorbaObject(notificationChannel, 
                       InternalDistributionChannelHelper.type(),
                       s);

      if (connected)
      {
        writeCorbaObject(pullConsumer, PullConsumerHelper.type(), s);
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
    operational_state = OperationalState.from_int(s.readInt());
    administrative_state = AdministrativeState.from_int(s.readInt());


    try
    {
      consumerAdmin = InternalConsumerAdminHelper.narrow(readCorbaObject(s));
      notificationChannel = 
          InternalDistributionChannelHelper.narrow(readCorbaObject(s));

      if (connected)
      {
        pull_consumer_object = readCorbaObject(s);
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
