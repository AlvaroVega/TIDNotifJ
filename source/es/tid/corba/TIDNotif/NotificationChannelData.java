/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 92 $
* Date: $Date: 2008-08-13 10:09:17 +0200 (Wed, 13 Aug 2008) $
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import org.omg.CORBA.ORB;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannel;
import org.omg.ServiceManagement.AdministrativeState;
import org.omg.ServiceManagement.OperationMode;
import org.omg.ServiceManagement.OperationalState;

import es.tid.corba.TIDNotif.qos.ChannelQoSAdmin;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

public class NotificationChannelData extends CommonData implements java.io.Serializable
{
  private static final String NULL = "";

  // Creation Timestamp
  public String creation_date;
  public String creation_time;

  // Default priority anf lifetime.
  public int default_priority;
  public int default_event_lifetime;

  // Admin properties
  public int max_queue_length;
  public int max_consumers;
  public int max_suppliers;

  // Modo de operacion
  transient public OperationMode operation_mode;

  // Estado
  transient public OperationalState operational_state;
  transient public AdministrativeState administrative_state;

  public String default_consumer_id;
  public String default_supplier_id;
  
  public FilterFactoryImpl filterFactoryDelegate;  
  public FilterAdminImpl filterAdminDelegate;
  public ChannelQoSAdmin qosAdminDelegate;

  public boolean destroying;

  transient public Hashtable consumerAdminTable;
  transient public Hashtable supplierAdminTable;

  transient public Hashtable newConsumerAdminTable;
  transient public Hashtable newSupplierAdminTable;

  transient public InternalDistributionChannel reference;
  
  

// ***************************************************************************
//
// NotificationChannelData Constructor
//
// ***************************************************************************
  public NotificationChannelData( String id,
                                  org.omg.PortableServer.POA poa,
                                  OperationMode operation_mode,
                                  int priority,
                                  int event_lifetime,
								  ORB orb)
  {
    this.id = id;
    this.poa = poa;

    // Modo de operacion
    this.operation_mode = operation_mode;

    // setting the priority and lifetime
    default_priority = priority;
    default_event_lifetime = event_lifetime;

    java.util.Date date = new java.util.Date(System.currentTimeMillis());
    creation_date = NotifDateFormat.DATE_FORMAT.format(date);
    creation_time = NotifDateFormat.TIME_FORMAT.format(date);

    // Init Operational and Administrative States
    operational_state = OperationalState.enabled;
    administrative_state = AdministrativeState.unlocked;

    // Id's de los default ConsumerAdmin y SupplierAdmin
    default_consumer_id = null;
    default_supplier_id = null;

    consumerAdminTable = new Hashtable();
    supplierAdminTable = new Hashtable();

    if (operation_mode == OperationMode.notification)
    {
      newConsumerAdminTable = null;
      newSupplierAdminTable = null;
    }
    else
    {
      newConsumerAdminTable = new Hashtable();
      newSupplierAdminTable = new Hashtable();
    }

    
    
    this.filterAdminDelegate = new FilterAdminImpl();
    filterFactoryDelegate = new FilterFactoryImpl();
    
    this.qosAdminDelegate = new ChannelQoSAdmin( orb );
    
    reference = 
        NotifReference.internalChannelReference(poa, 
                                                id,
                                                qosAdminDelegate.getPolicies());

    max_queue_length = TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_QUEUE_KEY);
    max_consumers = -1;
    max_suppliers = -1;

    destroying = false;
  }
 
  private void writeObject(ObjectOutputStream s) throws IOException
  {
    //
    s.defaultWriteObject();

    // 
    s.writeInt(operation_mode.value());
    s.writeInt(operational_state.value());
    s.writeInt(administrative_state.value());

    //s.writeObject(consumerAdminTable.ids());
    java.util.Vector v = new java.util.Vector();
    synchronized(consumerAdminTable)
    {
      for (java.util.Iterator e = consumerAdminTable.values().iterator(); 
                                                                 e.hasNext(); )
      {
        v.add(((CommonData)e.next()).id);
      }
    }
    s.writeObject(v);
    v.clear();

    //s.writeObject(supplierAdminTable.ids());
    v = new java.util.Vector();
    synchronized(supplierAdminTable)
    {
      for (java.util.Iterator e = supplierAdminTable.values().iterator(); 
                                                                 e.hasNext(); )
      {
        v.add(((CommonData)e.next()).id);
      }
    }
    s.writeObject(v);
    v.clear();

    if (operation_mode != OperationMode.notification)
    {
      //s.writeObject(newConsumerAdminTable.ids());
      v = new java.util.Vector();
      synchronized(newConsumerAdminTable)
      {
        for (java.util.Iterator e = newConsumerAdminTable.values().iterator(); 
                                                                 e.hasNext(); )
        {
          v.add(((CommonData)e.next()).id);
        }
      }
      s.writeObject(v);
      v.clear();

      //s.writeObject(newSupplierAdminTable.ids());
      v = new java.util.Vector();
      synchronized(newSupplierAdminTable)
      {
        for (java.util.Iterator e = newSupplierAdminTable.values().iterator(); 
                                                                 e.hasNext(); )
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

    java.util.Vector v;
    consumerAdminTable = new Hashtable();
    v = (java.util.Vector)s.readObject();
    for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
    {
      consumerAdminTable.put((String)e.nextElement(), NULL);
    }
    supplierAdminTable = new Hashtable();
    v = (java.util.Vector)s.readObject();
    for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
    {
      supplierAdminTable.put((String)e.nextElement(), NULL);
    }

    if (operation_mode != OperationMode.notification)
    {
      newConsumerAdminTable = new Hashtable();
      v = (java.util.Vector)s.readObject();
      for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
      {
        newConsumerAdminTable.put((String)e.nextElement(), NULL);
      }

      newSupplierAdminTable = new Hashtable();
      v = (java.util.Vector)s.readObject();
      for ( java.util.Enumeration e = v.elements(); e.hasMoreElements(); )
      {
        newSupplierAdminTable.put((String)e.nextElement(), NULL);
      }
    }
  }
}
