//
// PushConsumerPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

public class PushConsumerPOATie
 extends PushConsumerPOA
 implements PushConsumerOperations {

  private PushConsumerOperations _delegate;
  public PushConsumerPOATie(PushConsumerOperations delegate) {
    this._delegate = delegate;
  };

  public PushConsumerOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:omg.org/CosEventComm/PushConsumer:1.0"  };

  public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    this._delegate.push(
    data
    );
  };

  public void disconnect_push_consumer() {
    this._delegate.disconnect_push_consumer(
    );
  };


}
