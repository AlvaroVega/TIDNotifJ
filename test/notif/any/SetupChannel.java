
//package test.any;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosNotification.*;
import org.omg.TimeBase.*;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.EventChannelFactoryHelper;
import org.omg.CosNotifyChannelAdmin.ChannelNotFound;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;

import es.tid.TIDorbj.util.UTC;

public class SetupChannel
{
    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("SetupChannel admin_url");            
            return;
        }
        
        Properties props = new Properties();
        
        props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
        System.getProperties().put( "org.omg.CORBA.ORBSingletonClass",
                                    "es.tid.TIDorbj.core.SingletonORB" );
        
        ORB orb = null;
        
        try {
            
            orb = (ORB) ORB.init(args, props);
            
//             NotificationAdmin agent = 
//                 NotificationAdminHelper.narrow(orb.string_to_object(args[0]));
            
//             EventChannelFactory factory = agent.channel_factory();

			// Another way to obtain factory if we know host and port where Notifcation Service is listening
            String corbaloc = "corbaloc:iiop:1.2@127.0.0.1:2002/NotificationService";
            EventChannelFactory factory = 
                EventChannelFactoryHelper.narrow(orb.string_to_object(corbaloc));

            if(!factory._non_existent()) {
                System.out.println("[SetupChannel] Factory exists");
            }

            //
            // QoS supported policies:
            //

            Any order_any = orb.create_any();
            Any event_any = orb.create_any();
            Any connection_any = orb.create_any();
            Any priority_any = orb.create_any();
            Any start_any = orb.create_any();
            Any stop_any = orb.create_any();
            Any timeout_any = orb.create_any();
            Any start_supported_any = orb.create_any();
            Any stop_supported_any = orb.create_any();

            UtcT start_utc = new UtcT(UTC.toTimeT(UTC.currentUtcTimeMillis()), 
                                      (int)0, (short)0, (short)0);
            start_utc.time += (5 * 10000000); // now + 5 '' -> OK

            UtcT stop_utc  = new UtcT(UTC.toTimeT(UTC.currentUtcTimeMillis()), 
                                      (int)0, (short)0, (short)0);
            stop_utc.time +=  (7 * 10000000); // now + 7 '' -> OK
            
            UtcT timeout_utc = new UtcT(UTC.toTimeT(UTC.currentUtcTimeMillis()), 
                                        (int)0, (short)0, (short)0);
            timeout_utc.time += (10 * 10000000); // now + 10 '' -> OK
    

            order_any.insert_short(FifoOrder.value);  // { Any Fifo Priority Deadline }
            event_any.insert_short(Persistent.value); // { BestEffort, Persistent }
            connection_any.insert_short(Persistent.value);  // { BestEffort, Persistent }
            priority_any.insert_short(HighestPriority.value); // { Highest, Default }
            UtcTHelper.insert(start_any, start_utc);       // UtcT
            UtcTHelper.insert(stop_any, stop_utc);         // UtcT
            TimeTHelper.insert(timeout_any, timeout_utc.time);  // TimeT
            start_supported_any.insert_boolean(true); // { true, false }
            stop_supported_any.insert_boolean(true);  // { true, false }

            Property[] initial_qos = new Property[1];
            //initial_qos[] = new Property(OrderPolicy.value, order_any);
            //initial_qos[] = new Property(EventReliability.value, event_any);  
            //initial_qos[] = new Property(ConnectionReliability.value, connection_any);
            //initial_qos[] = new Property(Priority.value, priority_any);            
            initial_qos[0] = new Property(StartTime.value, start_any);            
//             initial_qos[1] = new Property(StopTime.value, stop_any);
            //initial_qos[] = new Property(Timeout.value, timeout_any);            
            //initial_qos[] = new Property(StartTimeSupported.value, start_supported_any);
            //initial_qos[] = new Property(StopTimeSupported.value, stop_supported_any);

            Any queue_any = orb.create_any();
            Any max_consumers_any = orb.create_any();
            Any max_suppliers_any = orb.create_any();
            queue_any.insert_long(1000);
            max_consumers_any.insert_long(10);
            max_suppliers_any.insert_long(10);
            

            // AdminProperties are not supported by TIDNotif 2.0.1
            // This behaviours could be defined in start TIDNotif script

            Property[] initial_admin = new Property[3];
            initial_admin[0] = new Property(MaxQueueLength.value, queue_any);
            initial_admin[1] = new Property(MaxConsumers.value, max_consumers_any);
            initial_admin[2] = new Property(MaxSuppliers.value, max_suppliers_any);
         

            IntHolder channel_id = new IntHolder();

            org.omg.CosNotifyChannelAdmin.EventChannel channel = null;

            try {
                channel = factory.get_event_channel(1);
                channel.set_qos(initial_qos);
                System.out.println("[SetupChannel] Got channel 1");
            } catch (ChannelNotFound e){
 
                factory.create_channel(initial_qos, new Property[0], channel_id);
                System.out.println("[SetupChannel] Channel created: " + channel_id.value);
            }

            writeFile("channel.ior", orb.object_to_string(channel)); 
            
        }  catch (Throwable th) {
            th.printStackTrace();            
        } finally {
            if (orb != null) {
                orb.destroy();
            }
        }
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
