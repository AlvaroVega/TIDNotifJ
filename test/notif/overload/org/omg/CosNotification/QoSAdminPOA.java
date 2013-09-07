//
// QoSAdminPOA.java (skeleton)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

abstract public class QoSAdminPOA
 extends org.omg.PortableServer.DynamicImplementation
 implements QoSAdminOperations {

  public QoSAdmin _this() {
    return QoSAdminHelper.narrow(super._this_object());
  };

  public QoSAdmin _this(org.omg.CORBA.ORB orb) {
    return QoSAdminHelper.narrow(super._this_object(orb));
  };

  public java.lang.String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectID) {
    return __ids;
  };

  private static java.lang.String[] __ids = {
    "IDL:org.omg/CosNotification/QoSAdmin:1.0"
  };

  private static java.util.Dictionary _methods = new java.util.Hashtable();
  static {
    _methods.put("get_qos", new Integer(0));
    _methods.put("set_qos", new Integer(1));
    _methods.put("validate_qos", new Integer(2));
  }

  public void invoke(org.omg.CORBA.ServerRequest _request) {
    java.lang.Object _method = _methods.get(_request.operation());
    if (_method == null) {
      throw new org.omg.CORBA.BAD_OPERATION(_request.operation());
    }
    int _method_id = ((java.lang.Integer)_method).intValue();
    switch(_method_id) {
    case 0: {
      org.omg.CORBA.NVList _params = _orb().create_list(0);
      _request.arguments(_params);
      org.omg.CosNotification.Property[] _result = this.get_qos();
      org.omg.CORBA.Any _resultAny = _orb().create_any();
      org.omg.CosNotification.QoSPropertiesHelper.insert(_resultAny, _result);
      _request.set_result(_resultAny);
      return;
    }
    case 1: {
      try {
      org.omg.CORBA.NVList _params = _orb().create_list(1);
      org.omg.CORBA.Any $qos = _orb().create_any();
      $qos.type(org.omg.CosNotification.QoSPropertiesHelper.type());
      _params.add_value("qos", $qos, org.omg.CORBA.ARG_IN.value);
      _request.arguments(_params);
      org.omg.CosNotification.Property[] qos;
      qos = org.omg.CosNotification.QoSPropertiesHelper.extract($qos);
      this.set_qos(qos);
      } catch(org.omg.CosNotification.UnsupportedQoS _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosNotification.UnsupportedQoSHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    case 2: {
      try {
      org.omg.CORBA.NVList _params = _orb().create_list(2);
      org.omg.CORBA.Any $required_qos = _orb().create_any();
      $required_qos.type(org.omg.CosNotification.QoSPropertiesHelper.type());
      _params.add_value("required_qos", $required_qos, org.omg.CORBA.ARG_IN.value);
      org.omg.CORBA.Any $available_qos = _orb().create_any();
      $available_qos.type(org.omg.CosNotification.NamedPropertyRangeSeqHelper.type());
      _params.add_value("available_qos", $available_qos, org.omg.CORBA.ARG_OUT.value);
      _request.arguments(_params);
      org.omg.CosNotification.Property[] required_qos;
      required_qos = org.omg.CosNotification.QoSPropertiesHelper.extract($required_qos);
      org.omg.CosNotification.NamedPropertyRangeSeqHolder available_qos = new org.omg.CosNotification.NamedPropertyRangeSeqHolder();
      this.validate_qos(required_qos, available_qos);
      org.omg.CosNotification.NamedPropertyRangeSeqHelper.insert($available_qos,available_qos.value);
      } catch(org.omg.CosNotification.UnsupportedQoS _exception) {
        org.omg.CORBA.Any _exceptionAny = _orb().create_any();
        org.omg.CosNotification.UnsupportedQoSHelper.insert(_exceptionAny, _exception);
        _request.set_exception(_exceptionAny);
      }
      return;
    }
    }
  }
}
