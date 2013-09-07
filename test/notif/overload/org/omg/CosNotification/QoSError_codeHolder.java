//
// QoSError_codeHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class QoSError_codeHolder
   implements org.omg.CORBA.portable.Streamable {

  public QoSError_code value; 
  public QoSError_codeHolder() {
  }

  public QoSError_codeHolder(QoSError_code initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.QoSError_codeHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.QoSError_codeHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.QoSError_codeHelper.type();
  };

}
