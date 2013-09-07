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

public interface PersistenceDB
{
  // Operations
  //public final static int SAVE   = 0;
  //public final static int UPDATE = 1;
  //public final static int LOAD   = 2;
  //public final static int REMOVE = 3;

  //
  // Common
  public final static int STATE          = 0;
  //
  // Input
  public final static int OBJ_CHANNEL               = 0;
  public final static int OBJ_SUPPLIER_ADMIN        = 1;
  public final static int OBJ_PROXY_PUSH_CONSUMER   = 2;
  public final static int OBJ_PROXY_PULL_CONSUMER   = 3;
  public final static int OBJ_CONSUMER_ADMIN        = 4;
  public final static int OBJ_PROXY_PUSH_SUPPLIER   = 5;
  public final static int OBJ_PROXY_PULL_SUPPLIER   = 6;
  public final static int OBJ_DISCRIMINATOR         = 7;
  public final static int OBJ_INDEXLOCATOR          = 8;
  public final static int OBJ_TRANSFORMING_OPERATOR = 9;
  public final static int OBJ_MAPPING_DISCRIMINATOR = 10;

  public final static int ATTR_ADMINISTRATIVE_STATE  = 0;
  public final static int ATTR_DISCRIMINATOR         = 1;
  public final static int ATTR_INDEXLOCATOR          = 2;
  public final static int ATTR_MAPPING_DISCRIMINATOR = 3;
  public final static int ATTR_D_ERROR_HANDLER       = 4;
  public final static int ATTR_ORDER                 = 5;
  public final static int ATTR_ORDER_GAP             = 6;
  public final static int ATTR_TRANSFORMING_OPERATOR = 7;
  public final static int ATTR_T_ERROR_HANDLER       = 8;
  public final static int ATTR_SUPPLIER_ADMIN        = 9;
  public final static int ATTR_SUPPLIER_ADMIN_PARENT = 10;
  public final static int ATTR_SUPPLIER_ADMIN_CHILD  = 11;
  public final static int ATTR_PROXY_PUSH_CONSUMER   = 12;
  public final static int ATTR_PROXY_PULL_CONSUMER   = 13;
  public final static int ATTR_CONSUMER_ADMIN        = 14;
  public final static int ATTR_CONSUMER_ADMIN_PARENT = 15;
  public final static int ATTR_PROXY_PUSH_SUPPLIER   = 16;
  public final static int ATTR_PROXY_PULL_SUPPLIER   = 17;
  public final static int ATTR_EXTENDED_CRITERIA     = 18;
  public final static int ATTR_PUSH_SUPPLIER         = 19;
  public final static int ATTR_PULL_SUPPLIER         = 20;
  public final static int ATTR_PUSH_CONSUMER         = 21;
  public final static int ATTR_PULL_CONSUMER         = 22;

  public final static int ATTR_SUPPLIER_ADMIN_TABLES = 23;
  public final static int ATTR_PUSH_CONSUMER_TABLE   = 24;
  public final static int ATTR_PULL_CONSUMER_TABLE   = 25;
  public final static int ATTR_CONSUMER_ADMIN_TABLES = 26;
  public final static int ATTR_PUSH_SUPPLIER_TABLE   = 27;
  public final static int ATTR_PULL_SUPPLIER_TABLE   = 28;
  public final static int ATTR_CONSTRAINTS           = 29;
  public final static int ATTR_TRANSFORMING_RULES    = 30;

  public final static int ATTR_DEFAULT_PRIORITY      = 31;
  public final static int ATTR_DEFAULT_LIFETIME      = 32;
  public final static int ATTR_LEVEL                 = 33;

  // type
  public final static int PUSH = 0;
  public final static int PULL = 1;
 

  //
  // Operations
  //
  public void save(NotificationChannelData data);
  public void update(int what, NotificationChannelData data);
  public void delete(NotificationChannelData data);
  public NotificationChannelData load_c(String name) throws java.lang.Exception;
  public java.util.ArrayList load_channels();

  public void save(SupplierAdminData data);
  public void update(int what, SupplierAdminData data);
  public void delete(SupplierAdminData data);
  public SupplierAdminData load_sa(String name) throws java.lang.Exception;

  public void save(ConsumerAdminData data);
  public void update(int what, ConsumerAdminData data);
  public void delete(ConsumerAdminData data);
  public ConsumerAdminData load_ca(String name) throws java.lang.Exception;

  public void save(ProxyPushConsumerData data);
  public void update(int what, ProxyPushConsumerData data);
  public void delete(ProxyPushConsumerData data);
  public ProxyPushConsumerData load_ppushc(String name) throws java.lang.Exception;

