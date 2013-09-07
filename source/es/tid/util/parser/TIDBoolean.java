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

/* Generated By:JJTree: Do not edit this line. TIDBoolean.java */

package es.tid.util.parser;

import es.tid.util.parser.Operations.TIDBooleanValues;

public class TIDBoolean extends SimpleNode implements TIDBooleanValues
{
  private static final String NODENAME = "Boolean: ";

  private String name;
  private boolean value;

  public TIDBoolean(int id) {
    super(id);
    super.node_type = NodeType.TIDBoolean;
  }

  public TIDBoolean(ConstraintParser p, int id) {
    super(p, id);
    super.node_type = NodeType.TIDBoolean;
  }

  public void setName(String n) {
    if (n.compareTo(TRUE_STR) == 0)
    {
      name = TRUE_STR;
      value = true;
    }
    else
    {
      name = FALSE_STR;
      value = false;
    }
  }

  public boolean getValue()
  {
    return this.value;
  }

  public String toString() {
    return NODENAME + name;
  }

  public TypeValuePair evaluate( org.omg.DynamicAny.DynAny dynany )
  {
    return new TypeValuePair(value);
  }
}