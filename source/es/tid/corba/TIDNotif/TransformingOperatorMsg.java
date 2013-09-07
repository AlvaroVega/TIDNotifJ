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

public interface TransformingOperatorMsg
{
  public final static String GET_GRAMMAR = 
   "-> TransformingOperatorMsg.grammar()";
  public final static String ADD_TRANSFORMING_RULE[] =
   { "-> TransformingOperatorImpl.add_transforming_rule(",
                   null, ", ", null, "\n      ", null, "\n      ", null, ")" };
  public final static String INSERT_TRANSFORMING_RULE[] =
   {"-> TransformingOperatorImpl.insert_transforming_rule(",null,", ",null,")"};
  public final static String MOVE_TRANSFORMING_RULE[] =
   {"-> TransformingOperatorImpl.move_transforming_rule(",null, ", ", null,")"};
  public final static String GET_TRANSFORMING_RULE[] =
   { "-> TransformingOperatorImpl.get_transforming_rule(", null, ")" };
  public final static String GET_TRANSFORMING_RULE_ORDER[] =
   { "-> TransformingOperatorImpl.get_transforming_rule_order(", null, ")" };
  public final static String DELETE_TRANSFORMING_RULE[] =
   { "-> TransformingOperatorImpl.delete_transforming_rule(", null, ")" };
  public final static String REPLACE_TRANSFORMING_RULE[] =
   { "-> TransformingOperatorImpl.replace_transforming_rule(Id: ",
                                                      null, ", <", null, ">" };
  public final static String GET_TRANSFORMING_RULES =
   "-> TransformingOperatorImpl.get_transforming_rules()";
  public final static String GET_TRANSFORMING_RULES_ORDER =
   "-> TransformingOperatorImpl.get_transforming_rules_order()";

  public final static String TRANSFORM_VALUE
   = "-> TransformingOperatorImpl.transform_value(any)";

  public final static String[] NEW_TRANSFORM =
   { " * Tranform: {", null, "} - Id = ", null, " - Position = ", null };
  public final static String ARBOL = 
   " * Arbol asociado: ";
  public final static String NOT_FOUND_TRANSFORMING_RULE =
   "  Transforming Rule: null";
  public final static String FOUND_ARBOL = 
   "  Arbol asociado: removed!";
  public final static String NOT_FOUND_ARBOL = 
   "  Arbol asociado: NOT FOUND";
  public final static String TRANSFORMING_RULE[] =
   { "  Rule Order: ",  null, " - Rule Id: ", null, " *** ", null , " ***" };
  public final static String[] FOUND_TRANSFORMING_RULE =
   { "  Transforming Rule: {", null, "}" };
  public final static String[] FOUND_REPLACED_TRANSFORMING_RULE =
   { "  Antigua transforming rule: {", null, "}" };
  public final static String NOT_FOUND_REPLACED_TRANSFORMING_RULE =
   "  Antigua transforming rule: null";

  public final static String REGISTER[] =
   { " # TransformingOperatorImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # TransformingOperatorImpl.unregister() -> ", null };
}
