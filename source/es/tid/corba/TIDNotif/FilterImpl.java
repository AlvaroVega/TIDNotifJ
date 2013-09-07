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

import java.util.Iterator;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotification.StructuredEventHelper;
import org.omg.CosNotifyComm.NotifySubscribe;
import org.omg.CosNotifyFilter.CallbackNotFound;
import org.omg.CosNotifyFilter.ConstraintExp;
import org.omg.CosNotifyFilter.ConstraintInfo;
import org.omg.CosNotifyFilter.ConstraintNotFound;
import org.omg.CosNotifyFilter.FilterPOA;
import org.omg.CosNotifyFilter.InvalidConstraint;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

import es.tid.util.parser.TIDParser;
import es.tid.util.parser.SimpleNode;
import es.tid.util.parser.TypeValuePair;

public class FilterImpl extends FilterPOA implements FilterMsg {
	private static final int ADD_ONE = 0;
	private static final int SUB_ONE = 1;
	private static final int RESET 	 = 2;
	

	private String _grammar;

	private boolean _return_on_error;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// List of Discriminators (Pack)
	protected java.util.Hashtable _discriminators_table;

	//
	// Constructors
	//
	public FilterImpl() {			
		_discriminators_table = new java.util.Hashtable();
		_grammar = TIDParser._CONSTRAINT_GRAMMAR; // set the default _grammar
		_return_on_error = TIDNotifConfig
				.getBool(TIDNotifConfig.RETURN_ON_ERROR_KEY);
	}

	public FilterImpl(String grammar) {
		if (!TIDParser._CONSTRAINT_GRAMMAR.equals(grammar)) {
			throw new org.omg.CORBA.NO_IMPLEMENT();
		}
		
		
		_discriminators_table = new java.util.Hashtable();
		_grammar = grammar;
		_return_on_error = TIDNotifConfig
				.getBool(TIDNotifConfig.RETURN_ON_ERROR_KEY);
	}

	//
	// Temas del default Servant
	//
	//
	synchronized private void setCurrent() {
		if (_current == null) {
			try {
				_current = org.omg.PortableServer.CurrentHelper.narrow(_orb()
						.resolve_initial_references("POACurrent"));
			} catch (Exception ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				throw new org.omg.CORBA.INTERNAL();
			}
		}
	}

	private FilterData getData() {
		if (_current == null)
			setCurrent();

		try {
			FilterData data = (FilterData) _discriminators_table
					.get(new String(_current.get_object_id()));
			if (data != null) {
				return data;
			} else {
				throw new org.omg.CORBA.OBJECT_NOT_EXIST();
			}
		} catch (Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.CORBA.INV_OBJREF();
		}
	}

	public void register(FilterData data) {
		REGISTER[1] = data.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		_discriminators_table.put(data.id, data);
	}

	public void unregister(FilterData data) {
		UNREGISTER[1] = data.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_discriminators_table.remove(data.id);
	}
	
	public FilterData getData( String id ){
		return (FilterData)_discriminators_table.get( id );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#constraint_grammar()
	 */
	public String constraint_grammar() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_GRAMMAR);
		return _grammar;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#add_constraints(org.omg.CosNotifyFilter.ConstraintExp[])
	 */
	public ConstraintInfo[] add_constraints(ConstraintExp[] constraint_list)
			throws InvalidConstraint {

		if (constraint_list != null && constraint_list.length > 0) {
			int i;
			InvalidConstraint error;
			ConstraintInfo[] info_list;

			error = null;
			info_list = new ConstraintInfo[constraint_list.length];

			for (i = 0; error == null && i < constraint_list.length; i++) {
				try {
					info_list[i] = add_constraint(constraint_list[i]);
				} catch (InvalidConstraint ic) {
					error = ic;
				} catch (Throwable th) {
					error = new InvalidConstraint(th.getMessage(),
							constraint_list[i]);
				}
			}
			if (error != null) {
                            for (int j = i; j >= 0; j++) { // j = i -1 & j--??
					if (info_list[j] != null) {
						try {
							this.remove_constraint(info_list[j].constraint_id);
						} catch (Throwable th) {
						}
					}
				}
				throw error;
			}
			return info_list;
		} else {
			throw new BAD_PARAM();
		}
	}//add_constraints

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#add_constraints(org.omg.CosNotifyFilter.ConstraintExp[])
	 */
	public ConstraintInfo add_constraint(ConstraintExp new_constraint)
			throws InvalidConstraint {

		FilterData discriminator = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, ADD_CONSTRAINT);

