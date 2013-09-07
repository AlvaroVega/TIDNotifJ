//
// StructuredEvent.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class StructuredEvent
   implements org.omg.CORBA.portable.IDLEntity {

  public org.omg.CosNotification.EventHeader header;
  public org.omg.CosNotification.Property[] filterable_data;
  public org.omg.CORBA.Any remainder_of_body;

  public StructuredEvent() {
  }

  public StructuredEvent(org.omg.CosNotification.EventHeader header, org.omg.CosNotification.Property[] filterable_data, org.omg.CORBA.Any remainder_of_body) {
    this.header = header;
    this.filterable_data = filterable_data;
    this.remainder_of_body = remainder_of_body;
  }

}
