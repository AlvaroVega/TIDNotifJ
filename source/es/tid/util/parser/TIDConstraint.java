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

/* Generated By:JJTree: Do not edit this line. TIDConstraint.java */

package es.tid.util.parser;

public class TIDConstraint extends SimpleNode implements TIDConstraintMsg
{
  static private org.omg.DynamicAny.DynAnyFactory dynAnyFactory = null;

  public TIDConstraint(int id) 
  {
    super(id);
    super.node_type = NodeType.TIDConstraint;
  }

  public TIDConstraint(ConstraintParser p, int id) 
  {
    super(p, id);
    super.node_type = NodeType.TIDConstraint;
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

  public TypeValuePair evaluate( org.omg.DynamicAny.DynAny dynany ) 
  {
    TypeValuePair result = new TypeValuePair();

    if (children != null) 
    {
      for (int i = 0; i < children.length; ++i) 
      {
        SimpleNode n = (SimpleNode)children[i];

        if (n != null) 
        {
          result = n.evaluate( dynany );
        }
      }
    }
    return result;
  }

  public TypeValuePair check_event( org.omg.CORBA.Any event ) 
  {
    if ( locateDynAnyFactory() < 0 )
    {
      return new TypeValuePair();
    }

    org.omg.DynamicAny.DynAny dynAny;

    try
    {
      dynAny = dynAnyFactory.create_dyn_any(event);
    }
    catch (Exception e)
    {
      TIDParser.printStackTrace(TIDParser.ERROR, e);
      return new TypeValuePair();
    }
    return evaluate(dynAny);
  }
}