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
* (C) Copyright 2008 Telefonica Investigacion y Desarrollo
*     S.A.Unipersonal (Telefonica I+D)
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
import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosEventChannelAdmin.AlreadyConnected;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosEventComm.PullConsumer;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.FifoOrder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotification.StructuredEventHelper;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.ObtainInfoMode;
import org.omg.CosNotifyChannelAdmin.ProxyType;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.StructuredPullConsumer;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterNotFound;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.MappingFilter;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullSupplierHelper;

import es.tid.TIDorbj.core.poa.OID;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;

public class ProxyPullSupplierImpl extends
		org.omg.DistributionInternalsChannelAdmin.InternalProxyPullSupplierPOA
		implements ProxyPullSupplierMsg {
    
    public final static StructuredEvent NULL_ST_EVENT = 
        initNullStructuredEvent();
    
	private org.omg.CORBA.ORB _orb;

	private org.omg.PortableServer.POA _agentPOA;

	private org.omg.PortableServer.POAManager _poa_manager;

	private String _channelID;

	private String _supplierID;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// Controla si se utiliza un POA GLOBAL a todos los ConsumerAdmin's de todos
	// los CHANNEL's, LOCAL a todos los ConsumerAdmin's de un CHANNEL, o
	// EXCLUSIVO para cada ConsumerAdmin
	private int _consumer_poa_policy;

	// List of Proxy's 
	private DataTable2 _proxy_pull_suppliers_table;

	public ProxyPullSupplierImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agent_poa,
			org.omg.PortableServer.POAManager poa_manager, String channel_id,
			String supplier_id) {
		_orb = orb;
		_agentPOA = agent_poa;
		_poa_manager = poa_manager;
		_channelID = channel_id;
		_supplierID = supplier_id;

		_proxy_pull_suppliers_table = new DataTable2();

		// Controla como se crean los POA's para los Admin's
		_consumer_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.CONSUMER_POA_KEY);
		
	}

	
	/**
     * @return
     */
    private static StructuredEvent initNullStructuredEvent()
    {
         org.omg.CosNotification._EventType event_type = 
            new org.omg.CosNotification._EventType("", "");
        
        org.omg.CosNotification.FixedEventHeader fixed_header = 
            new org.omg.CosNotification.FixedEventHeader(event_type,"");
        
        org.omg.CosNotification.Property[] variable_header = {};
        
        
        
        org.omg.CosNotification.EventHeader header = 
            new org.omg.CosNotification.EventHeader(fixed_header,
                                                    variable_header);
        
        org.omg.CosNotification.Property[] filterable_data = {};
        org.omg.CORBA.Any remainder_of_body = ORB.init().create_any();
        
        return new StructuredEvent(header, filterable_data, remainder_of_body);
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

	private ProxyPullSupplierData getData() {
		if (_current == null)
			setCurrent();

		try {
			ProxyPullSupplierData data = (ProxyPullSupplierData) _proxy_pull_suppliers_table.table
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
		/*
		 if (_current == null) setCurrent();
		 try
		 {
		 return _current.get_POA().the_POAManager();
		 }
		 //catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
		 catch (Exception ex)
		 {
		 TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
		 throw new org.omg.CORBA.INV_OBJREF();
		 }
		 */
	}

	public void register(ProxyPullSupplierData proxy) {
		REGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		_proxy_pull_suppliers_table.put(new OID(proxy.id.getBytes()), proxy);
	}

	public void unregister(ProxyPullSupplierData proxy) {
		UNREGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_proxy_pull_suppliers_table.remove(new OID(proxy.id.getBytes()));
	}

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
		ProxyPullSupplierData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_ADMINISTRATIVE_STATE);
		if (proxy.administrative_state != value) {
			proxy.administrative_state = value;

			if (PersistenceManager.isActive()) {
				//PersistenceManager.getDB().save(PersistenceDB.UPDATE, this);
			}
		}
	}

	
	synchronized public Filter forwarding_discriminator() {
		ProxyPullSupplierData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_FORWARDING_DISCRIMINATOR);

		try {
		return proxy.filterAdminDelegate.get_filter(0);
		} catch (FilterNotFound fnf)
		{
		   Filter filter =null;
	        try {
	            filter = 
	                MyChannel().default_filter_factory()
	                           .create_filter( TIDParser._CONSTRAINT_GRAMMAR);
	        }
	        catch (InvalidGrammar e) {}
        
	        proxy.filterAdminDelegate.add_filter(filter);
		
	        return filter;
		}
		
	}
	
	synchronized public void forwarding_discriminator(Filter value) {
		
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_FORWARDING_DISCRIMINATOR);
		throw new org.omg.CORBA.NO_IMPLEMENT();
	}

	synchronized public org.omg.CosEventComm.PullConsumer getPullConsumer()
			throws org.omg.CosEventComm.Disconnected {
		ProxyPullSupplierData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_PULL_CONSUMER);
		if (!proxy.connected)
			throw new org.omg.CosEventComm.Disconnected();
		if (!proxy.narrowed) {
			try {
				proxy.pullConsumer = org.omg.CosEventComm.PullConsumerHelper
						.narrow(proxy.pull_consumer_object);
				proxy.narrowed = true;
			} catch (org.omg.CORBA.COMM_FAILURE ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, NARROW_ERROR);
				throw ex;
			}
		}
		return proxy.pullConsumer;
	}

	public void connect_pull_consumer(
			org.omg.CosEventComm.PullConsumer pull_consumer)
			throws org.omg.CosEventChannelAdmin.AlreadyConnected 
	{
	    connectPullConsumer(pull_consumer);
	}
	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPullSupplierOperations#connect_any_pull_consumer(org.omg.CosEventComm.PullConsumer)
	 */
	public void connect_any_pull_consumer(PullConsumer pull_consumer)
			throws AlreadyConnected {
		this.connect_pull_consumer(pull_consumer);

	}
	
	/* (non-Javadoc)
     * @see org.omg.CosNotifyChannelAdmin.StructuredProxyPullSupplierOperations#connect_structured_pull_consumer(org.omg.CosNotifyComm.StructuredPullConsumer)
     */
    public void connect_structured_pull_consumer(StructuredPullConsumer pull_consumer)
        throws AlreadyConnected
    {
        connectPullConsumer(pull_consumer);
        
    }
    
    public void 
    	connectPullConsumer(org.omg.CORBA.Object pull_consumer)
        throws org.omg.CosEventChannelAdmin.AlreadyConnected 
    {
  		ProxyPullSupplierData proxy = getData();
  		TIDNotifTrace.print(TIDNotifTrace.USER, CONNECT_PULL_CONSUMER);

  		if (proxy.connected)
  			throw new org.omg.CosEventChannelAdmin.AlreadyConnected();

  		proxy.connected = true;
  		proxy.pull_consumer_object = pull_consumer;
  		proxy.narrowed = false;

  		if (PersistenceManager.isActive()) {
  			//PersistenceManager.getDB().save(PersistenceDB.UPDATE, this);
  		}
  	}

	public void disconnect_pull_supplier() {
		ProxyPullSupplierData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, DISCONNECT_PULL_SUPPLIER);

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		/*
		 if ( !proxy.connected )
		 {
		 TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_CONNECTED);
		 //  No esta especificado
		 //throw new org.omg.CosEventComm.Disconnected();
		 return;
		 }
		 */

		if (proxy.destroying == true)
			return;
		proxy.destroying = true;
		proxy.connected = false;

		// Lo elimino de la lista del SupplierAdmin
		proxy.consumerAdmin.removeProxyPullSupplier(proxy.name);

		// y lo desactivo del POA si no quedan enventos
		// por tratar en el POAManager de entrada.
		unregister(proxy);

		proxy.connected = false;
		proxy.pullConsumer = null;
		proxy.narrowed = false;

		// Facilitamos la liberacion de memoria
		proxy.consumerAdmin = null;
		proxy.id = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(proxy);
		}
	}

	synchronized public void destroyFromAdmin() {
		ProxyPullSupplierData proxy = getData();
		String thread_name = Thread.currentThread().getName();
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_ADMIN);

		if (!proxy.connected) {
			NOT_CONNECTED3[1] = proxy.id;
			NOT_CONNECTED3[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_CONNECTED3);
			//  No esta especificado
			//throw new org.omg.CosEventComm.Disconnected();
			return;
		}

		if (proxy.destroying == true)
			return;
		proxy.destroying = true;

		// Disconnect the client.
		if (proxy.destroying) {
			try {
				if (!proxy.narrowed) {
					proxy.pullConsumer = org.omg.CosEventComm.PullConsumerHelper
							.narrow(proxy.pull_consumer_object);
				}
				proxy.pullConsumer.disconnect_pull_consumer();
			} catch (Exception ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						DISCONNECT_PULL_CONSUMER_ERROR);
			}
			proxy.connected = false;
			proxy.pullConsumer = null;
		}

		

		// Borramos la referencia al admin
		proxy.consumerAdmin = null;

		// Borramos la referencia al POA
		proxy.poa = null;

		// Borramos la referencia al objeto
		//reference = null;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(proxy);
		}
	}

	public org.omg.CORBA.Any pull() throws org.omg.CosEventComm.Disconnected {
		ProxyPullSupplierData proxy = getData();
		String thread_name = Thread.currentThread().getName();
		TIDNotifTrace.print(TIDNotifTrace.USER, PULL);
		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		if (!proxy.connected)
			throw new org.omg.CosEventComm.Disconnected();

		//    org.omg.CORBA.Any ev = _eventQueue.get();

		//    org.omg.CORBA.Any ev = org.omg.CORBA.ORB.init().create_any();
		//    ev.insert_string("Mensaje Prueba");
		//    return ev;

		//if ( !(proxy.forwarding_discriminator.eval_value(event)) )
		//{
		//  TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DISCARD_EVENT);
		//  return null;
		//}
		//return null;
		return proxy.eventQueue.get();
	}

	public org.omg.CORBA.Any try_pull(org.omg.CORBA.BooleanHolder has_event)
			throws org.omg.CosEventComm.Disconnected {
		ProxyPullSupplierData proxy = getData();
		String thread_name = Thread.currentThread().getName();
		TIDNotifTrace.print(TIDNotifTrace.USER, TRY_PULL);

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}
		org.omg.CORBA.Any event = null;

		if (!proxy.connected)
			throw new org.omg.CosEventComm.Disconnected();

		   if ( proxy.eventQueue.size() != 0 )
		   {
		     event = proxy.eventQueue.get();
		     has_event.value = true;
		     return event;
		   }
		   else
		   {
		     has_event.value = false;
		     return org.omg.CORBA.ORB.init().create_any();
		   }
		//if ( !(proxy.forwarding_discriminator.eval_value(event)) )
		//{
		//  TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DISCARD_EVENT);
		//  return null;
		//}
		//return null;
	}

	public void push_event(org.omg.CORBA.Any event) {
		ProxyPullSupplierData proxy = getData();
		String thread_name = Thread.currentThread().getName();

		if (proxy.destroying) {
			DESTROYING[1] = proxy.id;
			DESTROYING[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DESTROYING);
			return;
		}

		PUSH_EVENT[1] = proxy.id;
		PUSH_EVENT[3] = thread_name;
		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, PUSH_EVENT);

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			// Discard LOCKED
			LOCKED[1] = proxy.id;
			LOCKED[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, LOCKED);
			return;
		}

		if (!proxy.connected) {
			// Discard NOT CONNECTED
			NOT_CONNECTED3[1] = proxy.id;
			NOT_CONNECTED3[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, NOT_CONNECTED3);
			return;
		}


		if (proxy.filterAdminDelegate != null) {

			try { 
				if ( !proxy.filterAdminDelegate.match(event) ) {
					// Discard FALSE EVALUATION
					DISCARD_EVENT[1] = proxy.id;
					DISCARD_EVENT[3] = thread_name;
					TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DISCARD_EVENT);
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


		if (MyType() == ProxyType.PULL_ANY) {
                    
			this.insertEvent(event);

		} 

		// TODO: Implement StructuredEvent
	}

	protected void insertEvent(org.omg.CORBA.Any event) {
		ProxyPullSupplierData proxy = getData();
		proxy.eventQueue.add(event);
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
			FilterImpl servant = (FilterImpl) (discriminator.poa.get_servant());
			servant.unregister(discriminator);
		} catch (Exception e) {
		}

		
	}

	
	// Persistencia
	public void loadData() {
		ProxyPullSupplierData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER,
				"ProxyPullSupplierImpl.loadData()");

		
	}

	

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#MyType()
	 */
	public ProxyType MyType() {
		return ProxyType.from_int(getData().type);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#MyAdmin()
	 */
	public ConsumerAdmin MyAdmin() {
		return getData().consumerAdmin;
	}
	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#MyChannel()
	 */
	public EventChannel MyChannel() {
		return this.getData().notificationChannel;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#priority_filter()
	 */
	public MappingFilter priority_filter() {
		//TODO: NO_IMPLEMENT
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#priority_filter(org.omg.CosNotifyFilter.MappingFilter)
	 */
	public void priority_filter(MappingFilter value) {
		//TODO: NO_IMPLEMENT
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#lifetime_filter()
	 */
	public MappingFilter lifetime_filter() {
		//TODO: NO_IMPLEMENT
		throw new NO_IMPLEMENT();

	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#lifetime_filter(org.omg.CosNotifyFilter.MappingFilter)
	 */
	public void lifetime_filter(MappingFilter value) {
		//TODO: NO IMPLEMENT
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#obtain_offered_types(org.omg.CosNotifyChannelAdmin.ObtainInfoMode)
	 */
	public _EventType[] obtain_offered_types(ObtainInfoMode mode) {
		// TODO: NO_IMPLEMENT;
		return new _EventType[0];
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#validate_event_qos(org.omg.CosNotification.Property[], org.omg.CosNotification.NamedPropertyRangeSeqHolder)
	 */
	public void validate_event_qos(Property[] required_qos,
			NamedPropertyRangeSeqHolder available_qos) throws UnsupportedQoS {
		//TODO: NO IMPLEMENT??
		throw new NO_IMPLEMENT();
	}

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
	    ProxyPullSupplierData data = this.getData();
	    data.qosAdminDelegate.set_qos( qos );
		Policy[] policies = 
		    data.qosAdminDelegate.getPolicies();
		
		data.reference = 
		    InternalProxyPullSupplierHelper.narrow 
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
		getData().qosAdminDelegate.validate_qos(required_qos, available_qos);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#add_filter(org.omg.CosNotifyFilter.Filter)
	 */
	public int add_filter(Filter new_filter) {
		return getData().filterAdminDelegate.add_filter(new_filter);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_filter(int)
	 */
	public void remove_filter(int filter) throws FilterNotFound {
		getData().filterAdminDelegate.remove_filter(filter);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_filter(int)
	 */
	public Filter get_filter(int filter) throws FilterNotFound {
		return getData().filterAdminDelegate.get_filter(filter);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_all_filters()
	 */
	public int[] get_all_filters() {
		return getData().filterAdminDelegate.get_all_filters();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_all_filters()
	 */
	public void remove_all_filters() {
		getData().filterAdminDelegate.remove_all_filters();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyComm.NotifySubscribeOperations#subscription_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
	 */
	public void subscription_change(_EventType[] added, _EventType[] removed)
			throws InvalidEventType {
		//TODO: NO_IMPLEMENT;
		throw new NO_IMPLEMENT();
	}

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPullSupplierOperations#pull_structured_event()
     */
    public StructuredEvent pull_structured_event()
        throws Disconnected
    {
        Any event = this.pull();
        
        StructuredEvent st_event = StructuredEventHelper.extract(event);
        
        return st_event;
    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPullSupplierOperations#try_pull_structured_event(org.omg.CORBA.BooleanHolder)
     */
    public StructuredEvent try_pull_structured_event(BooleanHolder has_event)
        throws Disconnected
    {
        Any event = this.try_pull(has_event);
        
        if(has_event.value)
        {
            StructuredEvent st_event = StructuredEventHelper.extract(event);
            
            return st_event;
        } else {  
            //TODO
            return NULL_ST_EVENT;
        }
    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPullSupplierOperations#disconnect_structured_pull_supplier()
     */
    public void disconnect_structured_pull_supplier()
    {
        this.disconnect_pull_supplier();
        
    }


    
}
