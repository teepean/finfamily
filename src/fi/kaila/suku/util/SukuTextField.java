package fi.kaila.suku.util;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

/**
 * @author Kalle
 * 
 *         This will become a textfield with some intellisens feature
 * 
 */
public class SukuTextField extends JTextField implements FocusListener,
		KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Enum for the field types that will be recognized
	 * 
	 */
	public enum Field {
		/**
		 * Field type givenname
		 */
		Fld_Givenname,
		/**
		 * Field type Patronyme
		 */
		Fld_Patronyme,

		/**
		 * Field type Surname
		 */
		Fld_Surname,
		/**
		 * Field type place
		 */
		Fld_Place,
		/**
		 * Field type country
		 */
		Fld_Country,

		/**
		 * Field type = type
		 */
		Fld_Type,

		/**
		 * Field type description
		 */
		Fld_Description,
		/**
		 * No field
		 */
		Fld_Null
	};

	SukuSenser senser = null;
	private String tag = null;
	private Field type = Field.Fld_Null;

	/**
	 * @param tag
	 * @param type
	 */
	public SukuTextField(String tag, Field type) {
		addFocusListener(this);
		addKeyListener(this);
		this.tag = tag;
		this.type = type;
		senser = SukuSenser.getInstance();

	}

	boolean hasFocus = false;

	@Override
	public void focusGained(FocusEvent arg0) {
		hasFocus = true;
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		hasFocus = false;
		senser.hide();
	}

	@Override
	public void keyPressed(KeyEvent k) {
		// System.out.println("p:" + k.toString());
		int cmd = k.getKeyCode();
		// 40 = down
		// 38 = up
		if (cmd == 40 || cmd == 38 || cmd == 10) {
			senser.selectList(cmd);
		}

	}

	@Override
	public void keyReleased(KeyEvent k) {
		// System.out.println("r:" + k.toString());
		// char c = k.getKeyChar();
		int cmd = k.getKeyCode();
		if (cmd == 40 || cmd == 38) {
			return;
		}
		// if (c == '\n') {
		// senser.getSens(this);
		// return;
		// }
		senser.showSens(this, tag, type);
		if (cmd == 10) {
			senser.hide();
		}
	}

	@Override
	public void keyTyped(KeyEvent k) {
		// System.out.println("t:" + k.toString());
		// char c = k.getKeyChar();
		//
		// if (c == '\n') {
		// senser.getSens(this);
		// return;
		// }

		// int i = k.getKeyCode();
		// int m1 = k.getModifiers();
		// int m2 = k.getModifiersEx();
		//
		// int ii = c;
		// // if (k.getKeyChar() == '\n') {
		// System.out.println("K:" + ii + "/" + i + "/" + m1 + "/" + m2);
		// // System.out.println("k:" + k.toString());
		// // }
	}

}