package dev.xframe.admin.system.oplog;

import java.sql.Timestamp;
import java.util.Collections;

import dev.xframe.admin.conf.LogicException;
import dev.xframe.admin.system.EnumKeys;
import dev.xframe.admin.view.XColumn;
import dev.xframe.admin.view.XSegment;
import dev.xframe.http.service.Rest;
import dev.xframe.http.service.rest.HttpArgs;
import dev.xframe.http.service.rest.HttpMethods;
import dev.xframe.inject.Inject;
import dev.xframe.utils.XStrings;

@Rest("system/oplog")
@XSegment(name="操作日志", model=OpLog.class)
public class OpLogService {
    
    @Inject
    private OpLogRepo logRepo;
	
	@HttpMethods.GET
	public Object get() {
		return Collections.EMPTY_LIST;
	}
	
	@HttpMethods.GET("query")
	public Object query(
			@HttpArgs.Param @XColumn(value="操作用户", enumKey=EnumKeys.USER_LIST) String opUser,
	        @HttpArgs.Param @XColumn("操作路径") String opPath,
			@HttpArgs.Param	@XColumn(value="开始时间", type=XColumn.type_time) Timestamp startTime,
			@HttpArgs.Param @XColumn(value="结束时间", type=XColumn.type_time) Timestamp endTime) {
	    if(startTime == null || endTime == null) {
	        throw new LogicException("时间为空");
	    }
	    if(XStrings.isEmpty(opUser)) {
	        if(XStrings.isEmpty(opPath)) {
	            throw new LogicException("条件为空");
	        }
	        return logRepo.fetchByPath(opPath, startTime, endTime);
	    }
	    if(XStrings.isEmpty(opPath)) {
	        return logRepo.fetchByName(opUser, startTime, endTime);
	    }
	    return logRepo.fetchByNamePath(opUser, opPath, startTime, endTime);
	}

}
