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

//
// PushSupplier implementation
//
class myPushSupplier extends org.omg.CosEventComm.PushSupplierPOA
					 implements Runnable
{

  static String[] sources =
  {
    "MOTOR", "BATERIA", "FRENOS", "SUSPENSION", "DEPOSITO",
    "ACEITE", "PLATINOS", "FILTROS", "CORREAS", "BUJIAS",
  };

	/**
	 * Reference to the ORB
	 */
	private int id;
	private org.omg.CORBA.ORB orb;
	private int num_eventos;
	private static int num_clients = 0;
        private boolean local;
        private boolean verbose;

	private int count;

	/**
	 * Reference to the consumer
	 */
        private org.omg.NotificationChannelAdmin.ProxyPushConsumer consumer;

	/**
	 * Constructor
	 */ 
	public myPushSupplier( int id, 
                   org.omg.CORBA.ORB orb, 
                   org.omg.NotificationChannelAdmin.ProxyPushConsumer consumer,
                   int num_eventos,
                   boolean local,
                   boolean verbose )
	{
          incNumClients();
          this.id = id;
          this.orb = orb;
          this.consumer = consumer;
          this.num_eventos = num_eventos;
          this.local = local;
          this.verbose = verbose;
	}

	/**
	 * Disconnection from supplier
	 */
	public void disconnect_push_supplier()
	{
		consumer.disconnect_push_consumer();

		consumer = null;
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
    alarm.info.due_date = id;

    es.tid.corba.EventData.PossibleInfoHelper.insert(
                                        alarm.properties[1].value, alarm.info);

    alarm.more_info = orb.create_any();
    alarm.more_info.insert_string(new String("Mas Informacion"));
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
//    org.omg.CORBA.Any any = orb.create_any();
    String nombre = Thread.currentThread().getName();

    long d1 = System.currentTimeMillis();

    long request_time = 0;
    long min_request_time = 100000;
    long max_request_time = 0;

    for (int i = 0; i < num_eventos; i++) 
    {
        //org.omg.CORBA.Any any = createComplexEvent(i);
        //org.omg.CORBA.Any any = createSimpleEvent(Integer.toString(i));
        org.omg.CORBA.Any any = createSimpleEvent(i);

        long t1, t2, t3;

        try
        {
          t1 = System.currentTimeMillis();
          consumer.push(any);
          t2 = System.currentTimeMillis();
          t3 = t2-t1;
          if (verbose)
          {
            System.out.print("[s:"); System.out.print(id);
            System.out.print("] >>> Request Time: " + t3);
            System.out.print(" [" + t1);
            System.out.print("-" + t2);
            System.out.println("]");
          }

          if (t3 < min_request_time)
            min_request_time = t3;
          else if (t3 > max_request_time)
            max_request_time = t3;

          request_time = request_time + t3;
        }
        catch ( java.lang.Exception ex )
        {
          System.out.print("[s:"); System.out.print(id);
          System.out.println("] End of PushSupplier\n");
          ex.printStackTrace();
          return;
        }
    } // end while(true)

    long d2 = System.currentTimeMillis();
    System.out.print("[s:"); System.out.print(id);
    System.out.println("] ...End.  " + d2 );
    System.out.print("\n[s:"); System.out.print(id);
    System.out.println("] Enviados " + num_eventos + " eventos.");
    System.out.print("[s:"); System.out.print(id);
    System.out.println("] Tiempo total: " + ((d2-d1)/(1000.0)) + " seconds" );
    System.out.print("[s:"); System.out.print(id); System.out.print("] ");
    System.out.println( (num_eventos/((d2-d1)/1000.0)) + " events/second" );
    System.out.print("[s:"); System.out.print(id);
    System.out.println("] Min Request Time: "+min_request_time+" milliseconds");
    System.out.print("[s:"); System.out.print(id);
    System.out.println("] Max Request Time: "+max_request_time+" milliseconds");
    if (((request_time)/(num_eventos*1.0)) < 1)
    {
      System.out.print("[s:"); System.out.print(id);
      System.out.println("] Average Request Time: " 
              + (((request_time)/(num_eventos*1.0))*1000.0) + " nanoseconds" );
    }
    else
    {
      System.out.print("[s:"); System.out.print(id);
      System.out.println("] Average Request Time: " + 
                        ((request_time)/(num_eventos*1.0)) + " milliseconds" );
    }

    try
    {
      Thread.sleep(1000);
    }
    catch ( java.lang.Exception ex )
    {
    }
    disconnect_push_supplier();

    if (!local && (decNumClients() == 0))
    {
      System.out.print("\n[s:"); System.out.print(id);
      System.out.println("] Shutdown the Orb.\n");
      ((es.tid.TIDorbj.core.TIDORB)orb).shutdown(false);
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
