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

import org.omg.DistributionChannelAdmin.DistributionChannelFactory;
import org.omg.DistributionChannelAdmin.DistributionChannelFactoryHelper;
import org.omg.DistributionChannelAdmin.DistributionChannel;
import org.omg.DistributionChannelAdmin.DistributionChannelHelper;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;
import org.omg.ServiceManagement.OperationMode;

import es.tid.TIDorbj.core.TIDORB;

public class Channel
{		
  private static final String NAME = "Channel";

  private static final String OP_CREATE_SUPPLIER = "create_supplier";
  private static final String OP_CREATE_CONSUMER = "create_consumer";
  private static final String OP_FIND_SUPPLIER   = "find_supplier";
  private static final String OP_FIND_CONSUMER   = "find_consumer";
  private static final String OP_LIST_SUPPLIERS  = "list_suppliers";
  private static final String OP_LIST_CONSUMERS  = "list_consumers";
  private static final String OP_NLIST_SUPPLIERS = "nlist_suppliers";
  private static final String OP_NLIST_CONSUMERS = "nlist_consumers";
  private static final String OP_GET             = "get";
  private static final String OP_SET             = "set";
  private static final String OP_DESTROY         = "destroy";

  private static final int _OP_UNKNOWN         = -1;
  private static final int _OP_CREATE_SUPPLIER =  0;
  private static final int _OP_CREATE_CONSUMER =  1;
  private static final int _OP_FIND_SUPPLIER   =  2;
  private static final int _OP_FIND_CONSUMER   =  3;
  private static final int _OP_LIST_SUPPLIERS  =  4;
  private static final int _OP_LIST_CONSUMERS  =  5;
  private static final int _OP_NLIST_SUPPLIERS =  6;
  private static final int _OP_NLIST_CONSUMERS =  7;
  private static final int _OP_GET             =  8;
  private static final int _OP_SET             =  9;
  private static final int _OP_DESTROY         = 10;

  private static final String MODE_IOR = "ior";
  private static final String MODE_URL = "url";

  private static final int _MODE_IOR = 0;
  private static final int _MODE_URL = 1;

  private static final String NOTIFICATION_TYPE = "notification";
  private static final String DISTRIBUTION_TYPE = "distribution";

  private static final int _NOTIFICATION_TYPE = 0;
  private static final int _DISTRIBUTION_TYPE = 1;

  private static final String ATTR_OPERATIONAL    = "OperationalState";
  private static final String ATTR_VALUE_DISABLED = "disabled";
  private static final String ATTR_VALUE_ENABLED  = "enabled";

  private static final String ATTR_ADMINISTRATIVE     = "AdministrativeState";
  private static final String ATTR_VALUE_LOCKED       = "locked";
  private static final String ATTR_VALUE_UNLOCKED     = "unlocked";
  private static final String ATTR_VALUE_SUTTING_DOWN = "shutting_down";

  private static final String OPERATION_MODE          = "OperationMode";

  private static final int _ATTR_OPERATIONAL    = 0;
  private static final int _ATTR_ADMINISTRATIVE = 1;
  private static final int _OPERATION_MODE      = 2;

