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

public interface ProxyPushConsumerMsg
{
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
   "-> ProxyPushConsumerImpl.operational_state()";
  public final static String GET_ADMINISTRATIVE_STATE =
   "-> ProxyPushConsumerImpl.administrative_state()";
  public final static String SET_ADMINISTRATIVE_STATE =
   "-> ProxyPushConsumerImpl.administrative_state(value)";
  public final static String GET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPushConsumerImpl.forwarding_discriminator()";
  public final static String SET_FORWARDING_DISCRIMINATOR =
   "-> ProxyPushConsumerImpl.forwarding_discriminator(value)";
  public final static String GET_PUSH_SUPPLIER =
   "-> ProxyPushConsumerImpl.getPushSupplier()";
  public final static String CONNECT_PUSH_SUPPLIER =
   "-> ProxyPushConsumerImpl.connect_push_supplier(supplier)";
  public final static String DISCONNECT_PUSH_CONSUMER[] =
   {"-> ProxyPushConsumerImpl.disconnect_push_consumer(): ",null," [",null,"]"};
  public final static String GET_TRANSFORMING_OPERATOR =
   "-> ProxyPushConsumerImpl.operator()";
  public final static String SET_TRANSFORMING_OPERATOR =
   "-> ProxyPushConsumerImpl.operator(value)";
  public final static String GET_EXCEPTION_HANDLER =
   "-> ProxyPushConsumerImpl.transformator_error_handler()";
  public final static String SET_EXCEPTION_HANDLER =
   "-> ProxyPushConsumerImpl.transformator_error_handler(handler)";
  public final static String DESTROY_FROM_ADMIN[] =
   { " +>ProxyPushConsumerImpl.destroyFromAdmin(): ", null, " [", null, "]" };
  public final static String DESTROY_DISCRIMINATOR[] =
   {" +>ProxyPushConsumerImpl.destroyDiscriminator(): ", null, " [", null, "]"};
  public final static String DEACTIVATE_DISCRIMINATOR =
   " +>ProxyPushConsumerImpl.deactivate_discriminator()";
  public final static String REGISTER[] =
   { " # ProxyPushConsumerImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # ProxyPushConsumerImpl.unregister() -> ", null };

  public final static String DISCONNECT_PUSH_SUPPLIER_ERROR =
   " # ProxyPushConsumerImpl.disconnect_push_supplier(): *** EXCEPTION ***";
  public final static String[] NO_PROCESSED_EVENTS =
   { "  ProxyPushConsumerImpl.destroyFromAdmin(): NO PROCESSED EVENTS: ",null };
  public final static String IPUSH_COMM_FAILURE =
   " # ProxyPushConsumerImpl.ipush(event): org.omg.CORBA.COMM_FAILURE";
  public final static String IPUSH_EXCEPTION =
   " # ProxyPushConsumerImpl.ipush(event): EXCEPTION";
  public final static String DISCARD_EVENT =
   "  ProxyPushConsumerImpl.ipush(): DISCARD EVENT";
  public final static String[] TIME_INFO =
   { ">>>Supplier Request Time = ", null, " [", null, "-", null, "]" };
  public final static String[] TIME_INFO_2 =
   { ">>>Internal Process A Time = ", null, " [", null, "-", null, "]" };
  public final static String[] TIME_INFO_3 =
   { "\n****************************************************\n",
     "** Num. Processed Events = ", null, " events.\n",
     "** Average Processing Ratio  = ", null, " events/seconds.\n",
     "** Average Processing Time  = ", null, " milliseconds.\n",
     "** Max. Processing Time  = ", null, " milliseconds.\n",
     "** Total Processing Time = ", null, " milliseconds.\n",
     "****************************************************" };
  public final static String[] TIME_INFO_4 =
   { ">>>Transformation Time = ", null };

  public final static String B_O__NOT_ALLOWED =
   "  Not allowed in distribution mode.";
  public final static String O_N_EXIST = 
   "  ExceptionHandler: null value.";
  public final static String LOCKED = 
   "  ProxyPushConsumer locked!";
  public final static String NOT_ALLOWED = 
   "  NOT ALLOWED IN NOTIFICATION MODE";
  public final static String NOT_CONNECTED = 
   "  No CONNECTED or DESTROYING!";
  public final static String FLOOD_EVENTS = 
   "  FLOOD Event Reached. sleep().";

  public final static String NARROW_ERROR =
   " # ProxyPushConsumerImpl: narrow(push_supplier_object)";
}
