//
// PropertyErrorSeqHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class PropertyErrorSeqHolder
   implements org.omg.CORBA.portable.Streamable {

  public org.omg.CosNotification.PropertyError[] value; 
  public PropertyErrorSeqHolder() {
    value = new org.omg.CosNotification.PropertyError[0];
  }

  public PropertyErrorSeqHolder(org.omg.CosNotification.PropertyError[] initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.PropertyErrorSeqHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.PropertyErrorSeqHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.PropertyErrorSeqHelper.type();
  };

}
