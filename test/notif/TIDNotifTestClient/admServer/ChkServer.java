package es.tid.corba.TIDNotifTestClient.admServer;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import java.util.Properties;

import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.NotificationChannelAdmin.NotificationChannelFactory;
import org.omg.NotificationChannelAdmin.NotificationChannel;
import org.omg.NotificationChannelAdmin.NotificationChannelHelper;
import org.omg.NotificationChannelAdmin.SupplierAdmin;
import org.omg.NotificationChannelAdmin.SupplierAdminHelper;
import org.omg.NotificationChannelAdmin.ConsumerAdmin;
import org.omg.NotificationChannelAdmin.ConsumerAdminHelper;
import org.omg.CosEventChannelAdmin.ProxyPushSupplier;
import org.omg.CosEventChannelAdmin.ProxyPushConsumer;

import es.tid.TIDorbj.core.TIDORB;

public class ChkServer extends ServiceClient
{
  private static final String NAME = "ChkServer";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + 
                                    "-e envent_file < proxy_push_supplier_IOR";

  private static org.omg.CORBA.ORB orb;
  private static org.omg.CosEventChannelAdmin.ProxyPushSupplier 
                                                           proxy_push_supplier;

  private static int num_channels = 0;
  private static int consumer_admin = 0;
  private static int num_consumer_admins = 0;
  private static int num_clients = 0;
  private static int num_events = 0;

  // 0 = string / 1 = int / 2 = struct / 3 = any
  private static int event_type = 0;

  private static void showHelp()
  {
    System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
  }

  protected static NotificationChannelFactory channel_factory(
                                                      NotificationAdmin admin )
  {
    System.out.println("NotificationAdmin admin.channel_factory();");

    NotificationChannelFactory factory = null;
    try
    {
      factory = admin.channel_factory();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return factory;
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

    // Leemos la referencia al proxy_push_supplier de la entrada estandar
    String iorString = readStringIOR(null);
    if (iorString == null) System.exit(0);

    Properties props = new Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
	
    System.getProperties().put(
                  "org.omg.CORBA.ORBSingletonClass","es.tid.TIDorbj.core.SingletonORB");

    props.put("es.tid.TIDorbj.max_blocked_time","25000"); // 5000 default
		
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
      NotificationAdmin admin = NotificationAdminHelper.narrow(ref);

      NotificationChannelFactory factory = channel_factory(admin);

      NotificationChannel[] channels = null;
      channels = factory.channels();

      System.out.println("");
      System.out.print("NUM. CHANNELS: ");
      System.out.println(channels.length);
      System.out.println("=================");

      for (int i = 0; i < channels.length; i++)
      {
        String channelIOR = orb.object_to_string(channels[i]);
        System.out.println("");
        System.out.println("Channel [" + i + "]");
        System.out.println(channelIOR);

        SupplierAdmin[] suppliersAdmin = null;
        ConsumerAdmin[] consumersAdmin = null;

        try
        {
          suppliersAdmin = channels[i].supplier_admins();

          if (suppliersAdmin.length == 0)
          {
            System.out.println("");
            System.out.println("[ch:" +i+ ", adm: NO SUPPLIERS ADMIN ]");
          }
          else
          {
            for (int j = 0; j < suppliersAdmin.length; j++)
            {
              String supplierAdminIOR = 
                                    orb.object_to_string(suppliersAdmin[j]);
              System.out.println("");
              System.out.println("Supplier Admin [ch:" +i+ ", adm:" +j+ "]");
              System.out.println(supplierAdminIOR);

              try
              {
                ProxyPushConsumer[] proxyPushConsumers = 
                                            suppliersAdmin[j].obtain_push_consumers();

                if (proxyPushConsumers.length == 0)
                {
                  System.out.println("");
                  System.out.println(
                   "[ch:" +i+ ", adm:" +j+ ", con: NO PROXY PUSH CONSUMERS ]");
                }
                else
                {
                  for (int k = 0; k < proxyPushConsumers.length; k++)
                  {
                    String proxyPushConsumerIOR = 
                                   orb.object_to_string(proxyPushConsumers[k]);
                    System.out.println("");
                    System.out.println(
                    "ProxyPushConsumer [ch:" +i+ ", adm:" +j+ ", con:" +k+ "]");
                    System.out.println(proxyPushConsumerIOR);
                  }
                }
              }
              catch (Exception e)
              {
                System.out.println("");
                System.out.println(" * suppliersAdmin[j].push_consumers();");
                e.printStackTrace();
              }
            }
          }
        }
        catch (Exception e)
        {
          System.out.println("");
          System.out.println(
                         " * suppliersAdmin = channels[i].supplier_admins();");
          e.printStackTrace();
        }

        try
        {
          consumersAdmin = channels[i].consumer_admins();

          if (consumersAdmin.length == 0)
          {
            System.out.println("");
            System.out.println("[ch:" +i+ ", adm: NO CONSUMERS ADMIN ]");
          }
          else
          {
            for (int j = 0; j < consumersAdmin.length; j++)
            {
              String consumerAdminIOR = 
                                    orb.object_to_string(consumersAdmin[j]);
              System.out.println("");
              System.out.println("Consumer Admin [ch:" +i+ ", adm:" +j+ "]"); 
              System.out.println(consumerAdminIOR);

              try
              {
                ProxyPushSupplier[] proxyPushSuppliers = 
                                            consumersAdmin[j].obtain_push_suppliers();

                if (proxyPushSuppliers.length == 0)
                {
                  System.out.println("");
                  System.out.println(
                   "[ch:" +i+ ", adm:" +j+ ", con: NO PROXY PUSH SUPPLIERS ]");
                }
                else
                {
                  for (int k = 0; k < proxyPushSuppliers.length; k++)
                  {
                    String proxyPushSupplierIOR = 
                                   orb.object_to_string(proxyPushSuppliers[k]);
                    System.out.println("");
                    System.out.println(
                    "ProxyPushSupplier [ch:" +i+ ", adm:" +j+ ", sup:" +k+ "]");
                    System.out.println(proxyPushSupplierIOR);
                  }
                }
              }
              catch (Exception e)
              {
                System.out.println("");
                System.out.println(" * consumersAdmin[j].push_suppliers();");
                e.printStackTrace();
              }
            }
          }
        }
        catch (Exception e)
        {
          System.out.println("");
          System.out.println(
                         " * consumersAdmin = channels[i].consumer_admins();");
          e.printStackTrace();
        }
      }
    }
    catch ( java.lang.Exception ex )
    {
      System.out.println("");
      System.out.println("INICIO");
      ex.printStackTrace();
    }
    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
