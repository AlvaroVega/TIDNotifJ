//
// PushConsumer.java (interfaceOperations)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

public interface PushConsumerOperations {

  void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected;

  void disconnect_push_consumer();


}
