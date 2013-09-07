//
// PropertyHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class PropertyHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, Property value) {
    any.insert_Streamable(new PropertyHolder(value));
  };

  public static Property extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof PropertyHolder){
          return ((PropertyHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];
      members[0] = new org.omg.CORBA.StructMember("name", org.omg.CosNotification.PropertyNameHelper.type(), null);
      members[1] = new org.omg.CORBA.StructMember("value", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any), null);
      _type = _orb().create_struct_tc(id(), "Property", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/Property:1.0";
  };

  public static Property read(org.omg.CORBA.portable.InputStream is) {
    Property result = new Property();
    result.name = org.omg.CosNotification.PropertyNameHelper.read(is);
    result.value = is.read_any();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, Property val) {
    org.omg.CosNotification.PropertyNameHelper.write(os,val.name);
    os.write_any(val.value);
  };

}
