package io.spotnext.core.support.util;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import io.spotnext.support.util.MiscUtil;

public class MiscUtilTest {

	@Test
	public void testNullsafeGetter() {
		String test = null;

		Optional<Integer> length = MiscUtil.$(() -> test.length());

		Assert.assertTrue(!length.isPresent());
	}

	@Test
	public void testNullsafeGetterWithDefaultValue() {
		String test = null;

		Integer length = MiscUtil.$(() -> test.length(), 0);

		Assert.assertEquals(Integer.valueOf(0), length);
	}
}
