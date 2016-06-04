package com.germancoding.taleofpecora;

public abstract class DialogCallback implements Runnable {

	private Object result = null;

	@Override
	public abstract void run();

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
