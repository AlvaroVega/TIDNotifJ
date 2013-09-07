//
// StatsHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

final public class StatsHolder
   implements org.omg.CORBA.portable.Streamable {

  public Stats value; 
  public StatsHolder() {
  }

  public StatsHolder(Stats initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = Overload.StatsHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    Overload.StatsHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return Overload.StatsHelper.type();
  };

}
