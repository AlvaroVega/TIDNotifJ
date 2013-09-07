//
// EventHeaderHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class EventHeaderHolder
   implements org.omg.CORBA.portable.Streamable {

  public EventHeader value; 
  public EventHeaderHolder() {
  }

  public EventHeaderHolder(EventHeader initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.EventHeaderHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.EventHeaderHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.EventHeaderHelper.type();
  };

}
