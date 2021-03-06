/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.batch;

import org.junit.Test;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.test.IsMeasure;

import java.util.Arrays;
import java.util.List;

public class AbstractSumChildrenDecoratorTest {

  @Test
  public void sumChildren() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getChildrenMeasures(CoreMetrics.LINES)).thenReturn(Arrays.<Measure>asList(
        new Measure(CoreMetrics.LINES, 100.0),
        new Measure(CoreMetrics.LINES, 50.0)));

    create(false).decorate(null, context);

    verify(context).saveMeasure(argThat(new IsMeasure(CoreMetrics.LINES, 150.0)));
  }

  @Test
  public void doNotSaveZeroIfNoChildren() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getChildrenMeasures(CoreMetrics.LINES)).thenReturn(Arrays.<Measure>asList());

    create(false).decorate(null, context);

    verify(context, never()).saveMeasure((Measure) anyObject());
  }

  @Test
  public void saveZeroIfNoChildren() {
    DecoratorContext context = mock(DecoratorContext.class);
    when(context.getChildrenMeasures(CoreMetrics.LINES)).thenReturn(Arrays.<Measure>asList());

    create(true).decorate(null, context);

    verify(context).saveMeasure(argThat(new IsMeasure(CoreMetrics.LINES, 0.0)));
  }

  private AbstractSumChildrenDecorator create(final boolean zeroIfNoChildMeasures) {
    return new AbstractSumChildrenDecorator() {

      @Override
      @DependedUpon
      public List<Metric> generatesMetrics() {
        return Arrays.asList(CoreMetrics.LINES);
      }

      @Override
      protected boolean shouldSaveZeroIfNoChildMeasures() {
        return zeroIfNoChildMeasures;
      }
    };
  }
}
