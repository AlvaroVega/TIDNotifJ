//
// _QoSAdminStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class _QoSAdminStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements QoSAdmin {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotification/QoSAdmin:1.0"  };

  public org.omg.CosNotification.Property[] get_qos() {
    org.omg.CORBA.Request _request = this._request("get_qos");
    _request.set_return_type(org.omg.CosNotification.QoSPropertiesHelper.type());
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
    _result = org.omg.CosNotification.QoSPropertiesHelper.extract(_request.return_value());
    return _result;
  }

  public void set_qos(org.omg.CosNotification.Property[] qos)
    throws org.omg.CosNotification.UnsupportedQoS {
    org.omg.CORBA.Request _request = this._request("set_qos");
    org.omg.CORBA.Any $qos = _request.add_named_in_arg("qos");
    org.omg.CosNotification.QoSPropertiesHelper.insert($qos,qos);
    _request.exceptions().add(org.omg.CosNotification.UnsupportedQoSHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
        if (_userException.except.type().equal(org.omg.CosNotification.UnsupportedQoSHelper.type())) {
          throw org.omg.CosNotification.UnsupportedQoSHelper.extract(_userException.except);
        }
        throw new org.omg.CORBA.UNKNOWN();
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
  }

  public void validate_qos(org.omg.CosNotification.Property[] required_qos, org.omg.CosNotification.NamedPropertyRangeSeqHolder available_qos)
    throws org.omg.CosNotification.UnsupportedQoS {
    org.omg.CORBA.Request _request = this._request("validate_qos");
    org.omg.CORBA.Any $required_qos = _request.add_named_in_arg("required_qos");
    org.omg.CosNotification.QoSPropertiesHelper.insert($required_qos,required_qos);
    org.omg.CORBA.Any $available_qos = _request.add_named_out_arg("available_qos");
    $available_qos.type(org.omg.CosNotification.NamedPropertyRangeSeqHelper.type());
    _request.exceptions().add(org.omg.CosNotification.UnsupportedQoSHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
        if (_userException.except.type().equal(org.omg.CosNotification.UnsupportedQoSHelper.type())) {
          throw org.omg.CosNotification.UnsupportedQoSHelper.extract(_userException.except);
        }
        throw new org.omg.CORBA.UNKNOWN();
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
    available_qos.value = org.omg.CosNotification.NamedPropertyRangeSeqHelper.extract($available_qos);
  }


}
