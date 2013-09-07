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

public interface ConsumerAdminMsg
{

  // ID's
  public final static String PROXYPUSHSUPPLIER_ID = "ProxyPushSupplier@";
  public final static String PROXYPULLSUPPLIER_ID = "ProxyPullSupplier@";

  public final static String GLOBAL_CONSUMER_POA_ID = 
   "TIDGlobalConsumerPOA";
  public final static String LOCAL_CONSUMER_POA_ID = 
   "TIDLocalConsumerPOA@";
  public final static String CONSUMER_POA_ID = 
   "TIDConsumerPOA@";
  public final static String GLOBAL_PROXYPUSHSUPPLIER_POA_ID =
   "GlobalProxyPushSupplierPOA";
  public final static String LOCAL_PROXYPUSHSUPPLIER_POA_ID =
   "LocalProxyPushSupplierPOA@";
  public final static String PROXYPUSHSUPPLIER_POA_ID =
   "ProxyPushSupplierPOA@";
  public final static String GLOBAL_PROXYPULLSUPPLIER_POA_ID =
   "GlobalProxyPullSupplierPOA";
  public final static String LOCAL_PROXYPULLSUPPLIER_POA_ID =
   "LocalProxyPullSupplierPOA@";
  public final static String PROXYPULLSUPPLIER_POA_ID =
   "ProxyPullSupplierPOA@";
  public final static String GLOBAL_CONSUMER_DISCRIMINATOR_POA_ID =
   "GlobalConsumerDiscriminator";
  public final static String LOCAL_CONSUMER_DISCRIMINATOR_POA_ID =
   "LocalConsumerDiscriminator@";
  public final static String CONSUMER_DISCRIMINATOR_POA_ID =
   "ConsumerDiscriminator@";
  public final static String GLOBAL_CONSUMER_INDEXLOCATOR_POA_ID =
   "GlobalConsumerIndexLocator";
  public final static String LOCAL_CONSUMER_INDEXLOCATOR_POA_ID =
   "LocalConsumerIndexLocator@";
  public final static String CONSUMER_INDEXLOCATOR_POA_ID =
   "ConsumerIndexLocator@";
  public final static String GLOBAL_MAPPING_DISCRIMINATOR_POA_ID =
   "GlobalMappingDiscriminator";
  public final static String LOCAL_MAPPING_DISCRIMINATOR_POA_ID =
   "GlobalMappingDiscriminator@";
  public final static String MAPPING_DISCRIMINATOR_POA_ID =
   "MappingDiscriminator@";


  public final static String GET_OPERATIONAL_STATE =
   "-> ConsumerAdminImpl.operational_state()";
  public final static String GET_ADMINISTRATIVE_STATE =
   "-> ConsumerAdminImpl.administrative_state()";
  public final static String SET_ADMINISTRATIVE_STATE =
   "-> ConsumerAdminImpl.administrative_state(value)";
  public final static String GET_ASSOCIATED_CRITERIA =
   "-> ConsumerAdminImpl.associated_criteria()";
  public final static String GET_EXTENDED_CRITERIA =
   "-> ConsumerAdminImpl.extended_criteria()";
  public final static String SET_EXTENDED_CRITERIA =
   "-> ConsumerAdminImpl.extended_criteria(new_criteria)";
  public final static String GET_PRIORITY_DISCRIMINATOR =
   "-> ConsumerAdminImpl.priority_discriminator()";
  public final static String SET_PRIORITY_DISCRIMINATOR =
   "-> ConsumerAdminImpl.priority_discriminator(value)";
  public final static String GET_LIFETIME_DISCRIMINATOR =
   "-> ConsumerAdminImpl.lifetime_discriminator()";
  public final static String SET_LIFETIME_DISCRIMINATOR =
   "-> ConsumerAdminImpl.lifetime_discriminator(value)";
  public final static String GET_FORWARDING_DISCRIMINATOR =
   "-> ConsumerAdminImpl.forwarding_discriminator()";
  public final static String SET_FORWARDING_DISCRIMINATOR =
   "-> ConsumerAdminImpl.forwarding_discriminator(value)";
  public final static String GET_INDEX_LOCATOR =
   "-> ConsumerAdminImpl.get_index_locator()";
  public final static String SET_INDEX_LOCATOR =
   "-> ConsumerAdminImpl.set_index_locator()";
  public final static String GET_EXCEPTION_HANDLER =
   "-> ConsumerAdminImpl.distribution_error_handler()";
  public final static String SET_EXCEPTION_HANDLER =
   "-> ConsumerAdminImpl.distribution_error_handler(handler)";


