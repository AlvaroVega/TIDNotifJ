//
// PullSupplier.java (interfaceOperations)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

public interface PullSupplierOperations {

  org.omg.CORBA.Any pull()
    throws org.omg.CosEventComm.Disconnected;

  org.omg.CORBA.Any try_pull(org.omg.CORBA.BooleanHolder has_event)
    throws org.omg.CosEventComm.Disconnected;

  void disconnect_pull_supplier();


}
