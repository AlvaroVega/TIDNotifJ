//
// PropertyNameHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class PropertyNameHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, java.lang.String value) {
    any.insert_Streamable(new org.omg.CORBA.StringHolder(value));
  };

  public static java.lang.String extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof org.omg.CORBA.StringHolder){
          return ((org.omg.CORBA.StringHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.TypeCode original_type = org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_string);
      _type = _orb().create_alias_tc(id(), "PropertyName", original_type);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/PropertyName:1.0";
  };

  public static java.lang.String read(org.omg.CORBA.portable.InputStream is) {
    java.lang.String result;
    result = is.read_string();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, java.lang.String val) {
    os.write_string(val);
  };

}