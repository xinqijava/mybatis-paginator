/*
 * Copyright (c) 2012-2013, Poplar Yfyang 杨友峰 (poplar1123@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.miemiedev.mybatis.paginator.support;

import com.github.miemiedev.mybatis.paginator.dialect.Dialect;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author poplar.yfyang
 * @author miemiedev
 */
public class SQLHelp {
    private static Logger logger = LoggerFactory.getLogger(SQLHelp.class);

    /**
     * 查询总纪录数
     *
     * @param mappedStatement mapped
     * @param parameterObject 参数
     * @param boundSql        boundSql
     * @param dialect         database dialect
     * @return 总记录数
     * @throws java.sql.SQLException sql查询错误
     */
    public static int getCount(
                               final MappedStatement mappedStatement, final Transaction transaction, final Object parameterObject,
                               final BoundSql boundSql, Dialect dialect) throws SQLException {
        final String count_sql = dialect.getCountSQL();
        logger.debug("Total count SQL [{}] ", count_sql);
        logger.debug("Total count Parameters: {} ", parameterObject);

        Connection connection = transaction.getConnection();
        PreparedStatement countStmt = connection.prepareStatement(count_sql);
        DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement,parameterObject,boundSql);
        handler.setParameters(countStmt);

        ResultSet rs = countStmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        logger.debug("Total count: {}", count);
        return count;

    }


    /**
     * 尝试获取已经存在的在 MS，提供对手写count和page的支持
     *
     * @param configuration
     * @param msId
     * @return
     */
    public static MappedStatement getExistedMappedStatement(Configuration configuration, String msId) {
        MappedStatement mappedStatement = null;
        try {
            mappedStatement = configuration.getMappedStatement(msId, false);
        } catch (Throwable t) {
            //ignore
        }
        return mappedStatement;
    }

    /**
     * 执行自定义语句的查询
     *
     * @param executor
     * @param countMs
     * @param parameter
     * @param boundSql
     * @return
     * @throws SQLException
     */
    public static Integer executeManualCount(Executor executor, MappedStatement countMs,
                                             Object parameter, BoundSql boundSql) throws SQLException {
        BoundSql countBoundSql = countMs.getBoundSql(parameter);

        final String count_sql = countBoundSql.getSql();
        logger.debug("Total count SQL [{}] ", count_sql);
        logger.debug("Total count Parameters: {} ", parameter);

        Connection connection = executor.getTransaction().getConnection();
        PreparedStatement countStmt = connection.prepareStatement(count_sql);
        DefaultParameterHandler handler = new DefaultParameterHandler(countMs, parameter, boundSql);
        handler.setParameters(countStmt);

        ResultSet rs = countStmt.executeQuery();
        int count = 0;
        if (rs.next()) {
            count = rs.getInt(1);
        }
        logger.debug("Total count: {}", count);
        return count;

    }

}