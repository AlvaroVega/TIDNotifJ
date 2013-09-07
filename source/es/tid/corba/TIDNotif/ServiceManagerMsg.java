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

package es.tid.corba.TIDNotif;

public interface ServiceManagerMsg
{
  // Servant's ID
  public final static String NOTIFICANTIONADMIN_ID =
   "NotificationAdmin";
  public final static String CHANNELFACTORY_ID = 
   "NotificationChannelFactory";

  // POA's ID
  public final static String NOTIFSERVERPOA_ID = 
   "TIDNotifPOA@";

  public final static String ROOT_POA[] =
   { " * rootPOA [", null, "] - POA Manager = ", null };
  public final static String NEW_ADMIN =
   " * NEW NotificationAdminImpl: NotificationAdmin";
  public final static String NEW_FACTORY =
   " * NEW NotificationChannelFactoryImpl: NotificationChannelFactory";

  public final static String TRACE_ON = 
   " +>ServiceManager.trace_on(level)";
  public final static String TRACE_OFF = 
   " +>ServiceManager.trace_off()";
  public final static String SHUTDOWN = 
   " +>ServiceManager.shutdown()";
  public final static String LIST_CHANNELS =
   " +>ServiceManager.list_channels()";
  public final static String CHANNEL_FACTORY =
   " +>ServiceManager.channel_factory()";
  public final static String CREATE_CHANNEL_FACTORY =
   "  create_channel_factory()";
  public final static String LOAD_ALL =
   " +>ServiceManager.loadAll()";
  public final static String ADMIN_POA[] = 
   { "   POA = ", null };
  public final static String FACTORY_POA[] = 
   { "   POA = ", null };
}
