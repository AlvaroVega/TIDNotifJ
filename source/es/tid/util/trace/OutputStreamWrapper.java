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

package es.tid.util.trace;

/**
 * A <code>OutputStreamWrapper</code> wraps a OutputStream like Sistem.out,
 * and does not close the wrapped stream when close() is invoked.
 * This is useful when trace streams does not have to close it.
 *
 * @version    1.0
 * @author     Juan A. Caceres
 * @since      TIDorbJ 1.0.11
 */

public class OutputStreamWrapper extends java.io.OutputStream {

  private java.io.OutputStream m_stream;
  
  public OutputStreamWrapper (java.io.OutputStream stream) {
    m_stream = stream;
  }
 
  public void flush() throws java.io.IOException
  {
    if(m_stream != null)
      m_stream.flush();
  }
  public void close() throws java.io.IOException
  {
    // do_nothing
    m_stream = null;
  }
  public void write(int b) throws java.io.IOException
  {
    if(m_stream != null)
      m_stream.write(b);
  }

  public void write(byte buf[]) 
    throws java.io.IOException
   {
    if(m_stream != null)
      m_stream.write(buf);
  }
  
  public void write(byte buf[], int off, int len) 
    throws java.io.IOException
   {
    if(m_stream != null)
      m_stream.write(buf,off,len);
  }

}
