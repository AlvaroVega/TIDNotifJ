//
// OptionalHeaderFieldsHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class OptionalHeaderFieldsHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, org.omg.CosNotification.Property[] value) {
    any.insert_Streamable(new org.omg.CosNotification.PropertySeqHolder(value));
  };

  public static org.omg.CosNotification.Property[] extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof org.omg.CosNotification.PropertySeqHolder){
          return ((org.omg.CosNotification.PropertySeqHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.TypeCode original_type = org.omg.CosNotification.PropertySeqHelper.type();
      _type = _orb().create_alias_tc(id(), "OptionalHeaderFields", original_type);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/OptionalHeaderFields:1.0";
  };

  public static org.omg.CosNotification.Property[] read(org.omg.CORBA.portable.InputStream is) {
    org.omg.CosNotification.Property[] result;
    result = org.omg.CosNotification.PropertySeqHelper.read(is);
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, org.omg.CosNotification.Property[] val) {
    org.omg.CosNotification.PropertySeqHelper.write(os,val);
  };

}
