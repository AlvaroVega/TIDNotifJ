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

package es.tid.util.parser.Operations;

public interface TIDRelationalOperations
{
  // Limites
  public static final int FIRST_OP   = 0; // Limite Inferior
  public static final int EQ_OP      = 0; // ==
  public static final int DIFF_OP    = 1; // !=
  public static final int DIFF2_OP   = 2; // <>
  public static final int LESS_OP    = 3; // <
  public static final int LESSEQ_OP  = 4; // <=
  public static final int GREAT_OP   = 5; // >
  public static final int GREATEQ_OP = 6; // >=
  public static final int LIKE_OP    = 7; // >=
  public static final int LAST_OP    = 7; // Limite Superior
  public static final int UNKNOWN_OP = LAST_OP+1; // 

/*
  public static final String EQ_OP_STR      = "==";
  public static final String DIFF_OP_STR    = "!=";
  public static final String DIFF2_OP_STR   = "<>";
  public static final String LESS_OP_STR    = "<";
  public static final String LESSEQ_OP_STR  = "<=";
  public static final String GREAT_OP_STR   = ">";
  public static final String GREATEQ_OP_STR = ">=";
  public static final String LIKE_OP_STR    = "like";
*/

  public static final String[] STR_OP =
       { "==", "!=", "<>", "<", "<=", ">", ">=", "like", "UNKNOWN OPERATION" };
}
