/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 123 $
* Date: $Date: 2009-02-26 14:33:52 +0100 (Thu, 26 Feb 2009) $
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

import java.util.Vector;

import org.omg.CORBA.Any;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.InvalidPolicies;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.Object;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyCurrent;
import org.omg.CORBA.PolicyCurrentHelper;
import org.omg.CORBA.SetOverrideType;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosEventChannelAdmin.AlreadyConnected;
import org.omg.CosEventComm.Disconnected;
import org.omg.CosEventComm.PushSupplier;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotification.StructuredEventHelper;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.ObtainInfoMode;
import org.omg.CosNotifyChannelAdmin.ProxyType;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.CosNotifyComm.InvalidEventType;
import org.omg.CosNotifyComm.StructuredPushSupplier;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterNotFound;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPushConsumerHelper;
import org.omg.ServiceManagement.OperationMode;

import es.tid.TIDorbj.core.poa.OID;
import es.tid.corba.ExceptionHandlerAdmin.CannotProceed;
import es.tid.corba.TIDNotif.qos.EventQoSAdmin;
import es.tid.corba.TIDNotif.qos.PriorityProperty;
import es.tid.corba.TIDNotif.qos.StartTimeProperty;
import es.tid.corba.TIDNotif.qos.StartTimeSupportedProperty;
import es.tid.corba.TIDNotif.qos.StopTimeProperty;
import es.tid.corba.TIDNotif.qos.StopTimeSupportedProperty;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;

