//
// NamedPropertyRange.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class NamedPropertyRange
   implements org.omg.CORBA.portable.IDLEntity {

  public java.lang.String name;
  public org.omg.CosNotification.PropertyRange range;

  public NamedPropertyRange() {
    name = "";
  }

  public NamedPropertyRange(java.lang.String name, org.omg.CosNotification.PropertyRange range) {
    this.name = name;
    this.range = range;
  }

}
