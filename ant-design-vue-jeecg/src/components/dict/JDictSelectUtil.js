/**
 * 字典 util
 * author: scott
 * date: 20190109
 */

import {ajaxGetDictItems} from '@/api/api'
import {getAction} from '@/api/manage'

/**
 * 获取字典数组
 * @param dictCode 字典Code
 * @return List<Map>
 */
export async function initDictOptions(dictCode) {
  if (!dictCode) {
    return '字典Code不能为空!';
  }
  //获取字典数组
  let res = await ajaxGetDictItems(dictCode);
  return res;
}

/**
 * 字典值替换文本通用方法
 * @param dictOptions  字典数组
 * @param text  字典值
 * @return String
 */
export function filterDictText(dictOptions, text) {
  let re = "";
  dictOptions.forEach(function (option) {
    if (text === option.value) {
      re = option.text;
    }
  });
  return re;
}

/**
 * 字典值替换文本通用方法(多选)
 * @param dictOptions  字典数组
 * @param text  字典值
 * @return String
 */
export function filterMultiDictText(dictOptions, text) {
  if(!text){
    return ""
  }
  let re = "";
  let arr = text.split(",")
  dictOptions.forEach(function (option) {
    for(let i=0;i<arr.length;i++){
      if (arr[i] === option.value) {
        re += option.text+",";
        break;
      }
    }
  });
  if(re==""){
    return "";
  }
  return re.substring(0,re.length-1);
}

/**
 * 翻译字段值对应的文本
 * @param children
 * @returns string
 */
export async function ajaxFilterDictText(dictCode, key) {
  if (!dictCode) {
    return '字典Code不能为空!';
  }
  //console.log(`key : ${key}`);
  if (!key) {
    return '';
  }
  //通过请求读取字典文本
  let res = await getAction(`/sys/dict/getDictText/${dictCode}/${key}`);
  if (res.success) {
    // console.log('restult: '+ res.result);
    return res.result;
  } else {
    return '';
  }
}