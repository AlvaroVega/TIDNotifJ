//
// DisconnectedHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

final public class DisconnectedHolder
   implements org.omg.CORBA.portable.Streamable {

  public Disconnected value; 
  public DisconnectedHolder() {
  }

  public DisconnectedHolder(Disconnected initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosEventComm.DisconnectedHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosEventComm.DisconnectedHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosEventComm.DisconnectedHelper.type();
  };

}
