//
// PullConsumerPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public class PullConsumerPOATie
 extends PullConsumerPOA
 implements PullConsumerOperations {

  private PullConsumerOperations _delegate;
  public PullConsumerPOATie(PullConsumerOperations delegate) {
    this._delegate = delegate;
  };

  public PullConsumerOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotifyComm/PullConsumer:1.0",
    "IDL:omg.org/CosEventComm/PullConsumer:1.0",
    "IDL:org.omg/CosNotifyComm/NotifyPublish:1.0"  };

  public void disconnect_pull_consumer() {
    this._delegate.disconnect_pull_consumer(
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
