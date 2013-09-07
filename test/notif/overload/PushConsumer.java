/*
 * Morfeo Project
 * http://morfeo-project.org/
 * 
 * Copyright (C) 2008 Telefonica Investigacion y Desarrollo S.A. Unipersonal
 * 
 * Authors     : Alvaro Polo Valdenebro <apv at tid.es>
 *               Alvaro Vega Garcia <avega at tid dot es>
 * Module      : PushConsumer
 * Description : Main program for client side in Overload Test
 *
 * Info about members and contributors of the MORFEO project
 * is available at:
 *
 *     http://www.morfeo-project.org/TIDorbJ/CREDITS
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * If you want to use this software an plan to distribute a
 * proprietary application in any way, and you are not licensing and
 * distributing your source code under GPL, you probably need to
 * purchase a commercial license of the product.  More info about
 * licensing options is available at:
 *
 *   http://www.morfeo-project.org/TIDorbJ/Licensing
 *
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

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
import org.omg.CosNotifyChannelAdmin.ChannelNotFound;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumerHelper;
import org.omg.CosNotifyChannelAdmin.ProxyPushSupplier;
import org.omg.CosNotifyChannelAdmin.ProxyPushSupplierHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import org.omg.CORBA_2_5.ORB;


public class PushConsumer
{
    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("SetupChannel admin_url");            
            return;
        }
        
        Properties props = System.getProperties();
        props.setProperty(
                "es.tid.TIDorbj.iiop.orb_port",
                "4000");
        
        
        org.omg.CORBA_2_5.ORB orb = null;
        
        try {
            
            orb = (org.omg.CORBA_2_5.ORB) ORB.init(args, props);
            
            NotificationAdmin agent = 
                NotificationAdminHelper.narrow(orb.string_to_object(args[0]));
            
            EventChannelFactory factory = agent.channel_factory();
            
            if(!factory._non_existent()) {
                System.out.println("[PushConsumer] Factory exists");
            }
            
            //IntHolder channel_id = new IntHolder();
            EventChannel channel = null;

            try {
                channel = factory.get_event_channel(1);
                System.out.println("[PushConsumer] Got channel 1");
            } catch (ChannelNotFound e) {
                System.out.println("[PushConsumer] Channel 1 not found! ");
                return;
            }
            
                        
            //System.out.println("[PushConsumer] Get channel: " + channel_id.value);
            
            ConsumerAdmin admin = channel.default_consumer_admin();
            
            IntHolder supplier_id = new IntHolder();
            
            ProxyPushSupplier supplier = 
                ProxyPushSupplierHelper.narrow(
             admin.obtain_notification_push_supplier(ClientType.ANY_EVENT, 
                                                    supplier_id));
            
            System.out.println("[PushConsumer] Obtained ProxyPushSupplier: " + 
                               supplier_id.value);
            
            POA poa =
                POAHelper.narrow(orb.resolve_initial_references("RootPOA"));

            PushConsumerImpl consumer = new PushConsumerImpl(orb);

            org.omg.CORBA.Object raw_ref = poa.servant_to_reference(consumer);

            org.omg.CosNotifyComm.PushConsumer consumer_ref = consumer._this(orb);
            
            poa.the_POAManager().activate();
            
            supplier.connect_any_push_consumer(consumer_ref);

            // Register the push consumer as corbaloc.
            orb.register_initial_reference("PushConsumer", raw_ref);

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

}
