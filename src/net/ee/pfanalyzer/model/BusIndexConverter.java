package net.ee.pfanalyzer.model;

public class BusIndexConverter {

	public static int getRealBusIndex(int busIndex) {
		if(busIndex > 10000 && busIndex < 20000)
			return (int) Math.floor((busIndex - 10000) / 10) - 1;
		if(busIndex > 20000 && busIndex < 30000)
			return (int) Math.floor((busIndex - 20000) / 10);
		return busIndex;
	}
}
