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

import org.omg.CosEventComm.PushConsumer;
import org.omg.DistributionChannelAdmin.ProxyPushSupplierHelper;
// import org.omg.ConstraintAdmin.Discriminator;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;
import org.omg.ServiceManagement.OperationMode;
import es.tid.corba.ExceptionHandlerAdmin.DistributionHandler;
import es.tid.corba.ExceptionHandlerAdmin.DistributionHandlerHelper;

import es.tid.TIDorbj.core.TIDORB;

public class ProxyPushSupplier
{		
  private static final String NAME = "ProxyPushSupplier";

  private static final String OP_GET        = "get";
  private static final String OP_SET        = "set";
  private static final String OP_DISCONNECT = "disconnect";

  private static final int _OP_UNKNOWN    = -1;
  private static final int _OP_GET        =  0;
  private static final int _OP_SET        =  1;
  private static final int _OP_DISCONNECT =  2;

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

  private static final String PUSH_CONSUMER           = "PushConsumer";
  private static final String DISCRIMINATOR           = "Discriminator";
  private static final String OPERATION_MODE          = "OperationMode";
  private static final String ORDER                   = "Order";

  private static final int _ATTR_OPERATIONAL    = 0;
  private static final int _ATTR_ADMINISTRATIVE = 1;
  private static final int _PUSH_CONSUMER       = 2;
  private static final int _DISCRIMINATOR       = 3;
  private static final int _OPERATION_MODE      = 4;
  private static final int _ORDER               = 5;

  private static es.tid.corba.TIDDistrib.tools.Util utils = 
                                      new es.tid.corba.TIDDistrib.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
     "ProxyPushSupplier get OperationalState < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier get AdministrativeState < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier get PushConsumer < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier get Discriminator [-m (ior|url)] < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier get OperationMode < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier get Order < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier set AdministrativeState <new_state> < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier set Order <new_order> < proxy_push_supplier.ior");
    System.err.println(
     "ProxyPushSupplier disconnect < proxy_push_supplier.ior");
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
    int order = -1;
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
      // ProxyPushSupplier get OperationalState < proxy_push_supplier.ior
      // ProxyPushSupplier get AdministrativeState < proxy_push_supplier.ior
      // ProxyPushSupplier get discriminator < proxy_push_supplier.ior
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
        else if (_attr_name.compareTo(PUSH_CONSUMER) == 0)
        {
          attr_name = _PUSH_CONSUMER;
        }
        else
        {
          System.err.println( "Atributo invalido (" +
                ATTR_ADMINISTRATIVE + " | " + ATTR_OPERATIONAL + " | " +
                PUSH_CONSUMER+ " | " +DISCRIMINATOR + " | " + 
                OPERATION_MODE + " | " + ORDER + ")" );

          showHelp();
          System.exit(1);
        }
      }
      // ProxyPushSupplier set AdministrativeState <new_state> < proxy_p_c.ior
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
        else
        {
          System.err.println( "Atributo invalido (" +
                                    ATTR_ADMINISTRATIVE + " | " + ORDER + ")");
          showHelp();
          System.exit(1);
        }
      }
      // ProxyPushSupplier destroy < proxy_push_supplier.ior
      else if ( args[i].compareTo(OP_DISCONNECT) == 0 )
      {
        operation = _OP_DISCONNECT;
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
        System.err.println("Parametro invalido.");
        showHelp();
        System.exit(0);
      }
    }

// Debug 
/*
    System.err.println("Operation: " + operation);
    System.err.println("Ref mode: " + ref_mode);
    System.err.println("Attr. name: " + attr_name);
    System.err.println("Attr. value: " + attr_value);
*/

    if (operation == _OP_UNKNOWN)
    {
      System.err.println( "Operacion invalida (get|set|disconnect)" );
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    org.omg.DistributionChannelAdmin.ProxyPushSupplier proxy_push_supplier=null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      proxy_push_supplier = ProxyPushSupplierHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    PushConsumer push_consumer = null;
//     Discriminator discriminator = null;

    switch (operation)
    {
      // ProxyPushSupplier get OperationalState < proxy_push_supplier.ior
      // ProxyPushSupplier get AdministrativeState < proxy_push_supplier.ior
      // ProxyPushSupplier get Discriminator < proxy_push_supplier.ior
      case _OP_GET:
        try
        {
          int _attr_value;
          if (attr_name == _ATTR_OPERATIONAL)
          {
             _attr_value = proxy_push_supplier.operational_state().value();
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
             _attr_value = proxy_push_supplier.administrative_state().value();
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
//           else if (attr_name == _DISCRIMINATOR)
//           {
//             try
//             {
//               discriminator = proxy_push_supplier.forwarding_discriminator();
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
//             break;
//           }
          else if (attr_name == _OPERATION_MODE)
          {
            OperationMode mode = null;
            try
            {
              mode = proxy_push_supplier.operation_mode();
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
              order = proxy_push_supplier.order();
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
            System.out.println(order);
          }
         else //if (attr_name == _PUSH_CONSUMER)
          {
            try
            {
              push_consumer = proxy_push_supplier.getPushConsumer();
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
            if (ref_mode == _MODE_IOR)
            {
              reference = orb.object_to_string(push_consumer);
            }
            else
            {
              reference =
                ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(push_consumer);
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
      // ProxyPushSupplier set AdministrativeState <new_state> < proxy_p_c.ior
      case _OP_SET:
        try
        {
          if (attr_name == _ATTR_ADMINISTRATIVE)
          {
            if (attr_value.compareTo(ATTR_VALUE_LOCKED) == 0)
            {
              proxy_push_supplier.administrative_state(
                                                  AdministrativeState.locked );
            }
            else if (attr_value.compareTo(ATTR_VALUE_UNLOCKED) == 0)
            {
              proxy_push_supplier.administrative_state(
                                                AdministrativeState.unlocked );
            }
            else //if (attr_value.compareTo(ATTR_VALUE_SUTTING_DOWN) == 0)
            {
              proxy_push_supplier.administrative_state(
                                           AdministrativeState.shutting_down );
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
              proxy_push_supplier.order(order);
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
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

      // ProxyPushSupplier disconnect < proxy_push_supplier.ior
      case _OP_DISCONNECT:
        try
        {
          proxy_push_supplier.disconnect_push_supplier();
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
