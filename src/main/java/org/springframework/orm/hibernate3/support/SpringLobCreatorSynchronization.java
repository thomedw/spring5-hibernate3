package org.springframework.orm.hibernate3.support;/*
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 06/01/18
 * Time: 16.17
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.util.Assert;

/**
 * Callback for resource cleanup at the end of a Spring transaction.
 * Invokes {@code LobCreator.close()} to clean up temporary LOBs
 * that might have been created.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see LobCreator#close()
 */
 class SpringLobCreatorSynchronization  extends TransactionSynchronizationAdapter {

	/**
	 * Order value for TransactionSynchronization objects that clean up LobCreators.
	 * Return CONNECTION_SYNCHRONIZATION_ORDER - 200 to execute LobCreator cleanup
	 * before Hibernate Session (- 100) and JDBC Connection cleanup, if any.
	 * @see org.springframework.jdbc.datasource.DataSourceUtils#CONNECTION_SYNCHRONIZATION_ORDER
	 */
	public static final int LOB_CREATOR_SYNCHRONIZATION_ORDER =
			DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 200;


	private final LobCreator lobCreator;

	private boolean beforeCompletionCalled = false;


	/**
	 * Create a SpringLobCreatorSynchronization for the given LobCreator.
	 * @param lobCreator the LobCreator to close after transaction completion
	 */
	 SpringLobCreatorSynchronization(LobCreator lobCreator) {
		Assert.notNull(lobCreator, "LobCreator must not be null");
		this.lobCreator = lobCreator;
	}

	@Override
	public int getOrder() {
		return LOB_CREATOR_SYNCHRONIZATION_ORDER;
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
			// beforeCompletion not called before (probably because of flushing on commit
			// in the transaction manager, after the chain of beforeCompletion calls).
			// Close the LobCreator here.
			this.lobCreator.close();
		}
	}

}
