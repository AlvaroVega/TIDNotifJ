//
// InvalidEventTypeHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

abstract public class InvalidEventTypeHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, InvalidEventType value) {
    any.insert_Streamable(new InvalidEventTypeHolder(value));
  };

  public static InvalidEventType extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof InvalidEventTypeHolder){
          return ((InvalidEventTypeHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[1];
      members[0] = new org.omg.CORBA.StructMember("type", org.omg.CosNotification._EventTypeHelper.type(), null);
      _type = _orb().create_exception_tc(id(), "InvalidEventType", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotifyComm/InvalidEventType:1.0";
  };

  public static InvalidEventType read(org.omg.CORBA.portable.InputStream is) {
    if (! is.read_string().equals(id())) {
      throw new org.omg.CORBA.MARSHAL("Invalid repository id.");
    };
    InvalidEventType result = new InvalidEventType();
    result.type = org.omg.CosNotification._EventTypeHelper.read(is);
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, InvalidEventType val) {
    os.write_string(id());
    org.omg.CosNotification._EventTypeHelper.write(os,val.type);
  };

}
