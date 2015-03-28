/**
 *
 */
package danix.gui;

import java.util.*;

/**
 * @author Marcos Pires
 *
 */
public class DiceThrowerModel extends Observable implements Iterable<Integer> {
	int dicenum = 0;
	int result = 0;
	List<Integer> dice = new ArrayList<Integer>();
	Random rng = new Random();
	
	public void throwDice(int num) {
		dicenum = num;
		result = 0;
		dice = new ArrayList<Integer>(num);
		for (int i=0; i<num; ++i) {
			int random = rng.nextInt(6) + 1;
			result += random >= 5 ? 1 : 0;
			dice.add(random);
		}
		
		setChanged();
		notifyObservers();
		clearChanged();
	}
	
	public Iterator<Integer> iterator() {
		return dice.iterator();
	}
	
	public int getResult() {
		return result;
	}
	
	public int getNumberOfDice() {
		return dicenum;
	}
}
