//
// _EventTypeHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class _EventTypeHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, _EventType value) {
    any.insert_Streamable(new _EventTypeHolder(value));
  };

  public static _EventType extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof _EventTypeHolder){
          return ((_EventTypeHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];
      members[0] = new org.omg.CORBA.StructMember("domain_name", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), null);
      members[1] = new org.omg.CORBA.StructMember("type_name", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), null);
      _type = _orb().create_struct_tc(id(), "_EventType", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/_EventType:1.0";
  };

  public static _EventType read(org.omg.CORBA.portable.InputStream is) {
    _EventType result = new _EventType();
    result.domain_name = is.read_string();
    result.type_name = is.read_string();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, _EventType val) {
    os.write_string(val.domain_name);
    os.write_string(val.type_name);
  };

}
