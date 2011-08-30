package net.ee.pfanalyzer.model;

public interface IDerivedElement<TYPE extends AbstractNetworkElement> {

	TYPE getRealElement();
}
