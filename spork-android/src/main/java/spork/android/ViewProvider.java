package spork.android;

import android.view.View;

/**
 * Used by {@link spork.android.internal.DefaultViewResolver} and {@link spork.android.internal.DefaultContextResolver}
 * to resolve the root {@link View} of a non-standard object (e.g. POJO, support Fragment).
 * This enables Android injection on custom Android objects.
 */
public interface ViewProvider {
	View getView();
}
