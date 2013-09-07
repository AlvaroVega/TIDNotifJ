//
// PushConsumerHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

final public class PushConsumerHolder
   implements org.omg.CORBA.portable.Streamable {

  public PushConsumer value; 
  public PushConsumerHolder() {
  }

  public PushConsumerHolder(PushConsumer initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosEventComm.PushConsumerHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosEventComm.PushConsumerHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosEventComm.PushConsumerHelper.type();
  };

}
