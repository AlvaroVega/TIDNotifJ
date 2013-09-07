/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 3 $
* Date: $Date: 2006-01-17 17:42:13 +0100 (Tue, 17 Jan 2006) $
* Last modified by: $Author: aarranz $
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

package es.tid.corba.TIDDistrib;

public interface ServiceManagerMsg
{
   public final static String TIDDISTRIBSERVER = 
                                            " *** TIDDistribServer v1.00 *** ";
   public final static String NEW_SERVER = "\n  NEW TIDDistribServer *";
   public final static String SERVER_READY = "\n  TIDDistrib Service is ready...";
   public final static String SERVER_END = "  TIDDistrib Service shutdown...";

   // POA's ID
   public final static String DISTRIBSERVERPOA_ID = "TIDDistribPOA@";

  // Servant's ID
  public final static String AGENT_ID = "es.tid.corba.TIDDistribAdmin.Agent";

  public final static String[] ROOT_POA =
                           { "* rootPOA [", null, "] - POA Manager = ", null };

  public final static String NEW_POA[] = { "* NEW POA: ", null };

  public final static String POA_INFO_1[] = { "    [", null, "]" };

  public final static String POA_INFO_2[] = { "    POA Manager = ", null };
  public final static String POA_INFO_3[] = { "    Parent POA = ", null };

  public final static String POA_INFO_4[] = 
    {"    Num. Threads = [", null, ",", null, "] -  Max. Queue Size = ", null};

  public final static String NEW_ADMIN = 
                         "* NEW AgentImpl: es.tid.corba.TIDDistribAdmin.Agent";
   
  public final static String[] ADMIN_POA = { "   POA = ", null };

  public final static String START = " +>ServiceManager.start()";
  public final static String STOP = " +>ServiceManager.stop()";
  public final static String RESUME = " +>ServiceManager.resume()";

  public final static String TRACE_ON = " +>ServiceManager.trace_on(level)";
  public final static String TRACE_OFF = " +>ServiceManager.trace_off()";
  public final static String LIST_CHANNELS=" +>ServiceManager.list_channels()";
  public final static String CHANNEL_FACTORY =
                                         " +>ServiceManager.channel_factory()";
  public final static String SHUTDOWN = " +>ServiceManager.shutdown()";

  public final static String REGISTER_CHANNEL = 
                                         "-> ServiceManager.registerChannel()";
  public final static String REMOVE_CHANNEL = 
                                           "-> ServiceManager.removeChannel()";
  // Servant's ID
  public final static String CHANNELFACTORY_ID = "DistributionChannelFactory";
  
  public final static String NEW_FACTORY = 
            "* NEW DistributionChannelFactoryImpl: DistributionChannelFactory";
  public final static String[] FACTORY_POA = { "   POA = ", null };
}
