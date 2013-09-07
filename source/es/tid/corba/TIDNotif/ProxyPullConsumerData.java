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
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

import org.omg.CORBA.ORB;
import org.omg.CosEventComm.PullSupplier;
import org.omg.CosEventComm.PullSupplierHelper;

import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;

import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannel;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdminHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullConsumer;

public class ProxyPullConsumerData extends CommonData implements Runnable, Serializable 
{
  public String name;        // Nombre del Objeto
  public int    type;         //ver CosNotifyChannelAdmin::ProxyType
  public String admin_id;
  public String channel_id;
  public boolean connected;
  public boolean destroying;

  transient public OperationalState operational_state;
  transient public AdministrativeState administrative_state;
  transient public boolean narrowed;
  transient public org.omg.CORBA.Object pull_supplier_object;
  transient public PullSupplier pullSupplier;
  
  transient public InternalSupplierAdmin supplierAdmin;
  transient public InternalProxyPullConsumer reference;
  transient public InternalDistributionChannel notificationChannel;
  
  public ProxyQoSAdmin qosAdminDelegate;
  public FilterAdminImpl 		 filterAdminDelegate;

  // Tiempos
  private boolean _time_debug;
  public long num_received_events;
  public long max_proccessing_time;
  public long total_proccessing_time;
  public long total_proccessed_events;

  public long eventCount;

  final static String NARROW_ERROR =
                              " *** WARNING *** narrow() pull supplier client";

  public ProxyPullConsumerData( String name,
                                int type,
                                String id, 
                                org.omg.PortableServer.POA poa, 
                                String admin_id, 
                                String channel_id,
                                InternalDistributionChannel channel,
                                InternalSupplierAdmin supplierAdmin,                                
								ORB orb )
  {
    this.name = name;
    this.type = type;
    this.id = id;
    this.poa = poa;
    this.admin_id = admin_id;
    this.channel_id = channel_id;
    this.supplierAdmin = supplierAdmin;
    
    this.qosAdminDelegate = new ProxyQoSAdmin( orb );
    this.filterAdminDelegate = new FilterAdminImpl();

    connected = false;

    operational_state = OperationalState.enabled;
    administrative_state = AdministrativeState.unlocked;

    narrowed = false;
    pull_supplier_object = null;
    pullSupplier = null;
    
    reference = 
        NotifReference.iProxyPullConsumerReference(poa, 
                                                   id,
                                                   qosAdminDelegate.getPolicies());
    destroying = false;

    eventCount = 0;

    // Algo temporal
    _time_debug = TIDNotifConfig.getBool(TIDNotifConfig.TIME_DEBUG_KEY);

    // Tiempos
    num_received_events = 0;
    max_proccessing_time = 0;
    total_proccessing_time = 0;
    total_proccessed_events = 0;
  }

  public void run()
  {
    while (connected)
    {
      org.omg.CORBA.Any event = null;

      try
      {
        event = pullSupplier.pull( );
        // invocacion local para la creacion de un thread de orb
        try
        {
          incEventCount();
          reference.ipush( event );
        }
        catch ( java.lang.Exception ex )
        {
          decEventCount();
        }
      }
      catch (org.omg.CORBA.NO_RESPONSE ex1)
      {
        try
        {
          Thread.sleep(1000);
        }
        catch ( java.lang.Exception ex ) { }

        TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, 
                       "ProxyPullConsumerImpl.run(): NO_RESPONESE Exception.");
      }
      catch (org.omg.CORBA.COMM_FAILURE ex)
      {
        connected = false;
        TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, 
                       "ProxyPullConsumerImpl.run(): COMM_FAILURE exception.");
      }
      catch ( java.lang.Exception ex )
      {
        connected = false;
        TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, 
                               "ProxyPullConsumerImpl.run(): Java Exception.");
      }
    }

    try
    {
      reference.disconnect_pull_consumer();
    }
    catch ( java.lang.Exception ex )
    {
    }
  }


  synchronized
  protected void incEventCount()
  {
    ++eventCount;
  }

  synchronized
  protected long decEventCount()
  {
    --eventCount;
    if (_time_debug)
    {
      total_proccessed_events++;
    }
    return eventCount;
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
      // Parent reference
      writeCorbaObject( supplierAdmin, InternalSupplierAdminHelper.type(), s);
      writeCorbaObject(notificationChannel, 
                       InternalDistributionChannelHelper.type(),s);

      // Client reference
      if (connected)
      {
        writeCorbaObject(pullSupplier, PullSupplierHelper.type(), s);
        
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
      supplierAdmin = InternalSupplierAdminHelper.narrow(readCorbaObject(s));
      notificationChannel =
          InternalDistributionChannelHelper.narrow(readCorbaObject(s));

      if (connected)
      {
        pull_supplier_object = readCorbaObject(s);
/*
        try
        {
          pullSupplier = PullSupplierHelper.narrow(pull_supplier_object);
          narrowed = true;
        }
        catch ( java.lang.Exception ex )
        {
          TIDNotifTrace.print(TIDNotifTrace.DEBUG, NARROW_ERROR);
          narrowed = false;
          pullSupplier = null;
        }
*/
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
