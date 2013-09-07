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
* (C) Copyright 2005 TelefÃ³nica InvestigaciÃ³n y Desarrollo
*     S.A.Unipersonal (TelefÃ³nica I+D)
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

package es.tid.corba.TIDNotif;

public interface ThePOAFactoryMsg
{
  public final static String[] CREATE_GLOBAL_POA =
   { "-> ThePOAFactory.globalPOA(): ", null };
  public final static String[] CREATE_LOCAL_POA =
   { "-> ThePOAFactory.localPOA(): ", null };
  public final static String[] CREATE_EXCLUSIVE_POA =
   { "-> ThePOAFactory.exclusivePOA(): ", null };
  public final static String[] CONFIG_GLOBAL_POA =
   { "  config: ", null, ", ", null, ", ", null };
  public final static String[] CONFIG_LOCAL_POA =
   { "  config: ", null, ", ", null, ", ", null };
  public final static String[] CONFIG_EXCLUSIVE_POA =
   { "  config: ", null, ", ", null, ", ", null };
  public final static String ACTIVATE_GLOBAL_POA = 
   "  ¡activate!";
  public final static String ACTIVATE_LOCAL_POA = 
   "  ¡activate!";
  public final static String ACTIVATE_EXCLUSIVE_POA = 
   "  ¡activate!";
  public final static String DESTROY[] = 
   { "-> POAFactory.destroy: ", null };

  public final static String[] ERROR_POA =
   { " ERROR POAFactory: Invalid poaType = ", null };
  public final static String EXCEPTION_POA =
   " POAFactory: create child POA Exception.";
  public final static String POA_EXIST =
   " POAFactory: ERROR, child POA already exist.";

  // NUEVO
  public final static String NEW_POA[] = 
   { "* NEW POA: ", null };
  public final static String POA_INFO_1[] = 
   { "  [", null, "]" };
  public final static String POA_INFO_2[] = 
   { "  POA Manager = ", null };
  public final static String POA_INFO_3[] = 
   { "  Parent POA = ", null };
  public final static String POA_INFO_4[] =
   { "  Num. Threads = [", null, ",", null, "] -  Max. Queue Size = ", null };
}
