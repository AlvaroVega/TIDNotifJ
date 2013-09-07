//
// PropertyValueHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class PropertyValueHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, org.omg.CORBA.Any value) {
    any.insert_Streamable(new org.omg.CORBA.AnyHolder(value));
  };

  public static org.omg.CORBA.Any extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof org.omg.CORBA.AnyHolder){
          return ((org.omg.CORBA.AnyHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.TypeCode original_type = org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any);
      _type = _orb().create_alias_tc(id(), "PropertyValue", original_type);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/PropertyValue:1.0";
  };

  public static org.omg.CORBA.Any read(org.omg.CORBA.portable.InputStream is) {
    org.omg.CORBA.Any result;
    result = is.read_any();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, org.omg.CORBA.Any val) {
    os.write_any(val);
  };

}
