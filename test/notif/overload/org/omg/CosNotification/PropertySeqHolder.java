//
// PropertySeqHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class PropertySeqHolder
   implements org.omg.CORBA.portable.Streamable {

  public org.omg.CosNotification.Property[] value; 
  public PropertySeqHolder() {
    value = new org.omg.CosNotification.Property[0];
  }

  public PropertySeqHolder(org.omg.CosNotification.Property[] initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.PropertySeqHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.PropertySeqHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.PropertySeqHelper.type();
  };

}
