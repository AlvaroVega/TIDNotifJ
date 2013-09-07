package es.tid.corba.TIDNotifTestClient.push_consumer_client;

class myEndThread implements Runnable
{
  private String id;
  private myPushConsumer consumer;

  // Constructor
  public myEndThread( String id, myPushConsumer consumer )
  {
    this.id = id;
    this.consumer = consumer;
  }

  public void run()
  {
    System.out.print(id);
    System.out.println("myEndThread.run()");

    while (consumer.continuar())
    {
      try 
      {
        Thread.sleep(1000);
      }
      catch (InterruptedException e)
      {
        // the VM doesn't want us to sleep anymore,
        // so get back to work
      }
    }

    System.out.print(id);
    System.out.println("myEndThread.run(): ending.");

    try 
    {
      Thread.sleep(2000);
    }
    catch (InterruptedException e)
    {
      // the VM doesn't want us to sleep anymore,
      // so get back to work
    }

    if (consumer.getEstado() == 0)
    {
      System.out.print(id);
      System.out.println("myEndThread: consumer.disconnect_push_supplier()");
      consumer.disconnect_push_supplier();
    }
    else
    {
      System.out.print(id);
      System.out.println("myEndThread: consumer.finalizar()");
      consumer.finalizar();
    }
    consumer = null;
  }
}
