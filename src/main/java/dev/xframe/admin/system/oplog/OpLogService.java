package dev.xframe.admin.system.oplog;

import java.sql.Timestamp;
import java.util.Collections;

import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;

@Rest("system/oplog")
@XSegment(name="操作日志", model=OpLog.class)
public class OpLogService {
	
	@HttpMethods.GET
	public Object get() {
		return Collections.EMPTY_LIST;
	}
	
	@HttpMethods.GET("query")
	public Object query(
			@HttpArgs.Param @XColumn("操作用户") String opUser,
			@HttpArgs.Param	@XColumn(value="开始时间", type=XColumn.type_time) Timestamp startTime,
			@HttpArgs.Param @XColumn(value="结束时间", type=XColumn.type_time) Timestamp endTime) {
		System.out.println(startTime);
		return Collections.EMPTY_LIST;
	}

}
