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

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

import org.omg.NotificationService.*;
import org.omg.NotificationService.NotificationAdminPackage.CannotProceed;

import org.omg.ServiceManagement.OperationMode;

import es.tid.corba.TIDNotif.ThePOAFactory;
import es.tid.corba.TIDNotif.NotificationChannelFactoryImpl;

import es.tid.corba.TIDNotif.PersistenceManager;

import es.tid.TIDorbj.core.TIDORB;

public class ServiceManager implements NotificationAdminOperations, ServiceManagerMsg
{
  private static final String ORB_PORT = "es.tid.TIDorbj.iiop.port";
  private static final String version = "TIDNotifJ 2.0.3 [2008.10.05.10.00]";

  /**
   * Service ORB.
   */
  private static org.omg.CORBA.ORB _orb;

  // Referencia a la Fabrica de Objetos Notification Channels
  static NotificationChannelFactoryImpl factory = null;

  private static TIDNotifConfig _notifConfig = null;

  private static ServiceManager _notifServer = null;

  private static org.omg.PortableServer.POA _tidNotifPOA = null;

  private static NotificationAdminImpl _notificationAdmin = null;

  /**
   * Private Constructor
   */
  private ServiceManager( org.omg.CORBA.ORB orb ) throws CannotProceed
  {   
    _orb = orb;

    org.omg.PortableServer.POA _poa = ThePOAFactory.rootPOA(_orb);

    // Info sobre el _poa (rootPOA)
    ROOT_POA[1] = _poa.toString();
    ROOT_POA[3] = _poa.the_POAManager().toString();
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, ROOT_POA);

    // Creamos el POA del Servicio
    String poaId = new String(NOTIFSERVERPOA_ID +
                            TIDNotifConfig.get(TIDNotifConfig.ADMIN_PORT_KEY));

    _tidNotifPOA = ThePOAFactory.newPOA(
                poaId, _poa, ThePOAFactory.CREATE_MANAGER, ThePOAFactory._AGENT_POA);

    // NOTIFICATION
    ThePOAFactory.debugPoaInfo(poaId, _tidNotifPOA, _poa);

