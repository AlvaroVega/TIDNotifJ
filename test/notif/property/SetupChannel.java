//package test.property;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosNotification.ConnectionReliability;
import org.omg.CosNotification.OrderPolicy;
import org.omg.CosNotification.Persistent;
import org.omg.CosNotification.PriorityOrder;
import org.omg.CosNotification.Property;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;


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
            
            NotificationAdmin agent = 
                NotificationAdminHelper.narrow(orb.string_to_object(args[0]));
            
            EventChannelFactory factory = agent.channel_factory();
            
            if(!factory._non_existent()) {
                System.out.println("Factory exists");
            }
            
            IntHolder channel_id = new IntHolder();
            
            Property conn_persist  = new Property();
            
            conn_persist.name = ConnectionReliability.value;
            conn_persist.value = orb.create_any();
            conn_persist.value.insert_short(Persistent.value);
            
            
            Property order = new Property();
            
            order.name = OrderPolicy.value;
            order.value = orb.create_any();
            order.value.insert_short(PriorityOrder.value);
            
            Property[] qos_props = {conn_persist, order};
            
            org.omg.CosNotifyChannelAdmin.EventChannel channel = 
                factory.create_channel(qos_props, 
                                       new Property[0],
                                       channel_id);
            
            System.out.println("Channel created: " + channel_id.value);
            
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
