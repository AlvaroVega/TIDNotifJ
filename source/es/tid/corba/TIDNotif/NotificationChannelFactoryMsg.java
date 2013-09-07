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

public interface NotificationChannelFactoryMsg
{
  // POA's Id
  public final static String GLOBAL_CHANNEL_POA_ID = "TIDGlobalChannelPOA";
  public final static String LOCAL_CHANNEL_POA_ID = "TIDLocalChannelPOA@";

  public final static String[] CREATE_CHANNEL =
   { "-> NotificationChannelFactoryImpl.create_notification_channel(",
                                                        null, ",", null, ")" };
  public final static String[] CREATE_DCHANNEL =
   { "-> NotificationChannelFactoryImpl.create_distribution_channel(",
                                                        null, ",", null, ")" };
  public final static String CHANNELS =
   "-> NotificationChannelFactoryImpl.channels()";
  public final static String NEW_CHANNELS =
   "-> NotificationChannelFactoryImpl.new_channels()";
  public final static String DESTROY_FROM_ADMIN =
   " +>NotificationChannelFactoryImpl.destroyFromAdmin()";
  public final static String[] NEW_CHANNEL =
   { " * NEW NotificationChannelImpl: ", null };
  public final static String GET_GLOBAL_CHANNEL_POA =
   " +>NotificationChannelFactoryImpl.getGlobalChannelPOA()";
  public final static String DESTROY_GLOBAL_CHANNEL_POA =
   " +>NotificationChannelFactoryImpl.destroyGlobalChannelPOA()";


  public final static String CHANNEL_INFO_1[] = 
   { "   POA = ", null };
  public final static String CHANNEL_INFO_2[] =
   { "  Priority= ",null,", Event Lifetime= ",null,", Operation Mode= ",null };

  public final static String B_O__NOT_ALLOWED_N =
   "  Not allowed in notification mode.";
  public final static String B_O__NOT_ALLOWED_D =
   "  Not allowed in distribution mode.";
  public final static String REGISTER_EXCEPTION =
   "   ***** register_channel(): EXCEPTION *****";
}
