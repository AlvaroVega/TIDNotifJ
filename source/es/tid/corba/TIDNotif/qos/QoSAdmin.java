/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 112 $
* Date: $Date: 2008-11-14 08:19:26 +0100 (Fri, 14 Nov 2008) $
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

package es.tid.corba.TIDNotif.qos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CosNotification.AnyOrder;
import org.omg.CosNotification.BestEffort;
import org.omg.CosNotification.ConnectionReliability;
import org.omg.CosNotification.DeadlineOrder;
import org.omg.CosNotification.DiscardPolicy;
import org.omg.CosNotification.EventReliability;
import org.omg.CosNotification.HighestPriority;
import org.omg.CosNotification.LowestPriority;
import org.omg.CosNotification.MaxEventsPerConsumer;
import org.omg.CosNotification.MaximumBatchSize;
import org.omg.CosNotification.NamedPropertyRange;
import org.omg.CosNotification.NamedPropertyRangeSeqHolder;
import org.omg.CosNotification.OrderPolicy;
import org.omg.CosNotification.PacingInterval;
import org.omg.CosNotification.Persistent;
import org.omg.CosNotification.Priority;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.PropertyError;
import org.omg.CosNotification.PropertyErrorHolder;
import org.omg.CosNotification.PropertyRange;
import org.omg.CosNotification.QoSAdminOperations;
import org.omg.CosNotification.QoSError_code;
import org.omg.CosNotification.StartTime;
import org.omg.CosNotification.StartTimeSupported;
import org.omg.CosNotification.StopTime;
import org.omg.CosNotification.StopTimeSupported;
import org.omg.CosNotification.Timeout;
import org.omg.CosNotification.UnsupportedQoS;
import org.omg.TimeBase.TimeTHelper;
import org.omg.TimeBase.UtcT;
import org.omg.TimeBase.UtcTHelper;



