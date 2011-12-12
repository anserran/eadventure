package es.eucm.eadventure.editor.view.generics.scene.impl;

import es.eucm.eadventure.common.model.conditions.impl.ANDCondition;
import es.eucm.eadventure.common.model.conditions.impl.EmptyCondition;
import es.eucm.eadventure.common.model.conditions.impl.NOTCondition;
import es.eucm.eadventure.common.model.conditions.impl.OperationCondition;
import es.eucm.eadventure.common.model.conditions.impl.enums.Comparator;
import es.eucm.eadventure.common.model.effects.EAdEffect;
import es.eucm.eadventure.common.model.effects.impl.variables.EAdChangeFieldValueEffect;
import es.eucm.eadventure.common.model.elements.EAdComplexElement;
import es.eucm.eadventure.common.model.elements.EAdCondition;
import es.eucm.eadventure.common.model.elements.EAdSceneElement;
import es.eucm.eadventure.common.model.elements.impl.EAdBasicSceneElement;
import es.eucm.eadventure.common.model.elements.impl.EAdComplexElementImpl;
import es.eucm.eadventure.common.model.elements.impl.EAdSceneElementDefImpl;
import es.eucm.eadventure.common.model.events.enums.ConditionedEventType;
import es.eucm.eadventure.common.model.events.impl.EAdConditionEventImpl;
import es.eucm.eadventure.common.model.extra.EAdList;
import es.eucm.eadventure.common.model.guievents.impl.EAdMouseEventImpl;
import es.eucm.eadventure.common.model.impl.ResourcedElementImpl;
import es.eucm.eadventure.common.model.variables.EAdField;
import es.eucm.eadventure.common.model.variables.impl.EAdFieldImpl;
import es.eucm.eadventure.common.model.variables.impl.EAdVarDefImpl;
import es.eucm.eadventure.common.model.variables.impl.SystemFields;
import es.eucm.eadventure.common.model.variables.impl.operations.ValueOperation;
import es.eucm.eadventure.common.params.fills.impl.EAdPaintImpl;
import es.eucm.eadventure.common.predef.model.effects.EAdChangeAppearance;
import es.eucm.eadventure.common.predef.model.effects.EAdMakeActiveElementEffect;
import es.eucm.eadventure.common.resources.EAdBundleId;
import es.eucm.eadventure.common.resources.assets.drawable.basics.impl.shapes.RectangleShape;

public class EditionSceneElement extends EAdComplexElementImpl {

	private EAdSceneElement proxy = null; 
	
	private EAdList<EAdEffect> unselectEffects;
	
