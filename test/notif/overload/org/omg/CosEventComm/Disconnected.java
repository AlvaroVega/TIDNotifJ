//
// Disconnected.java (exception)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosEventComm;

final public class Disconnected
   extends org.omg.CORBA.UserException {


  public Disconnected() {
    super(DisconnectedHelper.id());
  }

  public Disconnected(String reason) {
    super(DisconnectedHelper.id()+" "+reason);

  }

}
