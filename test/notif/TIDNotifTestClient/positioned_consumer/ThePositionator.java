package es.tid.corba.TIDNotifTestClient.transformator;

import java.util.Properties;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import es.tid.corba.TIDDistribAdmin.Agent;
import es.tid.corba.TIDDistribAdmin.AgentHelper;

import org.omg.ServiceManagement.OperationMode;

import org.omg.CosEventChannelAdmin.ConsumerAdmin;
import org.omg.DistributionChannelAdmin.ProxyPushSupplier;

import org.omg.DistributionChannelAdmin.DistributionChannelFactory;
import org.omg.DistributionChannelAdmin.DistributionChannel;

import org.omg.TransformationAdmin.TransformingOperator;
import org.omg.TransformationAdmin.AssignedTransformingRule;
import org.omg.TransformationAdmin.AssignedRuleOrder;

import es.tid.TIDorbj.core.TIDORB;

public class ThePositionator extends ServiceClient
{
  private static final String NAME = "ThePositionator";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + "-f ior_filename";

  private static org.omg.CORBA.ORB orb;

  private static String iorFilename = null;

  private static void showHelp()
  {
    System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
  }

  static void add_proxypushsupplier(int position,
                             org.omg.DistributionChannelAdmin.ConsumerAdmin ca)
  {
    System.out.println("ADD " );
    System.out.println("  Creating ProxyPushConsumer: " + position);
    System.out.println("");

    ProxyPushSupplier proxyPushSupplier = null;

    try
    {
      if (position == 0)
      {
        org.omg.CosEventChannelAdmin.ProxyPushSupplier
                                 pPSupplier = ca.obtain_push_supplier();
      }
      else
        proxyPushSupplier = ca.new_obtain_push_supplier(position);
    }
    catch (org.omg.DistributionChannelAdmin.AlreadyDefinedOrder e1)
    {
      System.out.println("  ERROR: AlreadyDefinedOrder.");
    }
    catch (org.omg.CORBA.BAD_OPERATION e1)
    {
      System.out.println("  ERROR: BAD OPERATION.");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      System.exit(1);
    }
  }

  static void list_proxypushsuppliers(org.omg.DistributionChannelAdmin.ConsumerAdmin ca)
  {
    System.out.println( "LIST " );

    try
    {
      org.omg.NotificationChannelAdmin.ProxyPushSupplier[] lista =
                                                           ca.obtain_push_suppliers();

      System.out.println( "  Number of ProxyPushConsumers: " + lista.length );

      for ( int i = 0; i < lista.length; i++)
      {
        try
        {
          
          org.omg.DistributionChannelAdmin.ProxyPushSupplier ppc = 
            org.omg.DistributionChannelAdmin.ProxyPushSupplierHelper.narrow(
                                                                     lista[i]);
          System.out.println( "  Object: " + ppc.toString() );
          System.out.println( "  Order: " + ppc.order() );
        }
        catch (java.lang.Exception ex)
        {
          ex.printStackTrace();
          System.exit(1);
        }
      }
    }
    catch (java.lang.Exception ex)
    {
      ex.printStackTrace();
    }
  }

  static void gap(int new_gap,org.omg.DistributionChannelAdmin.ConsumerAdmin ca)
  {
    System.out.println( "GAP " + new_gap );

    try
    {
      ca.order_gap(new_gap);
    }
    catch (java.lang.Exception ex)
    {
      System.err.println(" *** ERROR UNKNOWN");
    }
  }
  public static void main(String args[])
  {
    String iorFilename = "agent.ior";
    String dataFilename = "operations.dat";

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
      else if ( (args[i].compareTo("-f") == 0) ||
                (args[i].compareTo("-F") == 0) )
      {
        iorFilename = new String(args[++i]);
      }
      else if ( (args[i].compareTo("-o") == 0) ||
                (args[i].compareTo("-O") == 0) )
      {
        dataFilename = new String(args[++i]);
      }
    }

    String iorString = readStringIOR(iorFilename);

    if (iorString == null) System.exit(0);

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
	
    try
    {
      // Obtenemos la referencia al Factory
      org.omg.CORBA.Object ref = orb.string_to_object(iorString);
      Agent admin = AgentHelper.narrow(ref);

      DistributionChannelFactory factory = admin.channel_factory();

      System.out.println("CREATING CHANNEL");
      System.out.println("================");
      System.out.println("");

      DistributionChannel channel = null;
      try
      {
        channel = 
         factory.create_distribution_channel(10,10,OperationMode.distribution);
        System.out.println("  OK!");
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.exit(1);
      }

      System.out.println("CREATING CONSUMERADMIN");
      System.out.println("======================");
      System.out.println("");

      ConsumerAdmin consumer_admin = null;
      try
      {
        consumer_admin = channel.for_consumers();
        System.out.println("  OK!");
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.exit(1);
      }

      org.omg.DistributionChannelAdmin.ConsumerAdmin ca = null;
      try
      {
        ca = org.omg.DistributionChannelAdmin.ConsumerAdminHelper.narrow(
                                                              consumer_admin );
      }
      catch (java.lang.Exception ex)
      {
        ex.printStackTrace();
        System.exit(1);
      }

      try
      {
        java.io.LineNumberReader r =
           new java.io.LineNumberReader (new java.io.FileReader(dataFilename));

        do
        {
          String op_name = r.readLine();

          if (op_name == null) 
          {
            break;
          }

          op_name = op_name.trim();

          if (op_name.compareTo("add") == 0)
          {
            String op_position = r.readLine();
            if (op_name == null) 
            {
              break;
            }
            op_position = op_position.trim();
            add_proxypushsupplier(Integer.parseInt(op_position), ca);
          }
          else if (op_name.compareTo("list") == 0)
          {
            list_proxypushsuppliers(ca);
          }
          else if (op_name.compareTo("gap") == 0)
          {
            String op_position = r.readLine();
            if (op_name == null) 
            {
              break;
            }
            op_position = op_position.trim();
            gap(Integer.parseInt(op_position), ca);
          }
          else
          {
            if (op_name.startsWith("#") || (op_name.compareTo("") == 0))
            {
            }
            else
              System.out.println("UNKNOWN TASK => " + op_name);
          }
        } while (true);

        r.close();
      }
      catch (java.io.IOException e1)
      {
        System.err.println("Error reading IOR from file " + dataFilename);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