    try
    {
      // Activate the POA by its manager way
      _tidNotifPOA.the_POAManager().activate();
    }
    catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      throw new CannotProceed("ServiceManager AdapterInactive");
    }
  }

  protected static NotificationAdminImpl createNotificationAdmin(
                                                org.omg.CORBA.ORB orb, 
                                                org.omg.PortableServer.POA poa, 
                                                ServiceManager manager )
                                                           throws CannotProceed
  {
    NotificationAdminImpl admin;

    try 
    {
      // Create the servant for the NotificationAdmin Agent
      admin = new NotificationAdminImpl(orb, manager);

      // Activate the servant into the POA with the 
      // ID "org.omg.NotificationService.NotificationAdmin"
      poa.activate_object_with_id(NOTIFICANTIONADMIN_ID.getBytes(), admin);
    }			
    catch ( java.lang.Exception ex )
    {			
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex );
      throw new CannotProceed("ServiceManager Activate Object Exception");
    }            

    // NOTIFICATION
    TIDNotifTrace.print(TIDNotifTrace.USER, NEW_ADMIN);
    ADMIN_POA[1] = poa.toString();
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, ADMIN_POA);

    return admin;
  }


  //*************************************************************************
  //
  // Metodos para usar la clase ServiceManager como modulo
  //
  //
  //*************************************************************************

  /**
   * Service initialitation.
   */
  synchronized
  public static org.omg.NotificationService.NotificationAdmin 
                                   init( org.omg.CORBA.ORB orb,
                                         java.lang.String[] args, 
                                         java.util.Properties props )
                                                           throws CannotProceed
  {
    if ( _notifServer != null )
    {
      throw new CannotProceed("ServiceManager already exists");
    }

    java.util.Properties persistent_changes = null;

    for (int i = 0; i<args.length; i++)
    {
      if (ORB_PORT.compareTo(args[i]) == 0)
      {
        persistent_changes = new java.util.Properties();
        persistent_changes.setProperty( TIDNotifConfig.ADMIN_PORT_KEY,
                                        args[++i] );
        break;
      }
    }

    // Cargamos las Properties y las actualizamos con las properties internas
    // en programacion (siempre las mismas)
    _notifConfig = TIDNotifConfig.init(props);

    if (persistent_changes != null)
    {
      TIDNotifConfig.updateConfig(persistent_changes);
    }

    try
    {
      // De existir properties salvadas a disco se cargan
      TIDNotifConfig.restore();
    } catch (java.io.IOException ex) {};

    // Actualizamos las Properties con los parametros
    TIDNotifConfig.updateConfig(args);

    // Inicializamos las trazas del servicio y las trazas globales
    TIDNotifTrace.init();
    TIDNotifTrace.print(TIDNotifTrace.USER, version);
    es.tid.util.trace.Trace.global = TIDNotifTrace.getTrace();

    _notifServer = new ServiceManager(orb);

    // Creamos el Agente del Servicio
    _notificationAdmin =
                    createNotificationAdmin( orb, _tidNotifPOA, _notifServer );

    String persDB = TIDNotifConfig.get(TIDNotifConfig.PERS_DB_KEY);
    String persID = TIDNotifConfig.get(TIDNotifConfig.ADMIN_PORT_KEY);

    try
    {
      PersistenceManager.init(persDB, persID, orb);
    }
    catch (java.lang.Exception ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
    }

    if (PersistenceManager.isActive())
    {
        // Volcamos los parametros a discos
        try {
            TIDNotifConfig.dump();
        } catch (java.io.IOException ex) {};

      System.err.print("\nLoading persistent data...");
      if ( !create_channel_factory() )
      {
        System.exit(-1);
      }
      loadAll();
      System.err.println(" loaded!");
    }
    return _notificationAdmin._this();
  }


  public void trace_on(org.omg.NotificationService.TraceLevel level)
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, TRACE_ON);
    TIDNotifTrace.setLevel(level.value());
  }

  public void trace_off()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, TRACE_OFF);
    TIDNotifTrace.setLevel(TIDNotifTrace.NONE);
  }

  // The first time the factory must be created.
  synchronized
  public org.omg.NotificationChannelAdmin.NotificationChannelFactory
                                                              channel_factory()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, CHANNEL_FACTORY);

    if ( factory == null )
    {
      if ( !create_channel_factory() )
      {
        System.exit(-1);
      }
    }
    return (org.omg.NotificationChannelAdmin.NotificationChannelFactory)
                                                               factory._this();
  }

  // Returns a list with the channels.
  public org.omg.NotificationChannelAdmin.NotificationChannel[] list_channels()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, LIST_CHANNELS);

    if ( factory == null )
    {
      return new org.omg.NotificationChannelAdmin.NotificationChannel[0];
    }
    return factory.channels();
  }

  // Returns a list with the channels.
  public String[] list_channels_id()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, LIST_CHANNELS);

    if ( factory == null )
    {
      return new String[0];
    }
    return factory.new_channels();
  }


  // The ServerProcessObject performs the shutdown.
  synchronized
  public void shutdown_service()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, SHUTDOWN);
    if (factory != null)
    {
      factory.destroyFromAdmin(true);
    }

    if (_tidNotifPOA != null)
    {
      try
      {
        ThePOAFactory.destroy( _tidNotifPOA, ThePOAFactory._AGENT_POA);
      }
      catch (java.lang.Exception ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }

  // The ServerProcessObject performs the shutdown.
  synchronized
  public void shutdown()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, SHUTDOWN);

    if (factory != null)
    {
      factory.destroyFromAdmin(true);
    }

    if (_tidNotifPOA != null)
    {
      try
      {
        //_tidNotifPOA.the_POAManager().deactivate(true, false);
        ThePOAFactory.destroy( _tidNotifPOA, ThePOAFactory._AGENT_POA);
      }
      catch (java.lang.Exception ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }


  static private boolean create_channel_factory()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, CREATE_CHANNEL_FACTORY);

    // Create the Notification Channel Factory
    factory = new NotificationChannelFactoryImpl(
                              _orb, _tidNotifPOA, OperationMode.notification );
    try 
    {
      _tidNotifPOA.activate_object_with_id( CHANNELFACTORY_ID.getBytes(),
                                            factory );
    }
    catch (java.lang.Exception ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      return false;
    }

    // NOTIFICATION
    TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FACTORY);
    FACTORY_POA[1] = _tidNotifPOA.toString();
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, FACTORY_POA);


    //
    // Register initial reference to NotificationService in ORB services table
    //    
    org.omg.CORBA.Object factory_object = null;
    try { 
        factory_object = 
            _tidNotifPOA.id_to_reference(CHANNELFACTORY_ID.getBytes());
        ((es.tid.TIDorbj.core.TIDORB)_orb).setORBservice("NotificationService", 
                                                         factory_object);
    }  catch (java.lang.Exception ex) {
        return true;   
    }
    
    return true;
  }

  static private void loadAll()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, LOAD_ALL);

    if (factory != null)
    {
      factory.loadChannels();
    }
  }
}
