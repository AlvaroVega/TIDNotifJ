//
// AdminPropertiesAdminPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class AdminPropertiesAdminPOATie
 extends AdminPropertiesAdminPOA
 implements AdminPropertiesAdminOperations {

  private AdminPropertiesAdminOperations _delegate;
  public AdminPropertiesAdminPOATie(AdminPropertiesAdminOperations delegate) {
    this._delegate = delegate;
  };

  public AdminPropertiesAdminOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotification/AdminPropertiesAdmin:1.0"  };

  public org.omg.CosNotification.Property[] get_admin() {
    return this._delegate.get_admin(
    );
  };

  public void set_admin(org.omg.CosNotification.Property[] admin)
    throws org.omg.CosNotification.UnsupportedAdmin {
    this._delegate.set_admin(
    admin
    );
  };


}
