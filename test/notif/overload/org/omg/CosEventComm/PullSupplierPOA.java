//
// PullSupplierPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

abstract public class PullSupplierPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements PullSupplierOperations {

  public PullSupplier _this() {
    return PullSupplierHelper.narrow(super._this_object());
  };

  public PullSupplier _this(org.omg.CORBA.ORB orb) {
    return PullSupplierHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PullSupplier:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("pull", new Integer(0));
    _methods.put("try_pull", new Integer(1));
    _methods.put("disconnect_pull_supplier", new Integer(2));
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
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      org.omg.CORBA.Any _result = this.pull();
      org.omg.CORBA.Any _resultAny = _orb().create_any();
      _resultAny.insert_any(_result);
      _request.set_result(_resultAny);
      } catch(org.omg.CosEventComm.Disconnected _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosEventComm.DisconnectedHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    case 1: {
      try {
      org.omg.CORBA.NVList _params = _orb().create_list(1);
      org.omg.CORBA.Any $has_event = _orb().create_any();
      $has_event.type(org.omg.CORBA.ORB.init().get_primitive_tc(org.omg.CORBA.TCKind.tk_boolean));
      _params.add_value("has_event", $has_event, org.omg.CORBA.ARG_OUT.value);
      _request.arguments(_params);
      org.omg.CORBA.BooleanHolder has_event = new org.omg.CORBA.BooleanHolder();
      org.omg.CORBA.Any _result = this.try_pull(has_event);
      org.omg.CORBA.Any _resultAny = _orb().create_any();
      _resultAny.insert_any(_result);
      _request.set_result(_resultAny);
      $has_event.insert_boolean(has_event.value);
      } catch(org.omg.CosEventComm.Disconnected _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosEventComm.DisconnectedHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    case 2: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      this.disconnect_pull_supplier();
      return;
    }
    }
  }
}
