/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 3 $
* Date: $Date: 2006-01-17 17:42:13 +0100 (Tue, 17 Jan 2006) $
* Last modified by: $Author: aarranz $
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

package es.tid.corba.TIDNotif.file;

import java.io.*;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.corba.TIDNotif.util.TIDNotifConfig;

import java.util.ArrayList;

import es.tid.corba.TIDNotif.CommonData;
import es.tid.corba.TIDNotif.NotificationChannelData;
import es.tid.corba.TIDNotif.SupplierAdminData;
import es.tid.corba.TIDNotif.ConsumerAdminData;
import es.tid.corba.TIDNotif.ProxyPushConsumerData;
import es.tid.corba.TIDNotif.ProxyPullConsumerData;
import es.tid.corba.TIDNotif.ProxyPushSupplierData;
import es.tid.corba.TIDNotif.ProxyPullSupplierData;
import es.tid.corba.TIDNotif.FilterData;
import es.tid.corba.TIDNotif.IndexLocatorData;
import es.tid.corba.TIDNotif.MappingFilterData;
import es.tid.corba.TIDNotif.TransformingOperatorData;

public class FILEPersistenceDB 
           implements es.tid.corba.TIDNotif.PersistenceDB, FILEPersistenceDBMsg
{
  // Directorios
  private static final String DIR_CHANNEL               = "channels";
  private static final String DIR_SUPPLIER_ADMIN        = "s_admins";
  private static final String DIR_PROXY_PUXX_CONSUMER   = "p_consumers";
  private static final String DIR_CONSUMER_ADMIN        = "c_admins";
  private static final String DIR_PROXY_PUXX_SUPPLIER   = "p_suppliers";
  private static final String DIR_DISCRIMINATOR         = "discriminators";
  private static final String DIR_INDEXLOCATOR         	= "i_locators";
  private static final String DIR_TRANSFORMING_OPERATOR = "t_operators";
  private static final String DIR_MAPPING_DISCRIMINATOR = "m_discriminators";

  // Fichero UID
  
  private static final String FILE_UID_DATA = "uid.data";
    
  // Miembros
  private String _base_path;
  private FileUID _uid_factory;

  //
  // - A�adir un elemento
  // - Actualizar un elemento
  // - Eliminar un elemento
  // - Eliminar todos los elementos
  // - Obtener una lista de elementos
  //
  public FILEPersistenceDB( String id )
  {
    try
    {
      _base_path = getPath();
      mkDirs(_base_path);
      _uid_factory = new FileUID(_base_path + File.separatorChar + FILE_UID_DATA);
    }
    catch (Exception ex) 
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
    }
  }

  private String getPath() throws IOException
  {
    StringBuffer the_file = new StringBuffer();

    the_file.append(TIDNotifConfig.get(TIDNotifConfig.DATA_PATH_KEY));
    if (!check_path(the_file.toString())) throw new IOException();
    the_file.append(File.separatorChar);
    the_file.append(TIDNotifConfig.get(TIDNotifConfig.DATA_ROOT_KEY));
    if (!check_path(the_file.toString())) throw new IOException();
    the_file.append(File.separatorChar);
    the_file.append(TIDNotifConfig.get(TIDNotifConfig.ADMIN_PORT_KEY));
    if (!check_path(the_file.toString())) throw new IOException();
    the_file.append(File.separatorChar);
    the_file.append(TIDNotifConfig.get(TIDNotifConfig.OBJECTS_PATH_KEY)); 
    if (!check_path(the_file.toString())) throw new IOException();

    return the_file.toString();
  }

  private void mkDirs(String dir) throws IOException
  {
    String path = dir+File.separatorChar;
    if (!check_path(path+DIR_CHANNEL)) throw new IOException();
    if (!check_path(path+DIR_SUPPLIER_ADMIN)) throw new IOException();
    if (!check_path(path+DIR_PROXY_PUXX_CONSUMER)) throw new IOException();
    if (!check_path(path+DIR_CONSUMER_ADMIN)) throw new IOException();
    if (!check_path(path+DIR_PROXY_PUXX_SUPPLIER)) throw new IOException();
    if (!check_path(path+DIR_DISCRIMINATOR)) throw new IOException();
    if (!check_path(path+DIR_INDEXLOCATOR)) throw new IOException();
    if (!check_path(path+DIR_TRANSFORMING_OPERATOR)) throw new IOException();
    if (!check_path(path+DIR_MAPPING_DISCRIMINATOR)) throw new IOException();
  }

  private String makeFilename(String name, int type)
  {
    StringBuffer the_file = new StringBuffer(_base_path);
    the_file.append(File.separatorChar);
    switch (type)
    {
      case OBJ_CHANNEL: 
        return the_file.append(DIR_CHANNEL).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_SUPPLIER_ADMIN: 
        return the_file.append(DIR_SUPPLIER_ADMIN).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_PROXY_PUSH_CONSUMER: 
      case OBJ_PROXY_PULL_CONSUMER: 
        return the_file.append(DIR_PROXY_PUXX_CONSUMER).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_CONSUMER_ADMIN: 
        return the_file.append(DIR_CONSUMER_ADMIN).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_PROXY_PUSH_SUPPLIER: 
      case OBJ_PROXY_PULL_SUPPLIER: 
        return the_file.append(DIR_PROXY_PUXX_SUPPLIER).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_DISCRIMINATOR: 
        return the_file.append(DIR_DISCRIMINATOR).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_INDEXLOCATOR: 
        return the_file.append(DIR_INDEXLOCATOR).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_TRANSFORMING_OPERATOR: 
        return the_file.append(DIR_TRANSFORMING_OPERATOR).append(
                                   File.separatorChar).append(name).toString();

      case OBJ_MAPPING_DISCRIMINATOR: 
        return the_file.append(DIR_MAPPING_DISCRIMINATOR).append(
                                   File.separatorChar).append(name).toString();
    }
    return the_file.append(name).toString();
  }

  private boolean check_path(String pathname)
  {
    File f = new File(pathname);
    if (f.exists()) return true;
    return f.mkdir();
  }

  //
  // Channel Methods
  //
  //
  public void delete(NotificationChannelData data)
  {
    //DELETE[1] = data.name;
    DELETE[1] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id, OBJ_CHANNEL))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                              " *** ERROR delete NotificationChannelData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_CHANNEL));
    }
  }

  public void save(NotificationChannelData data)
  {
    SAVE[1] = null;
    SAVE[1] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_CHANNEL), data);
  } 

  public void update(int what, NotificationChannelData data)
  {
    UPDATE[1] = null;
    UPDATE[1] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_CHANNEL), data);
  } 

  public NotificationChannelData load_c(String name) throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (NotificationChannelData)_load(makeFilename(name, OBJ_CHANNEL));
  }

  public ArrayList load_channels()
  {
    TIDNotifTrace.print(TIDNotifTrace.USER,
               " +>FILEPersistenceDB.load_channels(NotificationChannelData)" );

    ArrayList list = new ArrayList();
    String channel_dir = _base_path+File.separatorChar+DIR_CHANNEL;

    File dir = new File(channel_dir);
    String[] files = dir.list();

    if (files != null)
    {
      for (int i = 0; i<files.length; i++)
      {
        try
        {
          NotificationChannelData channel = (NotificationChannelData)
                                _load(channel_dir+File.separatorChar+files[i]);
          if (channel != null) list.add(channel);
        }
        catch (Exception ex) {}
      }
    }
    return list;
  }


  //
  // SupplierAdmin Methods
  //
  //
  public void delete(SupplierAdminData data)
  {
    DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id, OBJ_SUPPLIER_ADMIN))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                                   " *** ERROR delete SupplierAdminData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_SUPPLIER_ADMIN));
    }
  }

  public void save(SupplierAdminData data)
  {
    SAVE[1] = data.name;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_SUPPLIER_ADMIN), data);
  } 

  public void update(int what, SupplierAdminData data)
  {
    UPDATE[1] = data.name;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_SUPPLIER_ADMIN), data);
  } 

  public SupplierAdminData load_sa(String name) throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (SupplierAdminData) _load(makeFilename(name, OBJ_SUPPLIER_ADMIN));
  }


  //
  // ConsumerAdmin Methods
  //
  //
  public void delete(ConsumerAdminData data)
  {
    DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id, OBJ_CONSUMER_ADMIN))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                                    " *** ERROR delete ConsumerAdminData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_CONSUMER_ADMIN));
    }
  }

  public void save(ConsumerAdminData data)
  {
    SAVE[1] = data.name;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_CONSUMER_ADMIN), data);
  } 

  public void update(int what, ConsumerAdminData data)
  {
    UPDATE[1] = data.name;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_CONSUMER_ADMIN), data);
  } 

  public ConsumerAdminData load_ca(String name) throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (ConsumerAdminData) _load(makeFilename(name, OBJ_CONSUMER_ADMIN));
  }


  //
  // ProxyPushConsumer Methods
  //
  //
  public void delete(ProxyPushConsumerData data)
  {
    DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id,OBJ_PROXY_PUSH_CONSUMER))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                                " *** ERROR delete ProxyPushConsumerData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_PROXY_PUSH_CONSUMER));
    }
  }

  public void save(ProxyPushConsumerData data)
  {
    SAVE[1] = data.name;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_PROXY_PUSH_CONSUMER), data);
  } 

  public void update(int what, ProxyPushConsumerData data)
  {
    UPDATE[1] = data.name;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_PROXY_PUSH_CONSUMER), data);
  } 

  public ProxyPushConsumerData load_ppushc(String name) 
                                                     throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (ProxyPushConsumerData) 
                            _load(makeFilename(name, OBJ_PROXY_PUSH_CONSUMER));
  }


  //
  // ProxyPullConsumer Methods
  //
  //
  public void delete(ProxyPullConsumerData data)
  {
    DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id,OBJ_PROXY_PULL_CONSUMER))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                              " *** ERROR delete ProxyPullConsumerData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_PROXY_PULL_CONSUMER));
    }
  }

  public void save(ProxyPullConsumerData data)
  {
    SAVE[1] = data.name;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_PROXY_PULL_CONSUMER), data);
  } 

  public void update(int what, ProxyPullConsumerData data)
  {
    UPDATE[1] = data.name;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_PROXY_PULL_CONSUMER), data);
  } 

  public ProxyPullConsumerData load_ppullc(String name) 
                                                     throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (ProxyPullConsumerData) 
                            _load(makeFilename(name, OBJ_PROXY_PULL_CONSUMER));
  }


  //
  // ProxyPushSupplier Methods
  //
  //
  public void delete(ProxyPushSupplierData data)
  {
    DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id,OBJ_PROXY_PUSH_SUPPLIER))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                                " *** ERROR delete ProxyPushSupplierData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_PROXY_PUSH_SUPPLIER));
    }
  }

  public void save(ProxyPushSupplierData data)
  {
    SAVE[1] = data.name;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_PROXY_PUSH_SUPPLIER), data);
  } 

  public void update(int what, ProxyPushSupplierData data)
  {
    UPDATE[1] = data.name;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_PROXY_PUSH_SUPPLIER), data);
  } 

  public ProxyPushSupplierData load_ppushs(String name) 
                                                     throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (ProxyPushSupplierData) 
                            _load(makeFilename(name, OBJ_PROXY_PUSH_SUPPLIER));
  }


  //
  // ProxyPullSupplier Methods
  //
  //
  public void delete(ProxyPullSupplierData data)
  {
    DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id,OBJ_PROXY_PULL_SUPPLIER))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                                " *** ERROR delete ProxyPullSupplierData ***");
