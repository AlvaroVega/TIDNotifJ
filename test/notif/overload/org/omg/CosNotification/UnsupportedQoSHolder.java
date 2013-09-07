//
// UnsupportedQoSHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class UnsupportedQoSHolder
   implements org.omg.CORBA.portable.Streamable {

  public UnsupportedQoS value; 
  public UnsupportedQoSHolder() {
  }

  public UnsupportedQoSHolder(UnsupportedQoS initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.UnsupportedQoSHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.UnsupportedQoSHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.UnsupportedQoSHelper.type();
  };

}
