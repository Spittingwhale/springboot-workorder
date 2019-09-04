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
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        height="200"
        :scroll="{ x: 1500, y: 900 }"
        :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange}"
        @change="handleTableChange">

        <template slot="picslot" slot-scope="text, record, index">
          <div class="anty-img-wrap">
            <a-avatar shape="square" :src="getAvatarView(record.pic)" />
          </div>
        </template>

        <!-- 字符串超长截取省略号显示-->
        <span slot="cont" slot-scope="text">
          <j-ellipsis v-html=" text.length>500 ? '[图片]':text" :length="25" />
        </span>

        <span slot="action" slot-scope="text, record">
<!--          <a @click="handleEdit(record)">编辑</a>-->
          <a @click="toOpreaList()">hello</a>
<!--        <a slot="action" slot-scope="text" href="javascript:;">action</a>-->
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
                //token header
                // tokenHeader: {'X-Access-Token': Vue.ls.get(ACCESS_TOKEN)},

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
                        width:60,
                        dataIndex: 'accTime'
                    },
                    {
                        title: '报修地址',
                        align:"center",
                        width:60,
                        dataIndex: 'adress',
                        scopedSlots: {customRender: 'adress'},
                    },
                    {
                        title: '报修内容',
                        align:"center",
                        dataIndex: 'cont',
                        width: 60,
                        scopedSlots: { customRender: 'cont' },
                    },
                    {
                        title: '评论内容',
                        align:"center",
                        width:60,
                        dataIndex: 'evalCont',
                        scopedSlots: {customRender: 'evalCont'},
                    },
                    {
                        title: '满意度，满分10',
                        align:"center",
                        width:60,
                        dataIndex: 'evalDegree'
                    },
                    {
                        title: '报修人姓名',
                        align:"center",
                        width:60,
                        dataIndex: 'name'
                    },
                    {
                        title: '维修完成时间',
                        align:"center",
                        width:60,
                        dataIndex: 'overTime'
                    },
                    {
                        title: '图片',
                        align:"center",
                        width:60,
                        dataIndex: 'pic',
                        scopedSlots: {customRender: "picslot"}
                    },
                    {
                        title: '派工时间',
                        align:"center",
                        width:60,
                        dataIndex: 'sendTime'
                    },
                    {
                        title: '报修状态',
                        align:"center",
                        width:60,
                        dataIndex: 'stateTag'
                    },
                    {
                        title: '报修人电话',
                        align:"center",
                        width:60,
                        dataIndex: 'tel'
                    },
                    {
                        title: '报修标题',
                        align:"center",
                        width:60,
                        dataIndex: 'title'
                    },
                    {
                        title: '备注',
                        align:"center",
                        width:60,
                        dataIndex: 'vistBz'
                    },
                    {
                        title: '备注时间',
                        align:"center",
                        width:60,
                        dataIndex: 'vistTime'
                    },
                    {
                        title: '操作',
                        dataIndex: 'action',
                        align:"center",
                        fixed: 'right',
                        width:150,
                        scopedSlots: { customRender: 'action' },
                    }
                ],
                url: {
                    imgerver: window._CONFIG['domianURL'] + "/sys/common/view",
                    fileUpload: window._CONFIG['domianURL'] + "/sys/common/upload",
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