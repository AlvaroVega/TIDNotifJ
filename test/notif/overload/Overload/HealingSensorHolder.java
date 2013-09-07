//
// HealingSensorHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

final public class HealingSensorHolder
   implements org.omg.CORBA.portable.Streamable {

  public HealingSensor value; 
  public HealingSensorHolder() {
  }

  public HealingSensorHolder(HealingSensor initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = Overload.HealingSensorHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    Overload.HealingSensorHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return Overload.HealingSensorHelper.type();
  };

}
