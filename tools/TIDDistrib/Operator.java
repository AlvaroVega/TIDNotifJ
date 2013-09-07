///////////////////////////////////////////////////////////////////////////
//
// File          :
// Description   :
//
// Author/s      : Alvaro Rodriguez
// Project       :
// Rel           :
// Created       :
// Revision Date :
// Rev. History  :
//
// Copyright 2001 Telefonica, I+D. All Rights Reserved.
//
// The copyright of the software program(s) is property of Telefonica.
// The program(s) may be used, modified and/or copied only with the
// express written consent of Telefonica or in acordance with the terms
// and conditions stipulated in the agreement/contract under which
// the program(s) have been supplied.
//
///////////////////////////////////////////////////////////////////////////

package es.tid.corba.TIDDistrib.tools;

import org.omg.TransformationAdmin.TransformingOperator;
import org.omg.TransformationAdmin.TransformingOperatorHelper;

public class Operator
{		
  private static final String NAME = "Operator";

  private static final String OP_ADD_RULE     = "add_rule";
  private static final String OP_INSERT_RULE  = "insert_rule";
  private static final String OP_MOVE_RULE    = "move_rule";
  private static final String OP_GET_RULE     = "get_rule";
  private static final String OP_GET_ORDER    = "get_order";
  private static final String OP_REMOVE_RULE  = "remove_rule";
  private static final String OP_REPLACE_RULE = "replace_rule";
  private static final String OP_GET_RULES    = "get_rules";
  private static final String OP_GET_ORDERS   = "get_orders";

  private static final int _OP_UNKNOWN      = -1;
  private static final int _OP_ADD_RULE     =  0;
  private static final int _OP_INSERT_RULE  =  1;
  private static final int _OP_MOVE_RULE    =  2;
  private static final int _OP_GET_RULE     =  3;
  private static final int _OP_GET_ORDER    =  4;
  private static final int _OP_REMOVE_RULE  =  5;
  private static final int _OP_REPLACE_RULE =  6;
  private static final int _OP_GET_RULES    =  7;
  private static final int _OP_GET_ORDERS   =  8;

