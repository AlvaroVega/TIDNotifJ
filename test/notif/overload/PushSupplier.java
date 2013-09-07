/*
 * Morfeo Project
 * http://morfeo-project.org/
 * 
 * Copyright (C) 2008 Telefonica Investigacion y Desarrollo S.A. Unipersonal
 * 
 * Authors     : Alvaro Polo Valdenebro <apv at tid.es>
 *               Alvaro Vega Garcia <avega at tid dot es>
 * Module      : Supllier
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
import java.io.PrintStream;

import java.text.NumberFormat;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosEventChannelAdmin.ProxyPushConsumer;
import org.omg.CosNotification.*;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ChannelNotFound;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumerHelper;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;
import org.omg.CosNotifyChannelAdmin.ProxyConsumer;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import Overload.*;

public class PushSupplier {


    public static void set_max_queued_length(EventChannel channel, ORB orb, int value) {
        Any queue_any = orb.create_any();
        queue_any.insert_long(value);
        Property[] new_admin = new Property[1];
        new_admin[0] = new Property(MaxQueueLength.value, queue_any);
        try {
            channel.set_admin(new_admin);
        } catch (org.omg.CosNotification.UnsupportedAdmin ua) {
            System.out.println("[Supplier] UnssuportedAdmin property.");
        } catch (org.omg.CORBA.BAD_PARAM ex) {
            System.out.println("[Supplier] BAD_PARAM " + 
                               " Make sure that you are using SHADOWS branch of TIDorbJ");
        }

    }

    
    public static void main(String[] args) {
        
        /***
         *  Some useful aliases
         */
        PrintStream err = System.err;
        PrintStream out = System.out;
        NumberFormat num_for = NumberFormat.getInstance();


        if(args.length < 1) {
            System.err.println("PushSupplier admin_url");            
            return;
        }
        
        Properties props = System.getProperties();
        
        ORB m_orb = null;
        
        try {
            
            m_orb = (ORB) ORB.init(args, props);
            
            NotificationAdmin agent = 
                NotificationAdminHelper.narrow(m_orb.string_to_object(args[0]));
            
            EventChannelFactory factory = agent.channel_factory();
            
            if(!factory._non_existent()) {
                out.println("[Supplier] Factory exists");
            }
            
            EventChannel channel = null;

            try {
                channel = factory.get_event_channel(1);
                out.println("[Supplier] Got channel 1");
            } catch (ChannelNotFound e) {
                out.println("[Supplier] Channel 1 not found! ");
                return;
            }
            
            POA poa =
                POAHelper.narrow(m_orb.resolve_initial_references("RootPOA"));
            
            poa.the_POAManager().activate();


            


            /***
             * Creates SupplierThreads 
             */

            int threads_number = 30;
            int requests_number = 15000;
            boolean req_apply_thread = false;

            SupplierThread [] threads = new SupplierThread[threads_number];

            for (int i = 0; i < threads_number; i++) {
                if (req_apply_thread)  {  
                    threads[i] = new SupplierThread(i, m_orb, channel, requests_number);
                }
                else {
                    if (requests_number % threads_number == 0) {
                        threads[i] = new SupplierThread(i, m_orb, channel, 
                                                        requests_number / threads_number);
                    }
                    else {
                        if (i != threads_number - 1)
                            threads[i] = new SupplierThread(i, m_orb, channel, 
                                                            requests_number / (threads_number - 1));
                        else
                            threads[i] = new SupplierThread(i, m_orb, channel, 
                                                            requests_number % (threads_number - 1));
                    }
                }
            }
      
            /***
             * Start the SupplierThreads 
             * 
             */
            out.println("[Supplier] Starting SupplierThreads...");
            long start_time = System.currentTimeMillis();
            for (int i = 0; i < threads_number; i++)
                threads[i].start();
            

            /**
             * Wait a sort time: requests have been discarding
             */
            // Thread.sleep(1000); // sleep 1 seconds
            out.println("[Supplier] Changing MaxQueueLength to 4...");
            set_max_queued_length(channel, m_orb, 4);

            Thread.sleep(20000); // sleep 20 seconds

            out.println("[Supplier] Changing MaxQueueLength to 5...");
            set_max_queued_length(channel, m_orb, 100);

            Thread.sleep(5000); // sleep 5 seconds

            /***
             * Wait to finish SupplierThreads.
             * Calculate request rate
             */
            out.println("[Supplier] Waiting for join SupplierThreads...");
            for (int i = 0; i < threads_number; i++) {

                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    System.err.println("Warning: thread was interrupted");
                }
            }

            

            long total_time = System.currentTimeMillis() - start_time;
            double flooding_rate = 
                (double) (requests_number * threads_number) / 
                (double) (total_time / 1000.0);
            

            /***
             * Send a message to consumers in order to obtain 
             * statistical results from consumers
             */ 
            out.println("[Supplier] Sending shutdown to server... ");
            Thread.sleep(1000); // sleep 1 seconds

            SupplierAdmin admin = channel.default_supplier_admin();
            IntHolder supplier_id = new IntHolder();
            ProxyConsumer proxy_consumer = 
                admin.obtain_notification_push_consumer(ClientType.ANY_EVENT, 
                                                        supplier_id);
            ProxyPushConsumer consumer = 
                ProxyPushConsumerHelper.narrow(proxy_consumer);

            org.omg.CosEventComm.PushSupplier supplier = null;
            consumer.connect_push_supplier(supplier);

            Any data = m_orb.create_any();
            Event event = new Event();
            event.bool_val = true;
            event.long_val = 4;
            EventHelper.insert(data, event);

            consumer.push(data);

            out.println("");
            out.println("Statistics");
            out.println("==========");
            out.println("");
            out.println("  [  1 ] : Client threads: " + threads_number);
            out.println("  [  2 ] : Requests to be sent: " + requests_number);
            //out.println("  [  3 ] : Expected requests by server");
            //out.println("  [  4 ] : Arrived requests at server");
            //out.println("  [  5 ] : Requests in flood threshold");
            //out.println("  [  6 ] : Statistics time");
            //out.println("  [  7 ] : Server request processing rate (req/s)");
            out.println("  [  8 ] : Client flodding request rate (req/s): " + flooding_rate);
            //out.println("  [  9 ] : Requests sent by client");
            //out.println("  [ 10 ] : Success replies at client");
            //out.println("  [ 11 ] : Error replies at client");
            //out.println("  [ 12 ] : Success ratio at client");

            System.exit(0);

        }  catch (Throwable th) {
            th.printStackTrace();            
        } finally {
            if (m_orb != null) {
                m_orb.destroy();
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
