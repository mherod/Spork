package spork.android.internal;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;

import javax.annotation.Nullable;

import spork.BindException;
import spork.android.BindFragment;
import spork.android.interfaces.FragmentResolver;

/**
 * Default {@link FragmentResolver} implementation for all regular Android classes that
 * could provide a Fragment instance.
 * </p>
 * It resolves to {@link Object} because the Fragment might be a regular Android one or a support library one.
 */
public class DefaultFragmentResolver implements FragmentResolver {

	@Override
	@Nullable
	public Object resolveFragment(Object object, int id) {

		if (object instanceof Activity) {
			return ((Activity) object).getFragmentManager().findFragmentById(id);
		} else if (object instanceof Fragment) {
			Fragment fragment = ((Fragment) object);
			Fragment childFragment = fragment.getFragmentManager().findFragmentById(id);

			// On a Wiko Sunset handset, the child fragment was registered with the regular FragmentManager:
			if (childFragment == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				return fragment.getChildFragmentManager().findFragmentById(id);
			}

			return childFragment;
		} else {
			return null;
		}
	}

	@Override
	@Nullable
	public Object resolveFragment(Object object, String idName) {

		if (object instanceof Activity) {
			Activity activity = (Activity) object;
			int id = activity.getResources().getIdentifier(idName, "id", activity.getPackageName());

			if (id == 0) {
				throw new BindException(BindFragment.class, activity.getClass(), "Fragment not found by name for id '" + idName + "'");
			}

			return activity.getFragmentManager().findFragmentById(id);
		} else if (object instanceof Fragment) {
			Fragment fragment = (Fragment) object;

			int id = fragment.getResources().getIdentifier(idName, "id", fragment.getActivity().getPackageName());

			if (id == 0) {
				throw new BindException(BindFragment.class, fragment.getClass(), "Fragment not found by name for id '" + idName + "'");
			}

			Fragment childFragment = fragment.getFragmentManager().findFragmentById(id);

			// On a Wiko Sunset handset, the child fragment was registered with the regular FragmentManager:
			if (childFragment == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				return fragment.getChildFragmentManager().findFragmentById(id);
			}

			return childFragment;
		} else {
			return null;
		}
	}
}
