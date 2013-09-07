//
// AdminPropertiesAdminPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class AdminPropertiesAdminPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements AdminPropertiesAdminOperations {

  public AdminPropertiesAdmin _this() {
    return AdminPropertiesAdminHelper.narrow(super._this_object());
  };

  public AdminPropertiesAdmin _this(org.omg.CORBA.ORB orb) {
    return AdminPropertiesAdminHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotification/AdminPropertiesAdmin:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("get_admin", new Integer(0));
    _methods.put("set_admin", new Integer(1));
  }

  public void invoke(org.omg.CORBA.ServerRequest _request) {
    java.lang.Object _method = _methods.get(_request.operation());
    if (_method == null) {
      throw new org.omg.CORBA.BAD_OPERATION(_request.operation());
    }
    int _method_id = ((java.lang.Integer)_method).intValue();
    switch(_method_id) {
    case 0: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      org.omg.CosNotification.Property[] _result = this.get_admin();
      org.omg.CORBA.Any _resultAny = _orb().create_any();
      org.omg.CosNotification.AdminPropertiesHelper.insert(_resultAny, _result);
      _request.set_result(_resultAny);
      return;
    }
    case 1: {
      try {
      org.omg.CORBA.NVList _params = _orb().create_list(1);
      org.omg.CORBA.Any $admin = _orb().create_any();
      $admin.type(org.omg.CosNotification.AdminPropertiesHelper.type());
      _params.add_value("admin", $admin, org.omg.CORBA.ARG_IN.value);
      _request.arguments(_params);
      org.omg.CosNotification.Property[] admin;
      admin = org.omg.CosNotification.AdminPropertiesHelper.extract($admin);
      this.set_admin(admin);
      } catch(org.omg.CosNotification.UnsupportedAdmin _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosNotification.UnsupportedAdminHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    }
  }
}
