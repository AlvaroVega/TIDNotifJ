//
// Event.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

public class Event
   implements org.omg.CORBA.portable.IDLEntity {

  public boolean bool_val;
  public int long_val;

  public Event() {
  }

  public Event(boolean bool_val, int long_val) {
    this.bool_val = bool_val;
    this.long_val = long_val;
  }

}
