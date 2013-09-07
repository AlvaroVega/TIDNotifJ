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

public interface ProxyPullConsumerMsg
{
  public final static String GLOBAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "GlobalSupplierDiscriminator";
  public final static String LOCAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "LocalSupplierDiscriminator@";
  public final static String SUPPLIER_DISCRIMINATOR_POA_ID =
   "SupplierDiscriminator@";

  public final static String GET_OPERATIONAL_STATE =
   "-> ProxyPullConsumerImpl.operational_state()";
  public final static String GET_ADMINISTRATIVE_STATE =
   "-> ProxyPullConsumerImpl.administrative_state()";
  public final static String SET_ADMINISTRATIVE_STATE =
   "-> ProxyPullConsumerImpl.administrative_state(value)";
  public final static String GET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPullConsumerImpl.forwarding_discriminator()";
  public final static String SET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPullConsumerImpl.forwarding_discriminator(value)";
  public final static String GET_PULL_SUPPLIER =
   "-> ProxyPullConsumerImpl.getPullSupplier()";
  public final static String CONNECT_PULL_SUPPLIER =
   "-> ProxyPullConsumerImpl.connect_push_supplier(supplier)";
  public final static String DISCONNECT_PULL_CONSUMER[] =
   {"-> ProxyPullConsumerImpl.disconnect_push_consumer(): ",null," [",null,"]"};
  public final static String DESTROY_FROM_ADMIN[] =
   { " +>ProxyPullConsumerImpl.destroyFromAdmin(): ", null, " [", null, "]" };
  public final static String DEACTIVATE_DISCRIMINATOR =
   " +>ProxyPullConsumerImpl.deactivate_discriminator()";
  public final static String DISCONNECT_PULL_CONSUMER_ERROR =
  " # ProxyPullConsumerImpl.destroyFromAdmin(): DISCONNECT_PULL_CONSUMER ERROR";
  public final static String REGISTER[] =
   { " # ProxyPullConsumerImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # ProxyPullConsumerImpl.unregister() -> ", null };

  public final static String[] TIME_INFO_2 =
   { " InternalProxyPullConsumer: Processing Time = ",
                                          null, " [", null, " - ", null, "]" };
  public final static String[] TIME_INFO_3 =
   { "\n****************************************************\n",
     "** Num. Processed Events = ", null, " events.\n",
     "** Average Processing Ratio  = ", null, " events/seconds.\n",
     "** Average Processing Time  = ", null, " milliseconds.\n",
     "** Max. Processing Time  = ", null, " milliseconds.\n",
     "** Total Processing Time = ", null, " milliseconds.\n",
     "****************************************************" };

  public final static String LOCKED = 
   "  ProxyPullConsumer locked!";
  public final static String[] NO_PROCESSED_EVENTS =
   { "  ProxyPullConsumerImpl.destroyFromAdmin(): NO PROCESSED EVENTS: ",null };
  public final static String DISCARD_EVENT =
   "  ProxyPullConsumerImpl.ipush(): DISCARD EVENT";

  public final static String NARROW_ERROR =
   " # ProxyPullConsumerImpl: narrow(pull_supplier_object)";
}
