package es.tid.corba.TIDNotifTestClient.operator_client;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import java.util.Properties;

import es.tid.TIDorbj.core.TIDORB;

public class OperatorServer extends ServiceClient
{
  private static final String NAME = "OperatorServer";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + "-t event_type" +
                                       "-f ior_filename";

  private static org.omg.CORBA.ORB orb;

  // 0 = int
  // 1 = string
  // 2 = medium event
  // 3 = complex event
  private static int event_type = 0;
  private static String filename = null;

  private static void showHelp()
  {
    System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
  }

  public static void exportReference( String filename, String reference )
  {
    if ( filename == null)
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
        ex.printStackTrace();
        System.exit(-1);
      }
    }
  }

  public static void main(String args[])
  {
    String iorFilename = null;
    String eventFilename = null;

    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      if ( ( args[i].compareTo("-help") == 0 ) ||
           ( args[i].compareTo("-h") == 0 ) ||
           ( args[i].compareTo("/h") == 0 ) ||
           ( args[i].compareTo("-?") == 0 ) ||
           ( args[i].compareTo("/?") == 0 ) )
      {
        showHelp();
        System.exit(1);
      }
      else if ( (args[i].compareTo("-t") == 0) ||
                (args[i].compareTo("-T") == 0) )
      {
        i++;
        try
        {
          event_type = Integer.parseInt(args[i]);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
            ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
          System.exit(1);
        }
        if ( (event_type < 0) || (event_type > 3) )
        {
          System.out.println("ERROR: Invalid Event Type");
          if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
            ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
          System.exit(1);
        }
      }
      else if ( (args[i].compareTo("-f") == 0) ||
                (args[i].compareTo("-F") == 0) )
      {
        i++;
        filename = new String(args[i]);
      }
    }

    Properties props = new Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
	
    System.getProperties().put(
         "org.omg.CORBA.ORBSingletonClass","es.tid.TIDorbj.core.SingletonORB");
		
    orb = org.omg.CORBA.ORB.init( args, props );

    org.omg.PortableServer.POA rootPOA = null;
    try
    {
      rootPOA = org.omg.PortableServer.POAHelper.narrow(
                                   orb.resolve_initial_references("RootPOA") );
      rootPOA.the_POAManager().activate();
    }
    catch ( java.lang.Exception ex )
    {
      ex.printStackTrace();
      if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
        ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
      System.exit(1);
    }
	
    myOperator my_operator = null;

    String id = "[Basic Operator] ";

    try 
    {
      my_operator = new myOperator( id, orb, event_type );
      exportReference( filename, 
                       orb.object_to_string(my_operator._this(orb)) );

      System.out.println("OPERATOR Runnig...");
      orb.run();
    }
    catch (Exception e) 
    {
      e.printStackTrace();	
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
