//
// EventBatchHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class EventBatchHolder
   implements org.omg.CORBA.portable.Streamable {

  public org.omg.CosNotification.StructuredEvent[] value; 
  public EventBatchHolder() {
    value = new org.omg.CosNotification.StructuredEvent[0];
  }

  public EventBatchHolder(org.omg.CosNotification.StructuredEvent[] initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.EventBatchHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.EventBatchHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.EventBatchHelper.type();
  };

}
