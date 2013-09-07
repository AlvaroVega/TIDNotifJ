//
// StructuredEventHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class StructuredEventHolder
   implements org.omg.CORBA.portable.Streamable {

  public StructuredEvent value; 
  public StructuredEventHolder() {
  }

  public StructuredEventHolder(StructuredEvent initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.StructuredEventHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.StructuredEventHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.StructuredEventHelper.type();
  };

}
