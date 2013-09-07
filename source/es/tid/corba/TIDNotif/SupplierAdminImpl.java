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

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyChannelAdmin.AdminLimitExceeded;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator;
import org.omg.CosNotifyChannelAdmin.ProxyConsumer;
import org.omg.CosNotifyChannelAdmin.ProxyNotFound;
import org.omg.CosNotifyChannelAdmin.ProxyType;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterNotFound;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdminHelper;
import org.omg.NotificationChannelAdmin.ProxyAlreadyExist;
import org.omg.ServiceManagement.OperationMode;

import es.tid.TIDorbj.core.poa.OID;
import es.tid.corba.ExceptionHandlerAdmin.CannotProceed;
import es.tid.corba.TIDNotif.util.HashKey;
import es.tid.corba.TIDNotif.util.OIDComparator;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;

public class SupplierAdminImpl extends
		org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdminPOA
		implements SupplierAdminMsg {
	//private static final byte BEGIN_ID = Byte.parseByte("@"); 
	//private static final byte END_ID = Byte.parseByte("#");

	private static final String SEPARATOR = ".";

	private org.omg.CORBA.ORB _orb;

	private org.omg.PortableServer.POA _agentPOA;

	private org.omg.PortableServer.POA _supplierPOA;

	private org.omg.PortableServer.POAManager _poa_manager;

	// Controla si se utiliza un POA GLOBAL a todos los SupplierAdmin's de todos
	// los CHANNEL's, LOCAL a todos los SupplierAdmin's de un CHANNEL, o 
	// EXCLUSIVO para cada SupplierAdmin
	private int _supplier_poa_policy = 0;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// Servant: Default data
	//private SupplierAdminData _data = null;

	// Table of Suppliers
	private SortedMap suppliers;
	
	public SupplierAdminImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agentPOA,
			org.omg.PortableServer.POAManager poa_manager) {
		_orb = orb;
		_agentPOA = agentPOA;
		_poa_manager = poa_manager;

		// Controla como se crean los POA's para los Admin's
		_supplier_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.SUPPLIER_POA_KEY);

		suppliers = Collections.synchronizedSortedMap( new TreeMap(new OIDComparator()) );
		
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

	private SupplierAdminData getData() {
		if (_current == null)
			setCurrent();

		try {
			SupplierAdminData data = (SupplierAdminData) suppliers.get(new OID(_current.get_object_id()));
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

	//public void setData(SupplierAdminData data)
	//{
	//_data = data;
	//}

	public void register(SupplierAdminData admin) {
		REGISTER[1] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		suppliers.put(new OID(admin.id.getBytes()), admin);
	}

	private void unregister(SupplierAdminData admin) {
		UNREGISTER[1] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		suppliers.remove(new OID(admin.id.getBytes()));
	}

	synchronized public org.omg.CosEventChannelAdmin.ProxyPushConsumer 
		obtain_push_consumer() 
	{
		SupplierAdminData admin = getData();
		OBTAIN_PUSH_CONSUMER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PUSH_CONSUMER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		

		try {
//		  Obtenemos el Id (global y unico)
			String proxy_id = PersistenceManager.getDB().getUID();
			return this.obtain_named_push_consumer( proxy_id, 
			                                        proxy_id, 
			                                        ProxyType.PUSH_ANY );
		} catch ( Exception e ){
			throw new INTERNAL(e.toString());
		}
	}

	synchronized public org.omg.NotificationChannelAdmin.ProxyPushConsumer
		obtain_named_push_consumer(String name)
		throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist 
	{
		SupplierAdminData admin = getData();
		OBTAIN_NAMED_PUSH_CONSUMER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PUSH_CONSUMER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPushConsumerTable.containsKey(name)) {
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
        return this.obtain_named_push_consumer( name_id, 
		                                        proxy_id, 
		                                        ProxyType.PUSH_ANY);
	}

	synchronized private org.omg.NotificationChannelAdmin.ProxyPushConsumer 
		obtain_named_push_consumer(String name_id, 
		                           String proxy_id, 
		                           ProxyType type )
			throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist {
		SupplierAdminData admin = getData();
		OBTAIN_NAMED_PUSH_CONSUMER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PUSH_CONSUMER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPushConsumerTable.containsKey(name_id)) {
			throw new org.omg.NotificationChannelAdmin.ProxyAlreadyExist();
		}

		org.omg.PortableServer.POA proxy_poa;
		if (_supplier_poa_policy == 0) // Create the Global POA
		{
			proxy_poa = globalProxyPushConsumerPOA();
		} else if (_supplier_poa_policy == 1) // Create the Local POA
		{
			proxy_poa = localProxyPushConsumerPOA(admin.channel_id);
		} else // Create the Exclusive POA
		{
			proxy_poa = exclusiveProxyPushConsumerPOA(admin.channel_id,
					admin.id);
		}

		ProxyPushConsumerData proxy = 
		    new ProxyPushConsumerData(name_id, 
		                              type.value(),
		                              proxy_id, 
		                              proxy_poa, 
		                              admin.operation_mode, 
		                              admin.id,
		                              admin.channel_id, 
		                              admin.reference, 
		                              _orb);

		register_push_consumer(proxy, proxy_poa);

		// Insert the proxy into the SupplierAdmin's table
		synchronized (admin.proxyPushConsumerTable) {
			admin.proxyPushConsumerTable.put(name_id, proxy);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(proxy);
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_PUSH_CONSUMER_TABLE, admin);
		}
		return NotifReference.nProxyPushConsumerReference(proxy_poa, proxy_id,
				admin.operation_mode);
	}	
	
	
	synchronized public org.omg.CosEventChannelAdmin.ProxyPullConsumer 
		obtain_pull_consumer() 
	{
		SupplierAdminData admin = getData();
		OBTAIN_PULL_CONSUMER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PULL_CONSUMER);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		

		try {
		    //		  Obtenemos el Id (global y unico)
			String proxy_id = PersistenceManager.getDB().getUID();
			
			return this.obtain_named_pull_consumer( proxy_id, 
			                                        proxy_id, 
			                                        ProxyType.PULL_ANY );
		} catch (Exception e) {
		    throw new INTERNAL(e.toString());
		}
	}

	
	synchronized public org.omg.NotificationChannelAdmin.ProxyPullConsumer 
		obtain_named_pull_consumer(String name)
			throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist {
		SupplierAdminData admin = getData();
		OBTAIN_NAMED_PULL_CONSUMER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PULL_CONSUMER);

		// Solo se permite llamar a este metodo en NOTIFICATION mode
		if (admin.operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPullConsumerTable.containsKey(name)) {
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
        return this.obtain_named_pull_consumer( name_id, 
		                                        proxy_id, 
		                                        ProxyType.PULL_ANY);
	}

	synchronized private org.omg.NotificationChannelAdmin.ProxyPullConsumer 
		obtain_named_pull_consumer(
			String name_id, 
			String proxy_id,
			ProxyType type)
			throws org.omg.NotificationChannelAdmin.ProxyAlreadyExist {
		SupplierAdminData admin = getData();
		OBTAIN_NAMED_PULL_CONSUMER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_NAMED_PULL_CONSUMER);

		// Solo se permite llamar a este metodo en NOTIFICATION mode
		if (admin.operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.proxyPullConsumerTable.containsKey( name_id )) {
			throw new org.omg.NotificationChannelAdmin.ProxyAlreadyExist();
		}

		org.omg.PortableServer.POA proxy_poa;
		if (_supplier_poa_policy == 0) // Create the Global POA
		{
			proxy_poa = globalProxyPullConsumerPOA();
		} else if (_supplier_poa_policy == 1) // Create the Local POA
		{
			proxy_poa = localProxyPullConsumerPOA(admin.channel_id);
		} else // Create the Exclusive POA
		{
			proxy_poa = exclusiveProxyPullConsumerPOA(admin.channel_id,
					admin.id);
		}

		ProxyPullConsumerData proxy = 
		    new ProxyPullConsumerData(name_id,
		                              type.value(),
		                              proxy_id, 
		                              proxy_poa, 
		                              admin.id, 
		                              admin.channel_id,
		                              admin.notificationChannel,
		                              admin.reference, 
		                              this._orb);

		register_pull_consumer(proxy, proxy_poa);

		// Insert the proxy into the SupplierAdmin's table
		synchronized (admin.proxyPullConsumerTable) {
			admin.proxyPullConsumerTable.put(name_id, proxy);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(proxy);
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_PULL_CONSUMER_TABLE, admin);
		}
		return NotifReference.nProxyPullConsumerReference(proxy_poa, proxy_id);
	}

	
	public org.omg.NotificationChannelAdmin.ProxyPushConsumer find_push_consumer(
			String name) throws org.omg.NotificationChannelAdmin.ProxyNotFound {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_PUSH_CONSUMER);

		ProxyPushConsumerData proxy = (ProxyPushConsumerData) admin.proxyPushConsumerTable
				.get(name);
		if (proxy == null) {
			throw new org.omg.NotificationChannelAdmin.ProxyNotFound();
		}
		return NotifReference.nProxyPushConsumerReference(proxy.poa, proxy.id,
				proxy.operation_mode);
	}

	public org.omg.NotificationChannelAdmin.ProxyPullConsumer find_pull_consumer(
			String name) throws org.omg.NotificationChannelAdmin.ProxyNotFound {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_PULL_CONSUMER);

		ProxyPullConsumerData proxy = (ProxyPullConsumerData) admin.proxyPullConsumerTable
				.get(name);
		if (proxy == null) {
			throw new org.omg.NotificationChannelAdmin.ProxyNotFound();
		}
		return NotifReference.nProxyPullConsumerReference(proxy.poa, proxy.id);
	}

	//-----------------------------------------------------------------------

	// NUEVAS FUNCIONES DEL ATRIBUTO DISTRIBUTION_ERROR_HANDLER
	//
	public es.tid.corba.ExceptionHandlerAdmin.ExceptionHandler transformation_error_handler() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_EXCEPTION_HANDLER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_ALLOWED);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}
		if (admin.exception_handler == null) {
			throw new org.omg.CORBA.OBJECT_NOT_EXIST(O_N_EXIST);
		}
		return admin.exception_handler;
	}

	synchronized public void transformation_error_handler(
			es.tid.corba.ExceptionHandlerAdmin.ExceptionHandler handler) {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_EXCEPTION_HANDLER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_ALLOWED);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}
		if (!(handler._is_equivalent(admin.exception_handler))) {
			admin.exception_handler = handler;

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_T_ERROR_HANDLER, admin);
			}
		}
	}

	//-----------------------------------------------------------------------

	synchronized public org.omg.DistributionChannelAdmin.SupplierAdmin new_for_suppliers(
			org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.DistributionChannelAdmin.InvalidCriteria {
		SupplierAdminData admin = getData();
		NEW_FOR_SUPPLIERS[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FOR_SUPPLIERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					"NOT ALLOWED IN NOTIFICATION MODE");
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		InternalSupplierAdmin supplierAdmin = null;
		try {
			supplierAdmin = admin.notificationChannel
					.new_transformed_for_suppliers(the_criteria,
							admin.reference, admin.supplierAdminParent,
							admin.name, admin.level);
		} catch (org.omg.NotificationChannelAdmin.InvalidCriteria ic_ex) {
			throw new org.omg.DistributionChannelAdmin.InvalidCriteria(
					the_criteria);
		} catch (org.omg.CORBA.BAD_PARAM bp_ex) {
			throw new org.omg.CORBA.BAD_PARAM(B_P__EXIST_ID);
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		admin.supplierAdminParent = supplierAdmin;
		admin.level++;

		if (admin.supplierAdminChild != null) {
			admin.supplierAdminChild.incLevel();
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_SUPPLIER_ADMIN_TABLES, admin);
		}
		return org.omg.DistributionChannelAdmin.SupplierAdminHelper
				.narrow(supplierAdmin);
	}

	public void setSupplierAdminParent(InternalSupplierAdmin parent) {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_SUPPLIERADMIN_PARENT);
		if (parent == null) {
			TIDNotifTrace.print(TIDNotifTrace.USER, " ***** NULL *****");
			admin.notificationChannel.swapSupplierAdmin(admin.name);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.USER, " ***** NOT NULL *****");
		}
		admin.level--;
		admin.supplierAdminParent = parent;
		if (admin.supplierAdminChild != null) {
			admin.supplierAdminChild.decLevel();
		}
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_SUPPLIER_ADMIN_PARENT, admin);
		}
	}

	public void setSupplierAdminChild(InternalSupplierAdmin child) {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_SUPPLIERADMIN_CHILD);
		admin.supplierAdminChild = child;
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_SUPPLIER_ADMIN_CHILD, admin);
		}
	}

	public org.omg.DistributionChannelAdmin.SupplierAdmin[] find_for_suppliers(
			org.omg.CosLifeCycle.NVP[] the_criteria) throws //org.omg.DistributionChannelAdmin.NotAllowed,
			org.omg.DistributionChannelAdmin.InvalidCriteria,
			org.omg.DistributionChannelAdmin.CannotMeetCriteria {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_FOR_SUPPLIERS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					"NOT ALLOWED IN NOTIFICATION MODE");
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		java.util.Vector ctmp = new java.util.Vector();

		try {
			if (admin.supplierAdminChild != null) {
				if (admin.supplierAdminChild.meetCriteria(the_criteria)) {
					ctmp.add(admin.supplierAdminChild);
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

		org.omg.DistributionChannelAdmin.SupplierAdmin[] suppliers = new org.omg.DistributionChannelAdmin.SupplierAdmin[ctmp
				.size()];

		int i = 0;
		for (java.util.Enumeration e = ctmp.elements(); e.hasMoreElements();) {
			suppliers[i++] = org.omg.DistributionChannelAdmin.SupplierAdminHelper
					.narrow((InternalSupplierAdmin) e.nextElement());
		}
		ctmp.removeAllElements();
		ctmp = null;
		return suppliers;
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
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_OPERATIONAL_STATE);
		return admin.operational_state;
	}

	public org.omg.ServiceManagement.AdministrativeState administrative_state() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_ADMINISTRATIVE_STATE);
		return admin.administrative_state;
	}

	synchronized public void administrative_state(
			org.omg.ServiceManagement.AdministrativeState value) {
		SupplierAdminData admin = getData();
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
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_ASSOCIATED_CRITERIA);
		return admin.associatedCriteria.elements(BaseCriteria.ADMIN_ONLY);
	}

	public org.omg.CosLifeCycle.NVP[] extended_criteria() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_EXTENDED_CRITERIA);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}
		return admin.associatedCriteria
				.elements(BaseCriteria.EXTENDED_SUPPLIER_ONLY);
	}

	public void extended_criteria(org.omg.CosLifeCycle.NVP[] the_criteria) {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_EXTENDED_CRITERIA);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.associatedCriteria
				.updateExtendedSupplierCriteria(the_criteria) < 0) {
			TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR);
			throw new org.omg.CORBA.BAD_PARAM();
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_EXTENDED_CRITERIA, admin);
		}
	}

	synchronized public Filter forwarding_discriminator() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_FORWARDING_DISCRIMINATOR);

		try {
		return admin.filterAdminDelegate.get_filter(0);
		} catch (FilterNotFound fnf)
		{
		   Filter filter =null;
	        try {
	            filter = 
	                MyChannel().default_filter_factory()
	                           .create_filter( TIDParser._CONSTRAINT_GRAMMAR);
	        }
	        catch (InvalidGrammar e) {}
        
        admin.filterAdminDelegate.add_filter(filter);
		
		return filter;
		}
	}

	synchronized public void forwarding_discriminator(
			Filter value) {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_FORWARDING_DISCRIMINATOR);
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	public org.omg.TransformationAdmin.TransformingOperator operator() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_TRANSFORMING_OPERATOR);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.transforming_operator == null) {
			org.omg.PortableServer.POA operator_poa;
			if (_supplier_poa_policy == 0) // Create the Global POA
			{
				operator_poa = globalTransformingOperatorPOA();
			} else if (_supplier_poa_policy == 1) // Create the Local POA
			{
				operator_poa = localTransformingOperatorPOA(admin.channel_id);
			} else // Create the Exclusive POA
			{
				operator_poa = exclusiveTransformingOperatorPOA(
						admin.channel_id, admin.id);
			}

			String id;
            try {
                id = PersistenceManager.getDB().getUID();
            }
            catch (Exception e) {
                throw new INTERNAL(e.toString());
            }
            admin.transforming_operator = new TransformingOperatorData(id,
					operator_poa);

			register_operator(admin.transforming_operator, operator_poa);

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().save(admin.transforming_operator);
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_TRANSFORMING_OPERATOR, admin);
			}
		}
		return NotifReference
				.transformingOperatorReference(admin.transforming_operator.poa,
						admin.transforming_operator.id);
	}

	synchronized public void operator(
			org.omg.TransformationAdmin.TransformingOperator value) {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_TRANSFORMING_OPERATOR);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		throw new org.omg.CORBA.NO_IMPLEMENT();

		/*
		 if (!(value._is_equivalent(admin.transforming_operator)))
		 {
		 if (admin.transforming_operator != null)
		 {
		 try
		 {
		 admin.transforming_operator.destroy();
		 }
		 catch (java.lang.Exception ex) { }
		 }

		 admin.transforming_operator =
		 InternalTransformingOperatorHelper.narrow(value);

		 if (PersistenceManager.isActive())
		 {
		 //PersistenceManager.getDB().save(PersistenceDB.UPDATE, this);
		 }
		 }
		 */
	}

	//-----------------------------------------------------------------------

	public org.omg.ServiceManagement.OperationMode operation_mode() {
		SupplierAdminData admin = getData();
		return admin.operation_mode;
	}

	//-----------------------------------------------------------------------

	// NUEVAS FUNCIONES DEL ATRIBUTO LEVEL
	//
	public int level() {
		return getData().level;
	}

	public void set_level(int new_level) {
		if (new_level > 0) {
			getData().level = new_level;
		}
	}

	public void incLevel() {
		SupplierAdminData admin = getData();
		admin.level++;
		if (admin.supplierAdminChild != null) {
			admin.supplierAdminChild.incLevel();
		}
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_LEVEL, admin);
		}
	}

	public void decLevel() {
		SupplierAdminData admin = getData();
		admin.level--;
		if (admin.supplierAdminChild != null) {
			admin.supplierAdminChild.decLevel();
		}
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_LEVEL, admin);
		}
	}

	//-----------------------------------------------------------------------

	synchronized public org.omg.DistributionChannelAdmin.SupplierAdmin[] supplier_admins() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SUPPLIER_ADMINS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		org.omg.DistributionChannelAdmin.SupplierAdmin[] suppliers = null;

		if (admin.supplierAdminChild != null) {
			suppliers = new org.omg.DistributionChannelAdmin.SupplierAdmin[1];
			String admin_id = admin.supplierAdminChild.getAdminId();

			TRY_SUPPLIERADMIN[1] = admin_id;
			TRY_SUPPLIERADMIN[3] = admin.supplierAdminChild.toString();
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TRY_SUPPLIERADMIN);

			suppliers[0] = org.omg.DistributionChannelAdmin.SupplierAdminHelper
					.narrow(admin.supplierAdminChild);
		} else {
			suppliers = new org.omg.DistributionChannelAdmin.SupplierAdmin[0];
		}
		return suppliers;
	}

	public String[] supplier_admin_ids() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SUPPLIER_ADMIN_IDS);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		String[] suppliers = null;

		if (admin.supplierAdminChild != null) {
			suppliers = new String[1];
			suppliers[0] = admin.supplierAdminChild.getAdminId();
		} else {
			suppliers = new String[0];
		}
		return suppliers;
	}

	//-----------------------------------------------------------------------

	public org.omg.NotificationChannelAdmin.ProxyPullConsumer[] obtain_pull_consumers() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PULL_CONSUMERS);

		java.util.Vector pstmp = new java.util.Vector();

		synchronized (admin.proxyPullConsumerTable) {
			for (java.util.Iterator e = admin.proxyPullConsumerTable.values()
					.iterator(); e.hasNext();) {
				ProxyPullConsumerData proxy = (ProxyPullConsumerData) e.next();
				pstmp.add(NotifReference.nProxyPullConsumerReference(proxy.poa,
						proxy.id));
			}
		}
		return (org.omg.NotificationChannelAdmin.ProxyPullConsumer[]) pstmp
				.toArray(new org.omg.NotificationChannelAdmin.ProxyPullConsumer[0]);

		/*
		 org.omg.NotificationChannelAdmin.ProxyPullConsumer[] proxies =
		 new org.omg.NotificationChannelAdmin.ProxyPullConsumer[pstmp.size()];
		 int i = 0;
		 for ( java.util.Enumeration e = pstmp.elements(); e.hasMoreElements(); )
		 {
		 proxies[i++] = 
		 (org.omg.NotificationChannelAdmin.ProxyPullConsumer) e.nextElement();
		 }
		 pstmp.removeAllElements();
		 pstmp = null;
		 return proxies;
		 */
	}

	public org.omg.NotificationChannelAdmin.ProxyPushConsumer[] obtain_push_consumers() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PUSH_CONSUMERS);

		java.util.Vector pctmp = new java.util.Vector();

		synchronized (admin.proxyPushConsumerTable) {
			for (java.util.Iterator e = admin.proxyPushConsumerTable.values()
					.iterator(); e.hasNext();) {
				ProxyPushConsumerData proxy = (ProxyPushConsumerData) e.next();
				pctmp.add(NotifReference.nProxyPushConsumerReference(proxy.poa,
						proxy.id, proxy.operation_mode));
			}
		}
		return (org.omg.NotificationChannelAdmin.ProxyPushConsumer[]) pctmp
				.toArray(new org.omg.NotificationChannelAdmin.ProxyPushConsumer[0]);
		/*
		 org.omg.NotificationChannelAdmin.ProxyPushConsumer[] proxies =
		 new org.omg.NotificationChannelAdmin.ProxyPushConsumer[pctmp.size()];
		 int i = 0;
		 for ( java.util.Enumeration e = pctmp.elements(); e.hasMoreElements(); )
		 {
		 proxies[i++] = 
		 (org.omg.NotificationChannelAdmin.ProxyPushConsumer) e.nextElement();
		 }
		 pctmp.removeAllElements();
		 pctmp = null;
		 return proxies;
		 */
	}

	public String[] push_consumer_ids() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PUSH_CONSUMER_IDS);

		//java.util.Vector pctmp = admin.proxyPushConsumerTable.keys();
		//return (String[])pctmp.toArray(new String[0]);
		return (String[]) admin.proxyPushConsumerTable.keySet().toArray(
				new String[0]);
	}

	public String[] pull_consumer_ids() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, PULL_CONSUMER_IDS);

		//java.util.Vector pctmp = admin.proxyPullConsumerTable.keys();
		//return (String[])pctmp.toArray(new String[0]);
		return (String[]) admin.proxyPullConsumerTable.keySet().toArray(
				new String[0]);
	}

	synchronized public void remove() {
		SupplierAdminData admin = getData();
		REMOVE[1] = admin.name;
		REMOVE[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (admin.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.destroying)
			return;
		admin.destroying = true;

		basicDestroy(admin, true);

		if (admin.level == 0) {
			admin.notificationChannel.removeSupplierAdmin(admin.name);
			if (admin.supplierAdminChild != null) {
				admin.supplierAdminChild.setSupplierAdminParent(null);
				admin.supplierAdminChild = null;
				admin.level++;
			}
		} else {
			admin.notificationChannel.removeNewSupplierAdmin(admin.name);
			if (admin.supplierAdminParent != null) {
				admin.supplierAdminParent
						.setSupplierAdminChild(admin.supplierAdminChild);
			} else {
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						"    ***** ERROR: _supplierAdminParent is NULL ***** ");
			}
			if (admin.supplierAdminChild != null) {
				admin.supplierAdminChild
						.setSupplierAdminParent(admin.supplierAdminParent);
			}
			admin.supplierAdminParent = null;
			admin.supplierAdminChild = null;
		}
		admin.associatedCriteria = null;
		admin.reference.destroySupplierAdmin();
		admin.notificationChannel = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(admin);
		}
	}

	synchronized public void destroy() {
		SupplierAdminData admin = getData();
		DESTROY[1] = admin.name;
		DESTROY[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (admin.destroying)
			return;
		admin.destroying = true;

		basicDestroy(admin, false);

		if (admin.level == 0) {
			admin.notificationChannel.removeSupplierAdmin(admin.name);
			admin.supplierAdminChild = null;
		} else {
			admin.notificationChannel.removeNewSupplierAdmin(admin.name);
			if (admin.supplierAdminParent != null) {
				admin.supplierAdminParent.setSupplierAdminChild(null);
			} else {
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						"    ***** ERROR: _supplierAdminParent is NULL ***** ");
			}
			admin.supplierAdminParent = null;
			admin.supplierAdminChild = null;
		}
		admin.associatedCriteria = null;
		admin.reference.destroySupplierAdmin();
		admin.notificationChannel = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(admin);
		}
	}

	public void destroyFromChannel() {
		SupplierAdminData admin = getData();
		DESTROY_FROM_CHANNEL[1] = admin.name;
		DESTROY_FROM_CHANNEL[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_CHANNEL);

		if (admin.destroying)
			return;
		admin.destroying = true;

		basicDestroy(admin, false);

		admin.associatedCriteria = null;
		admin.reference.destroySupplierAdmin();
		admin.notificationChannel = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(admin);
		}
	}

	public void destroyFromAdmin() {
		SupplierAdminData admin = getData();
		DESTROY_FROM_ADMIN[1] = admin.name;
		DESTROY_FROM_ADMIN[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_ADMIN);

		if (admin.destroying)
			return;
		admin.destroying = true;

		basicDestroy(admin, false);

		admin.notificationChannel.removeNewSupplierAdmin(admin.name);

		admin.associatedCriteria = null;
		admin.reference.destroySupplierAdmin();
		admin.notificationChannel = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(admin);
		}
	}

	synchronized private void basicDestroy(SupplierAdminData admin,
			boolean remove) {
		BASIC_DESTROY[1] = admin.name;
		BASIC_DESTROY[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, BASIC_DESTROY);

		for (java.util.Iterator e = admin.proxyPushConsumerTable.values()
				.iterator(); e.hasNext();) {
			((ProxyPushConsumerData) e.next()).reference.destroyFromAdmin();
		}
		admin.proxyPushConsumerTable.clear();

		for (java.util.Iterator e = admin.proxyPullConsumerTable.values()
				.iterator(); e.hasNext();) {
			((ProxyPullConsumerData) e.next()).reference.destroyFromAdmin();
		}
		admin.proxyPullConsumerTable.clear();

		if (!remove) {
			// Destroye the SupplierAdmin child.
			if (admin.supplierAdminChild != null) {
				try {
					admin.supplierAdminChild.destroyFromAdmin();
				} catch (java.lang.Exception ex) {
					TIDNotifTrace.print(TIDNotifTrace.USER,
							DESTROY_FROM_ADMIN_ERROR);
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
				admin.supplierAdminChild = null;
			}
		}

		// Destroyes the transformator.
		if (admin.transforming_operator != null) {
			admin.transforming_operator.reference.destroy();
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().delete(admin.transforming_operator);
			}
			admin.transforming_operator = null;
		}
	}

	public boolean meetCriteria(org.omg.CosLifeCycle.NVP[] the_criteria)
			throws org.omg.NotificationChannelAdmin.InvalidCriteria {
		SupplierAdminData admin = getData();
		if (admin.operation_mode == OperationMode.distribution) {
			if (BaseCriteria.checkInvalidCriteria( BaseCriteria.EXTENDED_SUPPLIER, the_criteria) ) {
				throw new org.omg.NotificationChannelAdmin.InvalidCriteria(
						the_criteria);
			}
		} else {
			if (BaseCriteria.checkInvalidCriteria(
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

	public void push_event(org.omg.CORBA.Any event) {
		SupplierAdminData admin = getData();
		//TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, PUSH_EVENT);

		if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			// Discard LOCKED
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			return;
		}

		if(admin.filterAdminDelegate != null){
			try {
				if (!admin.filterAdminDelegate.match(event)) {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
					return;
				}
			} catch ( UnsupportedFilterableData ufd ){
	    		  //TODO: review this
		          TIDNotifTrace.print(TIDNotifTrace.ERROR, " EEE Unsuported filterable data EEE" );
		          TIDNotifTrace.printStackTrace( TIDNotifTrace.ERROR, ufd );
		          TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
		          return;
			}
		}
		

		// Notification => 0
		// Distribution => 1
		if (admin.operation_mode == OperationMode.notification) {
			if (admin.level > 0) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						DISPATCH_EVENT_LEVEL_ERROR);
			}
			admin.notificationChannel.dispatch_event(event);
			return;
		} else if (admin.operation_mode == OperationMode.distribution) {
			if (admin.transforming_operator != null) {
				event = transform_event(event, admin);

				if (event == null) {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
					TIDNotifTrace
							.print(TIDNotifTrace.ERROR,
									"SupplierAdminImpl.push_event(): transform_event return null");
					return;
				}
			}

			if (admin.supplierAdminParent == null) {
				if (admin.level > 0) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR,
							DISPATCH_EVENT_LEVEL_ERROR);
				}
				admin.notificationChannel.dispatch_event(event);
			} else {
				if (admin.level == 0) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR,
							PUSH_EVENT_LEVEL_ERROR);
				}
				admin.supplierAdminParent.push_event(event);
			}
			return;
		}
		TIDNotifTrace.print(TIDNotifTrace.ERROR, INVALID_MODE);
		return;
	}

	public org.omg.CORBA.Any transform_event(org.omg.CORBA.Any event,
			SupplierAdminData admin) {
		try {
			return admin.transforming_operator.reference.transform_value(event);
		} catch (org.omg.TransformationInternalsAdmin.DataError ex1) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex1);
			handle_error(admin, ex1.why, event, new CannotProceed("DataError"));
		} catch (org.omg.TransformationInternalsAdmin.TransformationError ex2) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex2);
			handle_error(admin, ex2.why, event, new CannotProceed(
					"TransformationError"));
		} catch (org.omg.TransformationInternalsAdmin.ConnectionError ex3) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex3);
			handle_error(admin, "", event, new CannotProceed("ConnectionError"));
		} catch (java.lang.Exception ex4) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex4);
			handle_error(admin, "", event, new CannotProceed("Error"));
		}
		return null;
	}

	//-----------------------------------------------------------

	private void handle_error(SupplierAdminData admin, String id,
			org.omg.CORBA.Any event, CannotProceed reason) {
		if (admin.exception_handler != null) {
			admin.exception_handler.handle_exception(id, event, reason);
		}
	}

	//-----------------------------------------------------------

	private String proxyPushConsumerId(SupplierAdminData admin, String id) {
		return (PROXYPUSHCONSUMER_ID + admin.channel_id + SEPARATOR + admin.id
				+ SEPARATOR + id);
	}

	String proxyPullConsumerId(SupplierAdminData admin, String id) {
		return (PROXYPULLCONSUMER_ID + admin.channel_id + SEPARATOR + admin.id
				+ SEPARATOR + id);
	}

	synchronized public String removeProxyPushConsumer(String cId) {
		SupplierAdminData admin = getData();
		REMOVE_PROXYPUSHCONSUMER[1] = cId;
		REMOVE_PROXYPUSHCONSUMER[3] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_PROXYPUSHCONSUMER);

		if (admin.destroying == true)
			return proxyPushConsumerId(admin, cId);

		// Lo eliminamos de la tabla
		admin.proxyPushConsumerTable.remove(cId);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_PROXY_PUSH_CONSUMER, admin);
		}
		return proxyPushConsumerId(admin, cId);
	}

	synchronized public String removeProxyPullConsumer(String cId) {
		SupplierAdminData admin = getData();
		REMOVE_PROXYPULLCONSUMER[1] = cId;
		REMOVE_PROXYPULLCONSUMER[3] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_PROXYPULLCONSUMER);

		if (admin.destroying == true)
			return proxyPullConsumerId(admin, cId);

		// Lo eliminamos de la tabla
		admin.proxyPullConsumerTable.remove(cId);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_PROXY_PULL_CONSUMER, admin);
		}
		return proxyPullConsumerId(admin, cId);
	}

	public String getAdminId() {
		SupplierAdminData admin = getData();
		return admin.name;
	}

	/*
	 private void debugPushConsumer( String id, org.omg.PortableServer.POA poa)
	 {
	 // NOTIFICATION
	 NEW_PROXYPUSHCONSUMER[1] = id;
	 TIDNotifTrace.print(TIDNotifTrace.USER, NEW_PROXYPUSHCONSUMER);
	 POA_INFO[1] = poa.toString();
	 TIDNotifTrace.print(TIDNotifTrace.USER, POA_INFO);
	 }

	 private void debugPullConsumer( String id, org.omg.PortableServer.POA poa)
	 {
	 // NOTIFICATION
	 NEW_PROXYPULLCONSUMER[1] = id;
	 TIDNotifTrace.print(TIDNotifTrace.USER, NEW_PROXYPULLCONSUMER);
	 POA_INFO[1] = poa.toString();
	 TIDNotifTrace.print(TIDNotifTrace.USER, POA_INFO);
	 }
	 */

	private org.omg.PortableServer.POA globalProxyPushConsumerPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_PROXYPUSHCONSUMER_POA_ID);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_PROXYPUSHCONSUMER_POA_ID,
					ThePOAFactory.PROXY_PUSH_CONSUMER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPushConsumerImpl(_orb,
							_agentPOA, getPOAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA localProxyPushConsumerPOA(
			String channel_id) {
		// Creation de Supplier POA comun
		String poa_id = LOCAL_PROXYPUSHCONSUMER_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.PROXY_PUSH_CONSUMER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPushConsumerImpl(_orb,
							_agentPOA, getPOAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA exclusiveProxyPushConsumerPOA(
			String channel_id, String supplier_id) {
		// Creation de Supplier POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(supplier_id);

		if (id_name.compareTo("") == 0) {
			poa_id = PROXYPUSHCONSUMER_POA_ID + supplier_id;
		} else {
			poa_id = PROXYPUSHCONSUMER_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.PROXY_PUSH_CONSUMER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPushConsumerImpl(_orb,
							_agentPOA, getPOAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA globalProxyPullConsumerPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_PROXYPULLCONSUMER_POA_ID);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_PROXYPULLCONSUMER_POA_ID,
					ThePOAFactory.PROXY_PUSH_CONSUMER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPullConsumerImpl(_orb,
							_agentPOA, getPOAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA localProxyPullConsumerPOA(
			String channel_id) {
		// Creation de Supplier POA comun
		String poa_id = LOCAL_PROXYPULLCONSUMER_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.PROXY_PUSH_CONSUMER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPullConsumerImpl(_orb,
							_agentPOA, getPOAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA exclusiveProxyPullConsumerPOA(
			String channel_id, String supplier_id) {
		// Creation de Supplier POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(supplier_id);

		if (id_name.compareTo("") == 0) {
			poa_id = PROXYPULLCONSUMER_POA_ID + supplier_id;
		} else {
			poa_id = PROXYPULLCONSUMER_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.PROXY_PUSH_CONSUMER_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new ProxyPullConsumerImpl(_orb,
							_agentPOA, getPOAManager()));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA globalTransformingOperatorPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_TRANSFORMING_OPERATOR_POA_ID);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_TRANSFORMING_OPERATOR_POA_ID,
					ThePOAFactory.TRANSFORMING_OPERATOR_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new TransformingOperatorImpl(_orb,
							_agentPOA));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA localTransformingOperatorPOA(
			String channel_id) {
		// Creation de Supplier POA comun
		String poa_id = LOCAL_TRANSFORMING_OPERATOR_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.TRANSFORMING_OPERATOR_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new TransformingOperatorImpl(_orb,
							_agentPOA));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA exclusiveTransformingOperatorPOA(
			String channel_id, String supplier_id) {
		// Creation de Supplier POA comun
		String poa_id;
		String id_name = ThePOAFactory.getIdName(supplier_id);

		if (id_name.compareTo("") == 0) {
			poa_id = TRANSFORMING_OPERATOR_POA_ID + supplier_id;
		} else {
			poa_id = TRANSFORMING_OPERATOR_POA_ID + channel_id + "." + id_name;
		}

		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getExclusivePOA(poa_id);

		if (the_poa == null) {
			the_poa = ThePOAFactory.createExclusivePOA(poa_id,
					ThePOAFactory.TRANSFORMING_OPERATOR_POA, getPOAManager());
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new TransformingOperatorImpl(_orb,
							_agentPOA));
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	public void destroySupplierAdmin() {
		SupplierAdminData admin = getData();
		DESTROY_SUPPLIER_ADMIN[1] = admin.name;
		DESTROY_SUPPLIER_ADMIN[3] = admin.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_SUPPLIER_ADMIN);

		unregister(admin);
		destroySupplierPOA(admin);
	}

	public void destroySupplierPOA(SupplierAdminData admin) {
		if (_supplier_poa_policy == TIDNotifConfig.EXCLUSIVE_POA) {
			// Solo el root SupplierAdmin puede destruir el POA, los SupplierAdmin
			// anidados no.
			if (admin.level == 0) {
				TIDNotifTrace.print(TIDNotifTrace.DEBUG, DESTROY_SUPPLIER_POA);
				String poa_id;
				String id_name = ThePOAFactory.getIdName(admin.id);

				if (id_name.compareTo("") == 0) {
					poa_id = SUPPLIER_POA_ID + admin.id;
				} else {
					poa_id = SUPPLIER_POA_ID + admin.channel_id + "." + id_name;
				}
				ThePOAFactory.destroyExclusivePOA(poa_id);
			}
		}
	}

	private void register_push_consumer(ProxyPushConsumerData proxy,
			org.omg.PortableServer.POA poa) {
		try {
			ProxyPushConsumerImpl servant = (ProxyPushConsumerImpl) poa
					.get_servant();
			servant.register(proxy);
		} catch (Exception e) {
		}
	}

	protected void unregister_push_consumer(ProxyPushConsumerData proxy) {
		try {
			ProxyPushConsumerImpl servant = (ProxyPushConsumerImpl) (proxy.poa
					.get_servant());
			servant.unregister(proxy);
		} catch (Exception e) {
		}
	}

	private void register_pull_consumer(ProxyPullConsumerData proxy,
			org.omg.PortableServer.POA poa) {
		try {
			ProxyPullConsumerImpl servant = (ProxyPullConsumerImpl) poa
					.get_servant();
			servant.register(proxy);
		} catch (Exception e) {
		}
	}

	protected void unregister_pull_consumer(ProxyPullConsumerData proxy) {
		try {
			ProxyPullConsumerImpl servant = (ProxyPullConsumerImpl) (proxy.poa
					.get_servant());
			servant.unregister(proxy);
		} catch (Exception e) {
		}
	}

	private void register_discriminator(FilterData data,
			org.omg.PortableServer.POA poa) {
		try {
			FilterImpl servant = (FilterImpl) poa.get_servant();
			servant.register(data);
		} catch (Exception e) {
		}
	}

	protected void unregister_discriminator(FilterData discriminator) {
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, DEACTIVATE_DISCRIMINATOR);
		try {
			FilterImpl servant = (FilterImpl) (discriminator.poa
					.get_servant());
			servant.unregister(discriminator);
		} catch (Exception e) {
		}
	}

	private void register_operator(TransformingOperatorData data,
			org.omg.PortableServer.POA poa) {
		try {
			TransformingOperatorImpl servant = (TransformingOperatorImpl) poa
					.get_servant();
			servant.register(data);
		} catch (Exception e) {
		}
	}

	// Persistencia
	public void loadData() {
		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, "SupplierAdminImpl.loadData()");

		
		if (admin.operator_ir != null) {
			org.omg.PortableServer.POA operator_poa;
			if (_supplier_poa_policy == 0) // Create the Global POA
			{
				operator_poa = globalTransformingOperatorPOA();
			} else if (_supplier_poa_policy == 1) // Create the Local POA
			{
				operator_poa = localTransformingOperatorPOA(admin.channel_id);
			} else // Create the Exclusive POA
			{
				operator_poa = exclusiveTransformingOperatorPOA(
						admin.channel_id, admin.id);
			}

			try {
				admin.transforming_operator = PersistenceManager.getDB()
						.load_to(admin.operator_ir);

				admin.transforming_operator.poa = operator_poa;
				admin.transforming_operator.reference = NotifReference
						.iTransformingOperatorReference(operator_poa,
								admin.transforming_operator.id);
				register_operator(admin.transforming_operator, operator_poa);
				admin.operator_ir = null;
			} catch (Exception ex) {
				admin.transforming_operator = null;
				admin.transforming_operator.reference = null;
			}
		}

		//java.util.Vector proxys = admin.proxyPushConsumerTable.keys();
		java.util.Vector proxys = new java.util.Vector();
		synchronized (admin.proxyPushConsumerTable) {
			proxys.addAll( admin.proxyPushConsumerTable.keySet() );
		}
		admin.proxyPushConsumerTable.clear();

		for (java.util.Enumeration e = proxys.elements(); e.hasMoreElements();) {
			try {
				String proxy_id = (String) e.nextElement();
				org.omg.PortableServer.POA proxy_poa;
				if (_supplier_poa_policy == 0) // Create the Global POA
				{
					proxy_poa = globalProxyPushConsumerPOA();
				} else if (_supplier_poa_policy == 1) // Create the Local POA
				{
					proxy_poa = localProxyPushConsumerPOA(admin.channel_id);
				} else // Create the Exclusive POA
				{
					proxy_poa = exclusiveProxyPushConsumerPOA(admin.channel_id,
							admin.id);
				}

				ProxyPushConsumerData proxy = PersistenceManager.getDB()
						.load_ppushc(proxy_id);
				proxy.poa = proxy_poa;
				proxy.reference = NotifReference.iProxyPushConsumerReference(
						proxy_poa, proxy_id, proxy.qosAdminDelegate.getPolicies());

				register_push_consumer(proxy, proxy_poa);

				admin.proxyPushConsumerTable.put(proxy.name, proxy);

				proxy.reference.loadData();
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		proxys.clear();

		//proxys = admin.proxyPullConsumerTable.keys();
		proxys = new java.util.Vector();
		synchronized (admin.proxyPullConsumerTable) {
			proxys.addAll( admin.proxyPullConsumerTable.keySet() );
		}
		admin.proxyPullConsumerTable.clear();

		for (java.util.Enumeration e = proxys.elements(); e.hasMoreElements();) {
			try {
				String proxy_id = (String) e.nextElement();

				org.omg.PortableServer.POA proxy_poa;
				if (_supplier_poa_policy == 0) // Create the Global POA
				{
					proxy_poa = globalProxyPullConsumerPOA();
				} else if (_supplier_poa_policy == 1) // Create the Local POA
				{
					proxy_poa = localProxyPullConsumerPOA(admin.channel_id);
				} else // Create the Exclusive POA
				{
					proxy_poa = exclusiveProxyPullConsumerPOA(admin.channel_id,
							admin.id);
				}
				ProxyPullConsumerData proxy = PersistenceManager.getDB()
						.load_ppullc(proxy_id);
				proxy.poa = proxy_poa;
				proxy.reference = NotifReference.iProxyPullConsumerReference(
						proxy_poa, proxy_id, proxy.qosAdminDelegate.getPolicies());

				register_pull_consumer(proxy, proxy_poa);

				admin.proxyPullConsumerTable.put(proxy.name, proxy);

				proxy.reference.loadData();
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		proxys.clear();
	}

	
	/**************************************************************************
	 * METODO DE CosNotifyChannelAdmin::SupplierAdmin...
	 *************************************************************************/
	
	
	/**
	  * METODOS DE CosNotifyFilter::FilterAdmin...
	  */
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
		this.getData().filterAdminDelegate.remove_filter( filter );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_filter(int)
	 */
	public Filter get_filter(int filter) throws FilterNotFound {
		return this.getData().filterAdminDelegate.get_filter( filter );
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

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#MyID()
	 */
	public int MyID() {
		return this.getData().id.hashCode();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#MyChannel()
	 */
	public EventChannel MyChannel() {
		return this.getData().notificationChannel;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#MyOperator()
	 */
	public InterFilterGroupOperator MyOperator() {
		return this.getData().associatedCriteria.interfilter_group_operator();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#get_proxy_consumer(int)
	 */
	public ProxyConsumer get_proxy_consumer(int proxy_id) throws ProxyNotFound {

		SupplierAdminData admin = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, FIND_PULL_CONSUMER);

		HashKey key = new HashKey( String.valueOf( proxy_id ) );
		
		ProxyPullConsumerData pullProxy;
		pullProxy = (ProxyPullConsumerData) admin.proxyPullConsumerTable.get( 
			key 
		);

		ProxyPushConsumerData pushProxy;
		pushProxy = (ProxyPushConsumerData) admin.proxyPushConsumerTable.get( 
			key 
		);

		if ( pullProxy != null ) {
			return NotifReference.nProxyPullConsumerReference(
				pullProxy.poa, pullProxy.id
			);	
		} else if (pushProxy != null ){
			return NotifReference.nProxyPushConsumerReference(
				pushProxy.poa, pushProxy.id, getData().operation_mode
			);
		} else {
			throw new ProxyNotFound();
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#obtain_notification_pull_consumer(org.omg.CosNotifyChannelAdmin.ClientType, org.omg.CORBA.IntHolder)
	 */
	public ProxyConsumer obtain_notification_pull_consumer(ClientType ctype, 
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
	    
					SupplierAdminData admin = getData();
			OBTAIN_PULL_CONSUMER[1] = admin.name;
			TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PUSH_CONSUMER);

			if (admin.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
				throw new org.omg.CORBA.NO_PERMISSION();
			}

			

			try {
//			  	Obtenemos el Id (global y unico)
				String id = PersistenceManager.getDB().getUID();
				proxy_id.value = Integer.parseInt( id );
				return this.obtain_named_pull_consumer( id, id, ptype );
			} catch (Exception e) {
			    throw new INTERNAL(e.toString());
			} 
		
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#obtain_notification_push_consumer(org.omg.CosNotifyChannelAdmin.ClientType, org.omg.CORBA.IntHolder)
	 */
	public ProxyConsumer obtain_notification_push_consumer(ClientType ctype,
	                                                       IntHolder proxy_id) 
	throws AdminLimitExceeded {
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
	        
	   
		SupplierAdminData admin = getData();
		OBTAIN_PUSH_CONSUMER[1] = admin.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, OBTAIN_PUSH_CONSUMER);

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
			return this.obtain_named_push_consumer( id, id, ptype );
		} catch (ProxyAlreadyExist e) {
			throw new INTERNAL();
		} catch ( NumberFormatException nfe ){
			throw new INTERNAL();
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
	    SupplierAdminData data = this.getData();
	    data.qosAdminDelegate.set_qos( qos );
		Policy[] policies = 
		    data.qosAdminDelegate.getPolicies();
		
		data.reference = 
		    InternalSupplierAdminHelper.narrow 
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
	 * @see org.omg.CosNotifyComm.NotifyPublishOperations#offer_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
	 */
	public void offer_change(_EventType[] added, _EventType[] removed) throws InvalidEventType {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.omg.NotificationChannelAdmin.SupplierAdminOperations#pull_consumers()
	 */
	public int[] pull_consumers() {
		String[] stringIds = this.pull_consumer_ids();
		int[] intIds = new int[ stringIds.length ];
		for ( int i=0; i < intIds.length; i++ ){
			intIds[ i ] = Integer.parseInt( stringIds[ i ] );
		}
		return intIds;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#push_consumers()
	 */
	public int[] push_consumers() {
		String[] stringIds = this.push_consumer_ids();
		int[] intIds = new int[ stringIds.length ];
		for ( int i=0; i < intIds.length; i++ ){
			intIds[ i ] = Integer.parseInt( stringIds[ i ] );
		}
		return intIds;
	}

}