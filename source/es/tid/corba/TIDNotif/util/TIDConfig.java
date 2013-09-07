/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 64 $
* Date: $Date: 2008-04-07 17:37:28 +0200 (Mon, 07 Apr 2008) $
* Last modified by: $Author: avega $
*
* (C) Copyright 2005 Telefónica Investigación y Desarrollo
*     S.A.Unipersonal (Telefónica I+D)
*
* Info about members and contributors of the MORFEO project
* is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/CREDITS
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
*
* If you want to use this software an plan to distribute a
* proprietary application in any way, and you are not licensing and
* distributing your source code under GPL, you probably need to
* purchase a commercial license of the product.  More info about
* licensing options is available at:
*
*   http://www.morfeo-project.org/TIDNotifJ/Licensing
*/ 

package es.tid.corba.TIDNotif.util;

public class TIDConfig
{
  // Property Types
  public static final String NUMTYPE  = "N";
  public static final String BOOLTYPE = "B";
  public static final String STRTYPE  = "S";

  // Property Attributes
  public static final int RO_ATTR       =  0;
  public static final int RW_ATTR       =  1;
  public static final String READ_ONLY  = "RO";
  public static final String READ_WRITE = "RW";

  // Integer Value Limits
  public static final int NO_LIMIT  =  -1;
  public static final String NO_MIN = "";
  public static final String NO_MAX = "";

  // Boolean Values
  public static final String TRUE_VALUE = "true";
  public static final String FALSE_VALUE = "false";

  public static final int NAME_INDEX  = 0;
  public static final int TYPE_INDEX  = 1;
  public static final int ATTR_INDEX  = 2;
  public static final int VALUE_INDEX = 3;
  public static final int MIN_INDEX   = 4;
  public static final int MAX_INDEX   = 5;

  // Table con los parametros configurables, sus valores y vus limites
  //{
  //  { "PARAM_KEY", "NUMTYPE", "ATTR", "VALUE", "MIN", "MAX"},
  //  { ... }
  //  { "PARAM_KEY", "STRTYPE", "ATTR", "VALUE" },
  //  { ... }
  //  { "PARAM_KEY", "BOOLSTRTYPE", "ATTR", "VALUE" },
  //  { ... }
  //  { "PARAM_KEY", "NUMTYPE", "ATTR", "VALUE", "MIN", "MAX"}
  //}
      
  private boolean _debug = false;
  private String key_prefix;
  private java.util.Properties tid_config = null;
  private java.util.Hashtable check_table = null;


  /**
   * Private constructor
   *
   * String prefix
   * String[][] config_base
   * java.util.Properties new_values
   *
   */
  public TIDConfig( String prefix, 
                    String[][] config_base,
                    java.util.Properties new_values )
  {
    // Prefijo paras las claves
    key_prefix = prefix;

    // Creamos la lista de propiedades por defecto y su tabla de chequeo
    tid_config = createDefaults(config_base);
    check_table = createCheckTable(config_base);

    // La actualizamos con los valores suministrados
    updateConfig( tid_config, new_values);
  }

  /**
   * createDefaults 
   *
   * String[][] config_base
   *
   */
  private java.util.Properties createDefaults( String[][] config_base )
  {
    java.util.Properties defaults = new java.util.Properties();
    // Rellenamos la lista de parametros configurables por defecto.
    for (int i = 0; i < config_base.length; i++)
    {
      defaults.setProperty( config_base[i][NAME_INDEX], 
                            config_base[i][VALUE_INDEX] );
    }
    return defaults;
  }

