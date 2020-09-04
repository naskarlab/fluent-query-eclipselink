package com.naskar.fluentquery.eclipselink.conventions;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import com.naskar.fluentquery.impl.Convention;

public class EclipseLinkConvention implements Convention {
	
	private Map<String, String> clazzes;
	private Map<String, Map<String, String>> methods;
	
	public EclipseLinkConvention() {
		this.clazzes = new HashMap<String, String>();
		this.methods = new HashMap<String, Map<String, String>>();
	}
	
	@Deprecated
	/* Use addAll(EntityManager em) */
	public void addAll(EntityManagerFactory factory) {
		EntityManager em = null;
		try {
			
			em = factory.createEntityManager();
			addAll(getSession(em));
			
		} finally {
			if(em != null) {
				em.close();
			}
		}
	}
	
	public void addAll(EntityManager em) {
		addAll(getSession(em));
	}

	private Session getSession(EntityManager em) {
		Session session = em.unwrap(Session.class);
		if(session == null) {
			throw new RuntimeException("No eclipselink session found.");
		}
		return session;
	}

	@SuppressWarnings("rawtypes")
	public void addAll(Session session) {
		boolean toUpperCase = System.getProperty("com.naskar.fluentquery.eclipselink.uppercase", "false").equalsIgnoreCase("true");
		
		Map<Class, ClassDescriptor> descriptors = session.getDescriptors();
		
		for(Entry<Class, ClassDescriptor> e : descriptors.entrySet()) {
			
			Class clazz = e.getKey();
			ClassDescriptor cd = e.getValue();
			
			String tableName = (String)cd.getTableNames().get(0);
			if(toUpperCase) {
				tableName = tableName.toUpperCase();
			}
			clazzes.put(clazz.getName(), tableName);
			
			Map<String, String> fields = new HashMap<String, String>();
			methods.put(clazz.getName(), fields);
			
			for(DatabaseMapping dm : cd.getMappings()) {
				String attributeName = dm.getAttributeName();
				String method = "get" + 
					attributeName.substring(0, 1).toUpperCase() + 
					attributeName.substring(1);
				DatabaseField field = dm.getField();
				if(field != null) {
					String columnName = field.getName();
					if(toUpperCase) {
						columnName = columnName.toUpperCase();
					}
					fields.put(method, columnName);
					
				} else if(dm.getFields() != null) {
					List<DatabaseField> fieldsMappings = dm.getFields();
					if(fieldsMappings.size() > 1) {
						// TODO: chave composta
						throw new UnsupportedOperationException();
						
					} else if(!fieldsMappings.isEmpty()) {
						String columnName = fieldsMappings.get(0).getName();
						if(toUpperCase) {
							columnName = columnName.toUpperCase();
						}
						fields.put(method, columnName);
						
					}
				}
				
				
			}
			
		}
	}
	
	@Override
	public <T> String getNameFromClass(Class<T> clazz) {
		return clazzes.get(clazz.getName());
	}
	
	@Override
	public String getNameFromMethod(List<Method> methods) {
		if(methods.size() > 1) {
			throw new UnsupportedOperationException(methods.toString());
		}
		
		String name = null;
		
		if(!methods.isEmpty()) {
			name = getNameFromMethod(methods.get(0));
		}
		
		return name;
	}
	
	@Override
	public String getNameFromMethod(Method m) {
		String name = null;
		
		Map<String, String> fields = methods.get(m.getDeclaringClass().getName());
		if(fields != null) {
			name = fields.get(m.getName());
		}
		
		return name;
	}

}