  private static es.tid.corba.TIDDistrib.tools.Util utils = 
                                      new es.tid.corba.TIDDistrib.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
       "Channel create_supplier [name] [-m (ior | url)] < channel.ior");
    System.err.println(
       "Channel create_consumer [name] [-t (notification|distribution)] [-m (ior|url)] <channel.ior");
    System.err.println(
       "Channel find_supplier [name] [-global] [-m (ior | url)] < channel.ior");
    System.err.println(
       "Channel find_consumer [name] [-global] [-m (ior | url)] < channel.ior");
    System.err.println(
       "Channel list_suppliers [-global] [-m (ior | url)] < channel.ior");
    System.err.println(
       "Channel list_consumers [-global] [-m (ior | url)] < channel.ior");
    System.err.println(
       "Channel nlist_suppliers [-global] < channel.ior");
    System.err.println(
       "Channel nlist_consumers [-global] < channel.ior");
    System.err.println(
       "Channel get OperationMode < channel.ior");
    System.err.println(
       "Channel get \"OperationalState\" < channel.ior");
    System.err.println(
       "Channel get \"AdministrativeState\" < channel.ior");
    System.err.println(
       "Channel set \"AdministrativeState\" <new_state> < channel.ior");
    System.err.println(
       "Channel destroy < channel.ior");
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
    String admin_name = null;
    int ref_mode = _MODE_IOR;
    boolean global = false;
    boolean debug = false;
    int attr_name = -1;
    String attr_value = null;
    int cadmin_type = _DISTRIBUTION_TYPE;

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
      if ( args[i].compareTo("-t") == 0 )
      {
        String _cadmin_type = new String(args[++i]);

        if (_cadmin_type.compareTo(NOTIFICATION_TYPE) == 0)
        {
          cadmin_type = _NOTIFICATION_TYPE;
        }
        else if (_cadmin_type.compareTo(DISTRIBUTION_TYPE) == 0)
        {
          cadmin_type = _DISTRIBUTION_TYPE;
        }
        else
        {
         System.err.println("Tipo invalido [-t (notification | distribution)]");
         showHelp();
         System.exit(1);
        }
      }
      // -m ( ior | url )
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
      // -global
      else if ( args[i].compareTo("-global") == 0 )
      {
        global = true;
      }
      // -debug
      else if ( args[i].compareTo("-debug") == 0 )
      {
        debug = true;
      }
      // Channel create_supplier [name] [-m (ior | url)] < channel.ior
      else if ( args[i].compareTo(OP_CREATE_SUPPLIER) == 0 )
      {
        operation = _OP_CREATE_SUPPLIER;
      }
      // Channel create_consumer [name] [-m (ior | url)] < channel.ior
      else if ( args[i].compareTo(OP_CREATE_CONSUMER) == 0 )
      {
        operation = _OP_CREATE_CONSUMER;
      }
      // Channel find_supplier [name] [-m (ior | url)] < channel.ior
      else if ( args[i].compareTo(OP_FIND_SUPPLIER) == 0 )
      {
        operation = _OP_FIND_SUPPLIER;
      }
      // Channel find_consumer [name] <[-m (ior | url)]  channel.ior
      else if ( args[i].compareTo(OP_FIND_CONSUMER) == 0 )
      {
        operation = _OP_FIND_CONSUMER;
      }
      // Channel list_suppliers [-m (ior | url)] < channel.ior
      else if ( args[i].compareTo(OP_LIST_SUPPLIERS) == 0 )
      {
        operation = _OP_LIST_SUPPLIERS;
      }
      // Channel list_consumers [-m (ior | url)] < channel.ior
      else if ( args[i].compareTo(OP_LIST_CONSUMERS) == 0 )
      {
        operation = _OP_LIST_CONSUMERS;
      }
      // Channel nlist_suppliers [-m (ior | url)] < channel.ior
      else if ( args[i].compareTo(OP_NLIST_SUPPLIERS) == 0 )
      {
        operation = _OP_NLIST_SUPPLIERS;
      }
      // Channel nlist_consumers [-m (ior | url)] < channel.ior
      else if ( args[i].compareTo(OP_NLIST_CONSUMERS) == 0 )
      {
        operation = _OP_NLIST_CONSUMERS;
      }
      // Channel get "OperationalState" < channel.ior
      // Channel get "AdministrativeState" < channel.ior
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
        else if (_attr_name.compareTo(OPERATION_MODE) == 0)
        {
          attr_name = _OPERATION_MODE;
        }
        else
        {
          System.err.println( "Atributo invalido (" + 
                       ATTR_ADMINISTRATIVE + " | " + ATTR_OPERATIONAL + " | " +
                       OPERATION_MODE + ")" );
          showHelp();
          System.exit(1);
        }
      }
      // Channel set "OperationalState" <new_state> < channel.ior
      // Channel set "AdministrativeState" <new_state> < channel.ior
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
      // Channel destroy < channel.ior
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
        admin_name = new String(args[i]);
      }
    }

    if (debug)
    {
      System.err.println("Operation: " + operation);
      System.err.println("Admin name: " + admin_name);
      System.err.println("Ref mode: " + ref_mode);
      System.err.println("Attr. name: " + attr_name);
      System.err.println("Attr. value: " + attr_value);
      System.err.println("Type: " + cadmin_type);
      System.err.println("Global: " + global);
    }

    if (operation == _OP_UNKNOWN)
    {
      System.err.println(
         "Operacion invalida (create_supplier|create_consumer|find_supplier|");
      System.err.println(
         "                    find_consumer|list_suppliers|list_consumers|");
      System.err.println(
         "                    get|set|destroy)");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    int channel_type = _DISTRIBUTION_TYPE;
    DistributionChannel channel = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      channel = DistributionChannelHelper.narrow( object_ref );
      channel_type = channel.operation_mode().value();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    //org.omg.DistributionChannelAdmin.SupplierAdmin supplier = null;
    //org.omg.DistributionChannelAdmin.ConsumerAdmin consumer = null;
    //org.omg.DistributionChannelAdmin.SupplierAdmin[] suppliers = null;
    //org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers = null;

    switch (operation)
    {
      // Channel create_supplier [name] [-m (ior | url)] < channel.ior
      case _OP_CREATE_SUPPLIER:
      {
        org.omg.NotificationChannelAdmin.SupplierAdmin supplier = null;
        try
        {
          org.omg.CosLifeCycle.NVP[] criteria = null;
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
          supplier = channel.new_for_suppliers(criteria);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(supplier);
        }
        else
        {
          reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(supplier);
        }
        utils.writeIORString(reference, null);
        break;
      }

      // Channel create_consumer [name] [-m (ior | url)] < channel.ior
      case _OP_CREATE_CONSUMER:
      {
        try
        {
          org.omg.CosLifeCycle.NVP[] criteria = null;
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
          if (channel_type == _NOTIFICATION_TYPE)
          {
            org.omg.NotificationChannelAdmin.ConsumerAdmin consumer = null;
            consumer = channel.new_for_consumers( criteria );
            if (ref_mode == _MODE_IOR)
            {
              reference = orb.object_to_string(consumer);
            }
            else
            {
              reference = 
                     ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(consumer);
            }
          }
          else
          {
            org.omg.DistributionChannelAdmin.ConsumerAdmin consumer = null;
            if (cadmin_type == _NOTIFICATION_TYPE)
            {
              consumer = channel.new_operated_for_consumers(
                                        criteria, OperationMode.notification );
            }
            else //if (cadmin_type == _DISTRIBUTION_TYPE)
            {
              consumer = channel.new_operated_for_consumers(
                                        criteria, OperationMode.distribution );
            }
            if (ref_mode == _MODE_IOR)
            {
              reference = orb.object_to_string(consumer);
            }
            else
            {
              reference = 
                     ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(consumer);
            }
          }
          utils.writeIORString(reference, null);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        break;
      }

      // Channel find_supplier [name] [-m (ior | url)] < channel.ior
      case _OP_FIND_SUPPLIER:
      {
        try
        {
          org.omg.CosLifeCycle.NVP[] criteria = null;
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
          if (global)
          {
            org.omg.DistributionChannelAdmin.SupplierAdmin[] suppliers = null;
            suppliers = channel.new_find_for_suppliers(criteria);
            for (int i = 0; i < suppliers.length; i++)
            {
              if (ref_mode == _MODE_IOR)
              {
                reference = orb.object_to_string(suppliers[i]);
              }
              else
              {
                reference = 
                 ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(suppliers[i]);
              }
              utils.writeIORString(reference, null);
            }
          }
          else
          {
            org.omg.NotificationChannelAdmin.SupplierAdmin[] suppliers = null;
            suppliers = channel.find_for_suppliers(criteria);
            for (int i = 0; i < suppliers.length; i++)
            {
              if (ref_mode == _MODE_IOR)
              {
                reference = orb.object_to_string(suppliers[i]);
              }
              else
              {
                reference = 
                 ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(suppliers[i]);
              }
              utils.writeIORString(reference, null);
            }
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;
      }

      // Channel find_consumer [name] <[-m (ior | url)]  channel.ior
      case _OP_FIND_CONSUMER:
      {
        try
        {
          org.omg.CosLifeCycle.NVP[] criteria = null;
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
          if (global)
          {
            org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers = 
                                      channel.new_find_for_consumers(criteria);
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
          else
          {
            org.omg.NotificationChannelAdmin.ConsumerAdmin[] consumers = 
                                          channel.find_for_consumers(criteria);
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
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;
      }

      // Channel list_suppliers [-m (ior | url)] < channel.ior
      case _OP_LIST_SUPPLIERS:
        try
        {
          if (global)
          {
            org.omg.DistributionChannelAdmin.SupplierAdmin[] suppliers = null;
            suppliers = channel.new_supplier_admins();
            for (int i = 0; i < suppliers.length; i++)
            {
              if (ref_mode == _MODE_IOR)
              {
                reference = orb.object_to_string(suppliers[i]);
              }
              else
              {
                reference = 
                 ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(suppliers[i]);
              }
              utils.writeIORString(reference, null);
            }
          }
          else
          {
            org.omg.NotificationChannelAdmin.SupplierAdmin[] suppliers = null;
            suppliers = channel.supplier_admins();
            for (int i = 0; i < suppliers.length; i++)
            {
              if (ref_mode == _MODE_IOR)
              {
                reference = orb.object_to_string(suppliers[i]);
              }
              else
              {
                reference = 
                 ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(suppliers[i]);
              }
              utils.writeIORString(reference, null);
            }
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Channel list_consumers [-m (ior | url)] < channel.ior
      case _OP_LIST_CONSUMERS:
        try
        {
          if (global)
          {
            org.omg.DistributionChannelAdmin.ConsumerAdmin[] consumers = null;
            consumers = channel.new_consumer_admins();
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
          else
          {
            org.omg.NotificationChannelAdmin.ConsumerAdmin[] consumers = null;
            consumers = channel.consumer_admins();
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
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Channel nlist_suppliers [-m (ior | url)] < channel.ior
      case _OP_NLIST_SUPPLIERS:
        try
        {
          String[] suppliers = null;
          if (global)
          {
            suppliers = channel.new_supplier_admin_ids();
          }
          else
          {
            suppliers = channel.supplier_admin_ids();
          }
          for (int i = 0; i < suppliers.length; i++)
          {
            System.out.println(suppliers[i]);
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Channel nlist_consumers [-m (ior | url)] < channel.ior
      case _OP_NLIST_CONSUMERS:
        try
        {
          String[] consumers = null;
          if (global)
          {
            consumers = channel.new_consumer_admin_ids();
          }
          else
          {
            consumers = channel.consumer_admin_ids();
          }
          for (int i = 0; i < consumers.length; i++)
          {
            System.out.println(consumers[i]);
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Channel get "OperationalState" < channel.ior
      // Channel get "AdministrativeState" < channel.ior
      case _OP_GET:
        try
        {
          int _attr_value;
          if (attr_name == _ATTR_OPERATIONAL)
          {
             _attr_value = channel.operational_state().value();
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
             _attr_value = channel.administrative_state().value();
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
          else if (attr_name == _OPERATION_MODE)
          {
            OperationMode mode = null;
            try
            {
              mode = channel.operation_mode();
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
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Channel set "AdministrativeState" <new_state> < channel.ior
      case _OP_SET:
        try
        {
          if (attr_name == _ATTR_ADMINISTRATIVE)
          {
            if (attr_value.compareTo(ATTR_VALUE_LOCKED) == 0)
            {
              channel.administrative_state(AdministrativeState.locked);
            }
            else if (attr_value.compareTo(ATTR_VALUE_UNLOCKED) == 0)
            {
              channel.administrative_state(AdministrativeState.unlocked);
            }
            else //if (attr_value.compareTo(ATTR_VALUE_SUTTING_DOWN) == 0)
            {
              channel.administrative_state(AdministrativeState.shutting_down);
            }
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Channel destroy < channel.ior
      case _OP_DESTROY:
        try
        {
          channel.destroy();
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
