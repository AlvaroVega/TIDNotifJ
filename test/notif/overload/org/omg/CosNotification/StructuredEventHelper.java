//
// StructuredEventHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class StructuredEventHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, StructuredEvent value) {
    any.insert_Streamable(new StructuredEventHolder(value));
  };

  public static StructuredEvent extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof StructuredEventHolder){
          return ((StructuredEventHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[3];
      members[0] = new org.omg.CORBA.StructMember("header", org.omg.CosNotification.EventHeaderHelper.type(), null);
      members[1] = new org.omg.CORBA.StructMember("filterable_data", org.omg.CosNotification.FilterableEventBodyHelper.type(), null);
      members[2] = new org.omg.CORBA.StructMember("remainder_of_body", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any), null);
      _type = _orb().create_struct_tc(id(), "StructuredEvent", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/StructuredEvent:1.0";
  };

  public static StructuredEvent read(org.omg.CORBA.portable.InputStream is) {
    StructuredEvent result = new StructuredEvent();
    result.header = org.omg.CosNotification.EventHeaderHelper.read(is);
    result.filterable_data = org.omg.CosNotification.FilterableEventBodyHelper.read(is);
    result.remainder_of_body = is.read_any();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, StructuredEvent val) {
    org.omg.CosNotification.EventHeaderHelper.write(os,val.header);
    org.omg.CosNotification.FilterableEventBodyHelper.write(os,val.filterable_data);
    os.write_any(val.remainder_of_body);
  };

}
