package es.eucm.eadventure.engine.core.util.impl;

import es.eucm.eadventure.engine.core.util.EAdMatrix;

public class EAdMatrixImpl implements EAdMatrix {

	private static final int DIMENSION = 3;

	/**
	 * <p>
	 * [ m0 m3 m6 ]
	 * </p>
	 * <p>
	 * [ m1 m4 m7 ]
	 * </p>
	 * <p>
	 * [ m2 m5 m8 ]
	 * </p>
	 */
	private float[] m;

	private float[] i;

	private boolean recalculateInverse;

	/**
	 * Constructs a matrix 3x3 with the identity
	 */
	public EAdMatrixImpl() {
		recalculateInverse = true;
		m = getIdentity();
	}

	public EAdMatrixImpl(float m[]) {
		this.m = m;
	}

	@Override
	public float[] getFlatMatrix() {
		return m;
	}

	@Override
	public float[] getTransposedMatrix() {
		float tm[] = new float[9];
		tm[0] = m[0];
		tm[1] = m[3];
		tm[2] = m[6];
		tm[3] = m[1];
		tm[4] = m[4];
		tm[5] = m[7];
		tm[6] = m[2];
		tm[7] = m[5];
		tm[8] = m[8];
		return tm;
	}

	@Override
	public void postTranslate(float x, float y) {
		float t[] = getIdentity();
		t[6] = x;
		t[7] = y;
		postMultiply(t);
	}

	@Override
	public void preTranslate(float x, float y) {
		float t[] = getIdentity();
		t[6] = x;
		t[7] = y;
		preMultiply(t);
	}

	@Override
	public void postRotate(float angle) {
		postMultiply(getRotationMatrix(angle));
	}

	@Override
	public void preRotate(float angle) {
		preMultiply(getRotationMatrix(angle));
	}

	private float[] getRotationMatrix(float angle) {
		float r[] = getIdentity();
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		r[0] = cos;
		r[1] = sin;
		r[3] = -sin;
		r[4] = cos;
		return r;
	}

	@Override
	public void postScale(float scaleX, float scaleY) {
		recalculateInverse = true;
		float s[] = getIdentity();
		s[0] = scaleX;
		s[4] = scaleY;
		postMultiply(s);
	}

	@Override
	public void preScale(float scaleX, float scaleY) {
		float s[] = getIdentity();
		s[0] = scaleX;
		s[4] = scaleY;
		preMultiply(s);
	}

	@Override
	public void preMultiply(float m1[]) {
		recalculateInverse = true;
		this.m = multiply(m1, this.m);

	}

	@Override
	public void postMultiply(float m1[]) {
		recalculateInverse = true;
		this.m = multiply(this.m, m1);

	}

	public static float[] multiply(float[] m1, float[] m2) {
		float m[] = new float[DIMENSION * DIMENSION];
		int row = 0, column = 0;

		for (int i = 0; i < DIMENSION * DIMENSION; i++) {
			row = i % DIMENSION;
			column = i / DIMENSION;
			m[i] = 0;
			for (int j = 0; j < DIMENSION; j++) {
				m[i] += m1[row + j * DIMENSION] * m2[column * DIMENSION + j];
			}
		}
		return m;
	}

	public static float[] getIdentity() {
		return new float[] { 1, 0, 0, 0, 1, 0, 0, 0, 1 };
	}

	public float getOffsetX() {
		return m[7];
	}

	public float getOffsetY() {
		return m[8];
	}

	public void setIdentity() {
		recalculateInverse = true;
		m = getIdentity();
	}

	private float[] postMultiplyPoint(float m[], float x, float y) {
		float px = m[0] * x + m[3] * y + m[6];
		float py = m[1] * x + m[4] * y + m[7];
		return new float[] { px, py };
	}

	@Override
	public float[] preMultiplyPoint(float x, float y) {
		return preMultiplyPoint(m, x, y);
	}

	private float[] preMultiplyPoint(float m[], float x, float y) {
		float px = m[0] * x + m[1] * y + m[2];
		float py = m[3] * x + m[4] * y + m[5];
		return new float[] { px, py };
	}

	public float[] preMultiplyPointInverse(float x, float y) {
		if (recalculateInverse) {
			getInversedMatrix();
		}
		return preMultiplyPoint(i, x, y);
	}

	@Override
	public float[] postMultiplyPoint(float x, float y) {
		return postMultiplyPoint(m, x, y);
	}

	public float[] postMultiplyPointInverse(float x, float y) {
		if (recalculateInverse) {
			getInversedMatrix();
		}
		return postMultiplyPoint(i, x, y);
	}

	public float[] getInversedMatrix() {
		if (recalculateInverse) {
			i = new float[9];
			i[2] = 0;
			i[5] = 0;
			i[8] = 1;
			float det = m[0] * m[4] - m[3] * m[1];
			i[0] = m[4] / det;
			i[1] = - m[1] / det;
			i[3] = - m[3] / det;
			i[4] = m[0] / det;

			i[6] = (-i[0] * m[6] - i[3] * m[7]);
			i[7] = (-i[1] * m[6] - i[4] * m[7]);

			recalculateInverse = false;
		}
		return i;
	}

}
