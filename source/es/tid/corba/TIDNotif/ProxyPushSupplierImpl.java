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

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosEventChannelAdmin.AlreadyConnected;
import org.omg.CosEventChannelAdmin.TypeError;
import org.omg.CosEventComm.PushConsumer;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotification.StructuredEventHelper;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyChannelAdmin.ConnectionAlreadyActive;
import org.omg.CosNotifyChannelAdmin.ConnectionAlreadyInactive;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.NotConnected;
import org.omg.CosNotifyChannelAdmin.ObtainInfoMode;
import org.omg.CosNotifyChannelAdmin.ProxyType;
import org.omg.CosNotifyChannelAdmin.StructuredProxyPushSupplier;
import org.omg.CosNotifyChannelAdmin.StructuredProxyPushSupplierOperations;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.StructuredPushConsumer;
import org.omg.CosNotifyComm.StructuredPushConsumerHelper;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterNotFound;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.MappingFilter;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPushSupplierHelper;
import org.omg.ServiceManagement.AdministrativeState;
import org.omg.ServiceManagement.OperationMode;

import es.tid.TIDorbj.core.poa.OID;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;

public class ProxyPushSupplierImpl extends
		org.omg.DistributionInternalsChannelAdmin.InternalProxyPushSupplierPOA
		implements ProxyPushSupplierMsg {
	private boolean _time_debug;

	// Control de errores
	private long max_disconnected_time;

	private long max_comm_failures;

	private int on_comm_failure;

	private long max_no_response;

	private int on_no_response;

	private org.omg.CORBA.ORB _orb;

	private org.omg.PortableServer.POA _agentPOA;

	private org.omg.PortableServer.POAManager _poa_manager;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// Controla si se utiliza un POA GLOBAL a todos los ConsumerAdmin's de todos
	// los CHANNEL's, LOCAL a todos los ConsumerAdmin's de un CHANNEL, o
	// EXCLUSIVO para cada ConsumerAdmin
	private int _consumer_poa_policy;

	// List of Channels (Pack)
	private DataTable2 _proxy_push_suppliers_table;

	public ProxyPushSupplierImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agent_poa,
			org.omg.PortableServer.POAManager poa_manager, String channel_id,
			String supplier_id) {
		_orb = orb;
		_agentPOA = agent_poa;
		_poa_manager = poa_manager;
		_proxy_push_suppliers_table = new DataTable2();

		// Controla como se crean los POA's para los Admin's
		_consumer_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.CONSUMER_POA_KEY);

		// Algo temporal
		_time_debug = TIDNotifConfig.getBool(TIDNotifConfig.TIME_DEBUG_KEY);

		max_disconnected_time = 1000 * TIDNotifConfig
				.getInt(TIDNotifConfig.MAX_DISCONNECTED_TIME);
		max_comm_failures = TIDNotifConfig
				.getInt(TIDNotifConfig.MAX_COMM_FAILURES);
		on_comm_failure = TIDNotifConfig.getInt(TIDNotifConfig.ON_COMM_FAILURE);
		max_no_response = TIDNotifConfig.getInt(TIDNotifConfig.MAX_NO_RESPONSE);
		on_no_response = TIDNotifConfig.getInt(TIDNotifConfig.ON_NO_RESPONSE);
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

	private ProxyPushSupplierData getData() {
		if (_current == null)
			setCurrent();

		try {
			ProxyPushSupplierData data = (ProxyPushSupplierData) _proxy_push_suppliers_table.table
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

	public void register(ProxyPushSupplierData proxy) {
		REGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);		
		_proxy_push_suppliers_table.put(new OID(proxy.id.getBytes()), proxy);
	}

	public void unregister(ProxyPushSupplierData proxy) {
		UNREGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_proxy_push_suppliers_table.remove(new OID(proxy.id.getBytes()));
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
		ProxyPushSupplierData proxy = getData();
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
		ProxyPushSupplierData proxy = getData();
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

	synchronized public org.omg.CosEventComm.PushConsumer getPushConsumer()
			throws org.omg.CosEventComm.Disconnected {
		ProxyPushSupplierData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_PUSH_CONSUMER);
		if (!proxy.connected)
			throw new org.omg.CosEventComm.Disconnected();
		if (!proxy.narrowed) {
			try {
				proxy.pushConsumer = org.omg.CosEventComm.PushConsumerHelper
						.narrow(proxy.push_consumer_object);
				proxy.narrowed = true;
			} catch (org.omg.CORBA.COMM_FAILURE ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, NARROW_ERROR);
				throw ex;
			}
		}
		return proxy.pushConsumer;
	}

	public org.omg.ServiceManagement.OperationMode operation_mode() {
		return getData().operation_mode;
	}

	public int order() {
		return getData().order.intValue();
	}

	synchronized public void order(int value) {
		ProxyPushSupplierData proxy = getData();
		if (proxy.operation_mode == OperationMode.distribution) {
			if (value != proxy.order.intValue()) {
				try {
					if (proxy.order.intValue() != 0) {
						proxy.consumerAdmin.move_positioned_push_supplier(
								proxy.order.intValue(), value, proxy.name);
					}
				} catch (org.omg.DistributionInternalsChannelAdmin.AlreadyDefinedOrder ex1) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR_1);
					return;
				} catch (org.omg.DistributionInternalsChannelAdmin.DataError ex2) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR_2);
					return;
				} catch (org.omg.DistributionInternalsChannelAdmin.OrderNotFound ex3) {
					TIDNotifTrace.print(TIDNotifTrace.ERROR, MSG_ERROR_3);
					return;
				}
				proxy.order = new Integer(value);

				if (PersistenceManager.isActive()) {
					PersistenceManager.getDB().update(PersistenceDB.ATTR_ORDER,
							proxy);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPushSupplierOperations#connect_any_push_consumer(org.omg.CosEventComm.PushConsumer)
	 */
	public void connect_any_push_consumer(PushConsumer push_consumer) 
	throws AlreadyConnected, TypeError 
	{
	    if(MyType() != ProxyType.PUSH_ANY) {
            throw new BAD_OPERATION();
        }
		this.connectPushConsumer( push_consumer );
	}
	
	public void connect_push_consumer(
			org.omg.CosEventComm.PushConsumer push_consumer)
			throws org.omg.CosEventChannelAdmin.AlreadyConnected 
    {	  
	    if(MyType() != ProxyType.PUSH_ANY) {
            throw new BAD_OPERATION();
        }
		this.connectPushConsumer( push_consumer );		
	}

	synchronized public void disconnect_push_supplier() {
		ProxyPushSupplierData proxy = getData();
		String thread_name = Thread.currentThread().getName();

		DISCONNECT_PUSH_SUPPLIER[1] = proxy.name;
		DISCONNECT_PUSH_SUPPLIER[3] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DISCONNECT_PUSH_SUPPLIER);

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			LOCKED[1] = proxy.id;
			LOCKED[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOCKED);
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		/*
		 if ( !connected )
		 {
		 NOT_CONNECTED2[1] = proxy.id;
		 TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_CONNECTED2);
		 //  No esta especificado
		 //throw new org.omg.CosEventComm.Disconnected();
		 return;
		 }
		 */
		if (proxy.destroying == true)
			return;
		proxy.destroying = true;

		// Lo elimino de la lista del SupplierAdmin
		proxy.consumerAdmin.removeProxyPushSupplier(proxy.name);

		// y lo desactivo del POA si no quedan enventos
		// por tratar en el POAManager de entrada.
		unregister(proxy);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(proxy);
		}
				
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

		proxy.connected = false;
		proxy.disconnected_time = 0;
		proxy.pushConsumer = null;
		proxy.narrowed = false;

		// Facilitamos la liberacion de memoria
		proxy.poa = null;
		proxy.consumerAdmin = null;
		proxy.id = null;
		proxy.reference = null;
	}

	synchronized public void destroyFromAdmin() {
		ProxyPushSupplierData proxy = getData();
		DESTROY_FROM_ADMIN[1] = proxy.name;
		DESTROY_FROM_ADMIN[3] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_ADMIN);

		if (proxy.destroying) {
			TIDNotifTrace.print(TIDNotifTrace.USER,
					"ProxyPushSupplierImpl: Already destroying.");
			return;
		}
		proxy.destroying = true;

		// Disconnect the client.
		if (proxy.connected) {
			try {
				if (!proxy.narrowed) {
					proxy.pushConsumer = org.omg.CosEventComm.PushConsumerHelper
							.narrow(proxy.push_consumer_object);
				}
				proxy.pushConsumer.disconnect_push_consumer();
			} catch (Exception ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						DISCONNECT_PUSH_CONSUMER_ERROR);
			}
			proxy.connected = false;
			proxy.pushConsumer = null;
		}

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

		// y lo desactivo del POA si no quedan enventos
		// por tratar en el POAManager de entrada.
		unregister(proxy);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(proxy);
		}

		// Facilitamos la liberacion de memoria
		proxy.poa = null;
		proxy.consumerAdmin = null;
		proxy.id = null;
		proxy.reference = null;
	}

	public void push_event(org.omg.CORBA.Any event)
	//throws org.omg.CosEventComm.Disconnected
	{
		ProxyPushSupplierData proxy = getData();
		String thread_name = Thread.currentThread().getName();

		if (proxy.destroying) {
			DESTROYING[1] = proxy.id;
			DESTROYING[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DESTROYING);
			proxy.total_proccessed_events--;
			return;
		}

		PUSH_EVENT[1] = proxy.id;
		PUSH_EVENT[3] = thread_name;
		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, PUSH_EVENT);

		long t1 = 0;
		long t_eval = 0;
		long t2;

		if (_time_debug) {
			proxy.total_proccessed_events++;
			t1 = System.currentTimeMillis();
		}

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			// Discard LOCKED
			LOCKED[1] = proxy.id;
			LOCKED[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, LOCKED);
			proxy.total_proccessed_events--;
			return;
		}

		if (!proxy.connected) {
			// Discard NOT CONNECTED
			NOT_CONNECTED3[1] = proxy.id;
			NOT_CONNECTED3[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, NOT_CONNECTED3);
			proxy.total_proccessed_events--;

			if (max_disconnected_time > 0) {
				if (proxy.disconnected_time == 0) {
					proxy.disconnected_time = System.currentTimeMillis();
				} else {
					if ((System.currentTimeMillis() - proxy.disconnected_time) > max_disconnected_time) {
						// Se ha superado el tiempo permitido sin conexion
						// Se destruye
						AUTO_DISCONNECT3[1] = proxy.id;
						AUTO_DISCONNECT3[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								AUTO_DISCONNECT3);
						disconnect_push_supplier();
					}
				}
			}
			return;
		}

		if (proxy.filterAdminDelegate != null) {
			if (_time_debug) {
				t_eval = System.currentTimeMillis();
			}
			try { 
				if ( !proxy.filterAdminDelegate.match(event) ) {
					if (_time_debug) {
						EVAL_TIME_INFO[1] = Long.toString(System
								.currentTimeMillis()
								- t_eval);
						EVAL_TIME_INFO[3] = proxy.id;
						EVAL_TIME_INFO[5] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG,
								EVAL_TIME_INFO);
					}
					// Discard FALSE EVALUATION
					DISCARD_EVENT[1] = proxy.id;
					DISCARD_EVENT[3] = thread_name;
					TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DISCARD_EVENT);
					proxy.total_proccessed_events--;
					return;
				}
			} catch ( UnsupportedFilterableData ufd ){
	    		  //TODO: review this
		          TIDNotifTrace.print(TIDNotifTrace.ERROR, " EEE Unsuported filterable data EEE" );
		          TIDNotifTrace.printStackTrace( TIDNotifTrace.ERROR, ufd );
		          TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
		          return;
			}
			
			if (_time_debug) {
				EVAL_TIME_INFO[1] = Long.toString(System.currentTimeMillis()
						- t_eval);
				EVAL_TIME_INFO[3] = proxy.id;
				EVAL_TIME_INFO[5] = thread_name;
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, EVAL_TIME_INFO);
			}
		}

		try {
		    if(MyType() == ProxyType.PUSH_ANY) {
				if (!proxy.narrowed) {
					proxy.pushConsumer = org.omg.CosEventComm.PushConsumerHelper
							.narrow(proxy.push_consumer_object);
					proxy.narrowed = true;
				}
				proxy.pushConsumer.push(event);
		    } else if(MyType() == ProxyType.PUSH_STRUCTURED) {
		        if (!proxy.narrowed) {
		            proxy.structuredPushConsumer = 
					    StructuredPushConsumerHelper
							.narrow(proxy.push_consumer_object);
					
					proxy.narrowed = true;
				}
		        
		        StructuredEvent structured_event = 
		            StructuredEventHelper.extract(event);
		        
		        // TODO: filtrar por tipo esperado _EventType
		        
				proxy.structuredPushConsumer
				     .push_structured_event(structured_event);
		        
		        
		    } else {		    
		        throw new INTERNAL("Invalid Proxy Type");
		    }
		    

			// Al responder reseteamos los errores
			proxy.total_comm_failure = 0;
			proxy.total_no_response = 0;

			if (_time_debug) {
				t2 = System.currentTimeMillis();
				long t3 = t2 - t1;

				if (t3 > proxy.max_proccessing_time)
					proxy.max_proccessing_time = t3;
				proxy.total_proccessing_time = proxy.total_proccessing_time
						+ t3;

				TIME_INFO[1] = Long.toString(t3);
				TIME_INFO[3] = proxy.id;
				TIME_INFO[5] = thread_name;
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO);
			}
		} catch (org.omg.CORBA.COMM_FAILURE ex) {
			PUSH_EXCEPTION_1[1] = proxy.id;
			PUSH_EXCEPTION_1[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, PUSH_EXCEPTION_1);
			if (_time_debug) {
				t2 = System.currentTimeMillis();
				long t3 = t2 - t1;

				TIME_INFO[1] = Long.toString(t3);
				TIME_INFO[3] = proxy.id;
				TIME_INFO[5] = thread_name;
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO);
			}
			if (max_comm_failures > 0) {
				proxy.total_comm_failure++;
				if (proxy.total_comm_failure > max_comm_failures) {
					switch (on_comm_failure) {
					case 0: // NONE: no action
						AUTO_RESET[1] = proxy.id;
						AUTO_RESET[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, AUTO_RESET);
						//reset counter
						proxy.total_comm_failure = 0;
						break;
					case 1: // Lock ProxyPushSupplier
						AUTO_LOCK[1] = proxy.id;
						AUTO_LOCK[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, AUTO_LOCK);
						proxy.administrative_state = org.omg.ServiceManagement.AdministrativeState.locked;
						break;
					case 2: // Destroy ProxyPushSupplier
						AUTO_DISCONNECT[1] = proxy.id;
						AUTO_DISCONNECT[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								AUTO_DISCONNECT);
						disconnect_push_supplier();
						break;
					default:
						AUTO_RESET[1] = proxy.id;
						AUTO_RESET[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, AUTO_RESET);
						//reset counter
						proxy.total_comm_failure = 0;
					}
				}
			}
		} catch (org.omg.CORBA.NO_RESPONSE ex1) {
			NO_RESPONSE_EXCEPTION[1] = proxy.id;
			NO_RESPONSE_EXCEPTION[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NO_RESPONSE_EXCEPTION);
			if (_time_debug) {
				t2 = System.currentTimeMillis();
				long t3 = t2 - t1;

				if (t3 > proxy.max_proccessing_time)
					proxy.max_proccessing_time = t3;
				proxy.total_proccessing_time = proxy.total_proccessing_time
						+ t3;

				TIME_INFO[1] = Long.toString(t3);
				TIME_INFO[3] = proxy.id;
				TIME_INFO[5] = thread_name;
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO);
			}
			if (max_no_response > 0) {
				proxy.total_no_response++;
				if (proxy.total_no_response > max_no_response) {
					switch (on_no_response) {
					case 0: // NONE: no action
						AUTO_RESET2[1] = proxy.id;
						AUTO_RESET2[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, AUTO_RESET2);
						//reset counter
						proxy.total_no_response = 0;
						break;
					case 1: // Lock ProxyPushSupplier
						AUTO_LOCK2[1] = proxy.id;
						AUTO_LOCK2[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, AUTO_LOCK2);
						proxy.administrative_state = org.omg.ServiceManagement.AdministrativeState.locked;
						break;
					case 2: // Destroy ProxyPushSupplier
						AUTO_DISCONNECT2[1] = proxy.id;
						AUTO_DISCONNECT2[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								AUTO_DISCONNECT2);
						disconnect_push_supplier();
						break;
					default:
						AUTO_RESET2[1] = proxy.id;
						AUTO_RESET2[3] = thread_name;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, AUTO_RESET2);
						//reset counter
						proxy.total_no_response = 0;
					}
				}
			}
		} catch (org.omg.CosEventComm.Disconnected ex2) {
			PUSH_EXCEPTION_2[1] = proxy.id;
			PUSH_EXCEPTION_2[3] = thread_name;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, PUSH_EXCEPTION_2);
			if (_time_debug) {
				t2 = System.currentTimeMillis();
				long t3 = t2 - t1;

				TIME_INFO[1] = Long.toString(t3);
				TIME_INFO[3] = proxy.id;
				TIME_INFO[5] = thread_name;
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO);
			}
			disconnect_push_supplier();
		} catch (java.lang.Exception ex3) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex3);
			if (_time_debug) {
				t2 = System.currentTimeMillis();
				long t3 = t2 - t1;

				TIME_INFO[1] = Long.toString(t3);
				TIME_INFO[3] = proxy.id;
				TIME_INFO[5] = thread_name;
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO);
			}
		}
	}

	public void distrib_event(org.omg.CORBA.Any event)
			throws org.omg.DistributionInternalsChannelAdmin.LockedState,
			org.omg.DistributionInternalsChannelAdmin.NotMatch,
			org.omg.DistributionInternalsChannelAdmin.ConnectionError {
	    
		ProxyPushSupplierData proxy = getData();
		
		//TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, DISTRIB_PUSH_EVENT);

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			// Discard LOCKED
			// TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, LOCKED);
			throw new org.omg.DistributionInternalsChannelAdmin.LockedState();
		}

		if (!proxy.connected) {
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, NOT_CONNECTED5);
			throw new org.omg.DistributionInternalsChannelAdmin.ConnectionError();
		}

		if (proxy.filterAdminDelegate != null) {
			try {
				if ( !proxy.filterAdminDelegate.match( event ) ) {
					TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, EVENT_NOT_MATCH);
					throw new org.omg.DistributionInternalsChannelAdmin.NotMatch();
				}
			} catch ( UnsupportedFilterableData ufd ){
	    		  //TODO: review this
		          TIDNotifTrace.print(TIDNotifTrace.ERROR, " EEE Unsuported filterable data EEE" );
		          TIDNotifTrace.printStackTrace( TIDNotifTrace.ERROR, ufd );
		          TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);
		          //TODO: review this too :)
		          throw new org.omg.DistributionInternalsChannelAdmin.NotMatch();
			}
		}

		try {
			if (!proxy.narrowed) {
				proxy.pushConsumer = org.omg.CosEventComm.PushConsumerHelper
						.narrow(proxy.push_consumer_object);
				proxy.narrowed = true;
			}
			proxy.pushConsumer.push(event);
		} catch (org.omg.CORBA.COMM_FAILURE cfe) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISTRIB_PUSH_EXCEPTION);
			throw new org.omg.DistributionInternalsChannelAdmin.ConnectionError();
		} catch (org.omg.CosEventComm.Disconnected de) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISTRIB_PUSH_EXCEPTION);
			throw new org.omg.DistributionInternalsChannelAdmin.ConnectionError();
		} catch (org.omg.CORBA.NO_RESPONSE nre) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISTRIB_PUSH_EXCEPTION);
			throw new org.omg.DistributionInternalsChannelAdmin.ConnectionError();
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.DistributionInternalsChannelAdmin.ConnectionError();
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
		DEACTIVATE_DISCRIMINATOR[1] = discriminator.id;
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, DEACTIVATE_DISCRIMINATOR);

		try {
			FilterImpl servant = (FilterImpl) (discriminator.poa
					.get_servant());
			servant.unregister(discriminator);
		} catch (Exception e) {
		}
		/*
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
		ProxyPushSupplierData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER,
				"ProxyPushSupplierImpl.loadData()");
	}

	

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPushSupplierOperations#suspend_connection()
	 */
	public void suspend_connection() throws ConnectionAlreadyInactive, NotConnected {
		//TODO: review this
		getData().administrative_state = AdministrativeState.locked;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPushSupplierOperations#resume_connection()
	 */
	public void resume_connection() throws ConnectionAlreadyActive, NotConnected {
		//TODO: review this
		getData().administrative_state = AdministrativeState.unlocked;
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
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#priority_filter()
	 */
	public MappingFilter priority_filter() {		
		throw new NO_IMPLEMENT();
	}
	
	/*
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#MyChannel()
	 */
	public EventChannel MyChannel() {
		return this.getData().notificationChannel;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#priority_filter(org.omg.CosNotifyFilter.MappingFilter)
	 */
	public void priority_filter(MappingFilter value) {
		//TODO: NO_IMPLEMENT?
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#lifetime_filter()
	 */
	public MappingFilter lifetime_filter() {
		//TODO: NO_IMPLEMENT?
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#lifetime_filter(org.omg.CosNotifyFilter.MappingFilter)
	 */
	public void lifetime_filter(MappingFilter value) {
		//TODO: NO_IMPLEMENT?
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#obtain_offered_types(org.omg.CosNotifyChannelAdmin.ObtainInfoMode)
	 */
	public _EventType[] obtain_offered_types(ObtainInfoMode mode) {
		//TODO: NO_IMPLEMENT;
		throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxySupplierOperations#validate_event_qos(org.omg.CosNotification.Property[], org.omg.CosNotification.NamedPropertyRangeSeqHolder)
	 */
	public void validate_event_qos(Property[] required_qos, NamedPropertyRangeSeqHolder available_qos) throws UnsupportedQoS {
		//TODO: NO_IMPLEMENT? This applies to structured events...
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
	    ProxyPushSupplierData data = this.getData();
	    data.qosAdminDelegate.set_qos( qos );
		Policy[] policies = 
		    data.qosAdminDelegate.getPolicies();
		
		data.reference = 
		    InternalProxyPushSupplierHelper.narrow 
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
		getData().qosAdminDelegate.validate_qos( required_qos, available_qos );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#add_filter(org.omg.CosNotifyFilter.Filter)
	 */
	public int add_filter(Filter new_filter) {
		return getData().filterAdminDelegate.add_filter( new_filter );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#remove_filter(int)
	 */
	public void remove_filter(int filter) throws FilterNotFound {
		getData().filterAdminDelegate.remove_filter( filter );		
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterAdminOperations#get_filter(int)
	 */
	public Filter get_filter(int filter) throws FilterNotFound {
		return getData().filterAdminDelegate.get_filter( filter );
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
	public void subscription_change(_EventType[] added, _EventType[] removed) throws InvalidEventType {
	    
		// TODO: NO IMPLEMENT;
		throw new NO_IMPLEMENT();
	}

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPushSupplierOperations#disconnect_structured_push_supplier()
     */
    public void disconnect_structured_push_supplier()
    {
        if(MyType() != ProxyType.PUSH_STRUCTURED) {
            throw new BAD_OPERATION();
        }
        
        this.disconnect_push_supplier();        
    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyChannelAdmin.StructuredProxyPushSupplierOperations#connect_structured_push_consumer(org.omg.CosNotifyComm.StructuredPushConsumer)
     */
    public void connect_structured_push_consumer(StructuredPushConsumer push_consumer)
        throws AlreadyConnected,
        TypeError
    {
        if(MyType() != ProxyType.PUSH_STRUCTURED) {
            throw new BAD_OPERATION();
        }
       this.connectPushConsumer(push_consumer);
        
    }
    
    protected synchronized void 
    	connectPushConsumer(org.omg.CORBA.Object push_consumer) 
    	throws AlreadyConnected 
    {
        ProxyPushSupplierData proxy = getData();
		CONNECT_PUSH_CONSUMER[1] = proxy.name;
		TIDNotifTrace.print(TIDNotifTrace.USER, CONNECT_PUSH_CONSUMER);

		if (proxy.connected)
			throw new org.omg.CosEventChannelAdmin.AlreadyConnected();

		proxy.connected = true;
		proxy.disconnected_time = 0;
		proxy.push_consumer_object = push_consumer;
		proxy.narrowed = false;

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_PUSH_CONSUMER,
					proxy);
		}
    }

}