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

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
//import es.tid.corba.util.notif.TIDNotifConfig;

import org.omg.IndexAdmin.*;

import org.omg.IndexAdmin.IndexType;

import es.tid.util.parser.TIDParser;
import es.tid.util.parser.TypeValuePair;

public class IndexLocatorImpl extends org.omg.IndexAdmin.IndexLocatorPOA implements IndexLocatorMsg
{
  private org.omg.CORBA.ORB _orb;
  private org.omg.PortableServer.POA _agentPOA;

  private String _grammar;

  // Default Servant Current POA
  private org.omg.PortableServer.Current _current;

  // List of IndexLocators (Pack)
  private java.util.Hashtable _index_locators_table;

  //
  // Constructors
  //
  public IndexLocatorImpl( org.omg.CORBA.ORB orb,
                            org.omg.PortableServer.POA agent_poa )
  {
    _orb = orb;
    _agentPOA = agent_poa;
    _index_locators_table = new java.util.Hashtable();
    _grammar = TIDParser._CONSTRAINT_GRAMMAR; // set the default _grammar
  } 

  public IndexLocatorImpl( org.omg.CORBA.ORB orb,
                            org.omg.PortableServer.POA agent_poa,
                            String grammar  )
  {
    if ( ! TIDParser._CONSTRAINT_GRAMMAR.equals( grammar ) ) {
      throw new org.omg.CORBA.NO_IMPLEMENT();
    }
    _orb = orb;
    _agentPOA = agent_poa;
    _index_locators_table = new java.util.Hashtable();
    _grammar = grammar;
  }

  //
  // Temas del default Servant
  //
  //
  synchronized
  private void setCurrent()
  {
    if (_current == null)
    {
      try
      {
        _current = org.omg.PortableServer.CurrentHelper.narrow(
                             _orb().resolve_initial_references("POACurrent") );
      }
      //catch (org.omg.CORBA.ORBPackage.InvalidName ex)
      //catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
      catch (Exception ex)
      {

        TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
        throw new org.omg.CORBA.INTERNAL();
      }
    }
  }

  private IndexLocatorData getData()
  {
    if (_current == null) setCurrent();

    try
    {
      IndexLocatorData data = (IndexLocatorData)
               _index_locators_table.get(new String(_current.get_object_id()));
      if (data != null) return data;
    }
    //catch (org.omg.CORBA.ORBPackage.InvalidName ex)
    //catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
    catch (Exception ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      throw new org.omg.CORBA.INV_OBJREF();
    }
    throw new org.omg.CORBA.OBJECT_NOT_EXIST();
  }

  public void register( IndexLocatorData data )
  {
    REGISTER[1] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
    _index_locators_table.put(data.id, data);
  }

  public void unregister( IndexLocatorData data )
  {
    UNREGISTER[1] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
    _index_locators_table.remove(data.id);
  }

  //
  // Others
  //
  public String grammar()
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, GET_GRAMMAR);
    return _grammar;
  }

  public IndexType returned_index_type()
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, GET_RETURNED_INDEX_TYPE);
    return index_locator.returned_index_type;
  }

  public void returned_index_type(IndexType value)
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, SET_RETURNED_INDEX_TYPE);
    index_locator.returned_index_type = value;
  }

  public String default_string_index()
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, GET_DEFAULT_STRING_INDEX);
    return index_locator.default_string_index;
  }

  public void default_string_index(String value)
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, SET_DEFAULT_STRING_INDEX);
    index_locator.default_string_index = value;
  }

  public int default_int_index()
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, GET_DEFAULT_LONG_INDEX);
    return index_locator.default_int_index;
  }

  public void default_int_index(int value)
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, SET_DEFAULT_LONG_INDEX);
    index_locator.default_int_index = value;
  }

  public String get_event_field_reference()
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, GET_EVENT_FIELD_REFERENCE);
    return index_locator.event_field_reference;
  }

  public void set_event_field_reference(String new_expression)
                                                 throws InvalidExpression
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, SET_EVENT_FIELD_REFERENCE);

    index_locator.filter_counter = 0;
    try
    {
      index_locator.event_field_filter = TIDParser.parse(new_expression);
      index_locator.event_field_reference = new_expression;
      index_locator.filter_counter = 1;
    }
    catch (es.tid.util.parser.ParseException ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      throw new InvalidExpression();
    }
    catch (java.lang.Exception ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
      throw new InvalidExpression();
    }

    TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, ARBOL);
    index_locator.event_field_filter.dump("   ");

    if (PersistenceManager.isActive())
    {
      PersistenceManager.getDB().update(
                               PersistenceDB.ATTR_INDEXLOCATOR, index_locator);
    }
  }

  public String get_string_index( org.omg.CORBA.Any value )
                     throws InvalidExpression, FieldNotFound, TypeDoesNotMatch
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, GET_STRING_INDEX);

    if (index_locator.filter_counter == 0) throw new InvalidExpression();

    TypeValuePair result = index_locator.event_field_filter.check_event(value);

    if (result.type == TypeValuePair.ERRTYPE)
    {
      throw new FieldNotFound();
    }

    if (result.type != TypeValuePair.STRTYPE) 
    {
      throw new TypeDoesNotMatch();
    }

    return (String)result.val.objVal;
  }

  public int get_int_index( org.omg.CORBA.Any value )
                     throws InvalidExpression, FieldNotFound, TypeDoesNotMatch
  {
    IndexLocatorData index_locator = getData();
    TIDNotifTrace.print(TIDNotifTrace.USER, GET_LONG_INDEX);

    if (index_locator.filter_counter == 0) throw new InvalidExpression();

    TypeValuePair result = index_locator.event_field_filter.check_event(value);

    if (result.type == TypeValuePair.ERRTYPE)
    {
      throw new FieldNotFound();
    }

    if (result.type != TypeValuePair.ITYPE) 
    {
      throw new TypeDoesNotMatch();
    }

    return ((Integer)result.val.objVal).intValue();
  }

}
