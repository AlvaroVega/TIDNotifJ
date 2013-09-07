//
// PullConsumerPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

abstract public class PullConsumerPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements PullConsumerOperations {

  public PullConsumer _this() {
    return PullConsumerHelper.narrow(super._this_object());
  };

  public PullConsumer _this(org.omg.CORBA.ORB orb) {
    return PullConsumerHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PullConsumer:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("disconnect_pull_consumer", new Integer(0));
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
      this.disconnect_pull_consumer();
      return;
    }
    }
  }
}
