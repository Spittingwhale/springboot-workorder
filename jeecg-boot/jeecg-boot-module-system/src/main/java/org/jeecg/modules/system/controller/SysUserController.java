package org.jeecg.modules.system.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserDepart;
import org.jeecg.modules.system.entity.SysUserRole;
import org.jeecg.modules.system.model.DepartIdModel;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserDepartService;
import org.jeecg.modules.system.service.ISysUserRoleService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecg.modules.system.vo.SysDepartUsersVO;
import org.jeecg.modules.system.vo.SysUserRoleVO;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

	@Autowired
	private ISysUserService sysUserService;

    @Autowired
    private ISysDepartService sysDepartService;

	@Autowired
	private ISysUserRoleService sysUserRoleService;

	@Autowired
	private ISysUserDepartService sysUserDepartService;

	@Autowired
	private ISysUserRoleService userRoleService;

	@Autowired
	private RedisUtil redisUtil;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	//@RequiresPermissions("sys:user:list")
	public Result<IPage<SysUser>> queryPageList(SysUser user,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
		QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(user, req.getParameterMap());
		Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
		IPage<SysUser> pageList = sysUserService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequiresPermissions("user:add")
	public Result<SysUser> add(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		String selectedRoles = jsonObject.getString("selectedroles");
		String selectedDeparts = jsonObject.getString("selecteddeparts");
		try {
			SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
			user.setCreateTime(new Date());//设置创建时间
			String salt = oConvertUtils.randomGen(8);
			user.setSalt(salt);
			String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), salt);
			user.setPassword(passwordEncode);
			user.setStatus(1);
			user.setDelFlag("0");
			sysUserService.addUserWithRole(user, selectedRoles);
            sysUserService.addUserWithDepart(user, selectedDeparts);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
//	@RequiresPermissions("user:edit")
	public Result<SysUser> edit(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			SysUser sysUser = sysUserService.getById(jsonObject.getString("id"));
			if(sysUser==null) {
				result.error500("未找到对应实体");
			}else {
				SysUser user = JSON.parseObject(jsonObject.toJSONString(), SysUser.class);
				user.setUpdateTime(new Date());
				//String passwordEncode = PasswordUtil.encrypt(user.getUsername(), user.getPassword(), sysUser.getSalt());
				user.setPassword(sysUser.getPassword());
				String roles = jsonObject.getString("selectedroles");
                String departs = jsonObject.getString("selecteddeparts");
				sysUserService.editUserWithRole(user, roles);
                sysUserService.editUserWithDepart(user, departs);
				result.success("修改成功!");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 * 删除用户
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysUser> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysUser> result = new Result<SysUser>();
		// 定义SysUserDepart实体类的数据库查询LambdaQueryWrapper
		LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
		SysUser sysUser = sysUserService.getById(id);
		if(sysUser==null) {
			result.error500("未找到对应实体");
		}else {
			// 当某个用户被删除时,删除其ID下对应的部门数据
			query.eq(SysUserDepart::getUserId, id);
			boolean ok = sysUserService.removeById(id);
			sysUserDepartService.remove(query);
			if(ok) {
				result.success("删除成功!");
			}
		}

		return result;
	}

	/**
	 * 批量删除用户
	 */
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysUser> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		// 定义SysUserDepart实体类的数据库查询对象LambdaQueryWrapper
		LambdaQueryWrapper<SysUserDepart> query = new LambdaQueryWrapper<SysUserDepart>();
		String[] idArry = ids.split(",");
		Result<SysUser> result = new Result<SysUser>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			this.sysUserService.removeByIds(Arrays.asList(ids.split(",")));
			// 当批量删除时,删除在SysUserDepart中对应的所有部门数据
			for(String id : idArry) {
				query.eq(SysUserDepart::getUserId, id);
				this.sysUserDepartService.remove(query);
			}
			result.success("删除成功!");
		}
		return result;
	}

	/**
	  * 冻结&解冻用户
	 * @param jsonObject
	 * @return
	 */
	@RequestMapping(value = "/frozenBatch", method = RequestMethod.PUT)
	public Result<SysUser> frozenBatch(@RequestBody JSONObject jsonObject) {
		Result<SysUser> result = new Result<SysUser>();
		try {
			String ids = jsonObject.getString("ids");
			String status = jsonObject.getString("status");
			String[] arr = ids.split(",");
			for (String id : arr) {
				if(oConvertUtils.isNotEmpty(id)) {
					this.sysUserService.update(new SysUser().setStatus(Integer.parseInt(status)),
							new UpdateWrapper<SysUser>().lambda().eq(SysUser::getId,id));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失败"+e.getMessage());
		}
		result.success("操作成功!");
		return result;

    }

    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysUser> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysUser);
            result.setSuccess(true);
        }
        return result;
    }

    @RequestMapping(value = "/queryUserRole", method = RequestMethod.GET)
    public Result<List<String>> queryUserRole(@RequestParam(name = "userid", required = true) String userid) {
        Result<List<String>> result = new Result<>();
        List<String> list = new ArrayList<String>();
        List<SysUserRole> userRole = sysUserRoleService.list(new QueryWrapper<SysUserRole>().lambda().eq(SysUserRole::getUserId, userid));
        if (userRole == null || userRole.size() <= 0) {
            result.error500("未找到用户相关角色信息");
        } else {
            for (SysUserRole sysUserRole : userRole) {
                list.add(sysUserRole.getRoleId());
            }
            result.setSuccess(true);
            result.setResult(list);
        }
        return result;
    }


    /**
	  *  校验用户账号是否唯一<br>
	  *  可以校验其他 需要检验什么就传什么。。。
     *
     * @param sysUser
     * @return
     */
    @RequestMapping(value = "/checkOnlyUser", method = RequestMethod.GET)
    public Result<Boolean> checkOnlyUser(SysUser sysUser) {
        Result<Boolean> result = new Result<>();
        //如果此参数为false则程序发生异常
        result.setResult(true);
        try {
            //通过传入信息查询新的用户信息
            SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>(sysUser));
            if (user != null) {
                result.setSuccess(false);
                result.setMessage("用户账号已存在");
                return result;
            }

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 修改密码
     */
    @RequestMapping(value = "/changPassword", method = RequestMethod.PUT)
    public Result<SysUser> changPassword(@RequestBody SysUser sysUser) {
        Result<SysUser> result = new Result<SysUser>();
        String password = sysUser.getPassword();
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()));
        if (sysUser == null) {
            result.error500("未找到对应实体");
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setResult(sysUser);
            result.success("密码修改完成！");
        }
        return result;
    }

    /**
     * 查询指定用户和部门关联的数据
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/userDepartList", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> getUserDepartsList(@RequestParam(name = "userId", required = true) String userId) {
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> depIdModelList = this.sysUserDepartService.queryDepartIdsOfUser(userId);
            if (depIdModelList != null && depIdModelList.size() > 0) {
                result.setSuccess(true);
                result.setMessage("查找成功");
                result.setResult(depIdModelList);
            } else {
                result.setSuccess(false);
                result.setMessage("查找失败");
            }
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("查找过程中出现了异常: " + e.getMessage());
            return result;
        }

    }

    /**
     * 生成在添加用户情况下没有主键的问题,返回给前端,根据该id绑定部门数据
     *
     * @return
     */
    @RequestMapping(value = "/generateUserId", method = RequestMethod.GET)
    public Result<String> generateUserId() {
        Result<String> result = new Result<>();
        System.out.println("我执行了,生成用户ID==============================");
        String userId = UUID.randomUUID().toString().replace("-", "");
        result.setSuccess(true);
        result.setResult(userId);
        return result;
    }

    /**
     * 根据部门id查询用户信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryUserByDepId", method = RequestMethod.GET)
    public Result<List<SysUser>> queryUserByDepId(@RequestParam(name = "id", required = true) String id) {
        Result<List<SysUser>> result = new Result<>();
        List<SysUser> userList = sysUserDepartService.queryUserByDepId(id);
        try {
            result.setSuccess(true);
            result.setResult(userList);
            return result;
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
            result.setSuccess(false);
            return result;
        }
    }

    /**
     * 查询所有用户所对应的角色信息
     *
     * @return
     */
    @RequestMapping(value = "/queryUserRoleMap", method = RequestMethod.GET)
    public Result<Map<String, String>> queryUserRole() {
        Result<Map<String, String>> result = new Result<>();
        Map<String, String> map = userRoleService.queryUserRole();
        result.setResult(map);
        result.setSuccess(true);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysUser sysUser,HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysUser> queryWrapper = QueryGenerator.initQueryWrapper(sysUser, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysUser> pageList = sysUserService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "用户列表");
        mv.addObject(NormalExcelConstants.CLASS, SysUser.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("用户列表数据", "导出人:"+user.getRealname(), "导出信息"));
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
    @RequiresPermissions("user:import")
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
                List<SysUser> listSysUsers = ExcelImportUtil.importExcel(file.getInputStream(), SysUser.class, params);
                for (SysUser sysUserExcel : listSysUsers) {
                    if (sysUserExcel.getPassword() == null) {
                        // 密码默认为“123456”
                        sysUserExcel.setPassword("123456");
                    }
                    sysUserService.save(sysUserExcel);
                }
                return Result.ok("文件导入成功！数据行数：" + listSysUsers.size());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                return Result.error("抱歉! 您导入的数据中用户名已经存在.");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                	log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
	 * @功能：根据id 批量查询
	 * @param userIds
	 * @return
	 */
	@RequestMapping(value = "/queryByIds", method = RequestMethod.GET)
	public Result<Collection<SysUser>> queryByIds(@RequestParam String userIds) {
		Result<Collection<SysUser>> result = new Result<>();
		String[] userId = userIds.split(",");
		Collection<String> idList = Arrays.asList(userId);
		Collection<SysUser> userRole = sysUserService.listByIds(idList);
		result.setSuccess(true);
		result.setResult(userRole);
		return result;
	}

	/**
	 * 首页密码修改
	 */
	@RequestMapping(value = "/updatePassword", method = RequestMethod.PUT)
	public Result<SysUser> changPassword(@RequestBody JSONObject json) {
		Result<SysUser> result = new Result<SysUser>();
		String username = json.getString("username");
		String oldpassword = json.getString("oldpassword");
		SysUser user = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
		if(user==null) {
			result.error500("未找到用户!");
			return result;
		}
		String passwordEncode = PasswordUtil.encrypt(username, oldpassword, user.getSalt());
		if(!user.getPassword().equals(passwordEncode)) {
			result.error500("旧密码输入错误!");
			return result;
		}

		String password = json.getString("password");
		String confirmpassword = json.getString("confirmpassword");
		if(oConvertUtils.isEmpty(password)) {
			result.error500("新密码不存在!");
			return result;
		}

		if(!password.equals(confirmpassword)) {
			result.error500("两次输入密码不一致!");
			return result;
		}
		String newpassword = PasswordUtil.encrypt(username, password, user.getSalt());
		this.sysUserService.update(new SysUser().setPassword(newpassword), new LambdaQueryWrapper<SysUser>().eq(SysUser::getId, user.getId()));
		result.success("密码修改完成！");
		return result;
	}

    @RequestMapping(value = "/userRoleList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> userRoleList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String roleId = req.getParameter("roleId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByRoleId(page,roleId,username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 给指定角色添加用户
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addSysUserRole", method = RequestMethod.POST)
    public Result<String> addSysUserRole(@RequestBody SysUserRoleVO sysUserRoleVO) {
        Result<String> result = new Result<String>();
        try {
            String sysRoleId = sysUserRoleVO.getRoleId();
            for(String sysUserId:sysUserRoleVO.getUserIdList()) {
                SysUserRole sysUserRole = new SysUserRole(sysUserId,sysRoleId);
                QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
                queryWrapper.eq("role_id", sysRoleId).eq("user_id",sysUserId);
                SysUserRole one = sysUserRoleService.getOne(queryWrapper);
                if(one==null){
                    sysUserRoleService.save(sysUserRole);
                }

            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }
    /**
     *   删除指定角色的用户关系
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRole", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRole(@RequestParam(name="roleId") String roleId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).eq("user_id",userId);
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定角色的用户关系
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteUserRoleBatch", method = RequestMethod.DELETE)
    public Result<SysUserRole> deleteUserRoleBatch(
            @RequestParam(name="roleId") String roleId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserRole> result = new Result<SysUserRole>();
        try {
            QueryWrapper<SysUserRole> queryWrapper = new QueryWrapper<SysUserRole>();
            queryWrapper.eq("role_id", roleId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserRoleService.remove(queryWrapper);
            result.success("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 部门用户列表
     */
    @RequestMapping(value = "/departUserList", method = RequestMethod.GET)
    public Result<IPage<SysUser>> departUserList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysUser>> result = new Result<IPage<SysUser>>();
        Page<SysUser> page = new Page<SysUser>(pageNo, pageSize);
        String depId = req.getParameter("depId");
        String username = req.getParameter("username");
        IPage<SysUser> pageList = sysUserService.getUserByDepId(page,depId,username);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 给指定部门添加对应的用户
     */
    @RequestMapping(value = "/editSysDepartWithUser", method = RequestMethod.POST)
    public Result<String> editSysDepartWithUser(@RequestBody SysDepartUsersVO sysDepartUsersVO) {
        Result<String> result = new Result<String>();
        try {
            String sysDepId = sysDepartUsersVO.getDepId();
            for(String sysUserId:sysDepartUsersVO.getUserIdList()) {
                SysUserDepart sysUserDepart = new SysUserDepart(null,sysUserId,sysDepId);
                QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
                queryWrapper.eq("dep_id", sysDepId).eq("user_id",sysUserId);
                SysUserDepart one = sysUserDepartService.getOne(queryWrapper);
                if(one==null){
                    sysUserDepartService.save(sysUserDepart);
                }
            }
            result.setMessage("添加成功!");
            result.setSuccess(true);
            return result;
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.setSuccess(false);
            result.setMessage("出错了: " + e.getMessage());
            return result;
        }
    }

    /**
     *   删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepart", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepart(@RequestParam(name="depId") String depId,
                                                    @RequestParam(name="userId",required=true) String userId
    ) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).eq("user_id",userId);
            sysUserDepartService.remove(queryWrapper);
            result.success("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }

    /**
     * 批量删除指定机构的用户关系
     */
    @RequestMapping(value = "/deleteUserInDepartBatch", method = RequestMethod.DELETE)
    public Result<SysUserDepart> deleteUserInDepartBatch(
            @RequestParam(name="depId") String depId,
            @RequestParam(name="userIds",required=true) String userIds) {
        Result<SysUserDepart> result = new Result<SysUserDepart>();
        try {
            QueryWrapper<SysUserDepart> queryWrapper = new QueryWrapper<SysUserDepart>();
            queryWrapper.eq("dep_id", depId).in("user_id",Arrays.asList(userIds.split(",")));
            sysUserDepartService.remove(queryWrapper);
            result.success("删除成功!");
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败！");
        }
        return result;
    }
    
    /**
         *  查询当前用户的所有部门/当前部门编码
     * @return
     */
    @RequestMapping(value = "/getCurrentUserDeparts", method = RequestMethod.GET)
    public Result<Map<String,Object>> getCurrentUserDeparts() {
        Result<Map<String,Object>> result = new Result<Map<String,Object>>();
        try {
        	LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
            List<SysDepart> list = this.sysDepartService.queryUserDeparts(sysUser.getId());
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("list", list);
            map.put("orgCode", sysUser.getOrgCode());
            result.setSuccess(true);
            result.setResult(map);
        }catch(Exception e) {
            log.error(e.getMessage(), e);
            result.error500("查询失败！");
        }
        return result;
    }

    


	/**
	 * 用户注册接口
	 * 
	 * @param jsonObject
	 * @param user
	 * @return
	 */
	@PostMapping("/register")
	public Result<JSONObject> userRegister(@RequestBody JSONObject jsonObject, SysUser user) {
		Result<JSONObject> result = new Result<JSONObject>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");
		Object code = redisUtil.get(phone);
		String username = jsonObject.getString("username");
		String password = jsonObject.getString("password");
		String email = jsonObject.getString("email");
		SysUser sysUser1 = sysUserService.getUserByName(username);
		if (sysUser1 != null) {
			result.setMessage("用户名已注册");
			result.setSuccess(false);
			return result;
		}
		SysUser sysUser2 = sysUserService.getUserByPhone(phone);

		if (sysUser2 != null) {
			result.setMessage("该手机号已注册");
			result.setSuccess(false);
			return result;
		}
		SysUser sysUser3 = sysUserService.getUserByEmail(email);
		if (sysUser3 != null) {
			result.setMessage("邮箱已被注册");
			result.setSuccess(false);
			return result;
		}

		if (!smscode.equals(code)) {
			result.setMessage("手机验证码错误");
			result.setSuccess(false);
			return result;
		}

		try {
			user.setCreateTime(new Date());// 设置创建时间
			String salt = oConvertUtils.randomGen(8);
			String passwordEncode = PasswordUtil.encrypt(username, password, salt);
			user.setSalt(salt);
			user.setUsername(username);
			user.setPassword(passwordEncode);
			user.setEmail(email);
			user.setPhone(phone);
			user.setStatus(1);
			user.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
			user.setActivitiSync(CommonConstant.ACT_SYNC_1);
			sysUserService.save(user);
			result.success("注册成功");
		} catch (Exception e) {
			result.error500("注册失败");
		}
		return result;
	}

	/**
	 * 
	 * @param 根据用户名或手机号查询用户信息
	 * @return
	 */
	@GetMapping("/querySysUser")
	public Result<Map<String, Object>> querySysUser(SysUser sysUser) {
		String phone = sysUser.getPhone();
		String username = sysUser.getUsername();
		Result<Map<String, Object>> result = new Result<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if (oConvertUtils.isNotEmpty(phone)) { 
			SysUser userList = sysUserService.getUserByPhone(phone);
			map.put("username",userList.getUsername());
			map.put("phone",userList.getPhone());
			result.setSuccess(true);
			result.setResult(map);
			return result;
		}
		if (oConvertUtils.isNotEmpty(username)) {
			SysUser userList = sysUserService.getUserByName(username);
			map.put("username",userList.getUsername());
			map.put("phone",userList.getPhone());
			result.setSuccess(true);
			result.setResult(map);
			return result;
		}
		result.setSuccess(false);
		result.setMessage("验证失败");
		return result;
	}
	
	/**
	 * 用户手机号验证
	 */
	@PostMapping("/phoneVerification")
	public Result<String> phoneVerification(@RequestBody JSONObject jsonObject) {
		Result<String> result = new Result<String>();
		String phone = jsonObject.getString("phone");
		String smscode = jsonObject.getString("smscode");
		Object code = redisUtil.get(phone);
		if (!smscode.equals(code)) {
			result.setMessage("手机验证码错误");
			result.setSuccess(false);
			return result;
		}
		redisUtil.set(phone, smscode);
		result.setResult(smscode);
		result.setSuccess(true);
		return result;
	}
	
	/**
	 * 用户更改密码
	 */
	@GetMapping("/passwordChange")
	public Result<SysUser> passwordChange(@RequestParam(name="username")String username,
										  @RequestParam(name="password")String password,
			                              @RequestParam(name="smscode")String smscode,
			                              @RequestParam(name="phone") String phone) {
        Result<SysUser> result = new Result<SysUser>();
        SysUser sysUser=new SysUser();
        Object object= redisUtil.get(phone);
        if(null==object) {
        	result.setMessage("更改密码失败");
            result.setSuccess(false);
        }
        if(!smscode.equals(object)) {
        	result.setMessage("更改密码失败");
            result.setSuccess(false);
        }
        sysUser = this.sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername,username));
        if (sysUser == null) {
            result.setMessage("未找到对应实体");
            result.setSuccess(false);
            return result;
        } else {
            String salt = oConvertUtils.randomGen(8);
            sysUser.setSalt(salt);
            String passwordEncode = PasswordUtil.encrypt(sysUser.getUsername(), password, salt);
            sysUser.setPassword(passwordEncode);
            this.sysUserService.updateById(sysUser);
            result.setSuccess(true);
            result.setMessage("密码修改完成！");
            return result;
        }
    }
	
}
