//
// _PushConsumerStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

public class _PushConsumerStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements PushConsumer {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PushConsumer:1.0"  };

  public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    org.omg.CORBA.Request _request = this._request("push");
    org.omg.CORBA.Any $data = _request.add_named_in_arg("data");
    $data.insert_any(data);
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
  }

  public void disconnect_push_consumer() {
    org.omg.CORBA.Request _request = this._request("disconnect_push_consumer");
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
