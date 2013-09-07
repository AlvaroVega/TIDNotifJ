package es.tid.corba.TIDNotifTestClient.push_supplier_client;

//
// PushSupplier implementation
//
class myPushSupplier extends org.omg.CosEventComm.PushSupplierPOA
					 implements Runnable
{
  static int num_suppliers = 0;

  static String[] sources = 
  {
    "MOTOR", "BATERIA", "FRENOS", "SUSPENSION", "DEPOSITO",
    "ACEITE", "PLATINOS", "FILTROS", "CORREAS", "BUJIAS",
  };

  /**
   * Reference to the ORB
   */
  private String id;
  private org.omg.CORBA.ORB orb;
  private int num_events;
  private int event_type;
  private int rate;

  private boolean continuar = true;

  private boolean verbose;

  /**
   * Reference to the consumer
   */
  private org.omg.CosEventComm.PushConsumer consumer;

  /**
   * Constructor
   */ 
  public myPushSupplier( String id,
                         org.omg.CORBA.ORB orb,
                         org.omg.CosEventComm.PushConsumer consumer,
                         int num_events,
                         int event_type,
                         int rate,
                         boolean verbose )

  {
    incSuppliers();

    this.id = id;
    this.orb = orb;
    this.consumer = consumer;
    this.num_events = num_events;
    this.event_type = event_type;
    this.rate = rate;
    this.verbose = verbose;

    String ref = orb.object_to_string(this.consumer);

    System.out.print(id);
    System.out.println(ref);
    System.out.println();
  }

  /**
   * Disconnection from supplier
   */
  public void disconnect_push_supplier()
  {
    System.out.print(id);
    System.out.println("RECEIVED: discconnect_push_supplier()");
    continuar = false;
    consumer = null;
  }

  /**
   * Disconnection push consumer
   */
  public void disconnect_push_consumer()
  {
    if (consumer != null)
    {
      long d0 = 0;
      long d1;
      try
      {
        d0 = System.currentTimeMillis();
        consumer.disconnect_push_consumer();
        d1 = System.currentTimeMillis() - d0;
        System.out.print(id);
        System.out.println( "REQUEST: disconnect_push_consumer() [" + 
                                        String.valueOf(d1) + " milliseconds]");
      }
      catch ( org.omg.CORBA.NO_RESPONSE nr_ex )
      {
        d1 = System.currentTimeMillis() - d0;
        System.out.print(id);
        System.out.println("*** NO RESPONSE DISCONNECT EXCEPTION *** [" +
                                        String.valueOf(d1) + " milliseconds]");
      }
      catch (Exception ex)
      {
        d1 = System.currentTimeMillis() - d0;
        System.out.print(id);
        System.out.println("UNKNOWN EXCEPTION: disconnect_push_consumer() [" +
                                        String.valueOf(d1) + " milliseconds]");
        ex.printStackTrace();
      }
      consumer = null;
    }
    else
    {
      System.out.print(id);
      System.out.println( "NULL: disconnect_push_consumer() ");
    }
  }

  protected org.omg.CORBA.Any createSimpleEvent(String id)
  {
    org.omg.CORBA.Any any = orb.create_any();
    any.insert_string(id);
    return any;
  }

  protected org.omg.CORBA.Any createSimpleEvent(int id)
  {
    org.omg.CORBA.Any any = orb.create_any();
    any.insert_long(id);
    return any;
  }

  protected org.omg.CORBA.Any createMediumEvent(int id)
  {
    es.tid.corba.EventData.MediumAlarm alarm = new es.tid.corba.EventData.MediumAlarm();

    alarm.alarm_id = id % 10;
    alarm.source_id = sources[alarm.alarm_id];

    switch (id % 5)
    {
      case 0:
        alarm.severity = es.tid.corba.EventData.PerceivedSeverity.indeterminate;
        break;
      case 1:
        alarm.severity = es.tid.corba.EventData.PerceivedSeverity.critical;
        break;
      case 2:
        alarm.severity = es.tid.corba.EventData.PerceivedSeverity.major;
        break;
      case 3:
        alarm.severity = es.tid.corba.EventData.PerceivedSeverity.minor;
        break;
      case 4:
        alarm.severity = es.tid.corba.EventData.PerceivedSeverity.warning;
        break;
    }

    alarm.properties = new org.omg.CosLifeCycle.NVP[2];
    alarm.properties[0] = new org.omg.CosLifeCycle.NVP();
    alarm.properties[1] = new org.omg.CosLifeCycle.NVP();

    alarm.properties[0].name = new String("alarm_id");
    alarm.properties[0].value = orb.create_any();
    alarm.properties[0].value.insert_long(alarm.alarm_id);

    alarm.properties[1].name = new String("source_id");
    alarm.properties[1].value = orb.create_any();
    alarm.properties[1].value.insert_string(alarm.source_id);

    org.omg.CORBA.Any any = orb.create_any();

    es.tid.corba.EventData.MediumAlarmHelper.insert(any, alarm);
    return any;
  }

  protected org.omg.CORBA.Any createComplexEvent(int id)
  {
    // int time_stamp;
    // int cause;
    // es.tid.corba.EventData.PerceivedSeverity severity;
    // int[] actions;
    // org.omg.CosLifeCycle.NVP[] properties;
    // es.tid.corba.EventData.PossibleInfo info;
    // org.omg.CORBA.Any more_info;
    // AlarmData alarm_data;

    // enum AlarmDataType
    // {
    //   AD_Number,
    //   AD_String,
    //   AD_Enum,
    //   AD_Struct,
    //   AD_Sequence,
    //   AD_Unknown
    // };
    //
    // union AlarmData switch (AlarmDataType)
    // {
    //   case AD_String: SourceId source_id;
    //   case AD_Enum: PerceivedSeverity severity;
    //   case AD_Struct: PossibleInfo info;
    //   case AD_Sequence: ProposedRepairActions actions;
    //   case AD_Unknown: any more_info;
    //   default: long alarm_id;
    // };

    es.tid.corba.EventData.Alarm alarm = new es.tid.corba.EventData.Alarm();

    alarm.time_stamp = id;
    alarm.cause = id;
    alarm.severity = es.tid.corba.EventData.PerceivedSeverity.major;

    alarm.actions = new int[2];
    alarm.actions[0] = 17;
    alarm.actions[1] = 23;

    alarm.properties = new org.omg.CosLifeCycle.NVP[2];

    alarm.properties[0] = new org.omg.CosLifeCycle.NVP();
    alarm.properties[1] = new org.omg.CosLifeCycle.NVP();

    alarm.properties[0].name = new String("priority");
    alarm.properties[0].value = orb.create_any();
    alarm.properties[0].value.insert_long(id);

    alarm.properties[1].name = new String("severity");
    alarm.properties[1].value = orb.create_any();

    //alarm.properties[1].value.insert_string(new String("ALTA"));

    alarm.info = new es.tid.corba.EventData.PossibleInfo();
    alarm.info.source_id = new String("MADRID");
    alarm.info.due_date = id % 10;

    es.tid.corba.EventData.PossibleInfoHelper.insert(
                                        alarm.properties[1].value, alarm.info);

    es.tid.corba.EventData.PossibleInfo aux = 
                                     new es.tid.corba.EventData.PossibleInfo();

    aux.source_id = new String("MADRID");
    aux.due_date = id % 10;

    alarm.more_info = orb.create_any();
    es.tid.corba.EventData.PossibleInfoHelper.insert(
                                        alarm.more_info, aux);

    org.omg.CORBA.Any any = orb.create_any();
    alarm.alarm_data = new es.tid.corba.EventData.AlarmData();
    alarm.alarm_data.alarm_id(id);

    es.tid.corba.EventData.AlarmHelper.insert(any, alarm);
    return any;
  }


  /**
   * Entry point
   */
  public void run()
  {
    try
    {
      Thread.sleep(5000);
    }
    catch ( java.lang.Exception ex )
    {
    }

    long d1 = System.currentTimeMillis();

    long request_time = 0;
    long min_request_time = 100000;
    long max_request_time = 0;
    long total_request_time = 0;

    int num_sent_events = 0;

    int i = 0;

    while (true)
    {
      long t0, t1, t2, t3;

      t0 = System.currentTimeMillis();

      if ((num_events >= 0) && (i == num_events)) break;
  
      org.omg.CORBA.Any any = null;
      if (event_type == 0) // int
      {
        any = createSimpleEvent(i);
      }
      else if (event_type == 1) // String
      {
        any = createSimpleEvent(new String(id + "Evento num. " + i));
      }
      else if (event_type == 2)
      {
        any = createMediumEvent(i);
      }
      else if (event_type == 3) // Struct
      {
        any = createComplexEvent(i);
      }


      try
      {
        if (verbose)
        {
          synchronized(System.out)
          {
            t1 = System.currentTimeMillis();
            if (continuar)
            {
              consumer.push(any);
              num_sent_events++;
            }
            else
            {
              break;
            }
            t2 = System.currentTimeMillis();
            t3 = t2-t1;
            System.out.print(id);
            System.out.print(">> Request Time: " + t3);
            System.out.print(" [" + t1);
            System.out.print(" - " + t2);
            System.out.println("]");
          }
        }
        else
        {
          t1 = System.currentTimeMillis();
          if (continuar)
          {
            consumer.push(any);
            num_sent_events++;
          }
          else
          {
            break;
          }
          t2 = System.currentTimeMillis();
          t3 = t2-t1;
        }

        total_request_time = total_request_time + t3;

        if (t3 < min_request_time)
          min_request_time = t3;
        else if (t3 > max_request_time)
            max_request_time = t3;

        request_time = request_time + t3;

        if (rate > 0)
        {
          if ((t2-t0) < rate)
            Thread.sleep(rate - (t2-t0));
        }
      }
      catch ( org.omg.CosEventComm.Disconnected d_ex)
      {
        System.out.print(id+"*** DISCONNECTED PUSH EXCEPTION ***");
        System.out.println(id+"End of PushSupplier");
        continuar = false;
      }
      catch ( org.omg.CORBA.NO_RESPONSE nr_ex )
      {
        System.out.println(id+"*** NO RESPONSE PUSH EXCEPTION ***");
      }
      catch ( java.lang.Exception p_ex )
      {
        System.out.println(id+"*** UNKNOWN PUSH EXCEPTION ***");
        p_ex.printStackTrace();
        System.out.println(id+"End of PushSupplier");
        break;
      }
      i++;
    } // while (...)

    long d2 = System.currentTimeMillis();
    System.out.print(id);
    System.out.println("...End.  " + d2 );
    System.out.print("\n");
    System.out.print(id);
    System.out.println("Enviados " + num_sent_events + " eventos.");

    if (num_sent_events > 0)
    {
      System.out.print(id);
      System.out.println("Elapsed Time: " + ((d2-d1)/(1000.0)) + " seconds" );
      System.out.print(id);
      System.out.println("Elapsed Request Ratio: " + (num_sent_events/((d2-d1)*1.0))*1000 + " events/second" );
      System.out.print(id);
      System.out.println("Elapsed Request Time: " + ((d2-d1)/(num_sent_events*1.0)) + " milliseconds" );

      System.out.print(id);
      System.out.println("Min Request Time: "+min_request_time+" milliseconds" );
      System.out.print(id);
      System.out.println("Max Request Time: "+max_request_time+" milliseconds" );

      if (((request_time)/(num_sent_events*1.0)) < 1)
      {
        System.out.print(id);
        System.out.println("Average Request Time: " + (((request_time)/(num_sent_events*1.0))*1000.0) + " nanoseconds" );
      }
      else
      {
        System.out.print(id);
        System.out.println("Average Request Time: " + ((request_time)/(num_sent_events*1.0)) + " milliseconds" );
      }
    }

    // Ha terminado de enviar los eventos
    if (continuar)
    {
      disconnect_push_consumer();
    }
    // else
    // Ha salido por orden del canal, asi que no manda la desconexion.

    try
    {
      Thread.sleep(1000);
    }
    catch ( java.lang.Exception ex )
    {
    }

    if (decSuppliers() == 0)
    {
      orb.shutdown(true);
    }
  }

  synchronized
  static private void incSuppliers()
  {
    num_suppliers++;
  }

  synchronized
  static private int decSuppliers()
  {
    num_suppliers--;
    return num_suppliers;
  }
}
