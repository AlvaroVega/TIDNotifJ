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
import org.omg.CORBA.ORB;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.PropertyErrorHolder;
import org.omg.CosNotification.StopTimeSupported;

public class StopTimeSupportedProperty extends QoSProperty
{
    boolean value;    
    
    protected StopTimeSupportedProperty(Property property, 
                              boolean value)
    {
        super(property);
        this.value = value;        
    }
    
    public boolean getValue()
    {
        return value;
    }

    protected static StopTimeSupportedProperty 
    	fromProperty(QoSAdmin admin,
    	             Property property, 
    	             PropertyErrorHolder error)
    	 
    { 
            
        boolean value = property.value.extract_boolean();                
        
        return new StopTimeSupportedProperty(property, value);
        
    }
    
    public void readObject(ObjectInput in)
    throws IOException, ClassNotFoundException
	{        
        Any prop_value = ORB.init().create_any();        
        this.value = in.readBoolean();
        prop_value.insert_boolean(this.value);
        this.property = new Property(StopTimeSupported.value,
                                     prop_value);

	}

	public void writeObject(ObjectOutput out)
	    throws IOException
	{
	    
	    out.writeBoolean(value);	
	}

}
