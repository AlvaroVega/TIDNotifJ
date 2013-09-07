//
// Property.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class Property
   implements org.omg.CORBA.portable.IDLEntity {

  public java.lang.String name;
  public org.omg.CORBA.Any value;

  public Property() {
    name = "";
  }

  public Property(java.lang.String name, org.omg.CORBA.Any value) {
    this.name = name;
    this.value = value;
  }

}
