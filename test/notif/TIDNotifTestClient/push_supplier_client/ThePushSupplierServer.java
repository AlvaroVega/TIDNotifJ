package es.tid.corba.TIDNotifTestClient.push_supplier_client;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import java.util.Properties;

import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.NotificationChannelAdmin.NotificationChannelFactory;
import org.omg.NotificationChannelAdmin.NotificationChannel;

import org.omg.CosEventChannelAdmin.SupplierAdmin;
import org.omg.CosEventChannelAdmin.SupplierAdminHelper;

import org.omg.CosEventChannelAdmin.ProxyPushConsumer;

import org.omg.ConstraintAdmin.Discriminator;

import org.omg.TransformationAdmin.TransformingOperator;

public class ThePushSupplierServer extends ServiceClient
{
  private static final String NAME = "PushServer";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + 
                                    "-e envent_file < proxy_push_consumer_IOR";

  private static org.omg.CORBA.ORB orb;
  private static org.omg.CosEventChannelAdmin.ProxyPushConsumer 
                                                           proxy_push_consumer;

  private static int num_channels = 0;
  private static int supplier_admin = 0;
  private static int chain_supplier_admin = 0;
  private static int num_supplier_admins = 0;
  private static int chain_num_supplier_admins = 0;
  private static String supplier_admins_filters_filename = null;
  private static String supplier_admins_operators_filename = null;
  private static int num_clients = 0;
  private static String pushsupplier_filters_filename = null;
  private static String pushsupplier_operators_filename = null;
  private static int num_events = 0;
  private static int rate = 0;

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
      if ((pos = line.indexOf("+")) < 0)
      {
        supplier_admin = Integer.parseInt(line);
      }
      else
      {
        supplier_admin = Integer.parseInt(line.substring(0, pos).trim());
        chain_supplier_admin = 
                 Integer.parseInt(line.substring(pos+1, line.length()).trim());
      }

      // Num. New SupplierAdmin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      if ((pos = line.indexOf("+")) < 0)
      {
        num_supplier_admins = Integer.parseInt(line);
      }
      else
      {
        num_supplier_admins = Integer.parseInt(line.substring(0, pos).trim());
        chain_num_supplier_admins = 
                 Integer.parseInt(line.substring(pos+1, line.length()).trim());
      }

      // Nombre del fichero con los filtros a los Suppliers Admin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      supplier_admins_filters_filename = new String(line);

      // Nombre del fichero con los Operadores de los Suppliers Admin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      supplier_admins_operators_filename = new String(line);

      // ConsumerAdmin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Num. New ConsumerAdmin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Nombre del fichero con los filtros a los Consumers Admin
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Num. PushSupplier Clients
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      num_clients = Integer.parseInt(line);

      // Nombre del fichero con los filtros a los PushSuppliers
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      pushsupplier_filters_filename = new String(line);

      // Nombre del fichero con los Operadores de los PushSuppliers
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      pushsupplier_operators_filename = new String(line);

