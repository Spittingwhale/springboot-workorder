<template>
  <a-card :bordered="false">

    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">

          <a-col :md="6" :sm="8">
            <a-form-item label="接受时间">
              <a-input placeholder="请输入接受时间" v-model="queryParam.accTime"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="报修地址">
              <a-input placeholder="请输入报修地址" v-model="queryParam.adress"></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
            <a-col :md="6" :sm="8">
              <a-form-item label="报修内容">
                <a-input placeholder="请输入报修内容" v-model="queryParam.cont"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="评论内容">
                <a-input placeholder="请输入评论内容" v-model="queryParam.evalCont"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="8">
              <a-form-item label="满意度，满分10">
                <a-input placeholder="请输入满意度，满分10" v-model="queryParam.evalDegree"></a-input>
              </a-form-item>
            </a-col>
          </template>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
              <a @click="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>

        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增</a-button>
      <a-button type="primary" icon="download" @click="handleExportXls('报修单详情')">导出</a-button>
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl"
                @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete"/>
            删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作
          <a-icon type="down"/>
        </a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{
        selectedRowKeys.length }}</a>项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </div>

      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        height="200"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <!-- 字符串超长截取省略号显示-->
        <span slot="cont" slot-scope="text">
          <j-ellipsis v-html=" text.length>500 ? '[图片]':text" :length="25" />
<!--          <img v-html="text" height="50" width="50"></img>-->
        </span>

        <span slot="action" slot-scope="text, record">
<!--          <a @click="handleEdit(record)">编辑</a>-->
          <a @click="toOpreaList()">hello</a>

          <a-divider type="vertical"/>
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down"/></a>
            <a-menu slot="overlay">
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <repairInfo-modal ref="modalForm" @ok="modalFormOk"></repairInfo-modal>
  </a-card>
</template>

