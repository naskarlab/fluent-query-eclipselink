package com.naskar.fluentquery.eclipselink.customs;

import java.util.Collection;
import java.util.Map;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;

public class SequenceSessionCustomizer implements SessionCustomizer {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void customize(Session session) throws Exception {
		
		Map seqs = session.getLogin().getSequences();
		if(seqs != null && !seqs.isEmpty()) {
			for(Sequence s : (Collection<Sequence>)seqs.values()) {
				s.setPreallocationSize(1);
			}
		}
		
		session.getLogin().getDefaultSequence().setPreallocationSize(1);
		
	}
	
}
