//
// SequencePullSupplierPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public class SequencePullSupplierPOATie
 extends SequencePullSupplierPOA
 implements SequencePullSupplierOperations {

  private SequencePullSupplierOperations _delegate;
  public SequencePullSupplierPOATie(SequencePullSupplierOperations delegate) {
    this._delegate = delegate;
  };

  public SequencePullSupplierOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/SequencePullSupplier:1.0",
    "IDL:org.omg/CosNotifyComm/NotifySubscribe:1.0"  };

  public org.omg.CosNotification.StructuredEvent[] pull_structured_events(int max_number)
    throws org.omg.CosEventComm.Disconnected {
    return this._delegate.pull_structured_events(
    max_number
    );
  };

  public org.omg.CosNotification.StructuredEvent[] try_pull_structured_events(int max_number, org.omg.CORBA.BooleanHolder has_event)
    throws org.omg.CosEventComm.Disconnected {
    return this._delegate.try_pull_structured_events(
    max_number, 
    has_event
    );
  };

  public void disconnect_sequence_pull_supplier() {
    this._delegate.disconnect_sequence_pull_supplier(
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
