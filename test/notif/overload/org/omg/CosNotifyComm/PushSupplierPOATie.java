//
// PushSupplierPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public class PushSupplierPOATie
 extends PushSupplierPOA
 implements PushSupplierOperations {

  private PushSupplierOperations _delegate;
  public PushSupplierPOATie(PushSupplierOperations delegate) {
    this._delegate = delegate;
  };

  public PushSupplierOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/PushSupplier:1.0",
    "IDL:omg.org/CosEventComm/PushSupplier:1.0",
    "IDL:org.omg/CosNotifyComm/NotifySubscribe:1.0"  };

  public void disconnect_push_supplier() {
    this._delegate.disconnect_push_supplier(
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
