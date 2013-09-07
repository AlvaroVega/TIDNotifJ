//
// SequencePullConsumerPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public class SequencePullConsumerPOATie
 extends SequencePullConsumerPOA
 implements SequencePullConsumerOperations {

  private SequencePullConsumerOperations _delegate;
  public SequencePullConsumerPOATie(SequencePullConsumerOperations delegate) {
    this._delegate = delegate;
  };

  public SequencePullConsumerOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/SequencePullConsumer:1.0",
    "IDL:org.omg/CosNotifyComm/NotifyPublish:1.0"  };

  public void disconnect_sequence_pull_consumer() {
    this._delegate.disconnect_sequence_pull_consumer(
    );
  };

  public void offer_change(org.omg.CosNotification._EventType[] added, org.omg.CosNotification._EventType[] removed)
    throws org.omg.CosNotifyComm.InvalidEventType {
    this._delegate.offer_change(
    added, 
    removed
    );
  };



}
