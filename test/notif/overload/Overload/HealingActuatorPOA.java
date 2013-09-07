//
// HealingActuatorPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

abstract public class HealingActuatorPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements HealingActuatorOperations {

  public HealingActuator _this() {
    return HealingActuatorHelper.narrow(super._this_object());
  };

  public HealingActuator _this(org.omg.CORBA.ORB orb) {
    return HealingActuatorHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/Overload/HealingActuator:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
  }

  public void invoke(org.omg.CORBA.ServerRequest _request) {
    java.lang.Object _method = _methods.get(_request.operation());
    if (_method == null) {
      throw new org.omg.CORBA.BAD_OPERATION(_request.operation());
    }
    int _method_id = ((java.lang.Integer)_method).intValue();
    switch(_method_id) {
    }
  }
}
