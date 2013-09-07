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
 * A client example in Push model
 */

import es.tid.corba.TIDNotif.ThePOAFactory;
import org.omg.NotificationChannelAdmin.ConsumerAdmin;

import org.omg.ServiceManagement.OperationMode;

public class PushClient extends Base
{		
  static final boolean VERBOSE = false;
  static final int NUM_DEF_CONSUMERS = 1;
  final static private int DEF_RECEIVED_EVENTS = 100;

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
        System.out.println("TIDDistribServer: !FOUND ONE CHANNEL!");
        return lista[i];
      }
    }
   return null;
  }

  private static ConsumerAdmin createConsumerAdmin( org.omg.CORBA.ORB orb,
                   org.omg.NotificationChannelAdmin.NotificationChannel canal )
  {
    String idCriteria = "CONSUMER_ADMIN_EVENT_PROCESSOR";

    org.omg.CosLifeCycle.NVP[] criteria = new org.omg.CosLifeCycle.NVP[2];

    org.omg.CORBA.Any value = orb.create_any();
    value.insert_string(idCriteria);

    criteria[0] = new org.omg.CosLifeCycle.NVP("Id", value);

    value = orb.create_any();
    value.insert_string("IDL:tid.es/Ejemplo:1.0");
    criteria[1] = new org.omg.CosLifeCycle.NVP("RepositoryId", value);

    System.out.println( "Obtaining consumers with criteria Id = " + 
                                          criteria[0].value.extract_string() );
    System.out.println( "Obtaining consumers with criteria RepositoryId = " + 
                                          criteria[1].value.extract_string() );
    
    // Si no existe ninguno, se crea uno, sino se coge el primero
    ConsumerAdmin consumerAdmin=null;

    try
    {
      boolean found = true;
      try
      {
        // Primero obtener los Consumer Admin
        ConsumerAdmin[] consumerAdmins = canal.find_for_consumers(criteria);
        System.out.println( "ConsumerAdmin found. Using it." );
        consumerAdmin = consumerAdmins[0];
      }
      catch ( org.omg.NotificationChannelAdmin.CannotMeetCriteria ex)
      {
        System.out.println( "ConsumerAdmin not found. Creating one" );
        consumerAdmin = canal.new_for_consumers(criteria);
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
        // Primero obtener los Consumer Admin
        ConsumerAdmin[] consumerAdmins = canal.find_for_consumers(criteria);
        System.out.println( "ConsumerAdmin found. Using it." );
        consumerAdmin = consumerAdmins[0];
      }
      catch ( org.omg.NotificationChannelAdmin.InvalidCriteria ex)
      {
        System.out.println( "ConsumerAdmin not found. Creating one" );
        System.exit(1);
      }
      catch ( org.omg.NotificationChannelAdmin.CannotMeetCriteria ex)
      {
        System.out.println( "ConsumerAdmin not found. Exit" );
        System.exit(1);
      }
*/

    return consumerAdmin; 
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
                                   orb.resolve_initial_references("RootPOA") );
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
    System.out.println("Push model Client Consumer");
    System.out.println("");

    int num_consumers;
    int num_events;

    if (args.length > 1)
    {
      num_consumers = Integer.parseInt(args[0]);
      num_events = Integer.parseInt(args[1]);
    }
    else
    {
      num_consumers = NUM_DEF_CONSUMERS;
      num_events = DEF_RECEIVED_EVENTS;
    }
    System.out.println("Num. Consummers: " + num_consumers);
    System.out.println("Num. Events: " + num_events);
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
      System.err.println("ERROR en la Referencia al EventChannel");
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
    System.out.println("");

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
    else
    {
//      reference = orb.object_to_string(canal);
//      System.out.println("\nReferencia al Channel:");
//      System.out.println(reference);
//      System.out.println("");
    }

    org.omg.NotificationChannelAdmin.ConsumerAdmin consumerAdmin =
                                             createConsumerAdmin(orb, canal);
//                                                        canal.for_consumers();

//    reference = orb.object_to_string(consumerAdmin);
//    System.out.println("\nReferencia al ConsumerAdmin:");
//    System.out.println(reference);
//    System.out.println("");

//    System.out.println("\nNarrow del Distribution ConsumerAdmin");
    org.omg.DistributionChannelAdmin.ConsumerAdmin consumerAdmin2 =
    org.omg.DistributionChannelAdmin.ConsumerAdminHelper.narrow(consumerAdmin);
//    System.out.println("\nOK");

//***************************************************************************
    org.omg.CosLifeCycle.NVP[] criteria = null;
    try
    {
//      System.out.println("\nconsumerAdmin2.associated_criteria()");
      criteria = consumerAdmin2.associated_criteria();
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
      criteria = consumerAdmin2.extended_criteria();
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

    myPushConsumer consumers[] = new myPushConsumer[num_consumers];

    System.out.println("");
    System.out.println("CREATING PUSHCONSUMERS");
    System.out.println("======================");

    for (int i = 0; i < num_consumers; i++)
    {
      // Obtenemos el ProxyPushSupplier
      String name = "ProxyPushSupplier_"+i;
      System.out.println("obtain_named_push_supplier("+name+")");

      org.omg.NotificationChannelAdmin.ProxyPushSupplier supplier = null;
      try
      {
        supplier = consumerAdmin.obtain_named_push_supplier(name);
      }
      catch ( org.omg.NotificationChannelAdmin.ProxyAlreadyExist pae_ex)
      {
        System.out.println(" *** AlreadyExist ***");
        try
        {
          supplier = consumerAdmin.find_push_supplier(name);
        }
        catch ( org.omg.NotificationChannelAdmin.ProxyNotFound nf_ex)
        {
          System.out.println(" *** NotFound ***");
          nf_ex.printStackTrace();
          System.exit(-1);
        }
      }
      //org.omg.CosEventChannelAdmin.ProxyPushSupplier supplier = 
               //org.omg.CosEventChannelAdmin.ProxyPushSupplierHelper.narrow(s);

      System.out.println("New myPushConsumer: " + i);
      consumers[i] = new myPushConsumer(i, orb, supplier, num_events, VERBOSE);

      try
      {			
        System.out.println("connect_push_consumer()");
        supplier.connect_push_consumer( (org.omg.CosEventComm.PushConsumer) consumers[i]._this(orb) );
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
    orb.run();		

    // the end 
    ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