public abstract class QoSAdmin
    implements QoSAdminOperations, Serializable
{    
    transient ORB orb;
    transient Property[] properties = null;    
    transient Policy[]  policies;
    
    public boolean properties_updated = false;

    final static int EVENT_RELIABILITY = 0;
    final static int CONNECTION_RELIABILITY = 1;
    final static int PRIORITY = 2;    
    final static int START_TIME = 4;
    final static int STOP_TIME = 5;
    final static int TIMEOUT = 6;
    final static int ORDER_POLICY = 7;
    final static int START_TIME_SUPPORTED = 8;
    final static int STOP_TIME_SUPPORTED = 9;
    final static int MAX_EVENTS_PER_CONSUMER = 10;
    final static int DISCARD_POLICY = 11;
    final static int MAXIMUM_BATCH_SIZE = 12;
    final static int PACING_INTERVAL = 13;
    
    
    /* 
     * Smart accessors
     */
    
    EventReliabilityProperty eventReliabilityProperty = null;
    ConnectionReliabilityProperty connectionReliabilityProperty = null;      
    StartTimeSupportedProperty startTimeSupportedProperty = null;
    StopTimeSupportedProperty stopTimeSupportedProperty = null;    
    PriorityProperty   priorityProperty = null;
    StartTimeProperty  startTimeProperty = null;
    StopTimeProperty   stopTimeProperty = null;
    TimeoutProperty    timeoutProperty = null;
    QueueOrderProperty queueOrderProperty = null;
    
    
      
    public static HashMap st_global_supported_properties = 
        initGlobalSupportedProperties();
    
    public static HashMap st_known_properties = 
        initKnownPropertyNames();
    
 
    
    QoSAdmin() {
        properties = null;        
        policies = null;
        this.orb = null;
        properties_updated = false;
    }
    
    QoSAdmin(ORB orb) {
        properties = null;        
        policies = null;
        this.orb = orb;
        properties_updated = false;
    }
    
    public ORB getORB()
    {
        return orb;
    }
    
    public void setORB(ORB orb) {
        this.orb = orb;
    }
    
    
    
    /**
     * @return Returns the connectionReliabilityProperty.
     */
    public ConnectionReliabilityProperty getConnectionReliabilityProperty()
    {
        return connectionReliabilityProperty;
    }
    /**
     * @return Returns the eventReliabilityProperty.
     */
    public EventReliabilityProperty getEventReliabilityProperty()
    {
        return eventReliabilityProperty;
    }
    
    
    /**
     * @return Returns the priorityProperty.
     */
    public PriorityProperty getPriorityProperty()
    {
        return priorityProperty;
    }
    /**
     * @return Returns the queueOrderProperty.
     */
    public QueueOrderProperty getQueueOrderProperty()
    {
        return queueOrderProperty;
    }
    /**
     * @return Returns the startTimeProperty.
     */
    public StartTimeProperty getStartTimeProperty()
    {
        return startTimeProperty;
    }
    /**
     * @return Returns the startTimeSupportedProperty.
     */
    public StartTimeSupportedProperty getStartTimeSupportedProperty()
    {
        return startTimeSupportedProperty;
    }
    /**
     * @return Returns the stopTimeProperty.
     */
    public StopTimeProperty getStopTimeProperty()
    {
        return stopTimeProperty;
    }
    /**
     * @return Returns the stopTimeSupportedProperty.
     */
    public StopTimeSupportedProperty getStopTimeSupportedProperty()
    {
        return stopTimeSupportedProperty;
    }
    /**
     * @return Returns the timeoutProperty.
     */
    public TimeoutProperty getTimeoutProperty()
    {
        return timeoutProperty;
    }
    /***************************************
     * CosNotification::QoSAdmin operatons *
     ***************************************/
    
    
    
    public synchronized Property[] get_qos()
    {   
        if((properties == null || (!properties_updated) )) {

          Vector props = new Vector();
          
          if(eventReliabilityProperty != null) {
              props.add(eventReliabilityProperty);
          }
          
          if(connectionReliabilityProperty != null) {
              props.add(connectionReliabilityProperty);
          }
          
          if(startTimeSupportedProperty != null) {
              props.add(startTimeSupportedProperty);
          }
          if(stopTimeSupportedProperty != null) {
              props.add(stopTimeSupportedProperty);
          }
          if(priorityProperty != null) {
              props.add(priorityProperty);
          }
          if(startTimeProperty != null) {
              props.add(startTimeProperty);
          }
          if(stopTimeProperty != null) {
              props.add(stopTimeProperty);
          }
          
          if(timeoutProperty != null) {
              props.add(timeoutProperty);
          }
          if(queueOrderProperty != null) {
              props.add(queueOrderProperty);
          }
         
          
          properties = new Property[props.size()];
          props.toArray(properties);
          properties_updated = true; 
        }
        
        return properties;
        
    }

    public synchronized void set_qos(Property[] qos)
        throws UnsupportedQoS
    {
        properties = null;
        policies = null;
        properties_updated = false;

        // parse and validate all properties
        
        Vector properties = validate(qos);
        
        commit(properties);
        
    }
    
    
    public synchronized Policy[] getPolicies()
    {   
        if(policies == null) {
          Vector pols = new Vector();
                    
          if(priorityProperty != null) {
              pols.add(priorityProperty.getPolicy(orb));
          }
          if(startTimeProperty != null) {
              pols.add(startTimeProperty.getPolicy(orb));
          }
          if(stopTimeProperty != null) {
              pols.add(stopTimeProperty.getPolicy(orb));
          }
          
          if(timeoutProperty != null) {
              pols.add(timeoutProperty.getPolicy(orb));
          }
          if(queueOrderProperty != null) {
              pols.add(queueOrderProperty.getPolicy(orb));
          }
         
          
          policies = new Policy[pols.size()];
          pols.toArray(policies);
        }
        
        return policies;
        
    }
    
    
    public void validate_qos(Property[] required_qos,
                             NamedPropertyRangeSeqHolder available_qos)
        throws UnsupportedQoS
    {
        validate(required_qos);        
        available_qos.value = getAvailableQoS();
     }
    
    
     
    /*
     * Implementation operations
     */
    
    /**
     * Set deffinitively validated properties
     * @param properties vector of QoSProperty 
     */
    private void commit(Vector properties) {
        int size = properties.size();
        for(int i = 0 ; i < size ; i++) {
            setProperty((QoSProperty) properties.elementAt(i));
        }
        
    }
    
    /**
     * Sets a validaded property     
     * @param property
     */
    
    private void setProperty(QoSProperty property)
    {
        Integer index =(Integer) 
        	st_global_supported_properties.get(property.getProperty().name);
        
        if(index == null) {
            throw new INTERNAL("Unexpected property" +
                               property.getProperty().name);
        }
        
        switch (index.intValue()) {
            case EVENT_RELIABILITY:  
                eventReliabilityProperty = (EventReliabilityProperty) property;
                break;
            case CONNECTION_RELIABILITY:
                connectionReliabilityProperty = 
                    (ConnectionReliabilityProperty) property; 
                break;
            case START_TIME_SUPPORTED:  
                startTimeSupportedProperty = 
                    (StartTimeSupportedProperty) property; 
                break;
            case STOP_TIME_SUPPORTED:
                stopTimeSupportedProperty = 
                    (StopTimeSupportedProperty) property; 
                break;
            case PRIORITY:
                priorityProperty = (PriorityProperty) property; 
                break;            
            case START_TIME:
                startTimeProperty = (StartTimeProperty) property; 
                break;                
            case STOP_TIME:
                stopTimeProperty = (StopTimeProperty) property; 
                break;
            case TIMEOUT:
                timeoutProperty = (TimeoutProperty) property; 
                break;
            case ORDER_POLICY:
                queueOrderProperty = (QueueOrderProperty) property; 
                break;            
        }       
    }
    
    /**
     * Validates and process the Property to QoSProperty list
     * @param qos the QoS properties
     * @return the Processed QoSProperties
     * @throws UnsupportedQoS if any property is not supported
     */
    private Vector validate(Property[] qos) 
    throws UnsupportedQoS
    { 
        // reset values for get_pos & toPolicyList
               
        Vector qos_policies = new Vector();
        
        QoSProperty property = null;
        PropertyErrorHolder error = new PropertyErrorHolder();        
        Vector errors = null;
        
        for (int i = 0; i < qos.length; i++) {
            
            error.value = null;
            
            property = toQoSProperty(qos[i], error);
            
            if(error.value  == null) {  
               qos_policies.add(property);
            } else {
                if(errors == null) {
                    errors = new Vector();
                }
                
                errors.add(error.value);
            }
            
        }
        
        if(errors != null) {
            
            qos_policies = null;
            
            PropertyError[] allErrors = new PropertyError[errors.size()];
            errors.toArray(allErrors);
            throw new UnsupportedQoS(allErrors);
            
        }
        
        return qos_policies;    

    }
    
   
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     
     * @param property 
     * @return error if not supported
     */    
    protected PropertyError validate(EventReliabilityProperty property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */
    protected PropertyError validate(ConnectionReliabilityProperty property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */
    protected PropertyError validate(StartTimeSupportedProperty  property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */ 
    protected PropertyError validate(StopTimeSupportedProperty property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */
    protected PropertyError validate(PriorityProperty property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */
    protected PropertyError validate(StartTimeProperty property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */
    protected PropertyError validate(StopTimeProperty  property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */
    protected PropertyError validate(TimeoutProperty property)
    {
        return null;
    }
    
    /**
     * Validates if this property is supported. By default, all property values
     * are valid. Override these methods if not supported values exist.
     * @param property 
     * @return error if not supported
     */
    protected PropertyError validate(QueueOrderProperty property)
    {
        return null;
    }
    
     
    
    /**
     * Gets the available QoS properties
     * @return
     */
    protected abstract NamedPropertyRange[] getAvailableQoS();
   
   
    
    /**
     * Maps a CosNotification property to a CORBA Messaging policy.
     * @param property the property
     * @param error out holder that could contain an error creating the policy
     * @return null if it has not any policy to map or there has been an error
     *         creating the policy  
     */
    public QoSProperty toQoSProperty(Property property, 
                               PropertyErrorHolder error)
    {
        error.value = null;
        
        Integer index = 
            (Integer) st_global_supported_properties.get(property.name);
        
        if(index == null) {
            error.value = createUnsupportedProperty(property);            
            return null;
        }
        
        switch (index.intValue()) {
            case EVENT_RELIABILITY: 
            {
                EventReliabilityProperty event_rel =  
                    EventReliabilityProperty.fromProperty(this, 
                                                          property, 
                                                          error);
                if(error.value == null) {               
                    validate(event_rel);
                    return event_rel;
                } else {
                    return null;
                }
            }
            case CONNECTION_RELIABILITY:
            {
                ConnectionReliabilityProperty conn_rel =  
                    ConnectionReliabilityProperty.fromProperty(this, 
                                                          property, 
                                                          error);
                if(error.value == null) {               
                    validate(conn_rel);
                    return conn_rel;
                } else {
                    return null;
                }
            }
            case START_TIME_SUPPORTED:  
            {
                StartTimeSupportedProperty time_sup =  
                    StartTimeSupportedProperty.fromProperty(this, 
                                                          property, 
                                                          error);
                if(error.value == null) {               
                    validate(time_sup);
                    return time_sup;
                } else {
                    return null;
                }
            }              
            case STOP_TIME_SUPPORTED:
            {
                StopTimeSupportedProperty time_sup =  
                    StopTimeSupportedProperty.fromProperty(this, 
                                                          property, 
                                                          error);
                if(error.value == null) {               
                    validate(time_sup);
                    return time_sup;
                } else {
                    return null;
                }
            }  
            case PRIORITY:
            {
                PriorityProperty priority =  
                    PriorityProperty.fromProperty(this, 
                                                  property, 
                                                  error);
                if(error.value == null) {               
                    validate(priority);
                    return priority;
                } else {
                    return null;
                }
            }
            case START_TIME:
            {
                StartTimeProperty start_tm =  
                    StartTimeProperty.fromProperty(this, 
                                                  property, 
                                                  error);
                if(error.value == null) {               
                    validate(start_tm);
                    return start_tm;
                } else {
                    return null;
                }
            }    
            case STOP_TIME:
            {
                StopTimeProperty stop_tm =  
                    StopTimeProperty.fromProperty(this, 
                                                  property, 
                                                  error);
                if(error.value == null) {               
                    validate(stop_tm);
                    return stop_tm;
                } else {
                    return null;
                }
            }             
            case TIMEOUT:
            {
                TimeoutProperty timeout =  
                    TimeoutProperty.fromProperty(this, 
                                                 property, 
                                                 error);
                if(error.value == null) {               
                    validate(timeout);
                    return timeout;
                } else {
                    return null;
                }
            }      
            case ORDER_POLICY:
            {
                QueueOrderProperty order =  
                    QueueOrderProperty.fromProperty(this, 
                                                    property, 
                                                    error);
                if(error.value == null) {               
                    validate(order);
                    return order;
                } else {
                    return null;
                }
            }            
        }
        //unreachable
        return null;
        
    }    
    

    /**
    * Create a PropertyRange for Order properties.
    * @return The range with short values (AnyOrder to DeadLineOrder)
    */
    public PropertyRange getOrderRange()
    {
       
        PropertyRange order_range = new PropertyRange();
        order_range.high_val = ORB.init().create_any();
        order_range.low_val = ORB.init().create_any();
        
        order_range.low_val.insert_ushort(AnyOrder.value);
        order_range.high_val.insert_ushort(DeadlineOrder.value);
        
        
        return order_range;
    }
    
    /**
     * Create a PropertyRange for ConnectionReliability properties.
     * @return The range with short values (BestEffort - Persistent)
     */
     public PropertyRange getConnectionReliabilityRange()
     {
         
         PropertyRange reliability_range = new PropertyRange();
         reliability_range.high_val = ORB.init().create_any();
         reliability_range.low_val = ORB.init().create_any();  
         
         reliability_range.low_val.insert_short(BestEffort.value);
         reliability_range.high_val.insert_short(Persistent.value);                 
     
         return reliability_range;                 
        
     }
     
     /**
      * Create a PropertyRange for Event properties.
      * @return The range with short values (BestEffort - Persistent)
      */
      public PropertyRange getEventReliabilityRange()
      {
          
          PropertyRange reliability_range = new PropertyRange();
          reliability_range.high_val = ORB.init().create_any();
          reliability_range.low_val = ORB.init().create_any();  
          
          reliability_range.low_val.insert_short(BestEffort.value);
          reliability_range.high_val.insert_short(Persistent.value);                 
      
          return reliability_range;                 
         
      }
    
    
    /**
     * Create a PropertyRange for time properties.
     * @return The range with TimeBase::UtcT values
     */
    public  PropertyRange getTimeRange()
    {
        
        PropertyRange time_range = new PropertyRange();
        time_range.high_val = ORB.init().create_any();
        time_range.low_val = ORB.init().create_any();
        
        
        UtcT time = new UtcT();
        
        time.time = 0L;
        
        UtcTHelper.insert(time_range.low_val, time);          
                        
        // TimeT is defined in IDL as unsigned long long. In Java
        // equivalent type is long (signed), and this value must be
        // assumed by the user as unsigned
        time.time = 0xffffffffffffffffL;
        
        UtcTHelper.insert(time_range.high_val, time);
        
        return time_range;
    }
    
    /**
     * Create a PropertyRange for the Priority property.
     * @return The range with unsigned short values
     */
    public PropertyRange getPriorityRange()
    {
       
        PropertyRange priority_range = new PropertyRange();
        priority_range.high_val = ORB.init().create_any();
        priority_range.low_val = ORB.init().create_any();
        
        priority_range.high_val.insert_short(HighestPriority.value);
        priority_range.low_val.insert_short(LowestPriority.value);
        
        return priority_range;
    }
    
    /**
     * Create a null PropertyRange for PropertyErrors.
     * @param property
     * @return The range with null Any values
     */
    public PropertyRange getBooleanRange()
    { 
        PropertyRange bool_range = new PropertyRange();
        bool_range.high_val = ORB.init().create_any();
        bool_range.low_val = ORB.init().create_any();  
        
        bool_range.high_val.insert_boolean(true);
        bool_range.low_val.insert_boolean(false);
        
        return bool_range;
    }
    
    /**
     * Create a null PropertyRange for PropertyErrors.
     * @param property
     * @return The range with null Any values
     */
    public static PropertyRange getNullRange()
    {        
        PropertyRange null_range = new PropertyRange();
        null_range.high_val = ORB.init().create_any();
        null_range.low_val = ORB.init().create_any();               
       
        return null_range;
    }
    
    /**
     * Create a PropertyRange for timeouts. Timeout type is TimeBase::TimeT,
     * and the units are 100 ns.
     * @return
     */
    public PropertyRange getTimeoutRange()
    {       
        PropertyRange timeout_range = new PropertyRange();
        timeout_range.high_val = ORB.init().create_any();
        timeout_range.low_val = ORB.init().create_any();
        
        // TimeT is defined in IDL as unsigned long long. In Java
        // equivalent type is long (signed), and this value must be
        // assumed by the user as unsigned
            
            TimeTHelper.insert(timeout_range.high_val, 
                               0xffffffffffffffffL);
            timeout_range.low_val.insert_ulonglong(0L);
   
        
        return timeout_range;
    }

    
    public static PropertyError createPropertyError(Property property) {
        PropertyError error = new PropertyError();
    	error.code = QoSError_code.BAD_VALUE;
    	error.name = property.name;
    	error.available_range = getNullRange(); 
    	return error; 
    }
    
    public PropertyError createBadProperty(Property property) {
        PropertyError error = createPropertyError(property);
        error.code = QoSError_code.BAD_PROPERTY;
        return error;
    }
    
    public PropertyError createUnsupportedProperty(Property property) {
        PropertyError error = createPropertyError(property);
        error.code = QoSError_code.UNSUPPORTED_PROPERTY;
        return error;
    }

    /**
     * @return
     */
    private static HashMap initGlobalSupportedProperties()
    {
        HashMap map = new HashMap();
        
        map.put(EventReliability.value, new Integer(EVENT_RELIABILITY));
        map.put(ConnectionReliability.value, new Integer(CONNECTION_RELIABILITY));
        map.put(Priority.value, new Integer(PRIORITY));
        map.put(StartTime.value, new Integer(START_TIME));
        map.put(StopTime.value, new Integer(STOP_TIME));
        map.put(Timeout.value, new Integer(TIMEOUT));        
        map.put(OrderPolicy.value, new Integer(ORDER_POLICY));
        map.put(StopTime.value, new Integer(STOP_TIME));
        map.put(StartTimeSupported.value, new Integer(START_TIME_SUPPORTED));
        map.put(StopTimeSupported.value, new Integer(STOP_TIME_SUPPORTED));
        
        return map;
        
    }
    
    
    
    /**
     * @return
     */
    private static HashMap initKnownPropertyNames()
    {
        HashMap map = new HashMap();
        
        map.put(EventReliability.value,
                new Integer(EVENT_RELIABILITY));
        map.put(ConnectionReliability.value, 
                new Integer(CONNECTION_RELIABILITY));
        map.put(Priority.value, 
                new Integer(PRIORITY));
        map.put(StartTime.value, 
                new Integer(START_TIME));
        map.put(StopTime.value, 
                new Integer(STOP_TIME));
        map.put(Timeout.value, 
                new Integer(TIMEOUT));        
        map.put(OrderPolicy.value, 
                new Integer(ORDER_POLICY));
        map.put(StopTime.value, 
                new Integer(STOP_TIME));
        map.put(StartTimeSupported.value, 
                new Integer(START_TIME_SUPPORTED));
        map.put(StopTimeSupported.value, 
                new Integer(STOP_TIME_SUPPORTED));
        
        map.put(MaxEventsPerConsumer.value, 
                new Integer(MAX_EVENTS_PER_CONSUMER));
        map.put(DiscardPolicy.value, 
                new Integer(DISCARD_POLICY));
        map.put(MaximumBatchSize.value, 
                new Integer(MAXIMUM_BATCH_SIZE));
        map.put(PacingInterval.value, 
                new Integer(PACING_INTERVAL));
     
        return map;
        
    }

    

   
}
