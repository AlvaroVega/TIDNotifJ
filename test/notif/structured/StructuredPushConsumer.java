
//package test.structured;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.omg.CORBA.IntHolder;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosNotification._EventType;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.StructuredProxyPushSupplier;
import org.omg.CosNotifyChannelAdmin.StructuredProxyPushSupplierHelper;
import org.omg.CosNotifyFilter.ConstraintExp;
import org.omg.CosNotifyFilter.ConstraintInfo;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterFactory;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import es.tid.util.parser.TIDParser;


public class StructuredPushConsumer
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
            
            ConsumerAdmin admin; 
            admin = channel.default_consumer_admin();
            
            IntHolder supplier_id = new IntHolder();
            
            StructuredProxyPushSupplier supplier;
            supplier = StructuredProxyPushSupplierHelper.narrow(
               admin.obtain_notification_push_supplier(
               		ClientType.STRUCTURED_EVENT, 
					supplier_id
				)
			);
            
            
            
            StructuredPushConsumerImpl consumer; 
            consumer = new StructuredPushConsumerImpl(orb);
            
            org.omg.CosNotifyComm.StructuredPushConsumer consumer_ref; 
            consumer_ref = consumer._this(orb);
            
            POA poa;
            poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            poa.the_POAManager().activate();
            
            
            FilterFactory filter_factory;
            filter_factory = channel.default_filter_factory();
            
            Filter filter;
            filter = filter_factory.create_filter( TIDParser._CONSTRAINT_GRAMMAR );
            
            ConstraintExp[] constraints;
            constraints = new ConstraintExp[]{
            	new ConstraintExp(
            		new _EventType[]{
            			new _EventType( "domain_name", "type_name" )
            		},
                        "TRUE"
                        //"$type_name == 'type_name'"
            	)
            };

            filter.add_constraints( constraints );
            supplier.add_filter( filter );
            
            supplier.connect_structured_push_consumer( consumer_ref );
            
            orb.run();      
            
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
    private static void writeFile(String fileName, String ior) {
        try {
            FileWriter file = new FileWriter(fileName);
            file.write(ior);
            file.close();
        }
        catch (IOException e) {            
            e.printStackTrace();
        }
    }

}
