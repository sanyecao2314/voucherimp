package com.citsamex.core.util;

import java.util.HashMap;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Test;

public class DBUtilTest {
	
	SoftAssertions soft = new SoftAssertions();
	
	@Test
	public void testQuerySqlUniqueResult() throws Exception {
		Object obj = DBUtil.querySqlUniqueResult("select 1");
		soft.assertThat(obj).isEqualTo(1);
		
		String sql = "select  min(FDetailID) as FDetailID from t_ItemDetail where FDetailCount=3 and F2=511 and F3=512 and F2039=622";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1 && ((HashMap)templist.get(0)).get("FDetailID") != null){
			String fDetailID = ((HashMap)templist.get(0)).get("FDetailID").toString();
		}else if(templist == null || templist.size() == 0){
			System.out.println("no data");
		}
	}

	@After
	public void after(){
		soft.assertAll();
	}
}
