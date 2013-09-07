//
// HealingPushConsumerHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

final public class HealingPushConsumerHolder
   implements org.omg.CORBA.portable.Streamable {

  public HealingPushConsumer value; 
  public HealingPushConsumerHolder() {
  }

  public HealingPushConsumerHolder(HealingPushConsumer initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = Overload.HealingPushConsumerHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    Overload.HealingPushConsumerHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return Overload.HealingPushConsumerHelper.type();
  };

}
