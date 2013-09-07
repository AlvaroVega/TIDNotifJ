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

package es.tid.util.parser;

import java.util.ArrayList;

public class EnumValue extends Object
{
  private int num_value = -1;
  private String string_value = null;
  private java.util.ArrayList string_values = null;

  public EnumValue()
  {
    super();
  } 

  public EnumValue(String s, ArrayList al)
  {
    super();
    string_value = s;
    string_values = al;
  } 

  public EnumValue(int i, ArrayList al)
  {
    super();
    num_value = i;
    string_values = al;
  } 

  public EnumValue(EnumValue ev)
  {
    super();
    num_value = ev.num_value;
    string_value = ev.string_value;
    string_values = ev.string_values;
  } 

  public EnumValue(int i, String s, java.util.ArrayList al)
  {
    super();
    num_value = i;
    string_value = s;
    string_values = al;
  } 

  public int numValue()
  {
    return num_value;
  }

  public String stringValue()
  {
    return string_value;
  }

  public int stringValues(String s)
  {
    if (string_values != null)
    {
      return string_values.indexOf(s);
    }
    return -1;
  }
}
