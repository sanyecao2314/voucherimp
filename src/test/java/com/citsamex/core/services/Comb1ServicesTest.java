package com.citsamex.core.services;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Test;

public class Comb1ServicesTest {

	@Test
	public void testConvertFile2VO() {
		BigDecimal taxAmount = new BigDecimal(743778.78).multiply(new BigDecimal(0.06)).divide(new BigDecimal(1.06), 2, 4);
		System.out.println(taxAmount);
		System.out.println(UUID.randomUUID());
	}

}
