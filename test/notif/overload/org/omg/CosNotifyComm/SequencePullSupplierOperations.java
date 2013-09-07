//
// SequencePullSupplier.java (interfaceOperations)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public interface SequencePullSupplierOperations
   extends org.omg.CosNotifyComm.NotifySubscribeOperations {

  org.omg.CosNotification.StructuredEvent[] pull_structured_events(int max_number)
    throws org.omg.CosEventComm.Disconnected;

  org.omg.CosNotification.StructuredEvent[] try_pull_structured_events(int max_number, org.omg.CORBA.BooleanHolder has_event)
    throws org.omg.CosEventComm.Disconnected;

  void disconnect_sequence_pull_supplier();


}
