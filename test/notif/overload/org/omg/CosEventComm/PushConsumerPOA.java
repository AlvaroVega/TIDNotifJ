//
// PushConsumerPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

abstract public class PushConsumerPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements PushConsumerOperations {

  public PushConsumer _this() {
    return PushConsumerHelper.narrow(super._this_object());
  };

  public PushConsumer _this(org.omg.CORBA.ORB orb) {
    return PushConsumerHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PushConsumer:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("push", new Integer(0));
    _methods.put("disconnect_push_consumer", new Integer(1));
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
    }
  }
}
