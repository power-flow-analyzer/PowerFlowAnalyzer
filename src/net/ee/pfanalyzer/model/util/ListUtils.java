package net.ee.pfanalyzer.model.util;

import java.util.List;

public class ListUtils {

	public static <T> int getIndexOf(List<? super T> list, T object) {
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i) == object)
				return i;
		}
		return -1;
	}
}
