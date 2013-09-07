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

package es.tid.corba.TIDDistrib;

import java.util.Properties;

import es.tid.corba.TIDDistribAdmin.Agent;
import es.tid.corba.TIDDistribAdmin.AgentHelper;

public class Shutdown
{
  private static final String NAME = "Shutdown";

  private static final String SERVICE_NAME = "TIDNotif: " + NAME;
  private static final String USAGE = "\nUsage:";
  private static final String USAGE1 = "\t" + NAME + " < IOR_String";
  private static final String OR = "or";
  private static final String USAGE2 = "\t" + NAME + " -f IOR_Filename\n";

  private static void showHelp()
  {
    System.out.println(SERVICE_NAME);
    System.out.println(USAGE);
    System.out.println(USAGE1);
    System.out.println(OR);
    System.out.println(USAGE2);
  }

  protected static String readStringIOR(String filename)
  {
    String ref = null;
    try
    {
      java.io.LineNumberReader r; 
      if (filename == null)
      {
        r = new 
           java.io.LineNumberReader (new java.io.InputStreamReader(System.in));
      }
      else
      {
        r = new java.io.LineNumberReader (new java.io.FileReader(filename));
      }
      ref = r.readLine();
      r.close();
    }
    catch (java.io.IOException e1)
    {
      if (filename == null)
      {
        System.err.println("Error reading IOR from Standard Input");
      }
      else
      {
        System.err.println("Error reading IOR from file " + filename);
      }
      ref = null;
    }
    return ref;
  }

  public static void main(String args[])
  {
    String iorFilename = null;

    // Lectura de parametros (si lus hubiera)
    for ( int i = 0; i < args.length; i++ )
    {
      if ( args[i].compareTo("-f") == 0 )
      {
        iorFilename = new String(args[++i]);
      }
      else if ( ( args[i].compareTo("-help") == 0 ) ||
                ( args[i].compareTo("-h") == 0 ) ||
                ( args[i].compareTo("/h") == 0 ) ||
                ( args[i].compareTo("-?") == 0 ) ||
                ( args[i].compareTo("/?") == 0 ) )
      {
        showHelp();
        System.exit(1);
      }
    }

    String iorString = readStringIOR(iorFilename);

    if (iorString == null) System.exit(0);

    Properties props = new Properties();
    props.put("org.omg.CORBA.ORBClass","es.tid.TIDorbj.core.TIDORB");
	
    System.getProperties().put(
                  "org.omg.CORBA.ORBSingletonClass","es.tid.TIDorbj.core.SingletonORB");
		
    props.put("es.tid.TIDorbj.max_blocked_time","60000");

    org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init( args, props );
	
    Agent admin = null;
    try 
    {
      org.omg.CORBA.Object ref = orb.string_to_object(iorString);
      admin = AgentHelper.narrow(ref);
      admin.shutdown();
    }
    catch (org.omg.CORBA.COMM_FAILURE ex)
    {
    }
//    catch (CannotProceed cp) 
//    {
//      System.err.println("shutdown_service error: " + cp.why);
//    }
    catch (Exception e) 
    {
      e.printStackTrace();	
    }
    if (orb instanceof es.tid.TIDorbj.core.TIDORB) 
      ((es.tid.TIDorbj.core.TIDORB)orb).destroy();
  }
}
