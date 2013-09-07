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

public interface SupplierAdminMsg
{
  public final static String PROXYPUSHCONSUMER_ID = 
   "ProxyPushConsumer@";
  public final static String PROXYPULLCONSUMER_ID = 
   "ProxyPullConsumer@";

  public final static String SUPPLIER_POA_ID = 
   "TIDSupplierPOA@";
  public final static String GLOBAL_PROXYPUSHCONSUMER_POA_ID =
   "GlobalProxyPushConsumerPOA";
  public final static String LOCAL_PROXYPUSHCONSUMER_POA_ID =
   "LocalProxyPushConsumerPOA@";
  public final static String PROXYPUSHCONSUMER_POA_ID =
   "ProxyPushConsumerPOA@";
  public final static String GLOBAL_PROXYPULLCONSUMER_POA_ID =
   "GlobalProxyPullConsumerPOA";
  public final static String LOCAL_PROXYPULLCONSUMER_POA_ID =
   "LocalProxyPullConsumerPOA@";
  public final static String PROXYPULLCONSUMER_POA_ID =
   "ProxyPullConsumerPOA@";
  public final static String GLOBAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "GlobalSupplierDiscriminator";
  public final static String LOCAL_SUPPLIER_DISCRIMINATOR_POA_ID =
   "LocalSupplierDiscriminator@";
  public final static String SUPPLIER_DISCRIMINATOR_POA_ID =
   "SupplierDiscriminator@";
  public final static String GLOBAL_TRANSFORMING_OPERATOR_POA_ID =
   "GlobalTransformingOperator";
  public final static String LOCAL_TRANSFORMING_OPERATOR_POA_ID =
   "LocalTransformingOperator@";
  public final static String TRANSFORMING_OPERATOR_POA_ID =
   "TransformingOperator@";

  public final static String GET_OPERATIONAL_STATE =
   "-> SupplierAdminImpl.operational_state()";
  public final static String GET_ADMINISTRATIVE_STATE =
   "-> SupplierAdminImpl.administrative_state()";
  public final static String SET_ADMINISTRATIVE_STATE =
   "-> SupplierAdminImpl.administrative_state(value)";
  public final static String GET_ASSOCIATED_CRITERIA =
   "-> SupplierAdminImpl.associated_criteria()";
  public final static String GET_EXTENDED_CRITERIA =
   "-> SupplierAdminImpl.extended_criteria()";
  public final static String SET_EXTENDED_CRITERIA =
   "-> SupplierAdminImpl.extended_criteria(value)";
  public final static String GET_FORWARDING_DISCRIMINATOR =
   "-> SupplierAdminImpl.forwarding_discriminator()";
  public final static String SET_FORWARDING_DISCRIMINATOR =
   "-> SupplierAdminImpl.forwarding_discriminator(value)";
  public final static String GET_TRANSFORMING_OPERATOR =
   "-> SupplierAdminImpl.operator()";
  public final static String SET_TRANSFORMING_OPERATOR =
   "-> SupplierAdminImpl.operator(value)";

