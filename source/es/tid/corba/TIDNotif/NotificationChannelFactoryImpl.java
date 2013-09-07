/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 77 $
* Date: $Date: 2008-04-11 12:50:51 +0200 (Fri, 11 Apr 2008) $
* Last modified by: $Author: avega $
*
* (C) Copyright 2005 Telef√≥nica Investigaci√≥n y Desarrollo
*     S.A.Unipersonal (Telef√≥nica I+D)
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

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.IntHolder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.UnsupportedAdmin;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotifyChannelAdmin.ChannelNotFound;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.NotificationChannelAdmin.NotificationChannel;
import org.omg.ServiceManagement.OperationMode;

import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;

//import es.tid.corba.TIDNotif.data.ChannelData;

public class NotificationChannelFactoryImpl
		extends
		org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelFactoryPOA
		implements NotificationChannelFactoryMsg {
	
	private org.omg.CORBA.ORB _orb;

	// POA del Notification Service
	private org.omg.PortableServer.POA _agentPOA;

	// POA global comun para todos los Notification Channels
	private org.omg.PortableServer.POA _globalChannelPOA = null;

	// Interpreter and poa are to be passed to the channels.
	//private TIDParser _grammarInterpreter;

	private boolean _destroying;

	private org.omg.ServiceManagement.OperationMode _operation_mode;

	// Controla si se crea un POA interno comun, con su pool de threads, para los
	// canales de eventos
	private int _channel_poa_policy;

	private int _supplier_poa_policy;

	private int _consumer_poa_policy;

	private DataTable _NotificationChannelsTable;

	// Objeto creador de discriminantes
	//private static DiscriminatorFactory _discriminatorFactory = null;

	// Objeto creador de operators
	//private static OperatorFactory _operatorFactory = null;

	// Constructor.
	public NotificationChannelFactoryImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA poa, OperationMode operation_mode) {
		_orb = orb;
		_agentPOA = poa;
		_operation_mode = operation_mode;
		_destroying = false;

		// Politicas de POA's
		_channel_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.CHANNEL_POA_KEY);
		_supplier_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.SUPPLIER_POA_KEY);
		_consumer_poa_policy = TIDNotifConfig
				.getInt(TIDNotifConfig.CONSUMER_POA_KEY);

		// podemos ponerle otro parametro si definimos la persistencia de los
		// canales en algun medio
		//_grammarInterpreter = TIDParser.createInterpreter();
		TIDParser.init(TIDNotifTrace.getTrace());

		//_discriminatorFactory = new DiscriminatorFactory();
		//_operatorFactory = new OperatorFactory(_orb);

		// Constructs a new, empty hashtable with a default capacity and load
		// factor, which is 0.75.
		_NotificationChannelsTable = new DataTable();
	}

	public org.omg.NotificationChannelAdmin.NotificationChannel create_notification_channel(
			int default_priority, int default_event_lifetime) {
		CREATE_CHANNEL[1] = Integer.toString(default_priority);
		CREATE_CHANNEL[3] = Integer.toString(default_event_lifetime);
		TIDNotifTrace.print(TIDNotifTrace.USER, CREATE_CHANNEL);

		if (_operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_D);
		}

		if ((default_priority < 0) || (default_event_lifetime <= 0)) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		// Get Channel ID
		String channel_id;
		String filter_factory_id;
        try {
            channel_id = PersistenceManager.getDB().getUID();
            filter_factory_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
       

		org.omg.PortableServer.POA channel_poa;
		if (_channel_poa_policy == 0) // Create the Global POA used by the channels
		{
			channel_poa = globalChannelPOA();
		} else // Create the Local POA used by this channel
		{
			channel_poa = localChannelPOA(channel_id);
		}

		NotificationChannelData channel = 
		    new NotificationChannelData(
				channel_id,				
				channel_poa, 
				OperationMode.notification,
				default_priority, 
				default_event_lifetime, 
				this._orb );

		register_channel(channel, channel_poa);

		// AÒadimos el canal a la tabla de canales
		synchronized (_NotificationChannelsTable) {
			_NotificationChannelsTable.put(channel_id, channel);
		}

		debugChannelInfo(channel_id, channel_poa, default_priority,
				default_event_lifetime, OperationMode.notification);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(channel);
		}

		return NotifReference.notifChannelReference(channel_poa, channel.id);
	}

	public org.omg.NotificationChannelAdmin.NotificationChannel new_create_notification_channel(
			String channel_name, int default_priority,
			int default_event_lifetime)
			throws org.omg.NotificationChannelAdmin.ChannelAlreadyExist {
		CREATE_CHANNEL[1] = Integer.toString(default_priority);
		CREATE_CHANNEL[3] = Integer.toString(default_event_lifetime);
		TIDNotifTrace.print(TIDNotifTrace.USER, CREATE_CHANNEL);

		if (_operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_D);
		}

		if ((default_priority < 0) || (default_event_lifetime <= 0)) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		NotificationChannelData channel;
		org.omg.PortableServer.POA channel_poa;
		synchronized (_NotificationChannelsTable) {
			if (_NotificationChannelsTable.containsKey(channel_name)) {
				throw new org.omg.NotificationChannelAdmin.ChannelAlreadyExist();
			}

			String channel_id = new String(channel_name);
			if (_channel_poa_policy == 0) //Create the Global POA used by the channels
			{
				channel_poa = globalChannelPOA();
			} else // Create the Local POA used by this channel
			{
				channel_poa = localChannelPOA(channel_id);
			}

			channel = new NotificationChannelData(channel_id, channel_poa,
					OperationMode.notification, default_priority,
					default_event_lifetime, this._orb );

			register_channel(channel, channel_poa);
			_NotificationChannelsTable.put(channel_id, channel);

			debugChannelInfo(channel_id, channel_poa, default_priority,
					default_event_lifetime, OperationMode.notification);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(channel);
		}

		return NotifReference.notifChannelReference(channel_poa, channel.id);
	}

	public org.omg.DistributionChannelAdmin.DistributionChannel create_distribution_channel(
			int default_priority, int default_event_lifetime,
			OperationMode operation_mode) {
		CREATE_DCHANNEL[1] = Integer.toString(default_priority);
		CREATE_DCHANNEL[3] = Integer.toString(default_event_lifetime);
		TIDNotifTrace.print(TIDNotifTrace.USER, CREATE_DCHANNEL);

		if (_operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_N);
		}

		String channel_id;
        try {
            channel_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        org.omg.PortableServer.POA channel_poa;
		if (_channel_poa_policy == 0) // Create the Global POA used by the channels 
		{
			channel_poa = globalChannelPOA();
		} else // Create the Local POA used by this channel
		{
			channel_poa = localChannelPOA(channel_id);
		}

		NotificationChannelData channel = new NotificationChannelData(
				channel_id, channel_poa, operation_mode, default_priority,
				default_event_lifetime, this._orb);

		register_channel(channel, channel_poa);
		synchronized (_NotificationChannelsTable) {
			_NotificationChannelsTable.put(channel_id, channel);
		}

		debugChannelInfo(channel_id, channel_poa, default_priority,
				default_event_lifetime, operation_mode);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(channel);
		}

		return NotifReference.distribChannelReference(channel_poa, channel.id);
	}

	public org.omg.DistributionChannelAdmin.DistributionChannel new_create_distribution_channel(
			String channel_name, int default_priority,
			int default_event_lifetime, OperationMode operation_mode)
			throws org.omg.DistributionChannelAdmin.ChannelAlreadyExist {
		CREATE_DCHANNEL[1] = Integer.toString(default_priority);
		CREATE_DCHANNEL[3] = Integer.toString(default_event_lifetime);
		TIDNotifTrace.print(TIDNotifTrace.USER, CREATE_DCHANNEL);

		if (_operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_N);
		}

		if ((default_priority < 0) || (default_event_lifetime <= 0)) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		NotificationChannelData channel;
		org.omg.PortableServer.POA channel_poa;
		synchronized (_NotificationChannelsTable) {
			if (_NotificationChannelsTable.containsKey(channel_name)) {
				throw new org.omg.DistributionChannelAdmin.ChannelAlreadyExist();
			}

			String channel_id = new String(channel_name);
			if (_channel_poa_policy == 0) // Create the Global POA used by the channels
			{
				channel_poa = globalChannelPOA();
			} else // Create the Local POA used by this channel
			{
				channel_poa = localChannelPOA(channel_id);
			}

			channel = new NotificationChannelData(channel_id, channel_poa,
					operation_mode, default_priority, default_event_lifetime, this._orb);

			register_channel(channel, channel_poa);
			_NotificationChannelsTable.put(channel_id, channel);

			debugChannelInfo(channel_id, channel_poa, default_priority,
					default_event_lifetime, operation_mode);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(channel);
		}

		return NotifReference.distribChannelReference(channel_poa, channel.id);
	}

	public org.omg.NotificationChannelAdmin.NotificationChannel get_notification_channel(
			String channel_name)
			throws org.omg.NotificationChannelAdmin.ChannelNotFound {
		if (_operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_D);
		}
		NotificationChannelData data = (NotificationChannelData) _NotificationChannelsTable
				.get(channel_name);
		if (data == null) {
			throw new org.omg.NotificationChannelAdmin.ChannelNotFound();
		}
		return NotifReference.notifChannelReference(data.poa, data.id);
	}

	public org.omg.DistributionChannelAdmin.DistributionChannel get_distribution_channel(
			String channel_name)
			throws org.omg.DistributionChannelAdmin.ChannelNotFound {
		if (_operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_N);
		}

		NotificationChannelData data = (NotificationChannelData) _NotificationChannelsTable
				.get(channel_name);
		if (data == null) {
			throw new org.omg.DistributionChannelAdmin.ChannelNotFound();
		}
		return NotifReference.distribChannelReference(data.poa, data.id);
	}

	// Returns a list with the channels.
	public org.omg.NotificationChannelAdmin.NotificationChannel[] channels() {
		TIDNotifTrace.print(TIDNotifTrace.USER, CHANNELS);

		if (_operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_D);
		}

		java.util.Vector chtmp = _NotificationChannelsTable.values();

		int i = 0;
		org.omg.NotificationChannelAdmin.NotificationChannel[] _channels = new org.omg.NotificationChannelAdmin.NotificationChannel[chtmp
				.size()];
		for (java.util.Enumeration e = chtmp.elements(); e.hasMoreElements();) {
			NotificationChannelData data = (NotificationChannelData) e
					.nextElement();
			_channels[i++] = NotifReference.notifChannelReference(data.poa,
					data.id);
		}
		chtmp.removeAllElements();
		chtmp = null;
		return _channels;
	}

	// Returns a list with the channels name.
	public String[] new_channels() {
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_CHANNELS);

		if (_operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_D);
		}

		java.util.Vector chtmp = _NotificationChannelsTable.keys();

		int i = 0;
		String[] _channels = new String[chtmp.size()];
		for (java.util.Enumeration e = chtmp.elements(); e.hasMoreElements();) {
			_channels[i++] = (String) e.nextElement();
		}
		chtmp.removeAllElements();
		chtmp = null;
		return _channels;
	}

	// Returns a list with the channels.
	public org.omg.DistributionChannelAdmin.DistributionChannel[] distribution_channels() {
		TIDNotifTrace.print(TIDNotifTrace.USER, CHANNELS);

		if (_operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_N);
		}

		java.util.Vector chtmp = _NotificationChannelsTable.values();

		int i = 0;
		org.omg.DistributionChannelAdmin.DistributionChannel[] _channels = new org.omg.DistributionChannelAdmin.DistributionChannel[chtmp
				.size()];
		for (java.util.Enumeration e = chtmp.elements(); e.hasMoreElements();) {
			NotificationChannelData data = (NotificationChannelData) e
					.nextElement();
			_channels[i++] = NotifReference.distribChannelReference(data.poa,
					data.id);
		}
		chtmp.removeAllElements();
		chtmp = null;
		return _channels;
	}

	// Returns a list with the channels.
	public String[] new_distribution_channels() {
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_CHANNELS);

		if (_operation_mode != OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_N);
		}

		java.util.Vector chtmp = _NotificationChannelsTable.keys();

		int i = 0;
		String[] _channels = new String[chtmp.size()];
		for (java.util.Enumeration e = chtmp.elements(); e.hasMoreElements();) {
			_channels[i++] = (String) e.nextElement();
		}
		chtmp.removeAllElements();
		chtmp = null;
		return _channels;
	}

	// TODO
	//  protected void close()
	//  {
	// Romper la referencia circular con el padre (_tidNotifServer = null;)
	// Vaciar la tabla de Channels (_NotificationChannelsTable.clear();)
	//  }

	synchronized public void destroyFromAdmin(boolean remain_objects) {
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, DESTROY_FROM_ADMIN);

		if (_destroying)
			return;
		_destroying = true;

		if (!remain_objects) {
			synchronized (_NotificationChannelsTable) {
				for (java.util.Iterator e = _NotificationChannelsTable.values()
						.iterator(); e.hasNext();)

				{
					((NotificationChannelData) e.next()).reference.destroy();
				}
			}
		}
		_NotificationChannelsTable.clear();
		destroyGlobalChannelPOA();
	}

	public void removeNotificationChannel(String cId) {
		// Remove the channel id from the list
		_NotificationChannelsTable.remove(cId);
	}

	public void removeDistributionChannel(String cId) {
		// Remove the channel id from the list
		_NotificationChannelsTable.remove(cId);
	}

	private void debugChannelInfo(String id,
			org.omg.PortableServer.POA the_poa, int priority,
			int event_lifetime, OperationMode operation_mode) {
		// NOTIFICATION
		NEW_CHANNEL[1] = id;
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_CHANNEL);

		CHANNEL_INFO_1[1] = the_poa.toString();
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, CHANNEL_INFO_1);

		CHANNEL_INFO_2[1] = Integer.toString(priority);
		CHANNEL_INFO_2[3] = Integer.toString(event_lifetime);

		switch (operation_mode.value()) {
		case OperationMode._notification:
			CHANNEL_INFO_2[5] = "NOTIFICATION";
			break;
		case OperationMode._distribution:
			CHANNEL_INFO_2[5] = "DISTRIBUTION";
			break;
		case OperationMode._checked_notification:
			CHANNEL_INFO_2[5] = "CHECKED NOTIFICATION";
			break;
		default:
			CHANNEL_INFO_2[5] = "UNKNOWN";
		}
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, CHANNEL_INFO_2);
	}

	public void loadChannels() {
		java.util.ArrayList channels = PersistenceManager.getDB()
				.load_channels();

		for (java.util.Iterator e = channels.iterator(); e.hasNext();) {
			NotificationChannelData channel = (NotificationChannelData) e
					.next();

			if (_channel_poa_policy == 0) //Create the Global POA used by the channels
			{
				channel.poa = globalChannelPOA();
			} else // Create the Local POA used by this channel
			{
				channel.poa = localChannelPOA(channel.id);
			}

			channel.reference = NotifReference.internalChannelReference(
					channel.poa, channel.id,
					channel.qosAdminDelegate.getPolicies());

			register_channel(channel, channel.poa);
			_NotificationChannelsTable.put(channel.id, channel);

			debugChannelInfo(channel.id, channel.poa, channel.default_priority,
					channel.default_event_lifetime, channel.operation_mode);

			channel.reference.loadData();
		}
		channels.clear();
	}

	private void register_channel(NotificationChannelData data,
			org.omg.PortableServer.POA poa) {
		try {
			((NotificationChannelImpl) poa.get_servant()).register(data);
		} catch (Exception e) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, REGISTER_EXCEPTION);
		}
	}

	private org.omg.PortableServer.POA globalChannelPOA() {
		org.omg.PortableServer.POA the_poa = ThePOAFactory
				.getGlobalPOA(GLOBAL_CHANNEL_POA_ID);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(GLOBAL_CHANNEL_POA_ID,
					ThePOAFactory.CHANNEL_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(
						new NotificationChannelImpl(
								_orb,
								_agentPOA, 
								this)
					);
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private void destroyGlobalChannelPOA() {
		if (_globalChannelPOA != null) {
			TIDNotifTrace
					.print(TIDNotifTrace.DEBUG, DESTROY_GLOBAL_CHANNEL_POA);
			try {
				ThePOAFactory.destroy(_globalChannelPOA,
						ThePOAFactory._COMMON_POA);
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
			_globalChannelPOA = null;
		}
	}

	protected org.omg.PortableServer.POA localChannelPOA(String channel_id) {
		// Creation de Channel POA comun
		String poa_id = LOCAL_CHANNEL_POA_ID + channel_id;

		org.omg.PortableServer.POA the_poa = ThePOAFactory.getLocalPOA(poa_id);
		if (the_poa == null) {
			the_poa = ThePOAFactory.createLocalPOA(poa_id,
					ThePOAFactory.CHANNEL_POA, null);
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(
						new NotificationChannelImpl(
							_orb,
							_agentPOA, 
							this)
					);
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelFactoryOperations#create_channel(org.omg.CosNotification.Property[], org.omg.CosNotification.Property[], org.omg.CORBA.IntHolder)
	 */
	public  EventChannel create_channel(Property[] initial_qos, Property[] initial_admin, IntHolder id) throws UnsupportedQoS, UnsupportedAdmin {
		int default_priority       = 1;
		int default_event_lifetime = 1;
		
		CREATE_CHANNEL[1] = Integer.toString(default_priority);
		CREATE_CHANNEL[3] = Integer.toString(default_event_lifetime);
		TIDNotifTrace.print(TIDNotifTrace.USER, CREATE_CHANNEL);

		if (_operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_D);
		}

		if ((default_priority < 0) || (default_event_lifetime <= 0)) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		// Get Channel ID
		String channel_id;
        try {
            channel_id = PersistenceManager.getDB().getUID();
        }
        catch (Exception e) {
            throw new INTERNAL(e.toString());
        }
        org.omg.PortableServer.POA channel_poa;
		if (_channel_poa_policy == 0) // Create the Global POA used by the channels
		{
			channel_poa = globalChannelPOA();
		} else // Create the Local POA used by this channel
		{
			channel_poa = localChannelPOA(channel_id);
		}

		NotificationChannelData channel = new NotificationChannelData(
				channel_id, channel_poa, OperationMode.notification,
				default_priority, default_event_lifetime, this._orb );

		register_channel(channel, channel_poa);

		// AÒadimos el canal a la tabla de canales
		synchronized (_NotificationChannelsTable) {
			_NotificationChannelsTable.put(channel_id, channel);
		}

		debugChannelInfo(channel_id, channel_poa, default_priority,
				default_event_lifetime, OperationMode.notification);

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save(channel);
		}

		try {
			id.value = Integer.decode( channel.id ).intValue();			
		} catch ( NumberFormatException nfe ){
			throw new INTERNAL( "Invalid identifier, not an int" );
		}
		
		NotificationChannel notif = 
		    NotifReference.notifChannelReference(channel_poa, channel.id);
		
		notif.set_qos(initial_qos);

                try {
                    notif.set_admin(initial_admin);
                } catch (NO_IMPLEMENT ne) {
                    TIDNotifTrace.print(TIDNotifTrace.USER, 
                                        "AdminProperties are not supported by the moment!");
                }
		
		return notif;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelFactoryOperations#get_all_channels()
	 */
	public int[] get_all_channels() {
		int[]    intIds;
		String[] stringIds; 
		stringIds = this.new_channels();
		intIds = new int[ stringIds.length ];
		for ( int i=0; i < stringIds.length; i++ ){
			try {
				intIds[ i ] = Integer.parseInt( stringIds[ i ] );
			} catch ( NumberFormatException nfe ){
				throw new INTERNAL( "Invalid identifier. Not an int" );
			}
		}
		return intIds;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyChannelAdmin.EventChannelFactoryOperations#get_event_channel(int)
	 */
	public EventChannel get_event_channel(int id) throws ChannelNotFound {
		if (_operation_mode == OperationMode.distribution) {
			throw new org.omg.CORBA.BAD_OPERATION(B_O__NOT_ALLOWED_D);
		}
		NotificationChannelData data = (NotificationChannelData) _NotificationChannelsTable
				.get( String.valueOf( id ) );
		if (data == null) {
			throw new ChannelNotFound();
		}
		return NotifReference.notifChannelReference(data.poa, data.id);
	}


}
