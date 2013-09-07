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

import java.io.*;

import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperator;
import org.omg.CosNotifyChannelAdmin.InterFilterGroupOperatorHelper;

public class AdminCriteria extends BaseCriteria implements Serializable
{
  private String _id = NULL_ID;
  private String _input = NULL_INPUT;
  private String _repository_id = NULL_REPOSITORY_ID;
  private boolean _persistent_connections = true;
  private boolean _persistent_events = false;
  private InterFilterGroupOperator _interfilter_group_operator;

  // Constructors
  public AdminCriteria()
  {
  }
  
  public AdminCriteria(String name)
  {
    _id = new String(name);
  }
  
  public AdminCriteria( AdminCriteria criteria )
  {
    if (criteria != null)
    {
      _id = new String (criteria._id);
      _input = new String (criteria._input);
      _repository_id = new String (criteria._repository_id);
      _persistent_connections = criteria._persistent_connections;
      _persistent_events = criteria._persistent_events;
    }
  }
  
  public AdminCriteria( int mode,
                        org.omg.CosLifeCycle.NVP[] the_criteria )
                     throws org.omg.NotificationChannelAdmin.InvalidCriteria
  {
    if (the_criteria == null)
    {
      return;
    }

    if (the_criteria.length == 0)
    {
      return;
    }

    // First Check for "Invalid Criteria"
    if ( AdminCriteria.checkInvalidCriteria(mode, the_criteria) )
    {
      throw new org.omg.NotificationChannelAdmin.InvalidCriteria(the_criteria);
    }
    setCriteriaValues(the_criteria);
  }

  public int compareTo( AdminCriteria criteria )
  {
    if ( (_id.compareTo(criteria._id) == 0) &&
         (_input.compareTo(criteria._input) == 0) &&
         (_repository_id.compareTo(criteria._repository_id) == 0) &&
         (_persistent_connections == criteria._persistent_connections) &&
         (_persistent_events == criteria._persistent_events) )
    {
      return 0;
    }
    else
    {
      return 1;
    }
  }

  public int compareTo( org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    for (int i = 0; i < the_criteria.length; i++) 
    {
      if (the_criteria[i].name.compareTo(ID) == 0)
      {
        if (_id.compareTo(the_criteria[i].value.extract_string()) != 0)
        {
          return 1;
        }
      }
      else if (the_criteria[i].name.compareTo(INPUT) == 0)
      {
        if (_input.compareTo(the_criteria[i].value.extract_string()) != 0)
        {
          return 1;
        }
      }
      else if (the_criteria[i].name.compareTo(REPOSITORY_ID) == 0)
      {
        if (_repository_id.compareTo(the_criteria[i].value.extract_string())!=0)
        {
          return 1;
        }
      }
      else if (the_criteria[i].name.compareTo(PERSISTENT_CONNECTIONS) == 0)
      {
        if (_persistent_connections != the_criteria[i].value.extract_boolean())
        {
          return 1;
        }
      }
      else if (the_criteria[i].name.compareTo(PERSISTENT_EVENTS) == 0)
      {
        if (_persistent_events != the_criteria[i].value.extract_boolean())
        {
          return 1;
        }
      }
      // Venga el Criterio que venga, devolvemos false
      else 
      {
        return 1;
      }
    }
    return 0;
  }

