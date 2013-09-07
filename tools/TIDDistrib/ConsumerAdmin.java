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

package es.tid.corba.TIDDistrib.tools;

import org.omg.DistributionChannelAdmin.ConsumerAdminHelper;
import org.omg.CosEventChannelAdmin.ProxyPushSupplier;
import org.omg.CosEventChannelAdmin.ProxyPushSupplierHelper;
import org.omg.CosEventChannelAdmin.ProxyPullSupplier;
import org.omg.CosEventChannelAdmin.ProxyPullSupplierHelper;
//import org.omg.ConstraintAdmin.Discriminator;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;
import org.omg.ServiceManagement.OperationMode;
import es.tid.corba.ExceptionHandlerAdmin.DistributionHandler;
import es.tid.corba.ExceptionHandlerAdmin.DistributionHandlerHelper;

import es.tid.TIDorbj.core.TIDORB;

public class ConsumerAdmin
{		
  private static final String NAME = "ConsumerAdmin";

  private static final String OP_CREATE_PUSH_SUPPLIER = "create_push_supplier";
  private static final String OP_CREATE_PULL_SUPPLIER = "create_pull_supplier";
  private static final String OP_FIND_PUSH_SUPPLIER   = "find_push_supplier";
  private static final String OP_FIND_PULL_SUPPLIER   = "find_pull_supplier";
  private static final String OP_LIST_PUSH_SUPPLIERS  = "push_suppliers";
  private static final String OP_LIST_PULL_SUPPLIERS  = "pull_suppliers";
  private static final String OP_LIST_PUSH_SUPPLIER_IDS="nlist_push_suppliers";
  private static final String OP_LIST_PULL_SUPPLIER_IDS="nlist_pull_suppliers";
  private static final String OP_CREATE_CONSUMER      = "create_consumer";
  private static final String OP_FIND_CONSUMERS       = "find_consumers";
  private static final String OP_LIST_CONSUMERS       = "list_consumers";
  private static final String OP_NLIST_CONSUMERS      = "nlist_consumers";
  private static final String OP_GET                  = "get";
  private static final String OP_SET                  = "set";
  private static final String OP_DESTROY              = "destroy";

  private static final int _OP_UNKNOWN              = -1;
  private static final int _OP_CREATE_PUSH_SUPPLIER =  0;
  private static final int _OP_CREATE_PULL_SUPPLIER =  1;
  private static final int _OP_FIND_PUSH_SUPPLIER   =  2;
  private static final int _OP_FIND_PULL_SUPPLIER   =  3;
  private static final int _OP_LIST_PUSH_SUPPLIERS  =  4;
  private static final int _OP_LIST_PULL_SUPPLIERS  =  5;
  private static final int _OP_LIST_PUSH_SUPPLIER_IDS = 6;
  private static final int _OP_LIST_PULL_SUPPLIER_IDS = 7;
  private static final int _OP_CREATE_CONSUMER      =  8;
  private static final int _OP_FIND_CONSUMERS       =  9;
  private static final int _OP_LIST_CONSUMERS       = 10;
  private static final int _OP_NLIST_CONSUMERS      = 11;
  //private static final int _OP_GET_DISCRIMINATOR  = 12;
  private static final int _OP_GET                  = 13;
  private static final int _OP_SET                  = 14;
  private static final int _OP_DESTROY              = 15;

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

  private static final String DISCRIMINATOR          = "Discriminator";
  private static final String OPERATION_MODE         = "OperationMode";
  private static final String ORDER                  = "Order";
  private static final String ORDER_GAP              = "OrderGap";
  private static final String ERROR_HANDLER          = "ErrorHandler";

  private static final int _ATTR_OPERATIONAL    = 0;
  private static final int _ATTR_ADMINISTRATIVE = 1;
  private static final int _ASSOCIATED_CRITERIA = 2;
  private static final int _DISCRIMINATOR       = 3;
  private static final int _OPERATION_MODE      = 4;
  private static final int _ORDER               = 5;
  private static final int _ORDER_GAP           = 6;
  private static final int _ERROR_HANDLER       = 7;


