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
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.PropertyErrorHolder;
import org.omg.CosNotification.QoSError_code;
import org.omg.CosNotification.StartTime;
import org.omg.Messaging.REQUEST_START_TIME_POLICY_TYPE;
import org.omg.Messaging.RequestStartTimePolicy;
import org.omg.Messaging.RequestStartTimePolicyHelper;
import org.omg.TimeBase.UtcT;
import org.omg.TimeBase.UtcTHelper;

public class StartTimeProperty extends QoSProperty
{
    UtcT value; 
    RequestStartTimePolicy policy;
    
    protected StartTimeProperty(Property property, 
                              RequestStartTimePolicy policy,
                              UtcT value)
    {
        super(property);
        this.value = value;   
        this.policy = policy;
    }
    
    public UtcT getValue()
    {
        return value;
    }

    protected static StartTimeProperty 
    	fromProperty(QoSAdmin admin,
    	             Property property, 
    	             PropertyErrorHolder error)
    	 
    {  
        ORB orb = admin.getORB();
        
        try {
                
           Policy policy =  orb.create_policy(REQUEST_START_TIME_POLICY_TYPE.value,
                                              property.value);
           
           RequestStartTimePolicy start_policy =
               RequestStartTimePolicyHelper.narrow(policy);
           
           return new StartTimeProperty(property,
                                      start_policy, 
                                      start_policy.start_time());
        }
        catch (Throwable e) {                
            error.value = QoSAdmin.createPropertyError(property);
        	error.value.code = QoSError_code.BAD_VALUE;            	
        	error.value.available_range = admin.getTimeRange(); 
        	return null; 
        }
   
    }
    
    public synchronized RequestStartTimePolicy getPolicy(ORB orb)
    {
        if (policy == null) {
	        try {
	            policy = RequestStartTimePolicyHelper.narrow(
	              orb.create_policy(REQUEST_START_TIME_POLICY_TYPE.value,
	                                     property.value)
	            );
	        }
	        catch (PolicyError e) {
	            throw new INTERNAL();
	        }        
        }
        return policy;
    }
    
    public void readObject(ObjectInput in)
    throws IOException, ClassNotFoundException
	{
        this.value = new UtcT();
        Any prop_value = ORB.init().create_any();        
        this.value.time = in.readLong();
        UtcTHelper.insert(prop_value, this.value);
        this.property = new Property(StartTime.value,
                                     prop_value);
        this.policy = null;
	
	
	}

	public void writeobject(ObjectOutput out)
	    throws IOException
	{
	    
	    out.writeLong(value.time);	
	}

}
