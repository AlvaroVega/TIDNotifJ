
//package test.any;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.lang.Thread;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosEventChannelAdmin.ProxyPushConsumer;
import org.omg.CosNotification.Property;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ProxyPullConsumerHelper;
import org.omg.CosNotifyChannelAdmin.ProxyPullSupplier;
import org.omg.CosNotifyChannelAdmin.ProxyPullSupplierHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;


public class PullConsumer
{
    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("SetupChannel admin_url");            
            return;
        }

        String proc_name = "[PullConsumer_" + System.getProperty("pid") + " ]";
        
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
            
            ConsumerAdmin admin = channel.default_consumer_admin();
            
            IntHolder supplier_id = new IntHolder();
            
            ProxyPullSupplier supplier = 
                ProxyPullSupplierHelper.narrow(
             admin.obtain_notification_pull_supplier(ClientType.ANY_EVENT, 
                                                     supplier_id));
            
            System.out.println(proc_name + " Obtained ProxyPullSupplier: " + 
                               supplier_id.value);
            
            PullConsumerImpl consumer = new PullConsumerImpl(orb);
            
            org.omg.CosNotifyComm.PullConsumer consumer_ref = consumer._this(orb);
            
            POA poa =
                POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            
            poa.the_POAManager().activate();
            
            supplier.connect_any_pull_consumer(consumer_ref);
            
            org.omg.CORBA.BooleanHolder got =
                new org.omg.CORBA.BooleanHolder();

            try {
                Thread.sleep(4000);

                boolean no_events = false;
                boolean next_event = true;
                int i = 0;
                while ( /* (i < 20 ||*/ (!no_events)) {

                    Any data = supplier.try_pull(got);
                    
                    if (got.value) {
                        System.out.println(proc_name + " received: " + 
                                           data.extract_string());
                        next_event = true;
                    }
                    else {
                        no_events = next_event;
                        next_event = false;
                    }

                    Thread.sleep(1500);
                    i++;
                }

                supplier.disconnect_pull_supplier();

                System.out.println(proc_name + " disconected and ending...: ");

            } catch( Exception e ) {
                e.printStackTrace();
            }

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
