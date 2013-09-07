/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 91 $
* Date: $Date: 2008-08-11 09:06:38 +0200 (Mon, 11 Aug 2008) $
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

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosNotification.DefaultPriority;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator;
import org.omg.CosNotifyChannelAdmin.ProxyNotFound;
import org.omg.CosNotifyChannelAdmin.ProxySupplier;
import org.omg.CosNotifyChannelAdmin.ProxyType;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterNotFound;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.MappingFilter;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;
import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdminHelper;
import org.omg.NotificationChannelAdmin.ProxyAlreadyExist;
import org.omg.ServiceManagement.OperationMode;
import org.omg.TimeBase.TimeTHelper;

import es.tid.TIDorbj.core.poa.OID;
import es.tid.TIDorbj.util.UTC;
import es.tid.corba.ExceptionHandlerAdmin.CannotProceed;
import es.tid.corba.TIDNotif.util.HashKey;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;

public class ConsumerAdminImpl extends
		org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdminPOA
		implements ConsumerAdminMsg {
	private static final String SEPARATOR = ".";

	private static final byte PUSHSUPPLIER_TYPE = 0;

	private static final byte CONSUMERADMIN_TYPE = 1;

	private org.omg.CORBA.ORB _orb;

	//private DiscriminatorFactory _discriminatorFactory;

	private org.omg.PortableServer.POA _agentPOA;

	private org.omg.PortableServer.POAManager _poa_manager;

	// Controla si se utiliza un POA global a todos los canales, local al canal
	// exclusivo para cada SupplierAdmin servant
	private int _consumer_poa_policy = 0;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// List of ChannelsConsumerAdminTabli (Pack)
	private DataTable2 _consumers_admin_table;
	
	public ConsumerAdminImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agentPOA,
			org.omg.PortableServer.POAManager poa_manager) {
		_orb = orb;
		_agentPOA = agentPOA;
		_poa_manager = poa_manager;

		// Controla como se crean los POA's para los Admin's
		_consumer_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.CONSUMER_POA_KEY);

		_consumers_admin_table = new DataTable2();

	}

	//
	// Temas del default Servant
	//
	//
	synchronized private void setCurrent() {
		if (_current == null) {
			try {
				_current = org.omg.PortableServer.CurrentHelper.narrow(_orb()
						.resolve_initial_references("POACurrent"));
			}
			//catch (org.omg.CORBA.ORBPackage.InvalidName ex)
			//catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
			catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				throw new org.omg.CORBA.INTERNAL();
			}
		}
	}

	private ConsumerAdminData getData() {
		if (_current == null)
			setCurrent();

		try {
			ConsumerAdminData data = (ConsumerAdminData) _consumers_admin_table.table
					.get(new OID(_current.get_object_id()));
			if (data != null)
				return data;
		}
		//catch (org.omg.CORBA.ORBPackage.InvalidName ex)
		//catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
		catch (Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.CORBA.INV_OBJREF();
		}
		throw new org.omg.CORBA.OBJECT_NOT_EXIST();
	}

	private org.omg.PortableServer.POAManager getPOAManager() {
		return _poa_manager;
	}

	//public void setData(ConsumerAdminData admin)
	//{
	//_data = admin;
	//}

	public void register(ConsumerAdminData admin) {
		REGISTER[1] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, REGISTER);
		_consumers_admin_table.put(new OID(admin.id.getBytes()), admin);
	}

	private void unregister(ConsumerAdminData admin) {
		UNREGISTER[1] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, UNREGISTER);
		_consumers_admin_table.remove(new OID(admin.id.getBytes()));
	}


	synchronized public org.omg.CosEventChannelAdmin.ProxyPullSupplier obtain_pull_supplier() {
		ConsumerAdminData admin = getData();
		OBTAIN_PULL_SUPPLIER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PULL_SUPPLIER);

		if (admin.operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION(N_P__LOCKED);
		}

		

		try {
		    //		  Obtenemos el Id (global y unico)
			String proxy_id = PersistenceManager.getDB().getUID();
			//same name as id
			return this.obtain_named_pull_supplier( proxy_id, 
			                                        proxy_id, 
			                                        ProxyType.PULL_ANY );
		} catch (ProxyAlreadyExist e) {
			throw new INTERNAL();
		} catch (Exception ex) {
		    throw new INTERNAL("Cannot get UID");
		}
	}

	synchronized public org.omg.NotificationChannelAdmin.ProxyPullSupplier obtain_named_pull_supplier(
			String name)
			throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist {
		ConsumerAdminData admin = getData();
		OBTAIN_NAMED_PULL_SUPPLIER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PULL_SUPPLIER);

		// Solo se permite llamar a este metodo en NOTIFICATION mode
		if (admin.operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPullSupplierMap.get(name) != null) {
			throw new org.omg.NotificationChannelAdmin.ProxyAlreadyExist();
		}

		String name_id = new String(name);
		String proxy_id;
		
        try {
            proxy_id = PersistenceManager.getDB().getUID();
        } catch (Exception e) {
           throw new INTERNAL("Cannot get UID");
        }
        
        return this.obtain_named_pull_supplier( name_id, 
		                                        proxy_id, 
		                                        ProxyType.PULL_ANY );
	}
	
	synchronized private org.omg.NotificationChannelAdmin.ProxyPullSupplier obtain_named_pull_supplier(
			String name_id, String proxy_id, ProxyType type )
			throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist {
		ConsumerAdminData admin = getData();
		OBTAIN_NAMED_PULL_SUPPLIER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PULL_SUPPLIER);

		// Solo se permite llamar a este metodo en NOTIFICATION mode
		if (admin.operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPullSupplierMap.get( name_id ) != null) {
			throw new org.omg.NotificationChannelAdmin.ProxyAlreadyExist();
		}

		org.omg.PortableServer.POA proxy_poa;
		if (_consumer_poa_policy == 0) // Create the Global POA
		{
			proxy_poa = globalProxyPullSupplierPOA();
		} else if (_consumer_poa_policy == 1) // Create the Local POA
		{
			proxy_poa = localProxyPullSupplierPOA(admin.channel_id);
		} else // Create the Exclusive POA
		{
			proxy_poa = exclusiveProxyPullSupplierPOA(admin.channel_id,
					admin.id);
		}

		ProxyPullSupplierData proxy = 
		    new ProxyPullSupplierData(name_id,
		                              type.value(),
		                              proxy_id, 
		                              proxy_poa, 
		                              admin.id, 
		                              admin.channel_id,
		                              admin.reference, 
		                              admin.notificationChannel, 
		                              _orb);

		register_pull_supplier(proxy, proxy_poa);

		// Insert the proxy into the SupplierAdmin's table
		synchronized (admin.proxyPullSupplierMap) {
			admin.proxyPullSupplierMap.put(name_id, proxy);
			admin.updatePullMap = true;
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(proxy);
			PersistenceManager.getDB().update(
					PersistenceDB.OBJ_PROXY_PULL_SUPPLIER, admin);
		}
		return NotifReference.nProxyPullSupplierReference(proxy_poa, proxy_id);
	}
	
	synchronized public org.omg.CosEventChannelAdmin.ProxyPushSupplier obtain_push_supplier() {
		ConsumerAdminData admin = getData();
		OBTAIN_PUSH_SUPPLIER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PUSH_SUPPLIER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION(N_P__LOCKED);
		}

		
		
		try {
		    //		  Obtenemos el Id (global y unico)
			String proxy_id = PersistenceManager.getDB().getUID();
			
			return obtain_named_push_supplier( proxy_id, 
			                                   proxy_id, 
			                                   ProxyType.PUSH_ANY );
		} catch (Exception e) {		    
			throw new INTERNAL();
		}
	}

	
	synchronized public org.omg.NotificationChannelAdmin.ProxyPushSupplier 
		obtain_named_push_supplier(String name)
			throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist {
		ConsumerAdminData admin = getData();
		OBTAIN_NAMED_PUSH_SUPPLIER[1] = name;
		OBTAIN_NAMED_PUSH_SUPPLIER[3] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PUSH_SUPPLIER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPushSupplierMap.containsKey(name)) {
			throw new org.omg.NotificationChannelAdmin.ProxyAlreadyExist();
		}

		String name_id = new String(name);
		String proxy_id;
        try {
            proxy_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
           throw new INTERNAL(e.toString());
        }
        return this.obtain_named_push_supplier( name_id, 
		                                        proxy_id, 
		                                        ProxyType.PUSH_ANY );
	}

	synchronized private org.omg.NotificationChannelAdmin.ProxyPushSupplier 
		obtain_named_push_supplier(String name_id,
		                           String proxy_id, 
		                           ProxyType type)
			throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist {
		ConsumerAdminData admin = getData();
		OBTAIN_NAMED_PUSH_SUPPLIER[1] = name_id;
		OBTAIN_NAMED_PUSH_SUPPLIER[3] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PUSH_SUPPLIER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPushSupplierMap.containsKey(name_id)) {
			throw new org.omg.NotificationChannelAdmin.ProxyAlreadyExist();
		}

		org.omg.PortableServer.POA proxy_poa;
		if (_consumer_poa_policy == 0) // Create the Global POA
		{
			proxy_poa = globalProxyPushSupplierPOA();
		} else if (_consumer_poa_policy == 1) // Create the Local POA
		{
			proxy_poa = localProxyPushSupplierPOA(admin.channel_id);
		} else // Create the Exclusive POA
		{
			proxy_poa = exclusiveProxyPushSupplierPOA(admin.channel_id,
					admin.id);
		}

		ProxyPushSupplierData proxy = 
		    new ProxyPushSupplierData(name_id,
		                              type.value(),
		                              proxy_id, 
		                              proxy_poa,
		                              admin.operation_mode,
		                              admin.id, admin.
		                              channel_id, 
		                              admin.reference, 
		                              admin.notificationChannel,
		                              _orb);

		register_push_supplier(proxy, proxy_poa);

		// Insert the proxy into the SupplierAdmin's table
		synchronized (admin.proxyPushSupplierMap) {
			admin.proxyPushSupplierMap.put(name_id, proxy);
			admin.updatePushMap = true;
		}

		// Insert the Id into the Order table
		if (admin.operation_mode == OperationMode.distribution) {
			Integer key = new Integer(update_order(admin, 0));
			synchronized (admin.orderTable) {
				admin.orderTable.put(key, new ObjectType(name_id,
						PUSHSUPPLIER_TYPE));
			}
			proxy.order = key;
		}
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(proxy);
			PersistenceManager.getDB().update(
					PersistenceDB.OBJ_PROXY_PUSH_SUPPLIER, admin);
		}
		return NotifReference.nProxyPushSupplierReference(proxy_poa, proxy_id,
				admin.operation_mode);
	}

	
	public org.omg.NotificationChannelAdmin.ProxyPushSupplier find_push_supplier(
			String name) throws org.omg.NotificationChannelAdmin.ProxyNotFound {
		ConsumerAdminData admin = getData();
		FIND_PUSH_SUPPLIER[1] = name;
		FIND_PUSH_SUPPLIER[3] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_PUSH_SUPPLIER);

		ProxyPushSupplierData proxy = (ProxyPushSupplierData) admin.proxyPushSupplierMap
				.get(name);
		if (proxy == null) {
			throw new org.omg.NotificationChannelAdmin.ProxyNotFound();
		}
		return NotifReference.nProxyPushSupplierReference(proxy.poa, proxy.id,
				proxy.operation_mode);
	}

	public org.omg.NotificationChannelAdmin.ProxyPullSupplier find_pull_supplier(
			String name) throws org.omg.NotificationChannelAdmin.ProxyNotFound {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_PULL_SUPPLIER);
		ProxyPullSupplierData proxy = (ProxyPullSupplierData) admin.proxyPullSupplierMap
				.get(name);
		if (proxy == null) {
			throw new org.omg.NotificationChannelAdmin.ProxyNotFound();
		}
		return NotifReference.nProxyPullSupplierReference(proxy.poa, proxy.id);
	}

	synchronized public org.omg.DistributionChannelAdmin.ConsumerAdmin new_for_consumers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.DistributionChannelAdmin.InvalidCriteria {
		ConsumerAdminData admin = getData();
		NEW_FOR_CONSUMERS[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FOR_CONSUMERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					"NOT ALLOWED IN NOTIFICATION MODE");
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION(N_P__LOCKED);
		}

		// obtenemos una posicion por defecto
		//Integer key = new Integer(update_order(admin, 0));

		InternalConsumerAdmin consumerAdmin = null;
		try {
			consumerAdmin = admin.notificationChannel
					.new_positioned_for_consumers(the_criteria,
							admin.reference, admin.name,
							admin.order.intValue(), update_order(admin, 0));
		} catch (org.omg.NotificationChannelAdmin.InvalidCriteria ic_ex) {
			throw new org.omg.DistributionChannelAdmin.InvalidCriteria(
					the_criteria);
		} catch (org.omg.CORBA.BAD_PARAM bp_ex) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (consumerAdmin == null) {
			throw new org.omg.DistributionChannelAdmin.InvalidCriteria(
					the_criteria);
		}

		// Insert the servant into the table
		//synchronized(admin.consumerAdminTable)
		//{
		// admin.consumerAdminTable.put(consumerAdmin.getAdminId(), consumerAdmin);
		//}

		// Insert the Id into the Order table
		//admin.orderTable.put( key, new ObjectType(
		//consumerAdmin.getAdminId(), CONSUMERADMIN_TYPE) );
		//consumerAdmin.order(key.intValue());

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, admin);
		}
		return consumerAdmin;
	}

	synchronized public org.omg.DistributionChannelAdmin.ConsumerAdmin new_positioned_for_consumers(
			int position, org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.DistributionChannelAdmin.AlreadyDefinedOrder,
			org.omg.DistributionChannelAdmin.InvalidCriteria {
		ConsumerAdminData admin = getData();
		NEW_POSITIONED_FOR_CONSUMERS[1] = admin.name;
		NEW_POSITIONED_FOR_CONSUMERS[3] = Integer.toString(position);
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_POSITIONED_FOR_CONSUMERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION(N_P__LOCKED);
		}

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (position == 0) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		//Integer key = new Integer(position);
		if (checkOrderPosition(admin, new Integer(position))) {
			throw new org.omg.DistributionChannelAdmin.AlreadyDefinedOrder();
		}

		InternalConsumerAdmin consumerAdmin = null;
		try {
			consumerAdmin = admin.notificationChannel
					.new_positioned_for_consumers(the_criteria,
							admin.reference, admin.name,
							admin.order.intValue(), update_order(admin,
									position));
		} catch (org.omg.NotificationChannelAdmin.InvalidCriteria ic_ex) {
			throw new org.omg.DistributionChannelAdmin.InvalidCriteria(
					the_criteria);
		} catch (org.omg.CORBA.BAD_PARAM bp_ex) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		// Insert the servant into the table
		//synchronized(_consumerAdminTable)
		//{
		//    _consumerAdminTable.put(consumerAdmin.getAdminId(), consumerAdmin);
		//}

		// Insert the Id into the Order table
		//_orderTable.put( key, new ObjectType(
		//                        consumerAdmin.getAdminId(), CONSUMERADMIN_TYPE) );
		//consumerAdmin.order(key.intValue());

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, admin);
		}
		return consumerAdmin;
	}

	public org.omg.DistributionChannelAdmin.ConsumerAdmin[] find_for_consumers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.DistributionChannelAdmin.InvalidCriteria,
			org.omg.DistributionChannelAdmin.CannotMeetCriteria {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_FOR_CONSUMERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		java.util.Vector ctmp = new java.util.Vector();

		try {
			synchronized (admin.consumerAdminTable) {
				for (java.util.Iterator e = admin.consumerAdminTable.values()
						.iterator(); e.hasNext();) {
					ConsumerAdminData nadmin = (ConsumerAdminData) e.next();
					if (admin.reference.meetCriteria(the_criteria)) {
						ctmp.add(NotifReference.dConsumerAdminReference(
								nadmin.poa, nadmin.id));
					}
				}
			}
		} catch (org.omg.NotificationChannelAdmin.InvalidCriteria ex) {
			throw new org.omg.DistributionChannelAdmin.InvalidCriteria(
					the_criteria);
		}

		if (ctmp.size() == 0) {
			throw new org.omg.DistributionChannelAdmin.CannotMeetCriteria(
					the_criteria);
		}
		return (org.omg.DistributionChannelAdmin.ConsumerAdmin[]) ctmp
				.toArray(new org.omg.DistributionChannelAdmin.ConsumerAdmin[0]);

		/*
		 org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers =
		 new org.omg.DistributionChannelAdmin.ConsumerAdmin[ctmp.size()];

		 int i = 0;
		 for ( java.util.Enumeration e = ctmp.elements(); e.hasMoreElements(); )
		 {
		 consumers[i++] = 
		 (org.omg.DistributionChannelAdmin.ConsumerAdmin) e.nextElement();
		 }
		 ctmp.removeAllElements();
		 ctmp=null;
		 return consumers;
		 */
	}

	synchronized public org.omg.DistributionChannelAdmin.ProxyPushSupplier 
		new_obtain_push_supplier(int position)
			throws org.omg.DistributionChannelAdmin.AlreadyDefinedOrder {
		ConsumerAdminData admin = getData();
		OBTAIN_POSITIONED_PUSH_SUPPLIER[1] = admin.name;
		TIDNotifTrace
				.print(TIDNotifTrace.USER, OBTAIN_POSITIONED_PUSH_SUPPLIER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode.value() != OperationMode._distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		Integer key = new Integer(position);
		if (checkOrderPosition(admin, key)) {
			throw new org.omg.DistributionChannelAdmin.AlreadyDefinedOrder();
		}
		update_order(admin, position);

		// Obtenemos el Id (global y unico)
		String proxy_id;
        try {
            proxy_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        org.omg.PortableServer.POA proxy_poa;
		if (_consumer_poa_policy == 0) // Create the Global POA
		{
			proxy_poa = globalProxyPushSupplierPOA();
		} else if (_consumer_poa_policy == 1) // Create the Local POA
		{
			proxy_poa = localProxyPushSupplierPOA(admin.channel_id);
		} else // Create the Exclusive POA
		{
			proxy_poa = exclusiveProxyPushSupplierPOA(admin.channel_id,
					admin.id);
		}

		ProxyPushSupplierData proxy = 
		    new ProxyPushSupplierData(proxy_id,
		                              ProxyType._PUSH_ANY,
		                              proxy_id, 
		                              proxy_poa, 
		                              admin.operation_mode, 
		                              admin.id,
		                              admin.channel_id, 
		                              admin.reference, 
		                              admin.notificationChannel,
		                              _orb);

		register_push_supplier(proxy, proxy_poa);

		// Insert the proxy into the SupplierAdmin's table
		synchronized (admin.proxyPushSupplierMap) {
			admin.proxyPushSupplierMap.put(proxy_id, proxy);
			admin.updatePushMap = true;
		}

		synchronized (admin.orderTable) {
			admin.orderTable.put(key, new ObjectType(proxy_id,
					PUSHSUPPLIER_TYPE));
			//proxy.reference.order(key.intValue());
		}
		proxy.order = key;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(proxy);
			PersistenceManager.getDB().update(
					PersistenceDB.OBJ_PROXY_PULL_SUPPLIER, admin);
		}
		return NotifReference.dProxyPushSupplierReference(proxy_poa, proxy_id);
	}

	synchronized public org.omg.DistributionChannelAdmin.ProxyPushSupplier 
		new_obtain_named_push_supplier(int position, String name)
			throws org.omg.DistributionChannelAdmin.AlreadyDefinedOrder,
			org.omg.DistributionChannelAdmin.ProxyAlreadyExist {
		ConsumerAdminData admin = getData();
		OBTAIN_NAMED_POSITIONED_PUSH_SUPPLIER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER,
				OBTAIN_NAMED_POSITIONED_PUSH_SUPPLIER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode.value() != OperationMode._distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		Integer key = new Integer(position);
		if (checkOrderPosition(admin, key)) {
			throw new org.omg.DistributionChannelAdmin.AlreadyDefinedOrder();
		}

		if (admin.proxyPushSupplierMap.containsKey(name)) {
			throw new org.omg.DistributionChannelAdmin.ProxyAlreadyExist();
		}

		update_order(admin, position);

		String name_id = new String(name);
		String proxy_id;
        try {
            proxy_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        org.omg.PortableServer.POA proxy_poa;
		if (_consumer_poa_policy == 0) // Create the Global POA
		{
			proxy_poa = globalProxyPushSupplierPOA();
		} else if (_consumer_poa_policy == 1) // Create the Local POA
		{
			proxy_poa = localProxyPushSupplierPOA(admin.channel_id);
		} else // Create the Exclusive POA
		{
			proxy_poa = exclusiveProxyPushSupplierPOA(admin.channel_id,
					admin.id);
		}

		ProxyPushSupplierData proxy = 
		    new ProxyPushSupplierData(name_id,
		                              ProxyType._PUSH_ANY,
		                              proxy_id, 
		                              proxy_poa, 
		                              admin.operation_mode,
		                              admin.id,
		                              admin.channel_id, 
		                              admin.reference, 
		                              admin.notificationChannel, 
		                              _orb);

		register_push_supplier(proxy, proxy_poa);

		// Insert the proxy into the SupplierAdmin's table
		synchronized (admin.proxyPushSupplierMap) {
			admin.proxyPushSupplierMap.put(name_id, proxy);
			admin.updatePushMap = true;
		}

		synchronized (admin.orderTable) {
			// Insert the Id into the Order table
			admin.orderTable.put(key,
					new ObjectType(name_id, PUSHSUPPLIER_TYPE));
			//proxy.reference.order(key.intValue());
		}
		proxy.order = key;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(proxy);
			PersistenceManager.getDB().update(
					PersistenceDB.OBJ_PROXY_PULL_SUPPLIER, admin);
		}
		return NotifReference.dProxyPushSupplierReference(proxy_poa, proxy_id);
	}

	synchronized public void move_positioned_consumer_admin(int old_position,
			int new_position, String admin_id)
			throws org.omg.DistributionInternalsChannelAdmin.AlreadyDefinedOrder,
			org.omg.DistributionInternalsChannelAdmin.DataError,
			org.omg.DistributionInternalsChannelAdmin.OrderNotFound {
		ConsumerAdminData admin = getData();
		MOVE_POSITIONED_COSUMER_ADMIN[1] = admin.name;
		MOVE_POSITIONED_COSUMER_ADMIN[3] = admin.id;
		MOVE_POSITIONED_COSUMER_ADMIN[5] = Integer.toString(old_position);
		MOVE_POSITIONED_COSUMER_ADMIN[7] = Integer.toString(new_position);
		MOVE_POSITIONED_COSUMER_ADMIN[9] = admin_id;
		TIDNotifTrace.print(TIDNotifTrace.USER, MOVE_POSITIONED_COSUMER_ADMIN);

		Integer old_key = new Integer(old_position);
		ObjectType object = (ObjectType) admin.orderTable.remove(old_key);

		if (object == null) {
			throw new org.omg.DistributionInternalsChannelAdmin.OrderNotFound();
		} else {
			if (admin_id.compareTo(object.id) != 0) {
				// De no coincidir con el id esperado restituimos el valor
				admin.orderTable.put(old_key, object);
				throw new org.omg.DistributionInternalsChannelAdmin.DataError();
			}
		}
		Integer new_key = new Integer(new_position);
		if (checkOrderPosition(admin, new_key)) {
			admin.orderTable.put(old_key, object);
			throw new org.omg.DistributionInternalsChannelAdmin.AlreadyDefinedOrder();
		}
		update_order(admin, new_position);
		admin.orderTable.put(new_key, object);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, admin);
		}
	}

	synchronized public void move_positioned_push_supplier(int old_position,
			int new_position, String proxy_id)
			throws org.omg.DistributionInternalsChannelAdmin.AlreadyDefinedOrder,
			org.omg.DistributionInternalsChannelAdmin.DataError,
			org.omg.DistributionInternalsChannelAdmin.OrderNotFound {
		ConsumerAdminData admin = getData();
		MOVE_POSITIONED_PUSH_SUPPLIER[1] = admin.name;
		MOVE_POSITIONED_PUSH_SUPPLIER[3] = admin.id;
		MOVE_POSITIONED_PUSH_SUPPLIER[5] = Integer.toString(old_position);
		MOVE_POSITIONED_PUSH_SUPPLIER[7] = Integer.toString(new_position);
		MOVE_POSITIONED_PUSH_SUPPLIER[9] = proxy_id;
		TIDNotifTrace.print(TIDNotifTrace.USER, MOVE_POSITIONED_PUSH_SUPPLIER);

		Integer old_key = new Integer(old_position);
		ObjectType object = (ObjectType) admin.orderTable.remove(old_key);

		if (object == null) {
			throw new org.omg.DistributionInternalsChannelAdmin.OrderNotFound();
		} else {
			if (proxy_id.compareTo(object.id) != 0) {
				// De no coincidir con el id esperado restituimos el valor
				admin.orderTable.put(old_key, object);
				throw new org.omg.DistributionInternalsChannelAdmin.DataError();
			}
		}
		Integer new_key = new Integer(new_position);
		if (checkOrderPosition(admin, new_key)) {
			admin.orderTable.put(old_key, object);
			throw new org.omg.DistributionInternalsChannelAdmin.AlreadyDefinedOrder();
		}
		update_order(admin, new_position);
		admin.orderTable.put(new_key, object);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, admin);
		}
	}

	// ***************************************************************************
	//
	// NotificationChannel Attributes Operations
	//
	// org.omg.ServiceManagement.OperationalState operational_state()
	// org.omg.ServiceManagement.AdministrativeState administrative_state()
	// void administrative_state(org.omg.ServiceManagement.AdministrativeState value)
	//
	// ***************************************************************************
	public org.omg.ServiceManagement.OperationalState operational_state() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_OPERATIONAL_STATE);
		return getData().operational_state;
	}

	public org.omg.ServiceManagement.AdministrativeState administrative_state() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_ADMINISTRATIVE_STATE);
		return getData().administrative_state;
	}

	synchronized public void administrative_state(
			org.omg.ServiceManagement.AdministrativeState value) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_ADMINISTRATIVE_STATE);
		if (admin.administrative_state != value) {
			admin.administrative_state = value;

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_ADMINISTRATIVE_STATE, admin);
			}
		}
	}

	public org.omg.CosLifeCycle.NVP[] associated_criteria() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_ASSOCIATED_CRITERIA);
		return admin.associatedCriteria.elements(BaseCriteria.ADMIN_ONLY);
	}

	public org.omg.CosLifeCycle.NVP[] extended_criteria() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_EXTENDED_CRITERIA);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}
		return admin.associatedCriteria
				.elements(BaseCriteria.EXTENDED_CONSUMER_ONLY);
	}

	public void extended_criteria(org.omg.CosLifeCycle.NVP[] the_criteria) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_EXTENDED_CRITERIA);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.associatedCriteria
				.updateExtendedConsumerCriteria(the_criteria) < 0) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR_04);
			throw new org.omg.CORBA.BAD_PARAM();
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_EXTENDED_CRITERIA, admin);
		}
	}

	synchronized public Filter forwarding_discriminator() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_FORWARDING_DISCRIMINATOR);

		try {
		return admin.filterAdminDelegate.get_filter(0);
		} catch (FilterNotFound fnf)
		{
		   Filter filter =null;
	        try {
	            filter = MyChannel().default_filter_factory().create_filter( TIDParser._CONSTRAINT_GRAMMAR);
	        }
	        catch (InvalidGrammar e) {}
        
        admin.filterAdminDelegate.add_filter(filter);
		
		return filter;
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.NotificationChannelAdmin.ConsumerAdminOperations#forwarding_discriminator(org.omg.CosNotifyFilter.Filter)
	 */
	synchronized public void forwarding_discriminator(
			Filter value) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_FORWARDING_DISCRIMINATOR);
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	synchronized public org.omg.IndexAdmin.IndexLocator index_locator() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_INDEX_LOCATOR);

		if (admin.index_locator == null) {
			org.omg.PortableServer.POA index_locator_poa;
			if (_consumer_poa_policy == 0) // Create the Global POA
			{
				index_locator_poa = globalIndexLocatorPOA();
			} else if (_consumer_poa_policy == 1) // Create the Local POA
			{
				index_locator_poa = localIndexLocatorPOA(admin.channel_id);
			} else // Create the Exclusive POA
			{
				index_locator_poa = exclusiveIndexLocatorPOA(admin.channel_id,
						admin.id);
			}

			String id;
            try {
                id = PersistenceManager.getDB().getUID();
            }
            catch (Exception e) {
                throw new INTERNAL(e.toString());
            }
            admin.index_locator = new IndexLocatorData(id, index_locator_poa);
			register_index_locator(admin.index_locator, index_locator_poa);

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().save(admin.index_locator);
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_INDEXLOCATOR, admin);
			}
		}
		return NotifReference.indexLocatorReference(admin.index_locator.poa,
				admin.index_locator.id);
	}

	synchronized public void index_locator(org.omg.IndexAdmin.IndexLocator value) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_INDEX_LOCATOR);
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	synchronized public MappingFilter priority_discriminator() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_PRIORITY_DISCRIMINATOR);

		if (admin.priority_discriminator == null) {
		    
		    Any default_value = this._orb.create_any();
		    default_value.insert_short(DefaultPriority.value);
		    
		    try {
                admin.priority_discriminator =		    
                    MyChannel().default_filter_factory()
                               .create_mapping_filter(TIDParser._CONSTRAINT_GRAMMAR,
                                                      default_value);
            }
            catch (InvalidGrammar e) {}
		}
		return admin.priority_discriminator;		    
	}

	synchronized public void priority_discriminator( MappingFilter value) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_PRIORITY_DISCRIMINATOR);
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	synchronized public MappingFilter lifetime_discriminator() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_LIFETIME_DISCRIMINATOR);

		if (admin.lifetime_discriminator == null) {
		    Any default_value = this._orb.create_any();
		    
		    // TimeT is in 100 ns. units
		    TimeTHelper.insert(default_value, UTC.toTimeT(5000));
		    
		    try {
                admin.lifetime_discriminator =		    
                    MyChannel().default_filter_factory()
                               .create_mapping_filter(TIDParser._CONSTRAINT_GRAMMAR,
                                                      default_value);
            }
            catch (InvalidGrammar e) {}
		}
		    
		    return admin.priority_discriminator;
	}

	/* (non-Javadoc)
	 * @see org.omg.NotificationChannelAdmin.ConsumerAdminOperations#lifetime_discriminator(org.omg.CosNotifyFilter.MappingFilter)
	 */
	synchronized public void lifetime_discriminator(
			MappingFilter value) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_LIFETIME_DISCRIMINATOR);
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	//-----------------------------------------------------------------------

	// NUEVAS FUNCIONES DEL ATRIBUTO DISTRIBUTION_ERROR_HANDLER
	//
	public es.tid.corba.ExceptionHandlerAdmin.DistributionHandler distribution_error_handler() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_EXCEPTION_HANDLER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode.value() != OperationMode._distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_ALLOWED);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}
		if (admin.distribution_handler == null) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_ALLOWED);
			throw new org.omg.CORBA.OBJECT_NOT_EXIST(O_N_EXIST);
		}
		return admin.distribution_handler;
	}

	synchronized public void distribution_error_handler(
			es.tid.corba.ExceptionHandlerAdmin.DistributionHandler handler) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_EXCEPTION_HANDLER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode.value() != OperationMode._distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_ALLOWED);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (!(handler._is_equivalent(admin.distribution_handler))) {
			admin.distribution_handler = handler;

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_D_ERROR_HANDLER, admin);
			}
		}
	}

	//-----------------------------------------------------------------------

	// NUEVAS FUNCIONES DEL ATRIBUTO ORDER
	//
	public int order() {
		return getData().order.intValue();
	}

	synchronized public void order(int value) {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, "ConsumerAdminImpl.order()");

		if (admin.operation_mode == OperationMode.distribution) {
			if (admin.consumerAdminParent == null) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, PARENT_NULL);
				return;
			}

			if (value != admin.order.intValue()) {
				try {
					if (admin.order.intValue() != 0) {
						admin.consumerAdminParent
								.move_positioned_consumer_admin(admin.order
										.intValue(), value, admin.name);
					}
				} catch (org.omg.DistributionInternalsChannelAdmin.AlreadyDefinedOrder ex1) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR_01);
					return;
				} catch (org.omg.DistributionInternalsChannelAdmin.DataError ex2) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR_02);
					return;
				} catch (org.omg.DistributionInternalsChannelAdmin.OrderNotFound ex3) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR_03);
					return;
				}
				admin.order = new Integer(value);

				if (PersistenceManager.isActive()) {
					PersistenceManager.getDB().update(PersistenceDB.ATTR_ORDER,
							admin);
				}
			}
		}
	}

	//-----------------------------------------------------------------------

	// NUEVAS FUNCIONES DEL ATRIBUTO ORDER_GAP
	//
	public int order_gap() {
		return getData().order_gap;
	}

	public void order_gap(int value) {
		if (value > 0) {
			ConsumerAdminData admin = getData();
			admin.order_gap = value;
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(PersistenceDB.ATTR_ORDER_GAP,
						admin);
			}
		}
	}

	public org.omg.ServiceManagement.OperationMode operation_mode() {
		return getData().operation_mode;
	}

	//-----------------------------------------------------------------------

	public org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumer_admins() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, CONSUMER_ADMINS);

		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		java.util.Vector ctmp = new java.util.Vector();

		synchronized (admin.orderTable) {
			for (java.util.Iterator e = admin.orderTable.entrySet().iterator(); e
					.hasNext();) {
				java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
				Integer order = (Integer) entry.getKey();
				ObjectType object = (ObjectType) entry.getValue();

				if (object.type == CONSUMERADMIN_TYPE) {
					String admin_id = object.id;

					TRY_CONSUMERADMIN[1] = admin_id;
					TRY_CONSUMERADMIN[3] = order.toString();
					TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG,
							TRY_CONSUMERADMIN);

					ConsumerAdminData nadmin = (ConsumerAdminData) admin.consumerAdminTable
							.get(admin_id);

					if (nadmin != null) {
						ctmp.add(NotifReference.dConsumerAdminReference(
								nadmin.poa, nadmin.id));
					} else {
						TIDNotifTrace
								.print(TIDNotifTrace.ERROR,
										"ConsumerAdmin.consumer_admins(): consumer_admin NOT FOUND");
					}
				}
			}
		}
		return (org.omg.DistributionChannelAdmin.ConsumerAdmin[]) ctmp
				.toArray(new org.omg.DistributionChannelAdmin.ConsumerAdmin[0]);
	}

	public String[] consumer_admin_ids() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, CONSUMER_ADMIN_IDS);

		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		java.util.Vector ctmp = new java.util.Vector();

		synchronized (admin.orderTable) {
			for (java.util.Iterator e = admin.orderTable.values().iterator(); e
					.hasNext();) {
				//java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
				//Integer order = (Integer) entry.getKey();
				//ObjectType object = (ObjectType)entry.getValue();

				ObjectType object = (ObjectType) e.next();
				if (object.type == CONSUMERADMIN_TYPE) {
					ctmp.add(object.id);
				}
			}
		}
		return (String[]) ctmp.toArray(new String[0]);
	}

	public org.omg.NotificationChannelAdmin.ProxyPushSupplier[] obtain_push_suppliers() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PUSH_SUPPLIERS);

		java.util.Vector ctmp = new java.util.Vector();

		if (admin.operation_mode == OperationMode.distribution) {
			synchronized (admin.orderTable) {
				for (java.util.Iterator e = admin.orderTable.entrySet()
						.iterator(); e.hasNext();) {
					java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
					Integer order = (Integer) entry.getKey();
					ObjectType object = (ObjectType) entry.getValue();

					if (object.type == PUSHSUPPLIER_TYPE) {
						String proxy_id = object.id;

						TRY_PROXYPUSHSUPPLIER[1] = proxy_id;
						TRY_PROXYPUSHSUPPLIER[3] = order.toString();
						TRY_PROXYPUSHSUPPLIER[5] = "push_suppliers";
						TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG,
								TRY_PROXYPUSHSUPPLIER);

						ProxyPushSupplierData proxy = (ProxyPushSupplierData) admin.proxyPushSupplierMap
								.get(proxy_id);

						if (proxy != null) {
							ctmp.add(NotifReference
									.nProxyPushSupplierReference(proxy.poa,
											proxy.id, proxy.operation_mode));
						} else {
							TIDNotifTrace
									.print(TIDNotifTrace.ERROR,
											"ConsumerAdmin.push_suppliers(): pushSupplier NOT FOUND");
						}
					}
				}
			}
		} else {
			for (java.util.Iterator e = admin.proxyPushSupplierMap.values()
					.iterator(); e.hasNext();) {
				ProxyPushSupplierData proxy = (ProxyPushSupplierData) e.next();
				ctmp.add(NotifReference.nProxyPushSupplierReference(proxy.poa,
						proxy.id, proxy.operation_mode));
			}
		}
		return (org.omg.NotificationChannelAdmin.ProxyPushSupplier[]) ctmp
				.toArray(new org.omg.NotificationChannelAdmin.ProxyPushSupplier[0]);
	}

	public org.omg.NotificationChannelAdmin.ProxyPullSupplier[] obtain_pull_suppliers() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PULL_SUPPLIERS);

		java.util.Vector pstmp = new java.util.Vector();

		synchronized (admin.proxyPullSupplierMap) {
			for (java.util.Iterator e = admin.proxyPullSupplierMap.values()
					.iterator(); e.hasNext();) {
				ProxyPullSupplierData proxy = (ProxyPullSupplierData) e.next();
				pstmp.add(NotifReference.nProxyPullSupplierReference(proxy.poa,
						proxy.id));
			}
		}
		return (org.omg.NotificationChannelAdmin.ProxyPullSupplier[]) pstmp
				.toArray(new org.omg.NotificationChannelAdmin.ProxyPullSupplier[0]);
	}

	public String[] push_supplier_ids() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PUSH_SUPPLIER_IDS);

		if (admin.operation_mode == OperationMode.distribution) {
			java.util.Vector ctmp = new java.util.Vector();

			synchronized (admin.orderTable) {
				for (java.util.Iterator e = admin.orderTable.values()
						.iterator(); e.hasNext();) {
					//java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
					//Integer order = (Integer) entry.getKey();
					//ObjectType object = (ObjectType)entry.getValue();

					ObjectType object = (ObjectType) e.next();
					if (object.type == PUSHSUPPLIER_TYPE) {
						ctmp.add(object.id);
					}
				}
			}
			return (String[]) ctmp.toArray(new String[0]);
		} else {
			//java.util.Vector pstmp = admin.proxyPushSupplierMap.keys();
			//return (String[])pstmp.toArray(new String[0]);
			return (String[]) admin.proxyPushSupplierMap.keySet().toArray(
					new String[0]);
		}
	}

	public String[] pull_supplier_ids() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PULL_SUPPLIER_IDS);

		//java.util.Vector pstmp = admin.proxyPullSupplierMap.keys();
		//return (String[])pstmp.toArray(new String[0]);
		return (String[]) admin.proxyPullSupplierMap.keySet().toArray(
				new String[0]);
	}

	synchronized public void destroy() {
		ConsumerAdminData admin = getData();
		DESTROY[1] = admin.name;
		DESTROY[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.destroying)
			return;
		admin.destroying = true;

		basicDestroy(admin);

		if (admin.operation_mode == OperationMode.notification) {
			admin.notificationChannel.removeConsumerAdmin(admin.name);
		} else {
			if (admin.order.intValue() == 0) {
				admin.notificationChannel.removeConsumerAdmin(admin.name);
			} else {
				admin.notificationChannel.removeNewConsumerAdmin(admin.name);
				admin.consumerAdminParent.removeConsumerAdmin(admin.name);
			}
		}
		admin.associatedCriteria = null;
		admin.reference.destroyConsumerAdmin();
		admin.notificationChannel = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(admin);
		}
	}

	public void destroyFromChannel() {
		ConsumerAdminData admin = getData();
		DESTROY_FROM_CHANNEL[1] = admin.name;
		DESTROY_FROM_CHANNEL[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_CHANNEL);

		if (admin.destroying) {
			return;
		}
		admin.destroying = true;

		basicDestroy(admin);

		admin.associatedCriteria = null;
		admin.reference.destroyConsumerAdmin();
		admin.notificationChannel = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(admin);
		}
	}

	public void destroyFromAdmin() {
		ConsumerAdminData admin = getData();
		DESTROY_FROM_ADMIN[1] = admin.name;
		DESTROY_FROM_ADMIN[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_ADMIN);

		if (admin.destroying) {
			return;
		}
		admin.destroying = true;

		basicDestroy(admin);
		admin.notificationChannel.removeNewConsumerAdmin(admin.name);

		admin.associatedCriteria = null;
		admin.reference.destroyConsumerAdmin();
		admin.notificationChannel = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(admin);
		}
		return;
	}

	synchronized void basicDestroy(ConsumerAdminData admin) {
		BASIC_DESTROY[1] = admin.name;
		BASIC_DESTROY[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, BASIC_DESTROY);

		for (java.util.Iterator e = admin.proxyPushSupplierMap.values()
				.iterator(); e.hasNext();) {
			((ProxyPushSupplierData) e.next()).reference.destroyFromAdmin();
		}
		admin.proxyPushSupplierMap.clear();
		admin.updatePushMap = true;

		for (java.util.Iterator e = admin.proxyPullSupplierMap.values()
				.iterator(); e.hasNext();) {
			((ProxyPullSupplierData) e.next()).reference.destroyFromAdmin();
		}
		admin.proxyPullSupplierMap.clear();
		admin.updatePullMap = true;

		if (admin.operation_mode == OperationMode.distribution) {
			// Destroyes the ConsumerAdmins.
			for (java.util.Iterator e = admin.consumerAdminTable.values()
					.iterator(); e.hasNext();) {
				((ConsumerAdminData) e.next()).reference.destroyFromAdmin();
			}
			admin.consumerAdminTable.clear();
		}
		
		if (admin.priority_discriminator != null) {
		    admin.priority_discriminator.destroy();			
			admin.priority_discriminator = null;
		}

		if (admin.lifetime_discriminator != null) {
		    admin.lifetime_discriminator.destroy();
			admin.lifetime_discriminator = null;
		}
	}

	// ---------------------------- Funciones Locales -----------------------------

	public ProxyPushSupplierData[] getPushSupplierArray(ConsumerAdminData admin) {
		// Si alguien ha modificado el HashMap
		if (admin.updatePushMap) {
			// Bloqueamos a todos los threads que hayan sido avisados de que se
			// ha modificado el HashMap (ademas de a los que lo quieran volver
			// a modificarlo).
			synchronized (admin.proxyPushSupplierMap) {
				// De todos los threads bloqueamos solo dejamos actualizar el HashMap
				// a uno de ellos (el primero).
				if (admin.updatePushMap) {
					ProxyPushSupplierData[] values = new ProxyPushSupplierData[admin.proxyPushSupplierMap
							.size()];
					java.util.Iterator it = admin.proxyPushSupplierMap.values()
							.iterator();
					for (int index = 0; index < values.length; index++) {
						values[index] = (ProxyPushSupplierData) it.next();
					}
					admin.pushSupplierArray = values;
					admin.updatePushMap = false;
				}
			}
		}
		return admin.pushSupplierArray;
	}

	public ProxyPullSupplierData[] getPullSupplierArray(ConsumerAdminData admin) {
		// Si alguien ha modificado el HashMap
		if (admin.updatePullMap) {
			// Bloqueamos a todos los threads que hayan sido avisados de que se
			// ha modificado el HashMap (ademas de a los que lo quieran volver
			// a modificarlo).
			synchronized (admin.proxyPullSupplierMap) {
				// De todos los threads bloqueamos solo dejamos actualizar el HashMap
				// a uno de ellos (el primero).
				if (admin.updatePullMap) {
					admin.pullSupplierArray = new ProxyPullSupplierData[admin.proxyPullSupplierMap
							.size()];
					java.util.Iterator it = admin.proxyPullSupplierMap.values()
							.iterator();
					for (int index = 0; index < admin.pullSupplierArray.length; index++) {
						admin.pullSupplierArray[index] = (ProxyPullSupplierData) it
								.next();
					}
					admin.updatePullMap = false;
				}
			}
		}
		return admin.pullSupplierArray;
	}

	public void dispatch_event(org.omg.CORBA.Any event) {
		ConsumerAdminData admin = getData();
		//TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DISPATCH_EVENT);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			// Discard LOCKED
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			return;
		}
		
		try {
			if (!( admin.filterAdminDelegate.match( event ) )) {
				TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
				return;
			}
	/*		if(admin.priority_discriminator != null) {
			    AnyHolder result = new AnyHolder();			 
	            if(admin.priority_discriminator.match(event, result)) {
	                short priority = 0;
	                
	                try {
	                    priority = result.value.extract_short();
	                } catch (SystemException se) {
	                    TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
	    				return;	                    
	                }
	                
	                setRequestPriority(priority);
	            }
			} */
		} catch ( UnsupportedFilterableData ufd ){
			TIDNotifTrace.printStackTrace( TIDNotifTrace.ERROR, ufd );
			TIDNotifTrace.print(TIDNotifTrace.ERROR, DISCARD_EVENT);
			return;
		}
		
		
		

		if (admin.operation_mode == OperationMode.notification) //modo Notificacion
		{
			// Dispatch event to all ProxyPushSupplier
			if (admin.proxyPushSupplierMap.size() > 0) {
				ProxyPushSupplierData[] values = getPushSupplierArray(admin);
				for (int index = 0; index < values.length; index++) {
					// Llamada oneway
					try {
						values[index].reference.push_event(event);
					} catch (org.omg.CORBA.OBJECT_NOT_EXIST ex) {
						// Se ingnora (objeto destruyendose)
					} catch (Exception ex) {
						TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
					}
				}
				values = null;
			}

			// Dispatch event to all ProxyPullSupplier
			if (admin.proxyPullSupplierMap.size() > 0) {
				ProxyPullSupplierData[] values = getPullSupplierArray(admin);
				for (int index = 0; index < values.length; index++) {
					// Llamada oneway
					try {
						values[index].reference.push_event(event);
					} catch (org.omg.CORBA.OBJECT_NOT_EXIST ex) {
						// Se ingnora (objeto destruyendose)
					} catch (Exception ex) {
						TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
					}
				}
				values = null;
			}
		} else //if (admin.operation_mode == OperationMode.distribution)
		{
			if (deliver_event(event, admin) == true) {
			} else {
				// Se pierde el evento, hay que enviarselo al ErrorHandler
				if (admin.distribution_handler == null) {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOST_EVENT);
				} else {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, SENT_TO_HANDLER);
					try {
						admin.distribution_handler.handle_distribution(event,
								new CannotProceed(NOT_DELIVERED));
					} catch (java.lang.Exception ex) {
						TIDNotifTrace.print(TIDNotifTrace.ERROR, HANDLER_ERROR);
						TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
					}
				}
			}
		}
	}

	public void distrib_event(org.omg.CORBA.Any event)
			throws org.omg.DistributionInternalsChannelAdmin.LockedState,
			org.omg.DistributionInternalsChannelAdmin.NotMatch,
			org.omg.DistributionInternalsChannelAdmin.NotDelivered {
		ConsumerAdminData admin = getData();

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			// Discard LOCKED
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.DistributionInternalsChannelAdmin.LockedState();
		}

		
		try {
			if (!(admin.filterAdminDelegate.match( event ))) {
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, MSG_ERROR_1C);
				throw new org.omg.DistributionInternalsChannelAdmin.NotMatch();
			}
		} catch ( UnsupportedFilterableData ufd ){
			TIDNotifTrace.printStackTrace( TIDNotifTrace.ERROR, ufd );
			TIDNotifTrace.print(TIDNotifTrace.ERROR, DISCARD_EVENT);
			throw new org.omg.DistributionInternalsChannelAdmin.NotMatch();
		}
	

		if (deliver_event(event, admin) == true) {
		} else {
			// Se pierde el evento, no hay que enviarselo al ErrorHandler, se retorna
			// con error, el unico error_handler que debe de utilizarse es el del
			// consumerAdmin padre.

			TIDNotifTrace.print(TIDNotifTrace.DEBUG, MSG_ERROR_2C);
			throw new org.omg.DistributionInternalsChannelAdmin.NotDelivered();
		}
	}

	private boolean deliver_event(org.omg.CORBA.Any event,
			ConsumerAdminData admin) {
		if (admin.index_locator != null) {
			try {
				String key = null;
				String default_key = null;
				if (admin.index_locator.returned_index_type == org.omg.IndexAdmin.IndexType.string_index_type) {
					key = admin.index_locator.reference.get_string_index(event);
					default_key = admin.index_locator.default_string_index;
				} else // IndexType.long_index_type)
				{
					key = String.valueOf(admin.index_locator.reference
							.get_int_index(event));
					default_key = String
							.valueOf(admin.index_locator.default_int_index);
				}

				int tries = 0;
				ProxyPushSupplierData pushSupplier = null;
				while (tries < 2) {
					if (tries == 0) {
						tries++;
						pushSupplier = (ProxyPushSupplierData) admin.proxyPushSupplierMap
								.get(key);
					} else {
						tries++;
						pushSupplier = null;
						if (default_key != null) {
							pushSupplier = (ProxyPushSupplierData) admin.proxyPushSupplierMap
									.get(default_key);
						}
					}

					if (pushSupplier == null) {
						if (tries == 1) {
							MSG_ERROR_10D[1] = key;
							TIDNotifTrace.print(TIDNotifTrace.ERROR,
									MSG_ERROR_10D);
						} else {
							if (default_key == null) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_00D);
							} else {
								MSG_ERROR_20D[1] = default_key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_20D);
							}
						}
					} else {
						try {
							pushSupplier.reference.distrib_event(event);
							return true;
						} catch (org.omg.DistributionInternalsChannelAdmin.LockedState e0) {
							if (tries == 1) {
								MSG_ERROR_11D[1] = key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_11D);
							} else {
								MSG_ERROR_21D[1] = default_key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_21D);
							}
						} catch (org.omg.DistributionInternalsChannelAdmin.NotMatch e1) {
							if (tries == 1) {
								MSG_ERROR_12D[1] = key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_12D);
							} else {
								MSG_ERROR_22D[1] = default_key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_22D);
							}
						} catch (org.omg.DistributionInternalsChannelAdmin.ConnectionError e2) {
							if (tries == 1) {
								MSG_ERROR_13D[1] = key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_13D);
							} else {
								MSG_ERROR_23D[1] = default_key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_23D);
							}
						} catch (java.lang.Exception e3) {
							if (tries == 1) {
								MSG_ERROR_14D[1] = key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_14D);
							} else {
								MSG_ERROR_24D[1] = default_key;
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_24D);
							}
						}
					}
				}
			} catch (org.omg.IndexAdmin.InvalidExpression ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, INDX_ERROR_0);
			} catch (org.omg.IndexAdmin.FieldNotFound ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, INDX_ERROR_1);
			} catch (org.omg.IndexAdmin.TypeDoesNotMatch ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, INDX_ERROR_2);
			}
			return false;
		}

		if (admin.orderTable.size() > 0) {
			for (java.util.Iterator e = admin.orderTable.entrySet().iterator(); e
					.hasNext();) {
				try {
					java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
					Integer order = (Integer) entry.getKey();
					ObjectType object = (ObjectType) entry.getValue();

					String object_id = object.id;

					if (object.type == PUSHSUPPLIER_TYPE) {
						TRY_PROXYPUSHSUPPLIER[1] = object_id;
						TRY_PROXYPUSHSUPPLIER[3] = order.toString();
						TRY_PROXYPUSHSUPPLIER[5] = "deliver";
						TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG,
								TRY_PROXYPUSHSUPPLIER);

						ProxyPushSupplierData pushSupplier = (ProxyPushSupplierData) admin.proxyPushSupplierMap
								.get(object_id);

						if (pushSupplier == null) {
							TIDNotifTrace.print(TIDNotifTrace.ERROR,
									MSG_ERROR_5B);
						} else {
							try {
								pushSupplier.reference.distrib_event(event);
								return true;
							} catch (org.omg.DistributionInternalsChannelAdmin.LockedState e0) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_0);
							} catch (org.omg.DistributionInternalsChannelAdmin.NotMatch e1) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_1);
							} catch (org.omg.DistributionInternalsChannelAdmin.ConnectionError e2) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_2);
							} catch (java.lang.Exception e3) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_4);
							}
						}
					} else // CONSUMERADMIN_TYPE
					{
						TRY_CONSUMERADMIN[1] = object_id.toString();
						TRY_CONSUMERADMIN[3] = order.toString();
						TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG,
								TRY_CONSUMERADMIN);
						ConsumerAdminData consumerAdmin = (ConsumerAdminData) admin.consumerAdminTable
								.get(object_id);
						if (consumerAdmin == null) {
							TIDNotifTrace.print(TIDNotifTrace.ERROR,
									MSG_ERROR_6B);
						} else {
							try {
								consumerAdmin.reference.distrib_event(event);
								return true;
							} catch (org.omg.DistributionInternalsChannelAdmin.LockedState e0) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_0B);
							} catch (org.omg.DistributionInternalsChannelAdmin.NotMatch e1) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_1B);
							} catch (org.omg.DistributionInternalsChannelAdmin.NotDelivered e2) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_2B);
							} catch (java.lang.Exception e3) {
								TIDNotifTrace.print(TIDNotifTrace.ERROR,
										MSG_ERROR_4B);
							}
						}
					}
				} catch (java.util.ConcurrentModificationException xm_ex) {
					TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DELIVER_CME);
				}
			}
		}
		return false;
	}

	public String removeProxyPushSupplier(String name) {
		ConsumerAdminData admin = getData();
		REMOVE_PROXYPUSHSUPPLIER[1] = admin.name;
		REMOVE_PROXYPUSHSUPPLIER[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_PROXYPUSHSUPPLIER);

		if (admin.destroying == true)
			return proxyPushSupplierId(admin, name);

		// Lo eliminamos de la tabla
		synchronized (admin.proxyPushSupplierMap) {
			if (admin.proxyPushSupplierMap.remove(name) == null) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, PPSHS_NOT_FOUND);
			}
			admin.updatePushMap = true;
		}

		if (admin.operation_mode == OperationMode.distribution) {
			synchronized (admin.orderTable) {
				for (java.util.Iterator e = admin.orderTable.entrySet()
						.iterator(); e.hasNext();) {
					java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
					Integer key = (Integer) entry.getKey();
					ObjectType object = (ObjectType) entry.getValue();

					if (object.type == PUSHSUPPLIER_TYPE) {
						if (name.compareTo(object.id) == 0) {
							TRY_PROXYPUSHSUPPLIER[1] = object.id;
							TRY_PROXYPUSHSUPPLIER[3] = key.toString();
							TRY_PROXYPUSHSUPPLIER[5] = "remove";
							TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG,
									TRY_PROXYPUSHSUPPLIER);
							e.remove();
							break;
						}
					}
				}
			}
		}
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_PROXY_PUSH_SUPPLIER, admin);
		}
		return proxyPushSupplierId(admin, name);
	}

	public String removeProxyPullSupplier(String name) {
		ConsumerAdminData admin = getData();
		REMOVE_PROXYPULLSUPPLIER[1] = admin.name;
		REMOVE_PROXYPULLSUPPLIER[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_PROXYPULLSUPPLIER);

		if (admin.destroying == true)
			return proxyPullSupplierId(admin, name);

		// Lo eliminamos de la tabla
		synchronized (admin.proxyPullSupplierMap) {
			if (admin.proxyPullSupplierMap.remove(name) == null) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, PPLLS_NOT_FOUND);
			}
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_PROXY_PULL_SUPPLIER, admin);
		}
		return proxyPullSupplierId(admin, name);
	}

	public boolean meetCriteria(org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria {
		ConsumerAdminData admin = getData();

		if (admin.operation_mode == OperationMode.distribution) {
			if (AdminCriteria.checkInvalidCriteria(
					BaseCriteria.EXTENDED_CONSUMER, the_criteria)) {
				throw new org.omg.NotificationChannelAdmin.InvalidCriteria(
						the_criteria);
			}
		} else {
			if (AdminCriteria.checkInvalidCriteria(
					BaseCriteria.ADMIN_ONLY, the_criteria)) {
				throw new org.omg.NotificationChannelAdmin.InvalidCriteria(
						the_criteria);
			}
		}
		if (admin.associatedCriteria.compareTo(the_criteria) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 public org.omg.PortableServer.POA getPOA()
	 {
	 ConsumerAdminData admin = getData();
	 return admin.consumerPOA;
	 }
	 */

	//-----------------------------------------------------------
	private String proxyPushSupplierId(ConsumerAdminData admin, String id) {
		return (PROXYPUSHSUPPLIER_ID + admin.channel_id + SEPARATOR + admin.id
				+ SEPARATOR + id);
	}

	String proxyPullSupplierId(ConsumerAdminData admin, String id) {
		return (PROXYPULLSUPPLIER_ID + admin.channel_id + SEPARATOR + admin.id
				+ SEPARATOR + id);
	}

	/*
	private org.omg.CORBA.Any createAnyValue(int value) {
		org.omg.CORBA.Any anyvalue = _orb.create_any();
		try {
			anyvalue.insert_long(value);
		} catch (Exception ex) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, CREATE_ANY_VALUE);
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
		}
		return anyvalue;
	}
	*/

	/*
	 private void debugPushSupplier( String id, 
	 org.omg.PortableServer.POA the_poa )
	 {
	 // NOTIFICATION
	 NEW_PROXYPUSHSUPPLIER[1] = id;
	 TIDNotifTrace.print(TIDNotifTrace.USER, NEW_PROXYPUSHSUPPLIER);
	 POA_INFO[1] = the_poa.toString();
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, POA_INFO);
	 }

	 private void debugPullSupplier( String id, 
	 org.omg.PortableServer.POA the_poa )
	 {
	 // NOTIFICATION
	 NEW_PROXYPULLSUPPLIER[1] = id;
	 TIDNotifTrace.print(TIDNotifTrace.USER, NEW_PROXYPULLSUPPLIER);
	 POA_INFO[1] = the_poa.toString();
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, POA_INFO);
	 }
	 */

	public void setConsumerAdminParent(InternalConsumerAdmin parent) {
		ConsumerAdminData admin = getData();
		admin.consumerAdminParent = parent;
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_PARENT, admin);
		}
	}

	public void removeConsumerAdmin(String consumer_id) {
		ConsumerAdminData admin = getData();
		REMOVE_CONSUMER_ADMIN[1] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_CONSUMER_ADMIN);

		if (admin.destroying) {
			return;
		}

		// Lo eliminamos de la tabla
		if (admin.consumerAdminTable.remove(consumer_id) == null) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, CA_NOT_FOUND);
		}

		synchronized (admin.orderTable) {
			for (java.util.Iterator e = admin.orderTable.entrySet().iterator(); e
					.hasNext();) {
				java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
				Integer key = (Integer) entry.getKey();
				ObjectType object = (ObjectType) entry.getValue();

				if (object.type == CONSUMERADMIN_TYPE) {
					if (consumer_id.compareTo(object.id) == 0) {
						TRY_CONSUMERADMIN[1] = object.id.toString();
						TRY_CONSUMERADMIN[3] = key.toString();
						TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG,
								TRY_CONSUMERADMIN);
						e.remove();
						if (PersistenceManager.isActive()) {
							PersistenceManager.getDB().update(
									PersistenceDB.ATTR_CONSUMER_ADMIN, admin);
						}
						break;
					}
				}
			}
		}
	}

	public String getAdminId() {
		ConsumerAdminData admin = getData();
		//return admin.id;
		return admin.name;
	}

	synchronized private int update_order(ConsumerAdminData admin,
			int new_position) {
		if (new_position == 0) {
			int tmp = admin.last_order + admin.order_gap
					- (admin.last_order % admin.order_gap);
			admin.last_order = tmp;
			return admin.last_order;
		}
		// else (me dan la posicion a ocupar, solo tengo que actualizar
		if (new_position > admin.last_order) {
			admin.last_order = new_position;
		}
		return new_position;
	}

	private boolean checkOrderPosition(ConsumerAdminData admin,
			Integer new_position) {
		if (admin.orderTable.get(new_position) != null) {
			return true;
		}
		return false;
	}

	private org.omg.PortableServer.POA globalProxyPushSupplierPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_PROXYPUSHSUPPLIER_POA_ID);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_PROXYPUSHSUPPLIER_POA_ID,
					ThePOAFactory.PROXY_PUSH_SUPPLIER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPushSupplierImpl(_orb,
							_agentPOA, getPOAManager(), null, null));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA localProxyPushSupplierPOA(
			String channel_id) {
		// Creation de Supplier POA comun
		String poa_id = LOCAL_PROXYPUSHSUPPLIER_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.PROXY_PUSH_SUPPLIER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPushSupplierImpl(_orb,
							_agentPOA, getPOAManager(), channel_id, null));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA exclusiveProxyPushSupplierPOA(
			String channel_id, String supplier_id) {
		// Creation de Supplier POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(supplier_id);

		if (id_name.compareTo("") == 0) {
			poa_id = PROXYPUSHSUPPLIER_POA_ID + supplier_id;
		} else {
			poa_id = PROXYPUSHSUPPLIER_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.PROXY_PUSH_SUPPLIER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa
							.set_servant(new ProxyPushSupplierImpl(_orb,
									_agentPOA, getPOAManager(), channel_id,
									supplier_id));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA globalProxyPullSupplierPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_PROXYPULLSUPPLIER_POA_ID);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_PROXYPULLSUPPLIER_POA_ID,
					ThePOAFactory.PROXY_PUSH_SUPPLIER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPullSupplierImpl(_orb,
							_agentPOA, getPOAManager(), null, null));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA localProxyPullSupplierPOA(
			String channel_id) {
		// Creation de Supplier POA comun
		String poa_id = LOCAL_PROXYPULLSUPPLIER_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.PROXY_PUSH_SUPPLIER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPullSupplierImpl(_orb,
							_agentPOA, getPOAManager(), channel_id, null));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA exclusiveProxyPullSupplierPOA(
			String channel_id, String supplier_id) {
		// Creation de Supplier POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(supplier_id);

		if (id_name.compareTo("") == 0) {
			poa_id = PROXYPULLSUPPLIER_POA_ID + supplier_id;
		} else {
			poa_id = PROXYPULLSUPPLIER_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.PROXY_PUSH_SUPPLIER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa
							.set_servant(new ProxyPullSupplierImpl(_orb,
									_agentPOA, getPOAManager(), channel_id,
									supplier_id));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA globalIndexLocatorPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_CONSUMER_INDEXLOCATOR_POA_ID);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_CONSUMER_INDEXLOCATOR_POA_ID,
					ThePOAFactory.CONSUMER_INDEXLOCATOR_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new IndexLocatorImpl(_orb, _agentPOA));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA localIndexLocatorPOA(String channel_id) {
		// Creation de Supplier POA comun
		String poa_id = LOCAL_CONSUMER_INDEXLOCATOR_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.CONSUMER_INDEXLOCATOR_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new IndexLocatorImpl(_orb, _agentPOA));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA exclusiveIndexLocatorPOA(
			String channel_id, String supplier_id) {
		// Creation de Supplier POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(supplier_id);

		if (id_name.compareTo("") == 0) {
			poa_id = CONSUMER_INDEXLOCATOR_POA_ID + supplier_id;
		} else {
			poa_id = CONSUMER_INDEXLOCATOR_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.CONSUMER_INDEXLOCATOR_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new IndexLocatorImpl(_orb, _agentPOA));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	
	public void destroyConsumerAdmin() {
		ConsumerAdminData admin = getData();
		DESTROY_CONSUMER_ADMIN[1] = admin.name;
		DESTROY_CONSUMER_ADMIN[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_CONSUMER_ADMIN);

		unregister(admin);
		destroyConsumerPOA(admin);
	}

	public void destroyConsumerPOA(ConsumerAdminData admin) {
		if (_consumer_poa_policy == TIDNotifConfig.EXCLUSIVE_POA) {
			// Solo el root ConsumerAdmin puede destruir el POA, los ConsumerAdmin 
			// anidados no.
			if (admin.order.intValue() == 0) {
				TIDNotifTrace.print(TIDNotifTrace.DEBUG, DESTROY_CONSUMER_POA);
				String poa_id;
				String id_name = ThePOAFactory.getIdName(admin.id);

				if (id_name.compareTo("") == 0) {
					poa_id = CONSUMER_POA_ID + admin.id;
				} else {
					poa_id = CONSUMER_POA_ID + admin.channel_id + "." + id_name;
				}
				ThePOAFactory.destroyExclusivePOA(poa_id);
			}
		}
	}

	/*
	 private void debugPoaInfo( String id, 
	 org.omg.PortableServer.POA the_poa,
	 org.omg.PortableServer.POA the_father_poa )
	 {
	 // NOTIFICATION
	 NEW_POA[1] = id;
	 TIDNotifTrace.print(TIDNotifTrace.USER, NEW_POA);

	 // NOTIFICATION
	 POA_INFO_1[1] = the_poa.toString();
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, POA_INFO_1);

	 es.tid.TIDorbj.core.poa.POAManagerImpl poa_mgr = 
	 (es.tid.TIDorbj.core.poa.POAManagerImpl) the_poa.the_POAManager();

	 // NOTIFICATION
	 POA_INFO_2[1] = poa_mgr.toString();
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, POA_INFO_2);

	 // NOTIFICATION
	 POA_INFO_3[1] = the_father_poa.toString();
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, POA_INFO_3);

	 // NOTIFICATION
	 //POA_INFO_4[1] = String.valueOf(poa_mgr.getConf().getMinThreads());
	 //POA_INFO_4[3] = String.valueOf(poa_mgr.getConf().getMaxThreads());
	 //POA_INFO_4[5] = String.valueOf(poa_mgr.getConf().getMaxQueuedRequests());
	 POA_INFO_4[1] = String.valueOf(poa_mgr.get_min_threads());
	 POA_INFO_4[3] = String.valueOf(poa_mgr.get_max_threads());
	 POA_INFO_4[5] = String.valueOf(poa_mgr.get_max_queued_requests());
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, POA_INFO_4);
	 }

	synchronized private String newId() {
		return (new java.rmi.server.UID()).toString();
	}
	*/

	private void register_push_supplier(ProxyPushSupplierData proxy,
			org.omg.PortableServer.POA poa) {
		try {
			ProxyPushSupplierImpl servant = (ProxyPushSupplierImpl) poa
					.get_servant();
			servant.register(proxy);
		} catch (Exception e) {
		}
	}

	protected void unregister_push_supplier(ProxyPushSupplierData proxy) {
		try {
			ProxyPushSupplierImpl servant = (ProxyPushSupplierImpl) (proxy.poa
					.get_servant());
			servant.unregister(proxy);
		} catch (Exception e) {
		}
	}

	private void register_pull_supplier(ProxyPullSupplierData proxy,
			org.omg.PortableServer.POA poa) {
		try {
			ProxyPullSupplierImpl servant = (ProxyPullSupplierImpl) poa
					.get_servant();
			servant.register(proxy);
		} catch (Exception e) {
		}
	}

	protected void unregister_pull_supplier(ProxyPullSupplierData proxy) {
		try {
			ProxyPullSupplierImpl servant = (ProxyPullSupplierImpl) (proxy.poa
					.get_servant());
			servant.unregister(proxy);
		} catch (Exception e) {
		}
	}

	/*
	private void register_discriminator(FilterData data,
			org.omg.PortableServer.POA poa) {
		try {
			((FilterImpl) (poa.get_servant())).register(data);
		} catch (Exception e) {
		}
	}
	*/

	protected void unregister_discriminator(FilterData discriminator) {
		try {
			((FilterImpl) (discriminator.poa.get_servant()))
					.unregister(discriminator);
		} catch (Exception e) {
		}
	}

	private void register_index_locator(IndexLocatorData data,
			org.omg.PortableServer.POA poa) {
		try {
			((IndexLocatorImpl) (poa.get_servant())).register(data);
		} catch (Exception e) {
		}
	}

	protected void unregister_index_locator(IndexLocatorData index_locator) {
		try {
			((IndexLocatorImpl) (index_locator.poa.get_servant()))
					.unregister(index_locator);
		} catch (Exception e) {
		}
	}

	/*
	private void register_mapping_discriminator(MappingFilterData data,
			org.omg.PortableServer.POA poa) {
		try {
			((MappingFilterImpl) (poa.get_servant())).register(data);
		} catch (Exception e) {
		}
	}
	*/

	protected void unregister_mapping_discriminator(
			MappingFilterData mapping_discriminator) {
		try {
			((MappingFilterImpl) (mapping_discriminator.poa
					.get_servant())).unregister(mapping_discriminator);
		} catch (Exception e) {
		}
	}

	// Persistencia
	public void loadData() {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, "ConsumerAdminImpl.loadData()");
		
		if (admin.index_locator_ir != null) {		    
		    
			org.omg.PortableServer.POA index_locator_poa;
			if (_consumer_poa_policy == 0) // Create the Global POA
			{
				index_locator_poa = globalIndexLocatorPOA();
			} else if (_consumer_poa_policy == 1) // Create the Local POA
			{
				index_locator_poa = localIndexLocatorPOA(admin.channel_id);
			} else // Create the Exclusive POA
			{
				index_locator_poa = exclusiveIndexLocatorPOA(admin.channel_id,
						admin.id);
			}
			try {
				admin.index_locator = PersistenceManager.getDB().load_l(
						admin.index_locator_ir);
				admin.index_locator.poa = index_locator_poa;
				admin.index_locator.reference = NotifReference
						.indexLocatorReference(index_locator_poa,
								admin.index_locator.id);
				register_index_locator(admin.index_locator, index_locator_poa);
				admin.index_locator_ir = null;
			} catch (Exception ex) {
				admin.index_locator = null;
				admin.index_locator.reference = null;
			}
		}

		//java.util.Vector proxys = admin.proxyPushSupplierMap.keys();
		java.util.Vector proxys = new java.util.Vector();
		synchronized (admin.proxyPushSupplierMap) {
			//      for (java.util.Enumeration e = admin.proxyPushSupplierMap.keys(); 
			//                                                          e.hasMoreElements();)
			for (java.util.Iterator e = admin.proxyPushSupplierMap.keySet()
					.iterator(); e.hasNext();) {
				//        proxys.add((String)e.nextElement());
				proxys.add((String) e.next());
			}
		}
		admin.proxyPushSupplierMap.clear();

		for (java.util.Enumeration e = proxys.elements(); e.hasMoreElements();) {
			try {
				String proxy_id = (String) e.nextElement();

				org.omg.PortableServer.POA proxy_poa;
				if (_consumer_poa_policy == 0) // Create the Global POA
				{
					proxy_poa = globalProxyPushSupplierPOA();
				} else if (_consumer_poa_policy == 1) // Create the Local POA
				{
					proxy_poa = localProxyPushSupplierPOA(admin.channel_id);
				} else // Create the Exclusive POA
				{
					proxy_poa = exclusiveProxyPushSupplierPOA(admin.channel_id,
							admin.id);
				}

				ProxyPushSupplierData proxy = PersistenceManager.getDB()
						.load_ppushs(proxy_id);
				proxy.poa = proxy_poa;
				//proxy_poa.the_POAManager();
				proxy.reference = NotifReference.iProxyPushSupplierReference(
						proxy_poa, proxy_id,proxy.qosAdminDelegate.getPolicies());

				register_push_supplier(proxy, proxy_poa);

				admin.proxyPushSupplierMap.put(proxy.name, proxy);

				proxy.reference.loadData();
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		if (proxys.size() > 0) {
			admin.updatePushMap = true;
		}
		proxys.clear();

		//proxys = admin.proxyPullSupplierMap.keys();
		synchronized (admin.proxyPullSupplierMap) {
			//      for (java.util.Enumeration e = admin.proxyPullSupplierMap.keys(); 
			//                                                         e.hasMoreElements();)
			for (java.util.Iterator e = admin.proxyPullSupplierMap.keySet()
					.iterator(); e.hasNext();) {
				//        proxys.add((String)e.nextElement());
				proxys.add((String) e.next());
			}
		}
		admin.proxyPullSupplierMap.clear();

		for (java.util.Enumeration e = proxys.elements(); e.hasMoreElements();) {
			try {
				String proxy_id = (String) e.nextElement();

				org.omg.PortableServer.POA proxy_poa;
				if (_consumer_poa_policy == 0) // Create the Global POA
				{
					proxy_poa = globalProxyPullSupplierPOA();
				} else if (_consumer_poa_policy == 1) // Create the Local POA
				{
					proxy_poa = localProxyPullSupplierPOA(admin.channel_id);
				} else // Create the Exclusive POA
				{
					proxy_poa = exclusiveProxyPullSupplierPOA(admin.channel_id,
							admin.id);
				}
				ProxyPullSupplierData proxy = PersistenceManager.getDB()
						.load_ppulls(proxy_id);
				proxy.poa = proxy_poa;
				proxy.reference = NotifReference.iProxyPullSupplierReference(
						proxy_poa, proxy_id,proxy.qosAdminDelegate.getPolicies());

				register_pull_supplier(proxy, proxy_poa);

				admin.proxyPullSupplierMap.put(proxy.name, proxy);

				proxy.reference.loadData();
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		if (proxys.size() > 0) {
			admin.updatePullMap = true;
		}
		proxys.clear();

		if (admin.operation_mode == OperationMode.distribution) {
			//java.util.Vector admins = admin.consumerAdminTable.keys();
			java.util.Vector admins = new java.util.Vector();
			synchronized (admin.consumerAdminTable) {
				for (java.util.Enumeration e = admin.consumerAdminTable.keys(); e
						.hasMoreElements();) {
					admins.add((String) e.nextElement());
				}
			}
			admin.consumerAdminTable.clear();

			for (java.util.Enumeration e = admins.elements(); e
					.hasMoreElements();) {
				try {
					String consumer_id = (String) e.nextElement();
					org.omg.PortableServer.POA consumer_poa;
					if (_consumer_poa_policy == 0) // Create the Global POA
					{
						consumer_poa = globalConsumerPOA();
					} else if (_consumer_poa_policy == 1) // Create the Local POA
					{
						consumer_poa = localConsumerPOA(admin.channel_id);
					} else // Create the Exclusive POA
					{
						consumer_poa = exclusiveConsumerPOA(admin.channel_id,
								consumer_id);
					}

					ConsumerAdminData consumer_admin = PersistenceManager
							.getDB().load_ca(consumer_id);
					consumer_admin.poa = consumer_poa;
					consumer_admin.reference = 
					    NotifReference
							.internalConsumerAdminReference(consumer_poa,
									consumer_id, 
									consumer_admin.qosAdminDelegate.getPolicies());
					register_consumer(consumer_admin, consumer_poa);
					admin.consumerAdminTable.put(consumer_admin.name,
							consumer_admin);
					consumer_admin.reference.loadData();
				} catch (Exception ex) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			}
			admins.clear();
		}
	}

	private void register_consumer(ConsumerAdminData data,
			org.omg.PortableServer.POA poa) {
		try {
			((ConsumerAdminImpl) (poa.get_servant())).register(data);
		} catch (Exception e) {
		}
	}

	private org.omg.PortableServer.POA globalConsumerPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_CONSUMER_POA_ID);
		if (the_poa == null) {
			TIDNotifTrace
					.print(TIDNotifTrace.ERROR,
							"ConsumerAdminImpl.globalConsumerPOA(): ### ERROR ### POA not found");
			throw new org.omg.CORBA.INTERNAL();
		}
		return the_poa;
	}

	protected org.omg.PortableServer.POA localConsumerPOA(String channel_id) {
		// Creation de Consumer POA comun
		String poa_id = LOCAL_CONSUMER_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			TIDNotifTrace
					.print(TIDNotifTrace.ERROR,
							"ConsumerAdminImpl.localConsumerPOA(): ### ERROR ### POA not found");
			throw new org.omg.CORBA.INTERNAL();
		}
		return the_poa;
	}

	protected org.omg.PortableServer.POA exclusiveConsumerPOA(
			String channel_id, String consumer_id) {
		// Creation de Consumer POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(consumer_id);

		if (id_name.compareTo("") == 0) {
			poa_id = CONSUMER_POA_ID + consumer_id;
		} else {
			poa_id = CONSUMER_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);
		if (the_poa == null) {
			TIDNotifTrace
					.print(TIDNotifTrace.ERROR,
							"ConsumerAdminImpl.exclusiveConsumerPOA(): ### ERROR ### POA not found");
			throw new org.omg.CORBA.INTERNAL();
		}
		return the_poa;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#MyID()
	 */
	public int MyID() {
		return getData().id.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#MyChannel()
	 */
	public EventChannel MyChannel() {
		return getData().notificationChannel;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#MyOperator()
	 */
	public InterFilterGroupOperator MyOperator() {
		return getData().associatedCriteria.interfilter_group_operator();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#priority_filter()
	 */
	public MappingFilter priority_filter() {
		return this.priority_discriminator();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#priority_filter(org.omg.CosNotifyFilter.MappingFilter)
	 */
	public void priority_filter(MappingFilter value) {
		this.priority_discriminator( value );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#lifetime_filter()
	 */
	public MappingFilter lifetime_filter() {
		return this.lifetime_discriminator();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#lifetime_filter(org.omg.CosNotifyFilter.MappingFilter)
	 */
	public void lifetime_filter(MappingFilter value) {
		this.lifetime_discriminator( value );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#pull_suppliers()
	 */
	public int[] pull_suppliers() {
		String[] stringIds = this.pull_supplier_ids();
		int[] intIds = new int[ stringIds.length ];
		for ( int i=0; i < intIds.length; i++ ){
			intIds[ i ] = Integer.parseInt( stringIds[ i ] );
		}
		return intIds;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#push_suppliers()
	 */
	public int[] push_suppliers() {
		String[] stringIds = this.push_supplier_ids();
		int[] intIds = new int[ stringIds.length ];
		for ( int i=0; i < intIds.length; i++ ){
			intIds[ i ] = Integer.parseInt( stringIds[ i ] );
		}
		return intIds;	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#get_proxy_supplier(int)
	 */
	public ProxySupplier get_proxy_supplier(int proxy_id) throws ProxyNotFound {
		ConsumerAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_PULL_SUPPLIER);

		HashKey key = new HashKey( String.valueOf( proxy_id ) );
		
		ProxyPullConsumerData pullProxy;
		pullProxy = (ProxyPullConsumerData) admin.proxyPullSupplierMap.get( 
			key 
		);

		ProxyPushConsumerData pushProxy;
		pushProxy = (ProxyPushConsumerData) admin.proxyPushSupplierMap.get( 
			key 
		);

		if ( pullProxy != null ) {
			return NotifReference.nProxyPullSupplierReference(
				pullProxy.poa, pullProxy.id
			);	
		} else if (pushProxy != null ){
			return NotifReference.nProxyPushSupplierReference(
				pushProxy.poa, pushProxy.id, getData().operation_mode
			);
		} else {
			throw new ProxyNotFound();
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#obtain_notification_pull_supplier(org.omg.CosNotifyChannelAdmin.ClientType, org.omg.CORBA.IntHolder)
	 */
	public ProxySupplier obtain_notification_pull_supplier(ClientType ctype, 
	                                                       IntHolder proxy_id) 
	throws AdminLimitExceeded 
	{
	    ProxyType ptype = null;
	    switch (ctype.value()) {
	        case ClientType._ANY_EVENT:
	            ptype = ProxyType.PULL_ANY;
	           	break;
	         case ClientType._STRUCTURED_EVENT:
	             ptype = ProxyType.PULL_STRUCTURED;
	         	break;
	         default:
	             throw new BAD_PARAM( "Not supported" );
	    }
		
		ConsumerAdminData admin = getData();
		OBTAIN_PULL_SUPPLIER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PULL_SUPPLIER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		// Obtenemos el Id (global y unico)
		String id;
        try {
            id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e1) {
            throw new INTERNAL(e1.toString());
        }
        try {
			proxy_id.value = Integer.parseInt( id );
			return this.obtain_named_pull_supplier( id, id, ptype );
		} catch (ProxyAlreadyExist e) {
			throw new INTERNAL();
		} catch ( NumberFormatException nfe ){
			throw new INTERNAL();
		}
			
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#obtain_notification_push_supplier(org.omg.CosNotifyChannelAdmin.ClientType, org.omg.CORBA.IntHolder)
	 */
	public ProxySupplier obtain_notification_push_supplier(ClientType ctype, 
	                                                       IntHolder proxy_id) 
		throws AdminLimitExceeded 
	{
	    ProxyType ptype = null;
	    switch (ctype.value()) {
	        case ClientType._ANY_EVENT:
	            ptype = ProxyType.PUSH_ANY;
	           	break;
	         case ClientType._STRUCTURED_EVENT:
	             ptype = ProxyType.PUSH_STRUCTURED;
	         	break;
	         default:
	             throw new BAD_PARAM( "Not supported" );
	    }
	    
		
		ConsumerAdminData admin = getData();
		OBTAIN_PUSH_SUPPLIER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PUSH_SUPPLIER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		

		try {
//		  Obtenemos el Id (global y unico)
			String id = PersistenceManager.getDB().getUID();
			proxy_id.value = Integer.parseInt( id );
			return this.obtain_named_push_supplier( id, id, ptype );
		} catch (ProxyAlreadyExist e) {
			throw new INTERNAL();
		} catch ( NumberFormatException nfe ){
			throw new INTERNAL();
		} catch (Exception ex) {
		    throw new INTERNAL("Cannot get UID");
		}
		
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.QoSAdminOperations#get_qos()
	 */
	public Property[] get_qos() {
		return this.getData().qosAdminDelegate.get_qos();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.QoSAdminOperations#set_qos(org.omg.CosNotification.Property[])
	 */
	public void set_qos(Property[] qos) throws UnsupportedQoS {
	    ConsumerAdminData data = this.getData();
	    data.qosAdminDelegate.set_qos( qos );
		Policy[] policies = 
		    data.qosAdminDelegate.getPolicies();
		
		data.reference = 
		    InternalConsumerAdminHelper.narrow 
		    (
		    data.reference._set_policy_override(policies,
                                             	SetOverrideType.SET_OVERRIDE
                                             	)
            );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.QoSAdminOperations#validate_qos(org.omg.CosNotification.Property[], org.omg.CosNotification.NamedPropertyRangeSeqHolder)
	 */
	public void validate_qos(Property[] required_qos, NamedPropertyRangeSeqHolder available_qos) throws UnsupportedQoS {
		this.getData().qosAdminDelegate.validate_qos( required_qos, available_qos );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyComm.NotifySubscribeOperations#subscription_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
	 */
	public void subscription_change(_EventType[] added, _EventType[] removed) throws InvalidEventType {
		// TODO Auto-generated method stub
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#add_filter(org.omg.CosNotifyFilter.Filter)
	 */
	public int add_filter(Filter new_filter) {
		return this.getData().filterAdminDelegate.add_filter( new_filter );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_filter(int)
	 */
	public void remove_filter(int filter) throws FilterNotFound {
		this.getData().filterAdminDelegate.remove_filter( filter);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_filter(int)
	 */
	public Filter get_filter(int filter) throws FilterNotFound {
		return this.getData().filterAdminDelegate.get_filter( filter);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_all_filters()
	 */
	public int[] get_all_filters() {
		return this.getData().filterAdminDelegate.get_all_filters();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_all_filters()
	 */
	public void remove_all_filters() {
		this.getData().filterAdminDelegate.remove_all_filters();
	}


}
