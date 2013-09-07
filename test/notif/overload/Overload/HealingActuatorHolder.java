//
// HealingActuatorHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

final public class HealingActuatorHolder
   implements org.omg.CORBA.portable.Streamable {

  public HealingActuator value; 
  public HealingActuatorHolder() {
  }

  public HealingActuatorHolder(HealingActuator initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = Overload.HealingActuatorHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    Overload.HealingActuatorHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return Overload.HealingActuatorHelper.type();
  };

}
