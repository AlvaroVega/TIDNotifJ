/*
* MORFEO Project
* http://www.morfeo-project.org
*
* Component: TIDNotifJ
* Programming Language: Java
*
* File: $Source$
* Version: $Revision: 107 $
* Date: $Date: 2008-11-05 11:04:22 +0100 (Wed, 05 Nov 2008) $
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

package es.tid.corba.TIDNotif;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;

import org.omg.CORBA.Any;
import org.omg.CORBA.INTERNAL;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CosNotifyFilter.Filter;
import org.omg.CosNotifyFilter.FilterFactoryOperations;
import org.omg.CosNotifyFilter.InvalidGrammar;
import org.omg.CosNotifyFilter.MappingFilter;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.NoServant;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import es.tid.corba.TIDNotif.util.TIDNotifTrace;
import es.tid.util.parser.TIDParser;

/**
 * @author jprojas
 *
 */
public class FilterFactoryImpl  
	implements FilterFactoryOperations, FilterFactoryMsg, Serializable {

	
	
	
	public FilterFactoryImpl(){					
	}
		
	
	public Filter get_filter( int filterId ){
		
		FilterData filterData;
		try {
			filterData = (( FilterImpl )globalFilterPOA().get_servant()).getData( String.valueOf( filterId ));
			
			return NotifReference.discriminatorReference(
				filterData.poa,
				filterData.id
			);
		} catch ( Exception e ){
			throw new OBJECT_NOT_EXIST();
		}
	}
	
		
	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterFactoryOperations#create_filter(java.lang.String)
	 */
	public Filter create_filter(String constraint_grammar)
			throws InvalidGrammar {
		
		CREATE_FILTER[ 1 ] = constraint_grammar;
		TIDNotifTrace.print(TIDNotifTrace.USER, CREATE_FILTER);
		
		if(!constraint_grammar.equals(TIDParser._CONSTRAINT_GRAMMAR))
		{
		    throw new InvalidGrammar();
		}
		    

		POA poa = globalFilterPOA();
		String id;
        try {
            if (PersistenceManager.isActive())
                id = PersistenceManager.getDB().getUID();
            else 
                id = "0";
        }
        catch (Exception e1) {
            throw new INTERNAL(e1.toString());
        }
        FilterData filterData  = new FilterData(id, poa );

		try {
			((FilterImpl) (poa.get_servant())).register( filterData );
		} catch (Exception e) {
		}

		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save( filterData );
			PersistenceManager.getDB().update(
				PersistenceDB.ATTR_DISCRIMINATOR, filterData
			);
		}

		return NotifReference.discriminatorReference(
			filterData.poa,
			filterData.id
		);
	}

	/* (non-Javadoc)
	 * @see org.omg.CosNotifyFilter.FilterFactoryOperations#create_mapping_filter(java.lang.String, org.omg.CORBA.Any)
	 */
	public MappingFilter create_mapping_filter(String constraint_grammar,
			Any default_value) throws InvalidGrammar {
		CREATE_MAPPING_FILTER[ 1 ] = constraint_grammar;
		TIDNotifTrace.print(TIDNotifTrace.USER, CREATE_MAPPING_FILTER);

		POA poa = globalMappingDiscriminatorPOA();
		String id;
        try {
            if (PersistenceManager.isActive())
                id = PersistenceManager.getDB().getUID();
            else
                id = "0";
        }
        catch (Exception e1) {
            throw new INTERNAL(e1.toString());
        }
        MappingFilterData filterData  = new MappingFilterData(id, default_value );

		try {
			((MappingFilterImpl) (poa.get_servant())).register( filterData );
		} catch (Exception e) {
		}
		
		if (PersistenceManager.isActive()) {
			PersistenceManager.getDB().save( filterData );
			PersistenceManager.getDB().update(
				PersistenceDB.ATTR_DISCRIMINATOR, filterData
			);
		}

		return NotifReference.mappingDiscriminatorReference(
			filterData.poa,
			filterData.id
		);
	}

	
	private org.omg.PortableServer.POA globalFilterPOA() {
		org.omg.PortableServer.POA the_poa; 
		the_poa = ThePOAFactory.getGlobalPOA(GLOBAL_FILTER_POA_ID);
		
		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_FILTER_POA_ID,
					ThePOAFactory.GLOBAL_FILTER_POA, null );
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new FilterImpl());
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}

	private org.omg.PortableServer.POA globalMappingDiscriminatorPOA() {
		org.omg.PortableServer.POA the_poa; 
		the_poa = ThePOAFactory.getGlobalPOA(GLOBAL_MAPPING_FILTER_POA_ID);
		
		if (the_poa == null) {
			the_poa = ThePOAFactory.createGlobalPOA(
					GLOBAL_MAPPING_FILTER_POA_ID,
					ThePOAFactory.GLOBAL_MAPPING_FILTER_POA, null );
			try {
				the_poa.get_servant();
			} catch (org.omg.PortableServer.POAPackage.NoServant ex) {
				try {
					the_poa.set_servant(new MappingFilterImpl());
				} catch (Exception e) {
					TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
				}
			} catch (org.omg.PortableServer.POAPackage.WrongPolicy ex) {
				TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, ex);
			}
		}
		return the_poa;
	}
	
	
	private void writeObject(ObjectOutputStream out) 
		throws IOException
	{
	    FilterImpl filter = null;
        try {
            filter = (FilterImpl) (globalFilterPOA().get_servant());
        }
        catch (NoServant e) {
        }catch (WrongPolicy e) {}
        
        Iterator it = filter._discriminators_table.values().iterator();
	    
	    out.writeInt(filter._discriminators_table.size());
	    
	    while(it.hasNext()) {
	        out.writeObject(((FilterData)it.next()).id);
	    }
	    
	    MappingFilterImpl mappingFilter = null;
	    
	    try {
            mappingFilter = (MappingFilterImpl) 
            	(globalMappingDiscriminatorPOA().get_servant());
        }
        catch (NoServant e) {
        }catch (WrongPolicy e) {}
        
        it = mappingFilter._mapping_discriminators_table.values().iterator();
	    
	    out.writeInt(mappingFilter._mapping_discriminators_table.size());
	    
	    while(it.hasNext()) {
	        out.writeObject(((MappingFilterData)it.next()).id);
	    }
	    
	}
	
	private void readObject(ObjectInputStream input) 
		throws IOException, ClassNotFoundException
	{
	    // Filters
	    FilterImpl filter = null;
        try {
            filter = (FilterImpl) (globalFilterPOA().get_servant());
        }
        catch (NoServant e) {
        }catch (WrongPolicy e) {}
        
	    int size = input.readInt();
	    
	    String id = null;
	    FilterData data = null;
	    
	    for(int i = 0; i < size; i++) {
	        id = (String) input.readObject();
	        try {
                data = PersistenceManager.getDB().load_d(id);
                filter.register(data);                
            }
            catch (Exception e) 
            {
                TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, e);
            }	        
	    }
	    
	    // MappingFilters
	    
	    
	    
	    MappingFilterImpl mappingFilter = null;
	    
	    try {
            mappingFilter = (MappingFilterImpl) 
            	(globalMappingDiscriminatorPOA().get_servant());
        }
        catch (NoServant e) {
        }catch (WrongPolicy e) {}
        
        MappingFilterData mappingData = null;
        
        size = input.readInt();
        
        for(int i = 0; i < size; i++) {
	        id = (String) input.readObject();
	        try {
	            mappingData = PersistenceManager.getDB().load_md(id);
	            mappingFilter.register(mappingData);                
            }
            catch (Exception e) 
            {
                TIDNotifTrace.printStackTrace(TIDNotifTrace.ERROR, e);
            }	        
	    }
	}
}

