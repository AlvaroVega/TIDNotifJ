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

import org.omg.CORBA.Any;
import org.omg.CORBA.AnyHolder;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CosNotification.Property;
import org.omg.CosNotification.StructuredEvent;
import org.omg.CosNotifyFilter.ConstraintExp;
import org.omg.CosNotifyFilter.ConstraintInfo;
import org.omg.CosNotifyFilter.ConstraintNotFound;
import org.omg.CosNotifyFilter.InvalidConstraint;
import org.omg.CosNotifyFilter.InvalidValue;
import org.omg.CosNotifyFilter.MappingConstraintInfo;
import org.omg.CosNotifyFilter.MappingConstraintPair;
import org.omg.CosNotifyFilter.MappingFilterPOA;
import org.omg.CosNotifyFilter.UnsupportedFilterableData;

import es.tid.TIDorbj.core.poa.OID;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;

import es.tid.util.parser.TIDParser;
import es.tid.util.parser.SimpleNode;
import es.tid.util.parser.TypeValuePair;

public class MappingFilterImpl extends MappingFilterPOA implements
		MappingFilterMsg {
	private org.omg.CORBA.ORB _orb;

    private String _grammar;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// List of MappingDiscriminators (Pack)
	protected java.util.Hashtable _mapping_discriminators_table;

	//
	// Constructors
	//
	public MappingFilterImpl() {			
		// set the default _grammar
		_grammar = TIDParser._CONSTRAINT_GRAMMAR;
		_mapping_discriminators_table = new java.util.Hashtable();
	}

	public MappingFilterImpl(String grammar) {
		if (!TIDParser._CONSTRAINT_GRAMMAR.equals(grammar)) {
			throw new org.omg.CORBA.NO_IMPLEMENT();
		}			
		_grammar = grammar;
		_mapping_discriminators_table = new java.util.Hashtable();
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
			}
			//catch (org.omg.CORBA.ORBPackage.InvalidName ex)
			//catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
			catch (Exception ex) {

				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				throw new org.omg.CORBA.INTERNAL();
			}
		}
	}

	private MappingFilterData getData() {
		if (_current == null)
			setCurrent();

		try {
			MappingFilterData data = (MappingFilterData) _mapping_discriminators_table
					.get(new OID(_current.get_object_id()));
			if (data != null)
				return data;
		}
		//catch (org.omg.CORBA.ORBPackage.InvalidName ex)
		//catch (org.omg.PortableServer.CurrentPackage.NoContext ex)
		catch (Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.CORBA.INV_OBJREF();
		}
		throw new org.omg.CORBA.OBJECT_NOT_EXIST();
	}

	public void register(MappingFilterData data) {
		REGISTER[1] = data.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		_mapping_discriminators_table.put(new OID(data.id.getBytes()), data);
	}

	public void unregister(MappingFilterData data) {
		UNREGISTER[1] = data.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_mapping_discriminators_table.remove(new OID(data.id.getBytes()));
	}

	//
	// Others
	//
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#constraint_grammar()
	 */
	public String constraint_grammar() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_GRAMMAR);
		return _grammar;
	}

	public org.omg.CORBA.TypeCode value_type() {
		MappingFilterData mapping_discriminator_data = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_VALUE_TYPE);
		return mapping_discriminator_data.value_type;
	}

	public org.omg.CORBA.Any default_value() {
		MappingFilterData mapping_discriminator_data = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_DEFAULT_VALUE);
		return mapping_discriminator_data.default_value;
	}

	public void default_value(org.omg.CORBA.Any value) {
		MappingFilterData mapping_discriminator_data = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, SET_DEFAULT_VALUE);
		mapping_discriminator_data.default_value = value;
		if (PersistenceManager.isActive()) {
			//PersistenceManager.getDB().save(PersistenceDB.SAVE, _id, mapping_discriminator_data.default_value);
		}
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#add_mapping_constraints(org.omg.CosNotifyFilter.MappingConstraintPair[])
	 */
	public MappingConstraintInfo[] add_mapping_constraints(MappingConstraintPair[] pair_list) throws InvalidConstraint, InvalidValue {

		if ( pair_list != null) {
			int i;
			InvalidConstraint error;
			MappingConstraintInfo[] info_list;

			error = null;
			info_list = new MappingConstraintInfo[pair_list.length];

			for (i = 0; error == null && i < pair_list.length; i++) {
				try {
					info_list[i] = this.add_mapping_constraint(pair_list[i]);
				} catch (InvalidConstraint ic) {
					error = ic;
				} catch (Throwable th) {
					error = new InvalidConstraint(
						th.getMessage(),
						pair_list[i].constraint_expression
					);
				}
			}
			if (error != null) {
				for (int j = i; j >= 0; j++) {
					if (info_list[j] != null) {
						try {
							this.delete_mapping_constraint(info_list[j].constraint_id);
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
	}

	
	public MappingConstraintInfo add_mapping_constraint(
			MappingConstraintPair new_constraint) throws InvalidConstraint,
			InvalidValue {
		
		MappingFilterData data = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, ADD_MAPPING_RULE);

		if (!(data.value_type.equal(new_constraint.result_to_set.type()))) {
			throw new InvalidValue();
		}

		SimpleNode new_filter = null;
		try {
			new_filter = TIDParser.parse( new_constraint.constraint_expression.constraint_expr );
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), new_constraint.constraint_expression);
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), new_constraint.constraint_expression );
		}

		//TODO: reimplement this... use a stack of unused keys instead...
		Integer new_key;
		int security_limit = 0;
		do {
			new_key = new Integer(TIDParser.newConstraintId());
			security_limit++;
			if (security_limit > TIDParser._MAX_CONSTRAINT_ID) {
				TIDNotifTrace.print(TIDNotifTrace.ERROR, TOO_MAPPING_RULES);
				throw new InvalidConstraint( TOO_MAPPING_RULES, new_constraint.constraint_expression );
			}
		} while (data.assignedMappingConstraintInfoTable.containsKey(new_key) );

		MappingConstraintInfo info;
		try {
			info = new MappingConstraintInfo(
				new_constraint.constraint_expression,
				new_key.intValue(),
				new_constraint.result_to_set
			);
			data.assignedMappingConstraintInfoTable.put(
					new_key,
					info
			);
			data.filterTable.put(new_key, new_filter);
		} catch (java.lang.NullPointerException ex) {
			// Por si lo que ha fallado es la seguinda insercion
			data.assignedMappingConstraintInfoTable.remove(new_key);
			data.filterTable.remove(new_key);
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint();
		}

		NEW_CONSTRAINT[1] = new_constraint.constraint_expression.constraint_expr;
		NEW_CONSTRAINT[3] = new_key.toString();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_CONSTRAINT);
		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, ARBOL);
		new_filter.dump("   ");

		if (PersistenceManager.isActive()) {
			//PersistenceManager.getDB().save( PersistenceDB.SAVE, _id, 
			//new_key.intValue(), new_constraint, value );
		}
		return info;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#get_mapping_constraints(int[])
	 */
	public MappingConstraintInfo[] get_mapping_constraints(int[] id_list)
			throws ConstraintNotFound {
		MappingFilterData data;
		ConstraintNotFound notFound;
		MappingConstraintInfo[] constraints;
		 
		data = getData();
		notFound = null;
		constraints = new MappingConstraintInfo[ id_list.length ];
		
		for ( int i=0; notFound == null && i < id_list.length; i++ ){
			constraints[ i ] = ( MappingConstraintInfo )data.assignedMappingConstraintInfoTable.get( 
				new Integer( id_list[ i ] ) 
			);
			if ( constraints[ i ] == null ){
				notFound = new ConstraintNotFound( id_list[ i ] );
			}
		}
		if ( notFound == null ){
			return constraints;
		} else {
			throw notFound;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#get_all_mapping_constraints()
	 */
	public MappingConstraintInfo[] get_all_mapping_constraints() {
		MappingFilterData data = getData();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_MAPPINGS_RULES);

		MappingConstraintInfo[] _constraints; 
		_constraints = new MappingConstraintInfo[ data.assignedMappingConstraintInfoTable.size() ];

		int i = 0;
		java.util.Map.Entry actualEntry;
		for (java.util.Iterator e = data.assignedMappingConstraintInfoTable.entrySet().iterator(); e.hasNext();) {
			actualEntry = (java.util.Map.Entry) e.next();
			_constraints[i++] = (MappingConstraintInfo) actualEntry.getValue();
		}
		return _constraints;
	}	
	
	public MappingConstraintInfo get_mapping_constraint(int id)
			throws ConstraintNotFound {
		
		MappingFilterData data = getData();
		Integer key = new Integer(id);

		GET_MAPPING_RULE[1] = key.toString();
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_MAPPING_RULE);

		MappingConstraintInfo info;
		info = ( MappingConstraintInfo ) data.assignedMappingConstraintInfoTable
				.get(key);

		if ( info == null ) {
			throw new ConstraintNotFound( id );
		}
		return info;
	}

	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#modify_mapping_constraints(int[], org.omg.CosNotifyFilter.MappingConstraintInfo[])
	 */
	public void modify_mapping_constraints(int[] del_list,
			MappingConstraintInfo[] modify_list) throws InvalidConstraint,
			InvalidValue, ConstraintNotFound {
		ConstraintNotFound notFound = null;
		InvalidConstraint invalidConstraint = null;

		MappingFilterData data;
		Integer[] del_list_keys, modify_list_keys;
		data = getData();
		/**
		 * Check preconditions: every constraint referenced must exist
		 */

		if (del_list != null && del_list.length > 0) {
			del_list_keys = new Integer[del_list.length];
			MappingConstraintInfo constraintToRemove;
			for (int i = 0; notFound == null && i < del_list.length; i++) {
				del_list_keys[i] = new Integer(del_list[i]);
				constraintToRemove = 
					(MappingConstraintInfo) data.assignedMappingConstraintInfoTable.get(del_list_keys[i]);
				if (constraintToRemove == null) {
					notFound = new ConstraintNotFound(del_list[i]);
				}
			}//for
		} else {
			del_list_keys = null;
		}

		if (modify_list != null && modify_list.length > 0) {
			modify_list_keys = new Integer[modify_list.length];
			MappingConstraintInfo constraintToModify;
			for (int i = 0; notFound == null && i < modify_list.length; i++) {
				modify_list_keys[i] = new Integer(modify_list[i].constraint_id);
				constraintToModify = (MappingConstraintInfo) data.assignedMappingConstraintInfoTable.get(modify_list_keys[i]);
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
				removedConstraints[i] = (ConstraintInfo) data.assignedMappingConstraintInfoTable
						.remove(del_list_keys[i]);
				if (removedConstraints[i] != null) {
					FOUND_MAPPING_RULE[1] = removedConstraints[i].constraint_expression.constraint_expr;
					TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_MAPPING_RULE);
				} else {
					TIDNotifTrace.print(TIDNotifTrace.DEBUG,
							NOT_FOUND_MAPPING_RULE);
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

				REPLACE_MAPPING_RULE[1] = modify_list_keys[i].toString();
				REPLACE_MAPPING_RULE[3] = modify_list[i].constraint_expression.constraint_expr;
				TIDNotifTrace.print(TIDNotifTrace.USER, REPLACE_MAPPING_RULE);

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
					replacedConstraints[i] = (ConstraintExp) data.assignedMappingConstraintInfoTable
							.put(modify_list_keys[i], modify_list[i]);
					if (replacedConstraints[i] != null) {
						FOUND_REPLACED_MAPPING_RULE[1] = replacedConstraints[i].constraint_expr;
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								FOUND_REPLACED_MAPPING_RULE);

					} else {
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								NOT_FOUND_REPLACED_MAPPING_RULE);
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
						data.assignedMappingConstraintInfoTable.put(modify_list_keys[i],
								replacedConstraints[i]);
						data.filterTable.put(modify_list_keys[i],
								replacedFilters[i]);
					}// replacedConstraint != null
				}// for
				for (int i = removedConstraints.length; i >= 0; --i) {
					if (removedConstraints[i] != null) {
						data.assignedMappingConstraintInfoTable.put(del_list_keys[i],
								removedConstraints[i]);
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

	}
	
	public void delete_mapping_constraint(int id)
			throws ConstraintNotFound {
		MappingFilterData data = getData();
		Integer key = new Integer(id);

		DELETE_MAPPING_RULE[1] = key.toString();
		TIDNotifTrace.print(TIDNotifTrace.USER, DELETE_MAPPING_RULE);

		if (data.assignedMappingConstraintInfoTable
				.containsKey(key) == false) {
			throw new ConstraintNotFound( id );
		}

		MappingConstraintInfo info; 
		info = (MappingConstraintInfo) data.assignedMappingConstraintInfoTable.remove(key);

		if (info != null) {
			FOUND_MAPPING_RULE[1] = info.constraint_expression.constraint_expr;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_MAPPING_RULE);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_MAPPING_RULE);
		}

		// Eliminamos el arbol
		SimpleNode filter = (SimpleNode) data.filterTable.remove(key);
		if (filter != null) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_ARBOL);
		}

		if (PersistenceManager.isActive()) {
			//PersistenceManager.getDB().removeMappingRules( id, mapping_discriminator_data.id);
		}
	}

	public MappingConstraintInfo replace_mapping_constraint( MappingConstraintInfo info )
			throws ConstraintNotFound, InvalidConstraint, InvalidValue {
		MappingFilterData data = getData();
		Integer key = new Integer( info.constraint_id );

		REPLACE_MAPPING_RULE[1] = key.toString();
		REPLACE_MAPPING_RULE[3] = info.constraint_expression.constraint_expr;
		TIDNotifTrace.print(TIDNotifTrace.USER, REPLACE_MAPPING_RULE);

		//if ( mapping_discriminator_data.value_type.kind.value() != value.type().kind.value() )
		if (!data.value_type.equal(info.value.type())) {
			throw new InvalidValue(info.constraint_expression,info.value);
		}

		if (data.assignedMappingConstraintInfoTable.containsKey(key) == false) {
			throw new ConstraintNotFound( info.constraint_id );
		}

		SimpleNode new_filter = null;
		try {
			new_filter = TIDParser.parse( info.constraint_expression.constraint_expr );
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), info.constraint_expression );
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), info.constraint_expression );
		}

		// Reemplazamos la MappingRule
		MappingConstraintInfo previous_info;
		previous_info = (MappingConstraintInfo) data.assignedMappingConstraintInfoTable.put(key, info);
		if (previous_info != null) {
			FOUND_REPLACED_MAPPING_RULE[1] = previous_info.constraint_expression.constraint_expr;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,FOUND_REPLACED_MAPPING_RULE);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,NOT_FOUND_REPLACED_MAPPING_RULE);
		}

		// Reemplazamos el arbol
		SimpleNode old_filter = (SimpleNode) data.filterTable
				.put(key, new_filter);
		if (old_filter != null) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_ARBOL);
		}

		if (PersistenceManager.isActive()) {
			//PersistenceManager.getDB().save( PersistenceDB.UPDATE, _id, 
			//id, new_constraint, new_value );
		}
		return previous_info;
	}

	
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#remove_all_mapping_constraints()
	 */
	public void remove_all_mapping_constraints() {
		MappingFilterData data = getData();
		
		TIDNotifTrace.print(TIDNotifTrace.USER, DELETE_ALL_MAPPING_RULES);

		
		data.assignedMappingConstraintInfoTable.clear();
		data.filterTable.clear();
		
		
		if (PersistenceManager.isActive()) {
		}
	}	
	
	public void matches(java.lang.String data_filed_expression) throws InvalidConstraint {
		MappingFilterData mapping_discriminator_data = getData();
		MATCHES[1] = data_filed_expression;
		TIDNotifTrace.print(TIDNotifTrace.USER, MATCHES);

		SimpleNode match_filter = null;
		try {
			match_filter = TIDParser.parse(data_filed_expression);
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), null );
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new InvalidConstraint( ex.getMessage(), null );
		}
	}


	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#match(org.omg.CORBA.Any, org.omg.CORBA.AnyHolder)
	 */
	public boolean match(Any filterable_data, AnyHolder result_to_set)
			throws UnsupportedFilterableData {

		MappingFilterData data = getData();
		TIDNotifTrace.print(TIDNotifTrace.DEBUG, MATCH);
		boolean ret = false;
		if (data.filterTable.size() > 0) {
			for (java.util.Iterator e = data.filterTable
					.entrySet().iterator(); e.hasNext();) {
				java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
				Integer key = (Integer) entry.getKey();
				SimpleNode tree = (SimpleNode) entry.getValue();

				TypeValuePair result = tree.check_event(filterable_data);

				if (result.type == TypeValuePair.BOOLTYPE) {
					if (((Boolean) result.val.objVal).booleanValue()) {
						MappingConstraintInfo info = (MappingConstraintInfo) data.assignedMappingConstraintInfoTable
								.get(key);
						if (info != null) {
							result_to_set.value = info.value;
							ret = true;
						}
					}
				}
			}
		}

		if (data.matches != null) {
			TypeValuePair result = data.matches
					.check_event(filterable_data);
			if (result.type == TypeValuePair.ITYPE) {
				TIDNotifTrace.print(TIDNotifTrace.USER, TO_DO_1);
			}
		}

		return ret;
	}



	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}


	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#match_structured(org.omg.CosNotification.StructuredEvent, org.omg.CORBA.AnyHolder)
	 */
	public boolean match_structured(StructuredEvent filterable_data,
			AnyHolder result_to_set) throws UnsupportedFilterableData {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.MappingFilterOperations#match_typed(org.omg.CosNotification.Property[], org.omg.CORBA.AnyHolder)
	 */
	public boolean match_typed(Property[] filterable_data,
			AnyHolder result_to_set) throws UnsupportedFilterableData {
		// TODO Auto-generated method stub
		return false;
	}

}