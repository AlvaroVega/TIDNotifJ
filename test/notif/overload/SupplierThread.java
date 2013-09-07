/*
 * Morfeo Project
 * http://morfeo-project.org/
 * 
 * Copyright (C) 2008 Telefonica Investigacion y Desarrollo S.A. Unipersonal
 * 
 * Authors     : Alvaro Polo Valdenebro <apv at tid.es>
 *               Alvaro Vega Garcia <avega at tid dot es>
 * Module      : SupllierThread
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

import java.util.Random;
import java.util.Date;

import org.omg.CORBA.Any;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA_2_5.ORB;

import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.ClientType;
import org.omg.CosNotifyChannelAdmin.ConsumerAdmin;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumerHelper;
import org.omg.CosNotifyChannelAdmin.ProxyPushConsumer;
import org.omg.CosNotifyChannelAdmin.SupplierAdmin;

import Overload.*;

class SupplierThread extends Thread
{

    private int m_id;
    private long m_number_of_requests;
    private long m_successes;
    private long m_errors;
    
    private Date m_start;
    private Date m_stop;
    
    private Random m_rnd;
    
    private EventChannel m_channel;

    private SupplierAdmin admin;

    private PushSupplierImpl m_supplier;

    private ProxyPushConsumer m_consumer;

    private ORB m_orb;

    public SupplierThread(int id, ORB orb,
                          EventChannel channel,
                          long number_of_requests)
   {
      m_id = id;
      m_number_of_requests = number_of_requests;
      m_successes = 0;
      m_errors = 0;
      
      m_start = null;
      m_stop = null;
      
      m_rnd = new Random();
      
      m_orb = orb;

      m_channel = channel;

      SupplierAdmin admin = channel.default_supplier_admin();

      IntHolder supplier_id = new IntHolder();
      try {
          m_consumer = ProxyPushConsumerHelper.narrow(
                         admin.obtain_notification_push_consumer(ClientType.ANY_EVENT, 
                                                                 supplier_id));


          m_supplier = new PushSupplierImpl(m_orb);
          
          m_consumer.connect_push_supplier(m_supplier._this(m_orb));
          
      }  catch (Throwable th) {
          th.printStackTrace();    
      }
      System.out.println("[SupplierThread " + m_id + "] Created ");
   }
   
   public long getSuccesses()
   {
      return m_successes;
   }
   
   public long getErrors()
   {
      return m_errors;
   }
   
   public float getSuccessRate()
   {
      return ((float) m_successes) / ((float)(m_successes + m_errors));
   }
   
   /* Thread body */
   public void run()
   {
       try {
           Any data = m_orb.create_any();
           
           Event event = new Event();
           event.bool_val = false;
           event.long_val = 3;
           EventHelper.insert(data, event);
           //data.insert_string("Hello");
           
           for (int i = 0; i < m_number_of_requests; i++) {

               try {               
                   
                   //System.out.println("Message sent");
                   m_consumer.push(data);

                   
               } catch (org.omg.CORBA.NO_RESPONSE e) {
                   // Catched with a small number of max poa Threads
                   System.err.println("[SupplierThread " + this.m_id + "] " + 
                                      "CORBA NO_RESPONSE expected exception received");
                   
               } catch (org.omg.CORBA.TRANSIENT e) {
                   // Catched with a small number of max_queued_request
                   System.err.println("[SupplierThread " + this.m_id + "] " + 
                                      "CORBA TRANSIENT expected exception received");
                   
               } catch (org.omg.CORBA.COMM_FAILURE e) {
                   // Catched with a small number of max_open_connections
                   System.err.println("[SupplierThread " + this.m_id + "] " + 
                                      "CORBA COMM_FAILURE expected exception received");
                   Thread.sleep(1000); // sleep 1 seconds
               } catch (Exception e) {                                     
                   System.err.println("[ClientThread " + this.m_id + "] expected excepcion: " + 
                                      e.getMessage());
                   e.printStackTrace();
               }
               
               

               
           }
       
           m_consumer.disconnect_push_consumer();
       }  catch (Throwable th) {
           th.printStackTrace();    
       }
       
   }
}
