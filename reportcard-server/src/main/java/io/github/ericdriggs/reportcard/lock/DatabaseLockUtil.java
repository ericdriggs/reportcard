package io.github.ericdriggs.reportcard.lock;

import com.mysql.cj.MysqlConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.UUID;

public enum DatabaseLockUtil {
    ;//static methods only

    private final static Logger log = LoggerFactory.getLogger(DatabaseLockUtil.class);

    public static boolean getLockOrFalse(final UUID uuid, final Connection conn, final int getLockTimeoutSeconds) throws SQLException {
        final String operationName = "getLockOrFalse";
        if (!isLockFree(uuid, conn)) {
            logOperation(operationName, 0, uuid);
            return false;
        }
        logOperation(operationName, 1, uuid);
        return selectGetLock(uuid, conn, getLockTimeoutSeconds);
    }

    public static void releaseLock(final UUID uuid, final Connection conn) throws SQLException {
        final String operationName = "releaseLock";
        final boolean releasedLock = selectReleaseLock(uuid, conn);
        if (!releasedLock) {
            logOperation(operationName, 0, uuid);
            throw new IllegalStateException("releaseLock failed to release lock: " + uuid);
        }
        logOperation(operationName, 1, uuid);
    }

    //requiring UUID for lock prevents possibility of injection
    static boolean selectGetLock(final UUID uuid, final Connection conn, final int getLockTimeoutSeconds) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT GET_LOCK(?, ?)");
        stmt.setString(1, uuid.toString());
        stmt.setInt(2, getLockTimeoutSeconds);
        return doSelectIntegerResultOne("selectGetLock", stmt, conn, uuid);
    }

    public static boolean isLockFree(final UUID uuid, final Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT IS_FREE_LOCK(?)");
        stmt.setString(1, uuid.toString());
        return doSelectIntegerResultOne("isLockFree", stmt, conn, uuid);
    }

    public static boolean isLockUsed(final UUID uuid, final Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT IS_USED_LOCK(?)");
        stmt.setString(1, uuid.toString());
        return doSelectIntegerResultNotZero("isLockUsed", stmt, conn, uuid);
    }

    static boolean selectReleaseLock(final UUID uuid, final Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT RELEASE_LOCK(?)");
        stmt.setString(1, uuid.toString());
        return doSelectIntegerResultOne("selectReleaseLock", stmt, conn, uuid);
    }

    static boolean doSelectIntegerResultOne(String operationName, PreparedStatement stmt, Connection conn, UUID uuid) throws SQLException {
        final Integer result = executeQueryReturnInteger(stmt);
        logOperation(operationName, result, uuid);
        return Integer.valueOf(1).equals(result);
    }

    static boolean doSelectIntegerResultNotZero(String operationName, PreparedStatement stmt, Connection conn, UUID uuid) throws SQLException {
        final Integer result = executeQueryReturnInteger(stmt);
        logOperation(operationName, result, uuid);
        return !Integer.valueOf(0).equals(result);
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
//        if (!rs.next()) {
//            throw new IllegalStateException("empty resultSet");
//        }
//        if (rs.getFetchSize() != 1) {
//            throw new IllegalStateException("expected single result set, got " + rs.getFetchSize());
//        }
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int cols = rsmd.getColumnCount();
//        if (cols != 1) {
//            throw new IllegalStateException("expected single column, got " + cols);
//        }
//        final int type = rsmd.getColumnType(1);
//        if (type != Types.INTEGER) {
//            throw new IllegalStateException("expected integer column, got " + type);
//        }
//        return rs.getInt(1);

    }

    public static void logOperation(String operationName, Integer result, UUID uuid) throws SQLException {
        log.info("operation: {}, result: {}, uuid: {}", operationName, result, uuid);
    }

}
