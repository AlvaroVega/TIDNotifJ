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

public class TIDSequenceAnyValue extends SimpleNode implements TIDConstraintMsg
{
  private static final String NODENAME_A = "SequenceAny Member: _length";
  private static final String NODENAME_B = "SequenceAny Member: ";

  static private org.omg.DynamicAny.DynAnyFactory dynAnyFactory = null;

  private int sequence_index = -1;
  private AnyValue anyvalue;
  private int _length = -1;

  public TIDSequenceAnyValue(int id) 
  {
    super(id);
    super.node_type = NodeType.TIDSequenceAnyValue;
    anyvalue = new AnyValue();
  }

  public TIDSequenceAnyValue(ConstraintParser p, int id) 
  {
    super(p, id);
    super.node_type = NodeType.TIDSequenceAnyValue;
    anyvalue = new AnyValue();
  }

  public void setMember( int index )
  {
    sequence_index = index;
  }

  public int getIndex()
  {
    return this.sequence_index;
  }

  public String toString() 
  {
    if (sequence_index == -1)
      return NODENAME_A;
    else
      return NODENAME_B + sequence_index;
  }

  public org.omg.DynamicAny.DynAny value(org.omg.DynamicAny.DynAny dynAny)
  {
    try
    {
      dynAny.rewind();
    }
    catch (Exception e)
    {
      TIDParser.printStackTrace(TIDParser.ERROR, e);
      return null;
    }
    
    org.omg.CORBA.TypeCode type;
    try
    {
      type = dynAny.type();
    }
    catch (Exception e)
    {
      TIDParser.printStackTrace(TIDParser.ERROR, e);
      return null;
    }

    while(type.kind().value() == org.omg.CORBA.TCKind._tk_alias)
    {
      try 
      {
        type = type.content_type();
      }
      catch (Exception e)
      {
        TIDParser.printStackTrace(TIDParser.ERROR, e);
        return null;
      }
    }

    try 
    {
      switch(type.kind().value())
      {
        case org.omg.CORBA.TCKind._tk_sequence:
        {
          org.omg.DynamicAny.DynSequence dynSequence = 
                           org.omg.DynamicAny.DynSequenceHelper.narrow(dynAny);

          _length = dynSequence.get_length();

          if (sequence_index == -1)
          {
            //TIDParser.print(TIDParser.DEBUG, TSEAV_INDEX_1);
            return null;
          }

          if ( !(dynSequence.seek(sequence_index)) )
          {
            TSEAV_INVALID_INDEX[1] = Integer.toString(sequence_index);
            TIDParser.print(TIDParser.DEBUG, TSEAV_INVALID_INDEX);
            return null;
          }
          org.omg.DynamicAny.DynAny dynElem = dynSequence.current_component();
          return dynElem;
        }
        case org.omg.CORBA.TCKind._tk_array:
        {
          org.omg.DynamicAny.DynArray dynArray;
          dynArray = org.omg.DynamicAny.DynArrayHelper.narrow(dynAny);

          _length = dynArray.get_elements().length;

          if (sequence_index == -1)
          {
            //TIDNotifTrace.print(TIDNotifTrace.DEBUG, TSEAV_INDEX_1);
            return null;
          }

          if ( !(dynArray.seek(sequence_index)) )
          {
            TSEAV_INVALID_INDEX[1] = Integer.toString(sequence_index);
            TIDParser.print(TIDParser.DEBUG, TSEAV_INVALID_INDEX);
            return null;
          }
          org.omg.DynamicAny.DynAny dynElem = dynArray.current_component();
          return dynElem;
        }
        //
        // NUEVO (por si la estructura estuviera dentro de un ANY)
        //
        case org.omg.CORBA.TCKind._tk_any:
        {
          try
          {
            if ( locateDynAnyFactory() < 0 )
            {
              return null;
            }
            return value(dynAnyFactory.create_dyn_any(dynAny.get_any()));
            //return value(dynAny.get_dyn_any());
          }
          catch (Exception e)
          {
            TIDParser.printStackTrace(TIDParser.ERROR, e);
          }
          return null;
        }
        default:
          TSEAV_DEFAULT[1] = Integer.toString(type.kind().value());
          TIDParser.print(TIDParser.DEBUG, TSEAV_DEFAULT);
          return null;
      }
    }
    catch (Exception e)
    {
      TIDParser.printStackTrace(TIDParser.ERROR, e);
    }
    return null;
  }

  public TypeValuePair evaluate( org.omg.DynamicAny.DynAny dynany )
  {
    org.omg.DynamicAny.DynAny chosen = value(dynany);

    if (chosen == null)
    {
      if (sequence_index == -1)
      {
        return new TypeValuePair(_length);
      }
      else
      {
        return new TypeValuePair();
      }
    }

    if (children != null)
    {
      TypeValuePair result = null;

      for (int i = 0; i < children.length; ++i)
      {
        SimpleNode n = (SimpleNode)children[i];

        if (n != null)
        {
          return n.evaluate( chosen );
        }
      }
      return new TypeValuePair();
    }
    if (anyvalue != null)
    {
      return anyvalue.evaluate(chosen);
    }
    return new TypeValuePair();
  }

  synchronized
  private int locateDynAnyFactory()
  {
    if (dynAnyFactory == null)
    {
      java.util.Properties props = new java.util.Properties();
      props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
      try
      {
        System.getProperties().put(
         "org.omg.CORBA.ORBSingletonClass","es.tid.TIDorbj.core.SingletonORB");
      }
      catch (Exception e)
      {
        // Capturada para evitar ecepciones de seguridad en los applets
        // Se supone que el applet ya tiene esta propiedad configurada.
      }
      org.omg.CORBA.ORB orb;

      try
      {
        String[] args = new String[0];
        orb = org.omg.CORBA.ORB.init(args, props);
      }
      catch (Exception e)
      {
        TIDParser.printStackTrace(TIDParser.ERROR, e);
        return -1;
      }

      try
      {
        org.omg.CORBA.Object obj =
                               orb.resolve_initial_references("DynAnyFactory");
        dynAnyFactory = org.omg.DynamicAny.DynAnyFactoryHelper.narrow(obj);
      }
      catch (Exception e)
      {
        TIDParser.printStackTrace(TIDParser.ERROR, e);
        return -1;
      }
    }
    return 0;
  }
}
