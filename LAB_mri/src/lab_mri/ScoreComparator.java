/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_mri;

/**
 *
 * @author luigi
 */
import java.util.Comparator;
import java.util.Map.Entry;

public class ScoreComparator<T> implements Comparator<Entry<T, Float>> {

	@Override
	public int compare(Entry<T, Float> a, Entry<T, Float> b) {
		return a.getValue().compareTo(b.getValue());
	}
}