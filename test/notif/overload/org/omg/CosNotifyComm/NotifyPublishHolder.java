//
// NotifyPublishHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

final public class NotifyPublishHolder
   implements org.omg.CORBA.portable.Streamable {

  public NotifyPublish value; 
  public NotifyPublishHolder() {
  }

  public NotifyPublishHolder(NotifyPublish initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotifyComm.NotifyPublishHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotifyComm.NotifyPublishHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotifyComm.NotifyPublishHelper.type();
  };

}