public class ProxyPushConsumerImpl extends
		org.omg.DistributionInternalsChannelAdmin.InternalProxyPushConsumerPOA
		implements ProxyPushConsumerMsg {
	private static final int OP_GET = 0;

	private static final int OP_INC = 1;

	private static final int OP_DEC = -1;

	// Mecanismo de contencion
	// No se permitira que lleguen eventos al canal con un ritmo superior a
	// _FLOOD_EVENTS por _FLOOD_TIME, de ser asi se esperara _CONTENTION_TIME 
	// milliseconds
	private boolean _time_debug;

	private boolean _contention;

	private long _contention_time;

	private long _flood_events;

	private long _flood_time;

	private org.omg.CORBA.ORB _orb;

	private org.omg.PortableServer.POA _agentPOA;

	private org.omg.PortableServer.POAManager _poa_manager;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// Controla si se utiliza un POA GLOBAL a todos los SupplierAdmin's de todos
	// los CHANNEL's, LOCAL a todos los SupplierAdmin's de un CHANNEL, o 
	// EXCLUSIVO para cada SupplierAdmin
	private int _supplier_poa_policy;

	// List of Proxy's
	private DataTable2 _proxy_push_consumers_table;

	public ProxyPushConsumerImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agent_poa,
			org.omg.PortableServer.POAManager poa_manager) {
		_orb = orb;
		_agentPOA = agent_poa;
		_poa_manager = poa_manager;
		_proxy_push_consumers_table = new DataTable2();

		// Controla como se crean los POA's para los Admin's
		_supplier_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.SUPPLIER_POA_KEY);

		// Algo temporal
		_time_debug = TIDNotifConfig.getBool(TIDNotifConfig.TIME_DEBUG_KEY);

		// Mecanismo de contencion
		_contention = TIDNotifConfig.getBool(TIDNotifConfig.CONTENTION_KEY);
		_contention_time = TIDNotifConfig
				.getInt(TIDNotifConfig.CONTENT_TIME_KEY);
		_flood_events = TIDNotifConfig.getInt(TIDNotifConfig.FLOOD_EVENTS_KEY);
		_flood_time = TIDNotifConfig.getInt(TIDNotifConfig.FLOOD_TIME_KEY);
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

	private ProxyPushConsumerData getData() {
		if (_current == null)
			setCurrent();

		try {
			ProxyPushConsumerData data = (ProxyPushConsumerData) _proxy_push_consumers_table.table
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

	public void register(ProxyPushConsumerData proxy) {
		REGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		_proxy_push_consumers_table.put(new OID(proxy.id.getBytes()), proxy);
	}

	public void unregister(ProxyPushConsumerData proxy) {
		UNREGISTER[1] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_proxy_push_consumers_table.remove(new OID(proxy.id.getBytes()));
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
		ProxyPushConsumerData proxy = getData();
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
		ProxyPushConsumerData proxy = getData();
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

	synchronized public org.omg.CosEventComm.PushSupplier getPushSupplier()
			throws org.omg.CosEventComm.Disconnected {
		ProxyPushConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_PUSH_SUPPLIER);
		if (!proxy.connected)
			throw new org.omg.CosEventComm.Disconnected();
		if (!proxy.narrowed) {
			try {
				proxy.pushSupplier = org.omg.CosEventComm.PushSupplierHelper
						.narrow(proxy.push_supplier_object);
				proxy.narrowed = true;
			} catch (org.omg.CORBA.COMM_FAILURE ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, NARROW_ERROR);
				throw ex;
			}
		}
		return proxy.pushSupplier;
	}

	//--------------------------------------------------------------------------

	// NUEVAS FUNCIONES DEL ATRIBUTO OPERATOR
	//
	public org.omg.TransformationAdmin.TransformingOperator operator() {
		ProxyPushConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_TRANSFORMING_OPERATOR);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (proxy.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		if (proxy.transforming_operator == null) {
			org.omg.PortableServer.POA operator_poa;
			if (_supplier_poa_policy == 0) // Create the Global POA
			{
				operator_poa = globalTransformingOperatorPOA();
			} else if (_supplier_poa_policy == 1) // Create the Local POA
			{
				operator_poa = localTransformingOperatorPOA(proxy.channel_id);
			} else // Create the Exclusive POA
			{
				operator_poa = exclusiveTransformingOperatorPOA(
						proxy.channel_id, proxy.admin_id);
			}

			String id;
            try {
                id = PersistenceManager.getDB().getUID();
            }
            catch (Exception e) {
                throw new INTERNAL(e.toString());
            }
            proxy.transforming_operator = new TransformingOperatorData(id,
					operator_poa);

			register_operator(proxy.transforming_operator, operator_poa);

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().save(proxy.transforming_operator);
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_TRANSFORMING_OPERATOR, proxy);
			}
		}
		return NotifReference
				.transformingOperatorReference(proxy.transforming_operator.poa,
						proxy.transforming_operator.id);
	}

	synchronized public void operator(
			org.omg.TransformationAdmin.TransformingOperator value) {
		ProxyPushConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_TRANSFORMING_OPERATOR);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (proxy.operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}

		throw new org.omg.CORBA.NO_IMPLEMENT();

		/*
		 if (!(value._is_equivalent(proxy.transforming_operator)))
		 {
		 if (proxy.transforming_operator != null)
		 {
		 try
		 {
		 proxy.transforming_operator.reference.destroy();
		 }
		 catch (java.lang.Exception ex) { }
		 }

		 proxy.transforming_operator = 
		 InternalTransformingOperatorHelper.narrow(value);

		 if (PersistenceManager.isActive())
		 {
		 //PersistenceManager.getDB().save(PersistenceDB.UPDATE, this);
		 }
		 }
		 */
	}

	//-----------------------------------------------------------------------

	// NUEVAS FUNCIONES DEL ATRIBUTO DISTRIBUTION_ERROR_HANDLER
	//
	public es.tid.corba.ExceptionHandlerAdmin.ExceptionHandler transformation_error_handler() {
		ProxyPushConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_EXCEPTION_HANDLER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (proxy.operation_mode != OperationMode.distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_ALLOWED);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}
		if (proxy.exception_handler == null) {
			throw new org.omg.CORBA.OBJECT_NOT_EXIST(O_N_EXIST);
		}
		return proxy.exception_handler;
	}

	synchronized public void transformation_error_handler(
			es.tid.corba.ExceptionHandlerAdmin.ExceptionHandler handler) {
		ProxyPushConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_EXCEPTION_HANDLER);

		// Solo se permite llamar a este metodo en DISTRIBUTION mode
		if (proxy.operation_mode != OperationMode.distribution) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_ALLOWED);
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED);
		}
		if (!(handler._is_equivalent(proxy.exception_handler))) {
			proxy.exception_handler = handler;

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().update(
						PersistenceDB.ATTR_T_ERROR_HANDLER, proxy);
			}
		}
	}

	//-----------------------------------------------------------------------

	public void connect_push_supplier(
			org.omg.CosEventComm.PushSupplier push_supplier)
			throws org.omg.CosEventChannelAdmin.AlreadyConnected
	{		
		connectPushSupplier(push_supplier);
	}
	
	protected synchronized void 
		connectPushSupplier(org.omg.CORBA.Object push_supplier)
	throws org.omg.CosEventChannelAdmin.AlreadyConnected 
	{
		ProxyPushConsumerData proxy = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, CONNECT_PUSH_SUPPLIER);
		
		if (proxy.connected)
			throw new org.omg.CosEventChannelAdmin.AlreadyConnected();
		
		proxy.connected = true;
		proxy.push_supplier_object = push_supplier;
		proxy.narrowed = false;
		
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_PUSH_SUPPLIER,
					proxy);
		}
	}

	public void disconnect_push_consumer() {
		ProxyPushConsumerData proxy = getData();
		DISCONNECT_PUSH_CONSUMER[1] = proxy.name;
		DISCONNECT_PUSH_CONSUMER[3] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DISCONNECT_PUSH_CONSUMER);

		if (proxy.administrative_state == org.omg.ServiceManagement.AdministrativeState.locked) {
			throw new org.omg.CORBA.NO_PERMISSION();
		}

		// No compruebo si se encuentra o no conectado, para poder destruir los
		// proxy's que se crean y no se llegan a conectar.
		if (proxy.destroying == true) {
			return;
		}
		proxy.destroying = true;

		if (eventCount(OP_GET, proxy) == 0L) {
			// Lo elimino de la lista del SupplierAdmin
			proxy.supplierAdmin.removeProxyPushConsumer(proxy.name);

			// y lo desactivo del POA si no quedan enventos
			// por tratar en el POAManager de entrada.
			unregister(proxy);

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().delete(proxy);
			}

			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					" *** destroyDiscriminator() ***");
			destroyDiscriminator(proxy);

			// Facilitamos la liberacion de memoria
			proxy.supplierAdmin = null;
			//proxy.id = null;
			proxy.reference = null;
		}
		// Liberamos referencias
		proxy.connected = false;
		proxy.pushSupplier = null;
		proxy.narrowed = false;
	}

	synchronized public void destroyFromAdmin() {
		ProxyPushConsumerData proxy = getData();
		DESTROY_FROM_ADMIN[1] = proxy.name;
		DESTROY_FROM_ADMIN[3] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY_FROM_ADMIN);

		if (proxy.destroying) {
			TIDNotifTrace.print(TIDNotifTrace.USER,
					"ProxyPushConsumerImpl: Already destroying.");
			return;
		}
		proxy.destroying = true;

		// Disconnect the client.
		if (proxy.connected) {
			try {
				if (!proxy.narrowed) {
					proxy.pushSupplier = org.omg.CosEventComm.PushSupplierHelper
							.narrow(proxy.push_supplier_object);
				}
				proxy.pushSupplier.disconnect_push_supplier();
			} catch (Exception ex) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR,
						DISCONNECT_PUSH_SUPPLIER_ERROR);
			}
			proxy.connected = false;
			proxy.pushSupplier = null;
		}

		TIDNotifTrace.print(TIDNotifTrace.DEBUG,
				" $$$ destroyDiscriminator() $$$");
		destroyDiscriminator(proxy);

		NO_PROCESSED_EVENTS[1] = String.valueOf(eventCount(OP_GET, proxy));
		TIDNotifTrace.print(TIDNotifTrace.USER, NO_PROCESSED_EVENTS);

		// y lo desactivo del POA si no quedan enventos
		// por tratar en el POAManager de entrada.
		unregister(proxy);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(proxy);
		}

		// Facilitamos la liberacion de memoria
		proxy.supplierAdmin = null;
		proxy.id = null;
		proxy.reference = null;
	}

	public void push(org.omg.CORBA.Any eventData)
			throws org.omg.CosEventComm.Disconnected {
		ProxyPushConsumerData proxy = getData();
		//TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, PUSH);

		if (!(proxy.connected) || (proxy.destroying)) {
			throw new org.omg.CosEventComm.Disconnected();
		}

		long event_arrive_time = 0;

		if (_contention) {
			event_arrive_time = System.currentTimeMillis();

			if ((event_arrive_time - proxy.last_event_arrive_time) < (_flood_time / _flood_events)) {
				proxy.num_received_events++;
				if (proxy.num_received_events == _flood_events) {
					try {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, FLOOD_EVENTS);
						Thread.sleep(_contention_time);
					} catch (InterruptedException e) {
						// ...
					}
					proxy.num_received_events = 0;
				}
			} else {
				proxy.num_received_events = 0;
			}
			proxy.last_event_arrive_time = event_arrive_time;
		} else if (_time_debug) {
			event_arrive_time = System.currentTimeMillis();
		}

		try {
			// invocacion local para la creacion de un thread de orb
			eventCount(OP_INC, proxy);			
			proxy.reference.ipush(eventData);
			if (_time_debug) {
				long t2 = System.currentTimeMillis();

				TIME_INFO[1] = Long.toString(t2 - event_arrive_time);
				TIME_INFO[3] = Long.toString(event_arrive_time);
				TIME_INFO[5] = Long.toString(t2);
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO);
			}
		} catch (org.omg.CORBA.COMM_FAILURE ex) {
			// Descontamos el evento que no hemos encolado
			eventCount(OP_DEC, proxy);
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, IPUSH_COMM_FAILURE);
		} catch (Exception ex) {
			// Descontamos el evento que no hemos encolado
			eventCount(OP_DEC, proxy);
			TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, IPUSH_EXCEPTION);
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
		}
	}

	public void ipush(org.omg.CORBA.Any event) {
		ProxyPushConsumerData proxy = getData();
		//TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, IPUSH);

		long t1 = 0;
		if (_time_debug) {
			t1 = System.currentTimeMillis();
		}

		if (proxy.filterAdminDelegate!= null) {
			try {
				if (!proxy.filterAdminDelegate.match(event)) {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, DISCARD_EVENT);

					// Pongo aqui el decEventCount y no al principio de la rutina ya que si
					// se tratara del ultimo evento encolado y hubiera un cambio de thread
					// justo despues de decrementar la cuenta de eventos encolados y entra
					// a ejecutar el thread de un disconnec_push_consumer puedo encontrarme
					// que _forwarding_discriminator es null.
					if ((eventCount(OP_DEC, proxy) == 0L)
							&& (proxy.destroying == true)) {
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
		
		Policy event_priority = getEventPriority();
        
        if(event_priority != null) {
       
            Policy[] qos_policies = {event_priority};
           
            PolicyCurrent thread_policies = getThreadPolicyManager();
            
            try {
                thread_policies.set_policy_overrides(qos_policies, 
                                                     SetOverrideType.ADD_OVERRIDE);
            }
            catch (InvalidPolicies e1) {}
        }

		long t1_2 = 0;
		if (_time_debug) {
			t1_2 = System.currentTimeMillis();
		}

		if (proxy.operation_mode == OperationMode.distribution) {
			if (proxy.transforming_operator != null) {
				TIDNotifTrace.print(TIDNotifTrace.DEBUG, "TRANSFORMING_EVENT");
				try {
					event = proxy.transforming_operator.reference
							.transform_value(event);
				} catch (org.omg.TransformationInternalsAdmin.DataError ex1) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex1);
					handle_error(ex1.why, event, new CannotProceed("DataError"));

					// Pongo aqui el decEventCount y no al principio de la rutina ya que
					// si se tratara del ultimo evento encolado y hubiera un cambio de 
					// thread justo despues de decrementar la cuenta de eventos encolados
					// y entra a ejecutar el thread de un disconnec_push_consumer puedo 
					// encontrarme que _forwarding_discriminator es null.
					if ((eventCount(OP_DEC, proxy) == 0L)
							&& (proxy.destroying == true)) {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								" ### destroyDiscriminator() ###");
						destroyDiscriminator(proxy);
					}
					return;
				} catch (org.omg.TransformationInternalsAdmin.TransformationError ex2) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex2);
					handle_error(ex2.why, event, new CannotProceed(
							"TransformationError"));

					// Pongo aqui el decEventCount y no al principio de la rutina ya que
					// si se tratara del ultimo evento encolado y hubiera un cambio de 
					// thread justo despues de decrementar la cuenta de eventos encolados
					// y entra a ejecutar el thread de un disconnec_push_consumer puedo 
					// encontrarme que _forwarding_discriminator es null.
					if ((eventCount(OP_DEC, proxy) == 0L)
							&& (proxy.destroying == true)) {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								" ### destroyDiscriminator() ###");
						destroyDiscriminator(proxy);
					}
					return;
				} catch (org.omg.TransformationInternalsAdmin.ConnectionError ex3) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex3);
					handle_error("", event,
							new CannotProceed("ConnectionError"));

					// Pongo aqui el decEventCount y no al principio de la rutina ya que
					// si se tratara del ultimo evento encolado y hubiera un cambio de 
					// thread justo despues de decrementar la cuenta de eventos encolados
					// y entra a ejecutar el thread de un disconnec_push_consumer puedo 
					// encontrarme que _forwarding_discriminator es null.
					if ((eventCount(OP_DEC, proxy) == 0L)
							&& (proxy.destroying == true)) {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								" ### destroyDiscriminator() ###");
						destroyDiscriminator(proxy);
					}
					return;
				} catch (java.lang.Exception ex4) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex4);
					handle_error("", event, new CannotProceed("Error"));

					// Pongo aqui el decEventCount y no al principio de la rutina ya que
					// si se tratara del ultimo evento encolado y hubiera un cambio de 
					// thread justo despues de decrementar la cuenta de eventos encolados
					// y entra a ejecutar el thread de un disconnec_push_consumer puedo 
					// encontrarme que _forwarding_discriminator es null.
					if ((eventCount(OP_DEC, proxy) == 0L)
							&& (proxy.destroying == true)) {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								" ### destroyDiscriminator() ###");
						destroyDiscriminator(proxy);
					}
					return;
				}
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

			if (proxy.operation_mode == OperationMode.distribution) {
				TIME_INFO_4[1] = Long.toString(t2 - t1_2);
				TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, TIME_INFO_4);
			}
		}

		if (_time_debug) {
			t2 = t2 - t1;
			if (t2 > proxy.max_proccessing_time)
				proxy.max_proccessing_time = t2;
			proxy.total_proccessing_time = proxy.total_proccessing_time + t2;
		}

		if ((eventCount(OP_DEC, proxy) == 0L) && (proxy.destroying == true)) {
			// Lo elimino de la lista del SupplierAdmin
			proxy.supplierAdmin.removeProxyPushConsumer(proxy.name);

			// y lo desactivo del POA si no quedan enventos
			// por tratar en el POAManager de entrada.
			unregister(proxy);

			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().delete(proxy);
			}
			proxy.supplierAdmin = null;

			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					" &&& destroyDiscriminator() &&&");
			destroyDiscriminator(proxy);

			// Facilitamos la liberacion de memoria
			proxy.supplierAdmin = null;
			//proxy.id = null;
			proxy.reference = null;
		}
	}

	/**
     * @return
     */
    private Policy getEventPriority()
    {
        PriorityProperty priority = 
            getData().qosAdminDelegate.getPriorityProperty();
        
        if(priority != null) {
            return priority.getPolicy(_orb);
        } else {
            return null;
        }        
       
    }

    //-----------------------------------------------------------

	private void handle_error(String id, org.omg.CORBA.Any event,
			CannotProceed reason) {
		ProxyPushConsumerData proxy = getData();
		if (proxy.exception_handler != null) {
			proxy.exception_handler.handle_exception(id, event, reason);
		}
	}

    // Persistencia
    public void loadData() {
    	ProxyPushConsumerData proxy = getData();
    	TIDNotifTrace.print(TIDNotifTrace.USER,
    			"ProxyPushConsumerImpl.loadData()");
    	
    	if (proxy.operator_ir != null) {
    		org.omg.PortableServer.POA operator_poa;
    		if (_supplier_poa_policy == 0) // Create the Global POA
    		{
    			operator_poa = globalTransformingOperatorPOA();
    		} else if (_supplier_poa_policy == 1) // Create the Local POA
    		{
    			operator_poa = localTransformingOperatorPOA(proxy.channel_id);
    		} else // Create the Exclusive POA
    		{
    			operator_poa = exclusiveTransformingOperatorPOA(
    					proxy.channel_id, proxy.admin_id);
    		}
    
    		try {
    			proxy.transforming_operator = PersistenceManager.getDB()
    					.load_to(proxy.operator_ir);
    			proxy.transforming_operator.poa = operator_poa;
    			proxy.transforming_operator.reference = NotifReference
    					.iTransformingOperatorReference(operator_poa,
    							proxy.transforming_operator.id);
    			register_operator(proxy.transforming_operator, operator_poa);
    			proxy.operator_ir = null;
    		} catch (Exception ex) {
    			proxy.transforming_operator = null;
    			proxy.transforming_operator.reference = null;
    		}
    	}
    }
    

	//-----------------------------------------------------------

	synchronized protected long eventCount(int op, ProxyPushConsumerData proxy) {
		proxy.eventCount = proxy.eventCount + op;
		if (_time_debug) {
			if (op == OP_DEC)
				proxy.total_proccessed_events++;
		}
		return proxy.eventCount;
	}

	protected void destroyDiscriminator(ProxyPushConsumerData proxy) {
		DESTROY_DISCRIMINATOR[1] = proxy.name;
		DESTROY_DISCRIMINATOR[3] = proxy.id;
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, DESTROY_DISCRIMINATOR);

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

		// Destroyes the transformator.
		if (proxy.transforming_operator != null) {
			proxy.transforming_operator.reference.destroy();
			if (PersistenceManager.isActive()) {
				PersistenceManager.getDB().delete(proxy.transforming_operator);
			}
			proxy.transforming_operator = null;
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
		/*
		 ProxyPushConsumerData data.getData();

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

	
	/*
	 private Discriminator discriminatorReference(
	 org.omg.PortableServer.POA the_poa, String name )
	 {
	 try
	 {
	 org.omg.CORBA.Object discriminator = the_poa.create_reference_with_id(
	 name.getBytes(), DiscriminatorHelper.id() );
	 return DiscriminatorHelper.narrow(discriminator);
	 }
	 catch (org.omg.PortableServer.POAPackage.WrongPolicy ex)
	 {
	 TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
	 throw new org.omg.CORBA.INTERNAL();
	 }
	 }
	 */

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

	private void register_discriminator(FilterData data,
			org.omg.PortableServer.POA poa) {
		try {
			FilterImpl servant = (FilterImpl) poa.get_servant();
			servant.register(data);
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

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyPushConsumerOperations#connect_any_push_supplier(org.omg.CosEventComm.PushSupplier)
	 */
	public void connect_any_push_supplier(PushSupplier push_supplier) throws AlreadyConnected {
		this.connect_push_supplier( push_supplier );
	}
	
	/* (non-Javadoc)
     * @see org.omg.CosNotifyChannelAdmin.StructuredProxyPushConsumerOperations#connect_structured_push_supplier(org.omg.CosNotifyComm.StructuredPushSupplier)
     */
    public void connect_structured_push_supplier(StructuredPushSupplier push_supplier)
        throws AlreadyConnected
    {
        connectPushSupplier(push_supplier);
        
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
	/*
	 * @see org.omg.CosNotifyChannelAdmin.SupplierAdminOperations#MyChannel()
	 */
	public EventChannel MyChannel() {
		return this.getData().notificationChannel;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyConsumerOperations#obtain_subscription_types(org.omg.CosNotifyChannelAdmin.ObtainInfoMode)
	 */
	public _EventType[] obtain_subscription_types(ObtainInfoMode mode) {
		//TODO: NO_IMPLEMENT
		return new _EventType[ 0 ];
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.ProxyConsumerOperations#validate_event_qos(org.omg.CosNotification.Property[], org.omg.CosNotification.NamedPropertyRangeSeqHolder)
	 */
	public void validate_event_qos(Property[] required_qos, NamedPropertyRangeSeqHolder available_qos) 
	throws UnsupportedQoS 
	{		
	    EventQoSAdmin admin = new EventQoSAdmin(_orb);
	    admin.validate_qos(required_qos, available_qos);		
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
	    ProxyPushConsumerData data = this.getData();
	    data.qosAdminDelegate.set_qos( qos );
		Policy[] policies = 
		    data.qosAdminDelegate.getPolicies();
		
		data.reference = 
		    InternalProxyPushConsumerHelper.narrow 
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
	 * @see org.omg.CosNotifyComm.NotifyPublishOperations#offer_change(org.omg.CosNotification._EventType[], org.omg.CosNotification._EventType[])
	 */
	public void offer_change(_EventType[] added, _EventType[] removed) throws InvalidEventType {
		//TODO: NO_IMPLEMENT
		throw new NO_IMPLEMENT();
	}

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPushConsumerOperations#push_structured_event(org.omg.CosNotification.StructuredEvent)
     */
    public void push_structured_event(StructuredEvent notification)
        throws Disconnected
    { 
        if(notification.header.variable_header.length > 0) {
            Vector policies = new Vector();
            
            ProxyPushConsumerData data = getData();
            
            EventQoSAdmin admin = new EventQoSAdmin(_orb);
            
            try {
                admin.set_qos(notification.header.variable_header);
            }
            catch (UnsupportedQoS e) {}
            
            StartTimeProperty start_time = admin.getStartTimeProperty();
            
            if(start_time != null) {
                StartTimeSupportedProperty start_supported = 
                    data.qosAdminDelegate.getStartTimeSupportedProperty();
                
               if(start_supported != null && start_supported.getValue()) {
                    policies.add(start_time.getPolicy(_orb));
                }            
            }
            
            StopTimeProperty stop_time = admin.getStopTimeProperty();
            
            if(stop_time != null) {
                StopTimeSupportedProperty stop_supported = 
                    data.qosAdminDelegate.getStopTimeSupportedProperty();
                
                if(stop_supported != null && stop_supported.getValue()) {
                    policies.add(stop_time.getPolicy(_orb));
                }            
            }
            
            if(policies.size() > 0) {
                Policy[] qos_policies = new Policy[policies.size()];
                policies.toArray(qos_policies);
                
                PolicyCurrent thread_policies = getThreadPolicyManager();
                
                try {
                    thread_policies.set_policy_overrides(qos_policies, 
                                                         SetOverrideType.ADD_OVERRIDE);
                }
                catch (InvalidPolicies e1) {}
            }
        }
        
        Any event = _orb.create_any();
        StructuredEventHelper.insert(event, notification);
        push(event);
        
    }
    
    protected PolicyCurrent getThreadPolicyManager()
    {
        Object obj;
        try {
            obj = _orb.resolve_initial_references("PolicyCurrent");
            return PolicyCurrentHelper.narrow(obj);
        }
        catch (InvalidName e) { throw new INTERNAL();}
        
    }

    /* (non-Javadoc)
     * @see org.omg.CosNotifyComm.StructuredPushConsumerOperations#disconnect_structured_push_consumer()
     */
    public void disconnect_structured_push_consumer()
    {
        this.disconnect_push_consumer();
        
    }

    
	
	
	
}
