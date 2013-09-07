//
// _StructuredPullSupplierStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public class _StructuredPullSupplierStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements StructuredPullSupplier {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/StructuredPullSupplier:1.0",
    "IDL:org.omg/CosNotifyComm/NotifySubscribe:1.0"  };

  public org.omg.CosNotification.StructuredEvent pull_structured_event()
    throws org.omg.CosEventComm.Disconnected {
    org.omg.CORBA.Request _request = this._request("pull_structured_event");
    _request.set_return_type(org.omg.CosNotification.StructuredEventHelper.type());
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
    org.omg.CosNotification.StructuredEvent _result;
    _result = org.omg.CosNotification.StructuredEventHelper.extract(_request.return_value());
    return _result;
  }

  public org.omg.CosNotification.StructuredEvent try_pull_structured_event(org.omg.CORBA.BooleanHolder has_event)
    throws org.omg.CosEventComm.Disconnected {
    org.omg.CORBA.Request _request = this._request("try_pull_structured_event");
    _request.set_return_type(org.omg.CosNotification.StructuredEventHelper.type());
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
    org.omg.CosNotification.StructuredEvent _result;
    _result = org.omg.CosNotification.StructuredEventHelper.extract(_request.return_value());
    has_event.value = $has_event.extract_boolean();
    return _result;
  }

  public void disconnect_structured_pull_supplier() {
    org.omg.CORBA.Request _request = this._request("disconnect_structured_pull_supplier");
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

  public void subscription_change(org.omg.CosNotification._EventType[] added, org.omg.CosNotification._EventType[] removed)
    throws org.omg.CosNotifyComm.InvalidEventType {
    org.omg.CORBA.Request _request = this._request("subscription_change");
    org.omg.CORBA.Any $added = _request.add_named_in_arg("added");
    org.omg.CosNotification.EventTypeSeqHelper.insert($added,added);
    org.omg.CORBA.Any $removed = _request.add_named_in_arg("removed");
    org.omg.CosNotification.EventTypeSeqHelper.insert($removed,removed);
    _request.exceptions().add(org.omg.CosNotifyComm.InvalidEventTypeHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
        if (_userException.except.type().equal(org.omg.CosNotifyComm.InvalidEventTypeHelper.type())) {
          throw org.omg.CosNotifyComm.InvalidEventTypeHelper.extract(_userException.except);
        }
        throw new org.omg.CORBA.UNKNOWN();
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
  }



}
