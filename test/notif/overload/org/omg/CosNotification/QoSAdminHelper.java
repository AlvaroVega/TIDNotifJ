//
// QoSAdminHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class QoSAdminHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      _type = _orb().create_interface_tc(id(), "QoSAdmin");
    }
    return _type;
  }

  public static String id() {
    return "IDL:org.omg/CosNotification/QoSAdmin:1.0";
  };

  public static void insert(org.omg.CORBA.Any any, QoSAdmin value) {
    any.insert_Object((org.omg.CORBA.Object)value, type());
  };

  public static QoSAdmin extract(org.omg.CORBA.Any any) {
    org.omg.CORBA.Object obj = any.extract_Object();
    QoSAdmin value = narrow(obj);
    return value;
  };

  public static QoSAdmin read(org.omg.CORBA.portable.InputStream is) {
    return narrow(is.read_Object(), true); 
  }

  public static void write(org.omg.CORBA.portable.OutputStream os, QoSAdmin val) {
    if (!(os instanceof org.omg.CORBA_2_3.portable.OutputStream)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    if (val != null && !(val instanceof org.omg.CORBA.portable.ObjectImpl)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    os.write_Object((org.omg.CORBA.Object)val);
  }

  public static QoSAdmin narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, false);
  }

  public static QoSAdmin unchecked_narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, true);
  }

  private static QoSAdmin narrow(org.omg.CORBA.Object obj, boolean is_a) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof QoSAdmin) {
      return (QoSAdmin)obj;
    }
    if (is_a || obj._is_a(id())) {
      _QoSAdminStub result = (_QoSAdminStub)new _QoSAdminStub();
      ((org.omg.CORBA.portable.ObjectImpl) result)._set_delegate
        (((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate());
      return (QoSAdmin)result;
    }
    throw new org.omg.CORBA.BAD_PARAM();
  }

}
