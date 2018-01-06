package org.springframework.orm.hibernate3.support;/*
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 06/01/18
 * Time: 16.11
 * To change this template use File | Settings | File Templates.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.transaction.Status;
import javax.transaction.TransactionManager;

/**
 * Helper class for registering a transaction synchronization for closing
 * a LobCreator, preferring Spring transaction synchronization and falling
 * back to plain JTA transaction synchronization.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SpringLobCreatorSynchronization
 * @see org.springframework.transaction.support.TransactionSynchronizationManager
 * @see JtaLobCreatorSynchronization
 * @see javax.transaction.Transaction#registerSynchronization
 */
 abstract class LobCreatorUtils {

	private static final Log logger = LogFactory.getLog(LobCreatorUtils.class);


	/**
	 * Register a transaction synchronization for closing the given LobCreator,
	 * preferring Spring transaction synchronization and falling back to
	 * plain JTA transaction synchronization.
	 * @param lobCreator the LobCreator to close after transaction completion
	 * @param jtaTransactionManager the JTA TransactionManager to fall back to
	 * when no Spring transaction synchronization is active (may be {@code null})
	 * @throws IllegalStateException if there is neither active Spring transaction
	 * synchronization nor active JTA transaction synchronization
	 */
	public static void registerTransactionSynchronization(
			LobCreator lobCreator, TransactionManager jtaTransactionManager) throws IllegalStateException {

		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			logger.debug("Registering Spring transaction synchronization for LobCreator");
			TransactionSynchronizationManager.registerSynchronization(
					new SpringLobCreatorSynchronization(lobCreator));
		}
		else {
			if (jtaTransactionManager != null) {
				try {
					int jtaStatus = jtaTransactionManager.getStatus();
					if (jtaStatus == Status.STATUS_ACTIVE || jtaStatus == Status.STATUS_MARKED_ROLLBACK) {
						logger.debug("Registering JTA transaction synchronization for LobCreator");
						jtaTransactionManager.getTransaction().registerSynchronization(
								new JtaLobCreatorSynchronization(lobCreator));
						return;
					}
				}
				catch (Throwable ex) {
					throw new TransactionSystemException(
							"Could not register synchronization with JTA TransactionManager", ex);
				}
			}
			throw new IllegalStateException("Active Spring transaction synchronization or active " +
					"JTA transaction with specified [javax.transaction.TransactionManager] required");
		}
	}

}
