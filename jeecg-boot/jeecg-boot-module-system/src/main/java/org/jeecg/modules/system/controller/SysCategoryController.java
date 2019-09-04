package org.jeecg.modules.system.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysCategory;
import org.jeecg.modules.system.model.TreeSelectModel;
import org.jeecg.modules.system.service.ISysCategoryService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: 分类字典
 * @Author: jeecg-boot
 * @Date:   2019-05-29
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/category")
@Slf4j
public class SysCategoryController {
	@Autowired
	private ISysCategoryService sysCategoryService;
	
	/**
	  * 分页列表查询
	 * @param sysCategory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/rootList")
	public Result<IPage<SysCategory>> queryPageList(SysCategory sysCategory,
									  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									  HttpServletRequest req) {
		if(oConvertUtils.isEmpty(sysCategory.getPid())){
			sysCategory.setPid("0");
		}
		Result<IPage<SysCategory>> result = new Result<IPage<SysCategory>>();
		QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, req.getParameterMap());
		Page<SysCategory> page = new Page<SysCategory>(pageNo, pageSize);
		IPage<SysCategory> pageList = sysCategoryService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	@GetMapping(value = "/childList")
	public Result<List<SysCategory>> queryPageList(SysCategory sysCategory,HttpServletRequest req) {
		Result<List<SysCategory>> result = new Result<List<SysCategory>>();
		QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, req.getParameterMap());
		List<SysCategory> list = sysCategoryService.list(queryWrapper);
		result.setSuccess(true);
		result.setResult(list);
		return result;
	}
	
	
	/**
	  *   添加
	 * @param sysCategory
	 * @return
	 */
	@PostMapping(value = "/add")
	public Result<SysCategory> add(@RequestBody SysCategory sysCategory) {
		Result<SysCategory> result = new Result<SysCategory>();
		try {
			sysCategoryService.addSysCategory(sysCategory);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}
	
	/**
	  *  编辑
	 * @param sysCategory
	 * @return
	 */
	@PutMapping(value = "/edit")
	public Result<SysCategory> edit(@RequestBody SysCategory sysCategory) {
		Result<SysCategory> result = new Result<SysCategory>();
		SysCategory sysCategoryEntity = sysCategoryService.getById(sysCategory.getId());
		if(sysCategoryEntity==null) {
			result.error500("未找到对应实体");
		}else {
			sysCategoryService.updateSysCategory(sysCategory);
			result.success("修改成功!");
		}
		return result;
	}
	
	/**
	  *   通过id删除
	 * @param id
	 * @return
	 */
	@DeleteMapping(value = "/delete")
	public Result<SysCategory> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysCategory> result = new Result<SysCategory>();
		SysCategory sysCategory = sysCategoryService.getById(id);
		if(sysCategory==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = sysCategoryService.removeById(id);
			if(ok) {
				result.success("删除成功!");
			}
		}
		
		return result;
	}
	
	/**
	  *  批量删除
	 * @param ids
	 * @return
	 */
	@DeleteMapping(value = "/deleteBatch")
	public Result<SysCategory> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysCategory> result = new Result<SysCategory>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.sysCategoryService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("删除成功!");
		}
		return result;
	}
	
	/**
	  * 通过id查询
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/queryById")
	public Result<SysCategory> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysCategory> result = new Result<SysCategory>();
		SysCategory sysCategory = sysCategoryService.getById(id);
		if(sysCategory==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(sysCategory);
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
  public ModelAndView exportXls(HttpServletRequest request, SysCategory sysCategory) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<SysCategory> queryWrapper = QueryGenerator.initQueryWrapper(sysCategory, request.getParameterMap());
      List<SysCategory> pageList = sysCategoryService.list(queryWrapper);
      // Step.2 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      // 过滤选中数据
      String selections = request.getParameter("selections");
      if(oConvertUtils.isEmpty(selections)) {
    	  mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      }else {
    	  List<String> selectionList = Arrays.asList(selections.split(","));
    	  List<SysCategory> exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
    	  mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
      }
      //导出文件名称
      mv.addObject(NormalExcelConstants.FILE_NAME, "分类字典列表");
      mv.addObject(NormalExcelConstants.CLASS, SysCategory.class);
      LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("分类字典列表数据", "导出人:"+user.getRealname(), "导出信息"));
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
              List<SysCategory> listSysCategorys = ExcelImportUtil.importExcel(file.getInputStream(), SysCategory.class, params);
              for (SysCategory sysCategoryExcel : listSysCategorys) {
                  sysCategoryService.save(sysCategoryExcel);
              }
              return Result.ok("文件导入成功！数据行数:" + listSysCategorys.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
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
  
  
  
  /**
     * 加载单个数据 用于回显
   */
    @RequestMapping(value = "/loadOne", method = RequestMethod.GET)
 	public Result<SysCategory> loadOne(@RequestParam(name="field") String field,@RequestParam(name="val") String val) {
 		Result<SysCategory> result = new Result<SysCategory>();
 		try {
 			
 			QueryWrapper<SysCategory> query = new QueryWrapper<SysCategory>();
 			query.eq(field, val);
 			List<SysCategory> ls = this.sysCategoryService.list(query);
 			if(ls==null || ls.size()==0) {
 				result.setMessage("查询无果");
 	 			result.setSuccess(false);
 			}else if(ls.size()>1) {
 				result.setMessage("查询数据异常,["+field+"]存在多个值:"+val);
 	 			result.setSuccess(false);
 			}else {
 				result.setSuccess(true);
 				result.setResult(ls.get(0));
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 			result.setMessage(e.getMessage());
 			result.setSuccess(false);
 		}
 		return result;
 	}
   
    /**
          * 加载节点的子数据
     */
    @RequestMapping(value = "/loadTreeChildren", method = RequestMethod.GET)
	public Result<List<TreeSelectModel>> loadTreeChildren(@RequestParam(name="pid") String pid) {
		Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
		try {
			List<TreeSelectModel> ls = this.sysCategoryService.queryListByPid(pid);
			result.setResult(ls);
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			result.setMessage(e.getMessage());
			result.setSuccess(false);
		}
		return result;
	}
    
    /**
         * 加载一级节点/如果是同步 则所有数据
     */
    @RequestMapping(value = "/loadTreeRoot", method = RequestMethod.GET)
   	public Result<List<TreeSelectModel>> loadTreeRoot(@RequestParam(name="async") Boolean async,@RequestParam(name="pcode") String pcode) {
   		Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
   		try {
   			List<TreeSelectModel> ls = this.sysCategoryService.queryListByCode(pcode);
   			if(!async) {
   				loadAllCategoryChildren(ls);
   			}
   			result.setResult(ls);
   			result.setSuccess(true);
   		} catch (Exception e) {
   			e.printStackTrace();
   			result.setMessage(e.getMessage());
   			result.setSuccess(false);
   		}
   		return result;
   	}
  
    /**
         * 递归求子节点 同步加载用到
     */
  	private void loadAllCategoryChildren(List<TreeSelectModel> ls) {
  		for (TreeSelectModel tsm : ls) {
			List<TreeSelectModel> temp = this.sysCategoryService.queryListByPid(tsm.getKey());
			if(temp!=null && temp.size()>0) {
				tsm.setChildren(temp);
				loadAllCategoryChildren(temp);
			}
		}
  	}

}
