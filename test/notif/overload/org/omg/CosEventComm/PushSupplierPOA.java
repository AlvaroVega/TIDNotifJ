//
// PushSupplierPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

abstract public class PushSupplierPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements PushSupplierOperations {

  public PushSupplier _this() {
    return PushSupplierHelper.narrow(super._this_object());
  };

  public PushSupplier _this(org.omg.CORBA.ORB orb) {
    return PushSupplierHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PushSupplier:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("disconnect_push_supplier", new Integer(0));
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
      this.disconnect_push_supplier();
      return;
    }
    }
  }
}
