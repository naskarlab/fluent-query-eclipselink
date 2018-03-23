package com.naskar.fluentquery.eclipselink.customs;

import java.util.Collection;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;

public class SequenceSessionCustomizer implements SessionCustomizer {

	@SuppressWarnings("unchecked")
	@Override
	public void customize(Session session) throws Exception {
		
		for(Sequence s : (Collection<Sequence>)session.getLogin().getSequences().values()) {
			s.setPreallocationSize(1);
		}
		
		session.getLogin().getDefaultSequence().setPreallocationSize(1);
		
	}
	
}
