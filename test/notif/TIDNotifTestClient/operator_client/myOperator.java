package es.tid.corba.TIDNotifTestClient.operator_client;

//
// Operator implementation
//
class myOperator extends es.tid.corba.Transformer.OperatorPOA
{	
  private String id;
  private org.omg.CORBA.ORB orb;
  private int event_type;

  /**
   * Constructor
   */ 
  public myOperator( String id,
                     org.omg.CORBA.ORB orb,
                     int event_type)
  {		
    this.id = id;
    this.orb = orb;
    this.event_type = event_type;
  }

  public org.omg.CORBA.Any do_nothing(org.omg.CORBA.Any any)
                                  throws es.tid.corba.Transformer.CannotProceed
  {
    System.out.println("Operator: do_nothing()");
    return any;
  }

  public org.omg.CORBA.Any do_something(org.omg.CORBA.Any any)
                                  throws es.tid.corba.Transformer.CannotProceed
  {
    System.out.println("Operator: do_something()");

    org.omg.CORBA.Any new_any = orb.create_any();

    try
    {
    switch (event_type)
    {
      case 0: // int
        System.out.println("  event_type: 0");
        try
        {
          new_any.insert_long(any.extract_long());
        }
        catch (Exception ex)
        {
         throw new es.tid.corba.Transformer.CannotProceed("ERROR in Data Type");
        }
        break;
      case 1:
        System.out.println("  event_type: 1");
        try
        {
          new_any.insert_string(any.extract_string());
        }
        catch (Exception ex)
        {
         throw new es.tid.corba.Transformer.CannotProceed("ERROR in Data Type");
        }
        break;
      case 2:
        System.out.println("  event_type: 2");
        try
        {
          System.out.println("  * exteract()");
          es.tid.corba.EventData.MediumAlarm alarm = 
                         es.tid.corba.EventData.MediumAlarmHelper.extract(any);
          System.out.println("  * insert()");
          es.tid.corba.EventData.MediumAlarmHelper.insert(new_any, alarm);
        }
        catch (Exception ex)
        {
         throw new es.tid.corba.Transformer.CannotProceed("ERROR in Data Type");
        }
        break;
      case 3:
        System.out.println("  event_type: 3");
        try
        {
          es.tid.corba.EventData.Alarm alarm =
                               es.tid.corba.EventData.AlarmHelper.extract(any);
          es.tid.corba.EventData.AlarmHelper.insert(new_any, alarm);
        }
        catch (Exception ex)
        {
         throw new es.tid.corba.Transformer.CannotProceed("ERROR in Data Type");
        }
        break;
      default:
        System.out.println("  event_type: ?");
          throw new es.tid.corba.Transformer.CannotProceed("Invalid Data Type");
    }
    }
    catch (Exception ex)
    { 
      ex.printStackTrace();
    }
    return new_any;
  }

  public void shutdown()
  {
    orb.shutdown(true);
  }
}
