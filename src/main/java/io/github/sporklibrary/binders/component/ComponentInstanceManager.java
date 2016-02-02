package io.github.sporklibrary.binders.component;

import io.github.sporklibrary.Spork;
import io.github.sporklibrary.annotations.BindComponent;
import io.github.sporklibrary.annotations.ComponentScope;
import io.github.sporklibrary.reflection.AnnotatedField;
import io.github.sporklibrary.exceptions.BindException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages Component instances for types that are annotated with the Component annotation.
 */
public class ComponentInstanceManager
{
	private final Map<Class<?>, Object> mSingletonInstances = new HashMap<>();

	/**
	 * Gets an instance of a component within its scope.
	 * The scope is either singleton or default.
	 * If the scope is unsupported, default is assumed.
	 * @param annotatedField the annotated field to get an instance for
	 * @param parent the parent object that holds the field
	 * @return the component instance
	 */
	public Object getInstance(AnnotatedField<BindComponent> annotatedField, Object parent)
	{
		Class<?> field_target_class = getTargetClass(annotatedField);

		if (!annotatedField.getField().getType().isAssignableFrom(field_target_class))
		{
			throw new BindException(BindComponent.class, parent.getClass(), annotatedField.getField(), "incompatible type");
		}

		ComponentScope.Scope scope = getScope(field_target_class);

		switch (scope)
		{
			case SINGLETON:
				Object instance = mSingletonInstances.get(field_target_class);
				return (instance != null) ? instance : createSingletonInstance(field_target_class);

			case DEFAULT:
			default:
				return create(field_target_class);
		}
	}

	public ComponentScope.Scope getScope(Class<?> componentClass)
	{
		ComponentScope annotation = componentClass.getAnnotation(ComponentScope.class);

		return annotation != null ? annotation.value() : ComponentScope.Scope.DEFAULT;
	}

	private Class<?> getTargetClass(AnnotatedField<BindComponent> annotatedField)
	{
		Class<?> override_class = annotatedField.getAnnotation().implementation();

		if (override_class == BindComponent.Default.class)
		{
			return annotatedField.getField().getType();
		}
		else // override class is never null per annotation design
		{
			return override_class;
		}
	}

	private Object create(Class<?> classObject)
	{
		try
		{
			Constructor<?> constructor = classObject.getConstructor();

			if (constructor.isAccessible())
			{
				Object instance = constructor.newInstance();
				Spork.bind(instance);

				return instance;
			}
			else
			{
				constructor.setAccessible(true);
				Object instance = constructor.newInstance();
				Spork.bind(instance);
				constructor.setAccessible(false);

				return instance;
			}
		}
		catch (NoSuchMethodException e)
		{
			throw new BindException(BindComponent.class, classObject, "no default constructor found for " + classObject.getName() + " (must have a constructor with zero arguments)");
		}
		catch (InvocationTargetException e)
		{
			throw new BindException(BindComponent.class, classObject, "constructor threw exception for " + classObject.getName(), e);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			// This branch should never be called due to previous checks
			// We only catch it because we have to
			throw new BindException(BindComponent.class, classObject, "failed to create instance of " + classObject.getName(), e);
		}
	}

	private synchronized Object createSingletonInstance(Class<?> classObject)
	{
		Object instance = create(classObject);

		mSingletonInstances.put(classObject, instance);

		return instance;
	}
}