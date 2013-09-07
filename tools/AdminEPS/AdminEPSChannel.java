
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.NotificationChannelAdmin.NotificationChannelFactory;
import org.omg.NotificationChannelAdmin.NotificationChannelFactoryHelper;
import org.omg.NotificationChannelAdmin.NotificationChannel;
import org.omg.NotificationChannelAdmin.NotificationChannelHelper;

import es.tid.TIDorbj.core.TIDORB;

public class AdminEPSChannel
{		
  private static final String NAME = "AdminEPSChannel";

  private static final int OP_START = 0;
  private static final int OP_STOP  = 1;
  private static final String PARAM_START = "start";
  private static final String PARAM_STOP  = "stop";
  private static final String URL_MODE = "url";
  private static final String IOR_MODE = "ior";
  private static final int _URL_MODE = 0;
  private static final int _IOR_MODE = 1;

  private static final String CHANNEL_NAME="EventProcessorServiceChannel@";

  //private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = NAME + 
                   " <operation> [<optional_parameters>] < admin_ior_filename";
  private static final String OR = "or";
  private static final String USAGE2 = NAME + 
             " <operation> -fi <admin_ior_filename> [<optional_parameters>]\n";
  private static final String USAGE3 = "\n<operation> = [ start | stop ]";
  private static final String USAGE4 = "<optional_parameters> = [-fo <channel_ref_filename>] [-m <output_mode>]";
  private static final String USAGE5 = "\n<output_mode> = [ ior | url ]";
  private static final String USAGE6 = "\ndefaults: url mode, channel_ref_filename stdout";

  private static void showHelp()
  {
    //System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
    System.out.println(OR);
    System.out.println(USAGE2);
    System.out.println(USAGE3);
    System.out.println(USAGE4);
    System.out.println(USAGE5);
    System.out.println(USAGE6);
  }

  private static String readStringIOR(String filename)
  {
    String ref = null;
    try
    {
      java.io.LineNumberReader r;
      if (filename == null)
      {
        r = new
           java.io.LineNumberReader (new java.io.InputStreamReader(System.in));
      }
      else
      {
        r = new java.io.LineNumberReader (new java.io.FileReader(filename));
      }
      ref = r.readLine();
      r.close();
    }
    catch (java.io.IOException e1)
    {
      if (filename == null)
      {
        System.err.println("Error reading IOR from Standard Input");
      }
      else
      {
        System.err.println("Error reading IOR from file " + filename);
      }
      System.exit(1);
    }
    return ref;
  }

  public static void exportReference( String reference, String filename )
  {
    if ( filename == null )
    {
      System.out.println(reference);
    }
    else
    {
      try
      {
        java.io.FileOutputStream file =
                                   new java.io.FileOutputStream(filename);
        java.io.PrintStream pfile = new java.io.PrintStream(file);
        pfile.println(reference);
      }
      catch ( java.io.IOException ex )
      {
        //System.err.println("Server: Export reference file error.");
        ex.printStackTrace();
        System.exit(-1);
      }
    }
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

/*
    org.omg.PortableServer.POA rootPOA = null;
    try
    {
      rootPOA = org.omg.PortableServer.POAHelper.narrow(
                                    orb.resolve_initial_references("RootPOA"));
    }
    catch ( org.omg.CORBA.ORBPackage.InvalidName ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }		
*/

    int operation = -1;
    int _ior_mode = _URL_MODE;
    String ior_admin_filename = null;
    String channel_ref_filename = null;
    String channel_number = "0";

    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      if ( args[i].compareTo("-fi") == 0 )
      {
        ior_admin_filename = new String(args[++i]);
      }
      if ( args[i].compareTo("-fo") == 0 )
      {
        channel_ref_filename = new String(args[++i]);
      }
      if ( args[i].compareTo("-m") == 0 )
      {
        String ior_mode = new String(args[++i]);

        if (ior_mode.compareTo(IOR_MODE) == 0)
        {
          _ior_mode = _IOR_MODE;
        }
        else if (ior_mode.compareTo(URL_MODE) == 0)
        {
          _ior_mode = _URL_MODE;
        }
        else
        {
          showHelp();
          System.exit(1);
        }
      }
      else if ( args[i].compareTo("-n") == 0 )
      {
        channel_number = new String(args[++i]);
      }
      else if ( args[i].compareTo(PARAM_START) == 0 )
      {
        operation = OP_START;
      }
      else if ( args[i].compareTo(PARAM_STOP) == 0 )
      {
        operation = OP_STOP;
      }
      else if ( ( args[i].compareTo("-help") == 0 ) ||
                ( args[i].compareTo("-h") == 0 ) ||
                ( args[i].compareTo("/h") == 0 ) ||
                ( args[i].compareTo("-?") == 0 ) ||
                ( args[i].compareTo("/?") == 0 ) )
      {
        showHelp();
        System.exit(1);
      }
    }

    if (operation == -1)
    {
      showHelp();
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    NotificationChannelFactory factory = null;
    try
    {
      object_ref = orb.string_to_object(readStringIOR(ior_admin_filename) );
      NotificationAdmin admin = NotificationAdminHelper.narrow(object_ref);
      object_ref = admin.channel_factory();
      factory = NotificationChannelFactoryHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    }

    String reference = null;
    String name = CHANNEL_NAME+channel_number;
    org.omg.NotificationChannelAdmin.NotificationChannel canal = null;
    switch (operation)
    {
      case OP_START:
        try
        {
          object_ref = factory.get_notification_channel( name );
          System.out.println("Warning: Channel already created!");
          canal = NotificationChannelHelper.narrow(object_ref);
        }
        catch (org.omg.NotificationChannelAdmin.ChannelNotFound ex)
        {
          try
          {
            object_ref = 
                       factory.new_create_notification_channel( name, 10, 10 );
            System.out.println("New channel created!");
            canal = NotificationChannelHelper.narrow(object_ref);
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
        if (_ior_mode == _IOR_MODE)
        {
          reference = orb.object_to_string(canal);
          if (channel_ref_filename == null)
          System.out.println("Channel IOR reference:");
        }
        else
        {
          reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(canal);
          if (channel_ref_filename == null)
          System.out.println("Channel URL reference:");
        }
        exportReference(reference, channel_ref_filename);
        System.out.println("");
        break;

      case OP_STOP:
        try
        {
          object_ref = factory.get_notification_channel( name );
          System.out.println("Located channel!");
          canal = NotificationChannelHelper.narrow(object_ref);
          if (_ior_mode == _IOR_MODE)
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
        break;
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
