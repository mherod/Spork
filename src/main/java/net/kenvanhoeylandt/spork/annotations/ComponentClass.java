package net.kenvanhoeylandt.spork.annotations;

public class ComponentClass
{
	private final Component mComponent;

	private final Class<?> mDeclaredClass;

	public ComponentClass(Component component, Class<?> declaredClass)
	{
		mComponent = component;
		mDeclaredClass = declaredClass;
	}

	public Component getComponent()
	{
		return mComponent;
	}

	public Class<?> getDeclaredClass()
	{
		return mDeclaredClass;
	}
}
