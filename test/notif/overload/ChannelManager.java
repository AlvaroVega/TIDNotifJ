/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 64 $
* Date: $Date: 2008-04-07 17:37:28 +0200 (lun, 07 abr 2008) $
* Last modified by: $Author: avega $
*
* (C) Copyright 2005 Telefónica Investigación y Desarrollo
*     S.A.Unipersonal (Telefónica I+D)
*
* Info about members and contributors of the MORFEO project
* is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/CREDITS
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
*   http://www.morfeo-project.org/TIDNotifJ/Licensing
*/

import es.tid.corba.TIDNotif.*;

import java.util.Properties;

import es.tid.corba.TIDNotif.util.TIDNotifUtil;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

import org.omg.CORBA.Policy;
import org.omg.NotificationService.NotificationAdmin;
import org.omg.NotificationService.NotificationAdminPackage.CannotProceed;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.ServantRetentionPolicyValue;

public class ChannelManager
{
  private static String server_version = "TIDNotifJ 2.0.1";

 /**
  *
  */
  private static org.omg.CORBA.ORB orbInit( String args[] )
  {
    Properties props = new Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
    System.getProperties().put( "org.omg.CORBA.ORBSingletonClass",
                                "es.tid.TIDorbj.core.SingletonORB" );

    // Puerto de escucha por defecto para que sea una referencia constante
    props.put("es.tid.TIDorbj.iiop.port","2002");
    props.put("es.tid.TIDorbj.iiop.max_connections","500");

    // Initialize the ORB
    return org.omg.CORBA.ORB.init(args, props);
  }

  public static void main( String args[] )
  {
    // Initialize the ORB
    org.omg.CORBA_2_5.ORB orb = (org.omg.CORBA_2_5.ORB) orbInit( args );

    // obtenemos referencia al RootPOA
    org.omg.PortableServer.POA rootPOA = ThePOAFactory.rootPOA(orb);

    try
    {
      // Activate the POA by its manager way
      rootPOA.the_POAManager().activate();
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }

    try
    {
      System.err.println("[ChannelManager] Initializing... ");

      java.util.Properties notif_props = new java.util.Properties();

      NotificationAdmin _notifAdmin=ServiceManager.init(orb, args, notif_props);

      // Lo Inicializamos y exportamos su referencia a fichero
      if ( TIDNotifConfig.getBool(TIDNotifConfig.URL_MODE_KEY) == true )
      {
        TIDNotifUtil.exportReference(
                ((es.tid.TIDorbj.core.TIDORB)orb).objectToURL(_notifAdmin) );
      }
      else
      {
        TIDNotifUtil.exportReference(orb.object_to_string(_notifAdmin));
      }

      es.tid.TIDorbj.core.poa.POAManagerImpl poaMgr =
              (es.tid.TIDorbj.core.poa.POAManagerImpl)rootPOA.the_POAManager();

      poaMgr.set_max_threads(
                    TIDNotifConfig.getInt(TIDNotifConfig.ROOTPOA_MAXTHR_KEY) );
      poaMgr.set_min_threads(
                    TIDNotifConfig.getInt(TIDNotifConfig.ROOTPOA_MINTHR_KEY) );
      poaMgr.set_max_queued_requests(
                    TIDNotifConfig.getInt(TIDNotifConfig.ROOTPOA_QUEUE_KEY) );

      NotificationAdminHealerImpl healer = new NotificationAdminHealerImpl(
              poaMgr, _notifAdmin);

      Policy policies [] = new Policy[3];
      policies[0] = rootPOA.create_lifespan_policy(
              LifespanPolicyValue.PERSISTENT);
      policies[1] = rootPOA.create_id_assignment_policy(
              IdAssignmentPolicyValue.SYSTEM_ID);
      policies[2] = rootPOA.create_servant_retention_policy(
              ServantRetentionPolicyValue.RETAIN);

      POA healing_poa = rootPOA.create_POA("healer_poa", null, policies);

      healing_poa.activate_object(healer);
      org.omg.CORBA.Object heal_obj = healing_poa.servant_to_reference(healer);

      orb.register_initial_reference("NotificationAdminHealer", heal_obj);

      healing_poa.the_POAManager().activate();

      System.err.println("[ChannelManager] Service is ready...");      

      // Bloqueamos el Servidor
      orb.run();

      // ...
      // despues de shutdown
      System.err.println("[ChannelManager] Service shutdown...");
    }
    catch (CannotProceed cp)
    {
      System.err.println(cp.why);
      cp.printStackTrace();
    }
    catch ( java.io.IOException ex )
    {
      System.exit(-1);
    }
    catch ( Exception ex )
    {
      ex.printStackTrace();
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
