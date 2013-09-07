//
// UnsupportedQoS.java (exception)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class UnsupportedQoS
   extends org.omg.CORBA.UserException {

  public org.omg.CosNotification.PropertyError[] qos_err;

  public UnsupportedQoS() {
    super(UnsupportedQoSHelper.id());
  }

  public UnsupportedQoS(org.omg.CosNotification.PropertyError[] _qos_err) {
    super(UnsupportedQoSHelper.id());

    this.qos_err = _qos_err;
  }

  public UnsupportedQoS(String reason, org.omg.CosNotification.PropertyError[] _qos_err) {
    super(UnsupportedQoSHelper.id()+" "+reason);

    this.qos_err = _qos_err;
  }

}
