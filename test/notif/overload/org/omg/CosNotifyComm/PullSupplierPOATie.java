//
// PullSupplierPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public class PullSupplierPOATie
 extends PullSupplierPOA
 implements PullSupplierOperations {

  private PullSupplierOperations _delegate;
  public PullSupplierPOATie(PullSupplierOperations delegate) {
    this._delegate = delegate;
  };

  public PullSupplierOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/PullSupplier:1.0",
    "IDL:omg.org/CosEventComm/PullSupplier:1.0",
    "IDL:org.omg/CosNotifyComm/NotifySubscribe:1.0"  };

  public org.omg.CORBA.Any pull()
    throws org.omg.CosEventComm.Disconnected {
    return this._delegate.pull(
    );
  };

  public org.omg.CORBA.Any try_pull(org.omg.CORBA.BooleanHolder has_event)
    throws org.omg.CosEventComm.Disconnected {
    return this._delegate.try_pull(
    has_event
    );
  };

  public void disconnect_pull_supplier() {
    this._delegate.disconnect_pull_supplier(
    );
  };


  public void subscription_change(org.omg.CosNotification._EventType[] added, org.omg.CosNotification._EventType[] removed)
    throws org.omg.CosNotifyComm.InvalidEventType {
    this._delegate.subscription_change(
    added, 
    removed
    );
  };



}