<script>
  import RepairInfoModal from './modules/RepairInfoModal'
  import { filterObj } from '@/utils/util';
  import { deleteAction, getAction,downFile } from '@/api/manage'
  import Vue from 'vue'
  import JEllipsis from "@/components/jeecg/JEllipsis";
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import ImagPreview from "../jeecg/ImagPreview";
  // import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "RepairInfoList",
    // mixins:[JeecgListMixin],
    components: {
        ImagPreview,
        JEllipsis,
      RepairInfoModal
    },
    data () {
      return {
          //token header
          // tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},
          /* 查询条件-请不要在queryParam中声明非字符串值的属性 */
          queryParam: {},
          /* 数据源 */
          dataSource:[],
          /* 分页参数 */
          ipagination:{
              current: 1,
              pageSize: 10,
              pageSizeOptions: ['10', '20', '30'],
              showTotal: (total, range) => {
                  return range[0] + "-" + range[1] + " 共" + total + "条"
              },
              showQuickJumper: true,
              showSizeChanger: true,
              total: 0
          },
          /* 排序参数 */
          isorter:{
              column: 'createTime',
              order: 'desc',
          },
          /* 筛选参数 */
          filters: {},
          /* table加载状态 */
          loading:false,
          /* table选中keys*/
          selectedRowKeys: [],
          /* table选中records*/
          selectionRows: [],
          /* 查询折叠 */
          toggleSearchStatus:false,
          /* 高级查询条件生效状态 */
          superQueryFlag:false,
          /* 高级查询条件 */
          superQueryParams:"",
        description: '报修单详情管理页面',
        // 表头
        columns: [
          {
            title: '#',
            dataIndex: '',
            key:'rowIndex',
            width:60,
            align:"center",
            customRender:function (t,r,index) {
              return parseInt(index)+1;
            }
           },
		   {
            title: '接受时间',
            align:"center",
            dataIndex: 'accTime'
           },
		   {
            title: '报修地址',
            align:"center",
           dataIndex: 'adress',
           scopedSlots: {customRender: 'adress'},
           },
		   {
            title: '报修内容',
            align:"center",
           dataIndex: 'cont',
           scopedSlots: {customRender: 'cont'},
           },
		   {
            title: '评论内容',
            align:"center",
           dataIndex: 'evalCont',
           scopedSlots: {customRender: 'evalCont'},
           },
		   {
            title: '满意度，满分10',
            align:"center",
            dataIndex: 'evalDegree'
           },
		   {
            title: '报修人姓名',
            align:"center",
            dataIndex: 'name'
           },
		   {
            title: '维修完成时间',
            align:"center",
            dataIndex: 'overTime'
           },
		   {
            title: '图片',
            align:"center",
            dataIndex: 'pic'
           },
		   {
            title: '派工时间',
            align:"center",
            dataIndex: 'sendTime'
           },
		   {
            title: '报修状态',
            align:"center",
            dataIndex: 'stateTag'
           },
		   {
            title: '报修人电话',
            align:"center",
            dataIndex: 'tel'
           },
		   {
            title: '报修标题',
            align:"center",
            dataIndex: 'title'
           },
		   {
            title: '备注',
            align:"center",
            dataIndex: 'vistBz'
           },
		   {
            title: '备注时间',
            align:"center",
            dataIndex: 'vistTime'
           },
          {
            title: '操作',
            dataIndex: 'action',
            align:"center",
            scopedSlots: { customRender: 'action' },
          }
        ],
		url: {
          list: "/repairs/repairInfo/list",
          delete: "/repairs/repairInfo/delete",
          deleteBatch: "/repairs/repairInfo/deleteBatch",
          exportXlsUrl: "repairs/repairInfo/exportXls",
          importExcelUrl: "repairs/repairInfo/importExcel",
       },
    }
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    }
  },
  created() {
      this.loadData();
      //初始化字典配置 在自己页面定义
      this.initDictConfig();
  },
  methods: {
      loadData(arg) {
          if(!this.url.list){
              this.$message.error("请设置url.list属性!")
              return
          }
          //加载数据 若传入参数1则加载第一页的内容
          if (arg === 1) {
              this.ipagination.current = 1;
          }
          var params = this.getQueryParams();//查询条件
          this.loading = true;
          getAction(this.url.list, params).then((res) => {
              if (res.success) {
                  this.dataSource = res.result.records;
                  this.ipagination.total = res.result.total;
                  alert(JSON.stringify(this.dataSource))
              }
              if(res.code===510){
                  this.$message.warning(res.message)
              }
              this.loading = false;
          })
      },
      initDictConfig(){
          // console.log("--这是一个假的方法!")
      },
      handleSuperQuery(arg) {
          //高级查询方法
          if(!arg){
              this.superQueryParams=''
              this.superQueryFlag = false
          }else{
              this.superQueryFlag = true
              this.superQueryParams=JSON.stringify(arg)
          }
          this.loadData()
      },
      getQueryParams() {
          //获取查询条件
          let sqp = {}
          if(this.superQueryParams){
              sqp['superQueryParams']=encodeURI(this.superQueryParams)
          }
          var param = Object.assign(sqp, this.queryParam, this.isorter ,this.filters);
          param.field = this.getQueryField();
          param.pageNo = this.ipagination.current;
          param.pageSize = this.ipagination.pageSize;
          return filterObj(param);
      },
      getQueryField() {
          //TODO 字段权限控制
          var str = "id,";
          this.columns.forEach(function (value) {
              str += "," + value.dataIndex;
          });
          return str;
      },

      onSelectChange(selectedRowKeys, selectionRows) {
          this.selectedRowKeys = selectedRowKeys;
          this.selectionRows = selectionRows;
      },
      onClearSelected() {
          this.selectedRowKeys = [];
          this.selectionRows = [];
      },
      searchQuery() {
          this.loadData(1);
      },
      superQuery() {
          this.$refs.superQueryModal.show();
      },
      searchReset() {
          this.queryParam = {}
          this.loadData(1);
      },
      batchDel: function () {
          if(!this.url.deleteBatch){
              this.$message.error("请设置url.deleteBatch属性!")
              return
          }
          if (this.selectedRowKeys.length <= 0) {
              this.$message.warning('请选择一条记录！');
              return;
          } else {
              var ids = "";
              for (var a = 0; a < this.selectedRowKeys.length; a++) {
                  ids += this.selectedRowKeys[a] + ",";
              }
              var that = this;
              this.$confirm({
                  title: "确认删除",
                  content: "是否删除选中数据?",
                  onOk: function () {
                      deleteAction(that.url.deleteBatch, {ids: ids}).then((res) => {
                          if (res.success) {
                              that.$message.success(res.message);
                              that.loadData();
                              that.onClearSelected();
                          } else {
                              that.$message.warning(res.message);
                          }
                      });
                  }
              });
          }
      },
      handleDelete: function (id) {
          if(!this.url.delete){
              this.$message.error("请设置url.delete属性!")
              return
          }
          var that = this;
          deleteAction(that.url.delete, {id: id}).then((res) => {
              if (res.success) {
                  that.$message.success(res.message);
                  that.loadData();
              } else {
                  that.$message.warning(res.message);
              }
          });
      },
      handleEdit: function (record) {
          this.$refs.modalForm.edit(record);
          this.$refs.modalForm.title = "编辑";
          this.$refs.modalForm.disableSubmit = false;
      },
      handleAdd: function () {
          this.$refs.modalForm.add();
          this.$refs.modalForm.title = "新增";
          this.$refs.modalForm.disableSubmit = false;
      },
      handleTableChange(pagination, filters, sorter) {
          //分页、排序、筛选变化时触发
          //TODO 筛选
          if (Object.keys(sorter).length > 0) {
              this.isorter.column = sorter.field;
              this.isorter.order = "ascend" == sorter.order ? "asc" : "desc"
          }
          this.ipagination = pagination;
          this.loadData();
      },
      handleToggleSearch(){
          this.toggleSearchStatus = !this.toggleSearchStatus;
      },
      modalFormOk() {
          // 新增/修改 成功时，重载列表
          this.loadData();
      },
      handleDetail:function(record){
          this.$refs.modalForm.edit(record);
          this.$refs.modalForm.title="详情";
          this.$refs.modalForm.disableSubmit = true;
      },
      /* 导出 */
      handleExportXls2(){
          let paramsStr = encodeURI(JSON.stringify(this.getQueryParams()));
          let url = `${window._CONFIG['domianURL']}/${this.url.exportXlsUrl}?paramsStr=${paramsStr}`;
          window.location.href = url;
      },
      handleExportXls(fileName){
          if(!fileName || typeof fileName != "string"){
              fileName = "导出文件"
          }
          let param = {...this.queryParam};
          if(this.selectedRowKeys && this.selectedRowKeys.length>0){
              param['selections'] = this.selectedRowKeys.join(",")
          }
          console.log("导出参数",param)
          downFile(this.url.exportXlsUrl,param).then((data)=>{
              if (!data) {
                  this.$message.warning("文件下载失败")
                  return
              }
              if (typeof window.navigator.msSaveBlob !== 'undefined') {
                  window.navigator.msSaveBlob(new Blob([data]), fileName+'.xls')
              }else{
                  let url = window.URL.createObjectURL(new Blob([data]))
                  let link = document.createElement('a')
                  link.style.display = 'none'
                  link.href = url
                  link.setAttribute('download', fileName+'.xls')
                  document.body.appendChild(link)
                  link.click()
                  document.body.removeChild(link); //下载完成移除元素
                  window.URL.revokeObjectURL(url); //释放掉blob对象
              }
          })
      },
      /* 导入 */
      handleImportExcel(info){
          if (info.file.status !== 'uploading') {
              console.log(info.file, info.fileList);
          }
          if (info.file.status === 'done') {
              if(info.file.response.success){
                  this.$message.success(`${info.file.name} 文件上传成功`);
                  this.loadData();
              } else {
                  this.$message.error(`${info.file.name} ${info.file.response.message}.`);
              }
          } else if (info.file.status === 'error') {
              this.$message.error(`文件上传失败: ${info.file.msg} `);
          }
      },
      /* 图片预览 */
      getImgView(text){
          if(text && text.indexOf(",")>0){
              text = text.substring(0,text.indexOf(","))
          }
          return window._CONFIG['imgDomainURL']+"/"+text
      },
      /* 文件下载 */
      uploadFile(text){
          if(!text){
              this.$message.warning("未知的文件")
              return;
          }
          if(text.indexOf(",")>0){
              text = text.substring(0,text.indexOf(","))
          }
          window.open(window._CONFIG['domianURL'] + "/sys/common/download/"+text);
      },
      toOpreaList: function() {
          this.$router.push({ path: '/dashboard/analysis' })
      }

  }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less';
  table{
    table-layout:fixed;
  }
</style>