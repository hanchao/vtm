package org.oscim.utils.async;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

/**
 * GWT emulation of AsynchExecutor, will call tasks immediately :D
 * 
 * @author badlogic
 * 
 */
public class AsyncExecutor implements Disposable {

	/**
	 * Creates a new AsynchExecutor that allows maxConcurrent {@link Runnable}
	 * instances to run in parallel.
	 * 
	 * @param maxConcurrent
	 */
	public AsyncExecutor(int maxConcurrent) {
	}

	// FIXME TODO add wrap into 'FakeFuture' and run via Gdx.app.post()
	/**
	 * Submits a {@link Runnable} to be executed asynchronously. If
	 * maxConcurrent runnables are already running, the runnable
	 * will be queued.
	 * 
	 * @param task the task to execute asynchronously
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> AsyncResult<T> submit(final AsyncTask<T> task) {

		T result = null;
		boolean error = false;
		try {
			task.run();
			result = task.getResult();
		} catch (Throwable t) {
			error = true;
		}
		if (error)
			return null;

		return new AsyncResult(result);
	}

	/**
	 * Submits a {@link Runnable} to be executed asynchronously. If
	 * maxConcurrent runnables are already running, the runnable
	 * will be queued.
	 * 
	 * @param task the task to execute asynchronously
	 */
	public boolean post(Runnable task) {
		Gdx.app.postRunnable(task);
		return true;
	}

	/**
	 * Waits for running {@link AsyncTask} instances to finish,
	 * then destroys any resources like threads. Can not be used
	 * after this method is called.
	 */
	@Override
	public void dispose() {
	}
}
