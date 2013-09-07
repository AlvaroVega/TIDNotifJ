///////////////////////////////////////////////////////////////////////////
//
// File          :
// Description   :
//
// Author/s      : Alvaro Rodriguez
// Project       :
// Rel           :
// Created       :
// Revision Date :
// Rev. History  :
//
// Copyright 2001 Telefonica, I+D. All Rights Reserved.
//
// The copyright of the software program(s) is property of Telefonica.
// The program(s) may be used, modified and/or copied only with the
// express written consent of Telefonica or in acordance with the terms
// and conditions stipulated in the agreement/contract under which
// the program(s) have been supplied.
//
///////////////////////////////////////////////////////////////////////////

package es.tid.corba.TIDNotif.tools;

import org.omg.NotificationChannelAdmin.SupplierAdminHelper;
import org.omg.CosEventChannelAdmin.ProxyPushConsumer;
import org.omg.CosEventChannelAdmin.ProxyPushConsumerHelper;
import org.omg.CosEventChannelAdmin.ProxyPullConsumer;
import org.omg.CosEventChannelAdmin.ProxyPullConsumerHelper;
//import org.omg.ConstraintAdmin.Discriminator;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;

import es.tid.TIDorbj.core.TIDORB;

public class SupplierAdmin
{		
  private static final String NAME = "SupplierAdmin";

  private static final String OP_CREATE_PUSH_CONSUMER = "create_push_consumer";
  private static final String OP_CREATE_PULL_CONSUMER = "create_pull_consumer";
  private static final String OP_FIND_PUSH_CONSUMER  = "find_push_consumer";
  private static final String OP_FIND_PULL_CONSUMER  = "find_pull_consumer";
  private static final String OP_LIST_PUSH_CONSUMERS  = "push_consumers";
  private static final String OP_LIST_PULL_CONSUMERS  = "pull_consumers";
  private static final String OP_LIST_PUSH_CONSUMER_IDS="nlist_push_consumers";
  private static final String OP_LIST_PULL_CONSUMER_IDS="nlist_pull_consumers";
  //private static final String OP_GET_DISCRIMINATOR  = "get_discriminator";
  private static final String OP_GET                  = "get";
  private static final String OP_SET                  = "set";
  private static final String OP_DESTROY              = "destroy";

  private static final int _OP_UNKNOWN              = -1;
  private static final int _OP_CREATE_PUSH_CONSUMER =  0;
  private static final int _OP_CREATE_PULL_CONSUMER =  1;
  private static final int _OP_FIND_PUSH_CONSUMER   =  2;
  private static final int _OP_FIND_PULL_CONSUMER   =  3;
  private static final int _OP_LIST_PUSH_CONSUMERS  =  4;
  private static final int _OP_LIST_PULL_CONSUMERS  =  5;
  private static final int _OP_LIST_PUSH_CONSUMER_IDS =  6;
  private static final int _OP_LIST_PULL_CONSUMER_IDS =  7;
  //private static final int _OP_GET_DISCRIMINATOR  =  8;
  private static final int _OP_GET                  =  9;
  private static final int _OP_SET                  =  10;
  private static final int _OP_DESTROY              =  11;

  private static final String MODE_IOR = "ior";
  private static final String MODE_URL = "url";

  private static final int _MODE_IOR = 0;
  private static final int _MODE_URL = 1;

  private static final String ATTR_OPERATIONAL    = "OperationalState";
  private static final String ATTR_VALUE_DISABLED = "disabled";
  private static final String ATTR_VALUE_ENABLED  = "enabled";

  private static final String ATTR_ADMINISTRATIVE     = "AdministrativeState";
  private static final String ATTR_VALUE_LOCKED       = "locked";
  private static final String ATTR_VALUE_UNLOCKED     = "unlocked";
  private static final String ATTR_VALUE_SUTTING_DOWN = "shutting_down";

  private static final String ASSOCIATED_CRITERIA     = "AssociatedCriteria";
  private static final String CRITERIA_ID             = "Id";

  private static final String DISCRIMINATOR           = "Discriminator";

