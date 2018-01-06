package org.springframework.orm.hibernate3.support;/*
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 06/01/18
 * Time: 16.15
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.util.Assert;

import javax.transaction.Synchronization;

/**
 * Callback for resource cleanup at the end of a JTA transaction.
 * Invokes {@code LobCreator.close()} to clean up temporary LOBs
 * that might have been created.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see LobCreator#close()
 * @see javax.transaction.Transaction#registerSynchronization
 */
 class JtaLobCreatorSynchronization  implements Synchronization {

	private final LobCreator lobCreator;

	private boolean beforeCompletionCalled = false;


	/**
	 * Create a JtaLobCreatorSynchronization for the given LobCreator.
	 * @param lobCreator the LobCreator to close after transaction completion
	 */
	 JtaLobCreatorSynchronization(LobCreator lobCreator) {
		Assert.notNull(lobCreator, "LobCreator must not be null");
		this.lobCreator = lobCreator;
	}

	@Override
	public void beforeCompletion() {
		// Close the LobCreator early if possible, to avoid issues with strict JTA
		// implementations that issue warnings when doing JDBC operations after
		// transaction completion.
		this.beforeCompletionCalled = true;
		this.lobCreator.close();
	}

	@Override
	public void afterCompletion(int status) {
		if (!this.beforeCompletionCalled) {
			// beforeCompletion not called before (probably because of JTA rollback).
			// Close the LobCreator here.
			this.lobCreator.close();
		}
	}

}
