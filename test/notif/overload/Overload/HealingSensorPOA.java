//
// HealingSensorPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

abstract public class HealingSensorPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements HealingSensorOperations {

  public HealingSensor _this() {
    return HealingSensorHelper.narrow(super._this_object());
  };

  public HealingSensor _this(org.omg.CORBA.ORB orb) {
    return HealingSensorHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/Overload/HealingSensor:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("resetStatsCounter", new Integer(0));
    _methods.put("getStats", new Integer(1));
    _methods.put("getLastSecondsStats", new Integer(2));
  }

  public void invoke(org.omg.CORBA.ServerRequest _request) {
    java.lang.Object _method = _methods.get(_request.operation());
    if (_method == null) {
      throw new org.omg.CORBA.BAD_OPERATION(_request.operation());
    }
    int _method_id = ((java.lang.Integer)_method).intValue();
    switch(_method_id) {
    case 0: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      this.resetStatsCounter();
      return;
    }
    case 1: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      Overload.Stats _result = this.getStats();
      org.omg.CORBA.Any _resultAny = _orb().create_any();
      Overload.StatsHelper.insert(_resultAny, _result);
      _request.set_result(_resultAny);
      return;
    }
    case 2: {
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
