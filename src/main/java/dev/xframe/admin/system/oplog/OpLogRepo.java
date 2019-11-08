package dev.xframe.admin.system.oplog;

import java.sql.Timestamp;
import java.util.List;

import dev.xframe.admin.store.StoreKey;
import dev.xframe.inject.Repository;
import dev.xframe.jdbc.PSSetter;
import dev.xframe.jdbc.TypeQuery;
import dev.xframe.jdbc.sql.TypeSQL;

@Repository
public class OpLogRepo  {

    private TypeQuery<OpLog> logQuery =
            TypeQuery.newBuilder(OpLog.class)
                    .setTable(StoreKey.LOG, "T_OP_LOG")
                    .setSQL(0, TypeSQL.where().EQ("name").OVER_EQ("opTime").LESS_EQ("opTime").select())
                    .setSQL(1, TypeSQL.where().EQ("path").OVER_EQ("opTime").LESS_EQ("opTime").select())
                    .setSQL(2, TypeSQL.where().EQ("name").EQ("path").OVER_EQ("opTime").LESS_EQ("opTime").select())
                    .build();
    
    public void add(OpLog log) {
        logQuery.insert(log);
    }
    
    public List<OpLog> fetchByName(String name, Timestamp s, Timestamp e) {
        return logQuery.getSQL(0).fetchMany(PSSetter.of(name, s, e));
    }
    public List<OpLog> fetchByPath(String name, Timestamp s, Timestamp e) {
        return logQuery.getSQL(1).fetchMany(PSSetter.of(name, s, e));
    }
    public List<OpLog> fetchByNamePath(String name, String path, Timestamp s, Timestamp e) {
        return logQuery.getSQL(2).fetchMany(PSSetter.of(name, path, s, e));
    }

}
