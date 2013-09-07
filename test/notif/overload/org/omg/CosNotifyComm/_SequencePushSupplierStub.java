//
// _SequencePushSupplierStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public class _SequencePushSupplierStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements SequencePushSupplier {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/SequencePushSupplier:1.0",
    "IDL:org.omg/CosNotifyComm/NotifySubscribe:1.0"  };

  public void disconnect_sequence_push_supplier() {
    org.omg.CORBA.Request _request = this._request("disconnect_sequence_push_supplier");
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
