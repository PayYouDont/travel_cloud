/**   
* @Title: GsonIgnore.java 
* @Package com.gospell.chitong.rdcenter.broadcast.commonManage.annontation 
* @Description: TODO(     ) 
* @author peiyongdong  
* @date 2019年1月23日 下午3:42:09 
*/
package com.gospell.travel.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
* @ClassName: GsonIgnore 
* @Description: TODO(  忽略字段序列化   ) 
* @author peiyongdong
* @date 2019年1月23日 下午3:42:09 
*  
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE,ElementType.FIELD})
@Documented
@Inherited  //可以继承
public @interface GsonIgnore {

}
