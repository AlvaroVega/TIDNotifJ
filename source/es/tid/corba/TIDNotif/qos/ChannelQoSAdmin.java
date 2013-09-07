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

package es.tid.corba.TIDNotif.qos;

import org.omg.CORBA.ORB;
import org.omg.CosNotification.BestEffort;
import org.omg.CosNotification.ConnectionReliability;
import org.omg.CosNotification.EventReliability;
import org.omg.CosNotification.NamedPropertyRange;
import org.omg.CosNotification.Persistent;
import org.omg.CosNotification.Priority;
import org.omg.CosNotification.PropertyError;
import org.omg.CosNotification.PropertyRange;
import org.omg.CosNotification.StartTimeSupported;
import org.omg.CosNotification.StopTimeSupported;
import org.omg.CosNotification.Timeout;

public class ChannelQoSAdmin extends QoSAdmin
{   
    
    public static NamedPropertyRange[] st_available_properties = null;
    
    /**
     * @param orb
     */
    public ChannelQoSAdmin(ORB orb)
    {
        super(orb);
        
    }
    
    
    /** 
     * Overrided method. Property not supported 
     * for this kind of Notification elements
     * @see es.tid.corba.TIDNotif.qos.QoSAdmin#validate(es.tid.corba.TIDNotif.qos.StartTimeProperty)
     */
    protected PropertyError validate(StartTimeProperty property)
    {        
        return createBadProperty(property.getProperty());
    }
    
    /** 
     * Overrided method. Property not supported 
     * for this kind of Notification elements
     * @see es.tid.corba.TIDNotif.qos.QoSAdmin#validate(es.tid.corba.TIDNotif.qos.StopTimeProperty)
     */
    protected PropertyError validate(StopTimeProperty property)
    {       
        return createBadProperty(property.getProperty());
    } 
    

    /**
     * Validates if this property is supported. By default, all property values
     * are valid. 
     
     * @param property 
     * @return error if not supported
     */    
    protected PropertyError validate(EventReliabilityProperty property)
    {
        if(property.value == Persistent.value) {
           return this.createUnsupportedProperty(property.getProperty()); 
        }
        
        return null;
    }
    

    /* (non-Javadoc)
     * @see es.tid.corba.TIDNotif.qos.QoSAdmin#getAvailableQoS()
     */
    protected NamedPropertyRange[] getAvailableQoS()
    {
        synchronized (ChannelQoSAdmin.class) {
            if(st_available_properties == null) {                
                
                NamedPropertyRange[] tmp = 
                {
                  new NamedPropertyRange(EventReliability.value, 
                                         getEventReliabilityRange()),
                  new NamedPropertyRange(ConnectionReliability.value, 
                                        getConnectionReliabilityRange()),
                  new NamedPropertyRange(Priority.value, 
                                        getPriorityRange()),
                  new NamedPropertyRange(Timeout.value, 
                                        getTimeoutRange()),
                  new NamedPropertyRange(StartTimeSupported.value, 
                                         getBooleanRange()),                      
                  new NamedPropertyRange(StopTimeSupported.value, 
                                        getBooleanRange())
                };		
        	       
                
                st_available_properties = tmp; 
            }
            
        }
        
        return st_available_properties;
    }
       

    /**
     * Create the EventReliability property. Only BestEfford is available in the
     * current version of TIDNotif.
     * @return
     */
    public PropertyRange getEventReliabilityRange()
    {
        PropertyRange event_reliability = super.getEventReliabilityRange();               
        
        event_reliability.high_val.insert_short(BestEffort.value); 
        
        return event_reliability;
    }   

}