  public final static String OBTAIN_PULL_CONSUMER[] =
   { "-> SupplierAdminImpl.obtain_pull_consumer(): ", null };
  public final static String OBTAIN_PUSH_CONSUMER[] =
   { "-> SupplierAdminImpl.obtain_push_consumer(): ", null };
  public final static String OBTAIN_NAMED_PULL_CONSUMER[] =
   { "-> SupplierAdminImpl.obtain_named_pull_consumer(): ", null };
  public final static String OBTAIN_NAMED_PUSH_CONSUMER[] =
   {  "-> SupplierAdminImpl.obtain_named_push_consumer(): ", null };
  public final static String FIND_PUSH_CONSUMER =
   "-> SupplierAdminImpl.find_push_consumer(name)";
  public final static String FIND_PULL_CONSUMER =
   "-> SupplierAdminImpl.find_pull_consumer(name)";
  public final static String NEW_FOR_SUPPLIERS[] =
   { "-> SupplierAdminImpl.new_for_suppliers() ", null };
  public final static String SET_SUPPLIERADMIN_PARENT =
   " +>SupplierAdminImpl.setSupplierAdminParent(value)";
  public final static String SET_SUPPLIERADMIN_CHILD =
   " +>SupplierAdminImpl.setSupplierAdminChild(value)";
  public final static String FIND_FOR_SUPPLIERS =
   "SupplierAdminImpl: find_for_suppliers()";
  public final static String SUPPLIER_ADMINS =
   "-> SupplierAdminImpl.supplier_admins()";
  public final static String SUPPLIER_ADMIN_IDS =
   "-> SupplierAdminImpl.supplier_admin_ids()";
  public final static String PULL_CONSUMERS =
   "-> SupplierAdminImpl.pull_consumers()";
  public final static String PUSH_CONSUMERS =
   "-> SupplierAdminImpl.push_consumers()";
  public final static String PULL_CONSUMER_IDS =
   "-> SupplierAdminImpl.pull_consumer_ids()";
  public final static String PUSH_CONSUMER_IDS =
   "-> SupplierAdminImpl.push_consumer_ids()";
  public final static String GET_EXCEPTION_HANDLER =
   "-> SupplierAdminImpl.transformator_error_handler()";
  public final static String SET_EXCEPTION_HANDLER =
   "-> SupplierAdminImpl.transformator_error_handler(handler)";
  public final static String REMOVE[] =
   { "-> SupplierAdminImpl.remove(): ", null, " [", null, "]" };
  public final static String DESTROY[] =
   { "-> SupplierAdminImpl.destroy(): ", null, " [", null, "]" };
  public final static String DESTROY_FROM_CHANNEL[] =
   { " +>SupplierAdminImpl.destroyFromChannel(): ", null, " [", null, "]" };
  public final static String DESTROY_FROM_ADMIN[] =
   { " +>SupplierAdminImpl.destroyFromAdmin(): ", null, " [", null, "]" };
  public final static String BASIC_DESTROY[] =
   { " +>SupplierAdminImpl.basicDestroy(): ", null, " [", null, "]" };
  public final static String DESTROY_SUPPLIER_ADMIN[] =
   { " +>SupplierAdminImpl.destroySupplierAdmin(): ", null, " [", null, "]" };
  public final static String REMOVE_PROXYPUSHCONSUMER[] =
   { " +>SupplierAdminImpl.removeProxyPushConsumer(", null, "): [", null,"]" };
  public final static String REMOVE_PROXYPULLCONSUMER[] =
   { " +>SupplierAdminImpl.removeProxyPullConsumer(", null, "): [", null,"]" };
  public final static String DESTROY_SUPPLIER_POA =
   " +>SupplierAdminImpl.destroySupplierPOA()";
  public final static String DEACTIVATE_DISCRIMINATOR =
   " +>SupplierAdminImpl.deactivate_discriminator_servant()";

  public final static String REGISTER[] =
   { " # SupplierAdminImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # SupplierAdminImpl.unregister() -> ", null };

  public final static String TRY_SUPPLIERADMIN[] =
   { "  Order: ", null, " - ConsumerAdmin: ", null };
  public final static String DESTROY_FROM_ADMIN_ERROR =
   "  destroyFromAdmin EXCEPTION.";
  public final static String DISCARD_EVENT = 
   "  push_event(): DISCARD EVENT";
  public final static String DISPATCH_EVENT_LEVEL_ERROR =
   "SupplierAdminImpl: dispatch_event() Invalid Level";
  public final static String PUSH_EVENT_LEVEL_ERROR =
   "SupplierAdminImpl: push_event() Invalid Level";
  public final static String INVALID_MODE = 
   "  INVALID_MODE";

  public final static String NOT_ALLOWED = 
   "  NOT ALLOWED IN NOTIFICATION MODE";
  public final static String B_O__NOT_ALLOWED =
   "Not allowed in distribution mode.";
  public final static String O_N_EXIST = 
   "ExceptionHandler: null value.";
  public final static String B_P__EXIST_ID = 
   "Criteria Id Already Exist!";
  public final static String LOCKED = 
   "SupplierAdmin locked!";
  public final static String MSG_ERROR =
   "SupplierAdmin.extended_criteria(new_criteria): Invalid Criteria";

}
