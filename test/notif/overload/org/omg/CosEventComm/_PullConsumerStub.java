//
// _PullConsumerStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

public class _PullConsumerStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements PullConsumer {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PullConsumer:1.0"  };

  public void disconnect_pull_consumer() {
    org.omg.CORBA.Request _request = this._request("disconnect_pull_consumer");
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
