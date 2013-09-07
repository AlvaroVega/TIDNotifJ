//
// QoSError_code.java (enum)
//
// File generated: Wed Feb 25 11:12:05 CET 2009
//   by TIDorb idl2java 1.3.7
//

package org.omg.CosNotification;

public class QoSError_code
   implements org.omg.CORBA.portable.IDLEntity {

  public static final int _UNSUPPORTED_PROPERTY = 0;
  public static final QoSError_code UNSUPPORTED_PROPERTY = new QoSError_code(_UNSUPPORTED_PROPERTY);
  public static final int _UNAVAILABLE_PROPERTY = 1;
  public static final QoSError_code UNAVAILABLE_PROPERTY = new QoSError_code(_UNAVAILABLE_PROPERTY);
  public static final int _UNSUPPORTED_VALUE = 2;
  public static final QoSError_code UNSUPPORTED_VALUE = new QoSError_code(_UNSUPPORTED_VALUE);
  public static final int _UNAVAILABLE_VALUE = 3;
  public static final QoSError_code UNAVAILABLE_VALUE = new QoSError_code(_UNAVAILABLE_VALUE);
  public static final int _BAD_PROPERTY = 4;
  public static final QoSError_code BAD_PROPERTY = new QoSError_code(_BAD_PROPERTY);
  public static final int _BAD_TYPE = 5;
  public static final QoSError_code BAD_TYPE = new QoSError_code(_BAD_TYPE);
  public static final int _BAD_VALUE = 6;
  public static final QoSError_code BAD_VALUE = new QoSError_code(_BAD_VALUE);

  public int value() { return _value; }
  public static QoSError_code from_int(int value) {
    switch (value) {
      case 0: return UNSUPPORTED_PROPERTY;
      case 1: return UNAVAILABLE_PROPERTY;
      case 2: return UNSUPPORTED_VALUE;
      case 3: return UNAVAILABLE_VALUE;
      case 4: return BAD_PROPERTY;
      case 5: return BAD_TYPE;
      case 6: return BAD_VALUE;
    };
    return null;
  };
  protected QoSError_code (int value) { _value = value; };
  public java.lang.Object readResolve()
    throws java.io.ObjectStreamException
  {
    return from_int(value());
  }
  private int _value;
}
