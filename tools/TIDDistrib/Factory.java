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

import org.omg.ServiceManagement.OperationMode;
import org.omg.DistributionChannelAdmin.DistributionChannelFactory;
import org.omg.DistributionChannelAdmin.DistributionChannelFactoryHelper;
import org.omg.DistributionChannelAdmin.DistributionChannel;
import org.omg.DistributionChannelAdmin.DistributionChannelHelper;

import es.tid.TIDorbj.core.TIDORB;

public class Factory
{		
  private static final String NAME = "Factory";

  private static final String OP_CREATE  = "create";
  private static final String OP_FIND    = "find";
  private static final String OP_DESTROY = "destroy";
  private static final String OP_LIST    = "list";
  private static final String OP_NLIST   = "nlist";

  private static final int _OP_UNKNOWN = -1;
  private static final int _OP_CREATE  =  0;
  private static final int _OP_FIND    =  1;
  private static final int _OP_DESTROY =  2;
  private static final int _OP_LIST    =  3;
  private static final int _OP_NLIST   =  4;

  private static final String MODE_IOR = "ior";
  private static final String MODE_URL = "url";

  private static final int _MODE_IOR = 0;
  private static final int _MODE_URL = 1;

  private static final String NOTIFICATION_TYPE = "notification";
  private static final String DISTRIBUTION_TYPE = "distribution";

  private static final int _NOTIFICATION_TYPE = 0;
  private static final int _DISTRIBUTION_TYPE = 1;

  private static es.tid.corba.TIDDistrib.tools.Util utils = 
                                      new es.tid.corba.TIDDistrib.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println("Factory create [name] [-t (notification | distribution)] [-m (ior | url)] < factory.ior");
    System.err.println("Factory find name [-m (ior | url)] < factory.ior");
    System.err.println("Factory list < factory.ior");
    System.err.println("Factory nlist < factory.ior");
    System.err.println("Factory destroy name < factory.ior");
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
    String channel_name = null;
    int ref_mode = _MODE_IOR;
    int channel_type = _DISTRIBUTION_TYPE;

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
        String _channel_type = new String(args[++i]);

        if (_channel_type.compareTo(NOTIFICATION_TYPE) == 0)
        {
          channel_type = _NOTIFICATION_TYPE;
        }
        else if (_channel_type.compareTo(DISTRIBUTION_TYPE) == 0)
        {
          channel_type = _DISTRIBUTION_TYPE;
        }
        else
        {
         System.err.println("Tipo invalido: " + _channel_type + " [-t (" +
                        NOTIFICATION_TYPE + " | " + DISTRIBUTION_TYPE + ")]" );
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
      else if ( args[i].compareTo(OP_CREATE) == 0 )
      {
        operation = _OP_CREATE;
      }
      else if ( args[i].compareTo(OP_DESTROY) == 0 )
      {
        operation = _OP_DESTROY;
      }
      else if ( args[i].compareTo(OP_FIND) == 0 )
      {
        operation = _OP_FIND;
      }
      else if ( args[i].compareTo(OP_LIST) == 0 )
      {
        operation = _OP_LIST;
      }
      else if ( args[i].compareTo(OP_NLIST) == 0 )
      {
        operation = _OP_NLIST;
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
        channel_name = new String(args[i]);
      }
    }

// Debug 
/*
    System.err.println("Operation: " + operation);
    System.err.println("Channel name: " + channel_name);
    System.err.println("Ref mode: " + ref_mode);
*/

    if (operation == _OP_UNKNOWN)
    {
      System.err.println("Operacion invalida (create|find|list|nlist|destroy)");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    DistributionChannelFactory factory = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      factory = DistributionChannelFactoryHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    DistributionChannel canal = null;
    DistributionChannel[] canales = null;
    String[] ncanales = null;

    switch (operation)
    {
      // Factory -create [name] [-m (ior | url)] < factory.ior
      case _OP_CREATE:
        try
        {
          OperationMode operation_mode = null;
          if (channel_type == _NOTIFICATION_TYPE)
          {
            operation_mode = OperationMode.notification;
          }
          else //if (channel_type == _DISTRIBUTION_TYPE)
          {
            operation_mode = OperationMode.distribution;
          }
          if (channel_name == null)
          {
            canal = factory.create_distribution_channel(10,10, operation_mode);
          }
          else
          {
            canal = factory.new_create_distribution_channel(
                                        channel_name, 10, 10, operation_mode );
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(canal);
        }
        else
        {
          reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(canal);
        }
        utils.writeIORString(reference, null);
        break;

      // Factory -destroy name < factory.ior
      case _OP_DESTROY:
        if (channel_name == null)
        {
          System.err.println("Especificar un nombre de canal.");
          showHelp();
          System.exit(1);
        }
        try
        {
          canal = factory.get_distribution_channel( channel_name );
          canal.destroy();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Factory -find [name] [-m (ior | url)] < factory.ior
      case _OP_FIND:
        if (channel_name == null)
        {
          System.err.println("Especificar un nombre de canal.");
          showHelp();
          System.exit(1);
        }
        try
        {
          canal = factory.get_distribution_channel( channel_name );
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(canal);
        }
        else
        {
          reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(canal);
        }
        utils.writeIORString(reference, null);
        break;

      // Factory -list < factory.ior
      case _OP_LIST:
        try
        {
          canales = factory.distribution_channels();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        for (int i = 0; i < canales.length; i++)
        {
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(canales[i]);
          }
          else
          {
            reference = 
                   ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(canales[i]);
          }
          utils.writeIORString(reference, null);
        }
        break;

      // Factory -nlist < factory.ior
      case _OP_NLIST:
        try
        {
          ncanales = factory.new_distribution_channels();
          canales = factory.distribution_channels();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        for (int i = 0; i < ncanales.length; i++)
        {
          String creation_date = null;
          String creation_time = null;
          try
          {
            creation_date = canales[i].creation_date();
            creation_time = canales[i].creation_time();
            System.out.println( ncanales[i] + "\t" + 
                                (canales[i].operation_mode().value() == 0?
                                "NOTIFICATION":"DISTRIBUTION") + "\t" +
                                creation_date + "\t" + creation_time );
          }
          catch ( java.lang.Exception ex )
          {
            ex.printStackTrace();
            System.exit(1);
          }
        }
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
