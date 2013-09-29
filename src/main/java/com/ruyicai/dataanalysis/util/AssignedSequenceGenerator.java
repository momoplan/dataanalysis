package com.ruyicai.dataanalysis.util;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

public class AssignedSequenceGenerator extends TableGenerator implements
		PersistentIdentifierGenerator, Configurable {

	private Integer INCREMENT_PARAM = 0;
	
	@Override
	public void configure(Type type, Properties params, Dialect dialect)
			throws MappingException {
		super.configure(IntegerType.INSTANCE, params, dialect);
		if (params.containsKey(TableGenerator.INCREMENT_PARAM))
			INCREMENT_PARAM = new Integer(params.getProperty(TableGenerator.INCREMENT_PARAM));
	}

	@Override
	public Serializable generate(SessionImplementor session, Object obj)
			throws HibernateException {
		int seq = (Integer) super.generate(session, obj)+1+INCREMENT_PARAM;
		return seq;
	}
	
}
