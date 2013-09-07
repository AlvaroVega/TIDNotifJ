//
// AdminPropertiesAdminHolder.java (holder)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class AdminPropertiesAdminHolder
   implements org.omg.CORBA.portable.Streamable {

  public AdminPropertiesAdmin value; 
  public AdminPropertiesAdminHolder() {
  }

  public AdminPropertiesAdminHolder(AdminPropertiesAdmin initial) {
    value = initial;
  }

  public void _read(org.omg.CORBA.portable.InputStream is) {
    value = org.omg.CosNotification.AdminPropertiesAdminHelper.read(is);
  };

  public void _write(org.omg.CORBA.portable.OutputStream os) {
    org.omg.CosNotification.AdminPropertiesAdminHelper.write(os, value);
  };

  public org.omg.CORBA.TypeCode _type() {
    return org.omg.CosNotification.AdminPropertiesAdminHelper.type();
  };

}
