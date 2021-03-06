/**
 * eAdventure (formerly <e-Adventure> and <e-Game>) is a research project of the
 *    <e-UCM> research group.
 *
 *    Copyright 2005-2010 <e-UCM> research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    <e-UCM> is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          C Profesor Jose Garcia Santesmases sn,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure, version 2.0
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.eucm.ead.tools;

import es.eucm.ead.model.elements.extra.EAdList;
import es.eucm.ead.model.elements.operations.Operation;

/************************************************************************
 * <i>Mathematic expression evaluator.</i> Supports the following functions: +,
 * -, *, /, ^, %, cos, sin, tan, acos, asin, atan, sqrt, sqr, log, min, max,
 * ceil, floor, abs, neg, rndr, deg.<br>
 * When the getValue() is called, a float object is returned. If it returns
 * null, an error occured.
 * <p>
 * 
 * <pre>
 * Sample:
 * MathEvaluator m = new MathEvaluator("-5-6/(-2) + sqr(15+x)");
 * m.addVariable("x", 15.1d);
 * System.out.println( m.getValue() );
 * </pre>
 * 
 * @version 1.1
 * @author The-Son LAI, <a href="mailto:Lts@writeme.com">Lts@writeme.com</a>
 * @date April 2001
 ************************************************************************/
public class MathEvaluator {
	protected static Operator[] operators = null;
	private Node node = null;
	private String expression = null;
	private OperationResolver operationResolver;
	private EAdList<Operation> operationsList;

	private Pool<Node> pool = new Pool<Node>() {

		@Override
		protected Node newObject() {
			return new Node();
		}

	};

	/***
	 * creates an empty MathEvaluator. You need to use setExpression(String s)
	 * to assign a math expression string to it.
	 */
	public MathEvaluator() {
		init();
		node = new Node();
	}

	/***
	 * creates a MathEvaluator and assign the math expression string.
	 */
	public MathEvaluator(String s, OperationResolver variables,
			EAdList<Operation> varList) {
		init();
		setExpression(s, variables, varList);
	}

	private void init() {
		if (operators == null)
			initializeOperators();
	}

	/***
	 * sets the expression
	 * 
	 */
	public void setExpression(String s, OperationResolver operationResolver,
			EAdList<Operation> varList) {
		expression = s;
		this.operationResolver = operationResolver;
		this.operationsList = varList;
	}

	/***
	 * resets the evaluator
	 */
	public void reset() {
		node = null;
		expression = null;
	}

