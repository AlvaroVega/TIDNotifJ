//
// EventHeader.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class EventHeader
   implements org.omg.CORBA.portable.IDLEntity {

  public org.omg.CosNotification.FixedEventHeader fixed_header;
  public org.omg.CosNotification.Property[] variable_header;

  public EventHeader() {
  }

  public EventHeader(org.omg.CosNotification.FixedEventHeader fixed_header, org.omg.CosNotification.Property[] variable_header) {
    this.fixed_header = fixed_header;
    this.variable_header = variable_header;
  }

}
