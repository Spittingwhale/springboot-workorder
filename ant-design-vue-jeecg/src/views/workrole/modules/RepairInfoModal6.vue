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
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修地址">
          <a-input disabled style="color:black" placeholder="请输入报修地址" v-decorator="['adress', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修标题">
          <a-input disabled placeholder="请输入报修标题" v-decorator="['title', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修内容">
          <a-input disabled placeholder="请输入报修内容" v-decorator="['cont', {}]" />
        </a-form-item>
        <!--<a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="图片">
          <a-input disabled placeholder="请输入图片" v-decorator="['pic', {}]" />
        </a-form-item>-->
        <a-form-item label="图片" :labelCol="labelCol" :wrapperCol="wrapperCol" >
          <!--<a-upload disabled listType="picture-card" :data="{'isup':1}" >
            <img :src="getAvatarView()" alt="图片" style="height:208px;max-width:600px"/>
          </a-upload>-->
<!--            <div style="float:left;height:104px;max-width:180px">-->
              <div
                style="width: 100%;height: 100%;position: relative;padding: 8px;border: 1px solid #d9d9d9;border-radius: 4px;">
                <img style="width: 100%;" :src="getAvatarView()"  :preview="0" alt="图片" >
              </div>
<!--            </div>-->
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="维修单状态">
          <a-input disabled placeholder="维修单状态" v-decorator="['stateTag', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="修理工姓名">
          <a-input disabled placeholder="请输入修理工姓名" v-decorator="['staffName', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="报修提交时间">
          <a-date-picker disabled showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'sendTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="修理工接单时间">
          <a-date-picker disabled showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'accTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="维修完成时间">
          <a-date-picker disabled showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'overTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="开始维修时间">
          <a-date-picker disabled showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'startTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注">
          <a-input disabled placeholder="请输入备注" v-decorator="['vistBz', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="备注时间">
          <a-date-picker disabled showTime format='YYYY-MM-DD HH:mm:ss' v-decorator="[ 'vistTime', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="满意度（满分10）">
          <a-input-number disabled v-decorator="[ 'evalDegree', {}]" />
        </a-form-item>
        <a-form-item
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          label="评论内容">
          <a-input disabled placeholder="请输入评论内容" v-decorator="['evalCont', {}]" />
        </a-form-item>

      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
    import { httpAction } from '@/api/manage'
    import pick from 'lodash.pick'
    import moment from "moment"

    export default {
        name: "RepairInfoModal",
        data () {
            return {
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
                    imgerver: window._CONFIG['domianURL'] + "/sys/common/view",
                    add: "/repairs/repairInfo/add",
                    edit: "/repairs/repairInfo/edit",
                },
            }
        },
        created () {
        },
        methods: {
            getAvatarView(){
                return this.url.imgerver +"/"+ this.model.pic;
            },
            add () {
                this.edit({});
            },
            edit (record) {
                this.form.resetFields();
                this.model = Object.assign({}, record);
                this.visible = true;
                this.$nextTick(() => {
                    this.form.setFieldsValue(pick(this.model,'adress','cont','evalCont','evalDegree','name','pic','staffId','staffName','stateTag','tel','title','userId','vistBz'))
                    //时间格式化
                    this.form.setFieldsValue({accTime:this.model.accTime?moment(this.model.accTime):null})
                    this.form.setFieldsValue({overTime:this.model.overTime?moment(this.model.overTime):null})
                    this.form.setFieldsValue({sendTime:this.model.sendTime?moment(this.model.sendTime):null})
                    this.form.setFieldsValue({startTime:this.model.startTime?moment(this.model.startTime):null})
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
                        formData.startTime = formData.startTime?formData.startTime.format('YYYY-MM-DD HH:mm:ss'):null;
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
  .ant-spin-nested-loading input{
    color: black;
  }
</style>