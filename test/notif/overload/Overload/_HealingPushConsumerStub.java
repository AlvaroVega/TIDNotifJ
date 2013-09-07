//
// _HealingPushConsumerStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

public class _HealingPushConsumerStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements HealingPushConsumer {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:org.omg/Overload/HealingPushConsumer:1.0",
    "IDL:org.omg/CosNotifyComm/PushConsumer:1.0",
    "IDL:omg.org/CosEventComm/PushConsumer:1.0",
    "IDL:org.omg/CosNotifyComm/NotifyPublish:1.0",
    "IDL:org.omg/Overload/HealingSensor:1.0",
    "IDL:org.omg/Overload/HealingActuator:1.0"  };

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


  public void offer_change(org.omg.CosNotification._EventType[] added, org.omg.CosNotification._EventType[] removed)
    throws org.omg.CosNotifyComm.InvalidEventType {
    org.omg.CORBA.Request _request = this._request("offer_change");
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



  public void resetStatsCounter() {
    org.omg.CORBA.Request _request = this._request("resetStatsCounter");
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

  public Overload.Stats getStats() {
    org.omg.CORBA.Request _request = this._request("getStats");
    _request.set_return_type(Overload.StatsHelper.type());
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
    Overload.Stats _result;
    _result = Overload.StatsHelper.extract(_request.return_value());
    return _result;
  }

  public Overload.Stats getLastSecondsStats(int seconds) {
    org.omg.CORBA.Request _request = this._request("getLastSecondsStats");
    _request.set_return_type(Overload.StatsHelper.type());
    org.omg.CORBA.Any $seconds = _request.add_named_in_arg("seconds");
    $seconds.insert_ulong(seconds);
    _request.invoke();
    java.lang.Exception _exception = _request.env().exception();
    if (_exception != null) {
      if (_exception instanceof org.omg.CORBA.UnknownUserException) {
        org.omg.CORBA.UnknownUserException _userException = 
          (org.omg.CORBA.UnknownUserException) _exception;
      }
      throw (org.omg.CORBA.SystemException) _exception;
    };
    Overload.Stats _result;
    _result = Overload.StatsHelper.extract(_request.return_value());
    return _result;
  }




}
