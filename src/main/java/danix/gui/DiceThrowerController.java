/**
 *
 */
package danix.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Marcos Pires
 *
 */
public class DiceThrowerController {
	DiceThrowerModel model;
	DiceThrowerView view;
	
	public DiceThrowerController(DiceThrowerModel model, DiceThrowerView view) {
		this.model = model;
		this.view = view;
		
		view.addDiceThrowListener(new DiceThrowListener());
	}
	
	class DiceThrowListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int num = view.getDiceThrowInput();
			model.throwDice(num);		
		}
		
	}
}
