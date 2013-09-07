//
// HealingPushConsumerPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

abstract public class HealingPushConsumerPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements HealingPushConsumerOperations {

  public HealingPushConsumer _this() {
    return HealingPushConsumerHelper.narrow(super._this_object());
  };

  public HealingPushConsumer _this(org.omg.CORBA.ORB orb) {
    return HealingPushConsumerHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/Overload/HealingPushConsumer:1.0",
    "IDL:org.omg/CosNotifyComm/PushConsumer:1.0",
    "IDL:omg.org/CosEventComm/PushConsumer:1.0",
    "IDL:org.omg/CosNotifyComm/NotifyPublish:1.0",
    "IDL:org.omg/Overload/HealingSensor:1.0",
    "IDL:org.omg/Overload/HealingActuator:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("push", new Integer(0));
    _methods.put("disconnect_push_consumer", new Integer(1));
    _methods.put("offer_change", new Integer(2));
    _methods.put("resetStatsCounter", new Integer(3));
    _methods.put("getStats", new Integer(4));
    _methods.put("getLastSecondsStats", new Integer(5));
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
      org.omg.CORBA.NVList _params = _orb().create_list(1);
      org.omg.CORBA.Any $data = _orb().create_any();
      $data.type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_any));
      _params.add_value("data", $data, org.omg.CORBA.ARG_IN.value);
      _request.arguments(_params);
      org.omg.CORBA.Any data;
      data = $data.extract_any();
      this.push(data);
      } catch(org.omg.CosEventComm.Disconnected _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosEventComm.DisconnectedHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    case 1: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      this.disconnect_push_consumer();
      return;
    }
    case 2: {
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
      this.offer_change(added, removed);
      } catch(org.omg.CosNotifyComm.InvalidEventType _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosNotifyComm.InvalidEventTypeHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    case 3: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      this.resetStatsCounter();
      return;
    }
    case 4: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      Overload.Stats _result = this.getStats();
      org.omg.CORBA.Any _resultAny = _orb().create_any();
      Overload.StatsHelper.insert(_resultAny, _result);
      _request.set_result(_resultAny);
      return;
    }
    case 5: {
      org.omg.CORBA.NVList _params = _orb().create_list(1);
      org.omg.CORBA.Any $seconds = _orb().create_any();
      $seconds.type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_ulong));
      _params.add_value("seconds", $seconds, org.omg.CORBA.ARG_IN.value);
      _request.arguments(_params);
      int seconds;
      seconds = $seconds.extract_ulong();
      Overload.Stats _result = this.getLastSecondsStats(seconds);
      org.omg.CORBA.Any _resultAny = _orb().create_any();
      Overload.StatsHelper.insert(_resultAny, _result);
      _request.set_result(_resultAny);
      return;
    }
    }
  }
}
