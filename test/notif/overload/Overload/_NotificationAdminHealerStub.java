//
// _NotificationAdminHealerStub.java (stub)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

public class _NotificationAdminHealerStub
 extends org.omg.CORBA.portable.ObjectImpl
 implements NotificationAdminHealer {

  public java.lang.String[] _ids() {
    return __ids;
  }

  private static java.lang.String[] __ids = {
    "IDL:org.omg/Overload/NotificationAdminHealer:1.0",
    "IDL:org.omg/Overload/HealingSensor:1.0",
    "IDL:org.omg/Overload/HealingActuator:1.0"  };

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
