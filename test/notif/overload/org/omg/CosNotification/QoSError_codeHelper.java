//
// QoSError_codeHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class QoSError_codeHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, QoSError_code value) {
    any.insert_Streamable(new QoSError_codeHolder(value));
  };

  public static QoSError_code extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof QoSError_codeHolder){
          return ((QoSError_codeHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      java.lang.String[] members = new java.lang.String[7];
      members[0] = "UNSUPPORTED_PROPERTY";
      members[1] = "UNAVAILABLE_PROPERTY";
      members[2] = "UNSUPPORTED_VALUE";
      members[3] = "UNAVAILABLE_VALUE";
      members[4] = "BAD_PROPERTY";
      members[5] = "BAD_TYPE";
      members[6] = "BAD_VALUE";
      _type = _orb().create_enum_tc(id(), "QoSError_code", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/QoSError_code:1.0";
  };

  public static QoSError_code read(org.omg.CORBA.portable.InputStream is) {
    return QoSError_code.from_int(is.read_long());
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, QoSError_code val) {
    os.write_long(val.value());
  };

}
