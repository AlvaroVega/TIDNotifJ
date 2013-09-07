//
// UnsupportedQoSHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class UnsupportedQoSHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, UnsupportedQoS value) {
    any.insert_Streamable(new UnsupportedQoSHolder(value));
  };

  public static UnsupportedQoS extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof UnsupportedQoSHolder){
          return ((UnsupportedQoSHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[1];
      members[0] = new org.omg.CORBA.StructMember("qos_err", org.omg.CosNotification.PropertyErrorSeqHelper.type(), null);
      _type = _orb().create_exception_tc(id(), "UnsupportedQoS", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/UnsupportedQoS:1.0";
  };

  public static UnsupportedQoS read(org.omg.CORBA.portable.InputStream is) {
    if (! is.read_string().equals(id())) {
      throw new org.omg.CORBA.MARSHAL("Invalid repository id.");
    };
    UnsupportedQoS result = new UnsupportedQoS();
    result.qos_err = org.omg.CosNotification.PropertyErrorSeqHelper.read(is);
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, UnsupportedQoS val) {
    os.write_string(id());
    org.omg.CosNotification.PropertyErrorSeqHelper.write(os,val.qos_err);
  };

}
