package DynamicProg;

import java.util.Iterator;

public class DynamicSolution {
	public interface ISubsetHandler<E> extends Iterator<E> {
		public boolean doWork();
	}

	public void run() {
		while (_handler.hasNext()) {
			_handler.next();
			_handler.doWork();
		}
	}

	private ISubsetHandler _handler;
}
