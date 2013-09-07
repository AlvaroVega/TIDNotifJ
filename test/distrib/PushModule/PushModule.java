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
 * Module: PushSupplierConsumer's
 */
import es.tid.corba.TIDNotif.ThePOAFactory;

import es.tid.corba.TIDDistribAdmin.Agent;
import es.tid.corba.TIDDistrib.ServiceManager;
import es.tid.corba.TIDDistribAdmin.AgentPackage.CannotProceed;

import org.omg.ServiceManagement.OperationMode;
import org.omg.DistributionChannelAdmin.DistributionChannel;
import org.omg.DistributionChannelAdmin.DistributionChannelHelper;
import org.omg.DistributionChannelAdmin.DistributionChannelFactory;
import org.omg.DistributionChannelAdmin.DistributionChannelFactoryHelper;

public class PushModule
{		
  static final boolean VERBOSE = false;

  static final int NUM_MAX_SUPPLIERS = 1;
  static final int NUM_MAX_CONSUMERS = 1;
  static final int NUM_MAX_EVENTS = 100;
 
  static final String CONSTRAINT_SA_1 = "$.time_stamp < 1500";

  public static org.omg.DistributionChannelAdmin.DistributionChannel getFirstChannel( es.tid.corba.TIDDistribAdmin.Agent admin )
  {
    try
    {
      DistributionChannel[] lista = admin.list_channels();

      if (lista.length == 0)
      {
        System.out.println("TIDDistribServer: !NO CHANNELS!");
      }
      else
      {
        for (int i = 0; i < lista.length; i++)
        {
          System.out.println("TIDDistribServer: !FOUND ONE CHANNEL!");
          return lista[i];
        }
    }
    }
    catch ( es.tid.corba.TIDDistribAdmin.AgentPackage.CannotProceed ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }		
    return null;
  }

  private static org.omg.CORBA.ORB orbInit( String args[] )
  {
    java.util.Properties props = new java.util.Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
    System.getProperties().put( "org.omg.CORBA.ORBSingletonClass",
                                          "es.tid.TIDorbj.core.SingletonORB" );

    // Puerto de escucha por defecto para que sea una referencia constante
    props.put("es.tid.TIDorbj.iiop.orb_port","2002");

    props.put("es.tid.TIDorbj.trace.level","0"); // 0 default
    props.put("es.tid.TIDorbj.trace.file","tidorbj.out"); // 0 default

    // Initialize the ORB
    return org.omg.CORBA.ORB.init(args, props);
  }

  public static void main( String args[] )
  {
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
		
    System.out.println("Distribution Service");
    System.out.println("Push model Server Module");
    System.out.println("");

    java.util.Properties notif_props = new java.util.Properties();
    notif_props.put("TIDNotif.trace.level", "0");
    notif_props.put("TIDNotif.trace.date", "false");

    org.omg.CORBA.Object obj = null;
    es.tid.corba.TIDDistribAdmin.Agent admin = null;

    try
    {
      System.out.println("\nInit ServiceManager channel_factory()");
      admin = ServiceManager.init(orb, args, notif_props);

      // Obtenemos la referencia del Channel Factory
      System.out.println("\nGet channel_factory()");
      obj = admin.channel_factory();
    }
    catch ( CannotProceed ex )
    {
      ex.printStackTrace();
      System.exit(1);
    }		

    System.out.println("\nNarrow channel_factory()");

    DistributionChannelFactory factory = 
                                  DistributionChannelFactoryHelper.narrow(obj);

    System.out.println("\nGet channel()");

    DistributionChannel canal = null;
    if ( (canal = getFirstChannel(admin)) == null)
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

      canal = DistributionChannelHelper.narrow(obj);
    }

    // Obtenemos la referencia al SupplierAdmin
    org.omg.CosEventChannelAdmin.SupplierAdmin supplierAdmin =
                                                         canal.for_suppliers();

