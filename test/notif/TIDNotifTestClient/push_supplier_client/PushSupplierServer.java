package es.tid.corba.TIDNotifTestClient.push_supplier_client;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import java.util.Properties;
import org.omg.CosEventChannelAdmin.ProxyPushConsumer;
import org.omg.CosEventChannelAdmin.ProxyPushConsumerHelper;

public class PushSupplierServer extends ServiceClient
{
  private static final String NAME = "PushSupplierServer";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + 
                                    "-e envent_file < proxy_push_consumer_IOR";

  private static org.omg.CORBA.ORB orb;
  private static org.omg.CosEventChannelAdmin.ProxyPushConsumer 
                                                           proxy_push_consumer;

  private static int num_events = 0;
  // 0 = string
  // 1 = int
  // 2 = struct
  // 3 = any
  private static int event_type = 0;

  private static void showHelp()
  {
    System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
  }

  protected static int readEventFile(String filename)
  {
    if (filename == null)
    {
      return -1;
    }

    try
    {
      String line;
      java.io.LineNumberReader r = 
               new java.io.LineNumberReader (new java.io.FileReader(filename));

      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      num_events = Integer.parseInt(line);

      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      event_type = Integer.parseInt(line);
      r.close();
      return 0;
    }
    catch (java.io.IOException e1)
    {
      System.err.println("Error reading IOR from file " + filename);
      num_events = 0;
      event_type = 0;
    }
    return -1;
  }

  public static void main(String args[])
  {
    String iorFilename = null;
    String eventFilename = null;

    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      if ( args[i].compareTo("-f") == 0 )
      {
        eventFilename = new String(args[++i]);
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

    // Leemos la referencia al proxy_push_consumer de la entrada estandar
    String iorString = readStringIOR(null);
    if (iorString == null) System.exit(0);

    // Leemos de fichero el numero de eventos y el tipo de eventos
    if (readEventFile(eventFilename) < 0)
    {
      showHelp();
      System.exit(1);
    }

    Properties props = new Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
	
    System.getProperties().put(
                  "org.omg.CORBA.ORBSingletonClass","es.tid.TIDorbj.core.SingletonORB");
		
    props.put("es.tid.TIDorbj.max_blocked_time","25000");

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
	
    org.omg.CORBA.Object ref = orb.string_to_object(iorString);
    
    ProxyPushConsumer proxy_push_consumer = ProxyPushConsumerHelper.narrow(ref);

    myPushSupplier supplier = null; 
    try 
    {
      int rate = 0; // todos los eventos seguidos, sin pausa.
      String id = "[c:0] ";

      supplier = 
       new myPushSupplier(id, orb, proxy_push_consumer, 
                                          num_events, event_type, rate, false);

      proxy_push_consumer.connect_push_supplier(supplier._this(orb));
      Thread t = new Thread(supplier);
      t.start();
      orb.run();
    }
//    catch (CannotProceed cp)
//    {
//      System.err.println("shutdown_service error: " + cp.why);
//    }
    catch (Exception e) 
    {
      e.printStackTrace();	
    }
    if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
