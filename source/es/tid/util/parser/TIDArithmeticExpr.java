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

/* Generated By:JJTree: Do not edit this line. TIDArithmeticExpr.java */
///////////////////////////////////////////////////////////////////////////
//
// File          :
// Description   :
//
// Author/s      : Alvaro Rodriguez
// Project       :
// Rel           :
// Created       :
// Revision Date :
// Rev. History  :
//
// Copyright 2000 Telefonica, I+D. All Rights Reserved.
//
// The copyright of the software program(s) is property of Telefonica.
// The program(s) may be used, modified and/or copied only with the
// express written consent of Telefonica or in acordance with the terms
// and conditions stipulated in the agreement/contract under which
// the program(s) have been supplied.
//
///////////////////////////////////////////////////////////////////////////

package es.tid.util.parser;

import es.tid.util.parser.Operations.TIDArithmeticOperations;

public class TIDArithmeticExpr extends SimpleNode implements TIDArithmeticOperations, TIDConstraintMsg
{
  private static final String NODENAME = "ArithmeticExpr: ";

  private String name;
  private int operator;

  public TIDArithmeticExpr(int id) {
    super(id);
    super.node_type = NodeType.TIDArithmeticExpr;
  }

  public TIDArithmeticExpr(ConstraintParser p, int id) {
    super(p, id);
    super.node_type = NodeType.TIDArithmeticExpr;
  }

  public void setOp(int n) 
  {
    try
    {
      operator = n;
      name = STR_OP[n-FIRST_OP];
    }
    catch (Exception e)
    {
      operator = UNKNOWN_OP;
      name = STR_OP[UNKNOWN_OP-FIRST_OP];
    }
  }

  public int getOp()
  {
    return this.operator;
  }

  public String toString() 
  {
    return NODENAME + name;
  }

  public TypeValuePair evaluate( org.omg.DynamicAny.DynAny dynany )
  {
    TypeValuePair result = new TypeValuePair(); // Ahora vale ERRTYPE

    if (children != null) 
    {
      int j = 0;
      TypeValuePair operand[] = new TypeValuePair[2];

      for (int i = 0; i < children.length; ++i) 
      {
        SimpleNode n = (SimpleNode)children[i];

        if (n != null) 
        {
          operand[j++] = n.evaluate( dynany );
        }
      }

      if (j == 2) // Todo OK
      {
        result = performOperation(operand[0], operand[1]);
        operand[0] = null;
        operand[1] = null;
      }
      else // Num. hijos incorrecto
      {
        TIDParser.print(TIDParser.DEBUG, TAE_NUM_HIJOS);
      }
    }
    else
    {
      TIDParser.print(TIDParser.DEBUG,TAE_NO_HIJOS);
    }
    return result;
  }
  
