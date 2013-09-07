//
// StructuredPushConsumerHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

abstract public class StructuredPushConsumerHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      _type = _orb().create_interface_tc(id(), "StructuredPushConsumer");
    }
    return _type;
  }

  public static String id() {
    return "IDL:org.omg/CosNotifyComm/StructuredPushConsumer:1.0";
  };

  public static void insert(org.omg.CORBA.Any any, StructuredPushConsumer value) {
    any.insert_Object((org.omg.CORBA.Object)value, type());
  };

  public static StructuredPushConsumer extract(org.omg.CORBA.Any any) {
    org.omg.CORBA.Object obj = any.extract_Object();
    StructuredPushConsumer value = narrow(obj);
    return value;
  };

  public static StructuredPushConsumer read(org.omg.CORBA.portable.InputStream is) {
    return narrow(is.read_Object(), true); 
  }

  public static void write(org.omg.CORBA.portable.OutputStream os, StructuredPushConsumer val) {
    if (!(os instanceof org.omg.CORBA_2_3.portable.OutputStream)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    if (val != null && !(val instanceof org.omg.CORBA.portable.ObjectImpl)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    os.write_Object((org.omg.CORBA.Object)val);
  }

  public static StructuredPushConsumer narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, false);
  }

  public static StructuredPushConsumer unchecked_narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, true);
  }

  private static StructuredPushConsumer narrow(org.omg.CORBA.Object obj, boolean is_a) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof StructuredPushConsumer) {
      return (StructuredPushConsumer)obj;
    }
    if (is_a || obj._is_a(id())) {
      _StructuredPushConsumerStub result = (_StructuredPushConsumerStub)new _StructuredPushConsumerStub();
      ((org.omg.CORBA.portable.ObjectImpl) result)._set_delegate
        (((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate());
      return (StructuredPushConsumer)result;
    }
    throw new org.omg.CORBA.BAD_PARAM();
  }

}