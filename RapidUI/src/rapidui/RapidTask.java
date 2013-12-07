package rapidui;

import java.util.ArrayDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

public abstract class RapidTask<Params, Result> {
    private static final String LOG_TAG = "RapidTask";
    
    public enum WaitStrategy {
    	WAIT_NORMAL,
    	WAIT_EVEN_IF_CANCELED
    }

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 128;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "RapidTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(10);

    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                    TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    /**
     * An {@link Executor} that executes tasks one at a time in serial
     * order.  This serialization is global to a particular process.
     */
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static final int MESSAGE_POST_RESULT = 1;
    private static final int MESSAGE_POST_EXCEPTION = 2;
    private static final int MESSAGE_POST_RUNNABLE = 3;
    private static final int MESSAGE_SET_PROGRESS_DIALOG_MESSAGE = 4;
    private static final int MESSAGE_SET_PROGRESS_DIALOG_TITLE = 5;
    private static final int MESSAGE_SET_PROGRESS_DIALOG_PROGRESS = 6;
    private static final int MESSAGE_PROGRESS_DIALOG_TRANSACTION = 7;

    private static final InternalHandler sHandler = new InternalHandler();

    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;
    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;
    
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
    
    private ProgressDialog pd;
    private int threadPriority;
    private Semaphore mutex;
    
    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

