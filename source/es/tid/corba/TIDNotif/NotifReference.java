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


import org.omg.NotificationChannelAdmin.NotificationChannel;
import org.omg.NotificationChannelAdmin.NotificationChannelHelper;
import org.omg.DistributionChannelAdmin.DistributionChannel;
import org.omg.DistributionChannelAdmin.DistributionChannelHelper;

import org.omg.NotificationChannelAdmin.SupplierAdmin;
import org.omg.NotificationChannelAdmin.SupplierAdminHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalSupplierAdminHelper;

import org.omg.NotificationChannelAdmin.ConsumerAdmin;
import org.omg.NotificationChannelAdmin.ConsumerAdminHelper;
//import org.omg.IndexedDistributionChannelAdmin.ConsumerAdmin;
//import org.omg.IndexedDistributionChannelAdmin.ConsumerAdminHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdmin;
import org.omg.DistributionInternalsChannelAdmin.InternalConsumerAdminHelper;

import org.omg.TransformationAdmin.TransformingOperator;
import org.omg.TransformationAdmin.TransformingOperatorHelper;
import org.omg.TransformationInternalsAdmin.InternalTransformingOperator;
import org.omg.TransformationInternalsAdmin.InternalTransformingOperatorHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannel;
import org.omg.DistributionInternalsChannelAdmin.InternalDistributionChannelHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalProxyPushConsumer;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPushConsumerHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullConsumer;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullConsumerHelper;

import org.omg.DistributionInternalsChannelAdmin.InternalProxyPushSupplier;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPushSupplierHelper;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullSupplier;
import org.omg.DistributionInternalsChannelAdmin.InternalProxyPullSupplierHelper;

import org.omg.CORBA.Policy;
import org.omg.CORBA.SetOverrideType;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterHelper;
import org.omg.CosNotifyFilter.MappingFilter;
import org.omg.CosNotifyFilter.MappingFilterHelper;

import org.omg.IndexAdmin.IndexLocator;
import org.omg.IndexAdmin.IndexLocatorHelper;

import org.omg.ServiceManagement.OperationMode;

public abstract class NotifReference
{
  ////////////////////////////////////////////////////////////////////////////
  //
  // NotificationChannel
  //
  public static  NotificationChannel notifChannelReference(
                                            org.omg.PortableServer.POA the_poa,
                                            String name )
  {
      org.omg.CORBA.Object channel = the_poa.create_reference_with_id(
                             name.getBytes(), NotificationChannelHelper.id() );
      return NotificationChannelHelper.narrow(channel);
  }

  public static  DistributionChannel distribChannelReference(
                                            org.omg.PortableServer.POA the_poa,
                                            String name )
  {
      org.omg.CORBA.Object channel = the_poa.create_reference_with_id(
                             name.getBytes(), DistributionChannelHelper.id() );
      return DistributionChannelHelper.narrow(channel);
  }

  public static  InternalDistributionChannel internalChannelReference(
                                            org.omg.PortableServer.POA the_poa,
                                            String name,
                                            Policy[] policies)
  {
      org.omg.CORBA.Object channel = the_poa.create_reference_with_id(
                     name.getBytes(), InternalDistributionChannelHelper.id() );
      
      if(policies != null) {
          channel = channel._set_policy_override(policies,
                                             SetOverrideType.SET_OVERRIDE);
      }
      return InternalDistributionChannelHelper.narrow(channel);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // SupplierAdmin
  //
  public static  SupplierAdmin supplierAdminReference(
                                            org.omg.PortableServer.POA the_poa,
                                            String name,
                                            OperationMode operation_mode)
  {
      org.omg.CORBA.Object admin = the_poa.create_reference_with_id(
                   name.getBytes(), 
                   (operation_mode == OperationMode.notification)?
                   SupplierAdminHelper.id() :
                   org.omg.DistributionChannelAdmin.SupplierAdminHelper.id() );
      return SupplierAdminHelper.narrow(admin);
  }

  public static  org.omg.DistributionChannelAdmin.SupplierAdmin 
                   dSupplierAdminReference( org.omg.PortableServer.POA the_poa,
                                            String name )
  {
      org.omg.CORBA.Object admin = the_poa.create_reference_with_id(
                   name.getBytes(), 
                   org.omg.DistributionChannelAdmin.SupplierAdminHelper.id() );
      return org.omg.DistributionChannelAdmin.SupplierAdminHelper.narrow(admin);
  }

  public static InternalSupplierAdmin internalSupplierAdminReference(
                                            org.omg.PortableServer.POA the_poa,
                                            String name,                                            
                                            Policy[] policies)
  {
      org.omg.CORBA.Object admin = the_poa.create_reference_with_id(
                     name.getBytes(), InternalSupplierAdminHelper.id() );
      
      if(policies != null) {
          admin = admin._set_policy_override(policies,
                                             SetOverrideType.SET_OVERRIDE);
      }
     
      return InternalSupplierAdminHelper.narrow(admin);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // ProxyPushConsumer
  //
  public static org.omg.CosEventChannelAdmin.ProxyPushConsumer
      proxyPushConsumerReference( org.omg.PortableServer.POA poa, 
                                  String name,
                                  OperationMode operation_mode )
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               (operation_mode == OperationMode.notification)?
               org.omg.NotificationChannelAdmin.ProxyPushConsumerHelper.id() :
               org.omg.DistributionChannelAdmin.ProxyPushConsumerHelper.id() );
      return
            org.omg.CosEventChannelAdmin.ProxyPushConsumerHelper.narrow(proxy);
  }

