package es.eucm.eadventure.common.model.effects.impl.physics;

import es.eucm.eadventure.common.interfaces.Element;
import es.eucm.eadventure.common.interfaces.Param;
import es.eucm.eadventure.common.model.effects.impl.sceneelements.AbstractSceneElementEffect;
import es.eucm.eadventure.common.model.elements.EAdSceneElement;
import es.eucm.eadventure.common.model.variables.impl.operations.MathOperation;

@Element(detailed = PhApplyImpluse.class, runtime = PhApplyImpluse.class)
public class PhApplyImpluse extends AbstractSceneElementEffect {

	@Param("xForce")
	private MathOperation xForce;

	@Param("yForce")
	private MathOperation yForce;

	public PhApplyImpluse() {
		this(null, null, null);
	}

	public PhApplyImpluse(EAdSceneElement element, MathOperation xForce,
			MathOperation yForce) {
		super("phApplyImpulse_" + element);
		this.xForce = xForce;
		this.yForce = yForce;
		this.setQueueable(false);
		this.setSceneElement(element);
	}

	public MathOperation getXForce() {
		return xForce;
	}

	public MathOperation getYForce() {
		return yForce;
	}

	public void setForce(MathOperation xForce, MathOperation yForce) {
		this.xForce = xForce;
		this.yForce = yForce;
	}

}
