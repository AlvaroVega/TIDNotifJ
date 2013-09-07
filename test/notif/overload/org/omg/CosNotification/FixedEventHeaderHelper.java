//
// FixedEventHeaderHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class FixedEventHeaderHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, FixedEventHeader value) {
    any.insert_Streamable(new FixedEventHeaderHolder(value));
  };

  public static FixedEventHeader extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof FixedEventHeaderHolder){
          return ((FixedEventHeaderHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];
      members[0] = new org.omg.CORBA.StructMember("event_type", org.omg.CosNotification._EventTypeHelper.type(), null);
      members[1] = new org.omg.CORBA.StructMember("event_name", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_string), null);
      _type = _orb().create_struct_tc(id(), "FixedEventHeader", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/FixedEventHeader:1.0";
  };

  public static FixedEventHeader read(org.omg.CORBA.portable.InputStream is) {
    FixedEventHeader result = new FixedEventHeader();
    result.event_type = org.omg.CosNotification._EventTypeHelper.read(is);
    result.event_name = is.read_string();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, FixedEventHeader val) {
    org.omg.CosNotification._EventTypeHelper.write(os,val.event_type);
    os.write_string(val.event_name);
  };

}
