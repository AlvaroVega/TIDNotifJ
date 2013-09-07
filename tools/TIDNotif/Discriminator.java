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

package es.tid.corba.TIDNotif.tools;

//import org.omg.ConstraintAdmin.DiscriminatorHelper;

public class Discriminator
{		
  private static final String NAME = "Discriminator";

  private static final String OP_ADD_CONSTRAINT     = "add_constraint";
  private static final String OP_GET_CONSTRAINT     = "get_constraint";
  private static final String OP_REMOVE_CONSTRAINT  = "remove_constraint";
  private static final String OP_REPLACE_CONSTRAINT = "replace_constraint";
  private static final String OP_LIST_CONSTRAINTS   = "list_constraints";

  private static final int _OP_UNKNOWN            = -1;
  private static final int _OP_ADD_CONSTRAINT     =  0;
  private static final int _OP_GET_CONSTRAINT     =  1;
  private static final int _OP_REMOVE_CONSTRAINT  =  2;
  private static final int _OP_REPLACE_CONSTRAINT =  3;
  private static final int _OP_LIST_CONSTRAINTS   =  4;

  private static es.tid.corba.TIDNotif.tools.Util utils = 
                                        new es.tid.corba.TIDNotif.tools.Util();

  private static void showHelp()
  {
    System.err.println("");
    System.err.println(
      "Discriminator add_constraint \"expresion\" < discriminator.ior");
    System.err.println(
      "Discriminator get_constraint Id < discriminator.ior");
    System.err.println(
      "Discriminator remove_constraint Id < discriminator.ior");
    System.err.println(
      "Discriminator replace_constraint Id \"expresion\" < discriminator.ior");
    System.err.println(
      "Discriminator list_constraints < discriminator.ior");
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
    String constraint = null;
    int id = -1;

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
      // Discriminator add_constraint \"expresion\" < discriminator.ior
      if ( args[i].compareTo(OP_ADD_CONSTRAINT) == 0 )
      {
        operation = _OP_ADD_CONSTRAINT;
        constraint = args[++i];
      }
      // Discriminator get_constraint Id < discriminator.ior
      else if ( args[i].compareTo(OP_GET_CONSTRAINT) == 0 )
      {
        operation = _OP_GET_CONSTRAINT;
        try
        {
          id = Integer.parseInt(args[++i]);
        }
        catch (java.lang.NumberFormatException ex)
        {
          System.err.println("Parametro Id invalido, debe ser un int.");
          showHelp();
          System.exit(1);
        }
      }
      // Discriminator remove_constraint Id < discriminator.ior
      else if ( args[i].compareTo(OP_REMOVE_CONSTRAINT) == 0 )
      {
        operation = _OP_REMOVE_CONSTRAINT;
        try
        {
          id = Integer.parseInt(args[++i]);
        }
        catch (java.lang.NumberFormatException ex)
        {
          System.err.println("Parametro Id invalido, debe ser un int.");
          showHelp();
          System.exit(1);
        }
      }
      // Discriminator replace_constraint Id \"expresion\" < discriminator.ior
      else if ( args[i].compareTo(OP_REPLACE_CONSTRAINT) == 0 )
      {
        operation = _OP_REPLACE_CONSTRAINT;
        try
        {
          id = Integer.parseInt(args[++i]);
        }
        catch (java.lang.NumberFormatException ex)
        {
          System.err.println("Parametro Id invalido, debe ser un int.");
          showHelp();
          System.exit(1);
        }
        constraint = args[++i];
      }
      // Discriminator list_constraints < discriminator.ior
      else if ( args[i].compareTo(OP_LIST_CONSTRAINTS) == 0 )
      {
        operation = _OP_LIST_CONSTRAINTS;
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
/*
    System.err.println("Operation: " + operation);
    System.err.println("Constraint: " + constraint);
    System.err.println("Id: " + id);
*/

    if (operation == _OP_UNKNOWN)
    {
      System.err.println(
        "Operacion invalida (add_constraint|get_constraint|remove_constraint|");
      System.err.println(
        "                    replace_constraint|list_constraints)");
      System.exit(1);
    }

    org.omg.CORBA.Object object_ref = null; 
//     org.omg.ConstraintAdmin.Discriminator discriminator = null;
//     try
//     {
//       object_ref = orb.string_to_object(utils.readIORString(null));
//       discriminator = DiscriminatorHelper.narrow( object_ref );
//     }
//     catch (Exception e)
//     {
//       e.printStackTrace();
//       System.exit(1);
//     }

//     org.omg.ConstraintAdmin.AssignedConstraint[] constraints = null;

    switch (operation)
    {
      // Channel create_supplier [name] [-m (ior | url)] < channel.ior
      case _OP_ADD_CONSTRAINT:
        try
        {
          id = discriminator.add_constraint(constraint);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        System.out.println(Integer.toString(id));
        break;

      // Channel create_consumer [name] [-m (ior | url)] < channel.ior
      case _OP_GET_CONSTRAINT:
        try
        {
          constraint = discriminator.get_constraint(id);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        System.out.println(constraint);
        break;

      // Channel find_supplier [name] [-m (ior | url)] < channel.ior
      case _OP_REMOVE_CONSTRAINT:
        try
        {
          discriminator.remove_constraint(id);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        break;

      // Channel find_consumer [name] <[-m (ior | url)]  channel.ior
      case _OP_REPLACE_CONSTRAINT:
        try
        {
          id = discriminator.replace_constraint(id, constraint);
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        System.out.println(Integer.toString(id));
        break;

      // Channel list_suppliers [-m (ior | url)] < channel.ior
      case _OP_LIST_CONSTRAINTS:
        try
        {
          constraints = discriminator.get_constraints();
        }
        catch ( java.lang.Exception ex )
        {
          ex.printStackTrace();
          System.exit(1);
        }
        for (int i = 0; i < constraints.length; i++)
        {
          System.out.println(
               " " + constraints[i].id + ": \"" + constraints[i].value + "\"");
        }
    }

    if (orb instanceof es.tid.TIDorbj.core.TIDORB)
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
