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
          <a-col :md="6" :sm="8" >
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
      <a-upload name="file" :showUploadList="false" :multiple="false" :headers="tokenHeader" :action="importExcelUrl" @change="handleImportExcel">
        <a-button type="primary" icon="import">导入</a-button>
      </a-upload>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px"> 批量操作 <a-icon type="down" /></a-button>
      </a-dropdown>
    </div>

    <!-- table区域-begin -->
    <div>
      <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
        <i class="anticon anticon-info-circle ant-alert-icon"></i> 已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a>项
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
        :scroll="{ x: 1500}"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>

          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
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
  import RepairInfoModal from './modules/RepairInfoModal4-1'
  import { JeecgListMixin } from '@/mixins/JeecgListMixin'

  export default {
    name: "RepairInfoList",
    mixins:[JeecgListMixin],
    components: {
      RepairInfoModal
    },
    data () {
      return {
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
            dataIndex: 'adress'
           },
		   {
            title: '报修内容',
            align:"center",
            dataIndex: 'cont'
           },
		   {
            title: '评论内容',
            align:"center",
            dataIndex: 'evalCont'
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
          list: "/repairs/repairInfo/list/0",
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
    methods: {
     
    }
  }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>