///////////////////////////////////////////////////////////////////////////
//
// File          :
// Description   :
//
// Author/s      : Alvaro Rodriguez
// Project       :
// Rel           :
// Created       :
// Revision Date :
// Rev. History  :
//
// Copyright 2001 Telefonica, I+D. All Rights Reserved.
//
// The copyright of the software program(s) is property of Telefonica.
// The program(s) may be used, modified and/or copied only with the
// express written consent of Telefonica or in acordance with the terms
// and conditions stipulated in the agreement/contract under which
// the program(s) have been supplied.
//
///////////////////////////////////////////////////////////////////////////

package es.tid.corba.TIDNotif.tools;

public class Util
{
  private static final String[] PREFIXES = 
  {
    "org.omg.CORBA",
    "es.tid.TIDorbj",
    "-TIDNotif"
  };

  public static String readIORString(String filename)
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
      System.exit(1);
    }
    return ref;
  }

  public static void writeIORString( String reference, String filename )
  {
    if ( filename == null )
    {
      System.out.println(reference);
    }
    else
    {
      try
      {
        java.io.FileOutputStream file = new java.io.FileOutputStream(filename);
        java.io.PrintStream pfile = new java.io.PrintStream(file);
        pfile.println(reference);
      }
      catch ( java.io.IOException ex )
      {
        System.err.println("Error writing REFERENCE to file " + filename);
        System.exit(1);
      }
    }
  }

  public static String[] filterArgs(String[] old_args)
  {
    java.util.Vector tmp = new java.util.Vector();

    int i = 0;
    while ( i < old_args.length )
    {
      boolean found = false;
// Debug
//      System.out.println(old_args[i]);
      try
      {
        for (int j = 0; j < PREFIXES.length; j++)
        {
          if (old_args[i].startsWith(PREFIXES[j]))
          {
            i++; i++; found = true;
            break;
          }
        }
      }
      catch (java.lang.NullPointerException ex) {}

      if (! found)
      {
        tmp.add(old_args[i++]);
      }
    }

    i = 0;
    String[] new_args = new String[tmp.size()];
    for ( java.util.Enumeration e = tmp.elements(); e.hasMoreElements(); )
    {
      new_args[i++] = (String)e.nextElement();
    }
    tmp.removeAllElements();
    tmp = null;
    return new_args;
  }

}
