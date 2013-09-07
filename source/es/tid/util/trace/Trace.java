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

import java.util.Date;
import java.io.*;

import es.tid.TIDorbj.core.AnyImpl;
import org.omg.CORBA.Any;

public class Trace {
  
   public final static int NONE = 0;
   public final static int ERROR = 1;
   public final static int USER = 2;
   public final static int DEBUG = 3;
   public final static int DEEP_DEBUG = 4;

   private final static String[] levels = {"NONE ", "ERROR ","USER ", "DEBUG ", "DEEP_DEBUG "};
  
  /**
   * Trace Log Stream.
   */
   private PrintWriter _log;
  
  /**
   * Aplication Name printed in log messages.
   */
   protected String _applicationName;
  
  /**
   * Current trace level.
   */
   protected int _level = NONE; 
  
  /**
   * Include the date in log messages.
   */
   protected boolean _include_date = true;
  
  /**
   * Invokes the stream flush each x times. It improves the file output performance. 
   */
   protected int _do_flush_at = 10;
  

  /**
   * Printed messages counter
   */
   protected int _message_count = 0;
  
  /**
   * Global static Trace instance.
   */
   public static Trace global = null;
  

 /*********************************************************/
   private boolean _activated_trace=true;
   private String _circular_trace_file_name="";
   private CircularTraceFile _circular_trace_file=null; 
 /*********************************************************/


  /**
   * Private Constructor.   
   */
  private Trace(PrintWriter log, String applicationName, int level) 
  {
    _log = log;
    _level = level;
    _applicationName = applicationName;
    _activated_trace = true;
  }
	
   /**
   * Creates the trace using the standard error output. 
   * @param applicationName may be null
   * @param level trace level addopted
   * @return a new Trace instance
   */
  public static Trace create_trace(String applicationName, int level)
    throws IOException
  {
    PrintWriter writer = new PrintWriter(
                         new BufferedWriter(
                         new OutputStreamWriter(
                         new OutputStreamWrapper(System.err))));

    return new Trace(writer, applicationName, level);
    
  }
  
  /**
   * Creates the trace using the given output stream. 
   * @param log the output stream where the messages will be written.
   * @param applicationName may be null
   * @param level trace level addopted
   * @return a new Trace instance
   */
  public static Trace create_trace(OutputStream log, String applicationName, int level)
    throws IOException
  {
    PrintWriter writer = new PrintWriter(
                         new BufferedWriter(
                         new OutputStreamWriter(log)));

    return new Trace(writer, applicationName, level);
    
  }
  
  /**
   * Creates the trace using the given writer stream.
   * @param log the output stream where the messages will be written.
   * @param applicationName may be null
   * @param level trace level addopted
   * @return a new Trace instance
   */
  public static Trace create_trace(Writer log, String applicationName, int level)
    throws IOException
  {
    PrintWriter writer = 
      new PrintWriter(new BufferedWriter(log));
    
    return new Trace(writer, applicationName, level);
  }
  
  /**
   * Creates the trace that will write messages in the given file.
   * @param fileName messages file name.
   * @param applicationName application name. It may be null.
   * @param level trace level addopted
   * @return a new Trace instance
   */
  public static Trace create_trace(String fileName, String applicationName, int level)
    throws IOException
  {
    PrintWriter writer = 
      new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
    
    return new Trace(writer, applicationName, level);
    
  }
  
  /**
   * Creates the trace using the given circular trace file. 
   * @param log the output stream where the messages will be written.
   * @param applicationName may be null
   * @param level trace level addopted
   * @return a new Trace instance
   */
  public static Trace create_trace(CircularTraceFile log, String applicationName, int level)
    throws IOException
  {
    PrintWriter writer = new PrintWriter(
                         new BufferedWriter(log));

    return new Trace(writer, applicationName, level);
    
  }
  
  /**
   * Sets the trace level. If level is 0 traces will be deactivated.
   * @param level new trace level
   */
  public void setLevel(int level)
  {
    _level = level;
  }
  
  /**
   * Activates/deactivates the date inclussion in trace messages.
   * @param value if <code>true</code> include it, otherwise do not.
   */
  public void setIncludeDate(boolean value)
  {
    _include_date = value;
  }
  
  /**
   * Sets each times the stream flush operation is invoked. It improves the file output performance.
   * @param value buffer flush round.
   */
  public void setDoFlushAt(int value)
  {
    if(value > 0)
      _do_flush_at = value;
  }

  /**
   * @return the buffer flush round value.
   */
  public int getDoFlushAt()
  {
    return _do_flush_at;
  }

