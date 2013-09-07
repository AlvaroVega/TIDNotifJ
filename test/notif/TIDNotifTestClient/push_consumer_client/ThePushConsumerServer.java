package es.tid.corba.TIDNotifTestClient.push_consumer_client;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import java.util.Properties;

import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.NotificationChannelAdmin.NotificationChannelFactory;
import org.omg.NotificationChannelAdmin.NotificationChannel;

import org.omg.CosEventChannelAdmin.ConsumerAdmin;
import org.omg.CosEventChannelAdmin.ConsumerAdminHelper;

import org.omg.CosEventChannelAdmin.ProxyPushSupplier;

//import org.omg.ConstraintAdmin.Discriminator;

import es.tid.corba.TIDDistribAdmin.Agent;
import es.tid.corba.TIDDistribAdmin.AgentHelper;

import org.omg.ServiceManagement.OperationMode;

import org.omg.DistributionChannelAdmin.DistributionChannelFactory;
import org.omg.DistributionChannelAdmin.DistributionChannel;

public class ThePushConsumerServer extends ServiceClient
{
  private static final String NAME = "ThePushConsumerServer";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + 
                                    "-e envent_file < proxy_push_supplier_IOR";

  private static org.omg.CORBA.ORB orb;
  private static org.omg.CosEventChannelAdmin.ProxyPushSupplier 
                                                           proxy_push_supplier;

  private static OperationMode operation_mode = OperationMode.notification;

  private static int num_channels = 0;
  private static int consumer_admin = 0;
  private static int chain_consumer_admin = 0;
  private static int num_consumer_admins = 0;
  private static int chain_num_consumer_admins = 0;
  private static String consumer_admins_filters_filename = null;
  private static int num_clients = 0;
  private static String pushconsumer_filters_filename = null;
  private static int num_events = 0;

  // 0 = int / 1 = String / 2 = any / 3 = struct
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
      int saltar = 0;

      java.io.LineNumberReader r = 
               new java.io.LineNumberReader (new java.io.FileReader(filename));

      // Num. Channels
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      num_channels = Integer.parseInt(line);

      // SupplierAdmin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      int pos;
      int supplier_admin;
      if ((pos = line.indexOf("+")) < 0)
      {
        supplier_admin = Integer.parseInt(line);
      }
      else
      {
        supplier_admin = Integer.parseInt(line.substring(0, pos).trim());
      }

      // Num. New SupplierAdmin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      int new_supplier_admins;
      if ((pos = line.indexOf("+")) < 0)
      {
        new_supplier_admins = Integer.parseInt(line);
      }
      else
      {
        new_supplier_admins = Integer.parseInt(line.substring(0, pos).trim());
      }

      // Nombre del fichero con los filtros a los Suppliers Admin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Nombre del fichero con los Operadores de los Suppliers Admin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // ConsumerAdmin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      if ((pos = line.indexOf("+")) < 0)
      {
        consumer_admin = Integer.parseInt(line);
      }
      else
      {
        consumer_admin = Integer.parseInt(line.substring(0, pos).trim());
        chain_consumer_admin =
                 Integer.parseInt(line.substring(pos+1, line.length()).trim());
      }

      // Num. New ConsumerAdmin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      if ((pos = line.indexOf("+")) < 0)
      {
        num_consumer_admins = Integer.parseInt(line);
      }
      else
      {
        num_consumer_admins = Integer.parseInt(line.substring(0, pos).trim());
        chain_num_consumer_admins =
                 Integer.parseInt(line.substring(pos+1, line.length()).trim());
      }

      // Nombre del fichero con los filtros a los Consumers Admin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      consumer_admins_filters_filename = new String(line);

      // Num. PushSupplier Clients
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      int num_supplier_clients = Integer.parseInt(line);

      // Nombre del fichero con los filtros a los PushSuppliers
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Nombre del fichero con los Operadores de los PushSuppliers
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Num. PushConsumer Clients
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      num_clients = Integer.parseInt(line);

      // Nombre del fichero con los filtros a los PushConsumers
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      pushconsumer_filters_filename = new String(line);

      // Num. Events
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      num_events = Integer.parseInt(line);
      num_events = 
       num_events * num_supplier_clients * (supplier_admin+new_supplier_admins);

      // Event Type
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      event_type = Integer.parseInt(line);

      r.close();

      System.out.println("Num. Channels = " +  num_channels);
      System.out.println("ConsumerAdmin = " +  consumer_admin);
      System.out.println("Chain ConsumerAdmin = " +  chain_consumer_admin);
      System.out.println("Num. New ConsumerAdmins = " +  num_consumer_admins);
      System.out.println("Chain Num. New ConsumerAdmins = " +  chain_num_consumer_admins);
      System.out.println("Num. Consumer Clients = " +  num_clients);
      System.out.println("Num. Events = " +  num_events);
      System.out.println("Event type = " + event_type);
      System.out.println("");