  /**
   * createCheckTable 
   *
   * String[][] config_base
   *
   */
  private java.util.Hashtable createCheckTable( String[][] config_base )
  {
    java.util.Hashtable checktable = new java.util.Hashtable();

    for (int i = 0; i < config_base.length; i++)
    {
      int attr;

      if (READ_WRITE.compareTo(config_base[i][ATTR_INDEX]) == 0)
      {
        attr = RW_ATTR;
      }
      else if (READ_ONLY.compareTo(config_base[i][ATTR_INDEX]) == 0)
      {
        attr = RO_ATTR;
      }
      else
      {
        if (_debug)
          System.err.println("TIDConfig.createCheckTable(): invalid attribute value.");
        attr = RO_ATTR;
      }

      TIDPropValue value;

      if (NUMTYPE.compareTo(config_base[i][TYPE_INDEX]) == 0) 
      {
        int min, max;

        try
        {
          min = Integer.parseInt(config_base[i][MIN_INDEX]);
        }
        catch (Exception e)
        {
          if (_debug)
          {
            System.err.println("TIDConfig: parseInt(min) Exception.");
            e.printStackTrace();
          }
          min = NO_LIMIT;
        }
        try 
        {
          max = Integer.parseInt(config_base[i][MAX_INDEX]);
        }
        catch (Exception e)
        {
          if (_debug)
          {
            System.err.println("TIDConfig: parseInt(max) Exception.");
            e.printStackTrace();
          }
          max = NO_LIMIT;
        }
        value = new TIDPropValue( config_base[i][TYPE_INDEX], attr, min, max );
      }
      else
      {
        value = new TIDPropValue( config_base[i][TYPE_INDEX], attr );
      }
      checktable.put( config_base[i][NAME_INDEX], value );
    }
    return checktable;
  }

  /**
   * updateConfig 
   *
   * java.util.Properties config
   * java.util.Properties new_values
   *
   */
  private void updateConfig( java.util.Properties config,
                             java.util.Properties new_values ) 
  {
    if (new_values == null) return;

    // Actualizamos los valores con los nuevos propuestos
    for ( java.util.Enumeration e = new_values.propertyNames(); 
                                                         e.hasMoreElements(); )
    {
      String key = (String) e.nextElement();
      if (key_prefix != null)
      {
        if ( !(key.startsWith(key_prefix)) )
        {
          // USER
          if (_debug)
            System.err.println("TIDConfig.updateConfig(): ignored key -> " + key);
          continue;
        }
      }
      String value = new_values.getProperty(key);

      if (checkValue(key, value))
      {
        config.setProperty(key, value);
      }
      else
      {
        if (_debug)
          System.err.println("TIDConfig.updateConfig(): Value not allowed for the property: " + key);
      }
    }
  }