  /**
   * @return the log stream used by the trace instance. 
   */
  public PrintWriter getLog()
  {
    return _log;
  }
  
  /**
   * Forces the buffered stream flushing.
   */
  public void flush()
  {
    if(_log != null) {    
      _log.flush();
    }
  }

  /**
   * Closes the trace log stream.
   */
  public synchronized void close()
  {
    if(_log != null) {
      _log.println("LOG CLOSED");
      _log.close();
      _log = null;
    }
  }
  
  /**
   * Prints the message if the trace level is less or equal than current.
   * @param level message trace level
   * @param message the trace message to be written.
   */
  public void print(int level, String message)
  {
      if ((_log == null)||(!_activated_trace))
        return;

      if (level <= _level) 
      {
        synchronized(_log) 
	{
          try
          {
            _log.print('[');
            _log.print(_message_count++);
            _log.print("] ");
            _log.print(levels[level]);
            if(_include_date){
              _log.print((new Date()).toString());
              _log.print(" ");
            }
            if ((_applicationName != null)&&(_applicationName.length() > 0)) {
              _log.print(_applicationName);
              _log.print(" ");
            }
            _log.println(message);
            if((_message_count % _do_flush_at) == 0)
              _log.flush();    
	  } catch(Throwable th) {}
	}
      }
  }
  
  /**
   * Compounds and Prints the message chunks if the trace level is less or equal than current.
   * @param level message trace level
   * @param message the trace message chunks to be written.
   */
  public void print(int level, String[] message)
  { 
    if ((_log == null)||(!_activated_trace))
      return;
      
    if (level <= _level) 
    {
      synchronized(_log) 
      {
        try 
        {  
          _log.print('[');
          _log.print(_message_count++);
          _log.print("] ");
          _log.print(levels[level]);
          if(_include_date){
            _log.print((new Date()).toString());
            _log.print(" ");
          }
          
          if ((_applicationName != null)&&(_applicationName.length() > 0)) {
            _log.print(_applicationName);
            _log.print(" ");
          }

          for(int i = 0; i < message.length; i++) 
            _log.print(message[i]); 
            
          _log.println();
        
          if((_message_count % _do_flush_at) == 0)
            _log.flush();

        } catch(Throwable th) {}	
      }       
    }
  }
  
 /** 
   * Prints the Exception "e" and its backtrace to the _log print 
   * writer if the trace level is less or equal than current. 
   * @param level message trace level 
   * @param e the Exception object with the backtrace to be written. 
   */ 
  public void printStackTrace( int level, Throwable e ) 
  {
    if ((_log == null)||(!_activated_trace))
      return;

    if (level <= _level)
    {
      synchronized(_log) 
      { 
        try 
        { 
          _log.print('['); 
          _log.print(_message_count++); 
          _log.print("] "); 
          _log.print(levels[level]);
          if(_include_date) 
          { 
            _log.print((new Date()).toString()); 
            _log.print(" "); 
          } 

          if ((_applicationName != null)&&(_applicationName.length() > 0)) {
            _log.print(_applicationName); 
            _log.print(" "); 
          } 
  
          e.printStackTrace(_log); 

          _log.println(); 

          if ( (_message_count % _do_flush_at) == 0 ) 
            _log.flush(); 
        } catch(Throwable th) {} 
      } 
    }
  }

  public void printStackTrace( int level, String msg, Throwable e ) 
  { 
    if ((_log == null)||(!_activated_trace))
      return; 

    if (level <= _level) 
    { 
      synchronized(_log) 
      { 
        try 
        { 
          _log.print('['); 
          _log.print(_message_count++); 
          _log.print("] "); 
          _log.print(levels[level]);
          if(_include_date) 
          { 
            _log.print((new Date()).toString()); 
            _log.print(" "); 
          } 

          if ((_applicationName != null)&&(_applicationName.length() > 0)) {
            _log.print(_applicationName); 
            _log.print(" "); 
          } 
  
          if (msg != null) {
            _log.print(msg);
            _log.println();
          }
          
          e.printStackTrace(_log); 

          _log.println(); 

          if ( (_message_count % _do_flush_at) == 0 ) 
            _log.flush(); 
        } catch(Throwable th) {} 
      } 
    } 
  } 