  private static final int _ATTR_OPERATIONAL    = 0;
  private static final int _ATTR_ADMINISTRATIVE = 1;
  private static final int _ASSOCIATED_CRITERIA = 2;
  private static final int _DISCRIMINATOR       = 3;

  private static es.tid.corba.TIDNotif.tools.Util utils = 
                                        new es.tid.corba.TIDNotif.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
     "SupplierAdmin create_push_consumer [name] [-m (ior | url)] < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin create_pull_consumer [name] [-m (ior | url)] < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin find_push_consumer name [-m (ior | url)] < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin find_pull_consumer name [-m (ior | url)] < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin push_consumers [-m (ior | url)] < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin pull_consumers [-m (ior | url)] < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin nlist_push_consumers  < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin nlist_pull_consumers  < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin get OperationalState < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin get AdministrativeState < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin get AssociatedCriteria Id < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin get Discriminator [-m (ior | url)] < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin set AdministrativeState <new_state> < supplier_admin.ior");
    System.err.println(
     "SupplierAdmin destroy < supplier_admin.ior");
  }

  public static void main( String args [] )
  {
    java.util.Properties props = new java.util.Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
    System.getProperties().put( "org.omg.CORBA.ORBSingletonClass",
                                          "es.tid.TIDorbj.core.SingletonORB" );
    org.omg.CORBA.ORB orb = null;
    try
    {
      orb = org.omg.CORBA.ORB.init(args, props);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      System.exit(1);
    }		

    args = utils.filterArgs(args);

    if (args.length < 1)
    {
      System.err.println("Error en el numero de parametros");
      showHelp();
      System.exit(1);
    }
    
    int operation = _OP_UNKNOWN;
    String proxy_name = null;
    String admin_name = null;
    int ref_mode = _MODE_IOR;
    int attr_name = -1;
    String attr_value = null;

// Debug 
/*
    for ( int i = 0; i < args.length; i++ )
    {
      System.err.println(args[i]);
    }
*/

    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      if ( args[i].compareTo("-m") == 0 )
      {
        String _ref_mode = new String(args[++i]);

        if (_ref_mode.compareTo(MODE_IOR) == 0)
        {
          ref_mode = _MODE_IOR;
        }
        else if (_ref_mode.compareTo(MODE_URL) == 0)
        {
          ref_mode = _MODE_URL;
        }
        else
        {
          System.err.println("Modo invalido [-m (ior | url)]");
          showHelp();
          System.exit(1);
        }
      }
      // SupplierAdmin create_push_consumer [-m (ior|url)] < supplier_admin.ior
      else if ( args[i].compareTo(OP_CREATE_PUSH_CONSUMER) == 0 )
      {
        operation = _OP_CREATE_PUSH_CONSUMER;
      }
      // SupplierAdmin create_pull_consumer [-m (ior | url)] <supplier_admin.ior
      else if ( args[i].compareTo(OP_CREATE_PULL_CONSUMER) == 0 )
      {
        operation = _OP_CREATE_PULL_CONSUMER;
      }
      else if ( args[i].compareTo(OP_FIND_PUSH_CONSUMER) == 0 )
      {
        operation = _OP_FIND_PUSH_CONSUMER;
      }
      else if ( args[i].compareTo(OP_FIND_PULL_CONSUMER) == 0 )
      {
        operation = _OP_FIND_PULL_CONSUMER;
      }
      // SupplierAdmin push_consumers [-m (ior | url)] < supplier_admin.ior
      else if ( args[i].compareTo(OP_LIST_PUSH_CONSUMERS) == 0 )
      {
        operation = _OP_LIST_PUSH_CONSUMERS;
      }
      // SupplierAdmin pull_consumers [-m (ior | url)] < supplier_admin.ior
      else if ( args[i].compareTo(OP_LIST_PULL_CONSUMERS) == 0 )
      {
        operation = _OP_LIST_PULL_CONSUMERS;
      }
      else if ( args[i].compareTo(OP_LIST_PUSH_CONSUMER_IDS) == 0 )
      {
        operation = _OP_LIST_PUSH_CONSUMER_IDS;
      }
      else if ( args[i].compareTo(OP_LIST_PULL_CONSUMER_IDS) == 0 )
      {
        operation = _OP_LIST_PULL_CONSUMER_IDS;
      }
/*
      // SupplierAdmin get_discriminator [-m (ior | url)] < supplier_admin.ior
      else if ( args[i].compareTo(OP_GET_DISCRIMINATOR) == 0 )
      {
        operation = _OP_GET_DISCRIMINATOR;
      }
*/
      // SupplierAdmin get OperationalState < supplier_admin.ior
      // SupplierAdmin get AdministrativeState < supplier_admin.ior
      else if ( args[i].compareTo(OP_GET) == 0 )
      {
        operation = _OP_GET;

        if (args.length < 2)
        {
          System.err.println("Numero de parametros invalido.");
          showHelp();
          System.exit(1);
        }
        String _attr_name = args[++i];

        if (_attr_name.compareTo(ATTR_OPERATIONAL) == 0)
        {
          attr_name = _ATTR_OPERATIONAL;
        }
        else if (_attr_name.compareTo(ATTR_ADMINISTRATIVE) == 0)
        {
          attr_name = _ATTR_ADMINISTRATIVE;
        }
        else if (_attr_name.compareTo(ASSOCIATED_CRITERIA) == 0)
        {
          attr_name = _ASSOCIATED_CRITERIA;
          if (args.length < 3)
          {
            System.err.println("Numero de parametros invalido.");
            showHelp();
            System.exit(1);
          }
          attr_value = args[++i];

          if (attr_value.compareTo(CRITERIA_ID) != 0) 
          {
            System.err.println( "Valor invalido (" + CRITERIA_ID + ")" );
            showHelp();
            System.exit(1);
          }
        }
        else if (_attr_name.compareTo(DISCRIMINATOR) == 0)
        {
          attr_name = _DISCRIMINATOR;
        }
        else
        {
          System.err.println( "Atributo invalido (" + 
                       ATTR_ADMINISTRATIVE + " | " + ATTR_OPERATIONAL + " | " +
                       ASSOCIATED_CRITERIA + " | " +_DISCRIMINATOR + ")" );
          showHelp();
          System.exit(1);
        }
      }
      // SupplierAdmin set AdministrativeState <new_state> < supplier_admin.ior
      else if ( args[i].compareTo(OP_SET) == 0 )
      {
        operation = _OP_SET;

        if (args.length < 3)
        {
          System.err.println("Numero de parametros invalido.");
          showHelp();
          System.exit(1);
        }
        String _attr_name = args[++i];
        attr_value = args[++i];

        if (_attr_name.compareTo(ATTR_ADMINISTRATIVE) == 0)
        {
          attr_name = _ATTR_ADMINISTRATIVE;
          if ((attr_value.compareTo(ATTR_VALUE_LOCKED) != 0) &&
              (attr_value.compareTo(ATTR_VALUE_UNLOCKED) != 0) &&
              (attr_value.compareTo(ATTR_VALUE_SUTTING_DOWN) != 0))
          {
            System.err.println( "Valor invalido (" + ATTR_VALUE_LOCKED + " | "
                   + ATTR_VALUE_UNLOCKED + " | " + ATTR_VALUE_UNLOCKED + ")" );
            showHelp();
            System.exit(1);
          }
        }
        else
        {
          System.err.println( "Atributo invalido (" +ATTR_ADMINISTRATIVE+ ")");
          showHelp();
          System.exit(1);
        }
      }
      // SupplierAdmin destroy < supplier_admin.ior
      else if ( args[i].compareTo(OP_DESTROY) == 0 )
      {
        operation = _OP_DESTROY;
      }
      else if ( ( args[i].compareTo("-help") == 0 ) ||
                ( args[i].compareTo("-h") == 0 ) ||
                ( args[i].compareTo("/h") == 0 ) ||
                ( args[i].compareTo("-?") == 0 ) ||
                ( args[i].compareTo("/?") == 0 ) )
      {
        showHelp();
        System.exit(0);
      }
      else
      {
        proxy_name = args[i];
        //System.err.println("Parametro invalido.");
        //showHelp();
        //System.exit(0);
      }
    }