		SimpleNode new_filter = null;
		try {
			StringBuffer filter;
			filter = new StringBuffer();
			if ( new_constraint.event_types.length > 0 ) {
				filter.append( '(' );
				for ( int i=0; i < new_constraint.event_types.length; i++ ){
					filter.append( " ( $type_name == '" )
							.append( new_constraint.event_types[i].type_name )
						  .append( "' )" )
						  .append( " and" )
						  .append( " ( $domain_name == '" )
						    .append(new_constraint.event_types[i].domain_name )
						  .append( "' )" );
					if ( i < new_constraint.event_types.length - 1 ) {
						filter.append( " and" );
					}
				}
				filter.append( " ) and " );
			}
			filter.append( new_constraint.constraint_expr );
			
			new_filter = TIDParser.parse( filter.toString() );
			
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint(ex.getMessage(), new_constraint);
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint(ex.getMessage(), new_constraint);
		}

		Integer new_key;
		int security_limit = 0;
		do {
			new_key = new Integer(TIDParser.newConstraintId());
			security_limit++;
			if (security_limit > TIDParser._MAX_CONSTRAINT_ID) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, TO_MANY_CONSTRAINTS);
				throw new org.omg.CORBA.NO_RESOURCES();
			}
		} while (discriminator.constraintTable.containsKey(new_key));

		ConstraintInfo constraint_info;
		try {
			constraint_info = new ConstraintInfo(new_constraint, new_key
					.intValue());
			discriminator.constraintTable.put(new_key, constraint_info);
			discriminator.filterTable.put(new_key, new_filter);
			updateCounter(ADD_ONE, discriminator);
		} catch (Exception ex) {
			// Por si lo que ha fallado es la segunda insercion
			discriminator.filterTable.remove(new_key);
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint(
					((ConstraintInfo) discriminator.constraintTable
							.remove(new_key)).constraint_expression);
		}

		NEW_CONSTRAINT[1] = new_constraint.constraint_expr;
		NEW_CONSTRAINT[3] = new_key.toString();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_CONSTRAINT);

		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, ARBOL);
		new_filter.dump("   ");

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_CONSTRAINTS,
					discriminator);
		}
		return constraint_info;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#modify_constraints(int[], org.omg.CosNotifyFilter.ConstraintInfo[])
	 */
	public void modify_constraints(int[] del_list, ConstraintInfo[] modify_list)
			throws InvalidConstraint, ConstraintNotFound {

		ConstraintNotFound notFound = null;
		InvalidConstraint invalidConstraint = null;

		FilterData data;
		Integer[] del_list_keys, modify_list_keys;
		data = getData();
		/**
		 * Check preconditions: every constraint referenced must exist
		 */

		if (del_list != null && del_list.length > 0) {
			del_list_keys = new Integer[del_list.length];
			ConstraintInfo constraintToRemove;
			for (int i = 0; notFound == null && i < del_list.length; i++) {
				del_list_keys[i] = new Integer(del_list[i]);
				constraintToRemove = (ConstraintInfo) data.constraintTable
						.get(del_list_keys[i]);
				if (constraintToRemove == null) {
					notFound = new ConstraintNotFound(del_list[i]);
				}
			}//for
		} else {
			del_list_keys = null;
		}

		if (modify_list != null && modify_list.length > 0) {
			modify_list_keys = new Integer[modify_list.length];
			ConstraintInfo constraintToModify;
			for (int i = 0; notFound == null && i < modify_list.length; i++) {
				modify_list_keys[i] = new Integer(modify_list[i].constraint_id);
				constraintToModify = (ConstraintInfo) data.constraintTable
						.get(modify_list_keys[i]);
				if (constraintToModify == null) {
					notFound = new ConstraintNotFound(
							modify_list[i].constraint_id);
				}
			}//for
		} else {
			modify_list_keys = null;
		}

		/**
		 * Perform changes...
		 */
		if (notFound == null) {
			/**
			 * Constraint removal. Store a backup array in case of error
			 */
			SimpleNode[] removedFilters;
			removedFilters = new SimpleNode[del_list.length];
			ConstraintInfo[] removedConstraints;
			removedConstraints = new ConstraintInfo[del_list.length];

			for (int i = 0; i < del_list.length; i++) {
				updateCounter(SUB_ONE, data);
				removedConstraints[i] = (ConstraintInfo) data.constraintTable
						.remove(del_list_keys[i]);
				if (removedConstraints[i] != null) {
					FOUND_CONSTRAINT[1] = removedConstraints[i].constraint_expression.constraint_expr;
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_CONSTRAINT);
				} else {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG,
							NOT_FOUND_CONSTRAINT);
				}
				removedFilters[i] = (SimpleNode) data.filterTable
						.remove(del_list_keys[i]);
				if (removedFilters[i] != null) {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
				} else {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_ARBOL);
				}
			}

			/**
			 * Constraint replace. Note, if some constraint to be modified was removed
			 * in the previous step, it will be inserted as the spec doesn't
			 * tells anything about it
			 */
			SimpleNode[] replacedFilters;
			replacedFilters = new SimpleNode[modify_list.length];

			ConstraintExp[] replacedConstraints;
			replacedConstraints = new ConstraintExp[modify_list.length];

			for (int i = 0; invalidConstraint == null && i < modify_list.length; i++) {

				REPLACE_CONSTRAINT[1] = modify_list_keys[i].toString();
				REPLACE_CONSTRAINT[3] = modify_list[i].constraint_expression.constraint_expr;
				TIDNotifTrace.print(TIDNotifTrace.USER, REPLACE_CONSTRAINT);

				SimpleNode new_filter = null;
				try {
					new_filter = TIDParser
							.parse(modify_list[i].constraint_expression.constraint_expr);
				} catch (es.tid.util.parser.ParseException ex) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
					invalidConstraint = new InvalidConstraint(ex.getMessage(),
							modify_list[i].constraint_expression);
				} catch (java.lang.Exception ex) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
					invalidConstraint = new InvalidConstraint(ex.getMessage(),
							modify_list[i].constraint_expression);
				}

				if (invalidConstraint == null) {
					// Reemplazamos la Constraint
					replacedConstraints[i] = (ConstraintExp) data.constraintTable
							.put(modify_list_keys[i], modify_list[i]);
					if (replacedConstraints[i] != null) {
						FOUND_REPLACED_CONSTRAINT[1] = replacedConstraints[i].constraint_expr;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								FOUND_REPLACED_CONSTRAINT);

					} else {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								NOT_FOUND_REPLACED_CONSTRAINT);
						/**
						 * update constraints counter value as it was decremented upon
						 * constraint removal
						 */
						updateCounter(ADD_ONE, data);
					}

					// Reemplazamos el arbol
					replacedFilters[i] = (SimpleNode) data.filterTable.put(
							modify_list_keys[i], new_filter);
					if (replacedFilters[i] != null) {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
					} else {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								NOT_FOUND_ARBOL);
					}
				}//if invalidConstraint == null
			}//for

			if (invalidConstraint != null) {
				/**
				 * Restore previus changes...
				 */
				for (int i = replacedConstraints.length; i >= 0; --i) {
					if (replacedConstraints[i] != null) {
						data.constraintTable.put(modify_list_keys[i],
								replacedConstraints[i]);
						data.filterTable.put(modify_list_keys[i],
								replacedFilters[i]);
					}// replacedConstraint != null
				}// for
				for (int i = removedConstraints.length; i >= 0; --i) {
					if (removedConstraints[i] != null) {
						if (data.constraintTable.put(del_list_keys[i],
								removedConstraints[i]) == null) {
							updateCounter(ADD_ONE, data);
						}
						data.filterTable.put(del_list_keys[i],
								removedFilters[i]);
					}
				}// for
			} else {
				if (PersistenceManager.isActive()) {
					PersistenceManager.getDB().update(
							PersistenceDB.ATTR_CONSTRAINTS, data);
				}
			}
		} else {
			throw notFound;
		}

	}//modify_constraints

	public int replace_constraint( ConstraintInfo new_constraint )
			throws ConstraintNotFound, InvalidConstraint {
		FilterData discriminator = getData();
		
		Integer key = new Integer(new_constraint.constraint_id );
		REPLACE_CONSTRAINT[1] = key.toString();
		REPLACE_CONSTRAINT[3] = new_constraint.constraint_expression.constraint_expr;
		TIDNotifTrace.print(TIDNotifTrace.USER, REPLACE_CONSTRAINT);

		if (!discriminator.constraintTable.containsKey(key)) {
			throw new ConstraintNotFound( new_constraint.constraint_id );
		}

		SimpleNode new_filter = null;
		try {
			new_filter = TIDParser.parse(new_constraint.constraint_expression.constraint_expr);
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), new_constraint.constraint_expression );
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), new_constraint.constraint_expression );
		}

		// Reemplazamos la Constraint
		ConstraintInfo replacedConstraint;
		replacedConstraint = ( ConstraintInfo ) discriminator.constraintTable.put(
			key,
			new_constraint
		);
		if ( replacedConstraint != null ) {
			FOUND_REPLACED_CONSTRAINT[1] = replacedConstraint.constraint_expression.constraint_expr;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_REPLACED_CONSTRAINT);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_REPLACED_CONSTRAINT);
		}

		// Reemplazamos el arbol
		SimpleNode replacedFilter;
		replacedFilter = (SimpleNode) discriminator.filterTable.put(
			key,
			new_filter
		);
		if ( replacedFilter != null ) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_ARBOL);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_CONSTRAINTS,
					discriminator);
		}
		return replacedConstraint.constraint_id;
	}

	public void remove_constraint( int id ) throws ConstraintNotFound {
		FilterData discriminator = getData();
		Integer key = new Integer(id);
		REMOVE_CONSTRAINT[1] = key.toString();
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_CONSTRAINT);

		if (!discriminator.constraintTable.containsKey(key)) {
			throw new ConstraintNotFound( id );
		}
		updateCounter(SUB_ONE, discriminator);

		// Eliminamos la Constraint
		
		ConstraintExp removedConstraint;
		removedConstraint = ( ConstraintExp ) discriminator.constraintTable.remove(key);

		if ( removedConstraint != null ) {
			FOUND_CONSTRAINT[1] = removedConstraint.constraint_expr;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_CONSTRAINT);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_CONSTRAINT);
		}

		// Eliminamos el arbol
		SimpleNode removedFilter;
		removedFilter = (SimpleNode) discriminator.filterTable.remove(key);
		if ( removedFilter != null ) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_ARBOL);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(PersistenceDB.ATTR_CONSTRAINTS,
					discriminator);
		}
	}

	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#remove_all_constraints()
	 */
	public void remove_all_constraints() {
		FilterData data = getData();
		
		TIDNotifTrace.print(TIDNotifTrace.USER, REMOVE_ALL_CONSTRAINTS);

		updateCounter(SUB_ONE, data);

		// Eliminamos la Constraint
		
		data.constraintTable.clear();
		data.filterTable.clear();
		updateCounter( RESET, data );
		
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
				PersistenceDB.ATTR_CONSTRAINTS,
				data
			);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#get_all_constraints()
	 */
	public ConstraintInfo[] get_all_constraints() {
		FilterData discriminator = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_CONSTRAINTS);

		ConstraintInfo[] _constraints; 
		_constraints = new ConstraintInfo[ discriminator.constraintTable.size() ];

		int i = 0;
		java.util.Map.Entry actualEntry;
		for (java.util.Iterator e = discriminator.constraintTable.entrySet().iterator(); e.hasNext();) {
			actualEntry = (java.util.Map.Entry) e.next();
			_constraints[i++] = (ConstraintInfo) actualEntry.getValue();
		}
		return _constraints;
	}//get_all_contraints

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#get_constraints(int[])
	 */
	public ConstraintInfo[] get_constraints(int[] id_list)
			throws ConstraintNotFound {
		
		FilterData data;
		ConstraintNotFound notFound;
		ConstraintInfo[] _constraints;
		 
		data = getData();
		notFound = null;
		_constraints = new ConstraintInfo[ id_list.length ];
		
		for ( int i=0; notFound == null && i < id_list.length; i++ ){
			_constraints[ i ] = ( ConstraintInfo )data.constraintTable.get( 
				new Integer( id_list[ i ] ) 
			);
			if ( _constraints[ i ] == null ){
				notFound = new ConstraintNotFound( id_list[ i ] );
			}
		}
		if ( notFound == null ){
			return _constraints;
		} else {
			throw notFound;
		}
	}//get_constraints
	
	synchronized private void updateCounter(int op, FilterData data) {
		switch ( op ){
			case ADD_ONE:
				data.filter_counter++;
				break;
			case SUB_ONE:
				data.filter_counter--;
				break;
			case RESET:
				data.filter_counter = 0;
				break;
		}
	}//updateCounter

	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#match(org.omg.CORBA.Any)
	 */
	public boolean match(Any filterable_data) throws UnsupportedFilterableData {
	
		FilterData discriminator = getData();

		boolean ret = false;
		TypeValuePair result;
		if ( discriminator.filter_counter > 0) {
			for ( Iterator it = discriminator.filterTable.values().iterator(); !ret && it.hasNext();) {
				 result = ((SimpleNode) it.next()).check_event(filterable_data);

				if ( result.type == TypeValuePair.ERRTYPE ) {
					return this._return_on_error;
				} else {
					ret |= ((Boolean) (result.val.objVal)).booleanValue();
				}
			}
		}

		return ret;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#match_structured(org.omg.CosNotification.StructuredEvent)
	 */
	public boolean match_structured(StructuredEvent filterable_data)
			throws UnsupportedFilterableData 
	{
		Any any;
		any = this._orb().create_any();
		StructuredEventHelper.insert( any, filterable_data );
		return this.match( any );
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#match_typed(org.omg.CosNotification.Property[])
	 */
	public boolean match_typed(Property[] filterable_data)
			throws UnsupportedFilterableData {
//	  TODO NO_IMPLEMENT
	    throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#destroy()
	 */
	public void destroy() {
		String id = getData().id;
		this._discriminators_table.remove(id);
		PersistenceManager.getDB().delete(getData());
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#attach_callback(org.omg.CosNotifyComm.NotifySubscribe)
	 */
	public int attach_callback(NotifySubscribe callback) {
		// TODO NO_IMPLEMENT
	    throw new NO_IMPLEMENT();
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#detach_callback(int)
	 */
	public void detach_callback(int callback) throws CallbackNotFound {
		// TODO NO_IMPLEMENT
	    throw new NO_IMPLEMENT();

	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterOperations#get_callbacks()
	 */
	public int[] get_callbacks() {
		// TODO Auto-generated method stub
		return null;
	}
}
