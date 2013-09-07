package es.tid.corba.TIDNotifTestClient.common;

import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;

public class ServiceClient
{
  protected static String readStringIOR(String filename)
  {
    String ref = null;
    try
    {
      java.io.LineNumberReader r; 
      if (filename == null)
      {
        r = new 
           java.io.LineNumberReader (new java.io.InputStreamReader(System.in));
      }
      else
      {
        r = new java.io.LineNumberReader (new java.io.FileReader(filename));
      }
      ref = r.readLine();
      r.close();
    }
    catch (java.io.IOException e1)
    {
      if (filename == null)
      {
        System.err.println("Error reading IOR from Standard Input");
      }
      else
      {
        System.err.println("Error reading IOR from file " + filename);
      }
      ref = null;
    }
    return ref;
  }
}
