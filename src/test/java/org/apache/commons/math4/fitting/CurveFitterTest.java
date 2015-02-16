/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.fitting;

import org.apache.commons.math4.analysis.ParametricUnivariateFunction;
import org.apache.commons.math4.fitting.CurveFitter;
import org.apache.commons.math4.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;
import org.apache.commons.math4.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

@Deprecated
public class CurveFitterTest {
    @Test
    public void testMath303() {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter<ParametricUnivariateFunction> fitter = new CurveFitter<ParametricUnivariateFunction>(optimizer);
        fitter.addObservedPoint(2.805d, 0.6934785852953367d);
        fitter.addObservedPoint(2.74333333333333d, 0.6306772025518496d);
        fitter.addObservedPoint(1.655d, 0.9474675497289684);
        fitter.addObservedPoint(1.725d, 0.9013594835804194d);

        ParametricUnivariateFunction sif = new SimpleInverseFunction();

        double[] initialguess1 = new double[1];
        initialguess1[0] = 1.0d;
        Assert.assertEquals(1, fitter.fit(sif, initialguess1).length);

        double[] initialguess2 = new double[2];
        initialguess2[0] = 1.0d;
        initialguess2[1] = .5d;
        Assert.assertEquals(2, fitter.fit(sif, initialguess2).length);
    }

    @Test
    public void testMath304() {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter<ParametricUnivariateFunction> fitter = new CurveFitter<ParametricUnivariateFunction>(optimizer);
        fitter.addObservedPoint(2.805d, 0.6934785852953367d);
        fitter.addObservedPoint(2.74333333333333d, 0.6306772025518496d);
        fitter.addObservedPoint(1.655d, 0.9474675497289684);
        fitter.addObservedPoint(1.725d, 0.9013594835804194d);

        ParametricUnivariateFunction sif = new SimpleInverseFunction();

        double[] initialguess1 = new double[1];
        initialguess1[0] = 1.0d;
        Assert.assertEquals(1.6357215104109237, fitter.fit(sif, initialguess1)[0], 1.0e-14);

        double[] initialguess2 = new double[1];
        initialguess2[0] = 10.0d;
        Assert.assertEquals(1.6357215104109237, fitter.fit(sif, initialguess1)[0], 1.0e-14);
    }

    @Test
    public void testMath372() {
        LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
        CurveFitter<ParametricUnivariateFunction> curveFitter = new CurveFitter<ParametricUnivariateFunction>(optimizer);

        curveFitter.addObservedPoint( 15,  4443);
        curveFitter.addObservedPoint( 31,  8493);
        curveFitter.addObservedPoint( 62, 17586);
        curveFitter.addObservedPoint(125, 30582);
        curveFitter.addObservedPoint(250, 45087);
        curveFitter.addObservedPoint(500, 50683);

        ParametricUnivariateFunction f = new ParametricUnivariateFunction() {
            public double value(double x, double ... parameters) {
                double a = parameters[0];
                double b = parameters[1];
                double c = parameters[2];
                double d = parameters[3];

                return d + ((a - d) / (1 + FastMath.pow(x / c, b)));
            }

            public double[] gradient(double x, double ... parameters) {
                double a = parameters[0];
                double b = parameters[1];
                double c = parameters[2];
                double d = parameters[3];

                double[] gradients = new double[4];
                double den = 1 + FastMath.pow(x / c, b);

                // derivative with respect to a
                gradients[0] = 1 / den;

                // derivative with respect to b
                // in the reported (invalid) issue, there was a sign error here
                gradients[1] = -((a - d) * FastMath.pow(x / c, b) * FastMath.log(x / c)) / (den * den);

                // derivative with respect to c
                gradients[2] = (b * FastMath.pow(x / c, b - 1) * (x / (c * c)) * (a - d)) / (den * den);

                // derivative with respect to d
                gradients[3] = 1 - (1 / den);

                return gradients;

            }
        };

        double[] initialGuess = new double[] { 1500, 0.95, 65, 35000 };
        double[] estimatedParameters = curveFitter.fit(f, initialGuess);

        Assert.assertEquals( 2411.00, estimatedParameters[0], 500.00);
        Assert.assertEquals(    1.62, estimatedParameters[1],   0.04);
        Assert.assertEquals(  111.22, estimatedParameters[2],   0.30);
        Assert.assertEquals(55347.47, estimatedParameters[3], 300.00);
        Assert.assertTrue(optimizer.getRMS() < 600.0);
    }

    private static class SimpleInverseFunction implements ParametricUnivariateFunction {

        public double value(double x, double ... parameters) {
            return parameters[0] / x + (parameters.length < 2 ? 0 : parameters[1]);
        }

        public double[] gradient(double x, double ... doubles) {
            double[] gradientVector = new double[doubles.length];
            gradientVector[0] = 1 / x;
            if (doubles.length >= 2) {
                gradientVector[1] = 1;
            }
            return gradientVector;
        }
    }
}