///////////////////////////////////////////////////////////////////////////
//
// File          :
// Description   :
//
// Author/s      : Alvaro Rodriguez
// Project       :
// Rel           :
// Created       :
// Revision Date :
// Rev. History  :
//
// Copyright 2000 Telefonica, I+D. All Rights Reserved.
//
// The copyright of the software program(s) is property of Telefonica.
// The program(s) may be used, modified and/or copied only with the
// express written consent of Telefonica or in acordance with the terms
// and conditions stipulated in the agreement/contract under which
// the program(s) have been supplied.
//
///////////////////////////////////////////////////////////////////////////

package es.tid.corba.TIDNotif;

/**
 * Server: PushSupplier's
 */

import es.tid.corba.TIDNotif.ThePOAFactory;
import org.omg.NotificationChannelAdmin.SupplierAdmin;
import org.omg.CosNotifyChannelAdmin.*;

import es.tid.TIDorbj.core.TIDORB;

public class PushServer extends Base
{		
  static final boolean VERBOSE = false;
  static final int NUM_DEF_SUPPLIERS = 1;
  static final int NUM_DEF_EVENTOS = 100;

  public static org.omg.NotificationChannelAdmin.NotificationChannel 
    getFirstChannel( 
          org.omg.NotificationChannelAdmin.NotificationChannelFactory factory )
  {
    org.omg.NotificationChannelAdmin.NotificationChannel[] lista =
                                                            factory.channels();
    if (lista.length == 0)
    {
      System.out.println("TIDNotifServer: !NO CHANNELS!");
    }
    else
    {
      for (int i = 0; i < lista.length; i++)
      {
        System.out.println("TIDNotif: !FOUND ONE CHANNEL!");
        return lista[i];
      }
    }
   return null;
  }

  private static SupplierAdmin createSupplierAdmin( org.omg.CORBA.ORB orb,
                   org.omg.NotificationChannelAdmin.NotificationChannel canal )
  {
    String idCriteria = "SUPPLIER_ADMIN_EVENT_PROCESSOR";

    org.omg.CosLifeCycle.NVP[] criteria = new org.omg.CosLifeCycle.NVP[1];

    org.omg.CORBA.Any value = orb.create_any();
    value.insert_string(idCriteria);

    criteria[0] = new org.omg.CosLifeCycle.NVP("Id", value);

    System.out.println( "Obtaining suppliers with criteria Id = " + 
                                          criteria[0].value.extract_string() );
    
    // Si no existe ninguno, se crea uno, sino se coge el primero
    SupplierAdmin supplierAdmin=null;

    try
    {
      boolean found = true;
      try
      {
        // Primero obtener los Supplier Admin
        SupplierAdmin[] supplierAdmins = canal.find_for_suppliers(criteria);
        System.out.println( "SupplierAdmin found. Using it." );
        supplierAdmin = supplierAdmins[0];
      }
      catch ( org.omg.NotificationChannelAdmin.CannotMeetCriteria ex)
      {
        System.out.println( "SupplierAdmin not found. Creating one" );
        //supplierAdmin = canal.new_for_suppliers(criteria);
        CosNotifyChannelAdmin.AdminID id;
        supplierAdmin = canal.new_for_suppliers(CosNotifyChannelAdmin.AND_OP, id);
      }
    }
    catch ( org.omg.NotificationChannelAdmin.InvalidCriteria ex)
    {
      System.out.println( "ERROR: InvalidCriteria Exception." );
      System.exit(-1);
    }
    catch ( java.lang.Exception ex )
    {
      ex.printStackTrace();
      System.exit(-1);
    }

      try
      {
        // Primero obtener los Supplier Admin
        SupplierAdmin[] supplierAdmins = canal.find_for_suppliers(criteria);
        System.out.println( "SupplierAdmin found. Using it." );
        supplierAdmin = supplierAdmins[0];
      }
      catch ( org.omg.NotificationChannelAdmin.InvalidCriteria ex)
      {
        System.out.println( "SupplierAdmin not found. Creating one" );
        System.exit(1);
      }
      catch ( org.omg.NotificationChannelAdmin.CannotMeetCriteria ex)
      {
        System.out.println( "SupplierAdmin not found. Exit" );
        System.exit(1);
      }

    return supplierAdmin; 
  }

  private static org.omg.CORBA.ORB orbInit( String args[] )
  {
    java.util.Properties props = new java.util.Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
    System.getProperties().put( "org.omg.CORBA.ORBSingletonClass",
                                          "es.tid.TIDorbj.core.SingletonORB" );

    // Puerto de escucha por defecto para que sea una referencia constante
    //props.put("es.tid.TIDorbj.iiop.orb_port","2002");

    //props.put("es.tid.TIDorbj.trace.level","0"); // 0 default
    //props.put("es.tid.TIDorbj.trace.file","tidorbj.out"); // 0 default

    // Initialize the ORB
    return org.omg.CORBA.ORB.init(args, props);
  }

