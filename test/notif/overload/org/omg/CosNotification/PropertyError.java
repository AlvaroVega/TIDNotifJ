//
// PropertyError.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class PropertyError
   implements org.omg.CORBA.portable.IDLEntity {

  public org.omg.CosNotification.QoSError_code code;
  public java.lang.String name;
  public org.omg.CosNotification.PropertyRange available_range;

  public PropertyError() {
    name = "";
  }

  public PropertyError(org.omg.CosNotification.QoSError_code code, java.lang.String name, org.omg.CosNotification.PropertyRange available_range) {
    this.code = code;
    this.name = name;
    this.available_range = available_range;
  }

}
