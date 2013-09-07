/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 106 $
* Date: $Date: 2008-11-05 10:43:20 +0100 (Wed, 05 Nov 2008) $
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

import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosEventChannelAdmin.AlreadyConnected;
import org.omg.CosEventChannelAdmin.TypeError;
import org.omg.CosEventComm.PullSupplier;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyChannelAdmin.ConnectionAlreadyActive;
import org.omg.CosNotifyChannelAdmin.ConnectionAlreadyInactive;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.NotConnected;
import org.omg.CosNotifyChannelAdmin.ObtainInfoMode;
import org.omg.CosNotifyChannelAdmin.ProxyType;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.StructuredPullSupplier;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterNotFound;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullConsumerHelper;
import org.omg.ServiceManagement.AdministrativeState;

import es.tid.TIDorbj.core.poa.OID;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.util.parser.TIDParser;

import java.lang.Thread;

public class ProxyPullConsumerImpl extends
		org.omg.DistributionInternalsChannelAdmin.InternalProxyPullConsumerPOA
		implements ProxyPullConsumerMsg {
	private boolean _time_debug;

	private org.omg.CORBA.ORB _orb;

	private org.omg.PortableServer.POA _agentPOA;

	private org.omg.PortableServer.POAManager _poa_manager;

	/* NUEVO */

	private String _channelID;

	private String _supplierID;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// Controla si se utiliza un POA GLOBAL a todos los SupplierAdmin's de todos
	// los CHANNEL's, LOCAL a todos los SupplierAdmin's de un CHANNEL, o
	// EXCLUSIVO para cada SupplierAdmin
	private int _supplier_poa_policy;

	// List of Proxy's 
	private DataTable2 _proxy_pull_consumers_table;

	public ProxyPullConsumerImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agent_poa,
			org.omg.PortableServer.POAManager poa_manager) {
		_orb = orb;
		_agentPOA = agent_poa;
		_poa_manager = poa_manager;
		_channelID = null;
		_supplierID = null;

		_proxy_pull_consumers_table = new DataTable2();

		// Controla como se crean los POA's para los Admin's
		_supplier_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.SUPPLIER_POA_KEY);

		// Algo temporal
		_time_debug = TIDNotifConfig.getBool(TIDNotifConfig.TIME_DEBUG_KEY);
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

	private ProxyPullConsumerData getData() {
		if (_current == null)
			setCurrent();

		try {
			ProxyPullConsumerData data = (ProxyPullConsumerData) _proxy_pull_consumers_table.table
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

	public void register(ProxyPullConsumerData proxy) {
		REGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		_proxy_pull_consumers_table.put(new OID(proxy.id.getBytes()), proxy);
	}

	public void unregister(ProxyPullConsumerData proxy) {
		UNREGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_proxy_pull_consumers_table.remove(new OID(proxy.id.getBytes()));
	}

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
		ProxyPullConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_ADMINISTRATIVE_STATE);
		if (proxy.administrative_state != value) {
			proxy.administrative_state = value;

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_ADMINISTRATIVE_STATE, proxy);
			}
		}
	}

	synchronized public Filter forwarding_discriminator() {
		ProxyPullConsumerData admin = getData();
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

	synchronized public void forwarding_discriminator(Filter value) {
		ProxyPullConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_FORWARDING_DISCRIMINATOR);
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	synchronized public org.omg.CosEventComm.PullSupplier getPullSupplier()
			throws org.omg.CosEventComm.Disconnected {
		ProxyPullConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_PULL_SUPPLIER);
		if (!proxy.connected)
			throw new org.omg.CosEventComm.Disconnected();
		if (!proxy.narrowed) {
			try {
				proxy.pullSupplier = org.omg.CosEventComm.PullSupplierHelper
						.narrow(proxy.pull_supplier_object);
				proxy.narrowed = true;
			} catch (org.omg.CORBA.COMM_FAILURE ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, NARROW_ERROR);
				throw ex;
			}
		}
		return proxy.pullSupplier;
	}

	public void connect_pull_supplier(
			org.omg.CosEventComm.PullSupplier pull_supplier)
			throws org.omg.CosEventChannelAdmin.AlreadyConnected,
			org.omg.CosEventChannelAdmin.TypeError {
		ProxyPullConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, CONNECT_PULL_SUPPLIER);

		if (proxy.connected)
			throw new org.omg.CosEventChannelAdmin.AlreadyConnected();

		proxy.connected = true;
		proxy.pullSupplier = pull_supplier;
		proxy.narrowed = true;

		Thread proxy_thread = new Thread(proxy);
		proxy_thread.start();

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_PULL_SUPPLIER,
					proxy);
		}
	}

	public void disconnect_pull_consumer() {
		ProxyPullConsumerData proxy = getData();
		DISCONNECT_PULL_CONSUMER[1] = proxy.name;
		DISCONNECT_PULL_CONSUMER[3] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DISCONNECT_PULL_CONSUMER);

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			// Discard LOCKED
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		long n = getEventCount(proxy);

		if (proxy.destroying == true)
			return;
		proxy.destroying = true;

		if (n == 0) {
			// Lo elimino de la lista del SupplierAdmin
			proxy.supplierAdmin.removeProxyPullConsumer(proxy.name);

			// y lo desactivo del POA si no quedan enventos
			// por tratar en el POAManager de entrada.
			unregister(proxy);

			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					" *** destroyDiscriminator() ***");
			destroyDiscriminator(proxy);

			// Facilitamos la liberacion de memoria
			proxy.supplierAdmin = null;
			proxy.id = null;
			proxy.reference = null;
		}

		// Liberamos referencias referencia
		proxy.connected = false;
		proxy.pullSupplier = null;
		proxy.narrowed = false;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(proxy);
		}
	}

	synchronized public void destroyFromAdmin() {
		ProxyPullConsumerData proxy = getData();
		DESTROY_FROM_ADMIN[1] = proxy.name;
		DESTROY_FROM_ADMIN[3] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_ADMIN);

		if (proxy.destroying)
			return;
		proxy.destroying = true;

		// Disconnect the client.
		if (proxy.connected) {
			try {
				if (!proxy.narrowed) {
					proxy.pullSupplier = org.omg.CosEventComm.PullSupplierHelper
							.narrow(proxy.pull_supplier_object);
				}
				proxy.pullSupplier.disconnect_pull_supplier();
			} catch (Exception ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						DISCONNECT_PULL_CONSUMER_ERROR);
			}
			proxy.connected = false;
			proxy.pullSupplier = null;
		}

		TIDNotifTrace.print(TIDNotifTrace.DEBUG,
				" $$$ destroyDiscriminator() $$$");
		destroyDiscriminator(proxy);

		NO_PROCESSED_EVENTS[1] = String.valueOf(getEventCount(proxy));
		TIDNotifTrace.print(TIDNotifTrace.USER, NO_PROCESSED_EVENTS);

		// y lo desactivo del POA si no quedan enventos
		// por tratar en el POAManager de entrada.
		unregister(proxy);

		// Facilitamos la liberacion de memoria
		proxy.supplierAdmin = null;
		proxy.id = null;
		proxy.reference = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(proxy);
		}
	}

	public void ipush(org.omg.CORBA.Any event) {
		ProxyPullConsumerData proxy = getData();
		//TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, IPUSH);

		long t1 = 0;
		if (_time_debug) {
			t1 = System.currentTimeMillis();
		}

		if (proxy.filterAdminDelegate != null) {
			try {
				if (!(proxy.filterAdminDelegate.match(event))) {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);

					// Pongo aqui el decEventCount y no al principio de la rutina ya que si
					// se tratara del ultimo evento encolado y hubiera un cambio de thread
					// justo despues de decrementar la cuenta de eventos encolados y entra
					// a ejecutar el thread de un disconnec_push_consumer puedo encontrarme
					// que _forwarding_discriminator es null.
					long n = decEventCount(proxy);

					if ((proxy.destroying == true) && (n == 0)) {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								" ### destroyDiscriminator() ###");
						destroyDiscriminator(proxy);
					}
					return;
				}
			} catch (UnsupportedFilterableData ufd) {
				//TODO: review this
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						" EEE Unsuported filterable data EEE");
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ufd);
				TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
				return;
			}
		}

		// Llamada CORBA sincrona
		proxy.supplierAdmin.push_event(event);

		long t2 = 0;
		if (_time_debug) {
			t2 = System.currentTimeMillis();

			TIME_INFO_2[1] = Long.toString(t2 - t1);
			TIME_INFO_2[3] = Long.toString(t1);
			TIME_INFO_2[5] = Long.toString(t2);
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO_2);
		}

		if (_time_debug) {
			long t3 = t2 - t1;
			if (t3 > proxy.max_proccessing_time)
				proxy.max_proccessing_time = t3;
			proxy.total_proccessing_time = proxy.total_proccessing_time + t3;
		}

		long n = decEventCount(proxy);
		if ((proxy.destroying == true) && (n == 0)) {
			// Lo elimino de la lista del SupplierAdmin
			proxy.supplierAdmin.removeProxyPullConsumer(proxy.name);

			// y lo desactivo del POA si no quedan enventos
			// por tratar en el POAManager de entrada.
			unregister(proxy);

			proxy.supplierAdmin = null;

			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					" &&& destroyDiscriminator() &&&");
			destroyDiscriminator(proxy);

			// Facilitamos la liberacion de memoria
			proxy.supplierAdmin = null;
			proxy.id = null;
			proxy.reference = null;
		}
	}

	/*
	 protected void setInternalReference( org.omg.DistributionInternalsChannelAdmin
	 .InternalProxyPullConsumer reference )
	 {
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, SET_INTERNAL_REFERENCE);
	 reference = reference;
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, reference.toString());
	 TIDNotifTrace.print(TIDNotifTrace.DEBUG, reference.getClass().toString());
	 }
	 */

	/*
	 private org.omg.CosEventComm.PullSupplier getPullReference(String iorString)
	 {
	 org.omg.CORBA.Object ref = _orb.string_to_object(iorString);
	 return org.omg.CosEventComm.PullSupplierHelper.narrow(ref);
	 }
	 */

	synchronized protected long decEventCount(ProxyPullConsumerData proxy) {
		--proxy.eventCount;
		if (_time_debug) {
			proxy.total_proccessed_events++;
		}
		return proxy.eventCount;
	}

	synchronized protected long getEventCount(ProxyPullConsumerData proxy) {
		return proxy.eventCount;
	}

	synchronized protected void destroyDiscriminator(ProxyPullConsumerData proxy) 
	{
		
		// Lo desactivo del POA
		//_supplierAdmin.deactivateInternalProxyPushConsumer( _proxyId );
		proxy.supplierAdmin = null;

		if (_time_debug) {
			if (proxy.total_proccessed_events > 0) {
				TIME_INFO_3[2] = Long.toString(proxy.total_proccessed_events);
				TIME_INFO_3[5] = Double
						.toString((1000.0 * proxy.total_proccessed_events)
								/ proxy.total_proccessing_time);
				TIME_INFO_3[8] = Double.toString(proxy.total_proccessing_time
						/ (1.0 * proxy.total_proccessed_events));
				TIME_INFO_3[11] = Long.toString(proxy.max_proccessing_time);
				TIME_INFO_3[14] = Long.toString(proxy.total_proccessing_time);
			} else {
				TIME_INFO_3[2] = "0";
				TIME_INFO_3[5] = "0";
				TIME_INFO_3[8] = "0";
				TIME_INFO_3[11] = "0";
				TIME_INFO_3[14] = "0";
			}
			TIDNotifTrace.print(TIDNotifTrace.USER, TIME_INFO_3);
		}
	}

	protected void unregister_discriminator(FilterData discriminator) {
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, DEACTIVATE_DISCRIMINATOR);
		try {
			FilterImpl servant = (FilterImpl) (discriminator.poa.get_servant());
			servant.unregister(discriminator);
		} catch (Exception e) {
		}
		/*
		 String dId = DISCRIMINATOR_ID + discriminator._id;

		 // Desactivar el servant
		 try
		 {
		 _proxyPOA.deactivate_object(dId.getBytes());
		 }
		 catch (java.lang.Exception ex)
		 {
		 TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
		 }
		 */
	}
	
	// Persistencia
	public void loadData() {
		ProxyPullConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER,
				"ProxyPullConsumerImpl.loadData()");

	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPullConsumerOperations#connect_any_pull_supplier(org.omg.CosEventComm.PullSupplier)
	 */
	public void connect_any_pull_supplier(PullSupplier pull_supplier)
			throws AlreadyConnected, TypeError {
		this.connect_pull_supplier(pull_supplier);

	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPullConsumerOperations#suspend_connection()
	 */
	public void suspend_connection() throws ConnectionAlreadyInactive,
			NotConnected {
		// TODO: review this
		this.getData().administrative_state = AdministrativeState.locked;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPullConsumerOperations#resume_connection()
	 */
	public void resume_connection() throws ConnectionAlreadyActive,
			NotConnected {
		// TODO: review this
		this.getData().administrative_state = AdministrativeState.unlocked;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyConsumerOperations#MyType()
	 */
	public ProxyType MyType() {
		return ProxyType.from_int(getData().type);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyConsumerOperations#MyAdmin()
	 */
	public SupplierAdmin MyAdmin() {
		return getData().supplierAdmin;
	}
	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ConsumerAdminOperations#MyChannel()
	 */
	public EventChannel MyChannel() {
		return getData().notificationChannel;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyConsumerOperations#obtain_subscription_types(org.omg.CosNotifyChannelAdmin.ObtainInfoMode)
	 */
	public _EventType[] obtain_subscription_types(ObtainInfoMode mode) {
		// TODO Auto-generated method stub
		return new _EventType[0];
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyConsumerOperations#validate_event_qos(org.omg.CosNotification.Property[], org.omg.CosNotification.NamedPropertyRangeSeqHolder)
	 */
	public void validate_event_qos(Property[] required_qos,
			NamedPropertyRangeSeqHolder available_qos) throws UnsupportedQoS {
		//TODO: no implement?
		throw new NO_IMPLEMENT();
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
	    ProxyPullConsumerData data = this.getData();
	    data.qosAdminDelegate.set_qos( qos );
		Policy[] policies = 
		    data.qosAdminDelegate.getPolicies();
		
		data.reference = 
		    InternalProxyPullConsumerHelper.narrow 
		    (
		    data.reference._set_policy_override(policies,
                                             	SetOverrideType.SET_OVERRIDE
                                             	)
            );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotification.QoSAdminOperations#validate_qos(org.omg.CosNotification.Property[], org.omg.CosNotification.NamedPropertyRangeSeqHolder)
	 */
	public void validate_qos(Property[] required_qos,
			NamedPropertyRangeSeqHolder available_qos) throws UnsupportedQoS {
		this.getData().qosAdminDelegate.validate_qos(required_qos,
				available_qos);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#add_filter(org.omg.CosNotifyFilter.Filter)
	 */
	public int add_filter(Filter new_filter) {
		return this.getData().filterAdminDelegate.add_filter(new_filter);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_filter(int)
	 */
	public void remove_filter(int filter) throws FilterNotFound {
		this.getData().filterAdminDelegate.remove_filter(filter);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_filter(int)
	 */
	public Filter get_filter(int filter) throws FilterNotFound {
		return this.getData().filterAdminDelegate.get_filter(filter);
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
	 * @see org.omg.CosNotifyComm.NotifyPublishOperations#offer_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
	 */
	public void offer_change(_EventType[] added, _EventType[] removed)
			throws InvalidEventType {
		// TODO: NO IMPLEMENT
		throw new NO_IMPLEMENT();

	}

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPullConsumerOperations#disconnect_structured_pull_consumer()
     */
    public void disconnect_structured_pull_consumer()
    {
        this.disconnect_pull_consumer();
        
    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyChannelAdmin.StructuredProxyPullConsumerOperations#connect_structured_pull_supplier(org.omg.CosNotifyComm.StructuredPullSupplier)
     */
    public void connect_structured_pull_supplier(StructuredPullSupplier pull_supplier)
        throws AlreadyConnected,
        TypeError
    {
        // TODO Auto-generated method stub
        
    }
}