      // Num. PushConsumer Clients
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Nombre del fichero con los filtros a los PushConsumers
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));

      // Num. Events
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      num_events = Integer.parseInt(line);

      // Event Type
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      event_type = Integer.parseInt(line);

      // Event Rate
      do
      {
        line = r.readLine().trim();
      } while (line.startsWith("#"));
      rate = Integer.parseInt(line);

      r.close();

      System.out.println("Num. Channels = " +  num_channels);
      System.out.println("SupplierAdmin = " +  supplier_admin);
      System.out.println("Chain SupplierAdmin = " +  chain_supplier_admin);
      System.out.println("Num. New SupplierAdmins = " +  num_supplier_admins);
      System.out.println("Chain Num. New SupplierAdmins = " +  chain_num_supplier_admins);
      System.out.println("Num. Clients = " +  num_clients);
      System.out.println("Num. Events = " +  num_events);
      System.out.println("Event type = " + event_type);
      System.out.println("Rate = " + rate);

      return 0;
    }
    catch (java.io.IOException e1)
    {
      System.err.println("Error reading IOR from file " + filename);
      num_channels = 0;
      supplier_admin = 0;
      chain_supplier_admin = 0;
      num_supplier_admins = 0;
      chain_num_supplier_admins = 0;
      num_clients = 0;
      num_events = 0;
      event_type = 0;
      rate = 0;
    }

    System.out.println("Num. Channels = " +  num_channels);
    System.out.println("SupplierAdmin = " +  supplier_admin);
    System.out.println("Chain SupplierAdmin = " +  chain_supplier_admin);
    System.out.println("Num. New SupplierAdmins = " +  num_supplier_admins);
    System.out.println("Chain Num. New SupplierAdmins = " +  chain_num_supplier_admins);
    System.out.println("Num. Clients = " +  num_clients);
    System.out.println("Num. Events = " +  num_events);
    System.out.println("Event type = " + event_type);

    return -1;
  }

  protected static void setFilters( String filename, SupplierAdmin admin )
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

    if (admin != null)
    {
      String fname = filename + ".FIL";

      try
      {
        org.omg.NotificationChannelAdmin.SupplierAdmin sAdmin =
            org.omg.NotificationChannelAdmin.SupplierAdminHelper.narrow(admin);

        Discriminator sa_discriminator = sAdmin.forwarding_discriminator();
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
              constraintId = sa_discriminator.add_constraint(line);
              System.out.println("Add Constraint " +constraintId+ ": " + line);
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
    }
    return;
  }

  protected static void setFilters( String filename,
                                    int adm, 
                                    int prxy, 
                                    ProxyPushConsumer proxy)
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
      org.omg.NotificationChannelAdmin.ProxyPushConsumer ppc =
        org.omg.NotificationChannelAdmin.ProxyPushConsumerHelper.narrow(proxy);

      Discriminator ppc_discriminator = ppc.forwarding_discriminator();
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
            constraintId = ppc_discriminator.add_constraint(line);
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

  protected static void setOperators( String filename, SupplierAdmin admin )
  {
    if (filename == null)
    {
      System.out.println("*** NO OPERATORS ***");
      return;
    }

    if (filename.compareTo("none") == 0)
    {
      System.out.println("*** NO OPERATORS ***");
      return;
    }

    if (admin != null)
    {
      String fname = filename + ".OPR";

      try
      {
        org.omg.DistributionChannelAdmin.SupplierAdmin sAdmin =
            org.omg.DistributionChannelAdmin.SupplierAdminHelper.narrow(admin);

        TransformingOperator sa_operator = sAdmin.operator();

        String op_id, op_cnstr, op_ior_filename, op_function;

        java.io.LineNumberReader r = 
                  new java.io.LineNumberReader (new java.io.FileReader(fname));

        do
        {
          op_id = r.readLine();
          op_cnstr = r.readLine();
          op_ior_filename = r.readLine();
          op_function = r.readLine();

          if ( (op_id != null) && (op_cnstr != null) &&
               (op_ior_filename != null) && (op_function != null) )
          {
            op_id = op_id.trim();
            op_cnstr = op_cnstr.trim();
            op_ior_filename = op_ior_filename.trim();
            op_function = op_function.trim();
          
            String iorString = readStringIOR(op_ior_filename);
            System.out.println("Add Operator: " +op_id+ ", if: " + op_cnstr);
            if (iorString == null)
            {
              System.out.println("ERROR: Operator IOR String: is NULL.");
            }
            else
            {
              org.omg.CORBA.Object ref = orb.string_to_object(iorString);

              try
              {
                sa_operator.add_transforming_rule( 
                                           op_id, op_cnstr, ref, op_function );
              }
              catch (java.lang.Exception e)
              {
                System.err.println("Error in Operator: " + op_id);
                e.printStackTrace();
              }
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
    }
    return;
  }

  protected static void setOperators( String filename,
                                      int adm, 
                                      int prxy, 
                                      ProxyPushConsumer proxy)
  {
    if (filename == null)
    {
      System.out.println("*** NO OPERATORS ***");
      return;
    }

    if (filename.compareTo("none") == 0)
    {
      System.out.println("*** NO OPERATORS ***");
      return;
    }

    String fname = filename + "." + adm + "." + prxy + ".OPR";

    try
    {
      org.omg.DistributionChannelAdmin.ProxyPushConsumer ppc =
        org.omg.DistributionChannelAdmin.ProxyPushConsumerHelper.narrow(proxy);

      TransformingOperator ppc_operator = ppc.operator();
      String op_id, op_cnstr, op_ior_filename, op_function;

      java.io.LineNumberReader r = 
               new java.io.LineNumberReader (new java.io.FileReader(fname));

      do
      {
        op_id = r.readLine();
        op_cnstr = r.readLine();
        op_ior_filename = r.readLine();
        op_function = r.readLine();

        if ( (op_id != null) && (op_cnstr != null) &&
             (op_ior_filename != null) && (op_function != null) )
        {
          op_id = op_id.trim();
          op_cnstr = op_cnstr.trim();
          op_ior_filename = op_ior_filename.trim();
          op_function = op_function.trim();
          
          String iorString = readStringIOR(op_ior_filename);
          if (iorString == null) System.exit(0);
          org.omg.CORBA.Object ref = orb.string_to_object(iorString);

          try
          {
            ppc_operator.add_transforming_rule( 
                                           op_id, op_cnstr, ref, op_function );

            System.out.println("Add Operator: " +op_id+ ", if: " + op_cnstr);
          }
          catch (java.lang.Exception e)
          {
            System.err.println("Error in Operator: " + op_id);
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
      System.exit(1);
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
      System.exit(1);
    }
    return channel;
  }

  protected static SupplierAdmin for_suppliers( NotificationChannel channel )
  {
    System.out.println("NotificationChannel channel.for_suppliers();");

    SupplierAdmin supplierAdmin = null;
    try
    {
      supplierAdmin = channel.for_suppliers();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return supplierAdmin;
  }

  protected static SupplierAdmin new_for_suppliers(
                                          int id, NotificationChannel channel )
  {
    System.out.println("NotificationChannel channel.new_for_suppliers(criteria);");

    SupplierAdmin supplierAdmin = null;
    try
    {
      org.omg.CosLifeCycle.NVP[] criteria = new org.omg.CosLifeCycle.NVP[1];

      String name = new String("Id");
      org.omg.CORBA.Any any = orb.create_any();
      any.insert_string(String.valueOf(id));
      criteria[0] = new org.omg.CosLifeCycle.NVP(name, any);

      supplierAdmin = channel.new_for_suppliers(criteria);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return supplierAdmin;
  }

  protected static SupplierAdmin for_chain_suppliers( 
                                         int id, SupplierAdmin supplier_admin )
  {
    System.out.println(
                  "SupplierAdmin supplierAdmin.new_for_suppliers(criteria);" );

    SupplierAdmin supplierAdmin = null;
    try
    {
      org.omg.CosLifeCycle.NVP[] criteria = new org.omg.CosLifeCycle.NVP[1];

      String name = new String("Id");
      org.omg.CORBA.Any any = orb.create_any();
      any.insert_string(String.valueOf(id));
      criteria[0] = new org.omg.CosLifeCycle.NVP(name, any);

      org.omg.DistributionChannelAdmin.SupplierAdmin s_admin =
    org.omg.DistributionChannelAdmin.SupplierAdminHelper.narrow(supplier_admin);
      supplierAdmin = s_admin.new_for_suppliers(criteria);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return supplierAdmin;
  }

  protected static ProxyPushConsumer obtain_push_consumer(
                                                 SupplierAdmin supplier_admin )
  {
    System.out.println("SupplierAdmin supplier_admin.obtain_push_consumer();");

    if (supplier_admin != null)
    {
      try
      {
        ProxyPushConsumer proxyPushConsumer 
                                       = supplier_admin.obtain_push_consumer();
        return proxyPushConsumer;
      }
      catch (Exception e)
      {
      }
    }
    return null;
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
		
    props.put("es.tid.TIDorbj.max_blocked_time","25000"); // 5000 default
    props.put("es.tid.TIDorbj.iiop.max_connections","100"); // 5000 default

    orb = org.omg.CORBA.ORB.init( args, props );

    org.omg.PortableServer.POA rootPOA = null;
    try
    {
      rootPOA = org.omg.PortableServer.POAHelper.narrow(
                                   orb.resolve_initial_references("RootPOA") );

      org.omg.PortableServer.POAManager poa_manager = rootPOA.the_POAManager();
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
	
    try
    {
    // Obtenemos la referencia al Factory
    org.omg.CORBA.Object ref = orb.string_to_object(iorString);
    NotificationAdmin admin = NotificationAdminHelper.narrow(ref);

    NotificationChannelFactory factory = channel_factory(admin);

    System.out.println("");
    System.out.println("LOOKING FOR CHANNELS");
    System.out.println("====================");

    NotificationChannel[] channels = null;

    for (int i = 0; i < 5; i++)
    {
      channels = factory.channels();

      if (channels.length < num_channels)
      {
        System.out.println("Num. channels INSUFICIENTE. (sleep...)");
        try
        {
          Thread.sleep(2000);
        }
        catch (Exception ex)
        {
        }
      }
      else
      {
        break;
      }
    }

    if (channels.length < num_channels)
    {
      System.out.println("ERROR: Num. channels INSUFICIENTE. (exiting...)");
      if (orb instanceof es.tid.TIDorbj.core.TIDORB)
        ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
      System.exit(1);
    }

    myPushSupplier suppliers[][][] = new myPushSupplier 
               [num_channels][num_supplier_admins+supplier_admin][num_clients];

    Thread threads[][][] = 
     new Thread[num_channels][num_supplier_admins+supplier_admin][num_clients];

    for (int i = 0; i < num_channels; i++)
    {
      for (int j = 0; j < (num_supplier_admins+supplier_admin); j++)
      {
        SupplierAdmin supplierAdmin = null;

        if ( (supplier_admin == 1) && (j == 0) )
        {
          System.out.println("");
          System.out.println("CREATING SupplierAdmin");
          System.out.println("======================");
          supplierAdmin = for_suppliers(channels[i]);

          for (int chain = 0; chain < chain_supplier_admin; chain++)
          {
            System.out.println("");
            System.out.println("CREATING CHAIN SupplierAdmin");
            System.out.println("============================");
            for_chain_suppliers(j, supplierAdmin);
          }
        }
        else
        {
          System.out.println("");
          System.out.println("CREATING New SupplierAdmin");
          System.out.println("==========================");
          supplierAdmin = new_for_suppliers(j, channels[i]);

          for (int chain = 0; chain < chain_num_supplier_admins; chain++)
          {
            System.out.println("CREATING CHAIN New SupplierAdmin");
            System.out.println("================================");
            for_chain_suppliers(j, supplierAdmin);
          }
        }

        System.out.println("");
        System.out.println("SETTING SUPPLIER ADMIN FILTERS");
        System.out.println("==============================");
        setFilters(supplier_admins_filters_filename, supplierAdmin);

        System.out.println("");
        System.out.println("SETTING SUPPLIER ADMIN OPERATORS");
        System.out.println("================================");
        setOperators(supplier_admins_operators_filename, supplierAdmin);

        System.out.println("");
        System.out.println("CREATING PUSHSUPPLIERS");
        System.out.println("======================");

        ProxyPushConsumer proxy_push_consumer = null;

        for (int k = 0; k < num_clients; k++)
        {
          String id = "[c:"+i+"/a:"+j+"/c:"+k+"] ";

          // Obtenemos el ProxyPushConsummer
          System.out.print(id);
          System.out.println("obtain_push_consumer()");
          proxy_push_consumer = obtain_push_consumer(supplierAdmin);

          if (proxy_push_consumer == null)
          {
            System.out.print(id);
            System.out.println("ERROR: NO CREATE New myPushSupplier.");
            suppliers[i][j][k] = null;
          }
          else
          {
            System.out.print(id);
            System.out.println("*** NEW myPushSupplier ***");

            suppliers[i][j][k] = new myPushSupplier(
            id, orb, proxy_push_consumer, num_events, event_type, rate, false);
            try 
            {
              System.out.print(id);
              System.out.println("TRYING connect_push_supplier()");
              proxy_push_consumer.connect_push_supplier(
                                               suppliers[i][j][k]._this(orb) );
            }
            catch ( java.lang.Exception ex_connect )
            {
              System.out.print(id);
              System.out.println(" ### ERROR: Unable to connect to consumer.");
            }

            System.out.println("");
            System.out.println("SETTING PROXYPUSHCONSUMER FILTERS");
            System.out.println("=================================");

            setFilters(pushsupplier_filters_filename,j,k,proxy_push_consumer);

            System.out.println("");
            System.out.println("SETTING PROXYPUSHCONSUMER OPERATORS");
            System.out.println("===================================");

            setOperators( pushsupplier_operators_filename,
                                                    j, k, proxy_push_consumer);
          }
        }

        System.out.println("");
        System.out.println("CREATING THREADS");
        System.out.println("================\n");
    
        for (int k = 0; k < num_clients; k++)
        {
          String id = "[c:"+i+"/a:"+j+"/c:"+k+"] ";

          if (suppliers[i][j][k] == null)
          {
            threads[i][j][k] = null;
            System.out.print(id);
            System.out.println("*** SKIPPING PushSupplier (null) ***");
          }
          else
          {
            threads[i][j][k] = new Thread(suppliers[i][j][k]);
            System.out.print(id);
            System.out.println("THREAD for PushSupplier Created!!!");
          }
        }
      }
    }

    for (int i = 0; i < num_channels; i++)
    {
      for (int j = 0; j < (num_supplier_admins+supplier_admin); j++)
      {
        System.out.println("");
        System.out.println("STARTING PUSHSUPPLIERS");
        System.out.println("======================");

        for (int k = 0; k < num_clients; k++)
        {
          String id = "[c:"+i+"/a:"+j+"/c:"+k+"] ";

          if (threads[i][j][k] == null)
          {
            System.out.print(id);
            System.out.println("*** SKIPPING Thread (null) ***");
          }
          else
          {
            threads[i][j][k].start();
            System.out.print(id);
            System.out.println("THREAD for PushSupplier Started!!!");
          }
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

    orb.run();

    if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
