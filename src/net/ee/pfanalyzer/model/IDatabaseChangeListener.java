package net.ee.pfanalyzer.model;

public interface IDatabaseChangeListener {
	
	void elementChanged(DatabaseChangeEvent event);

	void parameterChanged(DatabaseChangeEvent event);
}
