/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms.service;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.stereotype.Service; import java.time.*; import java.util.*;
@Service public class EnterpriseYmsService {
 public SlotResult evaluate(@Valid SlotRequest r){
  List<String> blockers=new ArrayList<>(); if(r.arrival().isBefore(r.windowStart())||r.arrival().isAfter(r.windowEnd())) blockers.add("车辆未在预约时窗到达"); if(!r.dockAvailable()) blockers.add("指定月台不可用"); if(!r.vehicleCertified()) blockers.add("车辆或司机资质未通过"); if(r.loadUnits()>r.dockCapacity()) blockers.add("作业量超过月台单时段能力"); if(!r.windowEnd().isAfter(r.windowStart())) blockers.add("预约结束时间必须晚于开始时间");
  long wait=Math.max(0,Duration.between(r.arrival(),r.windowStart()).toMinutes());
  return new SlotResult(r.appointmentNo(),r.dockNo(),wait,blockers,blockers.isEmpty(),blockers.isEmpty()?"READY_FOR_GATE":"MANUAL_DISPATCH");
 }
 public record SlotRequest(@NotBlank String appointmentNo,@NotBlank String dockNo,@NotNull LocalDateTime arrival,@NotNull LocalDateTime windowStart,@NotNull LocalDateTime windowEnd,boolean dockAvailable,boolean vehicleCertified,@Min(1) int loadUnits,@Min(1) int dockCapacity){}
 public record SlotResult(String appointmentNo,String dockNo,long estimatedWaitMinutes,List<String> blockers,boolean gateAllowed,String decision){}
}

