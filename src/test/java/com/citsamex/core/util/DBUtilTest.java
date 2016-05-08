package com.citsamex.core.util;

import static org.junit.Assert.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Test;

public class DBUtilTest {
	
	SoftAssertions soft = new SoftAssertions();
	
	@Test
	public void testQuerySqlUniqueResult() throws Exception {
		Object obj = DBUtil.querySqlUniqueResult("select 1");
		soft.assertThat(obj).isEqualTo(1);
	}

	@After
	public void after(){
		soft.assertAll();
	}
}
