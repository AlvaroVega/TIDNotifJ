//
// QoSAdmin.java (interfaceOperations)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public interface QoSAdminOperations {

  org.omg.CosNotification.Property[] get_qos();

  void set_qos(org.omg.CosNotification.Property[] qos)
    throws org.omg.CosNotification.UnsupportedQoS;

  void validate_qos(org.omg.CosNotification.Property[] required_qos, org.omg.CosNotification.NamedPropertyRangeSeqHolder available_qos)
    throws org.omg.CosNotification.UnsupportedQoS;


}
