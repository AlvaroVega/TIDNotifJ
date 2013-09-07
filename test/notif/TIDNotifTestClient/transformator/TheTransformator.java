package es.tid.corba.TIDNotifTestClient.transformator;

import java.util.Properties;

import es.tid.corba.TIDNotifTestClient.common.ServiceClient;

import es.tid.corba.TIDDistribAdmin.Agent;
import es.tid.corba.TIDDistribAdmin.AgentHelper;

import org.omg.ServiceManagement.OperationMode;

import org.omg.CosEventChannelAdmin.SupplierAdmin;
import org.omg.CosEventChannelAdmin.ProxyPushConsumer;

import org.omg.DistributionChannelAdmin.DistributionChannelFactory;
import org.omg.DistributionChannelAdmin.DistributionChannel;

import org.omg.TransformationAdmin.TransformingOperator;
import org.omg.TransformationAdmin.AssignedTransformingRule;
import org.omg.TransformationAdmin.AssignedRuleOrder;

public class TheTransformator extends ServiceClient
{
  private static final String NAME = "TheTransformator";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + "-f ior_filename";

  private static org.omg.CORBA.ORB orb;
  private static TransformingOperator ppc_operator;

  private static String iorFilename = null;

  private static void showHelp()
  {
    System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
  }

