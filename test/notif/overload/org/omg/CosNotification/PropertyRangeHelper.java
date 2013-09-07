//
// PropertyRangeHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class PropertyRangeHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, PropertyRange value) {
    any.insert_Streamable(new PropertyRangeHolder(value));
  };

  public static PropertyRange extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof PropertyRangeHolder){
          return ((PropertyRangeHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.StructMember[] members = new org.omg.CORBA.StructMember[2];
      members[0] = new org.omg.CORBA.StructMember("low_val", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any), null);
      members[1] = new org.omg.CORBA.StructMember("high_val", org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any), null);
      _type = _orb().create_struct_tc(id(), "PropertyRange", members);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/PropertyRange:1.0";
  };

  public static PropertyRange read(org.omg.CORBA.portable.InputStream is) {
    PropertyRange result = new PropertyRange();
    result.low_val = is.read_any();
    result.high_val = is.read_any();
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, PropertyRange val) {
    os.write_any(val.low_val);
    os.write_any(val.high_val);
  };

}
