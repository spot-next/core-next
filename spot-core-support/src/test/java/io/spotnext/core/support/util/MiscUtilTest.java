package io.spotnext.core.support.util;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.spotnext.support.util.MiscUtil;

public class MiscUtilTest {

	@Test
	public void testNullsafeGetter() {
		String test = null;

		Optional<Integer> length = MiscUtil.$(() -> test.length());

		Assertions.assertTrue(!length.isPresent());
	}

	@Test
	public void testNullsafeGetterWithDefaultValue() {
		String test = null;

		Integer length = MiscUtil.$(() -> test.length(), 0);

		Assertions.assertEquals(Integer.valueOf(0), length);
	}
	
	@Test
	public void testWithHelper() {
//		BaseOpeningDay day = new OpeningDay();
//
//		MiscUtil.with(day, OpeningDay.class, d -> {
//			d.setWeekday(DayOfWeek.SUNDAY);
//		});
//
//		assertEquals(DayOfWeek.SUNDAY, ((OpeningDay) day).getWeekday());
	}
}
