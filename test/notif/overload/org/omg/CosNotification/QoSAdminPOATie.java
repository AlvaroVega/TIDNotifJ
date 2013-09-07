//
// QoSAdminPOATie.java (tie)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class QoSAdminPOATie
 extends QoSAdminPOA
 implements QoSAdminOperations {

  private QoSAdminOperations _delegate;
  public QoSAdminPOATie(QoSAdminOperations delegate) {
    this._delegate = delegate;
  };

  public QoSAdminOperations _delegate() {
    return this._delegate;
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotification/QoSAdmin:1.0"  };

  public org.omg.CosNotification.Property[] get_qos() {
    return this._delegate.get_qos(
    );
  };

  public void set_qos(org.omg.CosNotification.Property[] qos)
    throws org.omg.CosNotification.UnsupportedQoS {
    this._delegate.set_qos(
    qos
    );
  };

  public void validate_qos(org.omg.CosNotification.Property[] required_qos, org.omg.CosNotification.NamedPropertyRangeSeqHolder available_qos)
    throws org.omg.CosNotification.UnsupportedQoS {
    this._delegate.validate_qos(
    required_qos, 
    available_qos
    );
  };


}