  public void save(ProxyPushSupplierData data);
  public void update(int what, ProxyPushSupplierData data);
  public void delete(ProxyPushSupplierData data);
  public ProxyPushSupplierData load_ppushs(String name) throws java.lang.Exception;

  public void save(ProxyPullConsumerData data);
  public void update(int what, ProxyPullConsumerData data);
  public void delete(ProxyPullConsumerData data);
  public ProxyPullConsumerData load_ppullc(String name) throws java.lang.Exception;

  public void save(ProxyPullSupplierData data);
  public void update(int what, ProxyPullSupplierData data);
  public void delete(ProxyPullSupplierData data);
  public ProxyPullSupplierData load_ppulls(String name) throws java.lang.Exception;

  public void save(FilterData data);
  public void update(int what, FilterData data);
  public void delete(FilterData data);
  public FilterData load_d(String name) throws java.lang.Exception;

  public void save(IndexLocatorData data);
  public void update(int what, IndexLocatorData data);
  public void delete(IndexLocatorData data);
  public IndexLocatorData load_l(String name) throws java.lang.Exception;

  public void save(MappingFilterData data);
  public void update(int what, MappingFilterData data);
  public void delete(MappingFilterData data);
  public MappingFilterData load_md(String name) throws java.lang.Exception;

  public void save(TransformingOperatorData data);
  public void update(int what, TransformingOperatorData data);
  public void delete(TransformingOperatorData data);
  public TransformingOperatorData load_to(String name) throws java.lang.Exception;

  public String getUID() throws java.lang.Exception;

/*
  // definimos el interfaz para acceder a la base de datos
  public void removeEvents(int entryOrder, String proxyOID);
  public void removeProxyConsumers( int proxyID, int proxyType,
                                    int adminID, int channelID );
  public void removeProxySuppliers( int proxyID, int proxyType,
                                    int adminID, int channelID );
  public void removeMappingEvents(int mappingRuleID, int mappingDiscriminatorID);

  // +
  public void removeConstraints(int constraintID, int discriminatorID);

  public void removeConsumerAdmins(int adminID, int channelID);
  public void removeSupplierAdmins(int adminID, int channelID);
  public void removeChannels(int channelID);

  public boolean check_dbID();
  public void save_dbID();
  public java.util.Properties loadParams();
  public void saveParams();
  public void updateParams();
  public void deleteParams();
  public java.util.LinkedList loadChannels();
  public java.util.LinkedList loadSupplierAdmins(String channel_id);
  public java.util.LinkedList loadConsumerAdmins(String channel_id);
  //public es.tid.corba.TIDNotif.data.ConstraintData loadConstrait(int constraint_id);
  public java.util.List loadMappingRules(int id);
  public java.util.LinkedList loadProxySuppliers( int type,
                                                  String channel_id,
                                                  String admin );
  public java.util.LinkedList loadProxyConsumers( int type,
                                                  String channel_id,
                                                  String admin );
  public java.util.List loadEvents();
  public boolean save(int op, NotificationChannelImpl channel);
  public boolean save(int op, ConsumerAdminImpl admin);
  public boolean save(int op, SupplierAdminImpl admin);
  public boolean save(int op, int discriminatorId, int constraintId,
                      java.lang.String constraint);

  // MappingDiscriminator
  public boolean save( int op, int mappingDiscriminatorId, 
                       org.omg.CORBA.Any value );

  // MappingRule
  public boolean save(int op, int mappingDiscriminatorId, int mappingRuleId,
                      java.lang.String constraint, org.omg.CORBA.Any value);

  public boolean save(int op, ProxyPushConsumerImpl proxy);
  public boolean save(int op, ProxyPullConsumerImpl proxy);

  public boolean save(int op, ProxyPushSupplierImpl proxy);
  public boolean save(int op, ProxyPullSupplierImpl proxy);

  public boolean remove(ConsumerAdminImpl admin);
  public boolean remove(SupplierAdminImpl admin);

  public boolean remove(DiscriminatorImpl admin);
  public boolean remove(MappingDiscriminatorImpl admin);

  public void removeMappingRules(int mappingRuleId, int mappingDiscriminatorId);

  public boolean remove(ProxyPushSupplierImpl proxy);
  public boolean remove(ProxyPullSupplierImpl proxy);

  public boolean remove(ProxyPushConsumerImpl proxy);
  public boolean remove(ProxyPullConsumerImpl proxy);

  public org.omg.CORBA.Any loadDefaultValue(int id);
*/
}
