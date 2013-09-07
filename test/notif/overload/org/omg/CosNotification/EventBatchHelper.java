//
// EventBatchHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class EventBatchHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  public static void insert(org.omg.CORBA.Any any, org.omg.CosNotification.StructuredEvent[] value) {
    any.insert_Streamable(new EventBatchHolder(value));
  };

  public static org.omg.CosNotification.StructuredEvent[] extract(org.omg.CORBA.Any any) {
    if(any instanceof es.tid.CORBA.Any) {
      try {
        org.omg.CORBA.portable.Streamable holder =
          ((es.tid.CORBA.Any)any).extract_Streamable();
        if(holder instanceof EventBatchHolder){
          return ((EventBatchHolder) holder).value;
        }
      } catch (Exception e) {}
    }

    return read(any.create_input_stream());
  };

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      org.omg.CORBA.TypeCode original_type = _orb().create_sequence_tc(0 , org.omg.CosNotification.StructuredEventHelper.type());
      _type = _orb().create_alias_tc(id(), "EventBatch", original_type);
    }
    return _type;
  };

  public static String id() {
    return "IDL:org.omg/CosNotification/EventBatch:1.0";
  };

  public static org.omg.CosNotification.StructuredEvent[] read(org.omg.CORBA.portable.InputStream is) {
    org.omg.CosNotification.StructuredEvent[] result;
      int length0 = is.read_ulong();
      result = new org.omg.CosNotification.StructuredEvent[length0];
      for (int i0=0; i0<length0; i0++) {
        result[i0] = org.omg.CosNotification.StructuredEventHelper.read(is);
      }
    return result;
  };

  public static void write(org.omg.CORBA.portable.OutputStream os, org.omg.CosNotification.StructuredEvent[] val) {
      os.write_ulong(val.length);
      for (int i0=0; i0<val.length; i0++) {
        org.omg.CosNotification.StructuredEventHelper.write(os,val[i0]);
      }
  };

}