TIDNotifTrace.print(TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_PROXY_PULL_SUPPLIER));
    }
  }

  public void save(ProxyPullSupplierData data)
  {
    SAVE[1] = data.name;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_PROXY_PULL_SUPPLIER), data);
  } 

  public void update(int what, ProxyPullSupplierData data)
  {
    UPDATE[1] = data.name;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_PROXY_PULL_SUPPLIER), data);
  } 

  public ProxyPullSupplierData load_ppulls(String name) 
                                                     throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (ProxyPullSupplierData) 
                            _load(makeFilename(name, OBJ_PROXY_PULL_SUPPLIER));
  }


  //
  // Discriminator Methods
  //
  //
  public void delete(FilterData data)
  {
    //DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id, OBJ_DISCRIMINATOR))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                              " *** ERROR delete DiscriminatorData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_DISCRIMINATOR));
    }
  }

  public void save(FilterData data)
  {
    SAVE[1] = null;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_DISCRIMINATOR), data);
  } 

  public void update(int what, FilterData data)
  {
    UPDATE[1] = null;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_DISCRIMINATOR), data);
  } 

  public FilterData load_d(String name) throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (FilterData) _load(makeFilename(name, OBJ_DISCRIMINATOR));
  }


  //
  // IndexLocator Methods
  //
  //
  public void delete(IndexLocatorData data)
  {
    //DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id, OBJ_INDEXLOCATOR))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                                     " *** ERROR delete IndexLocatorData ***");
TIDNotifTrace.print( TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_INDEXLOCATOR));
    }
  }

  public void save(IndexLocatorData data)
  {
    SAVE[1] = null;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_INDEXLOCATOR), data);
  } 

  public void update(int what, IndexLocatorData data)
  {
    UPDATE[1] = null;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_INDEXLOCATOR), data);
  } 

  public IndexLocatorData load_l(String name) throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (IndexLocatorData) _load(makeFilename(name, OBJ_INDEXLOCATOR));
  }


  //
  // MappingDiscriminator Methods
  //
  //
  public void delete(MappingFilterData data)
  {
    //DELETE[1] = data.name;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id,OBJ_MAPPING_DISCRIMINATOR))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                             " *** ERROR delete MappingDiscriminatorData ***");
TIDNotifTrace.print(TIDNotifTrace.USER, " ### FILE: "+makeFilename(data.id,OBJ_MAPPING_DISCRIMINATOR));
    }
  }

  public void save(MappingFilterData data)
  {
    SAVE[1] = null;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_MAPPING_DISCRIMINATOR), data);
  } 

  public void update(int what, MappingFilterData data)
  {
    UPDATE[1] = null;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_MAPPING_DISCRIMINATOR), data);
  } 

  public MappingFilterData load_md(String name) 
                                                     throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (MappingFilterData) 
                          _load(makeFilename(name, OBJ_MAPPING_DISCRIMINATOR));
  }


  //
  // TransformingOperatorData Methods
  //
  //
  public void delete(TransformingOperatorData data)
  {
    DELETE[1] = null;
    DELETE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, DELETE);

    if (!((new File(makeFilename(data.id,OBJ_TRANSFORMING_OPERATOR))).delete()))
    {
      TIDNotifTrace.print(TIDNotifTrace.USER, 
                             " *** ERROR delete TransformingOperatorData ***");
TIDNotifTrace.print(TIDNotifTrace.USER, "FILE: "+makeFilename(data.id,OBJ_TRANSFORMING_OPERATOR));
    }
  }

  public void save(TransformingOperatorData data)
  {
    SAVE[1] = null;
    SAVE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, SAVE);
    _save(makeFilename(data.id, OBJ_TRANSFORMING_OPERATOR), data);
  } 

  public void update(int what, TransformingOperatorData data)
  {
    UPDATE[1] = null;
    UPDATE[3] = data.id;
    TIDNotifTrace.print(TIDNotifTrace.USER, UPDATE);
    _save(makeFilename(data.id, OBJ_TRANSFORMING_OPERATOR), data);
  } 

  public TransformingOperatorData load_to(String name) 
                                                     throws java.lang.Exception
  {
    LOAD[1] = name;
    TIDNotifTrace.print(TIDNotifTrace.USER, LOAD);
    return (TransformingOperatorData) 
                          _load(makeFilename(name, OBJ_TRANSFORMING_OPERATOR));
  }


  //
  // Internal Methods
  //
  //
  private CommonData _load(String name) throws java.lang.Exception
  {
    //String[] LOAD = { " - FILEPersistenceDB.load(", null, ")" };
    //LOAD[1] = name;
    //TIDNotifTrace.print( TIDNotifTrace.USER, LOAD );

    CommonData data = null;
    try
    {
      ObjectInputStream si = new ObjectInputStream(new FileInputStream(name));
      data = (CommonData) si.readObject();
      si.close();
    }
    catch (java.io.InvalidClassException ex)
    {
      TIDNotifTrace.print(TIDNotifTrace.ERROR, PERSISTENCE_ERROR);
      System.err.println();
      System.err.println(PERSISTENCE_ERROR);
      System.exit(-1);
    }
    catch (Exception ex)
    {
      throw ex;
    }
    return data;
  }

  private void _save(String name, CommonData data)
  {
    //String[] SAVE = { " - FILEPersistenceDB._save(", null, ")" };
    //SAVE[1] = name;
    //TIDNotifTrace.print( TIDNotifTrace.USER, SAVE );

    try
    {
      ObjectOutputStream so = 
                          new ObjectOutputStream( new FileOutputStream(name) );
      so.writeObject(data);
      so.flush();
      so.close();
    }
    catch (Exception ex)
    {
      TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
    }
  }

	/** 
	 * UID generator
	 */
	public String getUID() throws IOException
	{
	   return _uid_factory.getUID();
	}
}
