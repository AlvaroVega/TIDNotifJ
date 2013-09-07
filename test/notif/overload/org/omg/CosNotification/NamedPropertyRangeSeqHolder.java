//
// NamedPropertyRangeSeqHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class NamedPropertyRangeSeqHolder
   implements org.omg.CORBA.portable.Streamable {

  public org.omg.CosNotification.NamedPropertyRange[] value; 
  public NamedPropertyRangeSeqHolder() {
    value = new org.omg.CosNotification.NamedPropertyRange[0];
  }

  public NamedPropertyRangeSeqHolder(org.omg.CosNotification.NamedPropertyRange[] initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.NamedPropertyRangeSeqHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.NamedPropertyRangeSeqHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.NamedPropertyRangeSeqHelper.type();
  };

}
