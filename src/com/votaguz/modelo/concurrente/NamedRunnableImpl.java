package com.votaguz.modelo.concurrente;

public abstract class NamedRunnableImpl implements NamedRunnable {

	private String _name;

	public NamedRunnableImpl(String name) {
		_name = name;
	}

	@Override
	public String getName() {
		return _name;
	}

	public abstract void run();

}
