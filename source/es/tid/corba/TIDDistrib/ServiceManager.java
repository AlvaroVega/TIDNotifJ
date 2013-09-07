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

package es.tid.corba.TIDDistrib;

import org.omg.ServiceManagement.OperationMode;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

import es.tid.corba.TIDDistribAdmin.*;
import es.tid.corba.TIDDistribAdmin.AgentPackage.CannotProceed;

import es.tid.corba.TIDNotif.ThePOAFactory;
import es.tid.corba.TIDNotif.NotificationChannelFactoryImpl;

import es.tid.corba.TIDNotif.PersistenceManager;

public class ServiceManager implements AgentOperations, ServiceManagerMsg
{
  private static final String ORB_PORT = "es.tid.TIDorbj.iiop.port";
  private static final String version = "TIDNotifJ 1.3.4.2 [2004.23.07.14.00]";

  /**
   * Service ORB.
   */
  private static org.omg.CORBA.ORB _orb;

  // Referencia a la Fabrica de Objetos Distribution Channels
  static NotificationChannelFactoryImpl factory = null;

  private static TIDNotifConfig _notifConfig = null;

  private static ServiceManager _notifServer = null;

  private static org.omg.PortableServer.POA _tidDistribPOA = null;

  private static AgentImpl _notificationAdmin = null;

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
    String poaId = new String(DISTRIBSERVERPOA_ID +
                           TIDNotifConfig.get(TIDNotifConfig.ADMIN_PORT_KEY));

    _tidDistribPOA = ThePOAFactory.newPOA(
          poaId, _poa, ThePOAFactory.CREATE_MANAGER, ThePOAFactory._AGENT_POA);

    // NOTIFICATION
    debugPoaInfo(poaId, _tidDistribPOA, _poa);

    try
    {
      // Activate the POA by its manager way
      _tidDistribPOA.the_POAManager().activate();
    }
    catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      throw new CannotProceed("ServiceManager AdapterInactive");
    }
  }

  protected static AgentImpl createAgent( org.omg.CORBA.ORB orb, 
                                          org.omg.PortableServer.POA poa, 
                                          ServiceManager manager )
                                                           throws CannotProceed
  {
    AgentImpl agent = null;

    try 
    {
      // Create the servant for the Agent Agent
      agent = new AgentImpl(orb, manager);

      // Activate the servant into the POA with the 
      // ID "es.tid.corba.TIDDistribAdmin.Agent"
      poa.activate_object_with_id(AGENT_ID.getBytes(), agent);
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

    return agent;
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
  public static es.tid.corba.TIDDistribAdmin.Agent 
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
        persistent_changes.setProperty(TIDNotifConfig.ADMIN_PORT_KEY,args[++i]);
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

    if (persistent_changes != null)
    {
      TIDNotifConfig.updateConfig(persistent_changes);
    }

    // Actualizamos las Properties con los parametros
    TIDNotifConfig.updateConfig(args);

    // Inicializamos las trazas del servicio y las trazas globales
    TIDNotifTrace.init();
    TIDNotifTrace.print(TIDNotifTrace.USER, version);
    es.tid.util.trace.Trace.global = TIDNotifTrace.getTrace();

    // Volcamos los parametros a discos
    try
    {
      TIDNotifConfig.dump();
    } catch (java.io.IOException ex) {};

    _notifServer = new ServiceManager(orb);

    // Creamos el Agente del Servicio
    _notificationAdmin = createAgent( orb, _tidDistribPOA, _notifServer );

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

/*
  synchronized
  public void start()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, START);
    throw new org.omg.CORBA.NO_IMPLEMENT();
  }

  synchronized
  public void stop()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, STOP);
    throw new org.omg.CORBA.NO_IMPLEMENT();
  }

  synchronized
  public void resume()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, RESUME);
    throw new org.omg.CORBA.NO_IMPLEMENT();
  }
*/

  public void trace_on(es.tid.corba.TIDDistribAdmin.TraceLevel level)
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
  public org.omg.DistributionChannelAdmin.DistributionChannelFactory
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
    return (org.omg.DistributionChannelAdmin.DistributionChannelFactory)
                                                              factory._this();
  }

  // Returns a list with the channels.
  public org.omg.DistributionChannelAdmin.DistributionChannel[] list_channels()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, LIST_CHANNELS);

    if ( factory == null )
    {
      return new org.omg.DistributionChannelAdmin.DistributionChannel[0];
    }
    return factory.distribution_channels();
  }

  public String[] list_channels_id()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, LIST_CHANNELS);

    if ( factory == null )
    {
      return new String[0];
    }
    return factory.new_channels();
  }

/*
  // Adds a new channel.
  synchronized
  protected void registerChannel(
                        es.tid.corba.TIDNotif.NotificationChannelImpl channel )
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, REGISTER_CHANNEL);
    throw new org.omg.CORBA.NO_IMPLEMENT();
  }

  // Removes a channel.
  synchronized
  protected void removeChannel(
                        es.tid.corba.TIDNotif.NotificationChannelImpl channel )
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, REMOVE_CHANNEL);
    throw new org.omg.CORBA.NO_IMPLEMENT();
  }
*/

  // The ServerProcessObject performs the shutdown.
  synchronized
  public void shutdown_service()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, SHUTDOWN);
    if (factory != null)
    {
      factory.destroyFromAdmin(true);
    }

    if (_tidDistribPOA != null)
    {
      try
      {
        ThePOAFactory.destroy( _tidDistribPOA, ThePOAFactory._AGENT_POA);
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

    if (_tidDistribPOA != null)
    {
      try
      {
        _tidDistribPOA.the_POAManager().deactivate(false, true);
      }
      catch (java.lang.Exception ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }

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

  static private boolean create_channel_factory()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, 
                                    "ServiceManager.create_channel_factory()");

    // Create the Distribution Channel Factory
    factory = new NotificationChannelFactoryImpl(
                            _orb, _tidDistribPOA, OperationMode.distribution );

    // Create the servant ID
    byte[] factoryId = CHANNELFACTORY_ID.getBytes();

    try 
    {
      _tidDistribPOA.activate_object_with_id(factoryId, factory);
    }
    catch (java.lang.Exception ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      return false;
    }

    // NOTIFICATION
    TIDNotifTrace.print(TIDNotifTrace.USER, NEW_FACTORY);
    FACTORY_POA[1] = _tidDistribPOA.toString();
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, FACTORY_POA);

    return true;
  }

  static private void loadAll()
  {
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, "ServiceManager.recoverAll()");

    if (factory != null)
    {
      factory.loadChannels();
    }
  }
}
