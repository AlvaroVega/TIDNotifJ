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

import org.omg.CosNotifyFilter.ConstraintExp;
import org.omg.TransformationAdmin.TransformingRule;

import es.tid.TIDorbj.core.poa.OID;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;

import es.tid.util.parser.TIDParser;
import es.tid.util.parser.SimpleNode;
import es.tid.util.parser.TypeValuePair;

public class TransformingOperatorImpl extends
		org.omg.TransformationInternalsAdmin.InternalTransformingOperatorPOA
		implements TransformingOperatorMsg {
	private org.omg.CORBA.ORB _orb;

	private org.omg.PortableServer.POA _agentPOA;

	private String _grammar;

	// Default Servant Current POA
	private org.omg.PortableServer.Current _current;

	// List of Discriminators (Pack)
	private java.util.Hashtable _transforming_operators_table = null;

	//
	// Constructors
	//
	public TransformingOperatorImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agent_poa) {
		_orb = orb;
		_agentPOA = agent_poa;
		_transforming_operators_table = new java.util.Hashtable();

		// set the default _grammar
		_grammar = TIDParser._CONSTRAINT_GRAMMAR;
	}

	public TransformingOperatorImpl(org.omg.CORBA.ORB orb,
			org.omg.PortableServer.POA agent_poa, String grammar) {
		if (!grammar.equals(TIDParser._CONSTRAINT_GRAMMAR)) {
			throw new org.omg.CORBA.NO_IMPLEMENT();
		}
		_orb = orb;
		_agentPOA = agent_poa;
		_grammar = grammar;
		_transforming_operators_table = new java.util.Hashtable();
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

	private TransformingOperatorData getData() {
		if (_current == null)
			setCurrent();

		try {
			TransformingOperatorData data = (TransformingOperatorData) _transforming_operators_table
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

	public void register(TransformingOperatorData data) {
		REGISTER[1] = data.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, REGISTER);
		_transforming_operators_table.put(new OID(data.id.getBytes()), data);
	}

	public void unregister(TransformingOperatorData data) {
		UNREGISTER[1] = data.id;
		TIDNotifTrace.print(TIDNotifTrace.USER, UNREGISTER);
		_transforming_operators_table.remove(new OID(data.id.getBytes()));
	}

	//
	// Others
	//
	public String grammar() {
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_GRAMMAR);
		return _grammar;
	}

	//-----------------------------------------------------------------------

	synchronized public int add_transforming_rule(java.lang.String new_id,
			ConstraintExp new_constraint, org.omg.CORBA.Object new_object_ref,
			java.lang.String new_operation)
			throws org.omg.TransformationAdmin.AlreadyDefinedId,
			org.omg.TransformationAdmin.InvalidConstraint {
		TransformingOperatorData transforming_data = getData();

		ADD_TRANSFORMING_RULE[1] = new_id;
		ADD_TRANSFORMING_RULE[3] = new_constraint.constraint_expr;
		ADD_TRANSFORMING_RULE[5] = new_object_ref.toString();
		ADD_TRANSFORMING_RULE[7] = new_operation;
		TIDNotifTrace.print(TIDNotifTrace.USER, ADD_TRANSFORMING_RULE);

		if (transforming_data.assignedTransformingRuleTable.containsKey(new_id)) {
			throw new org.omg.TransformationAdmin.AlreadyDefinedId();
		}

		SimpleNode new_filter = null;
		try {
			new_filter = TIDParser.parse(new_constraint.constraint_expr);
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		}

		try {
			org.omg.TransformationAdmin.TransformingRule transformingRule = new TransformingRule(
					new_constraint, new_object_ref, new_operation);
			transforming_data.assignedTransformingRuleTable.put(new_id,
					transformingRule);
			transforming_data.filterTable.put(new_id, new_filter);
		} catch (java.lang.NullPointerException ex) {
			// Por si lo que ha fallado es la seguinda insercion
			transforming_data.assignedTransformingRuleTable.remove(new_id);
			transforming_data.filterTable.remove(new_id);
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		}

		// Tabla que guarda el orden
		Integer new_position = new Integer(update_order(0, transforming_data));
		transforming_data.orderTable.put(new_position, new_id);

		NEW_TRANSFORM[1] = new_constraint.constraint_expr;
		NEW_TRANSFORM[3] = new_id;
		NEW_TRANSFORM[5] = new_position.toString();
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_TRANSFORM);
		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, ARBOL);
		new_filter.dump("   ");

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_TRANSFORMING_RULES, transforming_data);
		}
		return new_position.intValue();
	}

	synchronized public void insert_transforming_rule(int order,
			java.lang.String new_id, ConstraintExp new_constraint,
			org.omg.CORBA.Object new_object_ref, java.lang.String new_operation)
			throws org.omg.TransformationAdmin.AlreadyDefinedOrder,
			org.omg.TransformationAdmin.AlreadyDefinedId,
			org.omg.TransformationAdmin.InvalidConstraint {
		TransformingOperatorData transforming_data = getData();

		INSERT_TRANSFORMING_RULE[1] = String.valueOf(order);
		INSERT_TRANSFORMING_RULE[3] = new_constraint.constraint_expr;
		TIDNotifTrace.print(TIDNotifTrace.USER, INSERT_TRANSFORMING_RULE);

		if (transforming_data.assignedTransformingRuleTable.containsKey(new_id)) {
			throw new org.omg.TransformationAdmin.AlreadyDefinedId();
		}

		SimpleNode new_filter = null;
		try {
			new_filter = TIDParser.parse(new_constraint.constraint_expr);
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		}

		// Comprueba que no este ya ocupada la posicion "order"
		Integer new_position = new Integer(order);
		String old_id = (String) transforming_data.orderTable.get(new_position);
		if (old_id != null) {
			throw new org.omg.TransformationAdmin.AlreadyDefinedOrder();
		}
		update_order(order, transforming_data);

		try {
			org.omg.TransformationAdmin.TransformingRule transformingRule = new org.omg.TransformationAdmin.TransformingRule(
					new_constraint, new_object_ref, new_operation);
			transforming_data.assignedTransformingRuleTable.put(new_id,
					transformingRule);
			transforming_data.filterTable.put(new_id, new_filter);
			transforming_data.orderTable.put(new_position, new_id);
		} catch (java.lang.NullPointerException ex) {
			// Por si lo que ha fallado es la seguinda insercion
			transforming_data.assignedTransformingRuleTable.remove(new_id);
			transforming_data.filterTable.remove(new_id);
			transforming_data.orderTable.remove(new_position);
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		}

		NEW_TRANSFORM[1] = new_constraint.constraint_expr;
		NEW_TRANSFORM[3] = new_id;
		TIDNotifTrace.print(TIDNotifTrace.USER, NEW_TRANSFORM);
		TIDNotifTrace.print(TIDNotifTrace.DEEP_DEBUG, ARBOL);
		new_filter.dump("   ");

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_TRANSFORMING_RULES, transforming_data);
		}
	}

	synchronized public String move_transforming_rule(String id,
			int new_position) throws org.omg.TransformationAdmin.NotFound,
			org.omg.TransformationAdmin.AlreadyDefinedOrder {
		TransformingOperatorData transforming_data = getData();

		MOVE_TRANSFORMING_RULE[1] = id;
		MOVE_TRANSFORMING_RULE[3] = String.valueOf(new_position);
		TIDNotifTrace.print(TIDNotifTrace.USER, MOVE_TRANSFORMING_RULE);

		// Comprueba que no este ya ocupada la posicion "order"
		Integer new_order = new Integer(new_position);
		if (transforming_data.orderTable.get(new_order) != null) {
			throw new org.omg.TransformationAdmin.AlreadyDefinedOrder();
		}
		update_order(new_position, transforming_data);

		Integer order = null;
		if (transforming_data.orderTable.size() > 0) {
			for (java.util.Iterator e = transforming_data.orderTable.entrySet()
					.iterator(); e.hasNext();) {
				java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
				String entry_id = (String) entry.getValue();

				if (entry_id.compareTo(id) == 0) {
					order = (Integer) entry.getKey();
					break;
				}
			}

			if (order != null) {
				transforming_data.orderTable.remove(order);
				transforming_data.orderTable.put(new_order, id);
				if (PersistenceManager.isActive()) {
					PersistenceManager.getDB().update(
							PersistenceDB.ATTR_TRANSFORMING_RULES,
							transforming_data);
				}
				return id;
			}
		}
		throw new org.omg.TransformationAdmin.NotFound();
	}

	public org.omg.TransformationAdmin.TransformingRule get_transforming_rule(
			String id) throws org.omg.TransformationAdmin.NotFound {
		TransformingOperatorData transforming_data = getData();

		GET_TRANSFORMING_RULE[1] = id;
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_TRANSFORMING_RULE);

		org.omg.TransformationAdmin.TransformingRule transformingRule = (org.omg.TransformationAdmin.TransformingRule) transforming_data.assignedTransformingRuleTable
				.get(id);

		if (transformingRule == null) {
			throw new org.omg.TransformationAdmin.NotFound();
		}
		return transformingRule;
	}

	public int get_transforming_rule_order(String id)
			throws org.omg.TransformationAdmin.NotFound {
		TransformingOperatorData transforming_data = getData();

		GET_TRANSFORMING_RULE_ORDER[1] = id;
		TIDNotifTrace.print(TIDNotifTrace.USER, GET_TRANSFORMING_RULE_ORDER);

		if (transforming_data.orderTable.size() > 0) {
			for (java.util.Iterator e = transforming_data.orderTable.entrySet()
					.iterator(); e.hasNext();) {
				java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
				String entry_id = (String) entry.getValue();

				if (entry_id.compareTo(id) == 0) {
					Integer entry_order = (Integer) entry.getKey();
					return entry_order.intValue();
				}
			}
		}
		throw new org.omg.TransformationAdmin.NotFound();
	}

	public void delete_transforming_rule(String id)
			throws org.omg.TransformationAdmin.NotFound {
		TransformingOperatorData transforming_data = getData();

		DELETE_TRANSFORMING_RULE[1] = id;
		TIDNotifTrace.print(TIDNotifTrace.USER, DELETE_TRANSFORMING_RULE);

		if (transforming_data.assignedTransformingRuleTable.containsKey(id) == false) {
			throw new org.omg.TransformationAdmin.NotFound();
		}

		// Eliminamos la TransformingrminggRule
		org.omg.TransformationAdmin.TransformingRule transformingRule = (org.omg.TransformationAdmin.TransformingRule) transforming_data.assignedTransformingRuleTable
				.remove(id);

		if (transformingRule != null) {
			FOUND_TRANSFORMING_RULE[1] = transformingRule.expression.constraint_expr;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_TRANSFORMING_RULE);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					NOT_FOUND_TRANSFORMING_RULE);
		}

		// Eliminamos el arbol
		SimpleNode filter = (SimpleNode) transforming_data.filterTable
				.remove(id);
		if (filter != null) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_ARBOL);
		}

		// Eliminamos el order
		//Integer order = (Integer)_orderTable.remove(key);

		Integer entry_order = null;
		for (java.util.Iterator e = transforming_data.orderTable.entrySet()
				.iterator(); e.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
			String entry_id = (String) entry.getValue();

			if (entry_id.compareTo(id) == 0) {
				entry_order = (Integer) entry.getKey();
				e.remove();
				break;
			}
		}

		// Chequeo que ha sido borrado
		if ((Integer) transforming_data.orderTable.remove(entry_order) == null) {
			TIDNotifTrace
					.print(TIDNotifTrace.DEBUG,
							"TransformingOperator.delete_transforming_rule(Order SIII borrado)");
		} else {
			TIDNotifTrace
					.print(TIDNotifTrace.DEBUG,
							"TransformingOperator.delete_transforming_rule(Order NOOO borrado)");
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_TRANSFORMING_RULES, transforming_data);
		}
	}

	public String replace_transforming_rule(String id,
			ConstraintExp new_constraint, org.omg.CORBA.Object new_object_ref,
			java.lang.String new_operation)
			throws org.omg.TransformationAdmin.NotFound,
			org.omg.TransformationAdmin.InvalidConstraint {
		TransformingOperatorData transforming_data = getData();

		REPLACE_TRANSFORMING_RULE[1] = id;
		REPLACE_TRANSFORMING_RULE[3] = new_constraint.constraint_expr;
		TIDNotifTrace.print(TIDNotifTrace.USER, REPLACE_TRANSFORMING_RULE);

		if (transforming_data.assignedTransformingRuleTable.containsKey(id) == false) {
			throw new org.omg.TransformationAdmin.NotFound();
		}

		SimpleNode new_filter = null;
		try {
			new_filter = TIDParser.parse(new_constraint.constraint_expr);
		} catch (es.tid.util.parser.ParseException ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		} catch (java.lang.Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		}

		if (new_filter == null) {
			throw new org.omg.TransformationAdmin.InvalidConstraint();
		}

		// Reemplazamos la TransformingRule
		org.omg.TransformationAdmin.TransformingRule newTransformingRule = new org.omg.TransformationAdmin.TransformingRule(
				new_constraint, new_object_ref, new_operation);

		org.omg.TransformationAdmin.TransformingRule old_transformingRule = (org.omg.TransformationAdmin.TransformingRule) transforming_data.assignedTransformingRuleTable
				.put(id, newTransformingRule);

		if (old_transformingRule != null) {
			FOUND_REPLACED_TRANSFORMING_RULE[1] = old_transformingRule.expression.constraint_expr;
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					FOUND_REPLACED_TRANSFORMING_RULE);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG,
					NOT_FOUND_REPLACED_TRANSFORMING_RULE);
		}

		// Reemplazamos el arbol
		SimpleNode old_filter = (SimpleNode) transforming_data.filterTable.put(
				id, new_filter);
		if (old_filter != null) {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, FOUND_ARBOL);
		} else {
			TIDNotifTrace.print(TIDNotifTrace.DEBUG, NOT_FOUND_ARBOL);
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().update(
					PersistenceDB.ATTR_TRANSFORMING_RULES, transforming_data);
		}
		return id;
	}

	public org.omg.CORBA.Any transform_value(org.omg.CORBA.Any value)
			throws org.omg.TransformationInternalsAdmin.DataError,
			org.omg.TransformationInternalsAdmin.ConnectionError,
			org.omg.TransformationInternalsAdmin.TransformationError {
		TransformingOperatorData transforming_data = getData();

		TIDNotifTrace.print(TIDNotifTrace.DEBUG, TRANSFORM_VALUE);

		org.omg.CORBA.Any _value = value;

		try {
			if (transforming_data.orderTable.size() > 0) {
				//
				//    for (java.util.Iterator e=_orderTable.values().iterator(); e.hasNext();)
				//
				for (java.util.Iterator e = transforming_data.orderTable
						.entrySet().iterator(); e.hasNext();) {
					java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
					Integer k = (Integer) entry.getKey();
					String key = (String) entry.getValue();

					TRANSFORMING_RULE[1] = k.toString();
					TRANSFORMING_RULE[3] = key;
					//TIDNotifTrace.print(TIDNotifTrace.DEBUG, TRANSFORMING_RULE);
					//
					//        String key = (String) e.next();
					//
					SimpleNode filter = (SimpleNode) transforming_data.filterTable
							.get(key);

					if (filter != null) {
						TypeValuePair result = filter.check_event(value);

						if (result.type == TypeValuePair.BOOLTYPE) {
							if (((Boolean) result.val.objVal).booleanValue()) {
								TRANSFORMING_RULE[5] = new String("PERFORMED");
								TIDNotifTrace.print(TIDNotifTrace.DEBUG,
										TRANSFORMING_RULE);

								org.omg.TransformationAdmin.TransformingRule transformingRule = (org.omg.TransformationAdmin.TransformingRule) transforming_data.assignedTransformingRuleTable
										.get(key);

								if (transformingRule != null) {
									_value = call_operator(key,
											transformingRule.object_ref,
											transformingRule.operation, _value);
								} else {
									TIDNotifTrace
											.print(TIDNotifTrace.USER,
													"    ERROR: transformingRule == null");
									throw new org.omg.TransformationInternalsAdmin.DataError(
											key);
								}
							} else {
								TRANSFORMING_RULE[5] = new String(
										"NOT PERFORMED");
								TIDNotifTrace.print(TIDNotifTrace.DEBUG,
										TRANSFORMING_RULE);
							}
						} else {
							TRANSFORMING_RULE[5] = new String("EVALUATED ERROR");
							TIDNotifTrace.print(TIDNotifTrace.DEBUG,
									TRANSFORMING_RULE);
							throw new org.omg.TransformationInternalsAdmin.DataError(
									key);
						}
					} else {
						TRANSFORMING_RULE[5] = new String("FILTER ERROR");
						TIDNotifTrace.print(TIDNotifTrace.DEBUG,
								TRANSFORMING_RULE);
					}
				}
			}
		} catch (Exception ex) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
		}
		return _value;
	}

	public org.omg.TransformationAdmin.AssignedTransformingRule[] get_transforming_rules() {
		TransformingOperatorData transforming_data = getData();

		TIDNotifTrace.print(TIDNotifTrace.USER, GET_TRANSFORMING_RULES);

		org.omg.TransformationAdmin.AssignedTransformingRule[] transformingRules = new org.omg.TransformationAdmin.AssignedTransformingRule[transforming_data.assignedTransformingRuleTable
				.size()];
		int i = 0;
		for (java.util.Iterator e = transforming_data.orderTable.entrySet()
				.iterator(); e.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
			Integer position = (Integer) entry.getKey();
			String ruleId = (String) entry.getValue();

			org.omg.TransformationAdmin.AssignedTransformingRule atr = new org.omg.TransformationAdmin.AssignedTransformingRule(
					ruleId,
					(org.omg.TransformationAdmin.TransformingRule) transforming_data.assignedTransformingRuleTable
							.get(ruleId));
			if (atr == null) {
				TIDNotifTrace
						.print(TIDNotifTrace.ERROR,
								"TransformingOperator.get_transforming_rules: pushSupplier NOT FOUND");
			} else {
				transformingRules[i++] = atr;
			}
		}
		return transformingRules;
	}

	public org.omg.TransformationAdmin.AssignedRuleOrder[] get_transforming_rules_order() {
		TransformingOperatorData transforming_data = getData();

		TIDNotifTrace.print(TIDNotifTrace.USER, GET_TRANSFORMING_RULES_ORDER);

		org.omg.TransformationAdmin.AssignedRuleOrder[] orders = new org.omg.TransformationAdmin.AssignedRuleOrder[transforming_data.orderTable
				.size()];

		int i = 0;
		for (java.util.Iterator e = transforming_data.orderTable.entrySet()
				.iterator(); e.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) e.next();
			orders[i++] = new org.omg.TransformationAdmin.AssignedRuleOrder(
					((Integer) entry.getKey()).intValue(), (String) entry
							.getValue());
		}
		return orders;
	}

	//
	// Funciones privadas de la clase
	//

	synchronized private int update_order(int new_position,
			TransformingOperatorData data) {
		if (new_position == 0) {
			data.last_rule_order = data.last_rule_order + data.order_gap
					- (data.last_rule_order % data.order_gap);
			return data.last_rule_order;
		} else {
			if (new_position > data.last_rule_order) {
				data.last_rule_order = new_position;
			}
		}
		return 0;
	}

	private org.omg.CORBA.Any call_operator(String key,
			org.omg.CORBA.Object obj_ref, java.lang.String operation,
			org.omg.CORBA.Any value)
			throws org.omg.TransformationInternalsAdmin.ConnectionError,
			org.omg.TransformationInternalsAdmin.TransformationError {
		try {
			org.omg.CORBA.Request _request = obj_ref._request(operation);

			_request.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(
					org.omg.CORBA.TCKind.tk_any));

			//TIDNotifTrace.print(TIDNotifTrace.USER, "     _request.add_in_arg()");
			org.omg.CORBA.Any reg_in = _request.add_in_arg();
			reg_in.insert_any(value);

			//_request.exceptions().add(org.omg.TransformationInternalsAdmin.DataErrorHelper.type());
			//_request.exceptions().add(org.omg.TransformationInternalsAdmin.ConnectionErrorHelper.type());
			//_request.exceptions().add(org.omg.TransformationInternalsAdmin.TransformationErrorHelper.type());

			_request.invoke();

			java.lang.Exception _exception = _request.env().exception();
			if (_exception != null) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, _exception);
				throw new org.omg.TransformationInternalsAdmin.TransformationError(
						key);
				//if (_exception instanceof org.omg.CORBA.UnknownUserException) 
				//{
				//org.omg.CORBA.UnknownUserException _userException =
				//(org.omg.CORBA.UnknownUserException) _exception;
				//if (_userException.except.type().equal(
				//org.omg.TransformationInternalsAdmin.DataErrorHelper.type())) 
				//{
				//throw org.omg.TransformationInternalsAdmin.DataErrorHelper.extract(_userException.except);
				//}
				//if (_userException.except.type().equal(
				//org.omg.TransformationInternalsAdmin.ConnectionErrorHelper.type())) 
				//{
				//throw org.omg.TransformationInternalsAdmin.ConnectionErrorHelper.extract(_userException.except);
				//}
				//if (_userException.except.type().equal(
				//org.omg.TransformationInternalsAdmin.TransformationErrorHelper.type())) 
				//{
				//throw org.omg.TransformationInternalsAdmin.TransformationErrorHelper.extract(_userException.except);
				//}
				//}
				//throw (org.omg.CORBA.SystemException) _exception;
			} else {
				org.omg.CORBA.Any _result;
				_result = _request.return_value().extract_any();
				return _result;
			}
		} catch (org.omg.CORBA.COMM_FAILURE ex) {
			// No existe el servidor
			TIDNotifTrace.print(TIDNotifTrace.ERROR,
					" *** Exception: COMM_FAILURE *** ");
			throw new org.omg.TransformationInternalsAdmin.ConnectionError(key);
		} catch (org.omg.CORBA.BAD_OPERATION ex2) {
			// No existe la operacion
			TIDNotifTrace.print(TIDNotifTrace.ERROR,
					" *** Exception: BAD_OPERATION *** ");
			throw new org.omg.TransformationInternalsAdmin.TransformationError(
					key);
		}
		// No existe la objeto
		// No responde en el tiempo dado
		//org.omg.CORBA.NO_RESPONSE
		// Excepcion en el cliente no capturada
		//org.omg.CORBA.UNKNOWN
		catch (Exception ex3) {
			TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex3);
			throw new org.omg.TransformationInternalsAdmin.TransformationError(
					key);
		}
	}

	public void destroy() {
		TransformingOperatorData transforming_data = getData();
		unregister(transforming_data);

		transforming_data.assignedTransformingRuleTable.clear();
		transforming_data.filterTable.clear();
		transforming_data.orderTable.clear();

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().delete(transforming_data);
		}
	}
}