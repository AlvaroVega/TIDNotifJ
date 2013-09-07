/*
 * Morfeo Project
 * http://morfeo-project.org/
 * 
 * Copyright (C) 2008 Telefonica Investigacion y Desarrollo S.A. Unipersonal
 * 
 * Authors     : Alvaro Polo Valdenebro <apv at tid.es>
 *               Alvaro Vega Garcia <avega at tid dot es>
 * Module      : PullConsumer
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
import org.omg.CosEventChannelAdmin.ProxyPullConsumer;
import org.omg.CosNotification.Property;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ChannelNotFound;
import org.omg.CosNotifyChannelAdmin.ProxyPullConsumerHelper;
import org.omg.CosNotifyChannelAdmin.ProxyPullSupplier;
import org.omg.CosNotifyChannelAdmin.ProxyPullSupplierHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import Overload.*;

public class PullConsumer
{

    public static void main(String[] args) {
        
        if(args.length < 1) {
            System.err.println("SetupChannel admin_url");            
            return;
        }
        
        Properties props = System.getProperties();
        
        ORB orb = null;

        try {
            
            orb = (ORB) ORB.init(args, props);
            
            NotificationAdmin agent = 
                NotificationAdminHelper.narrow(orb.string_to_object(args[0]));
            
            EventChannelFactory factory = agent.channel_factory();
            
            if(!factory._non_existent()) {
                System.out.println("[PullConsumer] Factory exists");
            }
            
            org.omg.CosNotifyChannelAdmin.EventChannel channel = null;
            try {
                channel = factory.get_event_channel(1);
                System.out.println("[PullConsumer] Got channel 1");
            } catch (ChannelNotFound e) {
                System.out.println("[PullConsumer] Channel 1 not found! ");
                return;
            }
                        
            //System.out.println("[PullConsumer] Get channel: " + channel_id.value);
            
            ConsumerAdmin admin = channel.default_consumer_admin();
            
            IntHolder supplier_id = new IntHolder();
            
            ProxyPullSupplier supplier = 
                ProxyPullSupplierHelper.narrow(
             admin.obtain_notification_pull_supplier(ClientType.ANY_EVENT, 
                                                     supplier_id));
            
            System.out.println("[PullConsumer] Obtained ProxyPullSupplier: " + 
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
                Thread.sleep(3000);

                for( int i = 0; i < 1000; i++ ) {

                    Any data = supplier.try_pull(got);
                    
                    if (got.value) {
                        Event ev = new Event();
                        ev = EventHelper.extract(data);
                        
                        // Check if is the last message
                        // then call to print statical numbers and disconnect and destroy
                        if (ev.bool_val) {
                            System.out.println("[PullConsumer] Received last message");
                            // TODO
                            System.out.println("[PullConsumer] Processed requests: " + i);
                            
                        } else {
                            //request();
                            //compute(my_struct);
                        }
                        
                    }
                    Thread.sleep(100);
                }

            } catch( Exception e ) {
                e.printStackTrace();
            }

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
