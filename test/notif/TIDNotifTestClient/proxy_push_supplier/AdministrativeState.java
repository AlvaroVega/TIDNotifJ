package es.tid.corba.TIDNotifTestClient.proxy_push_supplier;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import java.util.Properties;
import org.omg.NotificationChannelAdmin.ProxyPushSupplier;
import org.omg.NotificationChannelAdmin.ProxyPushSupplierHelper;

import es.tid.TIDorbj.core.TIDORB;

public class AdministrativeState extends ServiceClient
{
  private static final String NAME = "AdministrativeState";

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
    int new_state = -1;
    String iorFilename = null;

    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      if ( args[i].compareTo("-f") == 0 )
      {
        iorFilename = new String(args[++i]);
      }
      else if ( ( args[i].compareTo("locked") == 0 ) ||
                ( args[i].compareTo("LOCKED") == 0 ) )
      {
        new_state = 0;
      }
      else if ( ( args[i].compareTo("unlocked") == 0 ) ||
                ( args[i].compareTo("UNLOCKED") == 0 ) )
      {
        new_state = 1;
      }
      else if ( ( args[i].compareTo("shutting_down") == 0 ) ||
                ( args[i].compareTo("SHUTTING_DOWN") == 0 ) )
      {
        new_state = 2;
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
      org.omg.ServiceManagement.AdministrativeState state = null;
      if (new_state == -1)
      {
        state = push_supplier.administrative_state();
        if (state.value() == 0)
          System.out.println("LOCKED");
        else if (state.value() == 1)
          System.out.println("UNLOCKED");
        else if (state.value() == 2)
          System.out.println("SHUTTING DOWN");
        else
          System.out.println("UNKNOWN");
      }
      else
      {
        if (new_state == 0)
          state = org.omg.ServiceManagement.AdministrativeState.locked;
        else if (new_state == 1)
          state = org.omg.ServiceManagement.AdministrativeState.unlocked;
        else if (new_state == 2)
          state = org.omg.ServiceManagement.AdministrativeState.shutting_down;
        push_supplier.administrative_state(state);
      }
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
