//
// StatsHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

abstract public class StatsHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, Stats value) {
    any.insert_Streamable(new StatsHolder(value));
  };

  public static Stats extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof StatsHolder){
          return ((StatsHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[4];
      members[0] = new org.omg.CORBA.StructMember("requests_received", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_long), null);
      members[1] = new org.omg.CORBA.StructMember("requests_discarded", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_long), null);
      members[2] = new org.omg.CORBA.StructMember("requests_accepted", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_long), null);
      members[3] = new org.omg.CORBA.StructMember("error_rate", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_double), null);
      _type = _orb().create_struct_tc(id(), "Stats", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/Overload/Stats:1.0";
  };

  public static Stats read(org.omg.CORBA.portable.InputStream is) {
    Stats result = new Stats();
    result.requests_received = is.read_long();
    result.requests_discarded = is.read_long();
    result.requests_accepted = is.read_long();
    result.error_rate = is.read_double();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, Stats val) {
    os.write_long(val.requests_received);
    os.write_long(val.requests_discarded);
    os.write_long(val.requests_accepted);
    os.write_double(val.error_rate);
  };

}
