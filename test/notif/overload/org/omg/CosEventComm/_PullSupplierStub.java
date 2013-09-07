//
// _PullSupplierStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

public class _PullSupplierStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements PullSupplier {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PullSupplier:1.0"  };

  public org.omg.CORBA.Any pull()
    throws org.omg.CosEventComm.Disconnected {
    org.omg.CORBA.Request _request = this._request("pull");
    _request.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any));
    _request.exceptions().add(org.omg.CosEventComm.DisconnectedHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
        if (_userException.except.type().equal(org.omg.CosEventComm.DisconnectedHelper.type())) {
          throw org.omg.CosEventComm.DisconnectedHelper.extract(_userException.except);
        }
        throw new org.omg.CORBA.UNKNOWN();
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
    org.omg.CORBA.Any _result;
    _result = _request.return_value().extract_any();
    return _result;
  }

  public org.omg.CORBA.Any try_pull(org.omg.CORBA.BooleanHolder has_event)
    throws org.omg.CosEventComm.Disconnected {
    org.omg.CORBA.Request _request = this._request("try_pull");
    _request.set_return_type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any));
    org.omg.CORBA.Any $has_event = _request.add_named_out_arg("has_event");
    $has_event.type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
    _request.exceptions().add(org.omg.CosEventComm.DisconnectedHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
        if (_userException.except.type().equal(org.omg.CosEventComm.DisconnectedHelper.type())) {
          throw org.omg.CosEventComm.DisconnectedHelper.extract(_userException.except);
        }
        throw new org.omg.CORBA.UNKNOWN();
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
    org.omg.CORBA.Any _result;
    _result = _request.return_value().extract_any();
    has_event.value = $has_event.extract_boolean();
    return _result;
  }

  public void disconnect_pull_supplier() {
    org.omg.CORBA.Request _request = this._request("disconnect_pull_supplier");
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
  }


}