  public final static String NEW_FOR_CONSUMERS[] =
   { "-> ConsumerAdminImpl.new_for_consumers() ", null };
  public final static String NEW_POSITIONED_FOR_CONSUMERS[] =
   { "-> ConsumerAdminImpl.new_positioned_for_consumers() ", 
                                                        null, " (", null,")" };
  public final static String FIND_FOR_CONSUMERS =
   "-> ConsumerAdminImpl.find_for_consumers()";
  public final static String OBTAIN_POSITIONED_FOR_CONSUMERS[] =
   { "-> ConsumerAdminImpl.obtain_positioned_for_consumers() ", null };
  public final static String MOVE_POSITIONED_COSUMER_ADMIN[] =
   { " +>ConsumerAdminImpl.move_positioned_consumer_admin(): ",
                        null," [",null,"] * ",null," -> ",null," (",null,")" };
  public final static String CONSUMER_ADMINS =
   "-> ConsumerAdminImpl.consumer_admins()";
  public final static String CONSUMER_ADMIN_IDS =
   "-> ConsumerAdminImpl.consumer_admin_ids()";
  public final static String DESTROY[] =
   { "-> ConsumerAdminImpl.destroy(): ", null, " [", null, "]" };
  public final static String DESTROY_CONSUMER_ADMIN[] =
   { " +>ConsumerAdminImpl.destroyConsumerAdmin(): ", null, " [", null, "]" };
  public final static String REMOVE_CONSUMER_ADMIN[] =
   { " +>ConsumerAdminImpl.removeConsumerAdmin(): ", null };
  public final static String REMOVE_PROXYPUSHSUPPLIER[] =
   { " +>ConsumerAdminImpl.removeProxyPushSupplier(): ",null," [",null,"]" };
  public final static String REMOVE_PROXYPULLSUPPLIER[] =
   { " +>ConsumerAdminImpl.removeProxyPullSupplier(): ",null," [",null,"]" };
  public final static String DESTROY_FROM_CHANNEL[] =
   { " +>ConsumerAdminImpl.destroyFromChannel(): ", null, " [", null, "]" };
  public final static String DESTROY_FROM_ADMIN[] =
   { " +>ConsumerAdminImpl.destroyFromAdmin(): ", null, " [", null, "]" };
  public final static String BASIC_DESTROY[] =
   { " +>ConsumerAdminImpl.basicDestroy(): ", null, " [", null, "]" };
  public final static String TRY_CONSUMERADMIN[] =
   { "    ConsumerAdminId: ", null, "  (", null, ")" };
  public final static String TRY_PROXYPUSHSUPPLIER[] =
   { "    ProxyPushSupplierId: [", null, "] order=", null, " where=", null };
  public final static String TRY_PROXYPULLSUPPLIER[] =
   { "    ProxyPullSupplierId: ", null, "  (", null, ")" };




