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

import org.omg.NotificationChannelAdmin.ProxyPushSupplier;

//
// PushConsumer implementation
//
class myPushConsumer extends org.omg.CosEventComm.PushConsumerPOA
{	
  java.util.Timer timer;
  private int num_received_events;

  /**
   * Reference to consommateur
   */
  private int id;
  private org.omg.CORBA.ORB orb;
  private org.omg.NotificationChannelAdmin.ProxyPushSupplier supplier;
  private int num_eventos;
  private boolean verbose;

  private static int num_clients = 0;

  /**
   * Constructor
   */ 
  public myPushConsumer( int id,
                   org.omg.CORBA.ORB orb,
                   org.omg.NotificationChannelAdmin.ProxyPushSupplier supplier,
                   int num_eventos,
                   boolean verbose )
  {		
    incNumClients();
    this.id = id;
    this.orb = orb;
    this.supplier = supplier;
    num_received_events = 0;
    this.num_eventos = num_eventos;
    this.verbose = verbose;
  }

  /**
   * Disconnection from consumer
   */
  public void disconnect_push_consumer()
  {
    supplier.disconnect_push_supplier();
    supplier = null;		
  }

  synchronized
  public void inc_received_events()
  {
    num_received_events++;
  }

  class EndPushConsumerTask extends java.util.TimerTask 
  {
    public void run() 
    {
      disconnect_push_consumer();
      if (decNumClients() == 0)
      {
        timer.cancel();
        System.out.print("\n[c:"); System.out.print(id);
        System.out.println("] Shutdown the Orb.\n");
        ((es.tid.TIDorbj.core.TIDORB)orb).shutdown(false);
      }
      timer.cancel();
    }
  }

  public void push( org.omg.CORBA.Any any )
  {
    inc_received_events();

    if (verbose)
    {
      System.out.print("[c:"); System.out.print(id);
      System.out.println("] Received Event: " + num_received_events);
    }

    if (num_received_events == num_eventos)
    {
      System.out.print("[c:"); System.out.print(id);
      System.out.println("] MAX_RECEIVED_EVENTS Reached.");
      System.out.print("[c:"); System.out.print(id);
      System.out.println("] Consumer Client finished.\n");
      System.out.print("[c:"); System.out.print(id);
      System.out.println("]   supplier.disconnect_push_supplier()\n");

      timer = new java.util.Timer();
      timer.schedule(new EndPushConsumerTask(), 3000);

      //try
      //{
        //Thread.sleep(3000);
      //}
      //catch ( java.lang.Exception ex )
      //{
      //}
    }
  }

  synchronized
  public void incNumClients( )
  {
    ++num_clients;
  }

  synchronized
  public int decNumClients( )
  {
    --num_clients;
    return num_clients;
  }
}