  private static es.tid.corba.TIDDistrib.tools.Util utils = 
                                      new es.tid.corba.TIDDistrib.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
      "Operator add_rule Id \"expresion\" ior_file operation < operator.ior");
    System.err.println(
     "Operator insert_rule Id Position \"expresion\" ior_file operation <operator.ior");
    System.err.println(
     "Operator move_rule Id Position < operator.ior");
    System.err.println(
     "Operator replace_rule Id \"expresion\" ior_file operation <operator.ior");
    System.err.println(
     "Operator remove_rule Id < operator.ior");
    System.err.println(
     "Operator get_rule Id < operator.ior");
    System.err.println(
     "Operator get_order Id < operator.ior");
    System.err.println(
     "Operator get_rules < operator.ior");
    System.err.println(
     "Operator get_orders < operator.ior");
  }

  public static void main( String args [] )
  {
    java.util.Properties props = new java.util.Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
    System.getProperties().put( "org.omg.CORBA.ORBSingletonClass",
                                          "es.tid.TIDorbj.core.SingletonORB" );
    org.omg.CORBA.ORB orb = null;
    try
    {
      orb = org.omg.CORBA.ORB.init(args, props);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      System.exit(1);
    }		

    args = utils.filterArgs(args);

    if (args.length < 1)
    {
      System.err.println("Error en el numero de parametros");
      showHelp();
      System.exit(1);
    }
    
    int operation = _OP_UNKNOWN;
    String id = null;
    String constraint = null;
    String ior_file = null;
    String object_operation = null;
    int order = -1;
    boolean debug = false;

// Debug 
/*
    for ( int i = 0; i < args.length; i++ )
    {
      System.err.println(args[i]);
    }
*/
    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      // Debug
      if ( args[i].compareTo("-debug") == 0 )
      {
        debug = true;
      }
      // Operator add_rule Id \"expresion\" ior_file operation < operator.ior
      else if ( args[i].compareTo(OP_ADD_RULE) == 0 )
      {
        if (args.length < 5)
        {
          System.err.println("Numero de parametros erroneo.");
          showHelp();
          System.exit(1);
        }
        operation = _OP_ADD_RULE;
        id = args[++i];
        constraint = args[++i];
        ior_file = args[++i];
        object_operation = args[++i];
      }
      // Operator insert_rule Id Position \"expresion\" ior_file operation<operator.ior
      else if ( args[i].compareTo(OP_INSERT_RULE) == 0 )
      {
        if (args.length < 6)
        {
          System.err.println("Numero de parametros erroneo.");
          showHelp();
          System.exit(1);
        }
        operation = _OP_INSERT_RULE;
        id = args[++i];
        try
        {
          order = Integer.parseInt(args[++i]);
        }
        catch (java.lang.NumberFormatException ex)
        {
          System.err.println("Parametro Order invalido, debe ser un int.");
          showHelp();
          System.exit(1);
        }
        constraint = args[++i];
        ior_file = args[++i];
        object_operation = args[++i];
      }
      // Operator move_rule Id Position < operator.ior
      else if ( args[i].compareTo(OP_MOVE_RULE) == 0 )
      {
        if (args.length < 3)
        {
          System.err.println("Numero de parametros erroneo.");
          showHelp();
          System.exit(1);
        }
        operation = _OP_MOVE_RULE;
        id = args[++i];
        try
        {
          order = Integer.parseInt(args[++i]);
        }
        catch (java.lang.NumberFormatException ex)
        {
          System.err.println("Parametro Order invalido, debe ser un int.");
          showHelp();
          System.exit(1);
        }
      }
      // Operator replace_rule Id \"expresion\" ior_file operation <operator.ior
      else if ( args[i].compareTo(OP_REPLACE_RULE) == 0 )
      {
        if (args.length < 5)
        {
          System.err.println("Numero de parametros erroneo.");
          showHelp();
          System.exit(1);
        }
        operation = _OP_REPLACE_RULE;
        id = args[++i];
        constraint = args[++i];
        ior_file = args[++i];
        object_operation = args[++i];
      }
      // Operator remove_rule Id < operator.ior
      else if ( args[i].compareTo(OP_REMOVE_RULE) == 0 )
      {
        if (args.length < 2)
        {
          System.err.println("Numero de parametros erroneo.");
          showHelp();
          System.exit(1);
        }
        operation = _OP_REMOVE_RULE;
        id = args[++i];
      }
      // Operator get_rule Id < operator.ior
      else if ( args[i].compareTo(OP_GET_RULE) == 0 )
      {
        if (args.length < 2)
        {
          System.err.println("Numero de parametros erroneo.");
          showHelp();
          System.exit(1);
        }
        operation = _OP_GET_RULE;
        id = args[++i];
      }
      // Operator get_order Id < operator.ior
      else if ( args[i].compareTo(OP_GET_ORDER) == 0 )
      {
        if (args.length < 2)
        {
          System.err.println("Numero de parametros erroneo.");
          showHelp();
          System.exit(1);
        }
        operation = _OP_GET_ORDER;
        id = args[++i];
      }
      // Operator get_rules < operator.ior
      else if ( args[i].compareTo(OP_GET_RULES) == 0 )
      {
        operation = _OP_GET_RULES;
      }
      // Operator get_orders < operator.ior
      else if ( args[i].compareTo(OP_GET_ORDERS) == 0 )
      {
        operation = _OP_GET_ORDERS;
      }
      else if ( ( args[i].compareTo("-help") == 0 ) ||
                ( args[i].compareTo("-h") == 0 ) ||
                ( args[i].compareTo("/h") == 0 ) ||
                ( args[i].compareTo("-?") == 0 ) ||
                ( args[i].compareTo("/?") == 0 ) )
      {
        showHelp();
        System.exit(0);
      }
      else
      {
        System.err.println("Parametro invalido.");
        showHelp();
        System.exit(1);
      }
    }

