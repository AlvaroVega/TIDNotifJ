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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.omg.CORBA.Any;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.PolicyError;
import org.omg.CosNotification.AnyOrder;
import org.omg.CosNotification.DeadlineOrder;
import org.omg.CosNotification.FifoOrder;
import org.omg.CosNotification.OrderPolicy;
import org.omg.CosNotification.PriorityOrder;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.PropertyError;
import org.omg.CosNotification.PropertyErrorHolder;
import org.omg.CosNotification.QoSError_code;
import org.omg.Messaging.ORDER_ANY;
import org.omg.Messaging.ORDER_DEADLINE;
import org.omg.Messaging.ORDER_PRIORITY;
import org.omg.Messaging.ORDER_TEMPORAL;
import org.omg.Messaging.QUEUE_ORDER_POLICY_TYPE;
import org.omg.Messaging.QueueOrderPolicy;
import org.omg.Messaging.QueueOrderPolicyHelper;

public class QueueOrderProperty extends QoSProperty
{
    short value;    
    QueueOrderPolicy policy;
    
    protected QueueOrderProperty(Property property, 
                                 QueueOrderPolicy policy,
                                 short value)
    {
        super(property);
        this.value = value;  
        this.policy = policy;
    }
    
    public short getValue()
    {
        return value;
    }

    protected static QueueOrderProperty 
    	fromProperty(QoSAdmin admin,
    	             Property property, 
    	             PropertyErrorHolder error)
    	 
    {    
        ORB orb = admin.getORB();
        
        try {
	        short order = property.value.extract_short();
	            
	        
	        short order_value = 0;
	        
	        switch(order) {
	            case AnyOrder.value:
	                order_value = ORDER_ANY.value;
	            	break;
	            case FifoOrder.value:
	                order_value = ORDER_TEMPORAL.value;
	        	break;
	            case PriorityOrder.value:
	                order_value = ORDER_PRIORITY.value;
	            break;
	            case DeadlineOrder.value:
	                order_value = ORDER_DEADLINE.value;
	            break;
	            default:
	                error.value = new PropertyError();
	            	error.value.code = QoSError_code.BAD_VALUE;
	            	error.value.name = property.name;
	            	error.value.available_range = admin.getOrderRange(); 
	            	return null;
	        }
	        
	        Any policy_value = orb.create_any();
	        
	        policy_value.insert_ushort(order_value);        
       
	        QueueOrderPolicy policy =
	            QueueOrderPolicyHelper.narrow(
                orb.create_policy(QUEUE_ORDER_POLICY_TYPE.value, 
                                  policy_value));
	        
            return new QueueOrderProperty(property,
                                          policy, 
                                          order);
        }
        catch (Throwable e) {            
            error.value = QoSAdmin.createPropertyError(property);
        	error.value.code = QoSError_code.BAD_VALUE;        	
        	error.value.available_range = admin.getOrderRange(); 
        	return null;            
        }            
   
        
        
    }
    
    public QueueOrderPolicy getPolicy(ORB orb)
    {
        if(policy == null) {
	        try {
		        switch(value) {
		            case AnyOrder.value:
		            case FifoOrder.value:
		            case PriorityOrder.value:
		            case DeadlineOrder.value:
		            break;
		            default:
		                throw new INTERNAL();
		        }
		        
	            Any policy_value = orb.create_any();
		        
		        policy_value.insert_ushort(value);        
	       
	            
	            this.policy =  
	                QueueOrderPolicyHelper.narrow(
	                   orb.create_policy(QUEUE_ORDER_POLICY_TYPE.value, 
	                                     policy_value)
	                );
	        }
	        catch (PolicyError e) {
	            throw new INTERNAL();
	        }  
        }
        return this.policy;
    }
    
    public void readObject(ObjectInput in)
    throws IOException, ClassNotFoundException
	{
        Any prop_value = ORB.init().create_any();        
        this.value = in.readShort();
        prop_value.insert_short(this.value);
        this.property = new Property(OrderPolicy.value,
                                     prop_value);
        this.policy = null;
	
	
	}

	public void writeObject(ObjectOutput out)
	    throws IOException
	{
	    out.writeShort(value);	
	}

}