    /**
     * Indicates the current status of the task. Each status will be set only once
     * during the lifetime of a task.
     */
    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link AsyncTask#onPostExecute} has finished.
         */
        FINISHED,
    }

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    public RapidTask() {
        sHandler.getLooper();
    	
    	threadPriority = Process.THREAD_PRIORITY_BACKGROUND;
    	mutex = new Semaphore(1);
    	
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);
                
                Process.setThreadPriority(threadPriority);
                //noinspection unchecked
                
                Result result;
                try {
                	result = doInBackground(mParams);
                } catch (Exception e) {
                	postException(e);
                	return null;
                }
                
                return postResult(result);
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
            	mutex.release();
            	
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    android.util.Log.w(LOG_TAG, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occured while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    private Result postResult(Result result) {
        sHandler.obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result)).sendToTarget();
        return result;
    }

    private void postException(Exception e) {
        sHandler.obtainMessage(MESSAGE_POST_EXCEPTION,
                new AsyncTaskResult<Exception>(this, e)).sendToTarget();
    }
    
    public void setProgressMessage(CharSequence message) {
    	if (!isCancelled()) {
	        sHandler.obtainMessage(MESSAGE_SET_PROGRESS_DIALOG_MESSAGE,
	        		new AsyncTaskResult<CharSequence>(this, message)).sendToTarget();
    	}
    }
    
    public void setProgressTitle(CharSequence title) {
    	if (!isCancelled()) {
	        sHandler.obtainMessage(MESSAGE_SET_PROGRESS_DIALOG_TITLE,
	        		new AsyncTaskResult<CharSequence>(this, title)).sendToTarget();
    	}
    }    

    public void setProgress(int progress) {
    	if (!isCancelled()) {
	        sHandler.obtainMessage(MESSAGE_SET_PROGRESS_DIALOG_PROGRESS,
	        		new AsyncTaskResult<Integer>(this, progress)).sendToTarget();
    	}
    }    
    
    /**
     * Returns the current status of this task.
     *
     * @return The current status.
     */
    public final Status getStatus() {
        return mStatus;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     *
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     *
     * @return A result, defined by the subclass of this task.
     *
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @SuppressWarnings("unchecked")
	protected abstract Result doInBackground(Params... params) throws Exception;
    
    protected ProgressDialog onCreateProgressDialog() {
    	return null;
    }

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * 
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param result The result of the operation computed by {@link #doInBackground}.
     *
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object) 
     */
    protected void onPostExecute(Result result) {
    }
    
    protected void onException(Exception e) {
    	throw new RuntimeException(e);
    }

    /**
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     * 
     * <p>The default implementation simply invokes {@link #onCancelled()} and
     * ignores the result. If you write your own implementation, do not call
     * <code>super.onCancelled(result)</code>.</p>
     *
     * @param result The result, if any, computed in
     *               {@link #doInBackground(Object[])}, can be null
     * 
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled(Result result) {
        onCancelled();
    }    
    
    /**
     * <p>Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.</p>
     * 
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     *
     * @see #onCancelled(Object) 
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally. If you are calling {@link #cancel(boolean)} on the task,
     * the value returned by this method should be checked periodically from
     * {@link #doInBackground(Object[])} to end the task as soon as possible.
     *
     * @return <tt>true</tt> if task was cancelled before it completed
     *
     * @see #cancel(boolean)
     */
    public final boolean isCancelled() {
        return mCancelled.get();
    }

    /**
     * <p>Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when <tt>cancel</tt> is called,
     * this task should never run. If the task has already started,
     * then the <tt>mayInterruptIfRunning</tt> parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.</p>
     * 
     * <p>Calling this method will result in {@link #onCancelled(Object)} being
     * invoked on the UI thread after {@link #doInBackground(Object[])}
     * returns. Calling this method guarantees that {@link #onPostExecute(Object)}
     * is never invoked. After invoking this method, you should check the
     * value returned by {@link #isCancelled()} periodically from
     * {@link #doInBackground(Object[])} to finish the task as early as
     * possible.</p>
     *
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     *        task should be interrupted; otherwise, in-progress tasks are allowed
     *        to complete.
     *
     * @return <tt>false</tt> if the task could not be cancelled,
     *         typically because it has already completed normally;
     *         <tt>true</tt> otherwise
     *
     * @see #isCancelled()
     * @see #onCancelled(Object)
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return The computed result.
     *
     * @throws CancellationException If the computation was cancelled.
     * @throws ExecutionException If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted
     *         while waiting.
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }
    
    public final Result get(WaitStrategy strategy) throws InterruptedException, ExecutionException {
    	try {
    		return mFuture.get();
    	} catch (CancellationException e) {
    		if (strategy == WaitStrategy.WAIT_EVEN_IF_CANCELED) {
   				mutex.acquire();
    			mutex.release();
    			return null;
    		} else {
    			throw e;
    		}
    	}
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result.
     *
     * @param timeout Time to wait before cancelling the operation.
     * @param unit The time unit for the timeout.
     *
     * @return The computed result.
     *
     * @throws CancellationException If the computation was cancelled.
     * @throws ExecutionException If the computation threw an exception.
     * @throws InterruptedException If the current thread was interrupted
     *         while waiting.
     * @throws TimeoutException If the wait timed out.
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    /**
     * Executes the task with the specified parameters. The task returns
     * itself (this) so that the caller can keep a reference to it.
     * 
     * <p>Note: this function schedules the task on a queue for a single background
     * thread or pool of threads depending on the platform version.  When first
     * introduced, AsyncTasks were executed serially on a single background thread.
     * Starting with {@link android.os.Build.VERSION_CODES#DONUT}, this was changed
     * to a pool of threads allowing multiple tasks to operate in parallel. Starting
     * {@link android.os.Build.VERSION_CODES#HONEYCOMB}, tasks are back to being
     * executed on a single thread to avoid common application errors caused
     * by parallel execution.  If you truly want parallel execution, you can use
     * the {@link #executeOnExecutor} version of this method
     * with {@link #THREAD_POOL_EXECUTOR}; however, see commentary there for warnings
     * on its use.
     *
     * <p>This method must be invoked on the UI thread.
     *
     * @param params The parameters of the task.
     *
     * @return This instance of AsyncTask.
     *
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *         {@link AsyncTask.Status#RUNNING} or {@link AsyncTask.Status#FINISHED}.
     *
     * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
     * @see #execute(Runnable)
     */
    @SuppressWarnings("unchecked")
	public final RapidTask<Params, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    /**
     * Executes the task with the specified parameters. The task returns
     * itself (this) so that the caller can keep a reference to it.
     * 
     * <p>This method is typically used with {@link #THREAD_POOL_EXECUTOR} to
     * allow multiple tasks to run in parallel on a pool of threads managed by
     * AsyncTask, however you can also use your own {@link Executor} for custom
     * behavior.
     * 
     * <p><em>Warning:</em> Allowing multiple tasks to run in parallel from
     * a thread pool is generally <em>not</em> what one wants, because the order
     * of their operation is not defined.  For example, if these tasks are used
     * to modify any state in common (such as writing a file due to a button click),
     * there are no guarantees on the order of the modifications.
     * Without careful work it is possible in rare cases for the newer version
     * of the data to be over-written by an older one, leading to obscure data
     * loss and stability issues.  Such changes are best
     * executed in serial; to guarantee such work is serialized regardless of
     * platform version you can use this function with {@link #SERIAL_EXECUTOR}.
     *
     * <p>This method must be invoked on the UI thread.
     *
     * @param exec The executor to use.  {@link #THREAD_POOL_EXECUTOR} is available as a
     *              convenient process-wide thread pool for tasks that are loosely coupled.
     * @param params The parameters of the task.
     *
     * @return This instance of AsyncTask.
     *
     * @throws IllegalStateException If {@link #getStatus()} returns either
     *         {@link AsyncTask.Status#RUNNING} or {@link AsyncTask.Status#FINISHED}.
     *
     * @see #execute(Object[])
     */
    @SuppressWarnings("unchecked")
	public final RapidTask<Params, Result> executeOnExecutor(Executor exec,
            Params... params) {
        
    	if (mStatus != Status.PENDING) {
            switch (mStatus) {
            case RUNNING:
                throw new IllegalStateException("Cannot execute task:"
                        + " the task is already running.");
                
            case FINISHED:
                throw new IllegalStateException("Cannot execute task:"
                        + " the task has already been executed "
                        + "(a task can be executed only once)");

            default:
				break;
            }
        }

        pd = onCreateProgressDialog();
        onPreExecute();

        mStatus = Status.RUNNING;
        
        try {
			mutex.acquire();
		} catch (InterruptedException e) {
		}
        
        mWorker.mParams = params;
        exec.execute(mFuture);

        return this;
    }

    /**
     * Convenience version of {@link #execute(Object...)} for use with
     * a simple Runnable object. See {@link #execute(Object[])} for more
     * information on the order of execution.
     *
     * @see #execute(Object[])
     * @see #executeOnExecutor(java.util.concurrent.Executor, Object[])
     */
    public static void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }
    
    private void dismissDialog() {
    	if (pd != null) {
    		pd.dismiss();
    		pd = null;
    	}
    }
    
    private void finish(Result result) {
    	dismissDialog();
    	
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }
    
    private void finishWithException(Exception e) {
    	dismissDialog();
    	
        mStatus = Status.FINISHED;
        onException(e);
    }
    
    protected void runOnUiThread(Runnable r) {
    	sHandler.obtainMessage(MESSAGE_POST_RUNNABLE,
    			new AsyncTaskResult<Runnable>(this, r)).sendToTarget();
    }
    
    protected ProgressDialogTransaction beginProgressDialogTransaction() {
    	return new ProgressDialogTransaction(new ProgressDialogTransaction.OnCommitListener() {
			@Override
			public void onCommit(ProgressDialogTransaction transaction) {
		    	sHandler.obtainMessage(MESSAGE_PROGRESS_DIALOG_TRANSACTION,
		    			new AsyncTaskResult<ProgressDialogTransaction>(RapidTask.this, transaction)).sendToTarget();
			}
		});
    }
    
    private static class InternalHandler extends Handler {
        @SuppressWarnings({"unchecked"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData);
                    break;
                    
                case MESSAGE_POST_EXCEPTION:
                	result.mTask.finishWithException((Exception) result.mData);
                	break;
                    
                case MESSAGE_POST_RUNNABLE:
                	((Runnable) result.mData).run();
                    break;
                    
                case MESSAGE_SET_PROGRESS_DIALOG_MESSAGE:
                	if (result.mTask.pd != null) {
                		result.mTask.pd.setMessage((CharSequence) result.mData);
                	}
                	break;

                case MESSAGE_SET_PROGRESS_DIALOG_TITLE:
                	if (result.mTask.pd != null) {
                		result.mTask.pd.setTitle((CharSequence) result.mData);
                	}
                	break;

                case MESSAGE_SET_PROGRESS_DIALOG_PROGRESS:
                	if (result.mTask.pd != null) {
                		result.mTask.pd.setProgress((Integer) result.mData);
                	}
                	break;
                	
                case MESSAGE_PROGRESS_DIALOG_TRANSACTION:
                	if (result.mTask.pd != null) {
                		((ProgressDialogTransaction) result.mData).execute(result.mTask.pd);
                	}
                	break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    private static class AsyncTaskResult<Data> {
		@SuppressWarnings("rawtypes")
		final RapidTask mTask;
        final Data mData;

        @SuppressWarnings("rawtypes")
		AsyncTaskResult(RapidTask task, Data data) {
            mTask = task;
            mData = data;
        }
    }
    
    public RapidTask<Params, Result> setThreadPriority(int priority) {
    	if (mStatus != Status.PENDING) {
    		Log.w(LOG_TAG, "setThreadPriority() should be called before the task executed.");
    	} else {
    		this.threadPriority = priority;
    	}
    	return this;
    }
}