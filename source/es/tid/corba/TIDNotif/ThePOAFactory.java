/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 75 $
* Date: $Date: 2008-04-10 21:50:59 +0200 (Thu, 10 Apr 2008) $
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

// Create the child POA. Policies:
// ==============================
// Thread Policy:               * ORB_CTRL_MODEL
//                                SINGLE_THREAD_MODEL
// Lifespan Policy:             * TRANSIENT
//                                PERSISTENT
// Object Id Uniqueness Policy: * UNIQUE_ID
//                                MULTIPLE_ID
// Id Asignament Policy:        * SYSTEM_ID
//                                USER_ID
// Servant Retention Policy:    * RETAIN 
//                                NON_RETAIN
// Request Procesing Policy:    * USE_ACTIVE_OBJECT_MAP_ONLY
//                                USE_DEFAULT_SERVANT
//                                USE_SERVANT_MANAGER
// Implicit Activation Policy:  * IMPLICIT_ACTIVATION
//                                NO_IMPLICIT_ACTIVATION

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;
import es.tid.TIDorbj.core.poa.POAManagerImpl;

public class ThePOAFactory implements ThePOAFactoryMsg
{
  //
  // LEVELS
  //
  //public static final int GLOBAL    = 0;
  //public static final int LOCAL     = 1;
  //public static final int EXCLUSIVE = 2;

  //
  // POA TYPES
  //
  public static final int CHANNEL_POA                = 0;
  public static final int SUPPLIER_ADMIN_POA         = 1;
  public static final int CONSUMER_ADMIN_POA         = 2;
  public static final int PROXY_PUSH_SUPPLIER_POA    = 3;
  public static final int PROXY_PULL_SUPPLIER_POA    = 4;
  public static final int PROXY_PUSH_CONSUMER_POA    = 5;
  public static final int PROXY_PULL_CONSUMER_POA    = 6;
  public static final int SUPPLIER_DISCRIMINATOR_POA = 7;
  public static final int CONSUMER_DISCRIMINATOR_POA = 8;
  public static final int CONSUMER_INDEXLOCATOR_POA  = 9;
  public static final int MAPPING_DISCRIMINATOR_POA  =10;
  public static final int TRANSFORMING_OPERATOR_POA  =11;

  public static final int GLOBAL_FILTER_POA  		 =12;
  public static final int GLOBAL_MAPPING_FILTER_POA  =13;
  


  public static final int _SERVER_POA = 0;
  public static final int _AGENT_POA  = 1;
  public static final int _ADMIN_POA  = 2;
  public static final int _PROXY_POA  = 3;
  public static final int _CLIENT_POA = 4;
  public static final int _COMMON_POA = 5;

  public static final boolean CREATE_MANAGER  = true;
  public static final boolean NO_CREATE_MANAGER  = false;

  private static org.omg.CORBA.ORB _orb = null;
  private static org.omg.PortableServer.POA _root_poa = null;
  private static org.omg.PortableServer.POA _agentPOA = null;

  //
  private static java.util.Hashtable _globalPOATable;
  private static java.util.Hashtable _localPOATable;
  private static java.util.Hashtable _exclusivePOATable;