  /**
   * Compounds and Prints the message chunks if the trace level is less or equal than current.
   * @param level message trace level
   * @param message the trace message chunks to be written.
   */
  public void printStackTrace(int level, String[] message, Throwable ex)
  {
    if ((_log == null)||(!_activated_trace))
      return; 
      
    if (level <= _level) 
    {
      synchronized(_log) 
      {
        try 
        {
          _log.print('[');
          _log.print(_message_count++);
          _log.print("] ");

          _log.print(levels[level]);
          
          if(_include_date){
            _log.print((new Date()).toString());
            _log.print(" ");
          }
          
          if ((_applicationName != null)&&(_applicationName.length() > 0)) {
            _log.print(_applicationName);
            _log.print(" ");
          }
  
          for(int i = 0; i < message.length; i++) 
            _log.print(message[i]); 
            
          _log.println();
          
          ex.printStackTrace(_log);          
          
          _log.println();

          if((_message_count % _do_flush_at) == 0)
            _log.flush();

        } catch(Throwable th) {}
      }       
    }
  }

  public static void printStackTrace_st( int level, Throwable e )
  {
    if(global != null)
      global.printStackTrace(level, e);
  }

  public static void printStackTrace_st( int level, String msg, Throwable e ) 
  { 
    if(global != null) 
      global.printStackTrace(level, msg, e); 
  } 

  public static void printStackTrace_st( int level, String[] msg, Throwable e ) 
  { 
    if(global != null) 
      global.printStackTrace(level, msg, e); 
  } 
  
  /**
   * Prints the messages in the global trace instance if the trace level 
   * is less or equal than current.
   * @param level message trace level
   * @param message the trace message to be written.
   */

  public static void print_st(int level, String message)
  {
    if(global != null)
      global.print(level, message);
  }
  /**
   * Compounds and Prints the message chunks in the global trace instance
   * if the trace level is less or equal than current.
   * @param level message trace level
   * @param message the trace message chunks to be written.
   */
  
  public static void print_st(int level, String[] message)
  {
    if(global != null)
      global.print(level, message);
  }

  /**
   * Compounds and Prints the any in the global trace instance
   * if the trace level is less or equal than current.
   * @param level message trace level
   * @param Any the Any data to be written.
   */
  public static void print_any_st(int level, Any theany)
  {
    if(global == null)
      return;

    if (level <= global._level) 
    {
      synchronized(global._log) 
      {
        try 
        {
          global._log.print((new Date()).toString());
          if ((global._applicationName != null) &&
              (global._applicationName.length() > 0)) {
             global._log.print(" ");
             global._log.print(global._applicationName);
          }

          global._log.print(" ");
          AnyImpl.dump(theany, global._log);
          global._log.print('\n');
          global._log.flush();

        } catch(Throwable th) {}
      }
    }
  }

/*************************************************************************/
/******************* METODOS PARA GESTION REMOTA  ************************/
/*************************************************************************/

  public void set_trace_file (String name) throws CannotProceedException
  {
    if (!_circular_trace_file_name.equals(""))
    {    
      throw new CannotProceedException(
                        "The Circular Trace File nas already been stablished");
    }
    else
    {    
      _circular_trace_file_name = name;
      try
      {
        //init circular trace files with defect values (0,0)
        _circular_trace_file = new CircularTraceFile(0,0,name); 
      }
      catch (java.io.IOException ioex)
      {
        throw new CannotProceedException ("Cannot crate circular trace file");
      }
      PrintWriter writer = 
                     new PrintWriter(new BufferedWriter(_circular_trace_file));
      _log=writer;
    }
  } //set_trace_file
  
  public String get_trace_file() throws NoFileEspecifiedException
  {
     if (_circular_trace_file_name.equals(""))
       throw new NoFileEspecifiedException();
     else
       return _circular_trace_file_name;
  }

  public void activate_trace() throws AlreadyActivatedException
  {
    if (_activated_trace)
      throw new AlreadyActivatedException();
    else
      _activated_trace = true;
  } //activate_trace

  public void deactivate_trace() throws AlreadyDeactivatedException
  {
    if (!_activated_trace)
      throw new AlreadyDeactivatedException();
    else
      _activated_trace = false;
    return;
  }//deactivate_trace

  public void set_trace_level (int tr_l) 
     throws NotActivatedException, InvalidTraceLevelException
  {
    if ((tr_l<NONE)||(tr_l>DEEP_DEBUG))
      throw new InvalidTraceLevelException();

    if (!_activated_trace)
      throw new NotActivatedException();
    
    _level = tr_l;
    return;
  } //set_trace_level

/*************************************************************************/
/**************** FIN METODOS GESTION REMOTA *****************************/
/*************************************************************************/

} //Trace
