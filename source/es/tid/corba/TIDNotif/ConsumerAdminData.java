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
import java.util.Hashtable;
import java.util.HashMap;

import es.tid.corba.TIDNotif.qos.ProxyQoSAdmin;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;

import org.omg.ServiceManagement.OperationMode;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;

import org.omg.CORBA.ORB;
import org.omg.CosNotifyFilter.MappingFilter;
import org.omg.CosNotifyFilter.MappingFilterHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannel;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdminHelper;

import es.tid.corba.ExceptionHandlerAdmin.DistributionHandler;
import es.tid.corba.ExceptionHandlerAdmin.DistributionHandlerHelper;


public class ConsumerAdminData extends CommonData implements java.io.Serializable
{
  private static final String NULL = "";

  public String name;        // Nombre del Objeto
  public String channel_id;
  public boolean destroying;

  // DefaultValues
  public int event_priority;
  public int event_lifetime;

  // Atributos
  public Integer order;
  public int order_gap;
  public int last_order;

  // Criteria
  public AdminCriteria associatedCriteria;

  // Tabla con los objetos ordenados
  public java.util.TreeMap orderTable;

  transient public OperationMode operation_mode;
  transient public OperationalState operational_state;
  transient public AdministrativeState administrative_state;

//  transient public Hashtable proxyPushSupplierTable;
//  transient public Hashtable proxyPullSupplierTable;
  transient public HashMap proxyPushSupplierMap;
  transient public HashMap proxyPullSupplierMap;

  transient public boolean updatePushMap;
  transient public ProxyPushSupplierData[] pushSupplierArray;
  transient public boolean updatePullMap;
  transient public ProxyPullSupplierData[] pullSupplierArray;

   
  // Index Locator
  transient public String index_locator_ir;
  transient public IndexLocatorData index_locator; //= null;

  transient public DistributionHandler distribution_handler;

  // Mapping Discriminators
  transient public MappingFilter priority_discriminator;  
  transient public MappingFilter lifetime_discriminator;

  // Notification Channel al que pertenece el Supplier Admin (su padre)
  transient public InternalDistributionChannel notificationChannel;
  transient public InternalConsumerAdmin consumerAdminParent;
  transient public InternalConsumerAdmin reference;

  transient public Hashtable consumerAdminTable;

  //TODO review following serialization
  public FilterAdminImpl filterAdminDelegate;
  public ProxyQoSAdmin qosAdminDelegate;
  

  public ConsumerAdminData( String name,
                            String id, 
                            String channel_id,
                            org.omg.PortableServer.POA poa,
                            InternalDistributionChannel channel,
                            AdminCriteria criteria,
                            int priority,
                            int event_lifetime,
                            OperationMode operation_mode,
							ORB orb )
  {
    this.name = name;
    this.id = id;
    this.channel_id = channel_id;
    this.poa = poa;
    this.event_priority = priority;
    this.event_lifetime = event_lifetime;
    this.operation_mode = operation_mode;

    operational_state = OperationalState.enabled;
    administrative_state = AdministrativeState.unlocked;
   
    index_locator = null;
    distribution_handler = null;

    destroying = false;
    consumerAdminParent = null;
    notificationChannel = channel;

    // default criteria
    associatedCriteria = new AdminCriteria(criteria);

    priority_discriminator = null;

    lifetime_discriminator = null;

    last_order = 0;
    order_gap = 100;
    order = new Integer(0);

//    proxyPushSupplierTable = new Hashtable();
//    proxyPullSupplierTable = new Hashtable();
    proxyPushSupplierMap = new HashMap();
    proxyPullSupplierMap = new HashMap();

    updatePushMap = false;
    pushSupplierArray = new ProxyPushSupplierData[0];
    updatePullMap = false;
    pullSupplierArray = new ProxyPullSupplierData[0];

    if (operation_mode == OperationMode.distribution)
    {
      consumerAdminTable = new Hashtable();
      orderTable = new java.util.TreeMap();
    }
    else
    {
      consumerAdminTable = null;
      orderTable = null;
    }

	this.filterAdminDelegate = new FilterAdminImpl();
	this.qosAdminDelegate = new ProxyQoSAdmin( orb );

    
    reference = 
        NotifReference.internalConsumerAdminReference(poa,
                                                      id, 
                                                      qosAdminDelegate.getPolicies());
  }
  
  

