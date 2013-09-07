//////////////////////////////////////////////////////////////////////////
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
// Copyright 2000 Telefonica, I+D. All Rights Reserved.
//
// The copyright of the software program(s) is property of Telefonica.
// The program(s) may be used, modified and/or copied only with the
// express written consent of Telefonica or in acordance with the terms
// and conditions stipulated in the agreement/contract under which
// the program(s) have been supplied.
//
///////////////////////////////////////////////////////////////////////////

package es.tid.corba.TIDDistrib;

public class Base
{
  public static String readStringIOR(String filename)
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
}