  public static org.omg.NotificationChannelAdmin.ProxyPushConsumer
       nProxyPushConsumerReference(org.omg.PortableServer.POA poa,
                                   String name,
                                   OperationMode operation_mode )
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               (operation_mode == OperationMode.notification)?
               org.omg.NotificationChannelAdmin.ProxyPushConsumerHelper.id() :
               org.omg.DistributionChannelAdmin.ProxyPushConsumerHelper.id() );
      return
        org.omg.NotificationChannelAdmin.ProxyPushConsumerHelper.narrow(proxy);
  }

  public static org.omg.DistributionChannelAdmin.ProxyPushConsumer
       dProxyPushConsumerReference(org.omg.PortableServer.POA poa, String name)
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               org.omg.DistributionChannelAdmin.ProxyPushConsumerHelper.id() );
      return
        org.omg.DistributionChannelAdmin.ProxyPushConsumerHelper.narrow(proxy);
  }

  public static InternalProxyPushConsumer
       iProxyPushConsumerReference(org.omg.PortableServer.POA poa, 
                                   String name,
                                   Policy[] policies)
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
                                         InternalProxyPushConsumerHelper.id());
      if(policies != null) {
          proxy = proxy._set_policy_override(policies,
                                             SetOverrideType.SET_OVERRIDE);
      }
      return InternalProxyPushConsumerHelper.narrow(proxy);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // ProxyPullConsumer
  //
  public static org.omg.CosEventChannelAdmin.ProxyPullConsumer
      proxyPullConsumerReference( org.omg.PortableServer.POA poa, String name )
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               org.omg.NotificationChannelAdmin.ProxyPullConsumerHelper.id() );
      return
            org.omg.CosEventChannelAdmin.ProxyPullConsumerHelper.narrow(proxy);
  }

  public static org.omg.NotificationChannelAdmin.ProxyPullConsumer
       nProxyPullConsumerReference(org.omg.PortableServer.POA poa, String name )
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               org.omg.NotificationChannelAdmin.ProxyPullConsumerHelper.id() );
      return
        org.omg.NotificationChannelAdmin.ProxyPullConsumerHelper.narrow(proxy);
  }

  public static InternalProxyPullConsumer
       iProxyPullConsumerReference(org.omg.PortableServer.POA poa, 
                                   String name,
                                   Policy[] policies)
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
                                         InternalProxyPullConsumerHelper.id());
      if(policies != null) {
          proxy = proxy._set_policy_override(policies,
                                             SetOverrideType.SET_OVERRIDE);
      }
      
      return InternalProxyPullConsumerHelper.narrow(proxy);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // ConsumerAdmin
  //
  public static  ConsumerAdmin consumerAdminReference(
                                            org.omg.PortableServer.POA the_poa,
                                            String name, 
                                            OperationMode operation_mode )
  {
      org.omg.CORBA.Object admin = the_poa.create_reference_with_id(
                    name.getBytes(), 
                    (operation_mode == OperationMode.notification)?
                    ConsumerAdminHelper.id() :
             org.omg.IndexedDistributionChannelAdmin.ConsumerAdminHelper.id() );
      return ConsumerAdminHelper.narrow(admin);
  }

  public static  org.omg.DistributionChannelAdmin.ConsumerAdmin 
                   dConsumerAdminReference( org.omg.PortableServer.POA the_poa,
                                            String name )
  {
      org.omg.CORBA.Object admin = the_poa.create_reference_with_id(
                   name.getBytes(), 
             org.omg.IndexedDistributionChannelAdmin.ConsumerAdminHelper.id() );
      return org.omg.DistributionChannelAdmin.ConsumerAdminHelper.narrow(admin);
  }

  public static  InternalConsumerAdmin internalConsumerAdminReference(
                                            org.omg.PortableServer.POA the_poa,
                                            String name,
                                            Policy[] policies)
  {
      org.omg.CORBA.Object admin = the_poa.create_reference_with_id(
                     name.getBytes(), InternalConsumerAdminHelper.id() );
     
      if(policies != null) {
          admin = admin._set_policy_override(policies,
                                             SetOverrideType.SET_OVERRIDE);
      }
      
      return InternalConsumerAdminHelper.narrow(admin);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // ProxyPushSupplier
  //
  public static org.omg.CosEventChannelAdmin.ProxyPushSupplier
      proxyPushSupplierReference( org.omg.PortableServer.POA poa, String name,
                                  OperationMode operation_mode )
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               (operation_mode == OperationMode.notification)?
               org.omg.NotificationChannelAdmin.ProxyPushSupplierHelper.id() :
               org.omg.DistributionChannelAdmin.ProxyPushSupplierHelper.id() );
      return
            org.omg.CosEventChannelAdmin.ProxyPushSupplierHelper.narrow(proxy);
  }

  public static org.omg.NotificationChannelAdmin.ProxyPushSupplier
       nProxyPushSupplierReference(org.omg.PortableServer.POA poa, String name,
                                  OperationMode operation_mode )
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               (operation_mode == OperationMode.notification)?
               org.omg.NotificationChannelAdmin.ProxyPushSupplierHelper.id() :
               org.omg.DistributionChannelAdmin.ProxyPushSupplierHelper.id() );
      return
        org.omg.NotificationChannelAdmin.ProxyPushSupplierHelper.narrow(proxy);
  }

  public static org.omg.DistributionChannelAdmin.ProxyPushSupplier
       dProxyPushSupplierReference(org.omg.PortableServer.POA poa, String name)
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               org.omg.DistributionChannelAdmin.ProxyPushSupplierHelper.id() );
      return
        org.omg.DistributionChannelAdmin.ProxyPushSupplierHelper.narrow(proxy);
   }

  public static InternalProxyPushSupplier
       iProxyPushSupplierReference(org.omg.PortableServer.POA poa, 
                                   String name,
                                   Policy[] policies)
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
                                      InternalProxyPushSupplierHelper.id());
      if(policies != null) {
          proxy = proxy._set_policy_override(policies,
                                             SetOverrideType.SET_OVERRIDE);
      }
      
      return InternalProxyPushSupplierHelper.narrow(proxy);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // ProxyPullSupplier
  //
  public static org.omg.CosEventChannelAdmin.ProxyPullSupplier
      proxyPullSupplierReference( org.omg.PortableServer.POA poa, String name )
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               org.omg.NotificationChannelAdmin.ProxyPullSupplierHelper.id() );
      return
            org.omg.CosEventChannelAdmin.ProxyPullSupplierHelper.narrow(proxy);
  }

  public static org.omg.NotificationChannelAdmin.ProxyPullSupplier
       nProxyPullSupplierReference(org.omg.PortableServer.POA poa, String name)
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
               org.omg.NotificationChannelAdmin.ProxyPullSupplierHelper.id() );
      return
        org.omg.NotificationChannelAdmin.ProxyPullSupplierHelper.narrow(proxy);
  }

  public static InternalProxyPullSupplier
       iProxyPullSupplierReference(org.omg.PortableServer.POA poa, 
                                   String name,
                                   Policy[] policies)
  {
      org.omg.CORBA.Object proxy =
        poa.create_reference_with_id( name.getBytes(),
                                         InternalProxyPullSupplierHelper.id());
      if(policies != null) {
          proxy = proxy._set_policy_override(policies,
                                             SetOverrideType.SET_OVERRIDE);
      }
      return InternalProxyPullSupplierHelper.narrow(proxy);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // Discriminator
  //
  public static Filter discriminatorReference(
                                  org.omg.PortableServer.POA poa, String name )
  {
      org.omg.CORBA.Object discriminator = poa.create_reference_with_id(
                                   name.getBytes(), FilterHelper.id() );
      return FilterHelper.narrow(discriminator);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // IndexLocator
  //
  public static IndexLocator indexLocatorReference(
                                  org.omg.PortableServer.POA poa, String name )
  {
      org.omg.CORBA.Object indexLocator = poa.create_reference_with_id(
                                   name.getBytes(), IndexLocatorHelper.id() );
      return IndexLocatorHelper.narrow(indexLocator);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // MappingDiscriminator
  //
  public static MappingFilter mappingDiscriminatorReference(
                                  org.omg.PortableServer.POA poa, String name )
  {
      org.omg.CORBA.Object mapping_discriminator = 
               poa.create_reference_with_id( name.getBytes(), 
               								 MappingFilterHelper.id() );
      return MappingFilterHelper.narrow(mapping_discriminator);
  }


  ////////////////////////////////////////////////////////////////////////////
  //
  // TransformingOperatore
  //
  public static TransformingOperator transformingOperatorReference(
                              org.omg.PortableServer.POA the_poa, String name )
  {
      org.omg.CORBA.Object operator = the_poa.create_reference_with_id(
                            name.getBytes(), TransformingOperatorHelper.id() );
      return TransformingOperatorHelper.narrow(operator);
  }

  public static InternalTransformingOperator 
                  iTransformingOperatorReference(
                              org.omg.PortableServer.POA the_poa, String name )
  {
      org.omg.CORBA.Object operator = the_poa.create_reference_with_id(
                    name.getBytes(), InternalTransformingOperatorHelper.id() );
      return InternalTransformingOperatorHelper.narrow(operator);
  }
}
