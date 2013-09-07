//
// _EventTypeHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class _EventTypeHolder
   implements org.omg.CORBA.portable.Streamable {

  public _EventType value; 
  public _EventTypeHolder() {
  }

  public _EventTypeHolder(_EventType initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification._EventTypeHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification._EventTypeHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification._EventTypeHelper.type();
  };

}
