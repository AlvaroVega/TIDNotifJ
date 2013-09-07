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

public class Agent
{		
  private static final String NAME = "Agent";

  private static final String CHANNEL_FACTORY = "ChannelFactory";

  private static final String OP_RESOLVE  = "resolve";
  private static final String OP_SHUTDOWN = "shutdown";
  private static final String OP_LIST     = "list";
  //private static final String OP_NLIST    = "nlist";
  private static final String OP_START    = "start";
  private static final String OP_STOP     = "stop";

  private static final int _OP_UNKNOWN  = -1;
  private static final int _OP_RESOLVE  =  0;
  private static final int _OP_SHUTDOWN =  2;
  private static final int _OP_LIST     =  3;
  //private static final int _OP_NLIST    =  4;
  private static final int _OP_START    =  4;
  private static final int _OP_STOP     =  5;

  private static final String MODE_IOR = "ior";
  private static final String MODE_URL = "url";

  private static final int _MODE_IOR = 0;
  private static final int _MODE_URL = 1;

  private static final String CHANNEL_NAME="EventProcessorChannel";

  private static es.tid.corba.TIDNotif.tools.Util utils = 
                                        new es.tid.corba.TIDNotif.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println("Agent resolve ChannelFactory [-m (ior | url)] < agent.ior");
    System.err.println("Agent shutdown < agent.ior");
    System.err.println("Agent list [-m (ior | url)] < agent.ior");
/*
    System.err.println("Agent nlist < agent.ior");
*/
    System.err.println("");
    System.err.println(
            "Parametros conservados por compatibilidad con version anterior:");
    System.err.println("");
    System.err.println("Agent start -fi agent.ior -fo canal.url [-m (ior | url)]");
    System.err.println("Agent stop -fi agent.ior");
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
    String name = null;
    int ref_mode = _MODE_IOR;
    int default_mode = -1;
    String ior_filename = null;
    String ref_filename = null;

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
          default_mode = _MODE_IOR;
        }
        else if (_ref_mode.compareTo(MODE_URL) == 0)
        {
          ref_mode = _MODE_URL;
          default_mode = _MODE_URL;
        }
        else
        {
          System.err.println("Modo invalido [-m (ior | url)]");
          showHelp();
          System.exit(1);
        }
      }
      else if ( args[i].compareTo("-fi") == 0 )
      {
        ior_filename = new String(args[++i]);
      }
      else if ( args[i].compareTo("-fo") == 0 )
      {
        ref_filename = new String(args[++i]);
      }
      else if ( args[i].compareTo(OP_RESOLVE) == 0 )
      {
        operation = _OP_RESOLVE;
      }
      else if ( args[i].compareTo(OP_SHUTDOWN) == 0 )
      {
        operation = _OP_SHUTDOWN;
      }
      else if ( args[i].compareTo(OP_LIST) == 0 )
      {
        operation = _OP_LIST;
      }
/*
      else if ( args[i].compareTo(OP_NLIST) == 0 )
      {
        operation = _OP_NLIST;
      }
*/
      else if ( args[i].compareTo(OP_START) == 0 )
      {
        operation = _OP_START;
      }
      else if ( args[i].compareTo(OP_STOP) == 0 )
      {
        operation = _OP_STOP;
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
        name = new String(args[i]);
      }
    }

// Debug 
/*
    System.err.println("Operation: " + operation);
    System.err.println("Name : " + name);
    System.err.println("Ref mode: " + ref_mode);
    System.err.println("IOR filemame: " + ior_filename);
    System.err.println("REF filename: " + ref_filename);
*/

    if (operation == _OP_UNKNOWN)
    {
      System.err.println(
                "Operacion invalida (resolve|list|nlist|shutdown|start|stop");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    NotificationAdmin agent = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(ior_filename));
      agent = NotificationAdminHelper.narrow(object_ref);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    String reference = null;
    NotificationChannelFactory factory = null;
    NotificationChannel canal = null;
    NotificationChannel[] canales = null;
    String[] ncanales = null;

    switch (operation)
    {
      // Agent resolve "ChannelFactory" [-m (ior | url)] < factory.ior
      case _OP_RESOLVE:
        if (name == null)
        {
          System.err.println("Resolve what?");
          showHelp();
          System.exit(1);
        }
        else if (name.compareTo(CHANNEL_FACTORY) != 0)
        {
          System.err.println("Resolve what?");
          showHelp();
          System.exit(1);
        }
        try
        {
          factory = agent.channel_factory();
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(factory);
        }
        else
        {
          reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(factory);
        }
        utils.writeIORString(reference, null);
        break;

      // Agent shutdown < factory.ior
      case _OP_SHUTDOWN:
        try
        {
          agent.shutdown();
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        break;

      // Factory -list < factory.ior
      case _OP_LIST:
        try
        {
          canales = agent.list_channels();
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

/*
      // Factory -nlist < factory.ior
      case _OP_NLIST:
        try
        {
          ncanales = agent.list_channels_id();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        for (int i = 0; i < ncanales.length; i++)
        {
          utils.writeIORString(ncanales[i], null);
        }
        break;
*/
      case _OP_START:
        name = CHANNEL_NAME;
        try
        {
          factory = agent.channel_factory();
          canal = factory.get_notification_channel( name );
          System.out.println("Warning: Channel already created!");
        }
        catch (org.omg.NotificationChannelAdmin.ChannelNotFound ex)
        {
          try
          {
            canal = factory.new_create_notification_channel(name, 10, 10);
            System.out.println("New channel created!");
          }
          catch ( java.lang.Exception exx )
          {
            exx.printStackTrace();
            System.exit(1);
          }
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }

        if (default_mode == -1) ref_mode = _MODE_URL;

        if (ref_mode == _MODE_IOR)
        {
          reference = orb.object_to_string(canal);
          if (ref_filename == null)
            System.out.println("Channel IOR reference:");
        }
        else
        {
          reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(canal);
          if (ref_filename == null)
            System.out.println("Channel URL reference:");
        }
        utils.writeIORString(reference, ref_filename);
        break;

      // Factory -stop < factory.ior
      case _OP_STOP:
        name = CHANNEL_NAME;
        try
        {
          factory = agent.channel_factory();
          canal = factory.get_notification_channel( name );
          System.out.println("Located channel!");
          if (ref_mode == _MODE_IOR)
          {
            reference = orb.object_to_string(canal);
            System.out.println("Channel IOR reference:");
          }
          else
          {
            reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(canal);
            System.out.println("Channel URL reference:");
          }
          System.out.println(reference);
          canal.destroy();
          System.out.println("Channel destroyed!");
        }
        catch (org.omg.NotificationChannelAdmin.ChannelNotFound ex)
        {
          System.out.println("Channel not found: already destroyed!");
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
