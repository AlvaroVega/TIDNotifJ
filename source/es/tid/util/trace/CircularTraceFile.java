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

package es.tid.util.trace;

import java.io.*;
import java.util.Vector;

import java.io.*;
import java.util.Vector;

public class CircularTraceFile extends Writer
{

  private String _name; //nombre base usado para referenciar los ficheros

  private int _current = 0; //almacena el indice del array cuyo fichero esta 
  //siendo utilizado 

  private File _current_file=null; //es el fichero sobre el que se escribiendo 

  private long _size = 102400; //tamaño maximo, en bytes, que puede alcanzar cada uno
  //de los ficheros de la lista circular
  // 100 K por defecto

  private int _list_length = 5; //es el tamaño de la lista circular, el numero
  //de ficheros que tiene
  //5 por defecto

  private File [] _files_list = null; //es un array con los ficheros

  private FileWriter _fw=null; //es el FileWriter para escribor en los ficheros

  public CircularTraceFile (int length, long size, String name) throws java.io.IOException
  {
    if (length!=0) //si vale cero -> valor por defecto
      _list_length=length;
    if (size!=0)  //si vale cero -> valor por defecto
      _size=size;
    _name=name;
    _files_list = new File [_list_length];
    try{getInitFile();}
    catch(java.io.IOException ioex){throw ioex;}
	} //constructor

  /*implementación de los metodos abstractos del writer*/
  public void close()
  {
    for (int i=0;i<_list_length;i++)
      _files_list[i]=null;
  } //close

  public void flush() throws java.io.IOException
  {
    try
    {_fw.flush();
    }
    catch (java.io.IOException ioex)
    {throw ioex;
    }
  } //flush

  public void write (char[] cbuf, int off, int len)
  {
    try
    {
      _fw.write(cbuf,off,len);
      _fw.flush();
      if (_current_file.length()>=_size)
        nextFile();
    }
    catch (java.io.IOException ioex)
    {
    }
  } //write

  /* fin implementacion de los metodos abstractos del writer */


  // devuelve el siguiente fichero de la lista, es decir, sobre el 
  // que se deben seguir escribiendo las trazas  
  private void nextFile() throws java.io.IOException
  {
    try
    {
      _fw.close();
      _current=(_current+1)%_list_length;  

      if (_files_list[_current]==null) //primera vez que se usa un fichero de la lista
        _files_list[_current] = new File(_name+_current);
      else //el fichero se reutiliza --> debo vaciarlo (lo borro y lo creo de nuevo)
      {
        ((File)_files_list[_current]).delete();
        _files_list[_current] = new File(_name+_current);
        (_files_list[_current]).createNewFile();
      }     
      _current_file=(File)_files_list[_current];    
      _fw = new FileWriter(_current_file.getPath(),true);
    }
    catch (java.io.IOException ioex)
    {throw ioex;
    }
  }//nextFile


  //elige como fichero a utilizar inicialmente el más antiguo 
  private void getInitFile() throws java.io.IOException
  {
    int i=0;
    File aux;
    File selected=new File(_name+0);
    int use=0; //indice del vector de ficheros

    try
    {
      while (i<_list_length)
      {
        aux = new File(_name+i);
        if (aux.exists())
        {
          _files_list[i] = aux;        
          if (aux.lastModified()<selected.lastModified())
          { //selected es mas actual
            selected = aux;
            use=i;
          } 
        }
        i++; 
      }
      _current = use;    
      // En _files_list tenemos los nombres de los ficheros que existen
      // Si el que vamos a usar no esta en la lista, es porque no existe
      if (_files_list[use]==null) // Lo apuntamos
        _files_list[use] = selected;  
      else // El fichero a usar ya existe, lo limpiamos.  
      {
        ((File)_files_list[_current]).delete();
        _files_list[_current] = new File(_name+_current);
        (_files_list[_current]).createNewFile();
      }
      _current_file = (File)_files_list[use];
      _fw = new FileWriter(_current_file.getPath(),true);
    }
    catch (java.io.IOException ioex)
    {throw ioex;}
  }//getInitFile

} //class CircularTraceFile
