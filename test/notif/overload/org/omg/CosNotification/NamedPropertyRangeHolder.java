//
// NamedPropertyRangeHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class NamedPropertyRangeHolder
   implements org.omg.CORBA.portable.Streamable {

  public NamedPropertyRange value; 
  public NamedPropertyRangeHolder() {
  }

  public NamedPropertyRangeHolder(NamedPropertyRange initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.NamedPropertyRangeHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.NamedPropertyRangeHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.NamedPropertyRangeHelper.type();
  };

}
