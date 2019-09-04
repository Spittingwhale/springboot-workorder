<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
      
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="接受时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'accTime', {}]" />
        </a-form-item>-->
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修地址">
          <a-input placeholder="请输入报修地址" v-decorator="['adress', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修标题">
          <a-input placeholder="请输入报修标题" v-decorator="['title', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="{ xs: { span: 128 }, sm: { span: 5 } }"
          :wrapperCol="{ xs: { span: 128 }, sm: { span: 32 } }"
          label="报修内容">
          <textarea placeholder="请输入报修内容" v-decorator="['cont', {}]" :style="width=500" />
        </a-form-item>
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="内容">
          <j-editor v-decorator="[ 'cont', {} ]" triggerChange></j-editor>
        </a-form-item>-->
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="评论内容">
          <a-input placeholder="请输入评论内容" v-decorator="['evalCont', {}]" />
        </a-form-item>-->
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="满意度，满分10">
          <a-input-number v-decorator="[ 'evalDegree', {}]" />
        </a-form-item>-->
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修人姓名">
          <a-input placeholder="请输入报修人姓名" v-decorator="['name', {}]" />
        </a-form-item>-->
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="维修完成时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'overTime', {}]" />
        </a-form-item>-->
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片">
          <a-input placeholder="请输入图片" v-decorator="['pic', {}]" />
        </a-form-item>-->
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修人电话">
          <a-input placeholder="请输入报修人电话" v-decorator="['tel', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注">
          <a-input placeholder="请输入备注" v-decorator="['vistBz', {}]" />
        </a-form-item>
        <a-form-item label="图片" :labelCol="labelCol" :wrapperCol="wrapperCol">
          <a-upload
            listType="picture-card"
            class="avatar-uploader"
            :showUploadList="false"
            :action="uploadAction"
            :data="{'isup':1}"
            :headers="headers"
            :beforeUpload="beforeUpload"
            @change="handleChange"
          >
            <img v-if="picUrl" :src="getAvatarView()" alt="图片" style="height:104px;max-width:300px"/>
            <div v-else>
              <a-icon :type="uploadLoading ? 'loading' : 'plus'" />
              <div class="ant-upload-text">上传</div>
            </div>
          </a-upload>
        </a-form-item>
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="派工时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'sendTime', {}]" />
        </a-form-item>-->
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修状态">
          <a-input placeholder="报修状态" v-decorator="['stateTag', {}]" />
        </a-form-item>-->
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注时间">
          <a-date-picker showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'vistTime', {}]" />
        </a-form-item>-->
		
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
  import { ACCESS_TOKEN } from "@/store/mutation-types"
  import { httpAction } from '@/api/manage'
  import pick from 'lodash.pick'
  import moment from 'moment'
  import Vue from 'vue'
  import JEditor from '@/components/jeecg/JEditor'

  export default {
    components: { JEditor},
    name: "RepairInfoModal",
    data () {
      return {
        headers:{},
        uploadLoading:false,
        confirmLoading: false,
        picUrl: "",
        title:"操作",
        visible: false,
        model: {},
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 },
        },

        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules:{
        },
        url: {
          fileUpload: window._CONFIG['domianURL']+"/sys/common/upload",
          imgerver: window._CONFIG['domianURL']+"/sys/common/view",
          add: "/repairs/repairInfo/add",
          edit: "/repairs/repairInfo/edit",
        },
      }
    },
    created () {
        const token = Vue.ls.get(ACCESS_TOKEN);
        this.headers = {"X-Access-Token":token}
    },
    computed:{
        uploadAction:function () {
            return this.url.fileUpload;
        }
    },
    methods: {
        //窗口最大化切换
      toggleScreen(){
          if(this.modaltoggleFlag){
              this.modalWidth = window.innerWidth;
          }else{
              this.modalWidth = 800;
          }
          this.modaltoggleFlag = !this.modaltoggleFlag;
      },
      beforeUpload: function(file){
          var fileType = file.type;
          if(fileType.indexOf('image')<0){
              this.$message.warning('请上传图片');
              return false;
          }
          //TODO 验证文件大小
      },
      handleChange (info) {
          this.picUrl = "";
          if (info.file.status === 'uploading') {
              this.uploadLoading = true;
              return
          }
          if (info.file.status === 'done') {
              var response = info.file.response;
              this.uploadLoading = false;
              console.log(response);
              if(response.success){
                  this.model.pic = response.message;
                  this.picUrl = "Has no pic url yet";
              }else{
                  this.$message.warning(response.message);
              }
          }
      },

      getAvatarView(){
          return this.url.imgerver +"/"+ this.model.pic;
      },
      add () {
        this.picUrl = "";
        this.edit({});
      },
      edit (record) {
        this.form.resetFields();
        this.model = Object.assign({}, record);
        this.visible = true;
        if (this.model.pic) {
          this.picUrl = "Has no pic url yet";
        }
        this.$nextTick(() => {//vistBz,overTime,accTime,stateTag,tel,pic,cont,title,adress,center
        this.form.setFieldsValue(pick(this.model,'adress','cont','evalCont','name','pic','tel','title','vistBz'))
    //时间格式化
        this.form.setFieldsValue({accTime:this.model.accTime?moment(this.model.accTime):null})
        this.form.setFieldsValue({overTime:this.model.overTime?moment(this.model.overTime):null})
        this.form.setFieldsValue({sendTime:this.model.sendTime?moment(this.model.sendTime):null})
        this.form.setFieldsValue({vistTime:this.model.vistTime?moment(this.model.vistTime):null})
      });
      },
      close () {
        this.$emit('close');
        this.visible = false;
      },
      handleOk () {
        const that = this;
        // 触发表单验证
        this.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true;
            let httpurl = '';
            let method = '';
            if(!this.model.id){
              httpurl+=this.url.add;
              method = 'post';
            }else{
              httpurl+=this.url.edit;
               method = 'put';
            }
            let formData = Object.assign(this.model, values);
            //时间格式化
            formData.accTime = formData.accTime?formData.accTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.overTime = formData.overTime?formData.overTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.sendTime = formData.sendTime?formData.sendTime.format('YYYY-MM-DD HH:mm:ss'):null;
            formData.vistTime = formData.vistTime?formData.vistTime.format('YYYY-MM-DD HH:mm:ss'):null;
            
            console.log(formData)
            httpAction(httpurl,formData,method).then((res)=>{
              if(res.success){
                that.$message.success(res.message);
                that.$emit('ok');
              }else{
                that.$message.warning(res.message);
              }
            }).finally(() => {
              that.confirmLoading = false;
              that.close();
            })
          }
        })
      },
      handleCancel () {
        this.close()
      },


    }
  }
</script>

<style lang="less" scoped>
  .ant-form-item-children textarea{
    width: 60%;
  }
</style>