// Debug 
    if (debug)
    {
      System.err.println("Operation: " + operation);
      System.err.println("Id: " + id);
      System.err.println("Constraint: " + constraint);
      System.err.println("IOR file: " + ior_file);
      System.err.println("Id: " + id);
      System.err.println("Operation: " + object_operation);
      System.err.println("Order: " + order);
    }

    if (operation == _OP_UNKNOWN)
    {
      System.err.println(
          "Operacion invalida (add_rule|insert_rule|move_rule||replace_rule|");
      System.err.println(
          "                    remove_rule|get_rule|get_order|get_rules|");
      System.err.println(
          "                    get_orders)");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
    TransformingOperator operator = null;
    try
    {
      object_ref = orb.string_to_object(utils.readIORString(null));
      operator = TransformingOperatorHelper.narrow( object_ref );
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    org.omg.TransformationAdmin.TransformingRule rule = null;
    //org.omg.TransformationAdmin.AssignedRuleOrder order = null;
    org.omg.TransformationAdmin.AssignedTransformingRule[] assigned_rules=null;
    org.omg.TransformationAdmin.AssignedRuleOrder[] assigned_orders = null;

    switch (operation)
    {
      // Operator add_rule Id \"expresion\" ior_file operation < operator.ior
      case _OP_ADD_RULE:
        try
        {
          object_ref = orb.string_to_object(utils.readIORString(ior_file));
          order = operator.add_transforming_rule(
                                   id,constraint,object_ref,object_operation );
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        System.out.println(Integer.toString(order));
        break;

      // Operator insert_rule Id Position \"expresion\" ior_file operation<o.ior
      case _OP_INSERT_RULE:
        try
        {
          object_ref = orb.string_to_object(utils.readIORString(ior_file));
          operator.insert_transforming_rule(
                         order, id, constraint, object_ref, object_operation );
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        break;

      // Operator move_rule Id Position < operator.ior
      case _OP_MOVE_RULE:
        try
        {
          id = operator.move_transforming_rule( id, order );
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        System.out.println(id);
        break;

      // Operator get_rule Id \"expresion\" < operator.ior
      case _OP_GET_RULE:
        try
        {
          rule = operator.get_transforming_rule( id );
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        try
        {
          ior_file = orb.object_to_string(rule.object_ref);
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        System.out.println(rule.expression);
        System.out.println(ior_file);
        System.out.println(rule.operation);
        break;

      // Operator get_order Id < operator.ior
      case _OP_GET_ORDER:
        try
        {
          order = operator.get_transforming_rule_order( id );
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        System.out.println(Integer.toString(order));
        break;

      // Operator remove_rule Id < operator.ior
      case _OP_REMOVE_RULE:
        try
        {
          operator.delete_transforming_rule( id );
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        break;

      // Operator replace_rule Id \"expresion\" ior_file operation <operator.ior
      case _OP_REPLACE_RULE:
        try
        {
          object_ref = orb.string_to_object(utils.readIORString(ior_file));
          id = operator.replace_transforming_rule(
                                id, constraint, object_ref, object_operation );
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        System.out.println(id);
        break;

      // Operator get_rules < operator.ior
      case _OP_GET_RULES:
        try
        {
          assigned_rules = operator.get_transforming_rules();
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < assigned_rules.length; i++)
        {
          try
          {
            ior_file = orb.object_to_string(assigned_rules[i].rule.object_ref);
          }
          catch (Exception e)
          {
            e.printStackTrace();
            System.exit(1);
          }
          System.out.println("");
          System.out.println("Id: " + assigned_rules[i].id);
          System.out.println("Constraint: "+assigned_rules[i].rule.expression);
          System.out.println(ior_file);
          System.out.println("Operation: " + assigned_rules[i].rule.operation);
        }
        break;

      // Operator get_orders < operator.ior
      case _OP_GET_ORDERS:
        try
        {
          assigned_orders = operator.get_transforming_rules_order();
        }
        catch (Exception e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < assigned_orders.length; i++)
        {
          System.out.println("");
          System.out.println("Id: " + assigned_orders[i].id);
          System.out.println("Order: " + 
                                Integer.toString(assigned_orders[i].position));
        }
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
