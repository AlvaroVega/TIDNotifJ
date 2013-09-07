//
// DisconnectedHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

abstract public class DisconnectedHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, Disconnected value) {
    any.insert_Streamable(new DisconnectedHolder(value));
  };

  public static Disconnected extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof DisconnectedHolder){
          return ((DisconnectedHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[0];
      _type = _orb().create_exception_tc(id(), "Disconnected", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:omg.org/CosEventComm/Disconnected:1.0";
  };

  public static Disconnected read(org.omg.CORBA.portable.InputStream is) {
    if (! is.read_string().equals(id())) {
      throw new org.omg.CORBA.MARSHAL("Invalid repository id.");
    };
    Disconnected result = new Disconnected();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, Disconnected val) {
    os.write_string(id());
  };

}
