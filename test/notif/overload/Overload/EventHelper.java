//
// EventHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

abstract public class EventHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, Event value) {
    any.insert_Streamable(new EventHolder(value));
  };

  public static Event extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof EventHolder){
          return ((EventHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];
      members[0] = new org.omg.CORBA.StructMember("bool_val", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean), null);
      members[1] = new org.omg.CORBA.StructMember("long_val", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_long), null);
      _type = _orb().create_struct_tc(id(), "Event", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/Overload/Event:1.0";
  };

  public static Event read(org.omg.CORBA.portable.InputStream is) {
    Event result = new Event();
    result.bool_val = is.read_boolean();
    result.long_val = is.read_long();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, Event val) {
    os.write_boolean(val.bool_val);
    os.write_long(val.long_val);
  };

}
