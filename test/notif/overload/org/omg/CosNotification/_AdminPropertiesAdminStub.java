//
// _AdminPropertiesAdminStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class _AdminPropertiesAdminStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements AdminPropertiesAdmin {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotification/AdminPropertiesAdmin:1.0"  };

  public org.omg.CosNotification.Property[] get_admin() {
    org.omg.CORBA.Request _request = this._request("get_admin");
    _request.set_return_type(org.omg.CosNotification.AdminPropertiesHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
    org.omg.CosNotification.Property[] _result;
    _result = org.omg.CosNotification.AdminPropertiesHelper.extract(_request.return_value());
    return _result;
  }

  public void set_admin(org.omg.CosNotification.Property[] admin)
    throws org.omg.CosNotification.UnsupportedAdmin {
    org.omg.CORBA.Request _request = this._request("set_admin");
    org.omg.CORBA.Any $admin = _request.add_named_in_arg("admin");
    org.omg.CosNotification.AdminPropertiesHelper.insert($admin,admin);
    _request.exceptions().add(org.omg.CosNotification.UnsupportedAdminHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
        if (_userException.except.type().equal(org.omg.CosNotification.UnsupportedAdminHelper.type())) {
          throw org.omg.CosNotification.UnsupportedAdminHelper.extract(_userException.except);
        }
        throw new org.omg.CORBA.UNKNOWN();
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
  }


}
