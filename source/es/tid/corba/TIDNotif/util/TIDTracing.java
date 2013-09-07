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

package es.tid.corba.TIDNotif.util;

public class TIDTracing
{
  final static private String OUTPUT_FILENAME = "_tidtracing.txt";
  final static private int NUM_MAX_LINES = 12500;
  final static private int NUM_MAX_FILES = 10;

  static private int file_num = 0;
//  static private String fileName = null;

  static private java.io.PrintStream ps = null;

  static private int num_lines = 0;

  static public void setOutputFile()
  {
    file_num = 0;
    setOutputFile(OUTPUT_FILENAME);
  }

  static public void setOutputFile(String filename)
  {
    java.io.BufferedOutputStream bos = null;
    try
    {
      bos = new java.io.BufferedOutputStream(
                      new java.io.FileOutputStream(file_num + filename), 128 );
    }
    catch (java.io.FileNotFoundException ex)
    {
      System.err.println("TIDTracing: FileNotFoundException");
    }

    if (ps != null)
    {
      ps.close();
    }
    
    ps = new java.io.PrintStream(bos, true);

    System.setOut(ps);

    System.out.println("TIDTracing.setFileOutput " + filename );
    System.out.println("");
  }

  synchronized
  static public void print(int s)
  {
    if (ps != null)
    {
      ps.print(s);
    }
  }

  synchronized
  static public void print(Object s)
  {
    if (ps != null)
    {
      ps.print(s);
    }
  }

  synchronized
  static public void println(Object s)
  {
    if (ps != null)
    {
      num_lines++; 
      ps.println(s);

      if (num_lines > NUM_MAX_LINES)
      {
        num_lines = 0;
        file_num++;
        file_num = (file_num % NUM_MAX_FILES);
        setOutputFile(OUTPUT_FILENAME);
      }
    }
  }
}
