//
// EventHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

final public class EventHolder
   implements org.omg.CORBA.portable.Streamable {

  public Event value; 
  public EventHolder() {
  }

  public EventHolder(Event initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = Overload.EventHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    Overload.EventHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return Overload.EventHelper.type();
  };

}
