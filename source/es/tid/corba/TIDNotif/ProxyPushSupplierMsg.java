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

public interface ProxyPushSupplierMsg
{
  public final static String GLOBAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "GlobalSupplierDiscriminator";
  public final static String LOCAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "LocalSupplierDiscriminator@";
  public final static String SUPPLIER_DISCRIMINATOR_POA_ID =
   "SupplierDiscriminator@";

  public final static String GET_OPERATIONAL_STATE =
   "-> ProxyPushSupplierImpl.operational_state()";
  public final static String GET_ADMINISTRATIVE_STATE =
   "-> ProxyPushSupplierImpl.administrative_state()";
  public final static String SET_ADMINISTRATIVE_STATE =
   "-> ProxyPushSupplierImpl.administrative_state(value)";
  public final static String GET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPushSupplierImpl.forwarding_discriminator()";
  public final static String SET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPushSupplierImpl.forwarding_discriminator(value)";
  public final static String GET_PUSH_CONSUMER =
   "-> ProxyPushConsumerImpl.getPushConsumer()";
  public final static String CONNECT_PUSH_CONSUMER[] =
   { "-> ProxyPushSupplierImpl.connect_push_consumer(", null, ")"};
  public final static String DISCONNECT_PUSH_SUPPLIER[] =
   {"-> ProxyPushSupplierImpl.disconnect_push_supplier(): ",null," [",null,"]"};
  public final static String DESTROY_FROM_ADMIN[] =
   { " +>ProxyPushSupplierImpl.destroyFromAdmin(): ", null, " [", null, "]" };
  public final static String DISCONNECT_PUSH_CONSUMER_ERROR =
   " # ProxyPushSupplierImpl.disconnect_push_consumer(): *** EXCEPTION ***";
  public final static String REGISTER[] =
   { " # ProxyPushSupplierImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # ProxyPushSupplierImpl.unregister() -> ", null };

  public final static String DEACTIVATE_DISCRIMINATOR[] =
   { "  * deactivate_discriminator: ", null };

  public final static String MSG_ERROR_1 =
   "ProxyPushSupplierImpl.order: AlreadyDefinedOrder";
  public final static String MSG_ERROR_2 =
   "ProxyPushSupplierImpl.order: DataError";
  public final static String MSG_ERROR_3 =
   "ProxyPushSupplierImpl.order: OrderNotFound";

  public final static String LOCKED[] =
   { "   ProxyPushSupplier [", null, "] LOCKED! (", null, ")" };
  public final static String NOT_CONNECTED3[] =
   { "  ProxyPushSupplierImpl.push_event(): DISCARD EVENT, no CONNECTED! [",
                                                      null, "] (", null, ")" };
  public final static String DESTROYING[] =
   { "  ProxyPushSupplierImpl.push_event(): DISCARD EVENT, DESTROYING! [",
                                                      null, "] (", null, ")" };
  public final static String DISCARD_EVENT[] =
   {"  ProxyPushSupplierImpl.push_event(): DISCARD EVENT, FALSE CONSTRAINT [",
                                                      null, "] (", null, ")" };

  public final static String[] EVAL_TIME_INFO =
           { " ### Eval_Value Time = ", null, " [", null, "] (", null, ")" };

  public final static String[] TIME_INFO =
           { ">>> PushConsumer Time = ", null, " [", null, "] (", null, ")" };

  public final static String[] TIME_INFO_3 =
   { "\n++++++++++++++++++++++++++++++++++++++++++++++++++++\n",
     "++ Num. Scheduled Events = ", null, " events.\n",
     "++ Average Scheduling Ratio  = ", null, " events/seconds.\n",
     "++ Average Scheduling Time  = ", null, " milliseconds.\n",
     "++ Max. Scheduling Time  = ", null, " milliseconds.\n",
     "++ Total Scheduling Time = ", null, " milliseconds.\n",
     "++++++++++++++++++++++++++++++++++++++++++++++++++++" };

  public final static String PUSH_EXCEPTION_1[] =
   { "  ProxyPushSupplierImpl.push_event(): *** EXCEPTION: COMM_FAILURE [",
                                                  null, "] (", null, ") ***" };
  public final static String PUSH_EXCEPTION_2[] =
   { "  ProxyPushSupplierImpl.push_event(): *** EXCEPTION: DISCONNECTED [",
                                                  null, "] (", null, ") ***" };
  public final static String AUTO_RESET[] =
   { "  ProxyPushSupplierImpl.push_event(): *** MAX COMM FAILURES RESET [",
                                                  null, "] (", null, ") ***" };
  public final static String AUTO_LOCK[] =
   { "  ProxyPushSupplierImpl.push_event(): *** MAX COMM FAILURES LOCK [",
                                                  null, "] (", null, ") ***" };
  public final static String AUTO_DISCONNECT[] =
   { "  ProxyPushSupplierImpl.push_event(): *** MAX COMM FAILURES DISCONNECT [",
                                                  null, "] (", null, ") ***" };
  public final static String NO_RESPONSE_EXCEPTION[] =
   { "  ProxyPushSupplierImpl.push_event(): *** EXCEPTION: NO_RESPONSE [",
                                                  null, "] (", null, ") ***" };
  public final static String AUTO_RESET2[] =
   { "  ProxyPushSupplierImpl.push_event(): *** MAX NO RESPONSE RESET [",
                                                  null, "] (", null, ") ***" };
  public final static String AUTO_LOCK2[] =
   { "  ProxyPushSupplierImpl.push_event(): *** MAX NO RESPONSE LOCK [",
                                                  null, "] (", null, ") ***" };
  public final static String AUTO_DISCONNECT2[] =
   { "  ProxyPushSupplierImpl.push_event(): *** MAX NO RESPONSE DISCONNECT [",
                                                  null, "] (", null, ") ***" };
  public final static String AUTO_DISCONNECT3[] =
   { "  ProxyPushSupplierImpl.push_event(): *** MAX DISCONNECTED TIME DISCONNECT [",
                                                  null, "] (", null, ") ***" };
  public final static String NOT_CONNECTED5 =
   "  ProxyPushSupplierImpl.distrib_event(): No CONNECTED!";
  public final static String EVENT_NOT_MATCH =
   "  ProxyPushSupplierImpl.distrib_event(): EVENT NOT MATCH";
  public final static String DISTRIB_PUSH_EXCEPTION =
   "  ProxyPushSupplierImpl.idstrib_event(): Connetion EXCEPTION.";

  public final static String NARROW_ERROR =
   " # ProxyPushSupplierImpl: narrow(push_consumer_object)";

  public final static String PUSH_EVENT[] =
         { " +>ProxyPushSupplierImpl.push_event() [", null, "] (", null, ")" };
}