  public final static String OBTAIN_PUSH_SUPPLIER[] =
   { "-> ConsumerAdminImpl.obtain_push_supplier() ", null };
  public final static String OBTAIN_POSITIONED_PUSH_SUPPLIER[] =
   { "-> ConsumerAdminImpl.obtain_positioned_push_supplier() ", null };
  public final static String OBTAIN_NAMED_PUSH_SUPPLIER[] =
   { "-> ConsumerAdminImpl.obtain_named_push_supplier(", null, ") for ", null };
  public final static String OBTAIN_NAMED_POSITIONED_PUSH_SUPPLIER[] =
   { "-> ConsumerAdminImpl.obtain_named_positioned_push_supplier() ", null };
  public final static String FIND_PUSH_SUPPLIER[] =
   { "-> ConsumerAdminImpl.find_push_supplier(", null, ") in ", null };
  public final static String MOVE_POSITIONED_PUSH_SUPPLIER[] =
   { " +>ConsumerAdminImpl.move_positioned_push_supplier(): ",
                        null," [",null,"] * ",null," -> ",null," (",null,")" };
  public final static String PUSH_SUPPLIERS =
   "-> ConsumerAdminImpl.push_suppliers()";
  public final static String PUSH_SUPPLIER_IDS =
   "-> ConsumerAdminImpl.push_supplier_ids()";


  public final static String OBTAIN_PULL_SUPPLIER[] =
   { "-> ConsumerAdminImpl.obtain_pull_supplier() ", null };
  public final static String OBTAIN_NAMED_PULL_SUPPLIER[] =
   { "-> ConsumerAdminImpl.obtain_named_pull_supplier()", null };
  public final static String FIND_PULL_SUPPLIER =
   "-> ConsumerAdminImpl.find_pull_supplier()";
  public final static String OBTAIN_POSITIONED_PULL_SUPPLIER[] =
   { "-> ConsumerAdminImpl.obtain_positioned_pull_supplier() ", null };
  public final static String PULL_SUPPLIERS =
   "-> ConsumerAdminImpl.pull_suppliers()";
  public final static String PULL_SUPPLIER_IDS =
   "-> ConsumerAdminImpl.pull_supplier_ids()";
  public final static String CREATE_ANY_VALUE =
   " # ConsumerAdminImpl.createAnyValue(value): Exception.";

  public final static String[] NEW_PROXYPUSHSUPPLIER =
   { "* NEW ProxyPushSupplierImpl: ", null };
  public final static String[] NEW_PROXYPULLSUPPLIER =
   { "* NEW ProxyPullSupplierImpl: ", null };
  public final static String DESTROY_CONSUMER_POA =
   " +>ConsumerAdminImpl.destroyConsumerPOA()";
  public final static String REGISTER[] =
   { " # ConsumerAdminImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # ConsumerAdminImpl.unregister() -> ", null };

  public final static String N_P__LOCKED = 
   "  Administrative state: Locked.";
  public final static String B_O__NOT_ALLOWED =
   "  Not allowed in distribution mode.";
  public final static String B_P__EXIST_ID = 
   "  Criteria Id Already Exist!";
  public final static String NOT_ALLOWED = 
   "  NOT ALLOWED IN NOTIFICATION MODE";
  public final static String O_N_EXIST = 
   "  ExceptionHandler: null value.";
  public final static String LOCKED = 
   "  ConsumerAdmin locked!";
  public final static String DISCARD_EVENT = 
   "  push_event(): DISCARD EVENT";
  public final static String PARENT_NULL = 
   "  ##### PARENT == NULL #####";
  public final static String LOST_EVENT = 
   "  ConsumerAdminImpl.dispatch_event(): lost EVENT.";
  public final static String SENT_TO_HANDLER = 
   "  ConsumerAdminImpl.dispatch_event(): Event sent to handler";
  public final static String HANDLER_ERROR = 
   "  ConsumerAdminImpl.dispatch_event(): exception_handler ERROR";
  public final static String NOT_DELIVERED = 
   "NOT DELIVERED";





