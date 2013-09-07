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

public class TIDParser implements TIDConstraintMsg
{
  final static public int ERROR      = 1;
  final static public int USER       = 2;
  final static public int DEBUG      = 3;
  final static public int DEEP_DEBUG = 4;

  final static private String Title = "ConstraintGrammar Parser Version 1.0";

  // Id de la gramatica utilizada
  final static public String _CONSTRAINT_GRAMMAR = "EXTENDED_TCL";

  // int max value = 2147483647
  final static public int _MAX_CONSTRAINT_ID = 999999;

  private static int _constraintId = 0;

  private static String default_constraint = "TRUE";

  private static java.io.StringReader default_in =
                                  new java.io.StringReader(default_constraint);

  private static es.tid.util.parser.ConstraintParser _interpreter;

  private static es.tid.util.trace.Trace _tracelog;

  private TIDParser()
  {
  }

  public static void print( int level, String message )
  {
    if (_tracelog != null)
    {
      _tracelog.print(level, message);
    }
  }

  public static void print( int level, String[] message )
  {
    if (_tracelog != null)
    {
      _tracelog.print(level, message);
    }
  }

  public static void printStackTrace( int level, Exception  e )
  {
    if (_tracelog != null)
    {
      _tracelog.printStackTrace(level, e);
    }
  }

  synchronized
  public static es.tid.util.parser.SimpleNode parse( java.lang.String new_constraint ) throws ParseException, java.lang.Exception
  {
    TP_PARSE[1] = new_constraint;
    print(DEBUG, TP_PARSE);

    if (_interpreter == null)
    {
      //return null;
      throw new ParseException("Parser not initialized.");
    }

    java.io.StringReader in = new java.io.StringReader(new_constraint);
    ConstraintParser.ReInit(in);

    es.tid.util.parser.SimpleNode filter;
    try
    {
      filter = ConstraintParser.Constraint();
    }
    catch (Exception ex)
    {
      filter = null;
      printStackTrace(ERROR, ex);
      throw ex;
    }
    return filter;
  }

  synchronized
  public static int newConstraintId()
  {
    return (++_constraintId % _MAX_CONSTRAINT_ID);
  }

  synchronized
  public static TIDParser init(es.tid.util.trace.Trace log)
  {
    if (_interpreter == null)
    {
      _tracelog = log;
      print(USER, TP_INIT);
      _interpreter=new es.tid.util.parser.ConstraintParser(default_in);
    }
    return new TIDParser();
  }
}