	/***
	 * trace the binary tree for debug
	 */
	public void trace() {
		try {
			node = pool.obtain();
			node.init(null, expression, 0);
			node.trace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * evaluates and returns the value of the expression
	 */
	public Float getValue() {
		if (expression == null)
			return Float.valueOf(0);

		try {
			node.free();
			node = pool.obtain();
			node.init(null, expression, 0);
			return evaluate(node);
		} catch (Exception e) {
			e.printStackTrace();
			return Float.valueOf(0);
		}
	}

	private static float evaluate(Node n) {
		if (n.hasOperator() && n.hasChild()) {
			if (n.getOperator().getType() == 1)
				n.setValue(evaluateExpression(n.getOperator(), evaluate(n
						.getLeft()), 0));
			else if (n.getOperator().getType() == 2)
				n.setValue(evaluateExpression(n.getOperator(), evaluate(n
						.getLeft()), evaluate(n.getRight())));
		}
		return n.getValue();
	}

	private static float evaluateExpression(Operator o, float f1, float f2) {
		String op = o.getOperator();
		float res = 0;

		if ("+".equals(op))
			res = f1 + f2;
		else if ("-".equals(op))
			res = f1 - f2;
		else if ("*".equals(op))
			res = f1 * f2;
		else if ("/".equals(op))
			res = f1 / f2;
		else if ("^".equals(op))
			res = (float) Math.pow(f1, f2);
		else if ("%".equals(op))
			res = f1 % f2;
		else if ("&".equals(op))
			res = f1 + f2;
		else if ("|".equals(op))
			res = f1 + f2;
		else if ("cos".equals(op))
			res = (float) Math.cos(f1);
		else if ("sin".equals(op))
			res = (float) Math.sin(f1);
		else if ("tan".equals(op))
			res = (float) Math.tan(f1);
		else if ("acos".equals(op))
			res = (float) Math.acos(f1);
		else if ("asin".equals(op))
			res = (float) Math.asin(f1);
		else if ("atan".equals(op))
			res = (float) Math.atan(f1);
		else if ("sqr".equals(op))
			res = f1 * f1;
		else if ("sqrt".equals(op))
			res = (float) Math.sqrt(f1);
		else if ("log".equals(op))
			res = (float) Math.log(f1);
		else if ("min".equals(op))
			res = Math.min(f1, f2);
		else if ("max".equals(op))
			res = Math.max(f1, f2);
		else if ("exp".equals(op))
			res = (float) Math.exp(f1);
		else if ("floor".equals(op))
			res = (float) Math.floor(f1);
		else if ("ceil".equals(op))
			res = (float) Math.ceil(f1);
		else if ("abs".equals(op))
			res = Math.abs(f1);
		else if ("neg".equals(op))
			res = -f1;
		else if ("rnd".equals(op))
			res = (float) (Math.random() * f1);
		else if ("deg".equals(op)) {
			res = (float) Math.toDegrees(f1);
		}

		return res;
	}

	private void initializeOperators() {
		operators = new Operator[26];
		operators[0] = new Operator("+", 2, 0);
		operators[1] = new Operator("-", 2, 0);
		operators[2] = new Operator("*", 2, 10);
		operators[3] = new Operator("/", 2, 10);
		operators[4] = new Operator("^", 2, 10);
		operators[5] = new Operator("%", 2, 10);
		operators[6] = new Operator("&", 2, 0);
		operators[7] = new Operator("|", 2, 0);
		operators[8] = new Operator("cos", 1, 20);
		operators[9] = new Operator("sin", 1, 20);
		operators[10] = new Operator("tan", 1, 20);
		operators[11] = new Operator("acos", 1, 20);
		operators[12] = new Operator("asin", 1, 20);
		operators[13] = new Operator("atan", 1, 20);
		operators[14] = new Operator("sqrt", 1, 20);
		operators[15] = new Operator("sqr", 1, 20);
		operators[16] = new Operator("log", 1, 20);
		operators[17] = new Operator("min", 2, 0);
		operators[18] = new Operator("max", 2, 0);
		operators[19] = new Operator("exp", 1, 20);
		operators[20] = new Operator("floor", 1, 20);
		operators[21] = new Operator("ceil", 1, 20);
		operators[22] = new Operator("abs", 1, 20);
		operators[23] = new Operator("neg", 1, 20);
		operators[24] = new Operator("rnd", 1, 20);
		operators[25] = new Operator("deg", 1, 20);
	}

	/***
	 * gets the variable's value that was assigned previously
	 */
	public float getVariable(String id) {
		try {
			id = id.replace("[", "");
			id = id.replace("]", "");
			int index = Integer.parseInt(id);
			Operation number = operationsList.get(index);
			Object o = operationResolver.operate(number);
			if (o instanceof Number) {
				return ((Number) o).floatValue();
			} else
				return 0.0f;
		} catch (NumberFormatException e) {
			return 0.0f;
		}
	}

	private float getfloat(String s) {
		if (s == null)
			return 0;

		float res = 0;
		try {
			res = Float.parseFloat(s);
		} catch (Exception e) {
			return getVariable(s);
		}

		return res;
	}

	protected Operator[] getOperators() {
		return operators;
	}

	private class Operator {
		private String op;
		private int type;
		private int priority;

		public Operator(String o, int t, int p) {
			op = o;
			type = t;
			priority = p;
		}

		public String getOperator() {
			return op;
		}

		public int getType() {
			return type;
		}

		public int getPriority() {
			return priority;
		}
	}

	private class Node {
		public String nString = null;
		public Operator nOperator = null;
		public Node nLeft = null;
		public Node nRight = null;
		public int nLevel = 0;
		public float nValue = 0;

		public Node() {

		}

		public void free() {
			if (nLeft != null) {
				nLeft.free();
				nLeft = null;
			}

			if (nRight != null) {
				nRight.free();
				nRight = null;
			}

			pool.free(this);

		}

		private void init(Node parent, String s, int level) throws Exception {
			nString = null;
			nOperator = null;
			nLeft = null;
			nRight = null;
			nLevel = 0;
			nValue = 0;
			s = removeIllegalCharacters(s);
			s = removeBrackets(s);
			s = addZero(s);
			if (checkBrackets(s) != 0)
				throw new Exception("Wrong number of brackets in [" + s + "]");

			nString = s;
			nValue = getfloat(s);
			nLevel = level;
			int sLength = s.length();
			int inBrackets = 0;
			int startOperator = 0;

			int i = 0;
			while (i < sLength) {
				if (s.charAt(i) == '(')
					inBrackets++;
				else if (s.charAt(i) == ')')
					inBrackets--;
				else {
					// the expression must be at "root" level
					if (inBrackets == 0) {
						Operator o = getOperator(nString, i);
						if (o != null) {
							// if first operator or lower priority operator
							if (nOperator == null
									|| nOperator.getPriority() >= o
											.getPriority()) {
								nOperator = o;
								startOperator = i;
								i += nOperator.op.length() - 1;
							}
						}
					}
				}
				i++;
			}

			if (nOperator != null) {
				// one operand, should always be at the beginning
				if (startOperator == 0 && nOperator.getType() == 1) {
					// the brackets must be ok
					if (checkBrackets(s.substring(nOperator.getOperator()
							.length())) == 0) {
						nLeft = pool.obtain();
						nLeft.init(this, s.substring(nOperator.getOperator()
								.length()), nLevel + 1);
						nRight = null;
						return;
					} else
						throw new Exception(
								"Error during parsing... missing brackets in ["
										+ s + "]");
				}
				// two operands
				else if (startOperator > 0 && nOperator.getType() == 2) {
					nLeft = pool.obtain();
					nLeft.init(this, s.substring(0, startOperator), nLevel + 1);
					nRight = pool.obtain();
					nRight.init(this, s.substring(startOperator
							+ nOperator.getOperator().length()), nLevel + 1);
				}
			}
		}

		private Operator getOperator(String s, int start) {
			Operator[] operators = getOperators();
			String temp = s.substring(start);
			temp = getNextWord(temp);
			for (int i = 0; i < operators.length; i++) {
				if (temp.startsWith(operators[i].getOperator()))
					return operators[i];
			}
			return null;
		}

		private String getNextWord(String s) {
			int sLength = s.length();
			for (int i = 1; i < sLength; i++) {
				char c = s.charAt(i);
				if ((c > 'z' || c < 'a') && (c > '9' || c < '0'))
					return s.substring(0, i);
			}
			return s;
		}

		/***
		 * checks if there is any missing brackets
		 * 
		 * @return true if s is valid
		 */
		protected int checkBrackets(String s) {
			int sLength = s.length();
			int inBracket = 0;

			for (int i = 0; i < sLength; i++) {
				if (s.charAt(i) == '(' && inBracket >= 0)
					inBracket++;
				else if (s.charAt(i) == ')')
					inBracket--;
			}

			return inBracket;
		}

		/***
		 * returns a string that doesnt start with a + or a -
		 */
		protected String addZero(String s) {
			if (s.startsWith("+") || s.startsWith("-")) {
				int sLength = s.length();
				for (int i = 0; i < sLength; i++) {
					if (getOperator(s, i) != null)
						return "0" + s;
				}
			}

			return s;
		}

		/***
		 * displays the tree of the expression
		 */
		public void trace() {
			String op = getOperator() == null ? " " : getOperator()
					.getOperator();
			_D(op + " : " + getString());
			if (this.hasChild()) {
				if (hasLeft())
					getLeft().trace();
				if (hasRight())
					getRight().trace();
			}
		}

		protected boolean hasChild() {
			return (nLeft != null || nRight != null);
		}

		protected boolean hasOperator() {
			return (nOperator != null);
		}

		protected boolean hasLeft() {
			return (nLeft != null);
		}

		protected Node getLeft() {
			return nLeft;
		}

		protected boolean hasRight() {
			return (nRight != null);
		}

		protected Node getRight() {
			return nRight;
		}

		protected Operator getOperator() {
			return nOperator;
		}

		protected float getValue() {
			return nValue;
		}

		protected void setValue(float f) {
			nValue = f;
		}

		protected String getString() {
			return nString;
		}

		/***
		 * Removes spaces, tabs and brackets at the begining
		 */
		public String removeBrackets(String s) {
			String res = s;
			if (s.length() > 2 && res.startsWith("(") && res.endsWith(")")
					&& checkBrackets(s.substring(1, s.length() - 1)) == 0) {
				res = res.substring(1, res.length() - 1);
			}
			if (res != s)
				return removeBrackets(res);
			else
				return res;
		}

		/***
		 * Removes illegal characters
		 */
		public String removeIllegalCharacters(String s) {
			char[] illegalCharacters = { ' ' };
			String res = s;

			for (int j = 0; j < illegalCharacters.length; j++) {
				int i = res.lastIndexOf(illegalCharacters[j], res.length());
				while (i != -1) {
					String temp = res;
					res = temp.substring(0, i);
					res += temp.substring(i + 1);
					i = res.lastIndexOf(illegalCharacters[j], s.length());
				}
			}
			return res;
		}

		protected void _D(String s) {
			String nbSpaces = "";
			for (int i = 0; i < nLevel; i++)
				nbSpaces += "  ";
			System.out.println(nbSpaces + "|" + s);
		}
	}

	protected static void _D(String s) {
		System.err.println(s);
	}

	public interface OperationResolver {

		/**
		 * <p>
		 * Calculates the result of the given {@link es.eucm.ead.model.elements.operations.Operation} with the current
		 * game state
		 * </p>
		 *
		 * @param <T>          the operation class
		 * @param operation operation to be done
		 * @return operation's result. If operation is {@code null}, a null is
		 *         returned.
		 */
		<T extends Operation, S> S operate(T operation);
	}
}