  public org.omg.CosLifeCycle.NVP[] elements(int mode)
  {
    org.omg.CosLifeCycle.NVP[] values = null;
    org.omg.CORBA.ORB _orb = org.omg.CORBA.ORB.init();
    int i = 0;

    //All incremented because of INTERFILTER_GROUP_OPERATOR
    if (mode == ADMIN_ONLY)
      values = new org.omg.CosLifeCycle.NVP[4];
    else if (mode == EXTENDED_SUPPLIER_ONLY)
      values = new org.omg.CosLifeCycle.NVP[2];
    else if (mode == EXTENDED_CONSUMER_ONLY)
      values = new org.omg.CosLifeCycle.NVP[3];
    else 
      values = new org.omg.CosLifeCycle.NVP[7];

    if ((mode != EXTENDED_SUPPLIER_ONLY)&&(mode != EXTENDED_CONSUMER_ONLY))
    {
      org.omg.CORBA.Any value = _orb.create_any();
      value.insert_string(_id);
      values[i] = new org.omg.CosLifeCycle.NVP( ID, value );
      i++;
    }

    if ( (mode != ADMIN_ONLY) &&
         (mode != EXTENDED_CONSUMER) &&
         (mode != EXTENDED_CONSUMER_ONLY) )
    {
      org.omg.CORBA.Any value = _orb.create_any();
      value.insert_string(_input);
      values[i] = new org.omg.CosLifeCycle.NVP( INPUT, value );
      i++;
    }

    if ( (mode != ADMIN_ONLY) &&
         (mode != EXTENDED_SUPPLIER) &&
         (mode != EXTENDED_SUPPLIER_ONLY) )
    {
      org.omg.CORBA.Any value = _orb.create_any();
      value.insert_string(_repository_id);
      values[i] = new org.omg.CosLifeCycle.NVP( REPOSITORY_ID, value );
      i++;
    }

    if ((mode != EXTENDED_SUPPLIER_ONLY) && (mode != EXTENDED_CONSUMER_ONLY))
    {
      org.omg.CORBA.Any value = _orb.create_any();
      value.insert_boolean(_persistent_connections);
      values[i] = new org.omg.CosLifeCycle.NVP( PERSISTENT_CONNECTIONS, value );
      i++;
      value = _orb.create_any();
      value.insert_boolean(_persistent_events);
      values[i] = new org.omg.CosLifeCycle.NVP( PERSISTENT_EVENTS, value );
      i++;
    }

    org.omg.CORBA.Any value = _orb.create_any();
    InterFilterGroupOperatorHelper.insert( value, _interfilter_group_operator );
    values[i] = new org.omg.CosLifeCycle.NVP( INTERFILTER_GROUP_OPERATOR , value );
    i++;
    

    return values;
  }

  public int updateExtendedSupplierCriteria( org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    if (checkInvalidCriteria(EXTENDED_SUPPLIER_ONLY, the_criteria))
    {
      return -1;
    }
    setCriteriaValues(the_criteria);
    return 0;
  }

  public int updateExtendedConsumerCriteria( org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    if (checkInvalidCriteria(EXTENDED_CONSUMER_ONLY, the_criteria))
    {
      return -1;
    }
    setCriteriaValues(the_criteria);
    return 0;
  }

  private void setCriteriaValues ( org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    for (int i = 0; i < the_criteria.length; i++) 
    {
      if (the_criteria[i].name.compareTo(ID) == 0)
      {
        _id = new String(the_criteria[i].value.extract_string());
      }
      else if (the_criteria[i].name.compareTo(INPUT) == 0)
      {
        _input = new String(the_criteria[i].value.extract_string());
      }
      else if (the_criteria[i].name.compareTo(REPOSITORY_ID) == 0)
      {
        _repository_id = new String(the_criteria[i].value.extract_string());
      }
      else if (the_criteria[i].name.compareTo(PERSISTENT_CONNECTIONS) == 0)
      {
        _persistent_connections = the_criteria[i].value.extract_boolean();
      }
      else if (the_criteria[i].name.compareTo(PERSISTENT_EVENTS) == 0)
      {
        _persistent_events = the_criteria[i].value.extract_boolean();
      } else if ( the_criteria[ i ].name.equals( INTERFILTER_GROUP_OPERATOR ) ) {
      	_interfilter_group_operator = InterFilterGroupOperatorHelper.extract( the_criteria[i].value );
      }
      
    }
  }

  public String id()
  {
    return _id;
  }

  public String name()
  {
    return _input;
  }

  public String repository_id()
  {
    return _repository_id;
  }

  public boolean persistent_connections()
  {
    return _persistent_connections;
  }

  public boolean persistent_events()
  {
    return _persistent_events;
  }
  
  public InterFilterGroupOperator interfilter_group_operator(){
  	
  	return (_interfilter_group_operator!=null)?_interfilter_group_operator:InterFilterGroupOperator.AND_OP;
  }
  
}
