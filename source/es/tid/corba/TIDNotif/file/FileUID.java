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

package es.tid.corba.TIDNotif.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileUID
{  
  private int uid = 0;
 
  RandomAccessFile accessor;
  
  public FileUID (String path) throws IOException 
  {
      boolean exists = true;
      File file = new File(path);      
      
      if(!file.exists()) {
          file.createNewFile();
          exists = false;          
      }
   
      accessor = new RandomAccessFile(file, "rws");
      
      if(!exists) {
          uid = 0;
          writeUID();
      } else {      
          accessor.seek(0);
          uid = accessor.readInt();
      }
  }
  
  public synchronized void destroy() throws IOException
  {
      accessor.close();
      accessor = null;
  }
  
  protected void writeUID() throws IOException
  {
      accessor.setLength(0);
      accessor.seek(0);
      accessor.writeInt(uid);
      
  }
  
  
  
  public synchronized String getUID() throws IOException
  {
      uid++;
      writeUID();
      return Integer.toString(uid);     
    
  }

 
}
