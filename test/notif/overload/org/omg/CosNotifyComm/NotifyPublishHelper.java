//
// NotifyPublishHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

abstract public class NotifyPublishHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      _type = _orb().create_interface_tc(id(), "NotifyPublish");
    }
    return _type;
  }

  public static String id() {
    return "IDL:org.omg/CosNotifyComm/NotifyPublish:1.0";
  };

  public static void insert(org.omg.CORBA.Any any, NotifyPublish value) {
    any.insert_Object((org.omg.CORBA.Object)value, type());
  };

  public static NotifyPublish extract(org.omg.CORBA.Any any) {
    org.omg.CORBA.Object obj = any.extract_Object();
    NotifyPublish value = narrow(obj);
    return value;
  };

  public static NotifyPublish read(org.omg.CORBA.portable.InputStream is) {
    return narrow(is.read_Object(), true); 
  }

  public static void write(org.omg.CORBA.portable.OutputStream os, NotifyPublish val) {
    if (!(os instanceof org.omg.CORBA_2_3.portable.OutputStream)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    if (val != null && !(val instanceof org.omg.CORBA.portable.ObjectImpl)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    os.write_Object((org.omg.CORBA.Object)val);
  }

  public static NotifyPublish narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, false);
  }

  public static NotifyPublish unchecked_narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, true);
  }

  private static NotifyPublish narrow(org.omg.CORBA.Object obj, boolean is_a) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof NotifyPublish) {
      return (NotifyPublish)obj;
    }
    if (is_a || obj._is_a(id())) {
      _NotifyPublishStub result = (_NotifyPublishStub)new _NotifyPublishStub();
      ((org.omg.CORBA.portable.ObjectImpl) result)._set_delegate
        (((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate());
      return (NotifyPublish)result;
    }
    throw new org.omg.CORBA.BAD_PARAM();
  }

}
