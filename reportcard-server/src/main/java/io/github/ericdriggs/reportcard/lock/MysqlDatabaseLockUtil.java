package io.github.ericdriggs.reportcard.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Methods for performing lock operations
 * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/locking-functions.html">https://dev.mysql.com/doc/refman/8.4/en/locking-functions.html</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.4/en/locking-service.html">https://dev.mysql.com/doc/refman/8.4/en/locking-service.html</a>
 */
public enum MysqlDatabaseLockUtil {
    ;//static methods only

    private final static Logger log = LoggerFactory.getLogger(MysqlDatabaseLockUtil.class);

    public static boolean getLockOrFalse(final UUID uuid, final Connection conn, final int getLockTimeoutSeconds) throws SQLException {
        final String operationName = "getLockOrFalse";
        if (!isLockFree(uuid, conn)) {
            logOperation(operationName, 0, uuid);
            return false;
        }
        logOperation(operationName, 1, uuid);
        return getLock(uuid, conn, getLockTimeoutSeconds);
    }

    public static boolean isLockFree(final UUID uuid, final Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT IS_FREE_LOCK(?)");
        stmt.setString(1, uuid.toString());
        return doSelectIntegerResultOne("isLockFree", stmt, conn, uuid);
    }

    /**
     * Tries to obtain a lock with a name given by the string str, using a timeout of timeout seconds.
     * Negative timeouts would be infinite so they are not allowed.
     * The lock is exclusive. While held by one session, other sessions cannot obtain a lock of the same name.
     * @param uuid the lock name
     * @param conn the connection holding the session
     * @param getLockTimeoutSeconds how long to wait for getting the lock. 0 is recommended.
     * @return if the lock was able to be acquired
     * @throws SQLException if error attempting to get lock
     */
    static boolean getLock(final UUID uuid, final Connection conn, final int getLockTimeoutSeconds) throws SQLException {
        if (getLockTimeoutSeconds < 0) {
            throw new IllegalArgumentException("infinite timeout not allowed");
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT GET_LOCK(?, ?)");
        stmt.setString(1, uuid.toString());
        stmt.setInt(2, getLockTimeoutSeconds);
        return doSelectIntegerResultOne("selectGetLock", stmt, conn, uuid);
    }

    /**
     * Releases the lock named by the string str that was obtained with GET_LOCK().
     * Returns 1 if the lock was released,
     * 0 if the lock was not established by this thread (in which case the lock is not released),
     * and NULL if the named lock did not exist.
     * The lock does not exist if it was never obtained by a call to GET_LOCK() or if it has previously been released.
     * @param uuid the lock name
     * @param conn the connection holding the session
     * @return if the lock was able to be released
     * @throws SQLException if error attempting to release lock
     */
    static boolean releaseLock(final UUID uuid, final Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT RELEASE_LOCK(?)");
        stmt.setString(1, uuid.toString());
        return doSelectIntegerResultOne("selectReleaseLock", stmt, conn, uuid);
    }

    static boolean doSelectIntegerResultOne(String operationName, PreparedStatement stmt, Connection conn, UUID uuid) throws SQLException {
        final Integer result = executeQueryReturnInteger(stmt);
        logOperation(operationName, result, uuid);
        return Integer.valueOf(1).equals(result);
    }

    static Integer executeQueryReturnInteger(final PreparedStatement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            try {
                return rs.getInt(1);
            } catch (SQLException e) {
                throw e;
            }
        }
        return null;
    }

    public static void logOperation(String operationName, Integer result, UUID uuid) throws SQLException {
        log.trace("operation: {}, result: {}, uuid: {}", operationName, result, uuid);
    }
}