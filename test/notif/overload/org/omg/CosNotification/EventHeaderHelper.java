//
// EventHeaderHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class EventHeaderHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, EventHeader value) {
    any.insert_Streamable(new EventHeaderHolder(value));
  };

  public static EventHeader extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof EventHeaderHolder){
          return ((EventHeaderHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];
      members[0] = new org.omg.CORBA.StructMember("fixed_header", org.omg.CosNotification.FixedEventHeaderHelper.type(), null);
      members[1] = new org.omg.CORBA.StructMember("variable_header", org.omg.CosNotification.OptionalHeaderFieldsHelper.type(), null);
      _type = _orb().create_struct_tc(id(), "EventHeader", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/EventHeader:1.0";
  };

  public static EventHeader read(org.omg.CORBA.portable.InputStream is) {
    EventHeader result = new EventHeader();
    result.fixed_header = org.omg.CosNotification.FixedEventHeaderHelper.read(is);
    result.variable_header = org.omg.CosNotification.OptionalHeaderFieldsHelper.read(is);
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, EventHeader val) {
    org.omg.CosNotification.FixedEventHeaderHelper.write(os,val.fixed_header);
    org.omg.CosNotification.OptionalHeaderFieldsHelper.write(os,val.variable_header);
  };

}
