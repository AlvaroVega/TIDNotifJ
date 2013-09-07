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

public interface MappingFilterMsg
{
  public final static String GET_GRAMMAR =
   "-> MappingDiscriminatorImpl.grammar()";
  public final static String GET_VALUE_TYPE =
   "-> MappingDiscriminatorImpl.value_type()";
  public final static String GET_DEFAULT_VALUE =
   "-> MappingDiscriminatorImpl.default_value()";
  public final static String SET_DEFAULT_VALUE =
   "-> MappingDiscriminatorImpl.default_value(any)";
  public final static String ADD_MAPPING_RULE =
   "-> MappingDiscriminatorImpl.add_mapping_constraint(new_rule, any)";
  public final static String GET_MAPPING_RULE[] =
   { "-> MappingDiscriminatorImpl.get_mapping_constraint(", null, ")" };
  public final static String DELETE_MAPPING_RULE[] =
   { "-> MappingDiscriminatorImpl.delete_mapping_constraint(", null, ")" };
  public final static String DELETE_ALL_MAPPING_RULES =
  "-> MappingDiscriminatorImpl.delete_mapping_constraint()";
  public final static String REPLACE_MAPPING_RULE[] =
   { "-> MappingDiscriminatorImpl.replace_mapping_constraint(Id: ", 
                                                       null, ", <", null, ">"};
  public final static String MATCHES[] =
   { "-> MappingDiscriminatorImpl.matches(", null, ")" };
  public final static String MATCH =
   "-> MappingDiscriminatorImpl.match(any, anyholder)";
  public final static String GET_MAPPINGS_RULES =
   "-> MappingDiscriminatorImpl.get_mapping_rules()";
  public final static String REGISTER[] =
   { " # MappingDiscriminatorImpl.register() -> ", null };
  public final static String UNREGISTER[] =
   { " # MappingDiscriminatorImpl.unregister() -> ", null };

  public final static String EXTRACT_LONG =
   "   ERROR: _default_value.type()";
  public final static String TOO_MAPPING_RULES =
   "   ERROR: Numero maximo de mapping rules (constraints) alcanzado.";
  public final static String[] NEW_CONSTRAINT =
   { " * Constraint: {", null, "} - Id = ", null };
  public final static String ARBOL = " * Arbol asociado: ";
  public final static String[] FOUND_MAPPING_RULE =
   { "  Constraint: {", null, "}" };
  public final static String NOT_FOUND_MAPPING_RULE = "  Constraint: null";
  public final static String FOUND_ARBOL = "  Arbol asociado: removed!";
  public final static String NOT_FOUND_ARBOL = "  Arbol asociado: NOT FOUND";
  public final static String[] FOUND_REPLACED_MAPPING_RULE =
   { "  Antigua constraint: {", null, "}" };
  public final static String NOT_FOUND_REPLACED_MAPPING_RULE =
   "  Antigua constraint: null";

  public final static String TO_DO_1 =
   " > Creamos un Any copn el valor: TODO...";
}
