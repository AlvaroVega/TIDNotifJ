//
// EventTypeSeqHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class EventTypeSeqHolder
   implements org.omg.CORBA.portable.Streamable {

  public org.omg.CosNotification._EventType[] value; 
  public EventTypeSeqHolder() {
    value = new org.omg.CosNotification._EventType[0];
  }

  public EventTypeSeqHolder(org.omg.CosNotification._EventType[] initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.EventTypeSeqHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.EventTypeSeqHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.EventTypeSeqHelper.type();
  };

}
