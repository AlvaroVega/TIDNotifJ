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

//import org.omg.NotificationService.NotificationAdmin;
//import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.NotificationChannelAdmin.NotificationChannelFactory;
import org.omg.NotificationChannelAdmin.NotificationChannelFactoryHelper;
import org.omg.NotificationChannelAdmin.NotificationChannel;
import org.omg.NotificationChannelAdmin.NotificationChannelHelper;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;

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
  private static final int _OP_GET             =  6;
  private static final int _OP_SET             =  7;
  private static final int _OP_DESTROY         =  8;

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

  private static final int _ATTR_OPERATIONAL    = 0;
  private static final int _ATTR_ADMINISTRATIVE = 1;

  private static es.tid.corba.TIDNotif.tools.Util utils = 
                                        new es.tid.corba.TIDNotif.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
             "Channel create_supplier [name] [-m (ior | url)] < channel.ior");
    System.err.println(
             "Channel create_consumer [name] [-m (ior | url)] < channel.ior");
    System.err.println(
             "Channel find_supplier [name] [-m (ior | url)] < channel.ior");
    System.err.println(
             "Channel find_consumer [name] [-m (ior | url)] < channel.ior");
    System.err.println(
             "Channel list_suppliers [-m (ior | url)] < channel.ior");
    System.err.println(
             "Channel list_consumers [-m (ior | url)] < channel.ior");
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
        else
        {
          System.err.println( "Atributo invalido (" + 
                        ATTR_ADMINISTRATIVE + " | " + ATTR_OPERATIONAL + ")");
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

/*
        if (_attr_name.compareTo(ATTR_OPERATIONAL) == 0)
        {
          attr_name = _ATTR_OPERATIONAL;
          if ((attr_value.compareTo(ATTR_VALUE_DISABLED) != 0) &&
              (attr_value.compareTo(ATTR_VALUE_ENABLED) != 0))
          {
            System.err.println( "Valor invalido (" + 
                      ATTR_VALUE_DISABLED + " | " + ATTR_VALUE_ENABLED + ")" );
            showHelp();
            System.exit(1);
          }
        }
        else 
*/
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
        "Operacion invalida (create_supplier|create_consumer|find_supplier|");
      System.err.println(
        "                    find_consumer|list_suppliers|list_consumers|");
      System.err.println(
        "                    get|set|destroy)");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    NotificationChannel channel = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      channel = NotificationChannelHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    org.omg.NotificationChannelAdmin.SupplierAdmin supplier = null;
    org.omg.NotificationChannelAdmin.ConsumerAdmin consumer = null;
    org.omg.NotificationChannelAdmin.SupplierAdmin[] suppliers = null;
    org.omg.NotificationChannelAdmin.ConsumerAdmin[] consumers = null;

    switch (operation)
    {
      // Channel create_supplier [name] [-m (ior | url)] < channel.ior
      case _OP_CREATE_SUPPLIER:
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

      // Channel create_consumer [name] [-m (ior | url)] < channel.ior
      case _OP_CREATE_CONSUMER:
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
          consumer = channel.new_for_consumers(criteria);
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

      // Channel find_supplier [name] [-m (ior | url)] < channel.ior
      case _OP_FIND_SUPPLIER:
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
          suppliers = channel.find_for_suppliers(criteria);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
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
        break;

      // Channel find_consumer [name] <[-m (ior | url)]  channel.ior
      case _OP_FIND_CONSUMER:
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
          consumers = channel.find_for_consumers(criteria);
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
        break;

      // Channel list_suppliers [-m (ior | url)] < channel.ior
      case _OP_LIST_SUPPLIERS:
        try
        {
          suppliers = channel.supplier_admins();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
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
        break;

      // Channel list_consumers [-m (ior | url)] < channel.ior
      case _OP_LIST_CONSUMERS:
        try
        {
          consumers = channel.consumer_admins();
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
          else // _ATTR_ADMINISTRATIVE
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
/*
          if (attr_name == _ATTR_OPERATIONAL)
          {
            if (attr_value.compareTo(ATTR_VALUE_DISABLED) == 0)
            {
              channel.operational_state(OperationalState.disabled);
            }
            else //if (attr_value.compareTo(ATTR_VALUE_ENABLED) == 0)
            {
              channel.operational_state(OperationalState.enabled);
            }
          }
          else
*/
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
