/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 7 $
* Date: $Date: 2008-01-03 11:50:19 +0100 (Thu, 03 Jan 2008) $
* Last modified by: $Author: avega $
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

public class TypeValuePair implements TIDExprValueType, TIDConstraintMsg
{
  public int type;
  public TIDExprValue val = new TIDExprValue();

  public TypeValuePair()
  {
    type = ERRTYPE;
  }

  public TypeValuePair(boolean b)
  {
    type = BOOLTYPE;
    val.objVal = new Boolean(b);
  }

  public TypeValuePair(int i)
  {
    type = ITYPE;
    val.objVal = new Integer(i);
  }

  public TypeValuePair(float f)
  {
    type = FTYPE;
    val.objVal = new Float(f);
  }

  public TypeValuePair(String str)
  {
    type = STRTYPE;
    val.objVal = new String(str);
  }

  public TypeValuePair(EnumValue myenum)
  {
    type = ENUMTYPE;
    val.objVal = new EnumValue(myenum);
  }

  public TypeValuePair(int n_value, String str_value, ArrayList str_values)
  {
    type = ENUMTYPE;
    val.objVal = new EnumValue(n_value, str_value, str_values);
  }

  public TypeValuePair(TypeValuePair tvp)
  {
    TypeValuePair(tvp);
  }

  private void TypeValuePair(TypeValuePair tvp)
  {
    type = tvp.type;

    switch (type)
    {
      case TypeValuePair.BOOLTYPE:
        val.objVal = new Boolean(((Boolean)tvp.val.objVal).booleanValue());
        break;
      case TypeValuePair.ITYPE:
        val.objVal = new Integer(((Integer)tvp.val.objVal).intValue());
        break;
      case TypeValuePair.FTYPE:
        val.objVal = new Float(((Float)tvp.val.objVal).floatValue());
        break;
      case TypeValuePair.STRTYPE:
        val.objVal = new String((String)tvp.val.objVal);
        break;
      case TypeValuePair.ENUMTYPE:
        val.objVal = new EnumValue((EnumValue)tvp.val.objVal);
        break;
      default:
        val.objVal = null;
        TIDParser.print(TIDParser.DEBUG, TVP_MSG_EXCEPTION);
    }
  }

  public void assignTo(TypeValuePair t)
  {
    TypeValuePair(t);
  }
  
  public void dump()
  {
    TIDParser.print(TIDParser.DEBUG, TVP_MSG_TYPEVALUEPAIR + type);
    switch (type)
    {
      case BOOLTYPE:
        if (((Boolean)val.objVal).booleanValue())
          TIDParser.print(TIDParser.DEBUG, TVP_MSG_TRUE);
        else
          TIDParser.print(TIDParser.DEBUG, TVP_MSG_FALSE);
        break;
      case ITYPE:
        TIDParser.print(TIDParser.DEBUG,
                              TVP_MSG_DVAL + ((Integer)val.objVal).intValue());
        break;
      case FTYPE:
        TIDParser.print(TIDParser.DEBUG, 
                              TVP_MSG_DVAL + ((Float)val.objVal).floatValue());
        break;
      case STRTYPE:
        TIDParser.print(TIDParser.DEBUG, 
                                         TVP_MSG_STRVAL + (String)val.objVal);
        break;
      case ENUMTYPE:
        if ( ((EnumValue)val.objVal).stringValue() != null )
        {
          TIDParser.print(TIDParser.DEBUG, 
                     TVP_MSG_ENUMVAL + ((EnumValue)val.objVal).stringValue() );
        }
        else
        {
          TIDParser.print(TIDParser.DEBUG,
                        TVP_MSG_ENUMVAL + ((EnumValue)val.objVal).numValue() );
        }
        break;
      case ERRTYPE:
        TIDParser.print(TIDParser.DEBUG, TVP_MSG_ERRTYPE );
        break;
      default:
        break;
    }
  }

  public boolean isTrue()
  {
    switch (type)
    {
      case BOOLTYPE:
        return ((Boolean)val.objVal).booleanValue();
      case ITYPE:
        if ( ((Integer)val.objVal).intValue() == 0 )
          return false;
        else
          return true;
      case FTYPE:
        if ( ((Float)val.objVal).floatValue() == 0.0 )
          return false;
        else
          return true;
    }
    return false;
  }
}
