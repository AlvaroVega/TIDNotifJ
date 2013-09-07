//
// UnsupportedAdmin.java (exception)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

final public class UnsupportedAdmin
   extends org.omg.CORBA.UserException {

  public org.omg.CosNotification.PropertyError[] admin_err;

  public UnsupportedAdmin() {
    super(UnsupportedAdminHelper.id());
  }

  public UnsupportedAdmin(org.omg.CosNotification.PropertyError[] _admin_err) {
    super(UnsupportedAdminHelper.id());

    this.admin_err = _admin_err;
  }

  public UnsupportedAdmin(String reason, org.omg.CosNotification.PropertyError[] _admin_err) {
    super(UnsupportedAdminHelper.id()+" "+reason);

    this.admin_err = _admin_err;
  }

}
