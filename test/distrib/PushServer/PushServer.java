//////////////////////////////////////////////////////////////////////////
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

package es.tid.corba.TIDDistrib;

/**
 * Server: PushSupplier's
 */

import es.tid.corba.TIDNotif.ThePOAFactory;
import org.omg.NotificationChannelAdmin.SupplierAdmin;

import org.omg.ServiceManagement.OperationMode;

public class PushServer extends Base
{		
  static final boolean VERBOSE = false;
  static final int NUM_DEF_SUPPLIERS = 1;
  static final int NUM_DEF_EVENTOS = 100;

  public static org.omg.DistributionChannelAdmin.DistributionChannel 
    getFirstChannel( 
          org.omg.DistributionChannelAdmin.DistributionChannelFactory factory )
  {
    org.omg.DistributionChannelAdmin.DistributionChannel[] lista =
                                               factory.distribution_channels();
    if (lista.length == 0)
    {
      System.out.println("TIDDistribServer: !NO CHANNELS!");
    }
    else
    {
      for (int i = 0; i < lista.length; i++)
      {
        System.out.println("TIDDistrib: !FOUND ONE CHANNEL!");
        return lista[i];
      }
    }
   return null;
  }

  private static SupplierAdmin createSupplierAdmin( org.omg.CORBA.ORB orb,
                   org.omg.NotificationChannelAdmin.NotificationChannel canal )
  {
    String idCriteria = "CONSUMER_ADMIN_EVENT_PROCESSOR";

    org.omg.CosLifeCycle.NVP[] criteria = new org.omg.CosLifeCycle.NVP[2];

    org.omg.CORBA.Any value = orb.create_any();
    value.insert_string(idCriteria);

    criteria[0] = new org.omg.CosLifeCycle.NVP("Id", value);

    value = orb.create_any();
    value.insert_string("XMLInputAdapter");
    criteria[1] = new org.omg.CosLifeCycle.NVP("Entrada", value);

    System.out.println( "Obtaining suppliers with criteria Id = " +
                                          criteria[0].value.extract_string() );
    System.out.println( "Obtaining suppliers with criteria Entrada = " +
                                          criteria[1].value.extract_string() );

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
        supplierAdmin = canal.new_for_suppliers(criteria);
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

/*
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
*/

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
    es.tid.corba.TIDDistribAdmin.Agent admin = null;

    org.omg.CORBA.ORB orb = orbInit(args);

    org.omg.PortableServer.POA rootPOA = null;
    try
    {
      rootPOA = org.omg.PortableServer.POAHelper.narrow(
                                    orb.resolve_initial_references("RootPOA"));		
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
		
    System.out.println("Distribution Service");
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
		
    admin = es.tid.corba.TIDDistribAdmin.AgentHelper.narrow(obj);

    // Obtenemos la referencia del Channel Factory
    try
    {
      obj = admin.channel_factory();
    }
    catch ( es.tid.corba.TIDDistribAdmin.AgentPackage.CannotProceed ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }		

//    String reference = orb.object_to_string(obj);
//    System.out.println("\nReferencia al ChannelFactory:");
//    System.out.println(reference);
//    System.out.println("");

    org.omg.DistributionChannelAdmin.DistributionChannelFactory factory = org.omg.DistributionChannelAdmin.DistributionChannelFactoryHelper.narrow(obj);

    org.omg.DistributionChannelAdmin.DistributionChannel canal = null;

    if ( (canal = getFirstChannel(factory)) == null)
    {
      try
      {
        obj = factory.create_distribution_channel(
                                          10, 10, OperationMode.distribution );
      }
      catch (org.omg.DistributionChannelAdmin.InvalidOperationMode ex)
      {
        System.out.println("\nERROR: Invalid OperationMode exception.");
        ex.printStackTrace();
        System.exit(-1);			
      }
      canal = org.omg.DistributionChannelAdmin.DistributionChannelHelper.narrow(obj);
//      reference = orb.object_to_string(obj);
//      System.out.println("\nReferencia al Channel:");
//      System.out.println(reference);
//      System.out.println("");
      }
//    else
//    {
//      reference = orb.object_to_string(canal);
//      System.out.println("\nReferencia al Channel:");
//      System.out.println(reference);
//      System.out.println("");
//    }

    // Obtenemos la referencia al SupplierAdmin
    org.omg.NotificationChannelAdmin.SupplierAdmin supplierAdmin =
                                             createSupplierAdmin(orb, canal);
//                                                       canal.for_suppliers());

//    reference = orb.object_to_string(supplierAdmin);
//    System.out.println("\nReferencia al SupplierAdmin:");
//    System.out.println(reference);
//    System.out.println("");

    org.omg.DistributionChannelAdmin.SupplierAdmin supplierAdmin2 =
    org.omg.DistributionChannelAdmin.SupplierAdminHelper.narrow(supplierAdmin);

//***************************************************************************
    org.omg.CosLifeCycle.NVP[] criteria = null;
    try
    {
//      System.out.println("\nsupplierAdmin2.associated_criteria()");
      criteria = supplierAdmin2.associated_criteria();
//      System.out.println("\nOK");
    }
    catch ( java.lang.Exception ex_connect )
    {
      System.out.println("Unable to get associated_criteria");
      ex_connect.printStackTrace();
      System.exit(-1);
    }

    System.out.println("\nRecorrido del associated_criteria");
    if (criteria != null)
    {
      for (int i = 0; i < criteria.length; i++)
      {
        System.out.println( "Criteria Name: " + criteria[i].name );
//System.out.println("Criteria Value: " + criteria[i].value.extract_string());
//System.out.println("Criteria Value: " + criteria[i].value.extract_boolean());
      }
    }
    else
    {
      System.out.println( "Criteria is NULL");
    }
//***************************************************************************

    criteria = null;
    try
    {
      criteria = supplierAdmin2.extended_criteria();
    }
    catch ( java.lang.Exception ex_connect )
    {
      System.out.println("Unable to get extended_criteria");
      ex_connect.printStackTrace();
      System.exit(-1);
    }

    System.out.println("\nRecorrido del extended_criteria");
    if (criteria != null)
    {
      for (int i = 0; i < criteria.length; i++)
      {
        System.out.println( "Criteria Name: " + criteria[i].name );
//System.out.println("Criteria Value: " + criteria[i].value.extract_string());
//System.out.println("Criteria Value: " + criteria[i].value.extract_boolean());
      }
    }
    else
    {
      System.out.println( "Criteria is NULL");
    }

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
