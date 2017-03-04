package spork.inject.internal;

import org.junit.Test;

import spork.Spork;
import spork.interfaces.Binder;
import spork.interfaces.BinderRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ObjectGraphTests {

	@Test
	public void bindWithCustomSpork() {
		ObjectGraph graph = new ObjectGraphBuilder().build();
		Binder binder = mock(Binder.class);
		BinderRegistry binderRegistry = mock(BinderRegistry.class);
		Spork spork = new Spork(binderRegistry, binder);

		Object injectable = new Object();
		graph.inject(injectable, spork);
		verify(binder).bind(injectable, graph);
	}
}
