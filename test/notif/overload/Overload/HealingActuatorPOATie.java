//
// HealingActuatorPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

public class HealingActuatorPOATie
 extends HealingActuatorPOA
 implements HealingActuatorOperations {

  private HealingActuatorOperations _delegate;
  public HealingActuatorPOATie(HealingActuatorOperations delegate) {
    this._delegate = delegate;
  };

  public HealingActuatorOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/Overload/HealingActuator:1.0"  };


}
