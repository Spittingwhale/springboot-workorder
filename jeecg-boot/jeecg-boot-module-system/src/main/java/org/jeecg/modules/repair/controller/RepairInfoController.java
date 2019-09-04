package org.jeecg.modules.repair.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.repair.entity.RepairInfo;
import org.jeecg.modules.repair.service.IRepairInfoService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @Description: 报修单信息表
 * @Author: jeecg-boot
 * @Date: 2019-09-03
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "报修单信息表")
@RestController
@RequestMapping("/repairs/repairInfo")
public class RepairInfoController {
    @Autowired
    private IRepairInfoService repairInfoService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 分页列表查询
     *
     * @param repairInfo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "报修单信息表-分页列表查询")
    @ApiOperation(value = "报修单信息表-分页列表查询", notes = "报修单信息表-分页列表查询")
    @GetMapping(value = "/list/{state}")
    public Result<IPage<RepairInfo>> queryPageList(RepairInfo repairInfo,
                                                   @PathVariable("state") Integer state,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String role = sysUserService.getRoleByUserId(sysUser.getId());
        Result<IPage<RepairInfo>> result = new Result<IPage<RepairInfo>>();
        QueryWrapper<RepairInfo> queryWrapper = QueryGenerator.initQueryWrapper(repairInfo, req.getParameterMap());
//		if ((!StringUtils.isBlank(role)) && role.equals("constumer")) {
//			queryWrapper.select("adress,cont,eval_cont,eval_degree,name,pic,state_tag,tel,title,vist_bz,acc_time,over_time");
//		}
        if ((StringUtils.isNotBlank(role)) && (!role.equals("admin"))) {
            queryWrapper.eq("user_id", sysUser.getId());
        }
        // 0：报修（默认） 1：接收 2：派工 3：维修中 4：维修完毕
        if (state == 5) {
            queryWrapper.in("state_tag", 1, 2, 3);
        } else if (state == 6) {
            queryWrapper.in("state_tag", 0,1,2,3);
        } else if (state < 5) {
            queryWrapper.eq("state_tag", state);
        }
        Page<RepairInfo> page = new Page<RepairInfo>(pageNo, pageSize);
        IPage<RepairInfo> pageList = repairInfoService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param repairInfo
     * @return
     */
    @AutoLog(value = "报修单信息表-添加")
    @ApiOperation(value = "报修单信息表-添加", notes = "报修单信息表-添加")
    @PostMapping(value = "/add")
    public Result<RepairInfo> add(@RequestBody RepairInfo repairInfo) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String role = sysUserService.getRoleByUserId(sysUser.getId());
        Result<RepairInfo> result = new Result<RepairInfo>();
        try {
            repairInfo.setUserId(sysUser.getId());
            repairInfoService.save(repairInfo);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param repairInfo
     * @return
     */
    @AutoLog(value = "报修单信息表-编辑")
    @ApiOperation(value = "报修单信息表-编辑", notes = "报修单信息表-编辑")
    @PutMapping(value = "/edit")
    public Result<RepairInfo> edit(@RequestBody RepairInfo repairInfo) {
        Result<RepairInfo> result = new Result<RepairInfo>();
        RepairInfo repairInfoEntity = repairInfoService.getById(repairInfo.getId());
        if (repairInfoEntity == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = repairInfoService.updateById(repairInfo);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "报修单详情-通过id改变维修单状态")
    @ApiOperation(value = "报修单详情-通过id改变维修单状态", notes = "报修单详情-通过id改变维修单状态")
    @RequestMapping(value = "/editStat")
    public Result<?> editRepairOrderState(@RequestParam(name = "id") String id, @RequestParam(name = "state") Integer state) {
        Result<RepairInfo> result = new Result<RepairInfo>();
        RepairInfo repairInfoEntity = repairInfoService.getById(id);
        if (repairInfoEntity == null) {
            result.error500("未找到对应实体");
        } else {
            repairInfoEntity.setStateTag(state);
            boolean ok = repairInfoService.updateById(repairInfoEntity);
            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 修理工接单
     * status： start ：开始；end： 结束； accept：接受订单
     *
     * @param repairInfo
     * @return
     */
    @PostMapping(value = "/repair/{status}")
    public Result<RepairInfo> repair(@RequestBody RepairInfo repairInfo, @PathVariable("status") String status) {
        Result<RepairInfo> result = new Result<RepairInfo>();
        try {
            if (StringUtils.isNotBlank(status) && status.equals("start")) {
                //开始修理
                repairInfo.setStateTag(3);
                if (repairInfo.getStartTime() == null) {
                    repairInfo.setStartTime(new Date());
                }
            } else if (StringUtils.isNotBlank(status) && status.equals("end")) {
                //结束修理
                repairInfo.setStateTag(4);
                if (repairInfo.getOverTime() == null) {
                    repairInfo.setOverTime(new Date());
                }
            } else if (StringUtils.isNotBlank(status) && status.equals("accept")) {
                //接受订单
                repairInfo.setStateTag(2);
                repairInfo.setAccTime(new Date());
            }
            repairInfoService.saveOrUpdate(repairInfo);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "报修单信息表-通过id删除")
    @ApiOperation(value = "报修单信息表-通过id删除", notes = "报修单信息表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        try {
            repairInfoService.removeById(id);
        } catch (Exception e) {
            log.error("删除失败", e.getMessage());
            return Result.error("删除失败!");
        }
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "报修单信息表-批量删除")
    @ApiOperation(value = "报修单信息表-批量删除", notes = "报修单信息表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<RepairInfo> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<RepairInfo> result = new Result<RepairInfo>();
        if (ids == null || "".equals(ids.trim())) {
            result.error500("参数不识别！");
        } else {
            this.repairInfoService.removeByIds(Arrays.asList(ids.split(",")));
            result.success("删除成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "报修单信息表-通过id查询")
    @ApiOperation(value = "报修单信息表-通过id查询", notes = "报修单信息表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<RepairInfo> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<RepairInfo> result = new Result<RepairInfo>();
        RepairInfo repairInfo = repairInfoService.getById(id);
        if (repairInfo == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(repairInfo);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, HttpServletResponse response) {
        // Step.1 组装查询条件
        QueryWrapper<RepairInfo> queryWrapper = null;
        try {
            String paramsStr = request.getParameter("paramsStr");
            if (oConvertUtils.isNotEmpty(paramsStr)) {
                String deString = URLDecoder.decode(paramsStr, "UTF-8");
                RepairInfo repairInfo = JSON.parseObject(deString, RepairInfo.class);
                queryWrapper = QueryGenerator.initQueryWrapper(repairInfo, request.getParameterMap());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<RepairInfo> pageList = repairInfoService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "报修单信息表列表");
        mv.addObject(NormalExcelConstants.CLASS, RepairInfo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("报修单信息表列表数据", "导出人:Jeecg", "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<RepairInfo> listRepairInfos = ExcelImportUtil.importExcel(file.getInputStream(), RepairInfo.class, params);
                repairInfoService.saveBatch(listRepairInfos);
                return Result.ok("文件导入成功！数据行数:" + listRepairInfos.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.ok("文件导入失败！");
    }

}
