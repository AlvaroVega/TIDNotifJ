//
// NotificationAdminHealerPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package Overload;

public class NotificationAdminHealerPOATie
 extends NotificationAdminHealerPOA
 implements NotificationAdminHealerOperations {

  private NotificationAdminHealerOperations _delegate;
  public NotificationAdminHealerPOATie(NotificationAdminHealerOperations delegate) {
    this._delegate = delegate;
  };

  public NotificationAdminHealerOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/Overload/NotificationAdminHealer:1.0",
    "IDL:org.omg/Overload/HealingSensor:1.0",
    "IDL:org.omg/Overload/HealingActuator:1.0"  };

  public void resetStatsCounter() {
    this._delegate.resetStatsCounter(
    );
  };

  public Overload.Stats getStats() {
    return this._delegate.getStats(
    );
  };

  public Overload.Stats getLastSecondsStats(int seconds) {
    return this._delegate.getLastSecondsStats(
    seconds
    );
  };




}