  static void add_operator(java.io.LineNumberReader r)
  {
    //raises (AlreadyDefinedId, InvalidConstraint);
    //add 
    //SEGUNDA
    //$.id > 0
    //operator.ior
    //do_nothing

    try
    {
    String op_id = r.readLine();
    String op_cnstr = r.readLine();
    String op_ior_filename = r.readLine();
    String op_function = r.readLine();

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

      System.out.println( "ADD OPERATOR: " + op_id + ", if: " + op_cnstr);
      try
      {
        int order = ppc_operator.add_transforming_rule(
                                     op_id, op_cnstr, ref, op_function );
        System.out.println( "  Order => " + order );
      }
      catch (org.omg.TransformationAdmin.AlreadyDefinedId ex1)
      {
        System.err.println(" *** ERROR: AlreadyDefinedId");
      }
      catch (org.omg.TransformationAdmin.InvalidConstraint ex2)
      {
        System.err.println(" *** ERROR: InvalidConstraint");
      }
      catch (java.lang.Exception ex3)
      {
        System.err.println(" *** ERROR UNKNOWN");
      }
    }
    }
    catch (java.lang.Exception e)
    {
      e.printStackTrace();
    }
  }

  static void insert_operator(java.io.LineNumberReader r)
  {
    //raises (AlreadyDefinedOrder, AlreadyDefinedId, InvalidConstraint);
    //insert 
    //1000 
    //PRIMERA 
    //$.id > 0
    //operator.ior
    //do_nothing

    try
    {
    String op_order = r.readLine();
    String op_id = r.readLine();
    String op_cnstr = r.readLine();
    String op_ior_filename = r.readLine();
    String op_function = r.readLine();

    if ( (op_order != null) && (op_id != null) && (op_cnstr != null) &&
         (op_ior_filename != null) && (op_function != null) )
    {
      op_order = op_order.trim();
      op_id = op_id.trim();
      op_cnstr = op_cnstr.trim();
      op_ior_filename = op_ior_filename.trim();
      op_function = op_function.trim();

      String iorString = readStringIOR(op_ior_filename);
      if (iorString == null) System.exit(0);
      org.omg.CORBA.Object ref = orb.string_to_object(iorString);

      System.out.println( "INSERT OPERATOR: " 
                      + op_id + " - Order: " + op_order + ", if: " + op_cnstr);
      try
      {
        ppc_operator.insert_transforming_rule(
               Integer.parseInt(op_order), op_id, op_cnstr, ref, op_function );
      }
      catch (org.omg.TransformationAdmin.AlreadyDefinedOrder ex0)
      {
        System.err.println(" *** ERROR: AlreadyDefinedOrder");
      }
      catch (org.omg.TransformationAdmin.AlreadyDefinedId ex1)
      {
        System.err.println(" *** ERROR: AlreadyDefinedId");
      }
      catch (org.omg.TransformationAdmin.InvalidConstraint ex2)
      {
        System.err.println(" *** ERROR: InvalidConstraint");
      }
      catch (java.lang.Exception ex3)
      {
        System.err.println(" *** ERROR UNKNOWN");
        ex3.printStackTrace();
      }
    }
    }
    catch (java.lang.Exception e)
    {
      e.printStackTrace();
    }
  }

  static void delete_operator(java.io.LineNumberReader r)
  {
    //raises (NotFound);
    //id

    try
    {
    String op_id = r.readLine();

    if (op_id != null) 
    {
      op_id = op_id.trim();

      System.out.println( "DELETE OPERATOR: " + op_id );

      try
      {
        ppc_operator.delete_transforming_rule( op_id );
      }
      catch (org.omg.TransformationAdmin.NotFound ex1)
      {
        System.err.println(" *** ERROR: NotFound");
      }
      catch (java.lang.Exception ex3)
      {
        System.err.println(" *** ERROR UNKNOWN");
      }
    }
    }
    catch (java.lang.Exception e)
    {
      e.printStackTrace();
    }
  }

  static void move_operator(java.io.LineNumberReader r)
  {
    //raises (NotFound, AlreadyDefinedOrder);
    //move 
    //id 
    //new_position

    try
    {
    String op_id = r.readLine();
    String op_new_order = r.readLine();

    if ( (op_id != null) && (op_new_order != null) )
    {
      op_id = op_id.trim();
      op_new_order = op_new_order.trim();

      System.out.println( "MOVE OPERATOR: " 
                                        + op_id + " - Order: " + op_new_order);
      try
      {
        ppc_operator.move_transforming_rule( 
                                       op_id, Integer.parseInt(op_new_order) );
      }
      catch (org.omg.TransformationAdmin.AlreadyDefinedOrder ex0)
      {
        System.err.println(" *** ERROR: AlreadyDefinedOrder");
      }
      catch (org.omg.TransformationAdmin.NotFound ex1)
      {
        System.err.println(" *** ERROR: NotFound");
      }
      catch (java.lang.Exception ex3)
      {
        System.err.println(" *** ERROR UNKNOWN");
      }
    }
    }
    catch (java.lang.Exception e)
    {
      e.printStackTrace();
    }
  }

  static void replace_operator(java.io.LineNumberReader r)
  {
    //raises (NotFound, InvalidConstraint);
    //replace 
    //id 
    //constraint 
    //operator.ior 
    //do_nothing

    try
    {
    String op_id = r.readLine();
    String op_cnstr = r.readLine();
    String op_ior_filename = r.readLine();
    String op_function = r.readLine();

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

      System.out.println( "REPLACE OPERATOR: " + op_id + ", if: " + op_cnstr);
      try
      {
        ppc_operator.replace_transforming_rule(
                                     op_id, op_cnstr, ref, op_function );
      }
      catch (org.omg.TransformationAdmin.NotFound ex1)
      {
        System.err.println(" *** ERROR: NotFound");
      }
      catch (org.omg.TransformationAdmin.InvalidConstraint ex2)
      {
        System.err.println(" *** ERROR: InvalidConstraint");
      }
      catch (java.lang.Exception ex3)
      {
        System.err.println(" *** ERROR UNKNOWN");
      }
    }
    }
    catch (java.lang.Exception e)
    {
      e.printStackTrace();
    }
  }

  static void get_operator(java.io.LineNumberReader r)
  {
    //raises (NotFound);
    //get_t
    //id

    try
    {
    String op_id = r.readLine();

    if (op_id != null) 
    {
      op_id = op_id.trim();

      System.out.println( "GET OPERATOR: " + op_id );

      try
      {
        org.omg.TransformationAdmin.TransformingRule t_rule;
        t_rule = ppc_operator.get_transforming_rule( op_id );
        System.out.println( "  expresion:  " + t_rule.expression );
        System.out.println( "  object ref: " + t_rule.object_ref );
        System.out.println( "  operation:  " + t_rule.operation );
      }
      catch (org.omg.TransformationAdmin.NotFound ex1)
      {
        System.err.println(" *** ERROR: NotFound");
      }
      catch (java.lang.Exception ex3)
      {
        System.err.println(" *** ERROR UNKNOWN");
      }
    }
    }
    catch (java.lang.Exception e)
    {
      e.printStackTrace();
    }
  }

  static void get_operator_order(java.io.LineNumberReader r)
  {
    //raises (NotFound);
    //get_to
    //id

    try
    {
    String op_id = r.readLine();

    if (op_id != null) 
    {
      op_id = op_id.trim();

      System.out.println( "GET TR ORDER: " + op_id );

      try
      {
        int order = ppc_operator.get_transforming_rule_order( op_id );
        System.out.println( "  ORDER: " + order );
      }
      catch (org.omg.TransformationAdmin.NotFound ex1)
      {
        System.err.println(" *** ERROR: NotFound");
      }
      catch (java.lang.Exception ex3)
      {
        System.err.println(" *** ERROR UNKNOWN");
      }
    }
    }
    catch (java.lang.Exception e)
    {
      e.printStackTrace();
    }
  }

  static void list_operators()
  {
    System.out.println( "GET TRS " );

    try
    {
      AssignedTransformingRule[] trs = ppc_operator.get_transforming_rules( );

      if (trs.length == 0)
      {
        System.out.println(" NO TRANSFORMING RULES!");
      }
      else
      {
        for (int i = 0; i < trs.length; i++)
        {
          System.out.println( "  TRANSFORMING RULE " + trs[i].id);
          System.out.println( "    expresion:  " + trs[i].rule.expression );
          System.out.println( "    object ref: " + trs[i].rule.object_ref );
          System.out.println( "    operation:  " + trs[i].rule.operation );
        }
      }
    }
    catch (java.lang.Exception ex3)
    {
      System.err.println(" *** ERROR UNKNOWN");
    }
  }

  static void list_operators_order()
  {
    //get_trso
    System.out.println( "GET TRS ORDER" );

    try
    {
      AssignedRuleOrder[] trso = ppc_operator.get_transforming_rules_order();
      if (trso.length == 0)

      {
        System.out.println(" NO TRANSFORMING RULES!");
      }
      else
      {
        for (int i = 0; i < trso.length; i++)
        {
          System.out.println( "  TRANSFORMING RULE " + trso[i].id);
          System.out.println( "    Order: " + trso[i].position);
        }
      }
    }
    catch (java.lang.Exception ex3)
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

      System.out.println("CREATING SUPPLIERADMIN");
      System.out.println("======================");
      System.out.println("");

      SupplierAdmin supplier_admin = null;
      try
      {
        supplier_admin = channel.for_suppliers();
        System.out.println("  OK!");
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.exit(1);
      }

      System.out.println("CREATING PROXYPUSHCONSUMER");
      System.out.println("==========================");
      System.out.println("");

      ProxyPushConsumer proxyPushConsumer = null;
      try
      {
        proxyPushConsumer = supplier_admin.obtain_push_consumer();
        System.out.println("  OK!");
      }
      catch (Exception e)
      {
        e.printStackTrace();
        System.exit(1);
      }

      try
      {
        org.omg.DistributionChannelAdmin.ProxyPushConsumer ppc =
          org.omg.DistributionChannelAdmin.ProxyPushConsumerHelper.narrow(
                                                           proxyPushConsumer );

        System.out.println("GET OPERATOR");
        System.out.println("============");
        System.out.println("");

        ppc_operator = ppc.operator();
        System.out.println("  OK!");

        String op_id, op_cnstr, op_ior_filename, op_function;

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
            add_operator(r);
          }
          else if (op_name.compareTo("insert") == 0)
          {
            insert_operator(r);
          }
          else if (op_name.compareTo("delete") == 0)
          {
            delete_operator(r);
          }
          else if (op_name.compareTo("move") == 0)
          {
            move_operator(r);
          }
          else if (op_name.compareTo("replace") == 0)
          {
            replace_operator(r);
          }
          else if (op_name.compareTo("get_tr") == 0)
          {
            get_operator(r);
          }
          else if (op_name.compareTo("get_tro") == 0)
          {
            get_operator_order(r);
          }
          else if (op_name.compareTo("get_trs") == 0)
          {
            list_operators();
          }
          else if (op_name.compareTo("get_trso") == 0)
          {
            list_operators_order();
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
