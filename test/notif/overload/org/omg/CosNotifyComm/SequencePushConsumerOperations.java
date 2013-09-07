//
// SequencePushConsumer.java (interfaceOperations)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public interface SequencePushConsumerOperations
   extends org.omg.CosNotifyComm.NotifyPublishOperations {

  void push_structured_events(org.omg.CosNotification.StructuredEvent[] notifications)
    throws org.omg.CosEventComm.Disconnected;

  void disconnect_sequence_push_consumer();


}
