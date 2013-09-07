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

public interface TIDConstraintMsg
{
  public static final String ALL_NOM_IMPLEMENT =
                                  "TIDInOperator.evaluate(): NON IMPLEMENTED!";

  //
  // AnyValue.java
  //
  public static final String ANYVALUE_CHOSEN =
                                   "AnyValue.evaluate(): chosen non_existent.";
  public static final String ANYVALUE_TK_SHORT =
                                             "### AnyValue type _tk_short ###";
  public static final String ANYVALUE_TK_LONG =
                                              "### AnyValue type _tk_long ###";
  public static final String ANYVALUE_TK_USHORT =
                                            "### AnyValue type _tk_ushort ###";
  public static final String ANYVALUE_TK_ULONG =
                                             "### AnyValue type _tk_ulong ###";
  public static final String ANYVALUE_TK_FLOAT =
                                             "### AnyValue type _tk_float ###";
  public static final String ANYVALUE_TK_DOUBLE =
                                            "### AnyValue type _tk_double ###";
  public static final String ANYVALUE_TK_BOOLEAN =
                                           "### AnyValue type _tk_boolean ###";
  public static final String ANYVALUE_TK_CHAR =
                                              "### AnyValue type _tk_char ###";
  public static final String ANYVALUE_TK_OCTET =
                                             "### AnyValue type _tk_octet ###";
  public static final String ANYVALUE_TK_STRING =
                                            "### AnyValue type _tk_string ###";
  public static final String ANYVALUE_TK_ENUM =
                                              "### AnyValue type _tk_enum ###";
  public static final String ANYVALUE_UNKNOWN[] =
                               { "### AnyValue type Unknown! (",null,") ###" };
  public static final String IN_ANYVALUE_UNKNOWN[] =
                            { "### in AnyValue type Unknown! (",null,") ###" };
  //
  // SimpleNode.java
  //
  public static final String SN_EVALUATE  =
                    "SimpleNode: evaluate( org.omg.DynamicAny.DynAny dynany )";
  public static final String SN_EVALUATE_IN  =
    "SimpleNode: evaluate_in( TypeValuePair value, org.omg.DynamicAny.DynAny dynany )";
  public static final String SN_CHECK_EVENT  =
                            "SimpleNode: check_event(org.omg.CORBA.Any event)";

  //
  // TypeValuePair.java
  //
  public static final String TVP_MSG_EXCEPTION =
                                       "TypeValuePair: Exception UNKNOWN Type";
  public static final String TVP_MSG_TYPEVALUEPAIR = " TypeValuePair:  type:";
  public static final String TVP_MSG_TRUE = " val.boolvalue: TRUE";
  public static final String TVP_MSG_FALSE = " val.boolvalue: FALSE";
  public static final String TVP_MSG_DVAL = " val.dval: ";
  public static final String TVP_MSG_STRVAL = " val.strval: ";
  public static final String TVP_MSG_ENUMVAL = " val.enumval: ";
  public static final String TVP_MSG_ERRTYPE = " ERRTYPE ";

  //
  // TIDArithmeticExpr.java
  //
  public static final String TAE_NUM_HIJOS = 
                              "TIDArithmeticExpr: evaluate num operands ERROR";
  public static final String TAE_NO_HIJOS = 
                                 "TIDArithmeticExpr: evaluate no childs ERROR";
  //
  // TIDAssocAnyValue.java
  //
  public static final String TAAV_NAME_NOT_SEEK =
                                  "TIDAssocAnyValue.evaluate(): name not seek";
  public static final String TAAV_VALUE_NOT_SEEK =
                                 "TIDAssocAnyValue.evaluate(): value not seek";
  public static final String TAAV_UNKNOWN_VALUE =
                             "TIDAssocAnyValue.evaluate(): Unknown type value";
  public static final String TAAV_DEFAULT =
                             "TIDAssocAnyValue.evaluate(): Default.";
  public static final String TAAV_IN_DEFAULT =
                              "TIDAssocAnyValue.evaluate_value_in(): Default.";
  public static final String TAAV_VALUE_ERROR =
                           "TIDAssocAnyValue.evaluate_in(): value type ERROR.";
  public static final String TAAV_VALUE_ERROR2 =
                     "TIDAssocAnyValue.evaluate_value_in(): value type ERROR.";
  //
  // TIDBinaryExpr.java
  //
  public static final String TBE_NUM_OPERANDS =
                               "TIDBinaryExpr.evaluate(): num operands ERROR.";