// Debug 
/*
    System.err.println("Operation: " + operation);
    System.err.println("Admin name: " + admin_name);
    System.err.println("Ref mode: " + ref_mode);
    System.err.println("Attr. name: " + attr_name);
    System.err.println("Attr. value: " + attr_value);
*/

    if (operation == _OP_UNKNOWN)
    {
      System.err.println(
          "Operacion invalida (create_push_consumer|create_pull_consumer|");
      System.err.println(
          "                    push_consumers|pull_consumers|get|set|destroy");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    org.omg.NotificationChannelAdmin.SupplierAdmin supplier_admin = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      supplier_admin = SupplierAdminHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    ProxyPushConsumer proxy_push_consumer = null;
    ProxyPullConsumer proxy_pull_consumer = null;
    ProxyPushConsumer[] proxy_push_consumers = null;
    ProxyPullConsumer[] proxy_pull_consumers = null;
    org.omg.CosLifeCycle.NVP[] criteria = null;
    String[] proxy_names = null;

    switch (operation)
    {
      // SupplierAdmin create_push_consumer [-m (ior | url)] <supplier_admin.ior
      case _OP_CREATE_PUSH_CONSUMER:
        try
        {
          if (proxy_name == null)
            proxy_push_consumer = supplier_admin.obtain_push_consumer();
          else
            proxy_push_consumer = ProxyPushConsumerHelper.narrow(
                       supplier_admin.obtain_named_push_consumer(proxy_name) );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_push_consumer);
        }
        else
        {
          reference = 
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_push_consumer);
        }
        utils.writeIORString(reference, null);
        break;

      // SupplierAdmin create_pull_consumer [-m (ior | url)] <supplier_admin.ior
      case _OP_CREATE_PULL_CONSUMER:
        try
        {
          if (proxy_name == null)
            proxy_pull_consumer = supplier_admin.obtain_pull_consumer();
          else
            proxy_pull_consumer = ProxyPullConsumerHelper.narrow(
                       supplier_admin.obtain_named_pull_consumer(proxy_name) );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_pull_consumer);
        }
        else
        {
          reference = 
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_pull_consumer);
        }
        utils.writeIORString(reference, null);
        break;

      case _OP_FIND_PUSH_CONSUMER:
        if (proxy_name == null)
        {
          System.err.println("Parametro \"name\" no especificado");
          showHelp();
          System.exit(0);
        }
        try
        {
          proxy_push_consumer = ProxyPushConsumerHelper.narrow(
                               supplier_admin.find_push_consumer(proxy_name) );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_push_consumer);
        }
        else
        {
          reference = 
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_push_consumer);
        }
        utils.writeIORString(reference, null);
        break;

      // SupplierAdmin pull_consumers [-m (ior | url)] < supplier_admin.ior
      case _OP_FIND_PULL_CONSUMER:
        if (proxy_name == null)
        {
          System.err.println("Parametro \"name\" no especificado");
          showHelp();
          System.exit(0);
        }
        try
        {
          proxy_pull_consumer = ProxyPullConsumerHelper.narrow(
                               supplier_admin.find_pull_consumer(proxy_name) );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_pull_consumer);
        }
        else
        {
          reference = 
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_pull_consumer);
        }
        utils.writeIORString(reference, null);
        break;

      // SupplierAdmin push_consumers [-m (ior | url)] < supplier_admin.ior
      case _OP_LIST_PUSH_CONSUMERS:
        try
        {
            proxy_push_consumers = supplier_admin.obtain_push_consumers(); //push_consumers();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < proxy_push_consumers.length; i++)
        {
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(proxy_push_consumers[i]);
          }
          else
          {
            reference = ((es.tid.TIDorbj.core.TIDORB)
                                   orb).objectToURL(proxy_push_consumers[i]);
          }
          utils.writeIORString(reference, null);
        }
        break;

      // SupplierAdmin pull_consumers [-m (ior | url)] < supplier_admin.ior
      case _OP_LIST_PULL_CONSUMERS:
        try
        {
            proxy_pull_consumers = supplier_admin.obtain_pull_consumers(); //pull_consumers();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < proxy_pull_consumers.length; i++)
        {
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(proxy_pull_consumers[i]);
          }
          else
          {
            reference = ((es.tid.TIDorbj.core.TIDORB)
                                   orb).objectToURL(proxy_pull_consumers[i]);
          }
          utils.writeIORString(reference, null);
        }
        break;

      case _OP_LIST_PUSH_CONSUMER_IDS:
        try
        {
          proxy_names = supplier_admin.push_consumer_ids();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < proxy_names.length; i++)
        {
          utils.writeIORString(proxy_names[i], null);
        }
        break;

      // SupplierAdmin pull_consumers [-m (ior | url)] < supplier_admin.ior
      case _OP_LIST_PULL_CONSUMER_IDS:
        try
        {
          proxy_names = supplier_admin.pull_consumer_ids();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < proxy_names.length; i++)
        {
          utils.writeIORString(proxy_names[i], null);
        }
        break;
