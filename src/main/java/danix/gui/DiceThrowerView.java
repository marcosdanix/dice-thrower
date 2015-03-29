/**
 *
 */
package danix.gui;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.awt.EventQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JFormattedTextField;
import javax.swing.text.NumberFormatter;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import java.awt.Component;

import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Marcos Pires
 *
 */
public class DiceThrowerView {

	protected static final int SPRITE_SIZE = 50;
	
	private boolean done;
	private BufferedImage[] diceSprites;
	private DiceThrowerModel model;
	private JFrame frame;
	private DiceDisplayView dicePanel;
	private JButton btnDice;
	private JFormattedTextField textField;

	/**
	 * Create the application.
	 */
	public DiceThrowerView(DiceThrowerModel model) {
		this.model = model;
		done = false;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					loadResources();
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * loads the dice sprites
	 */
	private synchronized void loadResources() throws IOException {
		diceSprites = new BufferedImage[6];
		File sprite = new File("sprites/spr_dice.png");
		System.out.println("Loading sprite: " + sprite.getAbsolutePath());
		BufferedImage diceSpriteFile = ImageIO.read(sprite);
		for (int i=0; i<6; ++i) {
			diceSprites[i] = diceSpriteFile.getSubimage(i*SPRITE_SIZE, 0, SPRITE_SIZE, SPRITE_SIZE);
		}
		System.out.println("Done");
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private synchronized void initialize() {		
		frame = new JFrame();
		frame.getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				dicePanel.readjustLayout();
			}
		});
		//frame.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JPanel panel = new JPanel();
		textField = new JFormattedTextField(new NumberFormatter());
		
		btnDice = new JButton("Throw Dice");		
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		dicePanel = new DiceDisplayView(model, diceSprites);
		dicePanel.setBackground(Color.LIGHT_GRAY);
		scrollPane.setViewportView(dicePanel);
		dicePanel.setLayout(new GridLayout(0, 1, 5, 20));
		
		JLabel lblNumberOfDice = new JLabel("Number of dice:");
		
		DiceResultView diceResult = new DiceResultView(model);
		//diceResult.setText("Result:");

		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(lblNumberOfDice)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(diceResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
					.addComponent(btnDice))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNumberOfDice)
						.addComponent(btnDice)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(diceResult, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		panel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textField, btnDice}));
		frame.getContentPane().setLayout(groupLayout);
		frame.setBounds(100, 100, 454, 303);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{frame.getContentPane(), panel, lblNumberOfDice, textField, btnDice}));
		
		done = true;
		this.notifyAll();
	}
	
	//race conditions!
	public synchronized void setVisible() {
		waitIfNotDone();
		frame.setVisible(true);		
	}
	
	//race conditions!
	public synchronized void addDiceThrowListener(ActionListener diceThrowListener) {
		waitIfNotDone();
		btnDice.addActionListener(diceThrowListener);
	}
	
	public int getDiceThrowInput() {
		return ((Long) textField.getValue()).intValue();
	}
	
	private void waitIfNotDone() {
		while (!done) {
			try {
				this.wait();
			} catch (InterruptedException e) {}
		}		
	}

	@SuppressWarnings("serial")
	class DiceDisplayView extends JPanel implements Observer {
		BufferedImage sprites[];
		int dicenum;
		
		public DiceDisplayView(DiceThrowerModel model, BufferedImage sprites[]) {
			super();
			this.sprites = sprites;
			this.dicenum = 0;
			model.addObserver(this);
		}
		
		public void readjustLayout() {
			int hsize = this.getWidth();
			GridLayout manager = (GridLayout) this.getLayout();
			int spritesize = (int) (1.5 * DiceThrowerView.SPRITE_SIZE);
			int colnum = Math.min(hsize / spritesize, dicenum);
			manager.setColumns(Math.max(colnum, 1)); //It has to be at least one column
			this.revalidate();
			this.repaint();
		}

		@Override
		public void update(Observable arg0, Object arg1) {
			DiceThrowerModel model = (DiceThrowerModel) arg0;
			this.dicenum = model.getNumberOfDice();
			
			//draw the panel 'ere
			this.removeAll();
			for (int die : model) {
				this.add(newDiceComponent(die));
				this.revalidate();
			}
			
			this.readjustLayout();
		}

		private Component newDiceComponent(int die) {
			return new JLabel(new ImageIcon(sprites[die-1]));
		}
	}

	@SuppressWarnings("serial")
	class DiceResultView extends JLabel implements Observer {

		public DiceResultView(DiceThrowerModel model) {
			super("Result:");
			model.addObserver(this);
		}
		
		@Override
		public void update(Observable arg0, Object arg1) {
			DiceThrowerModel model = (DiceThrowerModel) arg0;
			this.setText("Result: " + model.getResult());
		}

	}
}



