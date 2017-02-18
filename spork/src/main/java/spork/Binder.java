package spork;

/**
 * Binds annotations for a specified instance with 0 or more modules specified.
 *
 * This interface is used as the main entry for binding, but also by internal caching mechanisms.
 */
public interface Binder {
	/**
	 * Bind all annotations for an object instance on all levels of inheritance.
	 *
	 * @param object the instance to bind annotations for
	 * @param parameters optional arguments (e.g. an ObjectGraph for spork-inject)
	 */
	void bind(Object object, Object... parameters);
}