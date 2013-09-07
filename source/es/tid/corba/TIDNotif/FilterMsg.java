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

public interface FilterMsg
{
  public final static String GET_GRAMMAR = 
   "-> DiscriminatorImpl.grammar()";
  public final static String ADD_CONSTRAINT =
   "-> DiscriminatorImpl.add_constraint( constraint_exp )";
  public final static String ADD_CONSTRAINTS =
    "-> DiscriminatorImpl.add_constraints( constraint_list )";
  
  public final static String GET_CONSTRAINT[] =
   { "-> DiscriminatorImpl.get_constraint(", null, ")" };
  public final static String REMOVE_CONSTRAINT[] =
   { "-> DiscriminatorImpl.remove_constraint(", null, ")" };
  public final static String REMOVE_ALL_CONSTRAINTS =
   "-> DiscriminatorImpl.remove_constraint()";
  public final static String REPLACE_CONSTRAINT[] =
   { "-> DiscriminatorImpl.replace_constraint(Id: ", null, ", <", null, ">" };
  public final static String EVAL_VALUE =
   "-> DiscriminatorImpl.eval_value(any)";
  public final static String GET_CONSTRAINTS =
   "-> DiscriminatorImpl.get_constraints()";
  public final static String REGISTER[] =
   { " # DiscriminatorImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # DiscriminatorImpl.unregister() -> ", null };

  public final static String NEW_CONSTRAINT[] =
   { "* Constraint: {", null, "} - Id = ", null };
  public final static String FOUND_CONSTRAINT[] =
   { "  Constraint: {", null, "}" };
  public final static String FOUND_REPLACED_CONSTRAINT[] =
   { "  Antigua constraint: {", null, "}" };
  public final static String ARBOL = 
   " * Arbol asociado: ";
  public final static String NOT_FOUND_CONSTRAINT = 
   "  Constraint: null";
  public final static String FOUND_ARBOL = 
   "  Arbol asociado: removed!";
  public final static String NOT_FOUND_ARBOL = 
   "  Arbol asociado: NOT FOUND";
  public final static String NOT_FOUND_REPLACED_CONSTRAINT =
   "  Antigua constraint: null";
  public final static String TO_MANY_CONSTRAINTS =
   "  ERROR: Numero maximo de constraints alcanzado.";
}
