/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 78 $
* Date: $Date: 2008-04-11 12:51:48 +0200 (Fri, 11 Apr 2008) $
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
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosLifeCycle.NVP;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.UnsupportedAdmin;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotification.MaxQueueLength;
import org.omg.CosNotification.MaxConsumers;
import org.omg.CosNotification.MaxSuppliers;
import org.omg.CosNotifyChannelAdmin.AdminNotFound;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperatorHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterFactory;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.MappingFilter;
import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelFactory;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdmin;
import org.omg.NotificationChannelAdmin.CannotMeetCriteria;
import org.omg.NotificationChannelAdmin.InvalidCriteria;
import org.omg.ServiceManagement.OperationMode;

import es.tid.TIDorbj.core.poa.OID;
import es.tid.TIDorbj.core.poa.POAManagerImpl;
import es.tid.corba.TIDNotif.qos.QueueOrderProperty;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;

import org.omg.Messaging.RequestStartTimePolicy;
import org.omg.Messaging.RequestStartTimePolicyHelper;

public class NotificationChannelImpl
		extends
		org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelPOA
		implements NotificationChannelMsg {
	private static final String NULL_ID = "";

	//private static final String DEFAULT_ID = "0";

	private static final byte PUSHSUPPLIER_TYPE = 0;

	private static final byte CONSUMERADMIN_TYPE = 1;

	private static final String SEPARATOR = ".";

	private InternalDistributionChannelFactory _channelFactory;
	
	//private OperatorFactory _operatorFactory;

	// Controla si se utiliza un POA global a todos los canales, local al canal 
	// exclusivo para cada SupplierAdmin servant
	private int _channel_poa_policy = 0;

	private int _supplier_poa_policy = 0;

	private int _consumer_poa_policy = 0;

	// Reference to the ORB
	private org.omg.CORBA.ORB _orb;

	private org.omg.PortableServer.POA _agentPOA;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// List of Channels (Pack)
	private DataTable2 _channels = null;

	// ***************************************************************************
	//
	// NotificationChannelImpl Constructor
	//
	// ***************************************************************************
	public NotificationChannelImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA poa,
			NotificationChannelFactoryImpl channel_factory)

	{
		// Orb y POA del Servicio
		_orb = orb;
		_agentPOA = poa;
		_channelFactory = channel_factory._this();		
		//_channels = channels;
		_channels = new DataTable2();
		//_discriminatorFactory = discriminator_factory;
		_current = null;
		//_data = null;

		// Politicas de POA's
		_channel_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.CHANNEL_POA_KEY);
		_supplier_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.SUPPLIER_POA_KEY);
		_consumer_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.CONSUMER_POA_KEY);

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

	private NotificationChannelData getData() {
		if (_current == null)
			setCurrent();

		byte[] oid = null;
		try {
			oid = _current.get_object_id();
			NotificationChannelData channel = (NotificationChannelData) _channels.table
					.get(new OID(oid));
			if (channel != null)
				return channel;
		}
		//catch (org.omg.CORBA.ORBPackage.InvalidName ex)
		//catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
		catch (Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.CORBA.INV_OBJREF();
		}
		throw new org.omg.CORBA.OBJECT_NOT_EXIST();
	}

	public void register(NotificationChannelData channel) {
		REGISTER[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		_channels.put(new OID(channel.id.getBytes()), channel);
	}

	public void unregister(NotificationChannelData channel) {
		UNREGISTER[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_channels.remove(new OID(channel.id.getBytes()));
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

	public void administrative_state(
			org.omg.ServiceManagement.AdministrativeState value) {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_ADMINISTRATIVE_STATE);

		if (channel.administrative_state != value) {
			channel.administrative_state = value;

			synchronized (channel.supplierAdminTable) {
				for (java.util.Iterator e = channel.supplierAdminTable.values()
						.iterator(); e.hasNext();) {
					((SupplierAdminData) e.next()).reference
							.administrative_state(value);
				}
			}

			synchronized (channel.consumerAdminTable) {
				for (java.util.Iterator e = channel.consumerAdminTable.values()
						.iterator(); e.hasNext();) {
					((ConsumerAdminData) e.next()).reference
							.administrative_state(value);
				}
			}

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_ADMINISTRATIVE_STATE, channel);
			}
		}
	}

	public org.omg.ServiceManagement.OperationMode operation_mode() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_OPERATION_MODE);
		return getData().operation_mode;
	}

	public String creation_date() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_CREATION_DATE);
		return getData().creation_date;
	}

	public String creation_time() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_CREATION_TIME);
		return getData().creation_time;
	}

	// ***************************************************************************
	//
	// NotificationChannel Operations
	//
	// org.omg.NotificationChannelAdmin.ConsumerAdmin
	//     new_for_consumers(org.omg.CosLifeCycle.NVP[] the_criteria)
	// org.omg.NotificationChannelAdmin.SupplierAdmin
	//     new_for_suppliers(org.omg.CosLifeCycle.NVP[] the_criteria)
	// org.omg.NotificationChannelAdmin.ConsumerAdmin[]
	//     find_for_consumers(org.omg.CosLifeCycle.NVP[] the_criteria)
	// org.omg.NotificationChannelAdmin.SupplierAdmin[]
	//     find_for_suppliers(org.omg.CosLifeCycle.NVP[] the_criteria)
	// org.omg.NotificationChannelAdmin.ConsumerAdmin[] consumer_admins()
	// org.omg.NotificationChannelAdmin.SupplierAdmin[] supplier_admins()
	//
	// ***************************************************************************

	public org.omg.NotificationChannelAdmin.ConsumerAdmin new_for_consumers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria {
		NotificationChannelData channel = getData();
		NEW_FOR_CONSUMERS[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FOR_CONSUMERS);

		if (channel.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		// if the_criteria is not valid this "new" throws InvalidCriteria exception
		AdminCriteria criteria = null;
		if (channel.operation_mode == OperationMode.distribution) {
			criteria = new AdminCriteria(BaseCriteria.EXTENDED_CONSUMER,
					the_criteria);
		} else {
			criteria = new AdminCriteria(BaseCriteria.ADMIN_ONLY, the_criteria);
		}

		String name_id;
		String consumer_admin_id;
        try {
            consumer_admin_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        if (NULL_ID.compareTo(criteria.id()) == 0) {
			name_id = consumer_admin_id;
		} else {
			name_id = new String(criteria.id());
		}

		if (channel.consumerAdminTable.containsKey(name_id)) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		}

		org.omg.PortableServer.POA consumer_poa;
		if (_consumer_poa_policy == 0) // Create the Global POA
		{
			consumer_poa = globalConsumerPOA();
		} else if (_consumer_poa_policy == 1) // Create the Local POA
		{
			consumer_poa = localConsumerPOA(channel.id);
		} else // Create the Exclusive POA
		{
			consumer_poa = exclusiveConsumerPOA(channel.id, consumer_admin_id);
		}

		ConsumerAdminData consumer_admin = new ConsumerAdminData(name_id,
				consumer_admin_id, channel.id, consumer_poa, channel.reference,
				criteria, channel.default_priority,
				channel.default_event_lifetime, channel.operation_mode,
				this._orb);

		register_consumer(consumer_admin, consumer_poa);

		// lo guardamos en la tabla
		synchronized (channel.consumerAdminTable) {
			channel.consumerAdminTable.put(name_id, consumer_admin);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(consumer_admin);
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, channel);
		}
		return NotifReference.consumerAdminReference(consumer_poa,
				consumer_admin_id, channel.operation_mode);
	}

	public org.omg.DistributionChannelAdmin.ConsumerAdmin new_operated_for_consumers(
			org.omg.CosLifeCycle.NVP[] the_criteria,
			OperationMode operation_mode)
			throws org.omg.DistributionChannelAdmin.InvalidCriteria {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_OPERATED_FOR_CONSUMERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (channel.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		AdminCriteria criteria = null;
		// if the_criteria is not valid this "new" throws InvalidCriteria exception
		try {
			//if the_criteria is not valid this "new" throws InvalidCriteria exception
			criteria = new AdminCriteria(BaseCriteria.EXTENDED_CONSUMER,
					the_criteria);
		} catch (java.lang.Exception ex) {
			throw new org.omg.DistributionChannelAdmin.InvalidCriteria(
					the_criteria);
		}

		String name_id;
		String consumer_admin_id;
        try {
            consumer_admin_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        if (NULL_ID.compareTo(criteria.id()) == 0) {
			name_id = consumer_admin_id;
		} else {
			name_id = new String(criteria.id());
		}

		if (channel.consumerAdminTable.containsKey(name_id)) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		}

		org.omg.PortableServer.POA consumer_poa;
		if (_consumer_poa_policy == 0) // Create the Global POA
		{
			consumer_poa = globalConsumerPOA();
		} else if (_consumer_poa_policy == 1) // Create the Local POA
		{
			consumer_poa = localConsumerPOA(channel.id);
		} else // Create the Exclusive POA
		{
			consumer_poa = exclusiveConsumerPOA(channel.id, consumer_admin_id);
		}

		ConsumerAdminData consumer_admin = new ConsumerAdminData(name_id,
				consumer_admin_id, channel.id, consumer_poa, channel.reference,
				criteria, channel.default_priority,
				channel.default_event_lifetime, operation_mode, this._orb);

		register_consumer(consumer_admin, consumer_poa);

		// lo guardamos en la tabla
		synchronized (channel.consumerAdminTable) {
			channel.consumerAdminTable.put(name_id, consumer_admin);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(consumer_admin);
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, channel);
		}
		return NotifReference.dConsumerAdminReference(consumer_poa,
				consumer_admin_id);
	}

	public InternalConsumerAdmin new_positioned_for_consumers(
			org.omg.CosLifeCycle.NVP[] the_criteria,
			InternalConsumerAdmin parent, String consumer_id, int order,
			int new_order)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria {
		NotificationChannelData channel = getData();
		NEW_POSITIONED_FOR_CONSUMERS[1] = channel.id;
		NEW_POSITIONED_FOR_CONSUMERS[3] = consumer_id;
		NEW_POSITIONED_FOR_CONSUMERS[5] = Integer.toString(order);
		NEW_POSITIONED_FOR_CONSUMERS[7] = Integer.toString(new_order);
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_POSITIONED_FOR_CONSUMERS);

		AdminCriteria criteria = new AdminCriteria(
				BaseCriteria.EXTENDED_CONSUMER, the_criteria);

		String name_id;
		String consumer_admin_id;
        try {
            consumer_admin_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        if (NULL_ID.compareTo(criteria.id()) == 0) {
			name_id = consumer_admin_id;
		} else {
			name_id = new String(criteria.id());
		}

		if (channel.newConsumerAdminTable.containsKey(name_id)) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		}

		ConsumerAdminData old_consumer_admin;
		org.omg.PortableServer.POA consumer_poa;
		if (order == 0)
			old_consumer_admin = (ConsumerAdminData) channel.consumerAdminTable
					.get(consumer_id);
		else
			old_consumer_admin = (ConsumerAdminData) channel.newConsumerAdminTable
					.get(consumer_id);

		if (old_consumer_admin == null) {
			TIDNotifTrace
					.print(TIDNotifTrace.USER,
							"NotificationChannelImpl.new_positioned_for_consumers(): ### NULL ###");

			throw new org.omg.CORBA.INTERNAL();
		}

		consumer_poa = old_consumer_admin.poa;

		ConsumerAdminData consumer_admin = new ConsumerAdminData(name_id,
				consumer_admin_id, channel.id, consumer_poa, channel.reference,
				criteria, channel.default_priority,
				channel.default_event_lifetime, channel.operation_mode,
				this._orb);

		consumer_admin.consumerAdminParent = parent;
		consumer_admin.order = new Integer(new_order);

		// Insert the servant into the parent table
		synchronized (old_consumer_admin.consumerAdminTable) {
			old_consumer_admin.consumerAdminTable.put(name_id, consumer_admin);
		}
		// Insert the Id into the Order table
		Integer key = new Integer(new_order);
		synchronized (old_consumer_admin.orderTable) {
			old_consumer_admin.orderTable.put(key, new ObjectType(name_id,
					CONSUMERADMIN_TYPE));
		}
		register_consumer(consumer_admin, consumer_poa);

		// Lo guardamos en la tabla de ConsumerAdmin's anidados
		synchronized (channel.newConsumerAdminTable) {
			channel.newConsumerAdminTable.put(name_id, consumer_admin);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(consumer_admin);
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, channel);
		}
		return consumer_admin.reference;
	}

	synchronized public org.omg.NotificationChannelAdmin.SupplierAdmin new_for_suppliers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria {
		NotificationChannelData channel = getData();
		NEW_FOR_SUPPLIERS[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FOR_SUPPLIERS);

		if (channel.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		// if the_criteria is not valid this "new" throws InvalidCriteria exception
		AdminCriteria criteria = null;
		if (channel.operation_mode == OperationMode.distribution) {
			criteria = new AdminCriteria(BaseCriteria.EXTENDED_SUPPLIER,
					the_criteria);
		} else {
			criteria = new AdminCriteria(BaseCriteria.ADMIN_ONLY, the_criteria);
		}

		String name_id;
		String supplier_admin_id;
        try {
            supplier_admin_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        if (NULL_ID.compareTo(criteria.id()) == 0) {
			name_id = supplier_admin_id;
		} else {
			name_id = new String(criteria.id());
		}

		if (channel.supplierAdminTable.containsKey(name_id)) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		}

		org.omg.PortableServer.POA supplier_poa;
		if (_supplier_poa_policy == 0) // Create the Global POA
		{
			supplier_poa = globalSupplierPOA();
		} else if (_supplier_poa_policy == 1) // Create the Local POA
		{
			supplier_poa = localSupplierPOA(channel.id);
		} else // Create the Exclusive POA
		{
			supplier_poa = exclusiveSupplierPOA(channel.id, supplier_admin_id);
		}

		SupplierAdminData supplier_admin = new SupplierAdminData(name_id,
				supplier_admin_id, channel.id, supplier_poa, channel.reference,
				criteria, channel.operation_mode, this._orb);

		register_supplier(supplier_admin, supplier_poa);

		// lo guardamos en la tabla
		synchronized (channel.supplierAdminTable) {
			channel.supplierAdminTable.put(name_id, supplier_admin);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(supplier_admin);
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_SUPPLIER_ADMIN_TABLES, channel);
		}
		return NotifReference.supplierAdminReference(supplier_poa,
				supplier_admin_id, channel.operation_mode);
	}
	
	public InternalSupplierAdmin new_transformed_for_suppliers(
			org.omg.CosLifeCycle.NVP[] the_criteria,
			InternalSupplierAdmin child, InternalSupplierAdmin parent,
			String supplier_id, int supplier_level)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria {
		NotificationChannelData channel = getData();
		NEW_TRANSFORMED_FOR_SUPPLIERS[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_TRANSFORMED_FOR_SUPPLIERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (channel.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		AdminCriteria criteria = new AdminCriteria(
				BaseCriteria.EXTENDED_SUPPLIER, the_criteria);

		String name_id;
		String supplier_admin_id;
        try {
            supplier_admin_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        if (NULL_ID.compareTo(criteria.id()) == 0) {
			name_id = supplier_admin_id;
		} else {
			name_id = new String(criteria.id());
		}

		// No se permite un admin con nombre repetido en todo el canal
		if ((channel.supplierAdminTable.containsKey(name_id))
				|| (channel.newSupplierAdminTable.containsKey(name_id))) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		}

		SupplierAdminData old_supplier_admin;
		org.omg.PortableServer.POA supplier_poa;
		if (parent == null) {
			old_supplier_admin = (SupplierAdminData) channel.supplierAdminTable
					.remove(supplier_id);

			if (old_supplier_admin == null) {
				throw new org.omg.CORBA.INTERNAL();
			} else {
				supplier_poa = old_supplier_admin.poa;
			}
		} else {
			old_supplier_admin = (SupplierAdminData) channel.newSupplierAdminTable
					.get(supplier_id);
			if (old_supplier_admin == null) {
				throw new org.omg.CORBA.INTERNAL();
			} else {
				supplier_poa = old_supplier_admin.poa;
			}
		}

		SupplierAdminData supplier_admin = new SupplierAdminData(name_id,
				supplier_admin_id, channel.id, supplier_poa, channel.reference,
				criteria, OperationMode.distribution, this._orb);

		// El nivel del nuevo es el del anterior
		supplier_admin.level = supplier_level;
		// El hijo del nuevo es el del anterior
		supplier_admin.supplierAdminChild = child;

		if (parent == null)
		// El nuevo sera un ROOT SupplierAdmin
		{
			TIDNotifTrace
					.print(TIDNotifTrace.USER,
							"  NotificationChannelImpl.new_transformed_for_suppliers: PARENT = NULL");

			// El viejo SupplierAdmin al ser ROOT se combierte en Chain SupplierAdmin
			synchronized (channel.newSupplierAdminTable) {
				channel.newSupplierAdminTable.put(supplier_id,
						old_supplier_admin);
			}

			// El nuevo SupplierAdmin pasa a ser ROOT SupplierAdmin
			synchronized (channel.supplierAdminTable) {
				channel.supplierAdminTable.put(name_id, supplier_admin);
			}
		} else
		// El nuevo sera un Chain SupplierAdmin
		{
			TIDNotifTrace
					.print(TIDNotifTrace.USER,
							"  NotificationChannelImpl.new_transformed_for_suppliers: PARENT != NULL");

			// El parent del nuevo es el parent del anterior
			supplier_admin.supplierAdminParent = parent;
			// El hijo de mi padre es ahora el nuevo SupplierAdmin
			parent.setSupplierAdminChild(supplier_admin.reference);

			// Al ser el anterior Chain el nuevo SupplierAdmin tambien es Chain 
			synchronized (channel.newSupplierAdminTable) {
				channel.newSupplierAdminTable.put(name_id, supplier_admin);
			}
		}
		register_supplier(supplier_admin, supplier_poa);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(supplier_admin);
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_SUPPLIER_ADMIN_TABLES, channel);
		}
		return supplier_admin.reference;
	}

	public org.omg.NotificationChannelAdmin.ConsumerAdmin[] find_for_consumers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria,
			org.omg.NotificationChannelAdmin.CannotMeetCriteria {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_FOR_CONSUMERS);

		java.util.Vector ctmp = new java.util.Vector();
		ConsumerAdminData admin;

		synchronized (channel.consumerAdminTable) {
			for (java.util.Iterator e = channel.consumerAdminTable.values()
					.iterator(); e.hasNext();) {
				admin = (ConsumerAdminData) e.next();
				if (admin.reference.meetCriteria(the_criteria)) {
					ctmp.add(admin);
				}
			}
		}

		if (ctmp.size() == 0) {
			throw new org.omg.NotificationChannelAdmin.CannotMeetCriteria(
					the_criteria);
		}

		org.omg.NotificationChannelAdmin.ConsumerAdmin[] consumers = new org.omg.NotificationChannelAdmin.ConsumerAdmin[ctmp
				.size()];

		int i = 0;
		for (java.util.Enumeration e = ctmp.elements(); e.hasMoreElements();) {
			admin = (ConsumerAdminData) e.nextElement();
			consumers[i++] = NotifReference.consumerAdminReference(admin.poa,
					admin.id, admin.operation_mode);
		}
		ctmp.removeAllElements();
		ctmp = null;
		return consumers;
	}

	public org.omg.NotificationChannelAdmin.SupplierAdmin[] find_for_suppliers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria,
			org.omg.NotificationChannelAdmin.CannotMeetCriteria {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_FOR_SUPPLIERS);

		java.util.Vector stmp = new java.util.Vector();
		SupplierAdminData admin;

		synchronized (channel.supplierAdminTable) {
			for (java.util.Iterator e = channel.supplierAdminTable.values()
					.iterator(); e.hasNext();) {
				admin = (SupplierAdminData) e.next();
				if (admin.reference.meetCriteria(the_criteria)) {
					stmp.add(admin);
				}
			}
		}

		if (stmp.size() == 0) {
			throw new org.omg.NotificationChannelAdmin.CannotMeetCriteria(
					the_criteria);
		}

		org.omg.NotificationChannelAdmin.SupplierAdmin[] suppliers = new org.omg.NotificationChannelAdmin.SupplierAdmin[stmp
				.size()];

		int i = 0;
		for (java.util.Enumeration e = stmp.elements(); e.hasMoreElements();) {
			admin = (SupplierAdminData) e.nextElement();
			suppliers[i++] = NotifReference.supplierAdminReference(admin.poa,
					admin.id, admin.operation_mode);
		}
		stmp.removeAllElements();
		stmp = null;
		return suppliers;
	}

	public org.omg.DistributionChannelAdmin.ConsumerAdmin[] new_find_for_consumers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.DistributionChannelAdmin.InvalidCriteria,
			org.omg.DistributionChannelAdmin.CannotMeetCriteria {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FIND_FOR_CONSUMERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		java.util.Vector ctmp = new java.util.Vector();
		ConsumerAdminData admin;

		try {
			synchronized (channel.consumerAdminTable) {
				for (java.util.Iterator e = channel.consumerAdminTable.values()
						.iterator(); e.hasNext();) {
					admin = (ConsumerAdminData) e.next();
					if (admin.reference.meetCriteria(the_criteria)) {
						ctmp.add(admin);
					}
				}
			}

			synchronized (channel.newConsumerAdminTable) {
				for (java.util.Iterator e = channel.newConsumerAdminTable
						.values().iterator(); e.hasNext();) {
					admin = (ConsumerAdminData) e.next();
					if (admin.reference.meetCriteria(the_criteria)) {
						ctmp.add(admin);
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

		org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers = new org.omg.DistributionChannelAdmin.ConsumerAdmin[ctmp
				.size()];

		int i = 0;
		for (java.util.Enumeration e = ctmp.elements(); e.hasMoreElements();) {
			admin = (ConsumerAdminData) e.nextElement();
			consumers[i++] = NotifReference.dConsumerAdminReference(admin.poa,
					admin.id);
		}
		ctmp.removeAllElements();
		ctmp = null;

		return consumers;
	}

	public org.omg.DistributionChannelAdmin.SupplierAdmin[] new_find_for_suppliers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.DistributionChannelAdmin.InvalidCriteria,
			org.omg.DistributionChannelAdmin.CannotMeetCriteria {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FIND_FOR_SUPPLIERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		java.util.Vector stmp = new java.util.Vector();
		SupplierAdminData admin;

		try {
			synchronized (channel.supplierAdminTable) {
				for (java.util.Iterator e = channel.supplierAdminTable.values()
						.iterator(); e.hasNext();) {
					admin = (SupplierAdminData) e.next();
					if (admin.reference.meetCriteria(the_criteria)) {
						stmp.add(admin);
					}
				}
			}

			synchronized (channel.newSupplierAdminTable) {
				for (java.util.Iterator e = channel.newSupplierAdminTable
						.values().iterator(); e.hasNext();) {
					admin = (SupplierAdminData) e.next();
					if (admin.reference.meetCriteria(the_criteria)) {
						stmp.add(admin);
					}
				}
			}
		} catch (org.omg.NotificationChannelAdmin.InvalidCriteria ex) {
			throw new org.omg.DistributionChannelAdmin.InvalidCriteria(
					the_criteria);
		}

		if (stmp.size() == 0) {
			throw new org.omg.DistributionChannelAdmin.CannotMeetCriteria(
					the_criteria);
		}

		org.omg.DistributionChannelAdmin.SupplierAdmin[] suppliers = new org.omg.DistributionChannelAdmin.SupplierAdmin[stmp
				.size()];

		int i = 0;
		for (java.util.Enumeration e = stmp.elements(); e.hasMoreElements();) {
			admin = (SupplierAdminData) e.nextElement();
			suppliers[i++] = NotifReference.dSupplierAdminReference(admin.poa,
					admin.id);
		}
		stmp.removeAllElements();
		stmp = null;
		return suppliers;
	}

	public org.omg.NotificationChannelAdmin.ConsumerAdmin[] consumer_admins() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, CONSUMER_ADMINS);

		//java.util.Vector ctmp = channel.consumerAdminTable.values();
		java.util.Vector ctmp = new java.util.Vector();
		synchronized (channel.consumerAdminTable) {
			for (java.util.Iterator e = channel.consumerAdminTable.values()
					.iterator(); e.hasNext();) {
				ctmp.add(e.next());
			}
		}

		org.omg.NotificationChannelAdmin.ConsumerAdmin[] consumers = new org.omg.NotificationChannelAdmin.ConsumerAdmin[ctmp
				.size()];
		int i = 0;
		for (java.util.Enumeration e = ctmp.elements(); e.hasMoreElements();) {
			ConsumerAdminData admin = (ConsumerAdminData) e.nextElement();
			consumers[i++] = NotifReference.consumerAdminReference(admin.poa,
					admin.id, admin.operation_mode);
		}
		ctmp.removeAllElements();
		ctmp = null;
		return consumers;
	}

	public org.omg.NotificationChannelAdmin.SupplierAdmin[] supplier_admins() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SUPPLIER_ADMINS);

		//java.util.Vector stmp = channel.supplierAdminTable.values();
		java.util.Vector stmp = new java.util.Vector();
		synchronized (channel.supplierAdminTable) {
			for (java.util.Iterator e = channel.supplierAdminTable.values()
					.iterator(); e.hasNext();) {
				stmp.add(e.next());
			}
		}

		org.omg.NotificationChannelAdmin.SupplierAdmin[] suppliers = new org.omg.NotificationChannelAdmin.SupplierAdmin[stmp
				.size()];
		int i = 0;
		for (java.util.Enumeration e = stmp.elements(); e.hasMoreElements();) {
			SupplierAdminData admin = (SupplierAdminData) e.nextElement();
			suppliers[i++] = NotifReference.supplierAdminReference(admin.poa,
					admin.id, admin.operation_mode);
		}
		stmp.removeAllElements();
		stmp = null;

		return suppliers;
	}

	public String[] consumer_admin_ids() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, CONSUMER_ADMIN_IDS);

		//java.util.Vector ctmp = channel.consumerAdminTable.keys();
		java.util.Vector ctmp = new java.util.Vector();
		synchronized (channel.consumerAdminTable) {
			for (java.util.Enumeration e = channel.consumerAdminTable.keys(); e
					.hasMoreElements();) {
				ctmp.add((String) e.nextElement());
			}
		}

		String[] consumers = new String[ctmp.size()];
		int i = 0;
		for (java.util.Enumeration e = ctmp.elements(); e.hasMoreElements();) {
			consumers[i++] = (String) e.nextElement();
		}
		ctmp.removeAllElements();
		ctmp = null;
		return consumers;
	}

	public String[] supplier_admin_ids() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SUPPLIER_ADMIN_IDS);

		//java.util.Vector stmp = channel.supplierAdminTable.keys();
		java.util.Vector stmp = new java.util.Vector();
		synchronized (channel.supplierAdminTable) {
			for (java.util.Enumeration e = channel.supplierAdminTable.keys(); e
					.hasMoreElements();) {
				stmp.add((String) e.nextElement());
			}
		}

		String[] suppliers = new String[stmp.size()];
		int i = 0;
		for (java.util.Enumeration e = stmp.elements(); e.hasMoreElements();) {
			suppliers[i++] = (String) e.nextElement();
		}
		stmp.removeAllElements();
		stmp = null;
		return suppliers;
	}

	public org.omg.DistributionChannelAdmin.ConsumerAdmin[] new_consumer_admins() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_CONSUMER_ADMINS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		//java.util.Vector ctmp = channel.consumerAdminTable.values();
		java.util.Vector ctmp = new java.util.Vector();
		synchronized (channel.consumerAdminTable) {
			for (java.util.Iterator e = channel.consumerAdminTable.values()
					.iterator(); e.hasNext();) {
				ctmp.add(e.next());
			}
		}
		//java.util.Vector nctmp = channel.newConsumerAdminTable.values();
		java.util.Vector nctmp = new java.util.Vector();
		synchronized (channel.newConsumerAdminTable) {
			for (java.util.Iterator e = channel.newConsumerAdminTable.values()
					.iterator(); e.hasNext();) {
				nctmp.add((CommonData) e.next());
			}
		}

		org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers = new org.omg.DistributionChannelAdmin.ConsumerAdmin[ctmp
				.size()
				+ nctmp.size()];

		int i = 0;
		for (java.util.Enumeration e = ctmp.elements(); e.hasMoreElements();) {
			ConsumerAdminData consumer_admin = (ConsumerAdminData) e
					.nextElement();
			consumers[i++] = NotifReference.dConsumerAdminReference(
					consumer_admin.poa, consumer_admin.id);
		}
		for (java.util.Enumeration e = nctmp.elements(); e.hasMoreElements();) {
			ConsumerAdminData consumer_admin = (ConsumerAdminData) e
					.nextElement();
			consumers[i++] = NotifReference.dConsumerAdminReference(
					consumer_admin.poa, consumer_admin.id);
		}
		ctmp.removeAllElements();
		ctmp = null;

		return consumers;
	}

	public org.omg.DistributionChannelAdmin.SupplierAdmin[] new_supplier_admins() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_SUPPLIER_ADMINS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		//java.util.Vector stmp = channel.supplierAdminTable.values();
		java.util.Vector stmp = new java.util.Vector();
		synchronized (channel.supplierAdminTable) {
			for (java.util.Iterator e = channel.supplierAdminTable.values()
					.iterator(); e.hasNext();) {
				stmp.add(e.next());
			}
		}
		//java.util.Vector nstmp = channel.newSupplierAdminTable.values();
		java.util.Vector nstmp = new java.util.Vector();
		synchronized (channel.newSupplierAdminTable) {
			for (java.util.Iterator e = channel.newSupplierAdminTable.values()
					.iterator(); e.hasNext();) {
				nstmp.add((CommonData) e.next());
			}
		}

		org.omg.DistributionChannelAdmin.SupplierAdmin[] suppliers = new org.omg.DistributionChannelAdmin.SupplierAdmin[stmp
				.size()
				+ nstmp.size()];

		int i = 0;
		for (java.util.Enumeration e = stmp.elements(); e.hasMoreElements();) {
			SupplierAdminData supplier_admin = (SupplierAdminData) e
					.nextElement();
			suppliers[i++] = NotifReference.dSupplierAdminReference(
					supplier_admin.poa, supplier_admin.id);
		}
		for (java.util.Enumeration e = nstmp.elements(); e.hasMoreElements();) {
			SupplierAdminData supplier_admin = (SupplierAdminData) e
					.nextElement();
			suppliers[i++] = NotifReference.dSupplierAdminReference(
					supplier_admin.poa, supplier_admin.id);
		}
		stmp.removeAllElements();
		stmp = null;

		return suppliers;
	}

	public String[] new_consumer_admin_ids() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_CONSUMER_ADMIN_IDS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		//java.util.Vector ctmp = channel.consumerAdminTable.keys();
		java.util.Vector ctmp = new java.util.Vector();
		synchronized (channel.consumerAdminTable) {
			for (java.util.Enumeration e = channel.consumerAdminTable.keys(); e
					.hasMoreElements();) {
				ctmp.add((String) e.nextElement());
			}
		}
		//java.util.Vector nctmp = channel.newConsumerAdminTable.keys();
		java.util.Vector nctmp = new java.util.Vector();
		synchronized (channel.newConsumerAdminTable) {
			for (java.util.Enumeration e = channel.newConsumerAdminTable.keys(); e
					.hasMoreElements();) {
				nctmp.add((String) e.nextElement());
			}
		}

		String[] consumers = new String[ctmp.size() + nctmp.size()];

		int i = 0;
		for (java.util.Enumeration e = ctmp.elements(); e.hasMoreElements();) {
			consumers[i++] = (String) e.nextElement();
		}
		for (java.util.Enumeration e = nctmp.elements(); e.hasMoreElements();) {
			consumers[i++] = (String) e.nextElement();
		}
		ctmp.removeAllElements();
		ctmp = null;

		return consumers;
	}

	public String[] new_supplier_admin_ids() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_SUPPLIER_ADMIN_IDS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (channel.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		//java.util.Vector stmp = channel.supplierAdminTable.keys();
		java.util.Vector stmp = new java.util.Vector();
		synchronized (channel.supplierAdminTable) {
			for (java.util.Enumeration e = channel.supplierAdminTable.keys(); e
					.hasMoreElements();) {
				stmp.add((String) e.nextElement());
			}
		}
		//java.util.Vector nstmp = channel.newSupplierAdminTable.keys();
		java.util.Vector nstmp = new java.util.Vector();
		synchronized (channel.newSupplierAdminTable) {
			for (java.util.Enumeration e = channel.newSupplierAdminTable.keys(); e
					.hasMoreElements();) {
				nstmp.add((String) e.nextElement());
			}
		}

		String[] suppliers = new String[stmp.size() + nstmp.size()];

		int i = 0;
		for (java.util.Enumeration e = stmp.elements(); e.hasMoreElements();) {
			suppliers[i++] = (String) e.nextElement();
		}
		for (java.util.Enumeration e = nstmp.elements(); e.hasMoreElements();) {
			suppliers[i++] = (String) e.nextElement();
		}
		stmp.removeAllElements();
		stmp = null;

		return suppliers;
	}

	public int default_priority() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_PRIORYTY);
		return getData().default_priority;
	}

	public void default_priority(int value) {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_PRIORYTY);
		if (channel.default_priority != value) {
			channel.default_priority = value;
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_DEFAULT_PRIORITY, channel);
			}
		}
	}

	public int default_event_lifetime() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_LIFETIME);
		return getData().default_event_lifetime;
	}

	public void default_event_lifetime(int value) {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_LIFETIME);
		if (channel.default_event_lifetime != value) {
			channel.default_event_lifetime = value;
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_DEFAULT_LIFETIME, channel);
			}
		}
	}

	// ***************************************************************************
	//
	// CosEventChannelAdmin Operations
	//
	// org.omg.CosEventChannelAdmin.ConsumerAdmin for_consumers()
	// org.omg.CosEventChannelAdmin.SupplierAdmin for_suppliers()
	// void destroy()
	//
	// ***************************************************************************

	/**
	 * returns an object reference that supports the 
	 * CosEventChannelAdmin::ConsumerAdmin interface, always return the same 
	 * reference (to the default object)
	 * @author Alvaro Rodriguez
	 * @version 1.0
	 */
	synchronized public org.omg.CosEventChannelAdmin.ConsumerAdmin for_consumers() {
		return this.default_consumer_admin();
	}

	/**
	 * returns an object reference that supports the 
	 * CosEventChannelAdmin::SupplierAdmin interface, always return the same 
	 * reference (to the default object)
	 */
	synchronized public org.omg.CosEventChannelAdmin.SupplierAdmin for_suppliers() {
		return this.default_supplier_admin();
	}

	/**
	 *  destroy the event channel
	 */
	synchronized public void destroy() {
		NotificationChannelData channel = getData();
		DESTROY[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY);

		if (channel.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (channel.destroying) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, "Already Destroying.");
			return;
		}
		channel.destroying = true;

		// Remove the channel id from the list
		_channelFactory.removeNotificationChannel(channel.id);

		// Destroyes the ConsumerAdmins.
		synchronized (channel.consumerAdminTable) {
			for (java.util.Iterator e = channel.consumerAdminTable.values()
					.iterator(); e.hasNext();) {
				try {
					((ConsumerAdminData) e.next()).reference
							.destroyFromChannel();
				} catch (java.lang.Exception ex) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			}
		}
		channel.default_consumer_id = null;
		channel.consumerAdminTable.clear();

		// Destroyes the SupplierAdmins.
		synchronized (channel.supplierAdminTable) {
			for (java.util.Iterator e = channel.supplierAdminTable.values()
					.iterator(); e.hasNext();) {
				try {
					((SupplierAdminData) e.next()).reference
							.destroyFromChannel();
				} catch (java.lang.Exception ex) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			}
		}
		channel.default_supplier_id = null;
		channel.supplierAdminTable.clear();

		if (channel.operation_mode == OperationMode.distribution) {
			channel.newConsumerAdminTable.clear();
			channel.newSupplierAdminTable.clear();
		}

		try {
			channel.reference.destroyChannel();
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			// Unregister channel
			unregister(channel);
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().delete(channel);
			}
		}
	}

	public void destroyChannel() {
		NotificationChannelData channel = getData();
		DESTROY_CHANNEL[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, DESTROY_CHANNEL);

		// Unregister channel
		unregister(channel);

		//destroy the local POA's
		destroyLocalChannelPOA(channel);
		destroyLocalSupplierPOA(channel);
		destroyLocalConsumerPOA(channel);

		ThePOAFactory.debugPOAs();

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(channel);
		}
	}

	// ***************************************************************************
	//
	// NotificationChannelImpl Internal Operations 
	//
	// protected void dispatchEvent(org.omg.CORBA.Any event)
	//
	// ***************************************************************************

	public void dispatch_event(org.omg.CORBA.Any event) {
		NotificationChannelData channel = getData();
		//TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DISPATCH_EVENT);

		// Estudiar esto mas detenidamente ya que puede que no sea necesario en
		// la gran mayoria de casos

		synchronized (channel.consumerAdminTable) {
			for (java.util.Iterator e = channel.consumerAdminTable.values()
					.iterator(); e.hasNext();) {
				((ConsumerAdminData) e.next()).reference.dispatch_event(event);
			}
		}
	}

	private void register_supplier(SupplierAdminData data,
			org.omg.PortableServer.POA poa) {
		try {
			((SupplierAdminImpl) (poa.get_servant())).register(data);
		} catch (Exception e) {
		}
	}

	private void register_consumer(ConsumerAdminData data,
			org.omg.PortableServer.POA poa) {
		try {
			((ConsumerAdminImpl) (poa.get_servant())).register(data);
		} catch (Exception e) {
		}
	}

	private org.omg.PortableServer.POA globalSupplierPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_SUPPLIER_POA_ID);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(GLOBAL_SUPPLIER_POA_ID,
					ThePOAFactory.SUPPLIER_ADMIN_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new SupplierAdminImpl(_orb, _agentPOA,/*_globalSupplierTable,*/
							the_poa.the_POAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	protected org.omg.PortableServer.POA localSupplierPOA(String channel_id) {
		// Creation de Supplier POA comun
		String poa_id = LOCAL_SUPPLIER_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.SUPPLIER_ADMIN_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new SupplierAdminImpl(_orb, _agentPOA,/*null,*/
							the_poa.the_POAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	protected org.omg.PortableServer.POA exclusiveSupplierPOA(
			String channel_id, String supplier_id) {
		// Creation de Supplier POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(supplier_id);

		if (id_name.compareTo("") == 0) {
			poa_id = SUPPLIER_POA_ID + supplier_id;
		} else {
			poa_id = SUPPLIER_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.SUPPLIER_ADMIN_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new SupplierAdminImpl(_orb, _agentPOA,/*null,*/
							the_poa.the_POAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	synchronized private void destroyLocalChannelPOA(
			NotificationChannelData data) {
		if (_channel_poa_policy == TIDNotifConfig.LOCAL_POA) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, DESTROY_LOCAL_CHANNEL_POA);
			ThePOAFactory.destroyLocalPOA(LOCAL_CHANNEL_POA_ID + data.id, true);
		}
	}

	synchronized private void destroyLocalSupplierPOA(
			NotificationChannelData data) {
		if (_supplier_poa_policy == TIDNotifConfig.LOCAL_POA) {
			TIDNotifTrace
					.print(TIDNotifTrace.DEBUG, DESTROY_LOCAL_SUPPLIER_POA);

			ThePOAFactory.destroyLocalPOA(LOCAL_PROXYPULLCONSUMER_POA_ID
					+ data.id, false);
			ThePOAFactory.destroyLocalPOA(LOCAL_PROXYPUSHCONSUMER_POA_ID
					+ data.id, false);
			ThePOAFactory.destroyLocalPOA(LOCAL_TRANSFORMING_OPERATOR_POA_ID
					+ data.id, false);
			ThePOAFactory.destroyLocalPOA(LOCAL_SUPPLIER_DISCRIMINATOR_POA_ID
					+ data.id, false);
			ThePOAFactory
					.destroyLocalPOA(LOCAL_SUPPLIER_POA_ID + data.id, true);
		}
	}

	private org.omg.PortableServer.POA globalConsumerPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_CONSUMER_POA_ID);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(GLOBAL_CONSUMER_POA_ID,
					ThePOAFactory.CONSUMER_ADMIN_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa
							.set_servant(new ConsumerAdminImpl(_orb, _agentPOA,
									the_poa.the_POAManager()/*,_globalConsumerTable*/));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	protected org.omg.PortableServer.POA localConsumerPOA(String channel_id) {
		// Creation de Consumer POA comun
		String poa_id = LOCAL_CONSUMER_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.CONSUMER_ADMIN_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ConsumerAdminImpl(_orb, _agentPOA,
							the_poa.the_POAManager()/*,null*/));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
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
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.CONSUMER_ADMIN_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ConsumerAdminImpl(_orb, _agentPOA,
							the_poa.the_POAManager()/*,null*/));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	synchronized private void destroyLocalConsumerPOA(
			NotificationChannelData data) {
		if (_consumer_poa_policy == TIDNotifConfig.LOCAL_POA) {
			TIDNotifTrace
					.print(TIDNotifTrace.DEBUG, DESTROY_LOCAL_CONSUMER_POA);
			ThePOAFactory.destroyLocalPOA(LOCAL_PROXYPUSHSUPPLIER_POA_ID
					+ data.id, false);
			ThePOAFactory.destroyLocalPOA(LOCAL_PROXYPULLSUPPLIER_POA_ID
					+ data.id, false);
			ThePOAFactory.destroyLocalPOA(LOCAL_CONSUMER_DISCRIMINATOR_POA_ID
					+ data.id, false);
			ThePOAFactory
					.destroyLocalPOA(LOCAL_CONSUMER_POA_ID + data.id, true);
		}
	}

	// Removes a SupplierAdmin from the list.
	synchronized public void removeSupplierAdmin(String supplier_id) {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_SUPPLIER_ADMIN);

		if (channel.destroying) {
			return;
		}

		if ((channel.default_supplier_id != null)
				&& (channel.default_supplier_id.compareTo(supplier_id) == 0)) {
			channel.default_supplier_id = null;
		}

		SupplierAdminData supplierAdmin = (SupplierAdminData) channel.supplierAdminTable
				.remove(supplier_id);

		if (supplierAdmin == null) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, NOT_FOUND);
		} else {
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_SUPPLIER_ADMIN_TABLES, channel);
			}
		}
	}

	// Removes a SupplierAdmin from the list.
	synchronized public void removeNewSupplierAdmin(String supplier_id) {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_NEW_SUPPLIER_ADMIN);

		if (channel.destroying) {
			return;
		}

		SupplierAdminData supplierAdmin = (SupplierAdminData) channel.newSupplierAdminTable
				.remove(supplier_id);

		if (supplierAdmin == null) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, NOT_FOUND);
		} else {
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_SUPPLIER_ADMIN_TABLES, channel);
			}
		}
	}

	// Swap a SupplierAdmin from the list.
	synchronized public void swapSupplierAdmin(String supplier_id) {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SWAP_SUPPLIER_ADMIN);

		if (channel.destroying) {
			return;
		}

		SupplierAdminData supplierAdmin = (SupplierAdminData) channel.newSupplierAdminTable
				.remove(supplier_id);

		if (supplierAdmin == null) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, NOT_FOUND);
		} else {
			channel.supplierAdminTable.put(supplier_id, supplierAdmin);

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_SUPPLIER_ADMIN_TABLES, channel);
			}
		}
	}

	// Removes a ConsumerAdmin from the list.
	synchronized public void removeConsumerAdmin(String consumer_id) {
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_CONSUMER_ADMIN);

		NotificationChannelData channel = getData();

		if (channel.destroying) {
			return;
		}

		if ((channel.default_consumer_id != null)
				&& (channel.default_consumer_id.compareTo(consumer_id) == 0)) {
			channel.default_consumer_id = null;
		}

		ConsumerAdminData consumerAdmin = (ConsumerAdminData) channel.consumerAdminTable
				.remove(consumer_id);

		if (consumerAdmin == null) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, NOT_FOUND);
		} else {
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, channel);
			}
		}
	}

	// Removes a ConsumerAdmin from the list.
	synchronized public void removeNewConsumerAdmin(String consumer_id) {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_NEW_CONSUMER_ADMIN);

		if (channel.destroying) {
			return;
		}

		ConsumerAdminData consumerAdmin = (ConsumerAdminData) channel.newConsumerAdminTable
				.remove(consumer_id);

		if (consumerAdmin == null) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, NOT_FOUND);
		} else {
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, channel);
			}
		}
	}

	public void loadData() {
		NotificationChannelData channel = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER,
				"NotificationChannelImpl.loadData()");
		
		//Vector admins = channel.supplierAdminTable.keys();
		java.util.Vector admins = new java.util.Vector();
		synchronized (channel.supplierAdminTable) {
			for (java.util.Enumeration e = channel.supplierAdminTable.keys(); e
					.hasMoreElements();) {
				admins.add((String) e.nextElement());
			}
		}
		channel.supplierAdminTable.clear();

		for (java.util.Enumeration e = admins.elements(); e.hasMoreElements();) {
			try {
				String supplier_id = (String) e.nextElement();
				org.omg.PortableServer.POA supplier_poa;
				if (_supplier_poa_policy == 0) // Create the Global POA
				{
					supplier_poa = globalSupplierPOA();
				} else if (_supplier_poa_policy == 1) // Create the Local POA
				{
					supplier_poa = localSupplierPOA(channel.id);
				} else // Create the Exclusive POA
				{
					supplier_poa = exclusiveSupplierPOA(channel.id, supplier_id);
				}

				SupplierAdminData supplier_admin = PersistenceManager.getDB()
						.load_sa(supplier_id);

				supplier_admin.poa = supplier_poa;
				supplier_admin.reference = NotifReference
						.internalSupplierAdminReference(supplier_poa,
								supplier_id,
								supplier_admin.qosAdminDelegate.getPolicies());
				
				register_supplier(supplier_admin, supplier_poa);
				channel.supplierAdminTable.put(supplier_admin.name,
						supplier_admin);
				supplier_admin.reference.loadData();
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		admins.clear();

		//admins = channel.consumerAdminTable.keys();
		admins = new java.util.Vector();
		synchronized (channel.consumerAdminTable) {
			for (java.util.Enumeration e = channel.consumerAdminTable.keys(); e
					.hasMoreElements();) {
				admins.add((String) e.nextElement());
			}
		}
		channel.consumerAdminTable.clear();

		for (java.util.Enumeration e = admins.elements(); e.hasMoreElements();) {
			try {
				String consumer_id = (String) e.nextElement();
				org.omg.PortableServer.POA consumer_poa;
				if (_consumer_poa_policy == 0) // Create the Global POA
				{
					consumer_poa = globalConsumerPOA();
				} else if (_consumer_poa_policy == 1) // Create the Local POA
				{
					consumer_poa = localConsumerPOA(channel.id);
				} else // Create the Exclusive POA
				{
					consumer_poa = exclusiveConsumerPOA(channel.id, consumer_id);
				}

				ConsumerAdminData consumer_admin = PersistenceManager.getDB()
						.load_ca(consumer_id);
				consumer_admin.poa = consumer_poa;
				consumer_admin.reference = NotifReference
						.internalConsumerAdminReference(consumer_poa,
								consumer_id,
								consumer_admin.qosAdminDelegate.getPolicies());
				
				register_consumer(consumer_admin, consumer_poa);
				channel.consumerAdminTable.put(consumer_admin.name,
						consumer_admin);
				consumer_admin.reference.loadData();
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		admins.clear();

		if (channel.operation_mode == OperationMode.distribution) {
			//admins = channel.newSupplierAdminTable.keys();
			admins = new java.util.Vector();
			synchronized (channel.newSupplierAdminTable) {
				for (java.util.Enumeration e = channel.newSupplierAdminTable
						.keys(); e.hasMoreElements();) {
					admins.add((String) e.nextElement());
				}
			}
			channel.newSupplierAdminTable.clear();

			for (java.util.Enumeration e = admins.elements(); e
					.hasMoreElements();) {
				try {
					String supplier_id = (String) e.nextElement();
					org.omg.PortableServer.POA supplier_poa;
					if (_supplier_poa_policy == 0) // Create the Global POA
					{
						supplier_poa = globalSupplierPOA();
					} else if (_supplier_poa_policy == 1) // Create the Local POA
					{
						supplier_poa = localSupplierPOA(channel.id);
					} else // Create the Exclusive POA
					{
						supplier_poa = exclusiveSupplierPOA(channel.id,
								supplier_id);
					}

					SupplierAdminData supplier_admin = PersistenceManager
							.getDB().load_sa(supplier_id);
					supplier_admin.poa = supplier_poa;
					supplier_admin.reference = NotifReference
							.internalSupplierAdminReference(supplier_poa,
									supplier_id,
									supplier_admin.qosAdminDelegate.getPolicies());
					register_supplier(supplier_admin, supplier_poa);
					channel.newSupplierAdminTable.put(supplier_admin.name,
							supplier_admin);
					supplier_admin.reference.loadData();
				} catch (Exception ex) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			}
			admins.clear();

			//admins = channel.newConsumerAdminTable.keys();
			admins = new java.util.Vector();
			synchronized (channel.newConsumerAdminTable) {
				for (java.util.Enumeration e = channel.newConsumerAdminTable
						.keys(); e.hasMoreElements();) {
					admins.add((String) e.nextElement());
				}
			}
			channel.newConsumerAdminTable.clear();

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
						consumer_poa = localConsumerPOA(channel.id);
					} else // Create the Exclusive POA
					{
						consumer_poa = exclusiveConsumerPOA(channel.id,
								consumer_id);
					}
					ConsumerAdminData consumer_admin = PersistenceManager
							.getDB().load_ca(consumer_id);
					consumer_admin.poa = consumer_poa;
					consumer_admin.reference = NotifReference
							.internalConsumerAdminReference(consumer_poa,
									consumer_id,
									consumer_admin.qosAdminDelegate.getPolicies());
					channel.newConsumerAdminTable.put(consumer_admin.name,
							consumer_admin);
					register_consumer(consumer_admin, consumer_poa);
					consumer_admin.reference.loadData();
				} catch (Exception ex) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			}
			admins.clear();
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#MyFactory()
	 */
	public EventChannelFactory MyFactory() {
		return this._channelFactory;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#default_consumer_admin()
	 */
	public ConsumerAdmin default_consumer_admin() {
		//NOTE: same code as for_consumers()... exactly the same
		NotificationChannelData channel = getData();
		FOR_CONSUMERS[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, FOR_CONSUMERS);

		ConsumerAdminData consumer_admin;
		if (channel.default_consumer_id == null) {
			String consumer_id;
            try {
                consumer_id = PersistenceManager.getDB().getUID();
            }
            catch (Exception e) {
                throw new INTERNAL(e.toString());
            }
            channel.default_consumer_id = consumer_id;

			org.omg.PortableServer.POA consumer_poa;
			if (_consumer_poa_policy == 0) // Create the Global POA
			{
				consumer_poa = globalConsumerPOA();
			} else if (_consumer_poa_policy == 1) // Create the Local POA
			{
				consumer_poa = localConsumerPOA(channel.id);
			} else // Create the Exclusive POA
			{
				consumer_poa = exclusiveConsumerPOA(channel.id, consumer_id);
			}

			consumer_admin = new ConsumerAdminData(consumer_id, consumer_id,
					channel.id, consumer_poa, channel.reference,
					new AdminCriteria(), channel.default_priority,
					channel.default_event_lifetime, channel.operation_mode,
					this._orb);

			register_consumer(consumer_admin, consumer_poa);

			// lo guardamos en la tabla
			synchronized (channel.consumerAdminTable) {
				channel.consumerAdminTable.put(consumer_id, consumer_admin);
			}
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().save(consumer_admin);
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_CONSUMER_ADMIN_TABLES, channel);
			}
		} else {
			consumer_admin = (ConsumerAdminData) channel.consumerAdminTable
					.get(channel.default_consumer_id);
		}

		return NotifReference.consumerAdminReference(consumer_admin.poa,
				consumer_admin.id, consumer_admin.operation_mode);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#default_supplier_admin()
	 */
	public SupplierAdmin default_supplier_admin() {
		NotificationChannelData channel = getData();
		FOR_SUPPLIERS[1] = channel.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, FOR_SUPPLIERS);

		SupplierAdminData supplier_admin;
		if (channel.default_supplier_id == null) {
			String supplier_id;
            try {
                supplier_id = PersistenceManager.getDB().getUID();
            }
            catch (Exception e) {
                throw new INTERNAL(e.toString());
            }
            channel.default_supplier_id = supplier_id;

			org.omg.PortableServer.POA supplier_poa;
			if (_supplier_poa_policy == 0) // Create the Global POA
			{
				supplier_poa = globalSupplierPOA();
			} else if (_supplier_poa_policy == 1) // Create the Local POA
			{
				supplier_poa = localSupplierPOA(channel.id);
			} else // Create the Exclusive POA
			{
				supplier_poa = exclusiveSupplierPOA(channel.id, supplier_id);
			}

			supplier_admin = new SupplierAdminData(supplier_id, supplier_id,
					channel.id, supplier_poa, channel.reference,
					new AdminCriteria(), channel.operation_mode, this._orb);

			register_supplier(supplier_admin, supplier_poa);

			// lo guardamos en la tabla
			synchronized (channel.supplierAdminTable) {
				channel.supplierAdminTable.put(supplier_id, supplier_admin);
			}
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().save(supplier_admin);
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_SUPPLIER_ADMIN_TABLES, channel);
			}
		} else {
			supplier_admin = (SupplierAdminData) channel.supplierAdminTable
					.get(channel.default_supplier_id);
		}
		return NotifReference.supplierAdminReference(supplier_admin.poa,
				supplier_admin.id, supplier_admin.operation_mode);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#default_filter_factory()
	 */
	public FilterFactory default_filter_factory() {
		return getData().reference;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#new_for_consumers(org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator, org.omg.CORBA.IntHolder)
	 */
	public ConsumerAdmin new_for_consumers(InterFilterGroupOperator op,
			IntHolder id) {
		NVP[] criteria = new NVP[1];
		criteria[ 0 ] = new NVP();
		criteria[ 0 ].name = BaseCriteria.INTERFILTER_GROUP_OPERATOR;
		criteria[ 0 ].value = this._orb.create_any();
		InterFilterGroupOperatorHelper.insert( criteria[ 0 ].value, op ); 
		
		try {
			ConsumerAdmin ca; 
			ca = this.new_for_consumers( criteria );
			id.value = ca.MyID();
			return ca;
		} catch (InvalidCriteria e) {
			throw new INTERNAL();
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#new_for_suppliers(org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator, org.omg.CORBA.IntHolder)
	 */
	public SupplierAdmin new_for_suppliers(InterFilterGroupOperator op,
			IntHolder id) {
		NVP[] criteria = new NVP[1];
		criteria[ 0 ] = new NVP();
		criteria[ 0 ].name = BaseCriteria.INTERFILTER_GROUP_OPERATOR;
		criteria[ 0 ].value = this._orb.create_any();
		InterFilterGroupOperatorHelper.insert( criteria[ 0 ].value, op ); 
		
		try {
			SupplierAdmin sa; 
			sa = this.new_for_suppliers( criteria );
			id.value = sa.MyID();
			return sa;
		} catch (InvalidCriteria e) {
			throw new INTERNAL();
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#get_consumeradmin(int)
	 */
	public ConsumerAdmin get_consumeradmin(int id) throws AdminNotFound {
		NVP[] criteria = new NVP[1];
		criteria[ 0 ] = new NVP();
		criteria[ 0 ].name = BaseCriteria.ID;
		criteria[ 0 ].value = this._orb.create_any();
		criteria[ 0 ].value.insert_string( String.valueOf( id ) );
		try {
			ConsumerAdmin[] ca;
			ca = this.find_for_consumers( criteria );
			switch( ca.length ){
				case 1:
					return ca[0];
				case 0:
					throw new AdminNotFound( "Couldn't find ConsumerAdmin with id: " + id );
				default:
					throw new INTERNAL( "More than one ConsumerAdmin with the same Id?");
			}
		} catch (InvalidCriteria e) {
			throw new INTERNAL();
		} catch (CannotMeetCriteria e) {
			throw new AdminNotFound( "Couldn't find ConsumerAdmin with id: " + id );
		}
	}//get_consumeradmin

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#get_supplieradmin(int)
	 */
	public SupplierAdmin get_supplieradmin(int id) throws AdminNotFound {
		NVP[] criteria = new NVP[1];
		criteria[ 0 ] = new NVP();
		criteria[ 0 ].name = BaseCriteria.ID;
		criteria[ 0 ].value = this._orb.create_any();
		criteria[ 0 ].value.insert_string( String.valueOf( id ) );
		try {
			SupplierAdmin[] sa;
			sa = this.find_for_suppliers( criteria );
			switch( sa.length ){
				case 1:
					return sa[0];
				case 0:
					throw new AdminNotFound( "Couldn't find SupplierAdmin with id: " + id );
				default:
					throw new INTERNAL( "More than one SupplierAdmin with the same Id?");
			}
		} catch (InvalidCriteria e) {
			throw new INTERNAL();
		} catch (CannotMeetCriteria e) {
			throw new AdminNotFound( "Couldn't find SupplierAdmin with id: " + id );
		}
	}//get_supplieradmin

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#get_all_consumeradmins()
	 */
	public int[] get_all_consumeradmins() {
		int[]    intIds;
		String[] stringIds;
		stringIds = this.consumer_admin_ids();
		intIds = new int[ stringIds.length ];
		for ( int i=0; i < stringIds.length; i++ ){
			try {
				intIds[ i ] = Integer.parseInt( stringIds[ i ] );
			} catch ( NumberFormatException nfe ){
				throw new INTERNAL( "Invalid id: not an int: " + stringIds[ i ] );
			}
		}
		return intIds;
	}//get_all_consumeradmins

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelOperations#get_all_supplieradmins()
	 */
	public int[] get_all_supplieradmins() {
		int[]    intIds;
		String[] stringIds;
		stringIds = this.supplier_admin_ids();
		intIds = new int[ stringIds.length ];
		for ( int i=0; i < stringIds.length; i++ ){
			try {
				intIds[ i ] = Integer.parseInt( stringIds[ i ] );
			} catch ( NumberFormatException nfe ){
				throw new INTERNAL( "Invalid id: not an int: " + stringIds[ i ] );
			}
		}
		return intIds;	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.QoSAdminOperations#get_qos()
	 */
	public Property[] get_qos() {
		return getData().qosAdminDelegate.get_qos();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.QoSAdminOperations#set_qos(org.omg.CosNotification.Property[])
	 */
	public void set_qos(Property[] qos) throws UnsupportedQoS {
	    NotificationChannelData data = this.getData();
	    data.qosAdminDelegate.set_qos( qos );
		Policy[] policies = 
		    data.qosAdminDelegate.getPolicies();
                int i = 0; 
		while (i < policies.length) {
                    RequestStartTimePolicy start_policy =
                        RequestStartTimePolicyHelper.narrow(policies[i]);
                    //if (start_policy) {
                        System.out.println("time inside Channel: " + 
                                          start_policy.start_time().time);
                        //}
                    i++;
                }
		data.reference = 
		    InternalDistributionChannelHelper.narrow 
		    (
		    data.reference._set_policy_override(policies,
                                                        SetOverrideType.SET_OVERRIDE
                                                        //SetOverrideType.ADD_OVERRIDE
                                             	)
            );
		
		QueueOrderProperty order_property = 
		    data.qosAdminDelegate.getQueueOrderProperty();
		
		if(order_property != null) {
		    if(this._channel_poa_policy == 0) { //global POA
		        POAManagerImpl manager = 
		            (POAManagerImpl) (this._poa().the_POAManager());		         
		        manager.set_queue_order(order_property.getPolicy(_orb()).allowed_orders());
		    }
		}
		
		
		
		
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.QoSAdminOperations#validate_qos(org.omg.CosNotification.Property[], org.omg.CosNotification.NamedPropertyRangeSeqHolder)
	 */
	public void validate_qos(Property[] required_qos,
			NamedPropertyRangeSeqHolder available_qos) throws UnsupportedQoS {
		getData().qosAdminDelegate.validate_qos( required_qos, available_qos );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.AdminPropertiesAdminOperations#get_admin()
	 */
	public Property[] get_admin() {

            Any max_queue_length_any = this._orb.create_any();
            
            if (this._channel_poa_policy == 0) { //global POA
                POAManagerImpl manager = (POAManagerImpl) (this._poa().the_POAManager());

                max_queue_length_any.insert_long(manager.get_max_queued_requests());
            }

            Property[] initial_admin = new Property[1];
            initial_admin[0] = new Property(MaxQueueLength.value, 
                                            max_queue_length_any);

            return initial_admin;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.AdminPropertiesAdminOperations#set_admin(org.omg.CosNotification.Property[])
	 */
	public void set_admin(Property[] admin) throws UnsupportedAdmin {

            System.out.println("NotificationChannelImpl.set_admin()");
	    NotificationChannelData data = this.getData();
            
            int max_queue_length = -1;
            int max_consumers = -1;
            int max_suppliers = -1;
            
            //
            // Read Admin properties and write it on NotificationChannelData
            //
            for (int i = 0; i < admin.length; i++) {

                if (admin[i].name.compareTo(MaxQueueLength.value) == 0) {
                    max_queue_length = admin[i].value.extract_long();
                } 
                else {
                    if (admin[i].name.compareTo(MaxSuppliers.value) == 0) {
                        max_suppliers = admin[i].value.extract_long();
                    }
                    else {
                        if (admin[i].name.compareTo(MaxConsumers.value) == 0) {
                            max_consumers = admin[i].value.extract_long();
                        }
                    }
                }
            }

            //
            // Set values
            //
            if (this._channel_poa_policy == 0) { //global POA
                POAManagerImpl manager = 
                    (POAManagerImpl) (this._poa().the_POAManager());
                if (max_queue_length != -1) {
                    data.max_queue_length = max_queue_length;
                    manager.set_max_queued_requests(data.max_queue_length);
                }
                if (max_consumers != -1) {
                    data.max_consumers = max_consumers;
                    // TODO
                }
                if (max_suppliers != -1) {
                    data.max_suppliers = max_suppliers;
                    // TODO
                }

            }
	}

	/*
	 * FilterFactory methods
	 */
    /* (non-Javadoc)
     * @see org.omg.CosNotifyFilter.FilterFactoryOperations#create_filter(java.lang.String)
     */
    public Filter create_filter(String constraint_grammar)
        throws InvalidGrammar
    {        
        return getData().filterFactoryDelegate.create_filter(constraint_grammar);
    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyFilter.FilterFactoryOperations#create_mapping_filter(java.lang.String, org.omg.CORBA.Any)
     */
    public MappingFilter create_mapping_filter(String constraint_grammar,
                                               Any default_value)
        throws InvalidGrammar
    {
        return getData().filterFactoryDelegate.create_mapping_filter(constraint_grammar, 
                                                             default_value);
    }
}
