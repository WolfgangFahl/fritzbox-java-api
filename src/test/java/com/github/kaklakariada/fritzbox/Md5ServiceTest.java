/**
 * A Java API for managing FritzBox HomeAutomation
 * Copyright (C) 2017 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.kaklakariada.fritzbox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for {@link Md5Service}
 */
public class Md5ServiceTest {

  @Test
  public void testMd5EmptyString() {
    assertMd5Sum("", "d41d8cd98f00b204e9800998ecf8427e");
  }

  @Test
  public void testMd5Test() {
    assertMd5Sum("test", "c8059e2ec7419f590e79d7f1b774bfe6");
  }

  @Test
  public void testMd5Umlauts() {
    assertMd5Sum("1234567z-äbc", "9e224a41eeefa284df7bb0f26c2913e2");
  }

  @Test
  public void testMd5SpecialChars() {
    assertMd5Sum("!\"§$%&/()=?ßüäöÜÄÖ-.,;:_`´+*#'<>≤|",
        "ad44a7cb10a95cb0c4d7ae90b0ff118a");
  }

  /**
   * check the given input to correspond to the given expected md5
   * 
   * @param input
   * @param expectedMd5
   */
  private void assertMd5Sum(String input, String expectedMd5) {
    assertEquals(expectedMd5, new Md5Service().md5(input));
  }
}
