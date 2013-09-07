
//package test.any;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosNotification.*;
import org.omg.CosEventChannelAdmin.ProxyPullConsumer;
import org.omg.CosNotification.Property;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ProxyPullConsumerHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import org.omg.TimeBase.*;
import es.tid.TIDorbj.util.UTC;

public class PullSupplier
{
    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("PullSupplier admin_url");            
            return;
        }

        String proc_name = "[PullSupplier_" + System.getProperty("pid") + " ]";
        
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
            
            ProxyPullConsumer consumer = 
                ProxyPullConsumerHelper.narrow(
             admin.obtain_notification_pull_consumer(ClientType.ANY_EVENT, 
                                                     consumer_id));
            
            System.out.println(proc_name + " Obtained ProxyPullConsumer: " + consumer_id.value);

            PullSupplierImpl supplier = new PullSupplierImpl(orb, proc_name);
            
            POA poa =
                POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            
            poa.the_POAManager().activate();
            
            
            consumer.connect_pull_supplier(supplier._this(orb));
            

            orb.run(); 

            consumer.disconnect_pull_consumer();                             
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
