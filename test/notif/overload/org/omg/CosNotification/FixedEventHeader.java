//
// FixedEventHeader.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class FixedEventHeader
   implements org.omg.CORBA.portable.IDLEntity {

  public org.omg.CosNotification._EventType event_type;
  public java.lang.String event_name;

  public FixedEventHeader() {
    event_name = "";
  }

  public FixedEventHeader(org.omg.CosNotification._EventType event_type, java.lang.String event_name) {
    this.event_type = event_type;
    this.event_name = event_name;
  }

}
