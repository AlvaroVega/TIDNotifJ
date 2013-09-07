//package test.property;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosNotification.Priority;
import org.omg.CosNotification.PriorityOrder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumer;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumerHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


public class PushSupplier
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
            
            org.omg.CosNotifyChannelAdmin.EventChannel channel = 
                factory.get_event_channel(1);
            
                        
            System.out.println("Got channel 1");
            
            SupplierAdmin admin = channel.default_supplier_admin();
            
            IntHolder supplier_id = new IntHolder();
            
            ProxyPushConsumer consumer = 
                ProxyPushConsumerHelper.narrow(
             admin.obtain_notification_push_consumer(ClientType.ANY_EVENT, 
                                                    supplier_id));
            
            PushSupplierImpl supplier = new PushSupplierImpl(orb);
            
            POA poa =
                POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            
            poa.the_POAManager().activate();
            
            Property order = new Property();            
            order.name = Priority.value;
            order.value = orb.create_any();
            order.value.insert_short(PriorityOrder.value);
            
            Property[] qos_props = {order};
            
            try {
                consumer.set_qos(qos_props);
            } catch (UnsupportedQoS us) {
                System.err.println("Property not supported");   
            }
            
            consumer.connect_push_supplier(supplier._this(orb));
                       
            
            Any msg = orb.create_any();
            
            msg.insert_string("Hello");
            
            for(int i = 0; i < 1000; i++) {
                
               System.out.println("Message sent");
               consumer.push(msg);
               Thread.sleep(3000);
                
            }
            
           consumer.disconnect_push_consumer();                             
            
            
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