  public static void main( String args [] )
  {
    org.omg.NotificationService.NotificationAdmin admin = null;

    org.omg.CORBA.ORB orb = orbInit(args);

    org.omg.PortableServer.POA rootPOA = null;
    try
    {
      rootPOA = org.omg.PortableServer.POAHelper.narrow(orb.resolve_initial_references("RootPOA"));		
    }
    catch ( org.omg.CORBA.ORBPackage.InvalidName ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }		

    try
    {
      rootPOA.the_POAManager().activate();    
    }
    catch ( java.lang.Exception ex )
    { 
      System.out.println("Internal error...");
      ex.printStackTrace();
    }
		
    System.out.println("Notification Service");
    System.out.println("Push model Server Supplier");
    System.out.println("");

    int num_suppliers;
    int num_events;

    if (args.length > 1)
    {
      num_suppliers = Integer.parseInt(args[0]);
      num_events = Integer.parseInt(args[1]);
    }
    else
    {
      num_suppliers = NUM_DEF_SUPPLIERS;
      num_events = NUM_DEF_EVENTOS;
    }

    System.out.println("Num. Suppliers: " + num_suppliers);
    System.out.println("Num. Eventos: " + num_events);
    System.out.println("");

    // Obtener la Referencia del EventChannel de un fichero
    String fileIOR = null;

    if (args.length > 2)
    {
      fileIOR = args[2];
    }
    else
    {
      System.out.println("Reading IOR from Standard Input...");
    }

    org.omg.CORBA.Object obj = null;
    try
    {
      obj = orb.string_to_object(readStringIOR(fileIOR));
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
      System.exit(0);
    }

    if ( obj == null )
    {
      System.out.println("ERROR en la Referencia al EventChannel");
      System.exit(-1);			
    }
		
    admin = org.omg.NotificationService.NotificationAdminHelper.narrow(obj);

    // Obtenemos la referencia del Channel Factory
    try
    {
      obj = admin.channel_factory();
    }
    catch ( org.omg.NotificationService.NotificationAdminPackage.CannotProceed ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }		

//    String reference = orb.object_to_string(obj);
//    System.out.println("\nReferencia al ChannelFactory:");
//    System.out.println(reference);
//    System.out.println("");

    org.omg.NotificationChannelAdmin.NotificationChannelFactory factory = org.omg.NotificationChannelAdmin.NotificationChannelFactoryHelper.narrow(obj);

    org.omg.NotificationChannelAdmin.NotificationChannel canal = null;

/*
    if ( (canal = getFirstChannel(factory)) == null)
    {
*/
      try
      {
        obj = factory.get_notification_channel( Base.CHANNEL_NAME );
        System.out.println("\nLOCATED CHANNEL Named: " + Base.CHANNEL_NAME);
      }
      catch (org.omg.NotificationChannelAdmin.ChannelNotFound ex)
      {
        try
        {
          obj = factory.new_create_notification_channel( 
                                                   Base.CHANNEL_NAME, 10, 10 );
          System.out.println("\nCREATED CHANNEL Named: " + Base.CHANNEL_NAME);
        }
        catch (org.omg.NotificationChannelAdmin.ChannelAlreadyExist exx)
        {
        }
      }
      canal = org.omg.NotificationChannelAdmin.NotificationChannelHelper.narrow(obj);
/*
      String reference = orb.object_to_string(obj);
      System.out.println("\nReferencia al Channel:");
      System.out.println(reference);
      System.out.println("");
*/

    String reference = ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(canal);
    System.out.println("\nReferencia al Channel:");
    System.out.println(reference);
    System.out.println("");

    // Obtenemos la referencia al SupplierAdmin
    SupplierAdmin supplierAdmin = createSupplierAdmin(orb, canal);

//    reference = orb.object_to_string(supplierAdmin);
//    System.out.println("\nReferencia al SupplierAdmin:");
//    System.out.println(reference);
//    System.out.println("");

    myPushSupplier suppliers[] = new myPushSupplier[num_suppliers];

    System.out.println("");
    System.out.println("CREATING PUSHSUPPLIERS");
    System.out.println("======================");

    for (int i = 0; i < num_suppliers; i++)
    {
      // Obtenemos el ProxyPullConsummer
      String name = "ProxyPushConsumer_"+i;
      System.out.println("obtain_push_consumer("+name+")");

      org.omg.NotificationChannelAdmin.ProxyPushConsumer consumer = null;

      try
      {
        consumer = supplierAdmin.obtain_named_push_consumer(name);
      }
      catch ( org.omg.NotificationChannelAdmin.ProxyAlreadyExist pae_ex)
      {
        System.out.println(" *** AlreadyExist ***");
        try
        {
          consumer = supplierAdmin.find_push_consumer(name);
        }
        catch ( org.omg.NotificationChannelAdmin.ProxyNotFound nf_ex)
        {
          System.out.println(" *** NotFound ***");
          nf_ex.printStackTrace();
          System.exit(-1);
        }
      }

//      reference = orb.object_to_string(consumer);
//      System.out.println("\nReferencia al ProxyPullConsummer:");
//      System.out.println(reference);
//      System.out.println("");

      System.out.println("New myPushSupplier: " + i);
      suppliers[i] = 
              new myPushSupplier(i, orb, consumer, num_events, false, VERBOSE);

      try
      {			
        System.out.println("connect_push_supplier()");
        consumer.connect_push_supplier( (org.omg.CosEventComm.PushSupplier) suppliers[i]._this(orb) );
        System.out.println("connected !");
      }
      catch ( java.lang.Exception ex_connect )
      {
        System.out.println("Unable to connect to consumer");
        ex_connect.printStackTrace();
        System.exit(-1);			
      }
    }
   
    System.out.println("");
    System.out.println("STARTING PUSHSUPPLIERS");
    System.out.println("======================");

    for (int i = 0; i < num_suppliers; i++)
    {
      System.out.println("Start myPushSupplier: " + i);
      Thread t = new Thread(suppliers[i]);
      t.start();
    }

    System.out.println("");
    orb.run();	

    ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
