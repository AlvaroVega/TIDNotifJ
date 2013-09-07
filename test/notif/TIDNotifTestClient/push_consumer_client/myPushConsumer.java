package es.tid.corba.TIDNotifTestClient.push_consumer_client;

//
// PushConsumer implementation
//
class myPushConsumer extends org.omg.CosEventComm.PushConsumerPOA
{	
  static int num_consumers = 0;

  private boolean continuar;
  private int estado = -1;

  private org.omg.CORBA.ORB orb;
  private String id;

  /**
   * Reference to consommateur
   */
  private org.omg.CosEventComm.PushSupplier supplier;

  private int num_events;
  private int event_type;
  private int wait_time;

  private int num_received_events = 0;

  private Thread endThread = null;

  private boolean verbose;

  /**
  * Constructor
  */ 
  public myPushConsumer( String id,
                         org.omg.CORBA.ORB orb,
                         org.omg.CosEventComm.PushSupplier supplier,
                         int num_events,
                         int event_type,
                         int wait_time,
                         boolean verbose )
  {		
    incConsumers(id);

    this.id = id;
    this.orb = orb;
    this.supplier = supplier;
    this.num_events = num_events;
    this.event_type = event_type;
    this.wait_time = wait_time;
    this.verbose = verbose;

    continuar = true;

    String ref = orb.object_to_string(this.supplier);

    System.out.print(id);
    System.out.println(ref);
    System.out.println();
  }

  public void setEndThread(Thread endthread)
  {
    System.out.print(id);
    System.out.println("myPushConsumer.setEndThread(Thread endthread)");
    this.endThread = endThread;
  }

  /**
   * Disconnection from consumer
   */
  public void disconnect_push_consumer()
  {
    System.out.print(id);
    System.out.println("RECEIVED: discconnect_push_consumer()");
    supplier = null;		

    continuar = false;
    estado = 1;
  }

  /**
   * Disconnection from consumer
   */
  public void disconnect_push_supplier()
  {
    long d0 = 0;
    long d1;
    try
    {
      d0 = System.currentTimeMillis();
      supplier.disconnect_push_supplier();
      d1 = System.currentTimeMillis() - d0;
      System.out.print(id);
      System.out.println( "REQUEST: disconnect_push_supplier() [" + 
                                        String.valueOf(d1) + " milliseconds]");
    }
    catch (Exception ex)
    {
      d1 = System.currentTimeMillis() - d0;
      System.out.print(id);
      System.out.println( "EXCEPTION: disconnect_push_supplier() [" +
                                        String.valueOf(d1) + " milliseconds]");
    }

    if (decConsumers(id) == 0)
    {
      System.out.print(id);
      System.out.println("Ya no quedan mas clientes. Me apago.");
      orb.shutdown(true);
    }
  }

  public void push( org.omg.CORBA.Any any )
  {
    num_received_events++;
    if (verbose)
    {
      synchronized(System.out)
      {
        if (event_type == 0) // String
        {
          System.out.print(id);
          System.out.print("Evento num. "); 
          System.out.print(any.extract_long());
          System.out.print(" [");
          System.out.print(num_received_events);
          System.out.println("]");
        }
        else if (event_type == 1) // int
        {
          System.out.print(id);
          System.out.print(any.extract_string());
          System.out.print(" [");
          System.out.print(num_received_events);
          System.out.println("]");
        }
        else if (event_type == 2) // Medium
        {
          System.out.print(id);
          System.out.print("Evento medio num. "); 
          System.out.println(num_received_events);
        }
        else if (event_type == 3) // Complex
        {
          System.out.print(id);
          System.out.print("Evento complejo num. "); 
          System.out.println(num_received_events);
        }
      }
    }

    if (wait_time != 0)
    {
      try
      {
        if (verbose)
        {
          System.out.print(id);
          System.out.println("Sleep wait time: " + wait_time);
        }
        Thread.sleep(wait_time);
      }
      catch ( java.lang.Exception ex ) {}
    }

    if (num_events > 0)
    {
      if (num_received_events == num_events)
      {
        synchronized(System.out)
        {
          System.out.print(id);
          System.out.println("MAX_RECEIVED_EVENTS Reached.");

          continuar = false;
          estado = 0;
        }
      }
    }
  }

  synchronized
  static private void incConsumers(String s)
  {
    ++num_consumers;
    System.out.print(s);
    System.out.println("incConsumers(): " + num_consumers);
  }

  synchronized
  static private int decConsumers(String s)
  {
    --num_consumers;
    System.out.print(s);
    System.out.println("decConsumers(): " + num_consumers);
    return num_consumers;
  }

  synchronized
  public boolean continuar()
  {
    return continuar;
  }

  public int getEstado()
  {
    return estado;
  }

  public void finalizar()
  {
    System.out.print(id);
    System.out.println("Finalizar cliente...");

    if (decConsumers(id) == 0)
    {
      System.out.print(id);
      System.out.println("Ya no quedan mas clientes. Termina la ejecucion.");
      orb.shutdown(true);
    }
  }

}
