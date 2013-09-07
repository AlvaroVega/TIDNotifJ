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

public abstract class BaseCriteria implements Serializable
{
  final static public int ADMIN_ONLY             = 0;
  final static public int EXTENDED_CONSUMER      = 1;
  final static public int EXTENDED_SUPPLIER      = 2;
  final static public int EXTENDED_CONSUMER_ONLY = 3;
  final static public int EXTENDED_SUPPLIER_ONLY = 4;

  final static public String NULL_ID = "";
  final static public String NULL_INPUT = "";
  final static public String NULL_REPOSITORY_ID = "";
  
  
  

  // Standard Criteria
  final static public String ID = "Id";
  final static public String INPUT = "Entrada";
  final static public String REPOSITORY_ID = "RepositoryId";
  final static public String PERSISTENT_CONNECTIONS = "Persistent Connections";
  final static public String PERSISTENT_EVENTS = "Persistent Events";
  final static public String INTERFILTER_GROUP_OPERATOR = "Interfilter Group Operator";
  

  static public boolean checkInvalidCriteria( int mode, 
                                      org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    for (int i = 0; i < the_criteria.length; i++) 
    {
      if (the_criteria[i].name.compareTo(ID) == 0)
      {
        if ((mode == EXTENDED_CONSUMER_ONLY) ||
            (mode == EXTENDED_SUPPLIER_ONLY)) return true;
        // OK para ADMIN_ONLY y para ALL_MODES
        continue;
      }
      else if (the_criteria[i].name.compareTo(INPUT) == 0)
      {
        if ((mode == ADMIN_ONLY) ||
            (mode == EXTENDED_CONSUMER) ||
            (mode == EXTENDED_CONSUMER_ONLY)) return true;
        // OK para EXTENDED_SUPPLIER y para ALL_MODES
        continue;
      }
      else if (the_criteria[i].name.compareTo(REPOSITORY_ID) == 0)
      {
        if ((mode == ADMIN_ONLY) ||
            (mode == EXTENDED_SUPPLIER) ||
            (mode == EXTENDED_SUPPLIER_ONLY)) return true;
        // OK para EXTENDED_CONSUMER y para ALL_MODES
        continue;
      }
      else if (the_criteria[i].name.compareTo(PERSISTENT_CONNECTIONS) == 0)
      {
        if ((mode == EXTENDED_CONSUMER_ONLY) ||
            (mode == EXTENDED_SUPPLIER_ONLY)) return true;
        // OK para ADMIN_ONLY y para ALL_MODES
        continue;
      }
      else if (the_criteria[i].name.compareTo(PERSISTENT_EVENTS) == 0)
      {
        if ((mode == EXTENDED_CONSUMER_ONLY) ||
            (mode == EXTENDED_SUPPLIER_ONLY)) return true;
        // OK para ADMIN_ONLY y para ALL_MODES
        continue;
      }
      else 
      {
        return true;
      }
    }
    return false;
  }

  static public org.omg.CosLifeCycle.NVP[] getAdminCriteria( org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    int n = 0;
    java.util.Hashtable _values = new java.util.Hashtable();

    for (int i = 0; i < the_criteria.length; i++) 
    {
      if ((the_criteria[i].name.compareTo(ID) == 0) ||
          (the_criteria[i].name.compareTo(PERSISTENT_CONNECTIONS) == 0) ||
          (the_criteria[i].name.compareTo(PERSISTENT_EVENTS) == 0) )
      {
        _values.put(the_criteria[i].name, the_criteria[i].value);
        n++;
      }
    }
    org.omg.CosLifeCycle.NVP[] values = new org.omg.CosLifeCycle.NVP[n];
    n = 0;
    for ( java.util.Iterator e = _values.entrySet().iterator(); e.hasNext(); )
    {
      java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
      values[n].name = (String)entry.getKey();
      values[n].value = (org.omg.CORBA.Any)entry.getValue();
      n++;
    }
    return values;
  }

  static public org.omg.CosLifeCycle.NVP[] getExtendedSupplierCriteria( org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    int n = 0;
    java.util.Hashtable _values = new java.util.Hashtable();

    for (int i = 0; i < the_criteria.length; i++) 
    {
      if (the_criteria[i].name.compareTo(INPUT) == 0)
      {
        _values.put(the_criteria[i].name, the_criteria[i].value);
        n++;
      }
    }
    org.omg.CosLifeCycle.NVP[] values = new org.omg.CosLifeCycle.NVP[n];
    n = 0;
    for ( java.util.Iterator e = _values.entrySet().iterator(); e.hasNext(); )
    {
      java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
      values[n].name = (String)entry.getKey();
      values[n].value = (org.omg.CORBA.Any)entry.getValue();
      n++;
    }
    return values;
  }

  static public org.omg.CosLifeCycle.NVP[] getExtendedConsumerCriteria( org.omg.CosLifeCycle.NVP[] the_criteria )
  {
    int n = 0;
    java.util.Hashtable _values = new java.util.Hashtable();

    for (int i = 0; i < the_criteria.length; i++) 
    {
      if (the_criteria[i].name.compareTo(REPOSITORY_ID) == 0)
      {
        _values.put(the_criteria[i].name, the_criteria[i].value);
        n++;
      }
    }
    org.omg.CosLifeCycle.NVP[] values = new org.omg.CosLifeCycle.NVP[n];
    n = 0;
    for ( java.util.Iterator e = _values.entrySet().iterator(); e.hasNext(); )
    {
      java.util.Map.Entry entry = (java.util.Map.Entry)e.next();
      values[n].name = (String)entry.getKey();
      values[n].value = (org.omg.CORBA.Any)entry.getValue();
      n++;
    }
    return values;
  }
}