  private static es.tid.corba.TIDDistrib.tools.Util utils = 
                                      new es.tid.corba.TIDDistrib.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
    "ConsumerAdmin create_push_supplier [name] [-p position] [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
    "ConsumerAdmin create_pull_supplier [name] [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "SupplierAdmin find_push_supplier name [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "SupplierAdmin find_pull_supplier name [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin push_suppliers [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin pull_suppliers [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "SupplierAdmin nlist_push_suppliers  < consumer_admin.ior");
    System.err.println(
     "SupplierAdmin nlist_pull_suppliers  < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin create_consumer [name] [-p position] [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin find_consumers [name] [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin list_consumers [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin nlist_consumers < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin get Discriminator [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin get OperationalState < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin get AdministrativeState < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin get AssociatedCriteria Id < supplier_admin.ior");
    System.err.println(
     "ConsumerAdmin get OperationMode < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin get Order < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin get OrderGap < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin get ErrorHandler [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin set AdministrativeState <new_state> < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin set Order <new_order> < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin set OrderGap <new_order_gap> < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin set ErrorHandler <fichero> [-m (ior | url)] < consumer_admin.ior");
    System.err.println(
     "ConsumerAdmin destroy < consumer_admin.ior");
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
    int order = -1;
    int order_gap = -1;
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
      if ( args[i].compareTo("-p") == 0 )
      {
        try
        {
          order = Integer.parseInt(args[++i]);
        }
        catch (java.lang.NumberFormatException ex)
        {
          System.err.println("Parametro Position invalido, debe ser un int.");
          showHelp();
          System.exit(1);
        }
      }
      else if ( args[i].compareTo("-m") == 0 )
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
      // ConsumerAdmin create_push_supplier [-p position] [-m (ior|url)] < consumer_admin.ior
      else if ( args[i].compareTo(OP_CREATE_PUSH_SUPPLIER) == 0 )
      {
        operation = _OP_CREATE_PUSH_SUPPLIER;
      }
      // ConsumerAdmin create_pull_supplier [-m (ior | url)] <consumer_admin.ior
      else if ( args[i].compareTo(OP_CREATE_PUSH_SUPPLIER) == 0 )
      {
        operation = _OP_CREATE_PUSH_SUPPLIER;
      }
      else if ( args[i].compareTo(OP_FIND_PUSH_SUPPLIER) == 0 )
      {
        operation = _OP_FIND_PUSH_SUPPLIER;
      }
      else if ( args[i].compareTo(OP_FIND_PULL_SUPPLIER) == 0 )
      {
        operation = _OP_FIND_PULL_SUPPLIER;
      }
      // ConsumerAdmin push_suppliers [-m (ior | url)] < consumer_admin.ior
      else if ( args[i].compareTo(OP_LIST_PUSH_SUPPLIERS) == 0 )
      {
        operation = _OP_LIST_PUSH_SUPPLIERS;
      }
      // ConsumerAdmin pull_suppliers [-m (ior | url)] < consumer_admin.ior
      else if ( args[i].compareTo(OP_LIST_PULL_SUPPLIERS) == 0 )
      {
        operation = _OP_LIST_PULL_SUPPLIERS;
      }
      else if ( args[i].compareTo(OP_LIST_PUSH_SUPPLIER_IDS) == 0 )
      {
        operation = _OP_LIST_PUSH_SUPPLIER_IDS;
      }
      else if ( args[i].compareTo(OP_LIST_PULL_SUPPLIER_IDS) == 0 )
      {
        operation = _OP_LIST_PULL_SUPPLIER_IDS;
      }
      else if ( args[i].compareTo(OP_CREATE_CONSUMER) == 0 )
      {
        operation = _OP_CREATE_CONSUMER;
      }
      // ConsumerAdmin find_consumers [Id] [-m (ior | url)] <consumer_admin.ior
      else if ( args[i].compareTo(OP_FIND_CONSUMERS) == 0 )
      {
        operation = _OP_FIND_CONSUMERS;
      }
      // ConsumerAdmin list_consumers [-m (ior | url)] < consumer_admin.ior
      else if ( args[i].compareTo(OP_LIST_CONSUMERS) == 0 )
      {
        operation = _OP_LIST_CONSUMERS;
      }
      // ConsumerAdmin nlist_consumers < consumer_admin.ior
      else if ( args[i].compareTo(OP_NLIST_CONSUMERS) == 0 )
      {
        operation = _OP_NLIST_CONSUMERS;
      }
      // ConsumerAdmin get_discriminator [-m (ior | url)] < consumer_admin.ior
/*
      else if ( args[i].compareTo(OP_GET_DISCRIMINATOR) == 0 )
      {
        operation = _OP_GET_DISCRIMINATOR;
      }
*/
      // ConsumerAdmin get OperationalState < consumer_admin.ior
      // ConsumerAdmin get AdministrativeState < consumer_admin.ior
      // ConsumerAdmin get AssociatedCriteria Id < supplier_admin.ior
      // ConsumerAdmin get OperationMode < consumer_admin.ior
      // ConsumerAdmin get Discriminator [-m (ior | url)] < consumer_admin.ior
      // ConsumerAdmin get Order < consumer_admin.ior
      // ConsumerAdmin get OrderGap < consumer_admin.ior
      // ConsumerAdmin get ErrorHandler [-m (ior | url)] < consumer_admin.ior
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
        else if (_attr_name.compareTo(OPERATION_MODE) == 0)
        {
          attr_name = _OPERATION_MODE;
        }
        else if (_attr_name.compareTo(ORDER) == 0)
        {
          attr_name = _ORDER;
        }
        else if (_attr_name.compareTo(ORDER_GAP) == 0)
        {
          attr_name = _ORDER_GAP;
        }
        else if (_attr_name.compareTo(ERROR_HANDLER) == 0)
        {
          attr_name = _ERROR_HANDLER;
        }
        else
        {
          System.err.println( "Atributo invalido (" + 
                       ATTR_ADMINISTRATIVE + " | " + ATTR_OPERATIONAL + " | " +
                       ASSOCIATED_CRITERIA + " | " + DISCRIMINATOR + " | " + 
                       OPERATION_MODE + " | " + ORDER + " | " +
                       ORDER_GAP + " | " + ERROR_HANDLER + ")" );
          showHelp();
          System.exit(1);
        }
      }
      // ConsumerAdmin set AdministrativeState <new_state> < consumer_admin.ior
      // ConsumerAdmin set Order <new_order> < consumer_admin.ior
      // ConsumerAdmin set OrderGap <new_order_gap> < consumer_admin.ior
      // ConsumerAdmin set ErrorHandler <fichero> [-m (ior|url)]<consumer_admin.ior
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
        else if (_attr_name.compareTo(ORDER) == 0)
        {
          attr_name = _ORDER;
        }
        else if (_attr_name.compareTo(ORDER_GAP) == 0)
        {
          attr_name = _ORDER_GAP;
        }
        else if (_attr_name.compareTo(ERROR_HANDLER) == 0)
        {
          attr_name = _ERROR_HANDLER;
        }
        else
        {
          System.err.println( "Atributo invalido (" +
                                  ATTR_ADMINISTRATIVE + " | " + ORDER + " | " + 
                                  ORDER_GAP + " | " + ERROR_HANDLER + ")" );
          showHelp();
          System.exit(1);
        }
      }
      // ConsumerAdmin destroy < consumer_admin.ior
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
/*
      else
      {
        System.err.println("Parametro invalido (" + args[i] +")" );
        showHelp();
        System.exit(0);
      }
*/
      else
      {
        proxy_name = new String(args[i]);
        admin_name = new String(args[i]);
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
         "Operacion invalida (create_push_supplier|create_pull_supplier|");
      System.err.println(
         "                    create_consumer|find_consumers|list_consumers|");
      System.err.println(
         "                    push_suppliers|pull_suppliers|get|set|destroy");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    org.omg.DistributionChannelAdmin.ConsumerAdmin consumer_admin = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      consumer_admin = ConsumerAdminHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    ProxyPushSupplier proxy_push_supplier = null;
    ProxyPullSupplier proxy_pull_supplier = null;
    ProxyPushSupplier[] proxy_push_suppliers = null;
    ProxyPullSupplier[] proxy_pull_suppliers = null;
    org.omg.CosLifeCycle.NVP[] criteria = null;
    String[] proxy_names = null;

    switch (operation)
    {
      // ConsumerAdmin create_push_supplier [-p position] [-m (ior | url)] <consumer_admin.ior
      case _OP_CREATE_PUSH_SUPPLIER:
        try
        {
          if (order == -1)
          {
            if (proxy_name == null)
              proxy_push_supplier = consumer_admin.obtain_push_supplier();
            else
              proxy_push_supplier = ProxyPushSupplierHelper.narrow(
                       consumer_admin.obtain_named_push_supplier(proxy_name) );
          }
          else
          {
            if (proxy_name == null)
              proxy_push_supplier =
                                consumer_admin.new_obtain_push_supplier(order);
            else
              proxy_push_supplier =
               consumer_admin.new_obtain_named_push_supplier(order,proxy_name);
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_push_supplier);
        }
        else
        {
          reference = 
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_push_supplier);
        }
        utils.writeIORString(reference, null);
        break;

      // ConsumerAdmin create_pull_supplier [-m (ior | url)] <consumer_admin.ior
      case _OP_CREATE_PULL_SUPPLIER:
        try
        {
          if (proxy_name == null)
            proxy_pull_supplier = consumer_admin.obtain_pull_supplier();
          else
            proxy_pull_supplier = ProxyPullSupplierHelper.narrow(
                       consumer_admin.obtain_named_pull_supplier(proxy_name) );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_pull_supplier);
        }
        else
        {
          reference = 
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_pull_supplier);
        }
        utils.writeIORString(reference, null);
        break;

      case _OP_FIND_PUSH_SUPPLIER:
        if (proxy_name == null)
        {
          System.err.println("Parametro \"name\" no especificado");
          showHelp();
          System.exit(0);
        }
        try
        {
          proxy_push_supplier = ProxyPushSupplierHelper.narrow(
                               consumer_admin.find_push_supplier(proxy_name) );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_push_supplier);
        }
        else
        {
          reference =
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_push_supplier);
        }
        utils.writeIORString(reference, null);
        break;

      // SupplierAdmin pull_suppliers [-m (ior | url)] < consumer_admin.ior
      case _OP_FIND_PULL_SUPPLIER:
        if (proxy_name == null)
        {
          System.err.println("Parametro \"name\" no especificado");
          showHelp();
          System.exit(0);
        }
        try
        {
          proxy_pull_supplier = ProxyPullSupplierHelper.narrow(
                               consumer_admin.find_pull_supplier(proxy_name) );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(proxy_pull_supplier);
        }
        else
        {
          reference =
           ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(proxy_pull_supplier);
        }
        utils.writeIORString(reference, null);
        break;

      // ConsumerAdmin push_suppliers [-m (ior | url)] < consumer_admin.ior
      case _OP_LIST_PUSH_SUPPLIERS:
        try
        {
            proxy_push_suppliers = consumer_admin.obtain_push_suppliers(); //push_suppliers();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < proxy_push_suppliers.length; i++)
        {
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(proxy_push_suppliers[i]);
          }
          else
          {
            reference = ((es.tid.TIDorbj.core.TIDORB)
                                   orb).objectToURL(proxy_push_suppliers[i]);
          }
          utils.writeIORString(reference, null);
        }
        break;

      // ConsumerAdmin pull_suppliers [-m (ior | url)] < consumer_admin.ior
      case _OP_LIST_PULL_SUPPLIERS:
        try
        {
            proxy_pull_suppliers = consumer_admin.obtain_pull_suppliers(); //pull_suppliers();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < proxy_pull_suppliers.length; i++)
        {
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(proxy_pull_suppliers[i]);
          }
          else
          {
            reference = ((es.tid.TIDorbj.core.TIDORB)
                                   orb).objectToURL(proxy_pull_suppliers[i]);
          }
          utils.writeIORString(reference, null);
        }
        break;

      case _OP_LIST_PUSH_SUPPLIER_IDS:
        try
        {
          proxy_names = consumer_admin.push_supplier_ids();
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

      // SupplierAdmin pull_suppliers [-m (ior | url)] < consumer_admin.ior
      case _OP_LIST_PULL_SUPPLIER_IDS:
        try
        {
          proxy_names = consumer_admin.pull_supplier_ids();
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

      // ConsumerAdmin create_consumer [name] [-p position] [-m (ior|url)]<consumer_admin.ior
      case _OP_CREATE_CONSUMER:
        org.omg.DistributionChannelAdmin.ConsumerAdmin consumer = null;
        try
        {
          if (admin_name == null)
          {
            criteria = new org.omg.CosLifeCycle.NVP[0];
          }
          else
          {
            criteria = new org.omg.CosLifeCycle.NVP[1];
            String name = new String("Id");
            org.omg.CORBA.Any any = orb.create_any();
            any.insert_string(admin_name);
            criteria[0] = new org.omg.CosLifeCycle.NVP(name, any);
          }
          if (order == -1)
          {
            consumer = consumer_admin.new_for_consumers(criteria);
          }
          else
          {
            consumer = 
                  consumer_admin.new_positioned_for_consumers(order, criteria);
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(consumer);
        }
        else
        {
          reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(consumer);
        }
        utils.writeIORString(reference, null);
        break;

      // ConsumerAdmin list_consumers [-m (ior | url)] < consumer_admin.ior
      case _OP_LIST_CONSUMERS:
        {
        org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers = null;
        try
        {
          consumers = consumer_admin.consumer_admins();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < consumers.length; i++)
        {
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(consumers[i]);
          }
          else
          {
            reference =
                 ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(consumers[i]);
          }
          utils.writeIORString(reference, null);
        }
        }
        break;

      // CoonsumerAdmin nlist_consumers < consumer_admin.ior
      case _OP_NLIST_CONSUMERS:
        {
        String[] consumers = null;
        try
        {
          consumers = consumer_admin.consumer_admin_ids();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < consumers.length; i++)
        {
          System.out.println(consumers[i]);
        }
        }
        break;

      // Channel find_consumer [name] <[-m (ior | url)]  channel.ior
      case _OP_FIND_CONSUMERS:
        {
        org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers = null;
        try
        {
          if (admin_name == null)
          {
            criteria = new org.omg.CosLifeCycle.NVP[0];
          }
          else
          {
            criteria = new org.omg.CosLifeCycle.NVP[1];
            String name = new String("Id");
            org.omg.CORBA.Any any = orb.create_any();
            any.insert_string(admin_name);
            criteria[0] = new org.omg.CosLifeCycle.NVP(name, any);
          }
          consumers = consumer_admin.find_for_consumers(criteria);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < consumers.length; i++)
        {
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(consumers[i]);
          }
          else
          {
            reference =
                 ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(consumers[i]);
          }
          utils.writeIORString(reference, null);
        }
        }
        break;
/*
      // ConsumerAdmin get_discriminator [-m (ior | url)] < consumer_admin.ior
      case _OP_GET_DISCRIMINATOR:
        try
        {
          discriminator = consumer_admin.forwarding_discriminator();
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

      // ConsumerAdmin get OperationalState < consumer_admin.ior
      // ConsumerAdmin get AdministrativeState < consumer_admin.ior
      // ConsumerAdmin get AssociatedCriteria Id < supplier_admin.ior
      // ConsumerAdmin get OperationMode < consumer_admin.ior
      // ConsumerAdmin get Discriminator [-m (ior | url)] < consumer_admin.ior
      // ConsumerAdmin get Order < consumer_admin.ior
      // ConsumerAdmin get OrderGap < consumer_admin.ior
      // ConsumerAdmin get ErrorHandler [-m (ior | url)] < consumer_admin.ior
      case _OP_GET:
        try
        {
          int _attr_value;
          if (attr_name == _ATTR_OPERATIONAL)
          {
             _attr_value = consumer_admin.operational_state().value();
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
             _attr_value = consumer_admin.administrative_state().value();
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
              criteria = consumer_admin.associated_criteria();
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
//               discriminator = consumer_admin.forwarding_discriminator();
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
          else if (attr_name == _OPERATION_MODE)
          {
            OperationMode mode = null;
            try
            {
              mode = consumer_admin.operation_mode();
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
            if (mode == OperationMode.notification)
            {
              System.out.println("notification");
            }
            else if (mode == OperationMode.distribution)
            {
              System.out.println("distribution");
            }
            else
            {
              System.out.println("unknown");
            }
          }
          else if (attr_name == _ORDER)
          {
            //int order = -1;
            try
            {
              order = consumer_admin.order();
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
            System.out.println(order);
          }
          else if (attr_name == _ORDER_GAP)
          {
            //int order_gap = -1;
            try
            {
              order_gap = consumer_admin.order_gap();
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
            System.out.println(order_gap);
          }
          else if (attr_name == _ERROR_HANDLER)
          {
            DistributionHandler handler = null;
            try
            {
              handler = consumer_admin.distribution_error_handler();
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
            if (ref_mode == _MODE_IOR)
            {
              reference = orb.object_to_string(handler);
            }
            else
            {
              reference =
                      ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(handler);
            }
            utils.writeIORString(reference, null);
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
      break;
      // ConsumerAdmin set AdministrativeState <new_state> < consumer_admin.ior
      case _OP_SET:
        try
        {
          if (attr_name == _ATTR_ADMINISTRATIVE)
          {
            if (attr_value.compareTo(ATTR_VALUE_LOCKED) == 0)
            {
              consumer_admin.administrative_state(AdministrativeState.locked);
            }
            else if (attr_value.compareTo(ATTR_VALUE_UNLOCKED) == 0)
            {
              consumer_admin.administrative_state(AdministrativeState.unlocked);
            }
            else //if (attr_value.compareTo(ATTR_VALUE_SUTTING_DOWN) == 0)
            {
              consumer_admin.administrative_state(
                                            AdministrativeState.shutting_down);
            }
          }
          else if (attr_name == _ORDER)
          {
            try
            {
              order = Integer.parseInt(attr_value);
            }
            catch (java.lang.NumberFormatException ex)
            {
              System.err.println("Parametro Order invalido, debe ser un int.");
              showHelp();
              System.exit(1);
            }
            try
            {
              consumer_admin.order(order);
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
          }
          else if (attr_name == _ORDER_GAP)
          {
            try
            {
              order_gap = Integer.parseInt(attr_value);
            }
            catch (java.lang.NumberFormatException ex)
            {
              System.err.println(
                              "Parametro OrderGap invalido, debe ser un int.");
              showHelp();
              System.exit(1);
            }
            try
            {
              consumer_admin.order_gap(order_gap);
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
          }
          else if (attr_name == _ERROR_HANDLER)
          {
            try
            {
              object_ref =
                         orb.string_to_object(utils.readIORString(attr_value));
              DistributionHandler handler =
                                DistributionHandlerHelper.narrow( object_ref );
              consumer_admin.distribution_error_handler(handler);
            }
            catch (Exception e)
            {
              e.printStackTrace();
              System.exit(1);
            }
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // ConsumerAdmin destroy < consumer_admin.ior
      case _OP_DESTROY:
        try
        {
          consumer_admin.destroy();
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