  private void writeObject(ObjectOutputStream s) throws IOException
  {
    //
    s.defaultWriteObject();

    //
    s.writeInt(operation_mode.value());
    s.writeInt(operational_state.value());
    s.writeInt(administrative_state.value());

    if (index_locator == null)
    {
      s.writeBoolean(false);
    }
    else
    {
      s.writeBoolean(true);
      s.writeObject(index_locator.id);
    }

    try
    {
        writeCorbaObject(priority_discriminator, MappingFilterHelper.type(), s);
        writeCorbaObject(lifetime_discriminator, MappingFilterHelper.type(), s);
        writeCorbaObject(notificationChannel, 
                         InternalDistributionChannelHelper.type(), 
                         s);

    if (distribution_handler == null)
    {
      s.writeBoolean(false);
    }
    else
    {
      s.writeBoolean(true);
      writeCorbaObject(distribution_handler,DistributionHandlerHelper.type(),s);
    }

    if (consumerAdminParent == null)
    {
      s.writeBoolean(false);
    }
    else
    {
      s.writeBoolean(true);
      writeCorbaObject(
                   consumerAdminParent, InternalConsumerAdminHelper.type(), s);
    }
    }
    catch (org.omg.IOP.CodecPackage.InvalidTypeForEncoding ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.DEBUG, ex);
    }

    //s.writeObject(proxyPushSupplierTable.ids());
    java.util.Vector v = new java.util.Vector();
//    synchronized(proxyPushSupplierTable)
    synchronized(proxyPushSupplierMap)
    {
//      for ( java.util.Iterator e = proxyPushSupplierTable.values().iterator();
      for ( java.util.Iterator e = proxyPushSupplierMap.values().iterator();
                                                                  e.hasNext();)
      {
        v.add(((CommonData)e.next()).id);
      }
    }
    s.writeObject(v);
    v.clear();

    //s.writeObject(proxyPullSupplierTable.ids());
    v = new java.util.Vector();
//    synchronized(proxyPullSupplierTable)
    synchronized(proxyPullSupplierMap)
    {
//      for (java.util.Iterator e = proxyPullSupplierTable.values().iterator();
      for (java.util.Iterator e = proxyPullSupplierMap.values().iterator();
                                                                  e.hasNext();)
      {
        v.add(((CommonData)e.next()).id);
      }
    }
    s.writeObject(v);
    v.clear();

    if (operation_mode == OperationMode.distribution)
    {
      //s.writeObject(consumerAdminTable.ids());
      v = new java.util.Vector();
      synchronized(consumerAdminTable)
      {
        for (java.util.Iterator e = consumerAdminTable.values().iterator();
                                                                  e.hasNext();)
        {
          v.add(((CommonData)e.next()).id);
        }
      }
      s.writeObject(v);
      v.clear();
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
      index_locator_ir = (String) s.readObject();
    }
    else
    {
      index_locator_ir = null;
      index_locator = null;
    }
 
    try
    {
        
	    priority_discriminator = 
	        MappingFilterHelper.narrow(readCorbaObject(s));
	    
	    lifetime_discriminator =
	        MappingFilterHelper.narrow(readCorbaObject(s));
    
	    notificationChannel =
                  InternalDistributionChannelHelper.narrow(readCorbaObject(s));

    b = s.readBoolean();
    if (b)
    {
      distribution_handler=DistributionHandlerHelper.narrow(readCorbaObject(s));
    }
    else
    {
      distribution_handler = null;
    }
    b = s.readBoolean();
    if (b)
    {
      consumerAdminParent =
                        InternalConsumerAdminHelper.narrow(readCorbaObject(s));
    }
    else
    {
      consumerAdminParent = null;
    }
    }
    catch (org.omg.IOP.CodecPackage.FormatMismatch ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.DEBUG, ex);
    }


//    proxyPushSupplierTable = new Hashtable();
    proxyPushSupplierMap = new HashMap();
    java.util.Vector v = (java.util.Vector)s.readObject();
    for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
    {
//      proxyPushSupplierTable.put((String)e.nextElement(), NULL);
      proxyPushSupplierMap.put((String)e.nextElement(), NULL);
    }
    updatePushMap = false;
    pushSupplierArray = new ProxyPushSupplierData[0];

//    proxyPullSupplierTable = new Hashtable();
    proxyPullSupplierMap = new HashMap();
    v = (java.util.Vector)s.readObject();
    for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
    {
//      proxyPullSupplierTable.put((String)e.nextElement(), NULL);
      proxyPullSupplierMap.put((String)e.nextElement(), NULL);
    }
    updatePullMap = false;
    pullSupplierArray = new ProxyPullSupplierData[0];

    if (operation_mode == OperationMode.distribution)
    {
      consumerAdminTable = new Hashtable();
      v = (java.util.Vector)s.readObject();
      for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
      {
        consumerAdminTable.put((String)e.nextElement(), NULL);
      }
    }
    else
    {
      consumerAdminTable = null;
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