    org.omg.DistributionChannelAdmin.SupplierAdmin sAdm = 
     org.omg.DistributionChannelAdmin.SupplierAdminHelper.narrow(supplierAdmin);

    org.omg.ConstraintAdmin.Discriminator sa_discriminator =
                                               sAdm.forwarding_discriminator();

/*
    int constraintId;
    try
    {
      constraintId = sa_discriminator.add_constraint(CONSTRAINT_SA_1);
      System.out.println("\nConstraintId = ");
      System.out.println(constraintId);
      System.out.println("\n");
    }
    catch (Exception e)
    {
      System.out.println("\nERROR: Invalid Constraint exception.");
      e.printStackTrace();
      return;
    }
*/

    // Obtenemos la referencia al SupplierAdmin
    org.omg.CosEventChannelAdmin.ConsumerAdmin consumerAdmin =
                                                       canal.for_consumers();
    int num_suppliers;
    int num_consumers;
    int num_events;

    if (args.length > 2)
    {
      System.out.println("Num. Suppliers: " + args[0]);
      num_suppliers = Integer.parseInt(args[0]);
      System.out.println("Num. Consumers: " + args[1]);
      num_consumers = Integer.parseInt(args[1]);
      System.out.println("Num. Events: " + args[2]);
      num_events = Integer.parseInt(args[2]);
    }
    else
    {
      System.out.println("Num. Suppliers: " + NUM_MAX_SUPPLIERS);
      num_suppliers = NUM_MAX_SUPPLIERS;
      System.out.println("Num. Consumers: " + NUM_MAX_CONSUMERS);
      num_consumers = NUM_MAX_CONSUMERS;
      System.out.println("Num. Events: " + NUM_MAX_EVENTS);
      num_events = NUM_MAX_EVENTS;
    }

    myPushSupplier suppliers[] = new myPushSupplier[num_suppliers];

    System.out.println("");
    System.out.println("CREATING PUSHSUPPLIERS");
    System.out.println("======================");

    for (int i = 0; i < num_suppliers; i++)
    {
      // Obtenemos el ProxyPullConsummer

      System.out.println("obtain_push_consumer()");
      org.omg.CosEventChannelAdmin.ProxyPushConsumer consumer =
                                          supplierAdmin.obtain_push_consumer();

      System.out.println("New myPushSupplier: " + i);
      suppliers[i] = 
               new myPushSupplier(i, orb, consumer, num_events, true, VERBOSE);

      try
      {			
        System.out.println("connect_push_supplier()");
        consumer.connect_push_supplier( 
                 (org.omg.CosEventComm.PushSupplier) suppliers[i]._this(orb) );
        System.out.println("connected !");
      }
      catch ( java.lang.Exception ex_connect )
      {
        System.out.println("Unable to connect to consumer");
        ex_connect.printStackTrace();
        System.exit(-1);			
      }
    }
   
    myPushConsumer consumers[] = new myPushConsumer[num_consumers];

    System.out.println("");
    System.out.println("CREATING MYPUSHCONSUMERS");
    System.out.println("========================");

    for (int i = 0; i < num_consumers; i++)
    {
      // Obtenemos el ProxyPushSupplier
      System.out.println("obtain_push_supplier()");
      org.omg.CosEventChannelAdmin.ProxyPushSupplier supplier =
                                          consumerAdmin.obtain_push_supplier();

      System.out.println("New myPushConsumer: " + i);
      consumers[i] =
       new myPushConsumer(i, orb, supplier, num_events*num_suppliers, VERBOSE);

      try
      {			
        System.out.println("connect_push_consumer()");
        supplier.connect_push_consumer( 
                 (org.omg.CosEventComm.PushConsumer) consumers[i]._this(orb) );
        System.out.println("connected !");
      }
      catch ( java.lang.Exception ex_connect )
      {
        System.out.println("Unable to connect to supplier");
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

