package spork.inject.internal.objectgraph.modulenode;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Qualifier;

import spork.BindException;
import spork.inject.internal.lang.Annotations;
import spork.inject.internal.InjectSignature;
import spork.inject.internal.lang.Nullability;
import spork.inject.internal.objectgraph.ObjectGraph;

public class ModuleMethodInvoker<T> {
	private final ObjectGraph objectGraph;
	private final Object module;
	private final Method method;
	private final Class<?> targetType;

	ModuleMethodInvoker(ObjectGraph objectGraph, Object module, Method method, Class<T> targetType) {
		this.objectGraph = objectGraph;
		this.module = module;
		this.method = method;
		this.targetType = targetType;
	}

	public T invoke() {
		@Nullable Object[] methodParameters = collectMethodParameters(objectGraph, method);
		return invokeMethod(methodParameters);
	}

	public Object getModule() {
		return module;
	}

	@SuppressWarnings("unchecked")
	private T invokeMethod(@Nullable Object[] params) {
		try {
			return (T) method.invoke(module, params);
		} catch (IllegalAccessException e) {
			throw new BindException(Inject.class, module.getClass(), targetType, module.getClass().getName() + "." + method.getName() + "() is not public (check class and method)", e);
		} catch (Exception e) {
			throw new BindException(Inject.class, module.getClass(), targetType, "failed to invoke method " + module.getClass().getName() + "." + method.getName() + "()", e);
		}
	}

	private static @Nullable Object[] collectMethodParameters(ObjectGraph objectGraph, Method method) {
		int parameterCount = method.getParameterTypes().length;
		if (parameterCount == 0) {
			return null;
		}

		Object[] argumentInstances = new Object[parameterCount];

		for (int i = 0; i < parameterCount; ++i) {
			// fetch all relevant argument data
			Class<?> parameterClass = method.getParameterTypes()[i];
			Annotation[] annotations = method.getParameterAnnotations()[i];
			Type genericParameterType = method.getGenericParameterTypes()[i];

			// retrieve provider
			InjectSignature injectSignature = getInjectSignature(parameterClass, annotations, genericParameterType);
			Provider provider = objectGraph.findProvider(injectSignature);

			if (provider == null) {
				String message = "none of the modules provides an instance for the argument " + injectSignature.getType().getName()
						+ " of " + method.getDeclaringClass().getName() + "." + method.getName() + "()";
				throw new BindException(Inject.class, method.getDeclaringClass(), injectSignature.getType(), message);
			}

			boolean parameterIsProvider = (parameterClass == Provider.class);

			// store provider or instance
			argumentInstances[i] = parameterIsProvider ? provider : provider.get();
		}

		return argumentInstances;
	}

	private static InjectSignature getInjectSignature(Class<?> parameterClass, Annotation[] annotations, Type genericParameterType) {
		Annotation qualifierAnnotation = Annotations.findAnnotationAnnotatedWith(Qualifier.class, annotations);
		Nullability nullability = Nullability.create(annotations);
		boolean parameterIsProvider = (parameterClass == Provider.class);
		// Determine the true type of the instance (so not Provider.class)
		Class<?> targetType = parameterIsProvider ? (Class<?>) ((ParameterizedType) genericParameterType).getActualTypeArguments()[0] : parameterClass;
		return new InjectSignature(targetType, nullability, qualifierAnnotation);
	}
}