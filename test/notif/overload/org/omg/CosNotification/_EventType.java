//
// _EventType.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class _EventType
   implements org.omg.CORBA.portable.IDLEntity {

  public java.lang.String domain_name;
  public java.lang.String type_name;

  public _EventType() {
    domain_name = "";
    type_name = "";
  }

  public _EventType(java.lang.String domain_name, java.lang.String type_name) {
    this.domain_name = domain_name;
    this.type_name = type_name;
  }

}
