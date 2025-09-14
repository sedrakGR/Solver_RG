package org.ppit.core.learn;

import java.util.ArrayList;
import org.ppit.core.concept.Concept;

interface GeneralizationBase
{
	ArrayList<ContainerEntry> m_lContainer = new ArrayList<ContainerEntry>();
	ArrayList<ContainerEntry> m_rContainer = new ArrayList<ContainerEntry>();
	
	ArrayList<BaseEntry> m_bContainer = new ArrayList<BaseEntry>();
	
	ArrayList<ArrayList<PairedEntry>> m_peList = new ArrayList<ArrayList<PairedEntry>>();
	
	public void initialize(Concept left, Concept right);
	public void generalize();
}
