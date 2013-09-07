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

import org.omg.CosEventComm.PullSupplier;
import org.omg.NotificationChannelAdmin.ProxyPullConsumerHelper;
//import org.omg.ConstraintAdmin.Discriminator;
import org.omg.ServiceManagement.OperationalState;
import org.omg.ServiceManagement.AdministrativeState;

import es.tid.TIDorbj.core.TIDORB;

public class ProxyPullConsumer
{		
  private static final String NAME = "ProxyPullConsumer";

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

  private static final String PULL_SUPPLIER           = "PullSupplier";
  private static final String DISCRIMINATOR           = "Discriminator";

  private static final int _ATTR_OPERATIONAL    = 0;
  private static final int _ATTR_ADMINISTRATIVE = 1;
  private static final int _PULL_SUPPLIER       = 2;
  private static final int _DISCRIMINATOR       = 3;

  private static es.tid.corba.TIDNotif.tools.Util utils = 
                                        new es.tid.corba.TIDNotif.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
     "ProxyPullConsumer get OperationalState < proxy_pull_consumer.ior");
    System.err.println(
     "ProxyPullConsumer get AdministrativeState < proxy_pull_consumer.ior");
    System.err.println(
     "ProxyPushSupplier get PullSupplier < proxy_pull_consumer.ior");
    System.err.println(
     "ProxyPullConsumer get Discriminator < proxy_pull_consumer.ior");
    System.err.println(
     "ProxyPullConsumer set AdministrativeState <new_state> < proxy_pull_consumer.ior");
    System.err.println(
     "ProxyPullConsumer disconnect < proxy_pull_consumer.ior");
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
      // ProxyPullConsumer get OperationalState < proxy_pull_consumer.ior
      // ProxyPullConsumer get AdministrativeState < proxy_pull_consumer.ior
      // ProxyPullConsumer get discriminator < proxy_pull_consumer.ior
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
        else if (_attr_name.compareTo(PULL_SUPPLIER) == 0)
        {
          attr_name = _PULL_SUPPLIER;
        }
        else
        {
          System.err.println( "Atributo invalido (" + ATTR_ADMINISTRATIVE + 
                                             " | " + ATTR_OPERATIONAL +
                                             " | " + PULL_SUPPLIER + ")" );
          showHelp();
          System.exit(1);
        }
      }
      // ProxyPullConsumer set AdministrativeState <new_state> < proxy_p_c.ior
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
      // ProxyPullConsumer destroy < proxy_pull_consumer.ior
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
    org.omg.NotificationChannelAdmin.ProxyPullConsumer proxy_pull_consumer=null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      proxy_pull_consumer = ProxyPullConsumerHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    PullSupplier pull_supplier = null;
    //Discriminator discriminator = null;

    switch (operation)
    {
      // ProxyPullConsumer get OperationalState < proxy_pull_consumer.ior
      // ProxyPullConsumer get AdministrativeState < proxy_pull_consumer.ior
      // ProxyPullConsumer get Discriminator < proxy_pull_consumer.ior
      case _OP_GET:
        try
        {
          int _attr_value;
          if (attr_name == _ATTR_OPERATIONAL)
          {
             _attr_value = proxy_pull_consumer.operational_state().value();
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
             _attr_value = proxy_pull_consumer.administrative_state().value();
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
//               discriminator = proxy_pull_consumer.forwarding_discriminator();
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
          else //if (attr_name == _PULL_SUPPLIER)
          {
            try
            {
              pull_supplier = proxy_pull_consumer.getPullSupplier();
            }
            catch ( java.lang.Exception ex )
            {
              ex.printStackTrace();
              System.exit(1);
            }
            if (ref_mode == _MODE_IOR)
            {
              reference = orb.object_to_string(pull_supplier);
            }
            else
            {
              reference = 
                ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(pull_supplier);
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
      // ProxyPullConsumer set AdministrativeState <new_state> < proxy_p_c.ior
      case _OP_SET:
        try
        {
          if (attr_name == _ATTR_ADMINISTRATIVE)
          {
            if (attr_value.compareTo(ATTR_VALUE_LOCKED) == 0)
            {
              proxy_pull_consumer.administrative_state(
                                                  AdministrativeState.locked );
            }
            else if (attr_value.compareTo(ATTR_VALUE_UNLOCKED) == 0)
            {
              proxy_pull_consumer.administrative_state(
                                                AdministrativeState.unlocked );
            }
            else //if (attr_value.compareTo(ATTR_VALUE_SUTTING_DOWN) == 0)
            {
              proxy_pull_consumer.administrative_state(
                                           AdministrativeState.shutting_down );
            }
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // ProxyPullConsumer disconnect < proxy_pull_consumer.ior
      case _OP_DISCONNECT:
        try
        {
          proxy_pull_consumer.disconnect_pull_consumer();
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
