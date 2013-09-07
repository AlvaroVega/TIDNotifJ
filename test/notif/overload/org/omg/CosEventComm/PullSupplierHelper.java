//
// PullSupplierHelper.java (helper)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

abstract public class PullSupplierHelper {

  private static org.omg.CORBA.ORB _orb() {
    return org.omg.CORBA.ORB.init();
  }

  private static org.omg.CORBA.TypeCode _type = null;
  public static org.omg.CORBA.TypeCode type() {
    if (_type == null) {
      _type = _orb().create_interface_tc(id(), "PullSupplier");
    }
    return _type;
  }

  public static String id() {
    return "IDL:omg.org/CosEventComm/PullSupplier:1.0";
  };

  public static void insert(org.omg.CORBA.Any any, PullSupplier value) {
    any.insert_Object((org.omg.CORBA.Object)value, type());
  };

  public static PullSupplier extract(org.omg.CORBA.Any any) {
    org.omg.CORBA.Object obj = any.extract_Object();
    PullSupplier value = narrow(obj);
    return value;
  };

  public static PullSupplier read(org.omg.CORBA.portable.InputStream is) {
    return narrow(is.read_Object(), true); 
  }

  public static void write(org.omg.CORBA.portable.OutputStream os, PullSupplier val) {
    if (!(os instanceof org.omg.CORBA_2_3.portable.OutputStream)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    if (val != null && !(val instanceof org.omg.CORBA.portable.ObjectImpl)) {;
      throw new org.omg.CORBA.BAD_PARAM();
    };
    os.write_Object((org.omg.CORBA.Object)val);
  }

  public static PullSupplier narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, false);
  }

  public static PullSupplier unchecked_narrow(org.omg.CORBA.Object obj) {
    return narrow(obj, true);
  }

  private static PullSupplier narrow(org.omg.CORBA.Object obj, boolean is_a) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof PullSupplier) {
      return (PullSupplier)obj;
    }
    if (is_a || obj._is_a(id())) {
      _PullSupplierStub result = (_PullSupplierStub)new _PullSupplierStub();
      ((org.omg.CORBA.portable.ObjectImpl) result)._set_delegate
        (((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate());
      return (PullSupplier)result;
    }
    throw new org.omg.CORBA.BAD_PARAM();
  }

}
