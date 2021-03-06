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

/* Generated By:JJTree: Do not edit this line. TIDStringArray.java */

package es.tid.util.parser;

public class TIDStringArray extends SimpleNode implements TIDConstraintMsg
{
  private static final String NODENAME = "StringArray: ";

  private java.util.List names;

  public TIDStringArray(int id) {
    super(id);
    names = new java.util.ArrayList();
  }

  public TIDStringArray(ConstraintParser p, int id) {
    super(p, id);
    names = new java.util.ArrayList();
  }

  public void setName(String n) {
    names.add(n.substring(1,n.length()-1));
  }

  public String getName()
  {
    StringBuffer n = new StringBuffer("(");
    for (int i = 0; i < names.size(); i++)
    {
      if (i > 0) n.append(",");
      n.append("'").append(names.get(i)).append("'");
    }
    n.append(")");
    return n.toString();
  }

  public String toString() {
    return NODENAME + getName();
  }

  public TypeValuePair evaluate( org.omg.DynamicAny.DynAny dynany )
  {
    return new TypeValuePair();
  }

  public TypeValuePair evaluate_in( TypeValuePair data_value, 
                                    org.omg.DynamicAny.DynAny dynany )
  {
    if (data_value.type != TypeValuePair.STRTYPE)
    {
      TIDParser.print(TIDParser.ERROR, TSA_VALUE_ERROR);
      return new TypeValuePair();
    }
    return new TypeValuePair(names.contains((String)data_value.val.objVal));
  }
}
