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

public interface NotificationChannelMsg
{
  // POA's Id
  public final static String GLOBAL_SUPPLIER_POA_ID = 
   "TIDGlobalSupplierPOA";
  public final static String LOCAL_SUPPLIER_POA_ID = 
   "TIDLocalSupplierPOA@";
  public final static String SUPPLIER_POA_ID = 
   "TIDSupplierPOA@";
  public final static String GLOBAL_CONSUMER_POA_ID = 
   "TIDGlobalConsumerPOA";
  public final static String LOCAL_CONSUMER_POA_ID = 
   "TIDLocalConsumerPOA@";
  public final static String CONSUMER_POA_ID = 
   "TIDConsumerPOA@";
  public final static String LOCAL_CHANNEL_POA_ID = 
  "TIDLocalChannelPOA@";
  public final static String LOCAL_PROXYPUSHCONSUMER_POA_ID =
   "LocalProxyPushConsumerPOA@";
  public final static String LOCAL_PROXYPULLCONSUMER_POA_ID =
   "LocalProxyPullConsumerPOA@";
  public final static String LOCAL_TRANSFORMING_OPERATOR_POA_ID =
   "LocalTransformingOperator@";
  public final static String LOCAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "LocalSupplierDiscriminator@";
  public final static String LOCAL_PROXYPUSHSUPPLIER_POA_ID =
   "LocalProxyPushSupplierPOA@";
  public final static String LOCAL_PROXYPULLSUPPLIER_POA_ID =
   "LocalProxyPullSupplierPOA@";
  public final static String LOCAL_CONSUMER_DISCRIMINATOR_POA_ID =
   "LocalConsumerDiscriminator@";


  public final static String GET_OPERATIONAL_STATE =
   "-> NotificationChannelImpl.operational_state()";
  public final static String GET_CREATION_DATE =
   "-> NotificationChannelImpl.creation_date()";
  public final static String GET_CREATION_TIME =
   "-> NotificationChannelImpl.creation_time()";
  public final static String GET_ADMINISTRATIVE_STATE =
   "-> NotificationChannelImpl.administrative_state()";
  public final static String SET_ADMINISTRATIVE_STATE =
   "-> NotificationChannelImpl.administrative_state(value)";
  public final static String GET_OPERATION_MODE =
   "-> NotificationChannelImpl.operation_mode()";
  public final static String NEW_FOR_CONSUMERS[] =
   { "-> NotificationChannelImpl.new_for_consumers(): ", null };
  public final static String NEW_FOR_SUPPLIERS[] =
   { "-> NotificationChannelImpl.new_for_suppliers(): ", null };
  public final static String NEW_OPERATED_FOR_CONSUMERS =
   "-> NotificationChannelImpl.new_operated_for_consumers()";
  public final static String NEW_POSITIONED_FOR_CONSUMERS[] =
   { " +>NotificationChannelImpl.new_positioned_for_consumers(): ",
     null,", consumer_id: ",null," [order: ",null, ", new_order: ",null, "]" };
  public final static String NEW_TRANSFORMED_FOR_SUPPLIERS[] =
   { " +>NotificationChannelImpl.new_transformed_for_suppliers(): ", null };
  public final static String FIND_FOR_CONSUMERS =
   "-> NotificationChannelImpl.find_for_consumers()";
  public final static String FIND_FOR_SUPPLIERS =
   "-> NotificationChannelImpl.find_for_supplier()";
  public final static String NEW_FIND_FOR_CONSUMERS =
   "-> NotificationChannelImpl.new_find_for_consumers()";
  public final static String NEW_FIND_FOR_SUPPLIERS =
   "-> NotificationChannelImpl.new_find_for_suppliers()";
  public final static String CONSUMER_ADMINS =
   "-> NotificationChannelImpl.consumer_admins()";
  public final static String SUPPLIER_ADMINS =
   "-> NotificationChannelImpl.supplier_admins()";
  public final static String CONSUMER_ADMIN_IDS =
   "-> NotificationChannelImpl.consumer_admin_ids()";
  public final static String SUPPLIER_ADMIN_IDS =
   "-> NotificationChannelImpl.supplier_admin_ids()";
  public final static String GET_PRIORYTY =
   "-> NotificationChannelImpl.default_priority()";
  public final static String SET_PRIORYTY =
   "-> NotificationChannelImpl.default_priority(value)";
  public final static String GET_LIFETIME =
   "-> NotificationChannelImpl.default_event_lifetime()";
  public final static String SET_LIFETIME =
   "-> NotificationChannelImpl.default_event_lifetime(value)";
  public final static String FOR_CONSUMERS[] =
   { "-> NotificationChannelImpl.for_consumers(): ", null };
  public final static String FOR_SUPPLIERS[] =
   { "-> NotificationChannelImpl.for_suppliers(): ", null };
  public final static String DESTROY[] =
   { "-> NotificationChannelImpl.destroy(): ", null };
  public final static String DESTROY_CHANNEL[] =
   { " +>NotificationChannelImpl.destroyChannel(): ", null };
  public final static String DESTROY_LOCAL_CHANNEL_POA =
   " +>NotificationChannelImpl.destroyLocalChannelPOA()";
  public final static String DESTROY_LOCAL_SUPPLIER_POA =
   " +>NotificationChannelImpl.destroyLocalSupplierPOA()";
  public final static String DESTROY_LOCAL_CONSUMER_POA =
   " +>NotificationChannelImpl.destroyLocalConsumerPOA()";
  public final static String NEW_CONSUMER_ADMINS =
   "-> NotificationChannelImpl.new_consumer_admins()";
  public final static String NEW_SUPPLIER_ADMINS =
   "-> NotificationChannelImpl.new_supplier_admins()";
  public final static String NEW_CONSUMER_ADMIN_IDS =
   "-> NotificationChannelImpl.new_consumer_admin_ids()";
  public final static String NEW_SUPPLIER_ADMIN_IDS =
   "-> NotificationChannelImpl.new_supplier_admin_ids()";
  public final static String REMOVE_SUPPLIER_ADMIN =
   " +>NotificationChannelImpl.removeSupplierAdmin()";
  public final static String REMOVE_NEW_SUPPLIER_ADMIN =
   " +>NotificationChannelImpl.removeNewSupplierAdmin()";
  public final static String SWAP_SUPPLIER_ADMIN =
   " +>NotificationChannelImpl.swapSupplierAdmin()";
  public final static String REMOVE_CONSUMER_ADMIN =
   " +>NotificationChannelImpl.removeConsumerAdmin()";
  public final static String REMOVE_NEW_CONSUMER_ADMIN =
   " +>NotificationChannelImpl.removeNewConsumerAdmin()";
  public final static String REGISTER[] =
   { " # NotificationChannelImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # NotificationChannelImpl.unregister() -> ", null };

  public final static String B_P__EXIST_ID = 
   "  Criteria Id Already Exist!";
  public final static String B_O__NOT_ALLOWED =
   "  Not allowed in distribution mode.";
  public final static String LOCKED =
   "  NotificationChannelImpl: AdministrativeState.locked()";
  public final static String NOT_FOUND = 
   "  # remove New Admin: NOT FOUND";

}
