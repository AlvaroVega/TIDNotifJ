/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 98 $
* Date: $Date: 2008-10-27 11:17:20 +0100 (Mon, 27 Oct 2008) $
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

package es.tid.corba.TIDNotif;

public interface ProxyPullSupplierMsg
{
  public final static String GLOBAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "GlobalSupplierDiscriminator";
  public final static String LOCAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "LocalSupplierDiscriminator@";
  public final static String SUPPLIER_DISCRIMINATOR_POA_ID =
   "SupplierDiscriminator@";

  public final static String GET_OPERATIONAL_STATE =
   "-> ProxyPullSupplierImpl.operational_state()";
  public final static String GET_ADMINISTRATIVE_STATE =
   "-> ProxyPullSupplierImpl.administrative_state()";
  public final static String SET_ADMINISTRATIVE_STATE =
   "-> ProxyPullSupplierImpl.administrative_state(value)";
  public final static String GET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPullSupplierImpl.forwarding_discriminator()";
  public final static String SET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPullSupplierImpl.forwarding_discriminator(value)";
  public final static String GET_PULL_CONSUMER =
   "-> ProxyPullConsumerImpl.getPullConsumer()";
  public final static String CONNECT_PULL_CONSUMER =
   "-> ProxyPullSupplierImpl.connect_push_consumer(consumer)";
  public final static String DISCONNECT_PULL_SUPPLIER =
   "-> ProxyPullSupplierImpl.disconnect_push_supplier()";
  public final static String DESTROY_FROM_ADMIN =
   " +>ProxyPullSupplierImpl.destroyFromAdmin()";
  public final static String PULL =
   " +>ProxyPullSupplierImpl.pull()";
  public final static String TRY_PULL =
   " +>ProxyPullSupplierImpl.try_pull()";
  public final static String DEACTIVATE_DISCRIMINATOR =
   " +>ProxyPullSupplierImpl.deactivate_discriminator_servant()";
  public final static String REGISTER[] =
   { " # ProxyPullSupplierImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # ProxyPullSupplierImpl.unregister() -> ", null };

  public final static String DISCONNECT_PULL_CONSUMER_ERROR =
  " # ProxyPullSupplierImpl.destroyFromAdmin(): disconnect_pull_consumer ERROR";
  public final static String LOCKED[] = 
  { "   ProxyPullSupplier [", null, "] LOCKED! (", null, ")" };
  public final static String NOT_CONNECTED3[] =
   { "  ProxyPushSupplierImpl.push_event(): DISCARD EVENT, no CONNECTED! [",
                                                      null, "] (", null, ")" };
  public final static String DESTROYING[] =
   { "  ProxyPullSupplierImpl.push_event(): DISCARD EVENT, DESTROYING! [",
                                                      null, "] (", null, ")" };
  public final static String DISCARD_EVENT[] =
   { "  ProxyPullSupplierImpl.push_event(): DISCARD EVENT, FALSE CONSTRAINT [",
                                                      null, "] (", null, ")" };
  public final static String NARROW_ERROR =
   " # ProxyPullSupplierImpl: narrow(pull_consumer_object)";

  public final static String PUSH_EVENT[] =
         { " +>ProxyPullSupplierImpl.push_event() [", null, "] (", null, ")" };

}