/*
      // SupplierAdmin get_discriminator [-m (ior | url)] < supplier_admin.ior
      case _OP_GET_DISCRIMINATOR:
        try
        {
          discriminator = supplier_admin.forwarding_discriminator();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(discriminator);
        }
        else
        {
          reference = 
                ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(discriminator);
        }
        utils.writeIORString(reference, null);
        break;
*/
      // SupplierAdmin get OperationalState < supplier_admin.ior
      // SupplierAdmin get AdministrativeState < supplier_admin.ior
      case _OP_GET:
        try
        {
          int _attr_value;
          if (attr_name == _ATTR_OPERATIONAL)
          {
             _attr_value = supplier_admin.operational_state().value();
             switch (_attr_value)
             {
               case 0: 
                 System.out.println("disabled");
                 break;
               case 1: 
                 System.out.println("enabled");
             }
          }
          else if (attr_name == _ATTR_ADMINISTRATIVE)
          {
             _attr_value = supplier_admin.administrative_state().value();
             switch (_attr_value)
             {
               case 0: 
                 System.out.println("locked");
                 break;
               case 1: 
                 System.out.println("unlocked");
                 break;
               case 2: 
                 System.out.println("shutting_down");
             }
          }
          else if (attr_name == _ASSOCIATED_CRITERIA)
          {
            if (attr_value.compareTo(CRITERIA_ID) == 0)
            {
              criteria = supplier_admin.associated_criteria();
              for (int i = 0; i < criteria.length; i++)
              {
                if (CRITERIA_ID.compareTo(criteria[i].name) == 0)
                {
                  System.out.println( criteria[i].value.extract_string() );
                  break;
                }
              }
            }
          }
//           else if (attr_name == _DISCRIMINATOR)
//           {
//             Discriminator discriminator = null;
//             try
//             {
//               discriminator = supplier_admin.forwarding_discriminator();
//             }
//             catch ( java.lang.Exception ex )
//             {
//               ex.printStackTrace();
//               System.exit(1);
//             }
//             if (ref_mode == _MODE_IOR)
//             {
//               reference = orb.object_to_string(discriminator);
//             }
//             else
//             {
//               reference =
//                 ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(discriminator);
//             }
//             utils.writeIORString(reference, null);
//           }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
      break;
      // SupplierAdmin set AdministrativeState <new_state> < supplier_admin.ior
      case _OP_SET:
        try
        {
          if (attr_name == _ATTR_ADMINISTRATIVE)
          {
            if (attr_value.compareTo(ATTR_VALUE_LOCKED) == 0)
            {
              supplier_admin.administrative_state(AdministrativeState.locked);
            }
            else if (attr_value.compareTo(ATTR_VALUE_UNLOCKED) == 0)
            {
              supplier_admin.administrative_state(AdministrativeState.unlocked);
            }
            else //if (attr_value.compareTo(ATTR_VALUE_SUTTING_DOWN) == 0)
            {
              supplier_admin.administrative_state(
                                            AdministrativeState.shutting_down);
            }
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // SupplierAdmin destroy < supplier_admin.ior
      case _OP_DESTROY:
        try
        {
          supplier_admin.destroy();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}