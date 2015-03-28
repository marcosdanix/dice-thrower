package danix.gui;

public class DiceThrower {

	public static void main(String[] args) {
		//the model
		DiceThrowerModel model = new DiceThrowerModel();
		
		//the view
		DiceThrowerView view = new DiceThrowerView(model);
		
		//and the ghost... i mean controller
		new DiceThrowerController(model, view);
		
		view.setVisible();
	}

}