  //
  // PUBLIC METHODS
  //
  synchronized
  public static org.omg.PortableServer.POA rootPOA( org.omg.CORBA.ORB orb )
  {
    if ( _root_poa == null)
    {
      _orb = orb;
      try
      {
        _root_poa = org.omg.PortableServer.POAHelper.narrow(
                                   orb.resolve_initial_references("RootPOA") );
      }
      catch ( org.omg.CORBA.ORBPackage.InvalidName ex )
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
        throw new org.omg.CORBA.INTERNAL();
      }
      _globalPOATable = new java.util.Hashtable();
      _localPOATable = new java.util.Hashtable();
      _exclusivePOATable = new java.util.Hashtable();
    }
    return _root_poa;
  }

  public static org.omg.PortableServer.POA newPOA( String poaName, 
                                          org.omg.PortableServer.POA poaFather,
                                          boolean newManager, 
                                          int poaType )
  {
    if (newManager)
    {
      return newPOA(poaName, poaFather, null, poaType);
    }
    else
    {
      return newPOA(poaName, poaFather, poaFather.the_POAManager(), poaType);
    }
  }

  synchronized
  public static org.omg.PortableServer.POA newPOA( String poaName,
                                  org.omg.PortableServer.POA poaFather,
                                  org.omg.PortableServer.POAManager poaManager,
                                  int poaType )
  {
    checkPOA( poaName, poaFather);

    // define policies for child POA
    org.omg.CORBA.Policy [] policies = null;

    switch (poaType) 
    {
      case _SERVER_POA: // 0
      case _AGENT_POA:  // 1
      case _ADMIN_POA:  // 2

        policies = new org.omg.CORBA.Policy[2];

        // Valores cambiados
        policies[0] = poaFather.create_lifespan_policy
          ( org.omg.PortableServer.LifespanPolicyValue.PERSISTENT );
        policies[1] = poaFather.create_id_assignment_policy
          ( org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID );

        // Valores estandar
//        policies[2] = poaFather.create_thread_policy
//          ( org.omg.PortableServer.ThreadPolicyValue.ORB_CTRL_MODEL );
//        policies[3] = poaFather.create_id_uniqueness_policy
//          ( org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID );
//        policies[4] = poaFather.create_servant_retention_policy
//          ( org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN );
//        policies[5] = poaFather.create_request_processing_policy
//          ( org.omg.PortableServer.RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY );
//        policies[6] = poaFather.create_implicit_activation_policy
//          (org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
      break;
      case _PROXY_POA:  // 3

        policies = new org.omg.CORBA.Policy[5];

        // Valores cambiados
        policies[0] = poaFather.create_lifespan_policy
          ( org.omg.PortableServer.LifespanPolicyValue.PERSISTENT );
        policies[1] = poaFather.create_id_assignment_policy
          ( org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID );
        policies[2] = poaFather.create_id_uniqueness_policy
          ( org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID );
        policies[3] = poaFather.create_servant_retention_policy
          ( org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN );
        policies[4] = poaFather.create_request_processing_policy
          ( org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT );

        // Valores estandar
//        policies[5] = poaFather.create_thread_policy
//          ( org.omg.PortableServer.ThreadPolicyValue.ORB_CTRL_MODEL );
//        policies[6] = poaFather.create_implicit_activation_policy
//          (org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
      break;
      case _CLIENT_POA: // 4
        policies = new org.omg.CORBA.Policy[1];

        // Valores cambiados
        policies[0] = poaFather.create_id_assignment_policy
          ( org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID );

        // Valores estandar
//        policies[1] = poaFather.create_lifespan_policy
//          ( org.omg.PortableServer.LifespanPolicyValue.TRANSIENT );
//        policies[2] = poaFather.create_thread_policy
//          ( org.omg.PortableServer.ThreadPolicyValue.ORB_CTRL_MODEL );
//        policies[3] = poaFather.create_id_uniqueness_policy
//          ( org.omg.PortableServer.IdUniquenessPolicyValue.UNIQUE_ID );
//        policies[4] = poaFather.create_servant_retention_policy
//          ( org.omg.PortableServer.ServantRetentionPolicyValue.RETAIN );
//        policies[5] = poaFather.create_request_processing_policy
//          ( org.omg.PortableServer.RequestProcessingPolicyValue.USE_ACTIVE_OBJECT_MAP_ONLY );
//        policies[6] = poaFather.create_implicit_activation_policy
//          (org.omg.PortableServer.ImplicitActivationPolicyValue.IMPLICIT_ACTIVATION);
      break;

      case _COMMON_POA:  // 5
        policies = new org.omg.CORBA.Policy[5];

        // Valores cambiados
        policies[0] = poaFather.create_lifespan_policy
          ( org.omg.PortableServer.LifespanPolicyValue.PERSISTENT );
        policies[1] = poaFather.create_id_assignment_policy
          ( org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID );
        policies[2] = poaFather.create_id_uniqueness_policy
          ( org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID );
        policies[3] = poaFather.create_servant_retention_policy
          ( org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN );
        policies[4] = poaFather.create_request_processing_policy
          ( org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT );
      break;

      default:
        ERROR_POA[1] = Integer.toString(poaType);
        TIDNotifTrace.print(TIDNotifTrace.ERROR, ERROR_POA);
        return null;
    }

    // Create the child POA
    try
    {
      return  poaFather.create_POA( poaName, poaManager, policies );
    }
    catch ( java.lang.Exception ex )
    {			
      TIDNotifTrace.print(TIDNotifTrace.ERROR, EXCEPTION_POA);
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
    }            
    return  null;
  }

  synchronized
  public static void destroy( org.omg.PortableServer.POA poa, int poaType )
  {
    DESTROY[1] = poa.toString();
    TIDNotifTrace.print(TIDNotifTrace.USER, DESTROY);

    switch (poaType) 
    {
      case _AGENT_POA:  // 1
        poa.destroy(true, false);
        break;

      case _PROXY_POA:  // 3
        //poa.set_servant( null );
        poa.destroy(true, false);
        break;

      case _CLIENT_POA: // 4
        break;

      case _COMMON_POA:  // 5
        //poa.set_servant( null );
        poa.destroy(true, false);
        break;

      default:
        ERROR_POA[1] = Integer.toString(poaType);
        TIDNotifTrace.print(TIDNotifTrace.ERROR, ERROR_POA);
    }
  }

  public static org.omg.PortableServer.POA getGlobalPOA( String name )
  {
    return (org.omg.PortableServer.POA)_globalPOATable.get(name);
  }

  synchronized
  public static org.omg.PortableServer.POA createGlobalPOA( 
             String name, int type, org.omg.PortableServer.POAManager manager )
  {
    org.omg.PortableServer.POA the_poa = (org.omg.PortableServer.POA) 
                                                     _globalPOATable.get(name);
    if (the_poa == null)
    {
      CREATE_GLOBAL_POA[1] =  name;
      TIDNotifTrace.print(TIDNotifTrace.DEBUG, CREATE_GLOBAL_POA);

      if ( TIDNotifConfig.getBool(TIDNotifConfig.SPEEDUP_POA_KEY) )
        the_poa = create_POA( name, _root_poa, manager);
      else
        the_poa = create_POA( name, _agentPOA, manager);

      if (manager == null)
      {
        int max_threads = 0, min_threads = 0, max_requests = 0;
        int current_max_threads = 0, current_min_threads = 0;
        switch (type)
        {
          case CHANNEL_POA:
            max_threads =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_MAXTHR_KEY);
            min_threads =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_QUEUE_KEY);
            break;
          case SUPPLIER_ADMIN_POA:
          case PROXY_PUSH_SUPPLIER_POA:
          case PROXY_PULL_SUPPLIER_POA:
          case SUPPLIER_DISCRIMINATOR_POA:
          case TRANSFORMING_OPERATOR_POA:
            max_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_MAXTHR_KEY);
            min_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_QUEUE_KEY);
            break;
          case CONSUMER_ADMIN_POA:
          case PROXY_PUSH_CONSUMER_POA:
          case PROXY_PULL_CONSUMER_POA:
          case CONSUMER_DISCRIMINATOR_POA:
          case CONSUMER_INDEXLOCATOR_POA:
          case MAPPING_DISCRIMINATOR_POA:
            max_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_MAXTHR_KEY);
            min_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_QUEUE_KEY);
          break;
        }
        current_max_threads = getMaxThreads((POAManagerImpl)the_poa.the_POAManager());
        current_min_threads = getMinThreads((POAManagerImpl)the_poa.the_POAManager());
        if (current_max_threads > min_threads) {
            setMinThreads( min_threads, (POAManagerImpl)the_poa.the_POAManager() );
            setMaxThreads( max_threads, (POAManagerImpl)the_poa.the_POAManager() );
        } else {
            setMaxThreads( max_threads, (POAManagerImpl)the_poa.the_POAManager() );
            setMinThreads( min_threads, (POAManagerImpl)the_poa.the_POAManager() );
        }
        setMaxQueuedRequests(
                      max_requests, (POAManagerImpl)the_poa.the_POAManager() );

        CONFIG_GLOBAL_POA[1] = Integer.toString(max_threads);
        CONFIG_GLOBAL_POA[3] = Integer.toString(min_threads);;
        CONFIG_GLOBAL_POA[5] = Integer.toString(max_requests);;
        TIDNotifTrace.print(TIDNotifTrace.DEBUG, CONFIG_GLOBAL_POA);
      }

      // NOTIFICATION
      if ( TIDNotifConfig.getBool(TIDNotifConfig.SPEEDUP_POA_KEY) )
        debugPoaInfo(name, the_poa, _root_poa);
      else
        debugPoaInfo(name, the_poa, _agentPOA);

      if (manager == null)
      {
        // Activate the POA by its manager way
        try
        {
          the_poa.the_POAManager().activate();
        }
        catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive ex)
        {
          TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
        }
        TIDNotifTrace.print(TIDNotifTrace.DEBUG, ACTIVATE_GLOBAL_POA);
      }
      _globalPOATable.put(name, the_poa);
    }
    return the_poa;
  }

  public static org.omg.PortableServer.POA getLocalPOA( String name )
  {
    return (org.omg.PortableServer.POA)_localPOATable.get(name);
  }

  synchronized
  public static org.omg.PortableServer.POA createLocalPOA( 
             String name, int type, org.omg.PortableServer.POAManager manager )
  {
    org.omg.PortableServer.POA the_poa = (org.omg.PortableServer.POA) 
                                                      _localPOATable.get(name);
    if (the_poa == null)
    {
      CREATE_LOCAL_POA[1] =  name;
      TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, CREATE_LOCAL_POA);

      if ( TIDNotifConfig.getBool(TIDNotifConfig.SPEEDUP_POA_KEY) )
        the_poa = create_POA( name, _root_poa, manager);
      else
        the_poa = create_POA( name, _agentPOA, manager);

      if (manager == null)
      {
        int max_threads = 0, min_threads = 0, max_requests = 0;
        int current_max_threads = 0, current_min_threads = 0;
        switch (type)
        {
          case CHANNEL_POA:
            max_threads =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_MAXTHR_KEY);
            min_threads =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_QUEUE_KEY);
            break;
          case SUPPLIER_ADMIN_POA:
          case PROXY_PUSH_SUPPLIER_POA:
          case PROXY_PULL_SUPPLIER_POA:
          case SUPPLIER_DISCRIMINATOR_POA:
          case TRANSFORMING_OPERATOR_POA:
            max_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_MAXTHR_KEY);
            min_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_QUEUE_KEY);
            break;
          case CONSUMER_ADMIN_POA:
          case PROXY_PUSH_CONSUMER_POA:
          case PROXY_PULL_CONSUMER_POA:
          case CONSUMER_DISCRIMINATOR_POA:
          case CONSUMER_INDEXLOCATOR_POA:
          case MAPPING_DISCRIMINATOR_POA:
            max_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_MAXTHR_KEY);
            min_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_QUEUE_KEY);
          break;
        }
        current_max_threads = getMaxThreads((POAManagerImpl)the_poa.the_POAManager());
        current_min_threads = getMinThreads((POAManagerImpl)the_poa.the_POAManager());
        if (current_max_threads > min_threads) {
            setMinThreads( min_threads, (POAManagerImpl)the_poa.the_POAManager() );
            setMaxThreads( max_threads, (POAManagerImpl)the_poa.the_POAManager() );
        } else {
            setMaxThreads( max_threads, (POAManagerImpl)the_poa.the_POAManager() );
            setMinThreads( min_threads, (POAManagerImpl)the_poa.the_POAManager() );
        }
        setMaxQueuedRequests(
                      max_requests, (POAManagerImpl)the_poa.the_POAManager() );
        CONFIG_LOCAL_POA[1] = Integer.toString(max_threads);
        CONFIG_LOCAL_POA[3] = Integer.toString(min_threads);;
        CONFIG_LOCAL_POA[5] = Integer.toString(max_requests);;
        TIDNotifTrace.print(TIDNotifTrace.DEBUG, CONFIG_LOCAL_POA);
      }

      // NOTIFICATION
      if ( TIDNotifConfig.getBool(TIDNotifConfig.SPEEDUP_POA_KEY) )
        debugPoaInfo(name, the_poa, _root_poa);
      else
        debugPoaInfo(name, the_poa, _agentPOA);

      if (manager == null)
      {
        // Activate the POA by its manager way
        try
        {
          the_poa.the_POAManager().activate();
        }
        catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive ex)
        {
          TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
        }
        TIDNotifTrace.print(TIDNotifTrace.DEBUG, ACTIVATE_LOCAL_POA);
      }
      _localPOATable.put(name, the_poa);
    }
    return the_poa;
  }

  public static org.omg.PortableServer.POA getExclusivePOA( String name )
  {
    return (org.omg.PortableServer.POA)_exclusivePOATable.get(name);
  }

  synchronized
  public static org.omg.PortableServer.POA createExclusivePOA( 
             String name, int type, org.omg.PortableServer.POAManager manager )
  {
    org.omg.PortableServer.POA the_poa = (org.omg.PortableServer.POA) 
                                                  _exclusivePOATable.get(name);
    if (the_poa == null)
    {
      CREATE_EXCLUSIVE_POA[1] =  name;
      TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, CREATE_EXCLUSIVE_POA);

      if ( TIDNotifConfig.getBool(TIDNotifConfig.SPEEDUP_POA_KEY) )
        the_poa = create_POA( name, _root_poa, manager);
      else
        the_poa = create_POA( name, _agentPOA, manager);

      if (manager == null)
      {
        int max_threads = 0, min_threads = 0, max_requests = 0;
        int current_max_threads = 0, current_min_threads = 0;
        switch (type)
        {
          case CHANNEL_POA:
            max_threads =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_MAXTHR_KEY);
            min_threads =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.CHANNEL_QUEUE_KEY);
            break;
          case SUPPLIER_ADMIN_POA:
          case PROXY_PUSH_SUPPLIER_POA:
          case PROXY_PULL_SUPPLIER_POA:
          case SUPPLIER_DISCRIMINATOR_POA:
          case TRANSFORMING_OPERATOR_POA:
            max_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_MAXTHR_KEY);
            min_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.SUPPLIER_QUEUE_KEY);
            break;
          case CONSUMER_ADMIN_POA:
          case PROXY_PUSH_CONSUMER_POA:
          case PROXY_PULL_CONSUMER_POA:
          case CONSUMER_DISCRIMINATOR_POA:
          case CONSUMER_INDEXLOCATOR_POA:
          case MAPPING_DISCRIMINATOR_POA:
            max_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_MAXTHR_KEY);
            min_threads =
                     TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_MINTHR_KEY);
            max_requests =
                      TIDNotifConfig.getInt(TIDNotifConfig.CONSUMER_QUEUE_KEY);
          break;
        }
        current_max_threads = getMaxThreads((POAManagerImpl)the_poa.the_POAManager());
        current_min_threads = getMinThreads((POAManagerImpl)the_poa.the_POAManager());
        if (current_max_threads > min_threads) {
            setMinThreads( min_threads, (POAManagerImpl)the_poa.the_POAManager() );
            setMaxThreads( max_threads, (POAManagerImpl)the_poa.the_POAManager() );
        } else {
            setMaxThreads( max_threads, (POAManagerImpl)the_poa.the_POAManager() );
            setMinThreads( min_threads, (POAManagerImpl)the_poa.the_POAManager() );
        }
        setMaxQueuedRequests(
                      max_requests, (POAManagerImpl)the_poa.the_POAManager() );
        CONFIG_EXCLUSIVE_POA[1] = Integer.toString(max_threads);
        CONFIG_EXCLUSIVE_POA[3] = Integer.toString(min_threads);;
        CONFIG_EXCLUSIVE_POA[5] = Integer.toString(max_requests);;
        TIDNotifTrace.print(TIDNotifTrace.DEBUG, CONFIG_EXCLUSIVE_POA);
      }

      // NOTIFICATION
      if ( TIDNotifConfig.getBool(TIDNotifConfig.SPEEDUP_POA_KEY) )
        debugPoaInfo(name, the_poa, _root_poa);
      else
        debugPoaInfo(name, the_poa, _agentPOA);

      if (manager == null)
      {
        // Activate the POA by its manager way
        try
        {
          the_poa.the_POAManager().activate();
        }
        catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive ex)
        {
          TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
        }
        TIDNotifTrace.print(TIDNotifTrace.DEBUG, ACTIVATE_EXCLUSIVE_POA);
      }
      _exclusivePOATable.put(name, the_poa);
    }
    return the_poa;
  }

  synchronized
  public static void destroyGlobalPOA() 
  {
  }

  synchronized
  public static void destroyLocalPOA(String name, boolean destroy_pm)
  {
    org.omg.PortableServer.POA poa = 
                   (org.omg.PortableServer.POA)_localPOATable.remove(name);

    if (poa == null)
    {
      TIDNotifTrace.print(TIDNotifTrace.DEBUG, 
                             "ThePOAFactory.destroyLocalPOA(): POA NOT FOUND");
    }
    else
    {
      try
      {
        if (destroy_pm)
        {
          if ( poa.the_POAManager().get_state() != 
               org.omg.PortableServer.POAManagerPackage.State.INACTIVE )
          {
            //poa.the_POAManager().deactivate(true, false);
            poa.the_POAManager().deactivate(false, false);
          }
        }
        //poa.destroy(true, false);
        poa.destroy(false, false);
      }
      catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }

  synchronized
  public static void destroyExclusivePOA(String name)
  {
    org.omg.PortableServer.POA poa = 
                   (org.omg.PortableServer.POA)_exclusivePOATable.remove(name);
    if (poa == null)
    {
      TIDNotifTrace.print(TIDNotifTrace.DEBUG, 
                         "ThePOAFactory.destroyExclusivePOA(): POA NOT FOUND");
    }
    else
    {
      try
      {
        poa.the_POAManager().deactivate(true, false);
        poa.destroy(true, false);
      }
      catch ( org.omg.PortableServer.POAManagerPackage.AdapterInactive ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }

  public static void debugPoaInfo( String id, 
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
    POA_INFO_4[1] = String.valueOf(poa_mgr.get_min_threads());
    POA_INFO_4[3] = String.valueOf(poa_mgr.get_max_threads());
    POA_INFO_4[5] = String.valueOf(poa_mgr.get_max_queued_requests());
    TIDNotifTrace.print(TIDNotifTrace.DEBUG, POA_INFO_4);
  }

  //
  // PRIVATE METHODS
  //
  private static org.omg.PortableServer.POA create_POA( 
                                 String poaName,
                                 org.omg.PortableServer.POA poaFather,
                                 org.omg.PortableServer.POAManager poaManager )
  {
    checkPOA( poaName, poaFather);

    org.omg.CORBA.Policy [] policies;
    policies = new org.omg.CORBA.Policy[5];

    // Valores cambiados
    policies[0] = poaFather.create_lifespan_policy
                     ( org.omg.PortableServer.LifespanPolicyValue.PERSISTENT );
    policies[1] = poaFather.create_id_assignment_policy
                    ( org.omg.PortableServer.IdAssignmentPolicyValue.USER_ID );
    policies[2] = poaFather.create_id_uniqueness_policy
                ( org.omg.PortableServer.IdUniquenessPolicyValue.MULTIPLE_ID );
    policies[3] = poaFather.create_servant_retention_policy
             ( org.omg.PortableServer.ServantRetentionPolicyValue.NON_RETAIN );
    policies[4] = poaFather.create_request_processing_policy
     (org.omg.PortableServer.RequestProcessingPolicyValue.USE_DEFAULT_SERVANT);

    // Create the child POA
    try
    {
      return  poaFather.create_POA(poaName, poaManager, policies);
    }
    //catch ( org.omg.PortableServer.POAPackage.AdapterAlreadyExists ex)
    catch ( java.lang.Exception ex )
    {			
      TIDNotifTrace.print(TIDNotifTrace.ERROR, EXCEPTION_POA);
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
    }            
    return  null;
  }

  private static void checkPOA( String poa_name,
                               org.omg.PortableServer.POA poa_father )
  {
    int counter = 3;
    while (counter > 0)
    {
      counter--;
      java.lang.Thread th = java.lang.Thread.currentThread();
      // Si existia antes 
      try
      {
        org.omg.PortableServer.POA old_poa = 
                                          poa_father.find_POA(poa_name, false);
        TIDNotifTrace.print(TIDNotifTrace.ERROR, POA_EXIST);
        if ( old_poa.the_POAManager() != null)
        {
          TIDNotifTrace.print(TIDNotifTrace.ERROR, "  Old POAManager state = "+ 
                                 old_poa.the_POAManager().get_state().value());
        }
        try
        {
          Thread.sleep(1000);
        }
        catch ( java.lang.Exception ex ) { }
      }
      catch ( org.omg.PortableServer.POAPackage.AdapterNonExistent ex ) 
      {
        counter = 0;
      }
    }
  }

  private static void setMaxThreads( 
                    int value, es.tid.TIDorbj.core.poa.POAManagerImpl poa_mgr )
  {
    if (value > -1)
    {
      try
      {
        poa_mgr.set_max_threads(value);
      }
      catch (java.lang.Exception ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }

  private static void setMinThreads(
                    int value, es.tid.TIDorbj.core.poa.POAManagerImpl poa_mgr )
  {
    if (value > -1)
    {
      try
      {
        poa_mgr.set_min_threads(value);
      }
      catch (java.lang.Exception ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }


  private static int getMaxThreads(es.tid.TIDorbj.core.poa.POAManagerImpl poa_mgr)
  {
      int value = 0;

      try {
          value = poa_mgr.get_max_threads();
      } catch (java.lang.Exception ex) {
          TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
      return value;
  }

  private static int getMinThreads(es.tid.TIDorbj.core.poa.POAManagerImpl poa_mgr)
  {
      int value = 0;
      try {
          value = poa_mgr.get_min_threads();
      } catch (java.lang.Exception ex) {
          TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
      return value;
  }

  private static void setMaxQueuedRequests(
                    int value, es.tid.TIDorbj.core.poa.POAManagerImpl poa_mgr )
  {
    if (value > -1)
    {
      try
      {
        poa_mgr.set_max_queued_requests(value);
      }
      catch (java.lang.Exception ex)
      {
        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      }
    }
  }

  public static void debugPOAs()
  {
    TIDNotifTrace.print( TIDNotifTrace.ERROR, " === GLOBAL POAs ===" ); 
    for (java.util.Enumeration e=_globalPOATable.keys(); e.hasMoreElements();)
    {
      TIDNotifTrace.print( TIDNotifTrace.ERROR, 
                                 " *** POA key: = "+ (String)e.nextElement() );
    }
    TIDNotifTrace.print( TIDNotifTrace.ERROR, " === LOCAL POAs ===" ); 
    for (java.util.Enumeration e=_localPOATable.keys(); e.hasMoreElements();)
    {
      TIDNotifTrace.print( TIDNotifTrace.ERROR, 
                                 " *** POA key: = "+ (String)e.nextElement() );
    }
    TIDNotifTrace.print( TIDNotifTrace.ERROR, " === EXCUSIVE POAs ===" ); 
    for (java.util.Enumeration e=_exclusivePOATable.keys();e.hasMoreElements();)
    {
      TIDNotifTrace.print( TIDNotifTrace.ERROR, 
                                 " *** POA key: = "+ (String)e.nextElement() );
    }
  }

  public static String getIdName(String id )
  {
    return id.substring(id.lastIndexOf("##")+2);
  }
}