  //
  // TIDInOperator.java
  //
  public static final String TIO_NUM_OPERANDS =
                               "TIDInOperator.evaluate(): num operands ERROR.";
  public static final String TIO_NO_OPERANDS = 
                                   "TIDInOperator: evaluate no operands ERROR";

  //
  // TIDParser.java
  //
  public static final String TP_INIT = "-> TIDParser.init(): NEW Interpreter.";

  public static final String TP_PARSE[] = { "-> TIDParser.parse(", null, ")" };

  //
  // TIDRelationalExpr.java
  //
  public static final String TRE_NUM_HIJOS = 
                              "TIDRelationalExpr: evaluate num operands ERROR";
  public static final String TRE_NO_HIJOS = 
                                 "TIDRelationalExpr: evaluate no childs ERROR";
  public static final String TRE_OPERAND_ERROR[] = 
                                 { "TIDRelationalExpr: Operand ERROR.", null };
  //
  // TIDStructAnyValue.java
  //
  public static final String TSTAV_DEFAULT[] =
                      { "TIDStructAnyValue.evaluate(): Unknown type: ", null };
  public static final String TSTAV_MEMBER_NOTFOUND[] =
                     { "TIDStructAnyValue.value(): Member not found: ", null };
  public static final String TSTAV_MEMBER[] =
                          { "TIDStructAnyValue.value(): Members: ", null };
  public static final String TSTAV_ANYVALUE =
                             "TIDStructAnyValue.evaluate(): anyvalue is null.";

  //
  // TIDSequenceAnyValue.java
  //
  public static final String TSEAV_INDEX_1 =
                               "TIDSequenceAnyValue.value(): Index value: -1.";
  public static final String TSEAV_INVALID_INDEX[] =
                      { "TIDSequenceAnyValue.value(): Invalid Index: ", null };
  public static final String TSEAV_DEFAULT[] =
                        { "TIDSequenceAnyValue.value(): Unknown type.", null };

  //
  // TIDUnaryExpr.java
  //
  public static final String TUE_DEFAULT =
                            "TIDUnaryExpr.evaluate(): NOT de algo no boolean.";

  //
  // TIDUnionAnyValue.java
  //
  public static final String TUAV_UNKNOWN =
                                  "TIDUnionAnyValue.value(): UNKNOWN TK_TYPE.";
  public static final String TUAV_ANYVALUE =
                                 "TIDUnionAnyValue.value(): anyvalue is null.";
  public static final String TUAV_INVALID_INDEX[] =
                { "TIDUnionAnyValue.value(): Index != discriminator: ", null };
  public static final String TUAV_INVALID_RESULT =
                              "TIDUnionAnyValue.value(): result is not valid.";
  //
  // TIDFunctionExpr.java
  //
  public static final String DATE_FORMAT_ERROR =
               "TIDFunctionExpr.evaluate(datecmp): date format operand ERROR.";
  public static final String PARSE_DATE_A_ERROR =
              "TIDFunctionExpr.evaluate(datecmp): first operand format ERROR.";
  public static final String PARSE_DATE_B_ERROR =
             "TIDFunctionExpr.evaluate(datecmp): second operand format ERROR.";
  public static final String TFE_NUM_OPERANDS =
                             "TIDFunctionExpr.evaluate(): num operands ERROR.";

  //
  // TIDSequenceString.java
  //
  public static final String TSA_VALUE_ERROR =
                             "TIDStringArray.evaluate_in(): value type ERROR.";
}