      return 0;
    }
    catch (java.io.IOException e1)
    {
      System.err.println("Error reading DATA from file " + filename);
      num_channels = 0;
      consumer_admin = 0;
      chain_consumer_admin = 0;
      num_consumer_admins = 0;
      chain_num_consumer_admins = 0;
      num_clients = 0;
      num_events = 0;
      event_type = 0;
    }

    System.out.println("Num. Channels = " +  num_channels);
    System.out.println("ConsumerAdmin = " +  consumer_admin);
    System.out.println("Chain ConsumerAdmin = " +  chain_consumer_admin);
    System.out.println("Num. New ConsumerAdmins = " +  num_consumer_admins);
    System.out.println("Chain Num. New ConsumerAdmins = " + chain_num_consumer_admins);
    System.out.println("Num. Clients = " +  num_clients);
    System.out.println("Num. Events = " +  num_events);
    System.out.println("Event type = " + event_type);

    return -1;
  }

  protected static void setFilters(String filename, ConsumerAdmin admin)
  {
    if (filename == null)
    {
      System.out.println("*** NO FILTERS ***");
      return;
    }

    if (filename.compareTo("none") == 0)
    {
      System.out.println("*** NO FILTERS ***");
      return;
    }

    String fname = filename + ".FIL";

    try
    {
      org.omg.NotificationChannelAdmin.ConsumerAdmin cAdmin =
            org.omg.NotificationChannelAdmin.ConsumerAdminHelper.narrow(admin);

      Discriminator ca_discriminator = cAdmin.forwarding_discriminator();
      String line;

      java.io.LineNumberReader r =
               new java.io.LineNumberReader (new java.io.FileReader(fname));

      do
      {
        line = r.readLine();
        if (line != null)
        {
          int constraintId;
          line = line.trim();
          try
          {
            constraintId = ca_discriminator.add_constraint(line);
            System.out.println("Add Constraint " + constraintId + ": " + line);
          }
          catch (java.lang.Exception e)
          {
            System.err.println("Error in Constraint: " + line);
          }
        }
        else
        {
          break;
        }
      } while (true);

      r.close();
    }
    catch (java.io.IOException e1)
    {
      System.err.println("Error reading IOR from file " + fname);
    }
    return;
  }

  protected static void setFilters( String filename,
                                    int adm,
                                    int prxy,
                                    ProxyPushSupplier proxy)
  {
    if (filename == null)
    {
      System.out.println("*** NO FILTERS ***");
      return;
    }

    if (filename.compareTo("none") == 0)
    {
      System.out.println("*** NO FILTERS ***");
      return;
    }

    String fname = filename + "." + adm + "." + prxy + ".FIL";

    try
    {
      org.omg.NotificationChannelAdmin.ProxyPushSupplier pps =
        org.omg.NotificationChannelAdmin.ProxyPushSupplierHelper.narrow(proxy);

      Discriminator pps_discriminator = pps.forwarding_discriminator();
      String line;

      java.io.LineNumberReader r =
               new java.io.LineNumberReader (new java.io.FileReader(fname));

      do
      {
        line = r.readLine();
        if (line != null)
        {
          int constraintId;
          line = line.trim();
          try
          {
            constraintId = pps_discriminator.add_constraint(line);
            System.out.println("Add Constraint " + constraintId + ": " + line);
          }
          catch (java.lang.Exception e)
          {
            System.err.println("Error in Constraint: " + line);
          }
        }
        else
        {
          break;
        }
      } while (true);

      r.close();
    }
    catch (java.io.IOException e1)
    {
      System.err.println("Error reading IOR from file " + fname);
    }
    return;
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

  protected static DistributionChannelFactory dchannel_factory( Agent admin )
  {
    System.out.println("NotificationAdmin admin.dchannel_factory();");

    DistributionChannelFactory factory = null;
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

  protected static NotificationChannel create_channel(
                                           NotificationChannelFactory factory )
  {
    System.out.println("NotificationChannelFactory factory.create_notification_channel(10,10);");

    NotificationChannel channel = null;
    try
    {
      channel = factory.create_notification_channel(10,10);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return channel;
  }

  protected static DistributionChannel create_dchannel(
                                           DistributionChannelFactory factory )
  {
    System.out.println("DistributionChannelFactory factory.create_distribution_channel(10,10, operation_mode);");

    DistributionChannel channel = null;
    try
    {
      channel = factory.create_distribution_channel(10,10,operation_mode);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return channel;
  }

  protected static ConsumerAdmin for_consumers( NotificationChannel channel )
  {
    System.out.println("NotificationChannel channel.for_consumers();");

    ConsumerAdmin consumerAdmin = null;
    try
    {
      consumerAdmin = channel.for_consumers();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return consumerAdmin;
  }


  protected static ConsumerAdmin new_for_consumers(
                                          int id, NotificationChannel channel )
  {
    System.out.println("NotificationChannel channel.channel.new_for_consumers(criteria);");

    ConsumerAdmin consumerAdmin = null;
    try
    {
      org.omg.CosLifeCycle.NVP[] criteria = new org.omg.CosLifeCycle.NVP[1];

      String name = new String("Id");
      org.omg.CORBA.Any any = orb.create_any();
      any.insert_string(String.valueOf(id));
      criteria[0] = new org.omg.CosLifeCycle.NVP(name, any);

      consumerAdmin = channel.new_for_consumers(criteria);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return consumerAdmin;
  }


  protected static ProxyPushSupplier obtain_push_consumer(
                                                 ConsumerAdmin consumer_admin )
  {
    System.out.println("ConsumerAdmin consumer_admin.obtain_push_supplier();");

    ProxyPushSupplier proxyPushSupplier = null;
    try
    {
      proxyPushSupplier = consumer_admin.obtain_push_supplier();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return proxyPushSupplier;
  }

  protected static ConsumerAdmin new_for_chain_consumers( 
                                         int id, ConsumerAdmin consumer_admin )
  {
    System.out.println(
            "ConsumerAdmin supplierAdmin.new_for_chain_consumers(criteria);" );

    ConsumerAdmin consumerAdmin = null;
    try
    {
      org.omg.CosLifeCycle.NVP[] criteria = new org.omg.CosLifeCycle.NVP[1];

      String name = new String("Id");
      org.omg.CORBA.Any any = orb.create_any();
      any.insert_string(String.valueOf(id));
      criteria[0] = new org.omg.CosLifeCycle.NVP(name, any);

      org.omg.DistributionChannelAdmin.ConsumerAdmin c_admin =
    org.omg.DistributionChannelAdmin.ConsumerAdminHelper.narrow(consumer_admin);
      consumerAdmin = c_admin.new_for_consumers(criteria);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return consumerAdmin;
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
      else if ( args[i].compareTo("-m") == 0 )
      {
        String mode = new String(args[++i]);
        if (mode.compareTo("notification") == 0)
        {
          operation_mode = OperationMode.notification;
          System.out.println("Setting up to Notification Mode.");
        }
        else if (mode.compareTo("distribution") == 0)
        {
          operation_mode = OperationMode.distribution;
          System.out.println("Setting up to Distribution Mode.");
        }
        else 
        {
          System.out.println("ERROR: Invalid Operation Mode.");
          System.out.println("Setting up to Notification Mode.");
        }
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

    //props.put("es.tid.TIDorbj.max_blocked_time","25000"); // 5000 default
    props.put("es.tid.TIDorbj.iiop.max_connections","100"); // 5000 default
		
    orb = org.omg.CORBA.ORB.init( args, props );

    org.omg.PortableServer.POA rootPOA = null;
    try
    {
      rootPOA = org.omg.PortableServer.POAHelper.narrow(
                                   orb.resolve_initial_references("RootPOA") );

      org.omg.PortableServer.POAManager poa_manager=rootPOA.the_POAManager();
      es.tid.TIDorbj.core.poa.POAManagerImpl poaMgr =
                          (es.tid.TIDorbj.core.poa.POAManagerImpl) poa_manager;
      poaMgr.set_max_threads(100);
      rootPOA.the_POAManager().activate();
    }
    catch ( java.lang.Exception ex )
    {
      ex.printStackTrace();
      if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
        ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
      System.exit(1);
    }
	
    if (operation_mode == OperationMode.notification)
    {
    try
    {
    // Obtenemos la referencia al Factory
    org.omg.CORBA.Object ref = orb.string_to_object(iorString);
    NotificationAdmin admin = NotificationAdminHelper.narrow(ref);

    NotificationChannelFactory factory = null;
    DistributionChannelFactory dfactory = null;

    factory = channel_factory(admin);

    System.out.println("");
    System.out.println("CREATING CHANNELS");
    System.out.println("=================");

    NotificationChannel[] channels = new NotificationChannel[num_channels];

    for (int i = 0; i < num_channels; i++)
    {
      System.out.println("Create Channel: " + i);
      channels[i] = create_channel(factory);
    }

    myPushConsumer consumers[][][] = new myPushConsumer
               [num_channels][num_consumer_admins+consumer_admin][num_clients];

    ProxyPushSupplier proxy_push_supplier[][][] = new ProxyPushSupplier
               [num_channels][num_consumer_admins+consumer_admin][num_clients];

    for (int i = 0; i < num_channels; i++)
    {
      for (int j = 0; j < ( num_consumer_admins+consumer_admin); j++)
      {
        ConsumerAdmin consumerAdmin = null;

        if ( (consumer_admin == 1) && (j == 0) )
        {
          System.out.println("");
          System.out.println("CREATING ConsumerAdmin");
          System.out.println("======================");

          consumerAdmin = for_consumers(channels[i]);
        }
        else
        {
          System.out.println("");
          System.out.println("CREATING New ConsumerAdmin");
          System.out.println("==========================");

          consumerAdmin = new_for_consumers(j, channels[i]);
        }

        System.out.println("");
        System.out.println("SETTING CONSUMER ADMIN FILTERS");
        System.out.println("==============================");
        setFilters(consumer_admins_filters_filename, consumerAdmin);

        System.out.println("");
        System.out.println("CREATING PUSHCONSUMERS");
        System.out.println("======================");

        for (int k = 0; k < num_clients; k++)
        {
          String id = "[c:"+i+"/a:"+j+"/c:"+k+"] ";

          // Obtenemos el ProxyPushSupplier
          System.out.print(id);
          System.out.println("obtain_push_supplier()");
          proxy_push_supplier[i][j][k] = consumerAdmin.obtain_push_supplier();

          System.out.println("New myPushConsumer: " + id);

          consumers[i][j][k] = new myPushConsumer( id, orb,
               proxy_push_supplier[i][j][k], num_events, event_type, 0, false);

          Thread th = new Thread(new myEndThread(id, consumers[i][j][k]));
          th.start();
          consumers[i][j][k].setEndThread(th);

          try
          {
            System.out.print(id);
            System.out.println("connect_push_consumer()\n");
            proxy_push_supplier[i][j][k].connect_push_consumer(
                                               consumers[i][j][k]._this(orb) );
          }
          catch ( java.lang.Exception ex_connect )
          {
            System.out.print(id);
            System.out.println("Unable to connect to supplier");
            ex_connect.printStackTrace();
          }

          System.out.println("");
          System.out.println("SETTING PROXYPUSHSUPPLIER FILTERS");
          System.out.println("=================================");
          setFilters( pushconsumer_filters_filename, j, k,
                                                proxy_push_supplier[i][j][k] );
        }
      }
    }
    }
    catch ( java.lang.Exception ex )
    {
      ex.printStackTrace();
      if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
        ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
      System.exit(1);
    }
    }
    else
    {
    try
    {
    // Obtenemos la referencia al Factory
    org.omg.CORBA.Object ref = orb.string_to_object(iorString);
    Agent admin = AgentHelper.narrow(ref);

    DistributionChannelFactory factory = dchannel_factory(admin);

    System.out.println("");
    System.out.println("CREATING CHANNELS");
    System.out.println("=================");

    DistributionChannel[] channels = new DistributionChannel[num_channels];

    for (int i = 0; i < num_channels; i++)
    {
      System.out.println("Create Channel: " + i);
      channels[i] = create_dchannel(factory);
    }

    myPushConsumer consumers[][][] = new myPushConsumer
      [num_channels][num_consumer_admins+consumer_admin+
                     num_consumer_admins*chain_num_consumer_admins+
                     consumer_admin*chain_consumer_admin ][num_clients];

    ProxyPushSupplier proxy_push_supplier[][][] = new ProxyPushSupplier
      [num_channels][num_consumer_admins+consumer_admin+
                     num_consumer_admins*chain_num_consumer_admins+
                     consumer_admin*chain_consumer_admin ][num_clients];

    for (int i = 0; i < num_channels; i++)
    {
      for (int j = 0; j < (num_consumer_admins+consumer_admin); j++)
      {
        ConsumerAdmin consumerAdmin = null;

        if ( (consumer_admin == 1) && (j == 0) )
        {
          System.out.println("");
          System.out.println("CREATING ConsumerAdmin");
          System.out.println("======================");

          consumerAdmin = for_consumers(channels[i]);
        }
        else
        {
          System.out.println("");
          System.out.println("CREATING New ConsumerAdmin");
          System.out.println("==========================");

          consumerAdmin = new_for_consumers(j, channels[i]);
        }

        System.out.println("");
        System.out.println("SETTING CONSUMER ADMIN FILTERS");
        System.out.println("==============================");
        setFilters(consumer_admins_filters_filename, consumerAdmin);

        int chain_limit = chain_consumer_admin+chain_num_consumer_admins;
        if (chain_limit > 0)
        {
          ConsumerAdmin chained_consumerAdmin = consumerAdmin;

          if ( (consumer_admin == 1) && (j == 0) )
          {
            chain_limit = chain_consumer_admin;

            System.out.println("");
            System.out.println("CREATING CHAIN CONSUMERADMIN");
            System.out.println("============================");
          }
          else
          {
            chain_limit = chain_num_consumer_admins;

            System.out.println("");
            System.out.println("CREATING CHAIN NEW CONSUMERADMIN");
            System.out.println("================================");
          }

          for (int l = 0; l < chain_limit; l++)
          {
            int new_j;
            if ( chain_consumer_admin > 0 )
            {
              new_j = l + num_consumer_admins+consumer_admin +
                               ((j > 0)?chain_consumer_admin:0)+
                                 ((j > 0)?(chain_num_consumer_admins*(j-1)):0);
            }
            else
            {
              new_j = l + num_consumer_admins+consumer_admin +
                                               (j*chain_num_consumer_admins);
            }

            ConsumerAdmin chainConsumerAdmin = 
                         new_for_chain_consumers(new_j, chained_consumerAdmin);
            
            chained_consumerAdmin = chainConsumerAdmin;

            System.out.println("");
            System.out.println("CREATING PUSHCONSUMERS FOR CHAIN CADM");
            System.out.println("=====================================");

            for (int k = 0; k < num_clients; k++)
            {
              String id = "[c:"+i+"/a:"+new_j+"/c:"+k+"] ";

              // Obtenemos el ProxyPushSupplier
              System.out.print(id);
              System.out.println("obtain_push_supplier()");
              proxy_push_supplier[i][new_j][k] = 
                                     chainConsumerAdmin.obtain_push_supplier();

              System.out.println("New myPushConsumer: " + id);

              consumers[i][new_j][k] = new myPushConsumer( id, orb,
            proxy_push_supplier[i][new_j][k], num_events, event_type, 0, false);

              Thread th=new Thread(new myEndThread(id,consumers[i][new_j][k]));
              th.start();
              consumers[i][new_j][k].setEndThread(th);

              try
              {
                System.out.print(id);
                System.out.println("connect_push_consumer()\n");
                proxy_push_supplier[i][new_j][k].connect_push_consumer(
                                           consumers[i][new_j][k]._this(orb) );
              }
              catch ( java.lang.Exception ex_connect )
              {
                System.out.print(id);
                System.out.println("Unable to connect to supplier");
                ex_connect.printStackTrace();
              }

              System.out.println("");
              System.out.println("SETTING PROXYPUSHSUPPLIER FILTERS");
              System.out.println("=================================");
              setFilters( pushconsumer_filters_filename, new_j, k,
                                            proxy_push_supplier[i][new_j][k] );
            }
          }
        }

        System.out.println("");
        System.out.println("CREATING PUSHCONSUMERS");
        System.out.println("======================");


        for (int k = 0; k < num_clients; k++)
        {
          String id = "[c:"+i+"/a:"+j+"/c:"+k+"] ";

          // Obtenemos el ProxyPushSupplier
          System.out.print(id);
          System.out.println("obtain_push_supplier()");
          proxy_push_supplier[i][j][k] = consumerAdmin.obtain_push_supplier();

          System.out.println("New myPushConsumer: " + id);

          consumers[i][j][k] = new myPushConsumer( id, orb,
               proxy_push_supplier[i][j][k], num_events, event_type, 0, false);

          Thread th = new Thread(new myEndThread(id, consumers[i][j][k]));
          th.start();
          consumers[i][j][k].setEndThread(th);

          try
          {
            System.out.print(id);
            System.out.println("connect_push_consumer()\n");
            proxy_push_supplier[i][j][k].connect_push_consumer(
                                               consumers[i][j][k]._this(orb) );
          }
          catch ( java.lang.Exception ex_connect )
          {
            System.out.print(id);
            System.out.println("Unable to connect to supplier");
            ex_connect.printStackTrace();
          }

          System.out.println("");
          System.out.println("SETTING PROXYPUSHSUPPLIER FILTERS");
          System.out.println("=================================");
          setFilters( pushconsumer_filters_filename, j, k,
                                                proxy_push_supplier[i][j][k] );
        }
      }
    }
    }
    catch ( java.lang.Exception ex )
    {
      ex.printStackTrace();
      if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
        ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
      System.exit(1);
    }
    }

    orb.run();

    if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
