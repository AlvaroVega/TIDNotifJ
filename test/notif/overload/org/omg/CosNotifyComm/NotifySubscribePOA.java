//
// NotifySubscribePOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

abstract public class NotifySubscribePOA
 extends org.omg.PortableServer.DynamicImplementation
 implements NotifySubscribeOperations {

  public NotifySubscribe _this() {
    return NotifySubscribeHelper.narrow(super._this_object());
  };

  public NotifySubscribe _this(org.omg.CORBA.ORB orb) {
    return NotifySubscribeHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/NotifySubscribe:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("subscription_change", new Integer(0));
  }

  public void invoke(org.omg.CORBA.ServerRequest _request) {
    java.lang.Object _method = _methods.get(_request.operation());
    if (_method == null) {
      throw new org.omg.CORBA.BAD_OPERATION(_request.operation());
    }
    int _method_id = ((java.lang.Integer)_method).intValue();
    switch(_method_id) {
    case 0: {
      try {
      org.omg.CORBA.NVList _params = _orb().create_list(2);
      org.omg.CORBA.Any $added = _orb().create_any();
      $added.type(org.omg.CosNotification.EventTypeSeqHelper.type());
      _params.add_value("added", $added, org.omg.CORBA.ARG_IN.value);
      org.omg.CORBA.Any $removed = _orb().create_any();
      $removed.type(org.omg.CosNotification.EventTypeSeqHelper.type());
      _params.add_value("removed", $removed, org.omg.CORBA.ARG_IN.value);
      _request.arguments(_params);
      org.omg.CosNotification._EventType[] added;
      added = org.omg.CosNotification.EventTypeSeqHelper.extract($added);
      org.omg.CosNotification._EventType[] removed;
      removed = org.omg.CosNotification.EventTypeSeqHelper.extract($removed);
      this.subscription_change(added, removed);
      } catch(org.omg.CosNotifyComm.InvalidEventType _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosNotifyComm.InvalidEventTypeHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    }
  }
}