  public final static String MSG_ERROR_0 =
   "  ConsumerAdmin.dispatch_event: pushSupplier LOCKED";
  public final static String MSG_ERROR_1 =
   "  ConsumerAdmin.dispatch_event: pushSupplier NOT MATCH";
  public final static String MSG_ERROR_2 =
   "  ConsumerAdmin.dispatch_event: pushSupplier COMM ERROR";
  public final static String MSG_ERROR_3 =
   "  ConsumerAdmin.dispatch_event: pushSupplier LOST EVENT";
  public final static String MSG_ERROR_4 =
   "  ConsumerAdmin.deliver_event: pushSupplier ERROR";
  public final static String MSG_ERROR_0B =
   "  ConsumerAdmin.dispatch_event: consumerAdmin LOCKED";
  public final static String MSG_ERROR_1B =
   "  ConsumerAdmin.dispatch_event: consumerAdmin NOT MATCH";
  public final static String MSG_ERROR_2B =
   "  ConsumerAdmin.dispatch_event: consumerAdmin NOT DELIVERED";
  public final static String MSG_ERROR_3B =
   "  ConsumerAdmin.dispatch_event: consumerAdmin LOST EVENT";
  public final static String MSG_ERROR_4B =
   "  ConsumerAdmin.deliver_event: consumerAdmin ERROR";
  public final static String MSG_ERROR_5B =
   "  ConsumerAdmin.deliver_event: ProxyPushSupplier NOT FOUND";
  public final static String MSG_ERROR_6B =
   "  ConsumerAdmin.deliver_event: consumerAdmin NOT FOUND";

  public final static String MSG_ERROR_00D =
   "  ConsumerAdmin.deliver_event: default_string_index is NULL";
  public final static String MSG_ERROR_10D[] =
   { "  ConsumerAdmin.deliver_event: ProxyPushSupplier NOT FOUND (1st round) key=",null };
  public final static String MSG_ERROR_20D[] =
   { "  ConsumerAdmin.deliver_event: ProxyPushSupplier NOT FOUND (2st round) key=",null};
  public final static String MSG_ERROR_11D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier LOCKED (1st round) key=", null };
  public final static String MSG_ERROR_21D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier LOCKED (2st round) key=", null };
  public final static String MSG_ERROR_12D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier NOT MATCH (1st round) key=", null };
  public final static String MSG_ERROR_22D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier NOT MATCH (2st round) key=", null };
  public final static String MSG_ERROR_13D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier COMM ERROR (1st round) key=", null };
  public final static String MSG_ERROR_23D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier COMM ERROR (2st round) key=", null };
  public final static String MSG_ERROR_14D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier ERROR (1st round) key=", null };
  public final static String MSG_ERROR_24D[] =
   { "  ConsumerAdmin.deliver_event: pushSupplier ERROR (2st round) key=", null };

  public final static String INDX_ERROR_0 =
   "  ConsumerAdmin.index_locator.get_xxx_index(): ERROR InvalidExpression";
  public final static String INDX_ERROR_1 =
   "  ConsumerAdmin.index_locator.get_xxx_index(): ERROR FieldNotFound";
  public final static String INDX_ERROR_2 =
   "  ConsumerAdmin.index_locator.get_xxx_index(): ERROR TypeDoesNotMatch";

  public final static String MSG_ERROR_01 =
   "  ConsumerAdmin.order(): Already Defined Order";
  public final static String MSG_ERROR_02 =
   "  ConsumerAdmin.order(): Data Error";
  public final static String MSG_ERROR_03 =
   "  ConsumerAdmin.order(): Order Not Found";
  public final static String MSG_ERROR_04 =
   "  ConsumerAdmin.extended_criteria(new_criteria): Invalid Criteria";
  public final static String MSG_ERROR_1C =
   "  ConsumerAdmin.distrib_event: NOT MATCH";
  public final static String MSG_ERROR_2C =
   "  ConsumerAdmin.distrib_event: lost EVENT";
  public final static String PPSHS_NOT_FOUND =
   "  removeProxyPushSupplier(): ProxyPushSupplier ### NOT FOUND ###";
  public final static String PPLLS_NOT_FOUND =
   "  removeProxyPushSupplier(): ProxyPullSupplier ### NOT FOUND ###";
  public final static String CA_NOT_FOUND =
   "  removeConsumerAdmin(): ConsumerAdmin ### NOT FOUND ###";

  public final static String DISPATCH_CME =
       "ConsumerAdminImpl.dispatch_event(): CONCURRENT MODIFICATION EXCEPTION";
  public final static String DELIVER_CME =
        "ConsumerAdminImpl.deliver_event(): CONCURRENT MODIFICATION EXCEPTION";
}
