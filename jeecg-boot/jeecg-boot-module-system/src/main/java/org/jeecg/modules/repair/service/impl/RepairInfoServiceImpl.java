package org.jeecg.modules.repair.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.repair.entity.RepairInfo;
import org.jeecg.modules.repair.mapper.RepairInfoMapper;
import org.jeecg.modules.repair.service.IRepairInfoService;
import org.springframework.stereotype.Service;

/**
 * @Description: 报修单信息表
 * @Author: jeecg-boot
 * @Date:   2019-09-03
 * @Version: V1.0
 */
@Service
public class RepairInfoServiceImpl extends ServiceImpl<RepairInfoMapper, RepairInfo> implements IRepairInfoService {

}
