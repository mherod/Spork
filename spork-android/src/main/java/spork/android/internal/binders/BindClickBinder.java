package spork.android.internal.binders;

import android.view.View;

import java.lang.reflect.Method;

import spork.extension.MethodBinder;
import spork.android.BindClick;
import spork.android.extension.ViewResolver;
import spork.android.internal.utils.Views;
import spork.internal.Reflection;

import static spork.internal.BindFailedBuilder.bindFailedBuilder;

public class BindClickBinder implements MethodBinder<BindClick> {
	private final ViewResolver viewResolver;

	public BindClickBinder(ViewResolver viewResolver) {
		this.viewResolver = viewResolver;
	}

	private static class BindClickListener implements View.OnClickListener {
		private final BindClick annotation;
		private final Method method;
		private final Object object;

		BindClickListener(BindClick annotation, Method method, Object object) {
			this.annotation = annotation;
			this.method = method;
			this.object = object;
		}

		@Override
		public void onClick(View v) {
			Class<?>[] parameterTypes = method.getParameterTypes();

			if (parameterTypes.length == 0) {
				Reflection.invokeMethod(BindClick.class, method, object);
			} else if (parameterTypes.length == 1 && View.class.isAssignableFrom(parameterTypes[0])) {
				Reflection.invokeMethod(BindClick.class, method, object, v);
			} else {
				throw bindFailedBuilder(BindClick.class, "onClick() failed because the method arguments are invalid")
						.suggest("method arguments must be a View type (e.g. View, Button, etc.)")
						.from(v.getClass())
						.into(method)
						.build();
			}
		}
	}

	@Override
	public void bind(Object object, BindClick annotation, Method method, Object... parameters) {
		View view = Views.getView(viewResolver, annotation.value(), method.getName(), object);
		view.setOnClickListener(new BindClickListener(annotation, method, object));
	}

	@Override
	public Class<BindClick> getAnnotationClass() {
		return BindClick.class;
	}
}
