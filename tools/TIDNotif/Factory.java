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

import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.NotificationChannelAdmin.NotificationChannelFactory;
import org.omg.NotificationChannelAdmin.NotificationChannelFactoryHelper;
import org.omg.NotificationChannelAdmin.NotificationChannel;
import org.omg.NotificationChannelAdmin.NotificationChannelHelper;

import es.tid.TIDorbj.core.TIDORB;

public class Factory
{		
  private static final String NAME = "Factory";

  private static final String OP_CREATE  = "create";
  private static final String OP_FIND    = "find";
  private static final String OP_DESTROY = "destroy";
  private static final String OP_LIST    = "list";
  private static final String OP_NLIST   = "nlist";
  private static final String OP_CREATES  = "creates";

  private static final int _OP_UNKNOWN = -1;
  private static final int _OP_CREATE  =  0;
  private static final int _OP_FIND    =  1;
  private static final int _OP_DESTROY =  2;
  private static final int _OP_LIST    =  3;
  private static final int _OP_NLIST   =  4;
  private static final int _OP_CREATES =  5;

  private static final String MODE_IOR = "ior";
  private static final String MODE_URL = "url";

  private static final int _MODE_IOR = 0;
  private static final int _MODE_URL = 1;

  private static es.tid.corba.TIDNotif.tools.Util utils = 
                                        new es.tid.corba.TIDNotif.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println("Factory create [name] [-m (ior | url)] < factory.ior");
    System.err.println("Factory destroy name < factory.ior");
    System.err.println("Factory find name [-m (ior | url)] < factory.ior");
    System.err.println("Factory list < factory.ior");
    System.err.println("Factory nlist < factory.ior");
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
    int num_channels = 0;
    int ref_mode = _MODE_IOR;

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
      else if ( args[i].compareTo(OP_CREATES) == 0 )
      {
        operation = _OP_CREATES;
        try
        {
          num_channels = Integer.parseInt(args[++i]);
        }
        catch (java.lang.NumberFormatException ex)
        {
          System.err.println("Parametro num_channels invalido, debe ser int.");
          System.exit(1);
        }
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
    NotificationChannelFactory factory = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      factory = NotificationChannelFactoryHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    org.omg.NotificationChannelAdmin.NotificationChannel canal = null;
    org.omg.NotificationChannelAdmin.NotificationChannel[] canales = null;
    String[] ncanales = null;

    switch (operation)
    {
      // Factory -create [name] [-m (ior | url)] < factory.ior
      case _OP_CREATE:
        try
        {
          if (channel_name == null)
          {
            canal = factory.create_notification_channel( 10, 10 );
          }
          else
          {
            canal = factory.new_create_notification_channel(channel_name,10,10);
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
          canal = factory.get_notification_channel( channel_name );
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
          canal = factory.get_notification_channel( channel_name );
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
          canales = factory.channels();
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
          ncanales = factory.new_channels();
          canales = factory.channels();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        //for (int i = 0; i < ncanales.length; i++)
        //{
        //  utils.writeIORString(ncanales[i], null);
        //}
        for (int i = 0; i < ncanales.length; i++)
        {
          String creation_date = null;
          String creation_time = null;
          try
          {
            creation_date = 
             org.omg.DistributionChannelAdmin.DistributionChannelHelper.narrow(
              canales[i]).creation_date();
            creation_time = 
             org.omg.DistributionChannelAdmin.DistributionChannelHelper.narrow(
              canales[i]).creation_time();
            System.out.println( ncanales[i] + "\t\t" +
                                creation_date + "\t" + creation_time );
          }
          catch ( java.lang.Exception ex )
          {
            ex.printStackTrace();
            System.exit(1);
          }
        }
        break;

      case _OP_CREATES:
        try
        {
          if (num_channels == 0)
          {
            System.err.println("...creates <num_channels> <base_name>...");
            System.exit(1);
          }
          for (int j = 0; j < num_channels; j++)
          {
            String cn = channel_name+"_"+j;
            System.err.println(cn);
            canal = factory.new_create_notification_channel(cn,10,10);
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

    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