  /**
   * checkValue
   *
   * java.util.String key
   * java.util.String value
   *
   */
  private boolean checkValue( String key, String value )
  {
    // De no existir la tabla de chequeo para los valores se permite siempre
    // su insercion en la lista de propiedades
    if (check_table == null)
    {
      if (_debug)
        System.err.println("TIDConfig: CHECK TABLE non exist, return true");
      return true;
    }

    TIDPropValue configValue = (TIDPropValue)check_table.get(key);

    if (configValue == null)
    {
      if (_debug)
        System.err.println("TIDConfig: Existing KEY NOT found in Type List");
      return false;
    }

    if (configValue.attr == RO_ATTR)
    {
      if (_debug)
        System.err.println("TIDConfig: READ ONLY attribute, NOT changed.");
      return false;
    }

    if (NUMTYPE.compareTo(configValue.type) == 0)
    {
      int new_value;

      try
      {
        new_value = Integer.parseInt(value);
        if ( (new_value >= configValue.min) && (new_value <= configValue.max) )
        {
          return true;
        }
        else
        {
          if (_debug)
            System.err.println("TIDConfig: invalid new_value.");
          return false;
        }
      }
      catch (NumberFormatException e)
      {
        System.err.println("TIDConfig: parseInt(new_value) Exception.");
        e.printStackTrace();
        return false;
      }
    }
    else if (BOOLTYPE.compareTo(configValue.type) == 0)
    {
      if ( TRUE_VALUE.compareTo(value) == 0 )
      {
        return true;
      }
      else if ( FALSE_VALUE.compareTo(value) == 0 )
      {
        return true;
      }
      // DEBUG
      if (_debug)
        System.err.println("TIDConfig: Incorrect boolean value");
      return true;
    }
    else if (STRTYPE.compareTo(configValue.type) == 0)
    {
      if (value.compareTo("") == 0)
      {
        if (_debug)
          System.err.println("TIDConfig: Invalid string value");
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * setPrefix
   *
   * java.util.String new_prefix
   *
   */
  public void setPrefix( String new_prefix )
  {
    key_prefix = new_prefix;
  }

  /**
   * getPrefix
   *
   */
  public String getPrefix()
  {
    return new String(key_prefix);
  }

  /**
   * get
   *
   * java.util.String key
   *
   */
  public String get( String key ) throws java.lang.IllegalStateException
  {
    if (tid_config == null)
    {
      throw new java.lang.IllegalStateException();
    }
    return tid_config.getProperty(key);
  }

  /**
   * set
   *
   * java.util.String key
   * java.util.String value
   *
   */
  public void set( String key, String value )
                                         throws java.lang.IllegalStateException
  {
    if (tid_config == null)
    {
      throw new java.lang.IllegalStateException();
    }

    if (checkValue(key, value))
    {
      tid_config.setProperty(key, value);
    }
    else
    {
      if (_debug)
        System.err.println("Value not allowed for the property: " + key);
    }
  }

  /**
   * updateConfig 
   *
   * java.util.Properties new_values
   *
   */
  public void updateConfig( java.util.Properties new_values ) 
  {
    if (new_values == null) return;

    // Actualizamos los valores con los nuevos propuestos
    for ( java.util.Enumeration e = new_values.propertyNames(); 
                                                         e.hasMoreElements(); )
    {
      String key = (String) e.nextElement();
      if (key_prefix != null)
      {
        if ( !(key.startsWith(key_prefix)) )
        {
          // USER
          if (_debug)
            System.err.println("TIDConfig: ignored key -> " + key);
          continue;
        }
      }
      String value = new_values.getProperty(key);

      if (checkValue(key, value))
      {
        tid_config.setProperty(key, value);
      }
      else
      {
        if (_debug)
          System.err.println("Value not allowed for the property: " + key);
      }
    }
  }

  /**
   * updateConfig 
   *
   * String[] new_values
   *
   */
  public void updateConfig( java.lang.String[] new_values ) 
  {
    if (new_values == null) return;

    // Actualizamos los valores con los nuevos propuestos
    for (int i = 0; i < new_values.length; i++)
    {
      String key; 
      if (key_prefix != null)
      {
        key = new_values[i].substring(1,new_values[i].length()) ; i++; // FIX
        if ( !(key.startsWith(key_prefix)) )
        {
          if (_debug)
            System.err.println("TIDConfig: ignored key -> " + key);
          continue;
        }
      } else {
          key = new_values[i].substring(1,new_values[i].length()) ; i++; // FIX
      }
      String value = new_values[i];

      if (checkValue(key, value))
      {
        tid_config.setProperty(key, value);
      }
      else
      {
        if (_debug)
          System.err.println("Value not allowed for the property: " + key);
      }
    }
  }

  /**
   * setDebug 
   *
   * boolean value
   *
   */
  public void setDebug(boolean value)
  {
    _debug = value;
  }

  /**
   * store
   *
   *
   *
   */
  public void store() throws java.lang.IllegalStateException,
                             java.io.IOException
  {
    if (tid_config == null)
    {
      throw new java.lang.IllegalStateException();
    }
    java.io.FileOutputStream file = new java.io.FileOutputStream(
                                        TIDNotifUtil.tidConfigFilename(true) );
    tid_config.store(file, null);
  }

  /**
   * load
   *
   *
   *
   */
  public void load() throws java.lang.IllegalStateException,
                            java.io.IOException
  {
    if (tid_config == null)
    {
      throw new java.lang.IllegalStateException();
    }
    java.io.FileInputStream file = new java.io.FileInputStream(
                                       TIDNotifUtil.tidConfigFilename(false) );
    tid_config.load(file);
  }
}
