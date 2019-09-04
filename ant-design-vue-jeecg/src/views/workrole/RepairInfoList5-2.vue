<!--用户提交报修单已完成-->
<template>
  <a-card :bordered="false">

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
        class="hello1"
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :scroll="{ x: 800}"
        @change="handleTableChange">
        <!--:rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"-->

        <template slot="picslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.pic)" />
          </div>
        </template>

         <!--字符串超长截取省略号显示-->
        <span slot="columns" slot-scope="text">
          <j-ellipsis :value="text" :length="25" />
        </span>

        <!--<span slot="action" slot-scope="text, record">
          <a style="color: #FFC540" @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical"/>
&lt;!&ndash;          <a @click="handleEditState(record,1)">提交</a>&ndash;&gt;
          <a-popconfirm title="确定提交吗?" @confirm="() => handleEditState(record.id,1)">
            <a style="color: blue;">提交</a>
          </a-popconfirm>
&lt;!&ndash;          <a @click="toOpreaList()">hello</a>&ndash;&gt;
          <a-divider type="vertical"/>
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
            <a style="color: red;">删除</a>
          </a-popconfirm>
        </span>-->

      </a-table>
    </div>
    <!-- table区域-end -->

    <!-- 表单区域 -->
    <repairInfo-modal ref="modalForm" @ok="modalFormOk"></repairInfo-modal>
  </a-card>
</template>

<script>
    import RepairInfoModal from './modules/RepairInfoModal5-2'
    import JEllipsis from "@/components/jeecg/JEllipsis";
    import ImagPreview from "../jeecg/ImagPreview";
    import { JeecgListMixin } from '@/mixins/JeecgListMixin'

    export default {
        name: "RepairInfoList",
        mixins:[JeecgListMixin],
        components: {
            ImagPreview,
            JEllipsis,
            RepairInfoModal
        },
        data () {
            return {
                uploadLoading:false,
                confirmLoading: false,
                headers:{},
                description: '报修单详情管理页面',
                // 表头
                columns: [
                    {
                        title: '#',
                        dataIndex: '',
                        key:'rowIndex',
                        width:20,
                        fixed: 'left',
                        align:"center",
                        customRender:function (t,r,index) {
                            return parseInt(index)+1;
                        }
                    },
                    {
                        title: '报修地址',
                        align:"center",
                        dataIndex: 'adress',
                        width: 120,
                        scopedSlots: {customRender: 'adress'},
                    },
                    {
                        title: '报修标题',
                        align:"center",
                        width: 120,
                        dataIndex: 'title'
                    },
                    {
                        title: '报修内容',
                        align:"center",
                        width: 120,
                        dataIndex: 'cont',
                        scopedSlots: { customRender: 'cont' },
                    },
                    {
                        title: '图片',
                        width:100,
                        align:"center",
                        dataIndex: 'pic',
                        scopedSlots: {customRender: "picslot"}
                    },
                    {
                        title: '联系方式',
                        align:"center",
                        width:60,
                        dataIndex: 'tel'
                    },
                    {
                        title: '报修状态',
                        align:"center",
                        width:60,
                        dataIndex: 'stateTag',
                        customRender: (text,r,index) => {
                            let re = "";
                            if (text === 0) {
                                re = "待提交";
                            } else if (text === 1) {
                                re = "报修";
                            } else if (text === 2) {
                                re = "接收";
                            } else if (text === 3) {
                                re = "维修中";
                            } else if (text === 4) {
                                re = "维修完毕";
                            }
                            return '<h1 style="color:red">维修完毕</h1>';
                        }
                    },
                    {
                        title: '接受时间',
                        align:"center",
                        width:60,
                        dataIndex: 'accTime'
                    },
                    {
                        title: '维修完成时间',
                        align:"center",
                        width:60,
                        dataIndex: 'overTime'
                    },
                    {
                        title: '备注',
                        align:"center",
                        width:60,
                        dataIndex: 'vistBz'
                    },
                    /*{
                        title: '操作',
                        dataIndex: 'action',
                        align:"center",
                        fixed: 'right',
                        width:150,
                        scopedSlots: { customRender: 'action' },
                    },*/

                ],
                url: {
                    imgerver: window._CONFIG['domianURL'] + "/sys/common/view",
                    fileUpload: window._CONFIG['domianURL'] + "/sys/common/upload",
                    list: "/repairs/repairInfo/list/4",
                    delete: "/repairs/repairInfo/delete",
                    editState: "/repairs/repairInfo/editStat",
                    deleteBatch: "/repairs/repairInfo/deleteBatch",
                    exportXlsUrl: "repairs/repairInfo/exportXls",
                    importExcelUrl: "repairs/repairInfo/importExcel",
                },
            }
        },
        computed: {
            importExcelUrl: function(){
                return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
            },
            uploadAction:function () {
                return this.url.fileUpload;
            }
        },
        methods: {
            getAvatarView: function (avatar) {
                return this.url.imgerver + "/" + avatar;
            },


        }
    }
</script>
<style scoped>
  @import '~@assets/less/common.less'
</style>