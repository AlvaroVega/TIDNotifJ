/*
 * Morfeo Project
 * http://morfeo-project.org/
 * 
 * Copyright (C) 2008 Telefonica Investigacion y Desarrollo S.A. Unipersonal
 * 
 * Authors     : Alvaro Polo Valdenebro <apv at tid.es>
 *               Alvaro Vega Garcia <avega at tid dot es>
 * Module      : Channel
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

import org.omg.CORBA.IntHolder;
import org.omg.CORBA.Object;
import org.omg.CORBA.Any;
import org.omg.CORBA_2_5.ORB;
import org.omg.CosNotification.*;
import org.omg.TimeBase.*;
import org.omg.CosNotifyChannelAdmin.EventChannel;
import org.omg.CosNotifyChannelAdmin.EventChannelFactory;
import org.omg.CosNotifyChannelAdmin.ChannelNotFound;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminHelper;

import es.tid.TIDorbj.util.UTC;

import Overload.*;

public class SetupChannel {


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
                System.out.println("[SetupChannel] Factory exists");
            }


            IntHolder channel_id = new IntHolder();

            org.omg.CosNotifyChannelAdmin.EventChannel channel = null;

            try {
                channel = factory.get_event_channel(1);
                System.out.println("[SetupChannel] Got channel 1");
            } catch (ChannelNotFound e){
 
                factory.create_channel(new Property[0], new Property[0], channel_id);
                System.out.println("[SetupChannel] Channel created: " + channel_id.value);
                writeFile("channel.ior", orb.object_to_string(channel)); 
            }
            
            /// Start PANACEA monitoring, etc

            String heal_loc =
                    "corbaloc:iiop:1.2@localhost:2002/NotificationAdminHealer";

            org.omg.CORBA.Object notif_heal_obj =
                    orb.string_to_object(heal_loc);
            
            NotificationAdminHealer notif_heal =
                    NotificationAdminHealerHelper.narrow(notif_heal_obj);

          
            while (true) {
                Stats stats = notif_heal.getLastSecondsStats(1);
                System.err.println(
                        "[SetupChannel] Channel error rate " + 
                        stats.error_rate + " (" + stats.requests_discarded +
                        " of " + stats.requests_received + ")");
            }


            /// End PANACEA monitoring, etc
            
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
