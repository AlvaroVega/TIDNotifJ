//
// PropertyRange.java (struct)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class PropertyRange
   implements org.omg.CORBA.portable.IDLEntity {

  public org.omg.CORBA.Any low_val;
  public org.omg.CORBA.Any high_val;

  public PropertyRange() {
  }

  public PropertyRange(org.omg.CORBA.Any low_val, org.omg.CORBA.Any high_val) {
    this.low_val = low_val;
    this.high_val = high_val;
  }

}
