
//package test.any;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosNotification.*;
import org.omg.CosEventChannelAdmin.ProxyPushConsumer;
import org.omg.CosNotification.Property;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumerHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import org.omg.TimeBase.*;
import es.tid.TIDorbj.util.UTC;

public class PushSupplier
{
    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("PushSupplier admin_url");            
            return;
        }

        String proc_name = "[PushSupplier_" + System.getProperty("pid") + " ]";
        
        Properties props = new Properties();
        
        props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
        System.getProperties().put( "org.omg.CORBA.ORBSingletonClass",
                                    "es.tid.TIDorbj.core.SingletonORB" );
        
        ORB orb = null;
        
        try {
            
            orb = (ORB) ORB.init(args, props);
            
            NotificationAdmin agent = 
                NotificationAdminHelper.narrow(orb.string_to_object(args[0]));
            
            EventChannelFactory factory = agent.channel_factory();
            
            if(!factory._non_existent()) {
                System.out.println(proc_name + " Factory exists");
            }
            
            org.omg.CosNotifyChannelAdmin.EventChannel channel = 
                factory.get_event_channel(1);
                        
            System.out.println(proc_name + " Got channel 1");
            
            SupplierAdmin admin = channel.default_supplier_admin();
            
            IntHolder consumer_id = new IntHolder();
            
            ProxyPushConsumer consumer = 
                ProxyPushConsumerHelper.narrow(
             admin.obtain_notification_push_consumer(ClientType.ANY_EVENT, 
                                                     consumer_id));
            
            System.out.println(proc_name + " Obtained ProxyPushConsumer: " + consumer_id.value);

            PushSupplierImpl supplier = new PushSupplierImpl(orb);
            
            POA poa =
                POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            
            poa.the_POAManager().activate();
            
            
            consumer.connect_push_supplier(supplier._this(orb));
            



            //
            // Group 1: all are delivered OK
            // 


            for(int i = 0; i < 10; i++) {
                Any msg = orb.create_any();  
                msg.insert_string("Hello " + i);
                System.out.println("Message sent " + i + " group 1");
                consumer.push(msg);
                Thread.sleep(1000);
            }

            System.out.println(" ");
            Thread.sleep(10000);


            //
            // Group 2: delivered in 5000 ms
            // 
            // Change QoS 
            UtcT start_utc = new UtcT(UTC.toTimeT(UTC.currentUtcTimeMillis()), 
                                      (int)0, (short)0, (short)0);
            start_utc.time += (5 * 10000000); // now + 5 '' -> OK

            Any start_any = orb.create_any();
            UtcTHelper.insert(start_any, start_utc);       // UtcT

            Property[] new_qos = new Property[1];
            new_qos[0] = new Property(StartTime.value, start_any);            

            channel.set_qos(new_qos);

            for(int i = 10; i < 20; i++) {
                Any msg = orb.create_any();  
                msg.insert_string("Hello " + i);
                System.out.println("Message sent " + i + " group 2");
                consumer.push(msg);
                Thread.sleep(1000);
            }

            System.out.println(" ");
            Thread.sleep(10000);

//             //
//             // Group 3: none are delivered: 
//             // 
//             // Change queue size and check tidnotif_orb.log:
//             // Some events are discarding and TRANSIENT exception is raised to supplier
//            msg.insert_string("Hello 3");
//             start_utc = new UtcT(UTC.toTimeT(UTC.currentUtcTimeMillis()), 
//                                       (int)0, (short)0, (short)0);
//             start_utc.time += (5 * 10000000); // now + 5 '' -> OK

//             stop_utc  = new UtcT(UTC.toTimeT(UTC.currentUtcTimeMillis()), 
//                                       (int)0, (short)0, (short)0);
//             stop_utc.time += (7 * 10000000); // now + 7 '' -> OK
//             Any start_any2 = orb.create_any();
//             Any stop_any2 = orb.create_any();
//             UtcTHelper.insert(start_any2, start_utc);       // UtcT
//             UtcTHelper.insert(stop_any2, stop_utc);         // UtcT
//             Property[] new_qos2 = new Property[2];
//             new_qos2[0] = new Property(StartTime.value, start_any2);            
//             new_qos2[1] = new Property(StopTime.value, stop_any2);
//             channel.set_qos(new_qos2);
//             for(int i = 0; i < 10; i++) {
//                 System.out.println("Message sent " + i + " group 4");
//                 try {
//                     consumer.push(msg);
//                 }  catch (org.omg.CORBA.TRANSIENT ex) {
//                     System.out.println("TRANSIENT exception in supplier ");
//                 }
//             }



//             //
//             // Group 4: none are delivered: 
//             // 
//            msg.insert_string("Hello 4");
//             Any queue_any = orb.create_any();
//             queue_any.insert_long(2);

//             Property[] new_admin = new Property[1];
//             new_admin[0] = new Property(MaxQueueLength.value, queue_any);
//             channel.set_admin(new_admin);

//             for(int i = 0; i < 10; i++) {
//                 System.out.println("Message sent " + i + " group 4");
//                 try {
//                     consumer.push(msg);
//                 }  catch (org.omg.CORBA.TRANSIENT ex) {
//                     System.out.println("TRANSIENT exception in supplier ");
//                 }
//             }






           consumer.disconnect_push_consumer();                             
           System.out.println(proc_name + " disconected and ending...: ");
            
        }  catch (Throwable th) {
            th.printStackTrace();            
        } finally {
            if (orb != null) {
                orb.destroy();
            }
        }
        System.exit(0);

    }

    /**
     * @param string
     * @param channel
     */
    private static void writeFile(String fileName, String ior)
    {
        try {
            FileWriter file = new FileWriter(fileName);
            file.write(ior);
            file.close();
        }
        catch (IOException e) {            
            e.printStackTrace();
        }
        
        
        
        
    }

    /**
     * @param object
     * @return
     */
    private static NotificationAdmin NotificationAdminHelper(Object object)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