	public EditionSceneElement(EAdSceneElement element, float scale) {
		setId(element.getId() + "_edition");
		
		if (element instanceof EAdBasicSceneElement) {
			proxy = new BasicSceneElementProxy(element);
		} else if (element instanceof EAdComplexElement){
			proxy = new ComplexSceneElementProxy(element);
		}
		
		this.components.add(proxy);
		this.setVarInitialValue(EAdBasicSceneElement.VAR_X, (int) (scale * (Integer) proxy.getVars().get(EAdBasicSceneElement.VAR_X)));
		this.setVarInitialValue(EAdBasicSceneElement.VAR_Y, (int) (scale * (Integer) proxy.getVars().get(EAdBasicSceneElement.VAR_Y)));
		proxy.setVarInitialValue(EAdBasicSceneElement.VAR_X, 0);
		proxy.setVarInitialValue(EAdBasicSceneElement.VAR_Y, 0);
		this.setVarInitialValue(EAdBasicSceneElement.VAR_SCALE, scale);
		
		proxy.setVarInitialValue(EAdBasicSceneElement.VAR_ALPHA, 0.3f);
		
		EAdChangeFieldValueEffect makeActiveElement = new EAdMakeActiveElementEffect(proxy);

		proxy.addBehavior(EAdMouseEventImpl.MOUSE_ENTERED, changeAlphaEffect(proxy, 0.6f, new OperationCondition(SystemFields.ACTIVE_ELEMENT, proxy, Comparator.DIFFERENT)));
		proxy.addBehavior(EAdMouseEventImpl.MOUSE_EXITED, changeAlphaEffect(proxy, 0.3f, new OperationCondition(SystemFields.ACTIVE_ELEMENT, proxy, Comparator.DIFFERENT)));
		proxy.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, makeActiveElement);
		proxy.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, changeAlphaEffect(proxy, 1.0f, null));

		EAdConditionEventImpl conditionedEvent = new EAdConditionEventImpl(new OperationCondition(SystemFields.ACTIVE_ELEMENT, proxy, Comparator.EQUAL));
		conditionedEvent.addEffect(ConditionedEventType.CONDITIONS_UNMET, changeAlphaEffect(proxy, 0.3f, null));
		unselectEffects = conditionedEvent.getEffects().get(ConditionedEventType.CONDITIONS_UNMET);
		this.getEvents().add(conditionedEvent);

		addResizeSquare();
		
		addChangeAppearance();
		
	}
	
	private void addChangeAppearance() {
		EAdSceneElementDefImpl squareDef = new EAdSceneElementDefImpl();
		EAdBasicSceneElement square = new EAdBasicSceneElement(squareDef);
		square.setDragCond(EmptyCondition.FALSE_EMPTY_CONDITION);
		square.setVarInitialValue(EAdBasicSceneElement.VAR_X, 12);
		square.setVarInitialValue(EAdBasicSceneElement.VAR_Y, 12);

		squareDef.getResources().addAsset(squareDef.getInitialBundle(), EAdSceneElementDefImpl.appearance, new RectangleShape(10, 10, EAdPaintImpl.BLACK_ON_WHITE));
		square.setVarInitialValue(EAdBasicSceneElement.VAR_VISIBLE, Boolean.FALSE);
		this.components.add(square);
		
		unselectEffects.add(new EAdChangeFieldValueEffect(
				new EAdFieldImpl<Boolean>(square, EAdBasicSceneElement.VAR_VISIBLE),
				new ValueOperation(Boolean.FALSE)));
		
		proxy.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, new EAdChangeFieldValueEffect(
				new EAdFieldImpl<Boolean>(square, EAdBasicSceneElement.VAR_VISIBLE),
				new ValueOperation(Boolean.TRUE)));
		
		EAdFieldImpl<EAdBundleId> appearanceField = new EAdFieldImpl<EAdBundleId>(proxy, ResourcedElementImpl.VAR_BUNDLE_ID);
		EAdFieldImpl<Boolean> changedAppearance = new EAdFieldImpl<Boolean>(proxy, new EAdVarDefImpl<Boolean>("changed", Boolean.class, Boolean.FALSE));

		EAdBundleId prev = proxy.getDefinition().getResources().getInitialBundle();
		for (EAdBundleId bundleID : proxy.getDefinition().getResources().getBundles()) {
			if (bundleID != proxy.getDefinition().getResources().getInitialBundle()) {
				EAdEffect effect = new EAdChangeAppearance(proxy, bundleID);
				ANDCondition cond = new ANDCondition(new OperationCondition(appearanceField, prev, Comparator.EQUAL), 
						new NOTCondition(new OperationCondition(changedAppearance)));
				effect.setCondition(cond);
				square.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, effect);
				
				effect = new EAdChangeFieldValueEffect(changedAppearance, new ValueOperation(Boolean.TRUE));
				effect.setCondition(cond);
				square.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, effect);
				
				prev = bundleID;
			}
		}

		EAdEffect effect = new EAdChangeAppearance(proxy, proxy.getDefinition().getResources().getInitialBundle());
		ANDCondition cond = new ANDCondition(new OperationCondition(appearanceField, prev, Comparator.EQUAL), 
				new NOTCondition(new OperationCondition(changedAppearance)));
		effect.setCondition(cond);
		square.addBehavior(EAdMouseEventImpl.MOUSE_RIGHT_CLICK, effect);
		
		square.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, new EAdChangeFieldValueEffect(changedAppearance, new ValueOperation(Boolean.FALSE)));

	}

	private void addResizeSquare() {
		EAdSceneElementDefImpl squareDef = new EAdSceneElementDefImpl();
		EAdBasicSceneElement square = new EAdBasicSceneElement(squareDef);
		square.setDragCond(EmptyCondition.TRUE_EMPTY_CONDITION);
		squareDef.getResources().addAsset(squareDef.getInitialBundle(), EAdSceneElementDefImpl.appearance, new RectangleShape(10, 10, EAdPaintImpl.BLACK_ON_WHITE));
		square.setVarInitialValue(EAdBasicSceneElement.VAR_VISIBLE, Boolean.FALSE);
		this.components.add(square);
		
		unselectEffects.add(new EAdChangeFieldValueEffect(
				new EAdFieldImpl<Boolean>(square, EAdBasicSceneElement.VAR_VISIBLE),
				new ValueOperation(Boolean.FALSE)));
		
		proxy.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, new EAdChangeFieldValueEffect(
				new EAdFieldImpl<Boolean>(square, EAdBasicSceneElement.VAR_VISIBLE),
				new ValueOperation(Boolean.TRUE)));
		proxy.addBehavior(EAdMouseEventImpl.MOUSE_LEFT_CLICK, new EAdChangeFieldValueEffect(
				new EAdFieldImpl<Integer>(square, EAdBasicSceneElement.VAR_X),
				new ValueOperation(new EAdFieldImpl<Integer>(proxy, EAdBasicSceneElement.VAR_WIDTH))));

	}
	
	
	
	private EAdChangeFieldValueEffect changeAlphaEffect(EAdSceneElement proxy, float alpha, EAdCondition cond) {
		EAdField<Float> alphaField = new EAdFieldImpl<Float>(proxy,
				EAdBasicSceneElement.VAR_ALPHA);
		EAdChangeFieldValueEffect effect = new EAdChangeFieldValueEffect(
				 alphaField, new ValueOperation(alpha));
		if (cond != null)
			effect.setCondition(cond);
		return effect;
	}
	

}