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

public class NodeType
{
  public static final int NUM_NODETYPES       = 20;

  public static final int TIDArithmeticExpr   = 0;
  public static final int TIDBinaryExpr       = 1;
  public static final int TIDRelationalExpr   = 2;
  public static final int TIDUnaryExpr        = 3;
  public static final int TIDString           = 4;
  public static final int TIDBoolean          = 5;
  public static final int TIDKeyword          = 6;
  public static final int TIDRuntimeVariable  = 7;
  public static final int TIDInteger          = 8;
  public static final int TIDFloat            = 9;
  public static final int TIDStructAnyValue   = 10;
  public static final int TIDSequenceAnyValue = 11;
  public static final int TIDUnionAnyValue    = 12;
  public static final int TIDAssocAnyValue    = 13;
  public static final int TIDConstraint       = 14;
  public static final int TIDBasicAnyValue    = 15;
  public static final int TIDDefault          = 16;
  public static final int TIDInOperator       = 17;
  public static final int TIDIdentifier       = 18;
  public static final int TIDFunctionExpr     = 19;
  public static final int UNKNOWN_NODE        = 20;

  public static final String[] STR_NODE = {
    "TIDArithmeticExpr", "TIDBinaryExpr", "TIDRelationalExpr", "TIDUnaryExpr", 
    "TIDString", "TIDBoolean", "TIDKeyword", "TIDRuntimeVariable", 
    "TIDInteger", "TIDFloat", "TIDStructAnyValue", "TIDSequenceAnyValue", 
    "TIDUnionAnyValue", "TIDAssocAnyValue", "TIDConstraint", 
    "TIDBasicAnyValue", "TIDDefault", "TIDInOperator", "TIDIdentifier", 
    "TIDFunctionExpr", null };

/*
  public static final int OP_TYPE = 0;
  // TIDArithmeticExpr TIDBinaryExpr TIDRelationalExpr TIDUnaryExpr

  public static final int NAME_TYPE = 1;
  // TIDString TIDBoolean TIDKeyword TIDRuntimeVariable

  public static final int VALUE_TYPE = 2;
  // TIDInteger TIDFloat 
  
  public static final int MEMBER_TYPE = 3;
  // TIDStructAnyValue TIDSequenceAnyValue TIDUnionAnyValue

  public static final int MEMBER_VALUE_TYPE = 4;
  // TIDAssocAnyValue

  public static final int OTHER_TYPE = 5;
  // TIDConstraint TIDBasicAnyValue TIDDefault TIDInOperator
*/
}
