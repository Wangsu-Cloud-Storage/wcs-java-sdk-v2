package com.wos.services.model;

import com.wos.services.exception.WosException;

/**
 * Task execution callback
 */
public interface TaskCallback<K, V> {

    /**
     * Callback when the task is executed successfully.
     *
     * @param result
     *            Callback parameter. Generally, the return type of a specific
     *            operation is used.
     */
    void onSuccess(K result);

    /**
     * Callback when an exception is thrown during task execution.
     *
     * @param exception
     *            Exception information
     * @param singleRequest
     *            The request that causes an exception
     * 
     */
    void onException(WosException exception, V singleRequest);
}
