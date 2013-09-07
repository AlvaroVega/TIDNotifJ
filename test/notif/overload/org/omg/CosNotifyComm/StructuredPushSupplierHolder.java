//
// StructuredPushSupplierHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

final public class StructuredPushSupplierHolder
   implements org.omg.CORBA.portable.Streamable {

  public StructuredPushSupplier value; 
  public StructuredPushSupplierHolder() {
  }

  public StructuredPushSupplierHolder(StructuredPushSupplier initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotifyComm.StructuredPushSupplierHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotifyComm.StructuredPushSupplierHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotifyComm.StructuredPushSupplierHelper.type();
  };

}
