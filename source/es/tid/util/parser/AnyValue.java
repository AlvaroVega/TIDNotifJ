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

public class AnyValue implements TIDConstraintMsg
{
  public AnyValue() 
  {
  }

  public org.omg.DynamicAny.DynAny value(org.omg.DynamicAny.DynAny dynAny)
  {
     return (org.omg.DynamicAny.DynAny) dynAny._duplicate();
  }

  public TypeValuePair evaluate( org.omg.DynamicAny.DynAny dynany )
  {
    org.omg.DynamicAny.DynAny chosen = value(dynany);

    if (chosen._non_existent()) 
    {
      TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_CHOSEN);
      return new TypeValuePair();
    }
    return processResult(chosen);
  }

  public TypeValuePair evaluate_in( TypeValuePair data_value, 
                                    org.omg.DynamicAny.DynAny dynany )
  {
    org.omg.DynamicAny.DynAny chosen = value(dynany);

    if (chosen._non_existent()) 
    {
      TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_CHOSEN);
      return new TypeValuePair();
    }
    return processResult(data_value, chosen);
  }


  private ArrayList get_enum_values( org.omg.DynamicAny.DynEnum dyn_enum )
  {
    ArrayList str_values = new ArrayList();
    try
    {
      for (int i = 0; i < 99999; i++)
      {
        dyn_enum.set_as_ulong(i);
        str_values.add(dyn_enum.get_as_string());
      }
    } catch (Exception ex) {};
    return str_values;
  }

  private TypeValuePair processResult(org.omg.DynamicAny.DynAny chosen)
  {
    try 
    {
      chosen.rewind();
    }
    catch (Exception e)
    {
      return new TypeValuePair();
    }

    org.omg.CORBA.TypeCode type;
    try 
    {
      type = chosen.type();
    }
    catch (Exception ex)
    {
      TIDParser.printStackTrace(TIDParser.ERROR, ex);
      return new TypeValuePair();
    }

    int tck_value;

    while( (tck_value = type.kind().value()) == org.omg.CORBA.TCKind._tk_alias)
    {
      try 
      {
        type = type.content_type();
      }
      catch (Exception e)
      {
        TIDParser.printStackTrace(TIDParser.ERROR, e);
        return new TypeValuePair();
      }
    }

    switch(tck_value)
    {
      case org.omg.CORBA.TCKind._tk_short:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_SHORT);
        short value;
        try
        {
          value = chosen.get_short();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return  new TypeValuePair( (int)value );
      }
      case org.omg.CORBA.TCKind._tk_long:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_LONG);
        int value;
        try
        {
          value = chosen.get_long();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( value );
      }
      case org.omg.CORBA.TCKind._tk_longlong:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_LONGLONG);
        long value;
        try
        {
          value = chosen.get_longlong();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( (new Long(value)).floatValue() );
      }
      case org.omg.CORBA.TCKind._tk_ushort:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_USHORT);
        short value;
        try
        {
          value = chosen.get_ushort();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( (int)value );
      }
      case org.omg.CORBA.TCKind._tk_ulong:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_ULONG);
        int value;
        try
        {
          value = chosen.get_ulong();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( value );
      }
      case org.omg.CORBA.TCKind._tk_float:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_FLOAT);
        float value;
        try
        {
          value = chosen.get_float();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( value );
      }
      case org.omg.CORBA.TCKind._tk_double:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_DOUBLE);
        double value;
        try
        {
          value = chosen.get_double();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( (float)value );
      }
      case org.omg.CORBA.TCKind._tk_boolean:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_BOOLEAN);
        boolean value;
        try
        {
          value = chosen.get_boolean();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( value );
      }
      case org.omg.CORBA.TCKind._tk_char:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_CHAR);
        char c[] = new char[1];
        try
        {
          c[0] = chosen.get_char();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( new String(c) );
      }
      case org.omg.CORBA.TCKind._tk_octet:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_OCTET);
        byte c;
        try
        {
          c = chosen.get_octet();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( Byte.toString(c) );
      }
      case org.omg.CORBA.TCKind._tk_string:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_STRING);
        String str;
        try
        {
          str = chosen.get_string();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( str );
      }
      case org.omg.CORBA.TCKind._tk_enum:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_ENUM);
        org.omg.DynamicAny.DynEnum dynenum;
        try
        {
          dynenum = org.omg.DynamicAny.DynEnumHelper.narrow(chosen);
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }

        int num_value;
        try
        {
          num_value = dynenum.get_as_ulong();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }

        String str;
        try
        {
          str = dynenum.get_as_string();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        return new TypeValuePair( num_value, str, get_enum_values(dynenum) );
      }
      default:
        ANYVALUE_UNKNOWN[1]=Integer.toString(tck_value);
        TIDParser.print(TIDParser.DEBUG, ANYVALUE_UNKNOWN);
    }
    return new TypeValuePair();
  }

  private TypeValuePair processResult( TypeValuePair data_value, 
                                       org.omg.DynamicAny.DynAny chosen)
  {
    try 
    {
      chosen.rewind();
    }
    catch (Exception e)
    {
      return new TypeValuePair();
    }

    org.omg.CORBA.TypeCode type;
    try 
    {
      type = chosen.type();
    }
    catch (Exception ex)
    {
      TIDParser.printStackTrace(TIDParser.ERROR, ex);
      return new TypeValuePair();
    }

    int tck_value;

    while( (tck_value = type.kind().value()) == org.omg.CORBA.TCKind._tk_alias)
    {
      try 
      {
        type = type.content_type();
      }
      catch (Exception e)
      {
        TIDParser.printStackTrace(TIDParser.ERROR, e);
        return new TypeValuePair();
      }
    }

    switch(tck_value)
    {
      //
      // NUEVO (por si la estructura estuviera dentro de un ANY)
      //
      case org.omg.CORBA.TCKind._tk_any:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_ANY);
        try
        {
          chosen = chosen.get_dyn_any();
        }
        catch (Exception e)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, e);
          return null;
        }
        return processResult(data_value, chosen);
      }
      case org.omg.CORBA.TCKind._tk_sequence:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_SEQUENCE);
        org.omg.DynamicAny.DynSequence dynSequence = 
                           org.omg.DynamicAny.DynSequenceHelper.narrow(chosen);

        if (dynSequence.get_length() == 0)
        {
          return new TypeValuePair(false);
        }

        try 
        {
          do
          {
            TypeValuePair result = processResult(data_value, 
                                               dynSequence.current_component());
            if ((result.type == TIDExprValueType.BOOLTYPE) &&
                ((Boolean)result.val.objVal).booleanValue())
            {
              return result;
            }
          }
          while (dynSequence.next());
          return new TypeValuePair(false);
        }
        catch (Exception e)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, e);
        }
        return new TypeValuePair();
      }
      case org.omg.CORBA.TCKind._tk_array:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_ARRAY);
        org.omg.DynamicAny.DynArray dynArray =
                              org.omg.DynamicAny.DynArrayHelper.narrow(chosen);

        org.omg.DynamicAny.DynAny elements[]=dynArray.get_elements_as_dyn_any();
        for (int j = 0; j < elements.length; j++)
        {
          TypeValuePair result = processResult(data_value, elements[j]);
          if ((result.type == TIDExprValueType.BOOLTYPE) &&
              ((Boolean)result.val.objVal).booleanValue())
          {
            return result;
          }
        }
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_struct:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_STRUCT);
        org.omg.DynamicAny.DynStruct dynStruct =
                             org.omg.DynamicAny.DynStructHelper.narrow(chosen);

        if (dynStruct.seek(0)) // Siempre vamos a mirar el primer miembro
        {
          try
          {
            return processResult(data_value, dynStruct.current_component());
          }
          catch (org.omg.DynamicAny.DynAnyPackage.TypeMismatch ex) 
          {
            TIDParser.printStackTrace(TIDParser.ERROR, ex);
          }
        }
        return new TypeValuePair();
      }
      case org.omg.CORBA.TCKind._tk_short:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_SHORT);
        short value;
        try
        {
          value = chosen.get_short();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.ITYPE)
          return new TypeValuePair(
                       (((Integer)data_value.val.objVal).intValue() == value));
        else if (data_value.type == TIDExprValueType.FTYPE)
          return new TypeValuePair(
                       (((Float)data_value.val.objVal).floatValue() == value));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_long:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_LONG);
        int value;
        try
        {
          value = chosen.get_long();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.ITYPE)
          return new TypeValuePair(
                       (((Integer)data_value.val.objVal).intValue() == value));
        else if (data_value.type == TIDExprValueType.FTYPE)
          return new TypeValuePair(
                       (((Float)data_value.val.objVal).floatValue() == value));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_ushort:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_USHORT);
        short value;
        try
        {
          value = chosen.get_ushort();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.ITYPE)
          return new TypeValuePair(
                       (((Integer)data_value.val.objVal).intValue() == value));
        else if (data_value.type == TIDExprValueType.FTYPE)
          return new TypeValuePair(
                       (((Float)data_value.val.objVal).floatValue() == value));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_ulong:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_ULONG);
        int value;
        try
        {
          value = chosen.get_ulong();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.ITYPE)
          return new TypeValuePair(
                       (((Integer)data_value.val.objVal).intValue() == value));
        else if (data_value.type == TIDExprValueType.FTYPE)
          return new TypeValuePair(
                       (((Float)data_value.val.objVal).floatValue() == value));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_float:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_FLOAT);
        float value;
        try
        {
          value = chosen.get_float();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.ITYPE)
          return new TypeValuePair(
                       (((Integer)data_value.val.objVal).intValue() == value));
        else if (data_value.type == TIDExprValueType.FTYPE)
          return new TypeValuePair(
                       (((Float)data_value.val.objVal).floatValue() == value));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_double:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_DOUBLE);
        double value;
        try
        {
          value = chosen.get_double();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.ITYPE)
          return new TypeValuePair(
                       (((Integer)data_value.val.objVal).intValue() == value));
        else if (data_value.type == TIDExprValueType.FTYPE)
          return new TypeValuePair(
                       (((Float)data_value.val.objVal).floatValue() == value));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_boolean:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_BOOLEAN);
        boolean value;
        try
        {
          value = chosen.get_boolean();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.BOOLTYPE)
          return new TypeValuePair(
                   (((Boolean)data_value.val.objVal).booleanValue() == value));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_char:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_CHAR);
        char c[] = new char[1];
        try
        {
          c[0] = chosen.get_char();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.STRTYPE)
          return new TypeValuePair(
                        ((String)data_value.val.objVal).equals(new String(c)));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_octet:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_OCTET);
        byte c;
        try
        {
          c = chosen.get_octet();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.STRTYPE)
          return new TypeValuePair(
                     ((String)data_value.val.objVal).equals(Byte.toString(c)));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_string:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_STRING);
        String str;
        try
        {
          str = chosen.get_string();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }
        if (data_value.type == TIDExprValueType.STRTYPE)
          return new TypeValuePair(((String)data_value.val.objVal).equals(str));
        return new TypeValuePair(false);
      }
      case org.omg.CORBA.TCKind._tk_enum:
      {
        //TIDParser.print(TIDParser.DEEP_DEBUG, ANYVALUE_TK_ENUM);
        org.omg.DynamicAny.DynEnum dynenum;
        try
        {
          dynenum = org.omg.DynamicAny.DynEnumHelper.narrow(chosen);
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }

        int num_value;
        try
        {
          num_value = dynenum.get_as_ulong();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }

        String str;
        try
        {
          str = dynenum.get_as_string();
        }
        catch (java.lang.Exception ex)
        {
          TIDParser.printStackTrace(TIDParser.ERROR, ex);
          return new TypeValuePair();
        }

        if (data_value.type == TIDExprValueType.ITYPE)
        {
          return new TypeValuePair(
                     num_value == ((Integer)data_value.val.objVal).intValue());
        }
        else if (data_value.type == TIDExprValueType.FTYPE)
        {
          return new TypeValuePair(
                     num_value == ((Float)data_value.val.objVal).floatValue());
        }
        else if (data_value.type == TIDExprValueType.STRTYPE)
        {
          return new TypeValuePair(str.equals((String)data_value.val.objVal));
        }
        return new TypeValuePair(false);
      }
      default:
        IN_ANYVALUE_UNKNOWN[1]=Integer.toString(tck_value);
        TIDParser.print(TIDParser.DEBUG, IN_ANYVALUE_UNKNOWN);
    }
    return new TypeValuePair();
  }
}
