package es.tid.corba.TIDNotifTestClient.proxy_push_supplier;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import java.util.Properties;
import org.omg.NotificationChannelAdmin.ProxyPushSupplier;
import org.omg.NotificationChannelAdmin.ProxyPushSupplierHelper;

import es.tid.TIDorbj.core.TIDORB;

public class ForwardingDiscriminator extends ServiceClient
{
  private static final String NAME = "ForwardingDiscriminator";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + " < IOR_String";
  private static final String OR = "or";
  private static final String USAGE2 = "\t" + NAME + " -f IOR_Filename\n";

  private static void showHelp()
  {
    System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
    System.out.println(OR);
    System.out.println(USAGE2);
  }

  public static void main(String args[])
  {
    String iorFilename = null;

    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      if ( args[i].compareTo("-f") == 0 )
      {
        iorFilename = new String(args[++i]);
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

    String iorString = readStringIOR(iorFilename);

    if (iorString == null) System.exit(0);

    Properties props = new Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
	
    System.getProperties().put(
                  "org.omg.CORBA.ORBSingletonClass","es.tid.TIDorbj.core.SingletonORB");
		
    props.put("es.tid.TIDorbj.max_blocked_time","25000");

    org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init( args, props );
	
    org.omg.CORBA.Object ref = orb.string_to_object(iorString);
    
    ProxyPushSupplier push_supplier = ProxyPushSupplierHelper.narrow(ref);

    try 
    {
      String forwardingDiscriminatorIOR =
               orb.object_to_string(push_supplier.forwarding_discriminator());
      System.out.println(forwardingDiscriminatorIOR);

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
