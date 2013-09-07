//
// StructuredPullSupplier.java (interfaceOperations)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

public interface StructuredPullSupplierOperations
   extends org.omg.CosNotifyComm.NotifySubscribeOperations {

  org.omg.CosNotification.StructuredEvent pull_structured_event()
    throws org.omg.CosEventComm.Disconnected;

  org.omg.CosNotification.StructuredEvent try_pull_structured_event(org.omg.CORBA.BooleanHolder has_event)
    throws org.omg.CosEventComm.Disconnected;

  void disconnect_structured_pull_supplier();


}