  private TypeValuePair performOperation( TypeValuePair operand1,
                                          TypeValuePair operand2)
  {
    if ( ( (operand1.type == TypeValuePair.ITYPE) ||
           (operand1.type == TypeValuePair.FTYPE) ) &&
         ( (operand2.type == TypeValuePair.ITYPE) ||
           (operand2.type == TypeValuePair.FTYPE) ) )
    {
      switch (operator)
      {
        case ADD_OP:
          if (operand1.type == TypeValuePair.ITYPE)
          {
            if (operand2.type == TypeValuePair.ITYPE)
            {
              return new TypeValuePair(
                               ((Integer)operand1.val.objVal).intValue() +
                               ((Integer)operand2.val.objVal).intValue() );
            }
            return new TypeValuePair(
                               ((Integer)operand1.val.objVal).floatValue() +
                               ((Float)operand2.val.objVal).floatValue() );
          }
          else
          {
            if (operand2.type == TypeValuePair.ITYPE)
            {
              return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() +
                               ((Integer)operand2.val.objVal).floatValue() );
            }
            return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() +
                               ((Float)operand2.val.objVal).floatValue() );
          }
        case MINUS_OP:
          if (operand1.type == TypeValuePair.ITYPE)
          {
            if (operand2.type == TypeValuePair.ITYPE)
            {
              return new TypeValuePair(
                               ((Integer)operand1.val.objVal).intValue() -
                               ((Integer)operand2.val.objVal).intValue() );
            }
            return new TypeValuePair(
                               ((Integer)operand1.val.objVal).floatValue() -
                               ((Float)operand2.val.objVal).floatValue() );
          }
          else
          {
            if (operand2.type == TypeValuePair.ITYPE)
            {
              return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() -
                               ((Integer)operand2.val.objVal).floatValue() );
            }
            return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() -
                               ((Float)operand2.val.objVal).floatValue() );
          }
        case MUL_OP:
          if (operand1.type == TypeValuePair.ITYPE)
          {
            if (operand2.type == TypeValuePair.ITYPE)
            {
              return new TypeValuePair(
                               ((Integer)operand1.val.objVal).intValue() *
                               ((Integer)operand2.val.objVal).intValue() );
            }
            return new TypeValuePair(
                               ((Integer)operand1.val.objVal).floatValue() *
                               ((Float)operand2.val.objVal).floatValue() );
          }
          else
          {
            if (operand2.type == TypeValuePair.ITYPE)
            {
              return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() *
                               ((Integer)operand2.val.objVal).floatValue() );
            }
            return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() *
                               ((Float)operand2.val.objVal).floatValue() );
          }
        case DIV_OP:
          if (operand2.type == TypeValuePair.ITYPE)
          {
            if ( ((Integer)operand2.val.objVal).intValue() == 0 )
            {
              return new TypeValuePair(0);
            }
            else
            {
              if (operand1.type == TypeValuePair.ITYPE)
              {
                return new TypeValuePair(
                               ((Integer)operand1.val.objVal).intValue() /
                               ((Integer)operand2.val.objVal).intValue() );
              }
              return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() /
                               ((Integer)operand2.val.objVal).floatValue() );
            }
          }
          else
          {
            if ( ((Integer)operand2.val.objVal).intValue() == 0 )
            {
              return new TypeValuePair(0);
            }
            else
            {
              if (operand1.type == TypeValuePair.ITYPE)
              {
                return new TypeValuePair(
                               ((Integer)operand1.val.objVal).floatValue() /
                               ((Float)operand2.val.objVal).floatValue() );
              }
              return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() /
                               ((Float)operand2.val.objVal).floatValue() );
            }
          }
        case MOD_OP:
          if (operand2.type == TypeValuePair.ITYPE)
          {
            if ( ((Integer)operand2.val.objVal).intValue() == 0 )
            {
              return new TypeValuePair(0);
            }
            else
            {
              if (operand1.type == TypeValuePair.ITYPE)
              {
                return new TypeValuePair(
                               ((Integer)operand1.val.objVal).intValue() %
                               ((Integer)operand2.val.objVal).intValue() );
              }
              return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() %
                               ((Integer)operand2.val.objVal).floatValue() );
            }
          }
          else
          {
            if ( ((Integer)operand2.val.objVal).intValue() == 0 )
            {
              return new TypeValuePair(0);
            }
            else
            {
              if (operand1.type == TypeValuePair.ITYPE)
              {
                return new TypeValuePair(
                               ((Integer)operand1.val.objVal).floatValue() %
                               ((Float)operand2.val.objVal).floatValue() );
              }
              return new TypeValuePair(
                               ((Float)operand1.val.objVal).floatValue() %
                               ((Float)operand2.val.objVal).floatValue() );
            }
          }
      }
    }
    //else
    //{
       /* if expressions types are not numeric, the filter
          will have been detected as invalid. So, this
          condition can not succeded when evaluating the
          filter 
        */
    //}
  
    return new TypeValuePair();
  }
}