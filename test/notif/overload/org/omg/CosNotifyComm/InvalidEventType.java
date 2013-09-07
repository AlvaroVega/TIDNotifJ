//
// InvalidEventType.java (exception)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotifyComm;

final public class InvalidEventType
   extends org.omg.CORBA.UserException {

  public org.omg.CosNotification._EventType type;

  public InvalidEventType() {
    super(InvalidEventTypeHelper.id());
  }

  public InvalidEventType(org.omg.CosNotification._EventType _type) {
    super(InvalidEventTypeHelper.id());

    this.type = _type;
  }

  public InvalidEventType(String reason, org.omg.CosNotification._EventType _type) {
    super(InvalidEventTypeHelper.id()+" "+reason);

    this.type = _type;
  }

}
